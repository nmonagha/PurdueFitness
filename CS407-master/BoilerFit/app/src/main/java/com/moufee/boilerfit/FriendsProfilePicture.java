package com.moufee.boilerfit;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.moufee.boilerfit.util.UserUtil;
import com.squareup.picasso.Picasso;

import java.util.function.Consumer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsProfilePicture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsProfilePicture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsProfilePicture extends Fragment {
    private TextView nameField;
    private TextView bioField;
    private TextView friendsFoodStreak;
    private TextView friendsGymtreak;
    private ImageButton friendProfile;

    private String name;
    private String bio;
    private String userID;
    private int activityStreak;
    private int foodStreak;

    public void setDate(String name, String bio, String userID, int activityStreak, int foodStreak){
        this.name = name;
        this.bio = bio;
        this.userID = userID;
        this.activityStreak = activityStreak;
        this.foodStreak = foodStreak;
    }

    // Step 4, you get the "name" as input here. Tada!
    public static FriendsProfilePicture newInstance(String input_name) {
        return new FriendsProfilePicture();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_friends_profile_page, container, false);
        nameField = (TextView) rootView.findViewById(R.id.friendsName);
        bioField = (TextView) rootView.findViewById(R.id.friendsBio);
        friendsFoodStreak = (TextView) rootView.findViewById(R.id.friendsFoodStreakTextView);
        friendsGymtreak = (TextView) rootView.findViewById(R.id.friendsGymtreakTextView);
        friendProfile = (ImageButton)rootView.findViewById(R.id.friendsProfilePicture);

        nameField.setText(name);
        bioField.setText(bio);
        friendsGymtreak.setText(activityStreak + "");
        friendsFoodStreak.setText(foodStreak + "");
        UserUtil.getProfilePictureForUser(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) {
                if (uri != null) {
                    Picasso.get().load(uri.toString()).into(friendProfile);
                } else {
                    uri = Uri.parse("android.resource://com.moufee.boilerfit/drawable/profile");
                    Picasso.get().load(uri.toString()).into(friendProfile);
                }
            }
        }, userID);

        return rootView;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
