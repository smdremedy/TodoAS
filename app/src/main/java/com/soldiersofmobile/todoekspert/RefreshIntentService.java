package com.soldiersofmobile.todoekspert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

public class RefreshIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String REFRESH_ACTION = "com.soldiersofmobile.todoekspert.REFRESH";

    public RefreshIntentService() {
        super(RefreshIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        App app = (App) getApplication();
        TodoManager todoManager = app.getTodoManager();
        todoManager.fetchTodosSync();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Todos refreshed");
        builder.setContentText("New items loaded");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent activityIntent = new Intent(this, TodoListActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.addAction(R.mipmap.ic_launcher, "Show", pendingIntent);
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

        Intent broadcast = new Intent(REFRESH_ACTION);
        sendBroadcast(broadcast);

    }
}
