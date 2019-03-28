package com.twobeone.ota.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e("SG2","BootReceiver ACTION_BOOT_COMPLETED");

            Intent i = new Intent(context, OTAService.class);
            context.startService(i);
        } else if (intent.getAction().equals(Intent.ACTION_POST_QUICKBOOT)) {
            Log.e("SG2","BootReceiver ACTION_POST_QUICKBOOT");

            Intent i = new Intent(context, OTAService.class);
            context.startService(i);
        }
    }
}
