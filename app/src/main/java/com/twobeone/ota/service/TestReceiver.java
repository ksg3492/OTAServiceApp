package com.twobeone.ota.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("ITSFORTEST")) {
            Log.e("SG2","ITSFORTEST");

            Intent i = new Intent(context, OTAService.class);
            context.startService(i);
        } else if (intent.getAction().equals("ITSFORTEST_STOP")) {
            Log.e("SG2","ITSFORTEST_STOP");

            Intent i = new Intent("ACTION_SERVICE_KILL");
            context.sendBroadcast(i);
        }
    }
}
