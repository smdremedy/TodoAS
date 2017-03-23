package com.soldiersofmobile.todoekspert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(
            final Context context,
            final Intent intent
    ) {
        Toast.makeText(context, "Sms!", Toast.LENGTH_SHORT).show();
    }
}
