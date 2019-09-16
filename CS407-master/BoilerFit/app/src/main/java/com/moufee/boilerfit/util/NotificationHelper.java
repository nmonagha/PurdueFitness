package com.moufee.boilerfit.util;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NotificationHelper {
    public static final String MENU_SCHEDULE_CHANNEL_ID = "menu_scheduled";
    public static final String COREC_SCHEDULE_CHANNEL_ID = "corec_scheduled";
    public static final String GEOFENCE_CHANNEL_ID = "geofence_channel";
    private Context mContext;
    private AlarmManager mAlarmManager;
    public static final String TAG = "NotificationHelper";
    public static final int GEOFENCE_REQUEST_CODE = 800;
    public static final int COREC_REQUEST_CODE = 7486345;

    @Inject
    public NotificationHelper(Context context, AlarmManager alarmManager) {
        mContext = context;
        mAlarmManager = alarmManager;
    }

    public void scheduleMenuNotification(DateTime dateTime, int code) {
        createNotificationChannel("Scheduled Meal Notifications", "Scheduled notifications for dining courts", MENU_SCHEDULE_CHANNEL_ID);

        Intent receiverIntent = NotificationService.getMealIntent(mContext);
        PendingIntent receiverPending = PendingIntent.getBroadcast(mContext, code, receiverIntent, 0);
        Log.d(TAG, "scheduleMenuNotification: " + dateTime.toInstant().getMillis() + ": " + System.currentTimeMillis());
        mAlarmManager.setRepeating(AlarmManager.RTC, dateTime.toInstant().getMillis(), 86400000, receiverPending);

    }

    public void scheduleCorecNotification(DateTime dateTime) {
        createNotificationChannel("Scheduled CoRec Notifications", "Scheduled notifications for CoRec availability", COREC_SCHEDULE_CHANNEL_ID);
        Intent receiverIntent = NotificationService.getCorecIntent(mContext);
        PendingIntent receiverPending = PendingIntent.getBroadcast(mContext, COREC_REQUEST_CODE, receiverIntent, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC, dateTime.toInstant().getMillis(), 86400000, receiverPending);
    }

    public void createGeofences() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        createNotificationChannel("Goefence Notifications", "Geofence", GEOFENCE_CHANNEL_ID);
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(mContext);
        List<Geofence> geofenceList = new ArrayList<>();
        geofenceList.add(new Geofence.Builder()
                .setRequestId("Ford")
                .setCircularRegion(40.432039, -86.919574, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(60000)
                .build());
        geofenceList.add(new Geofence.Builder()
                .setRequestId("Wiley")
                .setCircularRegion(40.428523, -86.920868, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(60000)
                .build());
        geofenceList.add(new Geofence.Builder()
                .setRequestId("Windsor")
                .setCircularRegion(40.426749, -86.921031, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(60000)
                .build());
        geofenceList.add(new Geofence.Builder()
                .setRequestId("Earhart")
                .setCircularRegion(40.425831, -86.925006, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(60000)
                .build());
        geofenceList.add(new Geofence.Builder()
                .setRequestId("Hillenbrand")
                .setCircularRegion(40.426506, -86.926897, 150)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLoiteringDelay(60000)
                .build());

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();
        PendingIntent geofencePending = PendingIntent.getBroadcast(mContext, GEOFENCE_REQUEST_CODE, NotificationService.getGeofenceIntent(mContext), PendingIntent.FLAG_UPDATE_CURRENT);

        geofencingClient.addGeofences(geofencingRequest, geofencePending).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "createGeofences: success");
        }).addOnFailureListener(e -> Log.e(TAG, "createGeofences: failure", e));
    }

    public void removeGeofences() {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(mContext);
        geofencingClient.removeGeofences(PendingIntent.getBroadcast(mContext, GEOFENCE_REQUEST_CODE, NotificationService.getGeofenceIntent(mContext), PendingIntent.FLAG_UPDATE_CURRENT))
                .addOnSuccessListener((aVoid) -> Log.d(TAG, "removeGeofences: success"))
                .addOnFailureListener(e -> Log.e(TAG, "removeGeofences: failure", e));
    }

    private void createNotificationChannel(String channelName, String channelDescription, String channelId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }
}
