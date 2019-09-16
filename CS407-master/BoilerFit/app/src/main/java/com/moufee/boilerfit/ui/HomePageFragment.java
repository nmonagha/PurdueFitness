package com.moufee.boilerfit.ui;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.User;
import com.moufee.boilerfit.repository.UserRepository;
import com.moufee.boilerfit.util.Callback;
import com.moufee.boilerfit.util.UserUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class HomePageFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    // Home Page UI data members
    private ImageView profilePicture;
    private ImageButton galleryUploadBtn;
    private TextView foodStreak;
    private TextView foodcurrentBadge;
    private TextView foodNextBadge;
    private TextView name;
    private TextView gymStreak;
    private TextView bioText;
    private ProgressBar foodProgressBar;
    private TextView activityCurrentBadge;
    private TextView activityNextBadge;
    private ProgressBar activityProgressBar;
    private String imageFilePath;
    private TextView activityLevelText;
    @Inject
    UserRepository mUserRepository;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSIONS = 200;


    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 1001;
    public String notificationBeforeLostFoodStreak = "You are about to lose your healthy food streak!";
    public String notificationAfterLostFoodStreak = "You lost your healthy food streak :(";

    public String notificationBeforeLostActivityStreak = "You are about to lose your healthy activity streak!";
    public String notificationAfterLostActivityStreak = "You lost your healthy activity streak :(";

    public String notificationTextFood = "Time to get some healthy food!";
    public String notificationTextActivity = "Time to get some healthy activity!";
    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mychannel";
            String description = "send losing streak alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.picture_upload_menu, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.camera_upload:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                        return false;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
                                "com.moufee.boilerfit.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                    }
                }
                return true;
            case R.id.gallery_upload:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_INTENT);
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserProfileViewModel.class);

        View rootView = inflater.inflate(R.layout.home_page_fragment, container, false);

        galleryUploadBtn = (ImageButton) rootView.findViewById(R.id.profilePictureHomePage);
        foodStreak = (TextView) rootView.findViewById(R.id.foodStreakTextView);
        gymStreak = (TextView) rootView.findViewById(R.id.gymtreakTextView);
        profilePicture = (ImageView) rootView.findViewById(R.id.profilePictureHomePage);
        bioText = (TextView) rootView.findViewById(R.id.Bio);
        foodcurrentBadge = (TextView) rootView.findViewById(R.id.foodCurrentBadge);
        foodNextBadge = (TextView) rootView.findViewById(R.id.foodNextBadge);
        name = (TextView) rootView.findViewById(R.id.nameTextView);
        foodProgressBar = (ProgressBar)rootView.findViewById(R.id.foodProgressBar);
        activityCurrentBadge = (TextView) rootView.findViewById(R.id.currentBadge);
        activityNextBadge = (TextView) rootView.findViewById(R.id.nextBadge);
        activityProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        activityLevelText = (TextView) rootView.findViewById(R.id.gymtreakTextView);

        registerForContextMenu(galleryUploadBtn); // connecting conext menu with galleryuploadbtn

        // initialize values
        mUserRepository.getUserCallback(new Callback<User>() {
            @Override
            public void accept(@javax.annotation.Nullable User user) {
                if(user!=null) {
                    int hoursForActivityStreak = user.hoursForActivityStreak();
                    if(hoursForActivityStreak < 6 && hoursForActivityStreak >=0){
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle(notificationBeforeLostActivityStreak)
                                .setContentText(notificationTextActivity)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                        notificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
                    } else if(hoursForActivityStreak < 0){
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle(notificationAfterLostActivityStreak)
                                .setContentText(notificationTextActivity)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                        notificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
                        user.setActivityStreak(0);
                        user.setActivityStreakDate(null);
                        mUserRepository.updateUser(user);

                    }
                    int hoursForFoodStreak = user.hoursForFoodStreak();

                    if(hoursForFoodStreak < 6 && hoursForFoodStreak >=0){
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle(notificationBeforeLostFoodStreak)
                                .setContentText(notificationTextFood)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                        notificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
                    } else if(hoursForFoodStreak < 0){
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle(notificationAfterLostFoodStreak)
                                .setContentText(notificationTextFood)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                        notificationManagerCompat.notify(NOTIFICATION_ID,mBuilder.build());
                        user.setFoodStreak(0);
                        user.setFoodStreakDate(null);
                        mUserRepository.updateUser(user);

                    }
                     foodcurrentBadge.setText("F" + user.getFoodStreakLevel());
                     foodNextBadge.setText("F" + (user.getFoodStreakLevel() + 1));
                     activityCurrentBadge.setText("G"+user.getFoodStreakLevel());
                    activityNextBadge.setText("G"+(user.getActivityLevel() + 1));

                    int targetValue = user.getNextFoodStreakLevel();
                    int currentValue = user.getFoodStreak();
                    foodProgressBar.setProgress((currentValue*100)/targetValue);

                    int activityTarget = user.getNextActivityStreakLevel();
                    int currentActivity = user.getActivityStreak();
                    activityProgressBar.setProgress((currentActivity*100)/activityTarget);

                    name.setText(user.getName());
                    String userBio = user.getBio();
                    foodStreak.setText(currentValue + "");
                    activityLevelText.setText("" + currentActivity);

                    if(userBio!=null)
                        bioText.setText(userBio);
                    else
                        bioText.setText("");
                }else{
                    foodcurrentBadge.setText("F0");
                    foodNextBadge.setText("F1");
                    activityCurrentBadge.setText("G0");
                    activityNextBadge.setText("G1");
                }
            }
        });

        foodStreak.setText("0");
        gymStreak.setText("0");

        UserUtil.getProfilePicture(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) {
                if (uri != null) {
                    Picasso.get().load(uri.toString()).into(profilePicture);
                } else {    
                    uri = Uri.parse("android.resource://com.moufee.boilerfit/drawable/profile");
                    Picasso.get().load(uri.toString()).into(profilePicture);
                }
            }
        });

        setListeners();

        return rootView;
    }

    private void setListeners() {
        mViewModel.getUser().observe(this, user -> {

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        if (context instanceof FragmentActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity) context, mViewModelFactory).get(UserProfileViewModel.class);
        }
        createNotificationChannel();
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {

            final Uri myUri = data.getData();
            UserUtil.setProfilePicture(myUri, new Consumer<Uri>() {
                @Override
                public void accept(Uri uri) {
                    if (uri != null) {
                        Toast.makeText(getActivity(), "Upload Done", Toast.LENGTH_LONG).show();
                        String s = uri.toString();
                        Picasso.get().load(s).into(profilePicture);
                    } else {
                        Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            //profilePicture.setImageBitmap(imageBitmap);

            final Uri myUri = Uri.fromFile(new File(imageFilePath));

            if (myUri != null) {

                UserUtil.setProfilePicture(myUri, new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) {
                        if (uri != null) {
                            Toast.makeText(getActivity(), "Upload Done", Toast.LENGTH_LONG).show();
                            String s = uri.toString();
                            Picasso.get().load(s).into(profilePicture);
                        } else {
                            Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imageFilePath = image.getAbsolutePath();
        return image;
    }

}
