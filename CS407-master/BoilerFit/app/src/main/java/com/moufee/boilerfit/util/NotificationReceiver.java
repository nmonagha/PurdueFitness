package com.moufee.boilerfit.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        JobIntentService.enqueueWork(context, NotificationService.class, 6843, intent);
    }
}
