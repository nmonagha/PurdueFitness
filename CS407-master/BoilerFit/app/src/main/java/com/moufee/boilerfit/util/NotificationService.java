package com.moufee.boilerfit.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.moufee.boilerfit.R;
import com.moufee.boilerfit.api.CorecService;
import com.moufee.boilerfit.corec.CorecFacility;
import com.moufee.boilerfit.repository.UserRepository;
import com.moufee.boilerfit.ui.MainActivity;
import com.moufee.boilerfit.ui.MainActivityKt;
import com.moufee.boilerfit.ui.corec.CoRecHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class NotificationService extends JobIntentService {
    private static final String ACTION_GEOFENCE = "com.moufee.boilerfit.util.occasion.GEOFENCE";
    private static final String ACTION_MEAL = "com.moufee.boilerfit.util.occasion.MEAL";
    private static final String ACTION_COREC = "com.moufee.boilerfit.util.occasion.COREC";
    private static final String KEY_NOTIFICATION_ID = "com.moufee.boilerfit.util.key.NID";
    public static final int MEAL_NOTIFICATION_ID = 819974;
    public static final int GEO_NOTIFICATION_ID = 314832;
    public static final int COREC_NOTIFICATION_ID = 613541;
    public static final String TAG = "NotificationService";

    @Inject
    UserRepository mUserRepository;
    @Inject
    CorecService mCorecService;


    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    public static Intent getMealIntent(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_MEAL);
        return intent;
    }

    public static Intent getGeofenceIntent(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_GEOFENCE);
        return intent;
    }

    public static Intent getCorecIntent(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_COREC);
        return intent;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        final String action = intent.getAction();
        if (ACTION_GEOFENCE.equals(action)) {
            handleGeofenceNotification(GeofencingEvent.fromIntent(intent));
        } else if (ACTION_MEAL.equals(action)) {
            handleMealNotification();
        } else if (ACTION_COREC.equals(action)) {
            handleCorecNotification();
        }
    }

    private void handleGeofenceNotification(GeofencingEvent event) {
        if (event.hasError()) {
            Log.e(TAG, "handleGeofenceNotification: error " + event.getErrorCode());
        }
        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        Geofence triggeringGeofence = triggeringGeofences.get(0);
        String fenceName = triggeringGeofence.getRequestId();
        Log.d(TAG, "handleGeofenceNotification: geofence received");
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(MainActivityKt.KEY_GOTO, MainActivityKt.MENUS);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.GEOFENCE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getString(R.string.geofence_notification_title))
                .setContentText(getString(R.string.geofence_notification_text, fenceName))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101010, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();
        notificationManager.notify(GEO_NOTIFICATION_ID, notification);
    }

    private void handleMealNotification() {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(MainActivityKt.KEY_GOTO, MainActivityKt.MENUS);

        //todo: customize for each meal?
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.MENU_SCHEDULE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Meal Time!")
                .setContentText("Check out the healthy menu options.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, MEAL_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();
        notificationManager.notify(MEAL_NOTIFICATION_ID, notification);
    }

    private void handleCorecNotification() {
        //this is where we access the CorecRepository and get the data
        mUserRepository.getUserCallback(user -> {
            Set<String> allFavoriteFacilities = new HashSet<>();
            for (String favoriteActivity :
                    user.getFavoriteActivities()) {
                List<String> facilityNames = CoRecHelper.getActivitiesMap().get(favoriteActivity);
                allFavoriteFacilities.addAll(facilityNames);
            }
            mCorecService.getCurrentActivity().enqueue(new Callback<List<CorecFacility>>() {
                @Override
                public void onResponse(Call<List<CorecFacility>> call, Response<List<CorecFacility>> response) {
                    List<CorecFacility> facilities = response.body();
                    if (facilities != null)
                        for (CorecFacility facility :
                                facilities) {
                            Log.d(TAG, "onResponse: " + ((double) facility.getCount()) / facility.getCapacity());
                            if (allFavoriteFacilities.contains(facility.getLocationName()) && ((double) facility.getCount()) / facility.getCapacity() <= .5) {
                                sendCorecNotification(facility.getLocationName() + " is available.", null);
                                return;
                            }
                        }
                }

                @Override
                public void onFailure(Call<List<CorecFacility>> call, Throwable t) {

                }
            });


        });
    }

    private void sendCorecNotification(String title, String content) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(MainActivityKt.KEY_GOTO, MainActivityKt.COREC);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.COREC_SCHEDULE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText("One of your favorite activities is available!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, COREC_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true);
        Notification notification = builder.build();
        notificationManager.notify(COREC_NOTIFICATION_ID, notification);
    }
}
