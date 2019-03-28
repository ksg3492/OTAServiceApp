package com.twobeone.ota.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.twobeone.ota.AppConst;
import com.twobeone.ota.DialogActivity;
import com.twobeone.ota.MainActivity;
import com.twobeone.ota.callback.VersionCallback;
import com.twobeone.ota.data.source.OTARepository;

public class OTAService extends Service {
    @Override
    public void onCreate() {
        Log.e("SG2","onCreate");
        super.onCreate();

        registerReceiver(receiver, new IntentFilter("ACTION_SERVICE_KILL"));

        logHandler.removeMessages(0);
        logHandler.sendEmptyMessage(0);
        checkNetworkAvailable();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACTION_SERVICE_KILL")) {
                stopSelf();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SG2","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("SG2","onBind");
        logHandler.removeMessages(0);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("SG2","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        Log.e("SG2","onDestroy");
        logHandler.removeMessages(0);
        networkDelayHandler.removeMessages(0);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void checkNetworkAvailable() {
        Log.e("SG2","checkNetworkAvailable");
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
            requestVersionCheck();
        } else {
            networkDelayHandler.removeMessages(0);
            networkDelayHandler.sendEmptyMessageDelayed(0, 5000);
        }
    }

    private Handler networkDelayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            checkNetworkAvailable();
        }
    };

    private Handler logHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.e("SG2","logHandler is running");

            logHandler.sendEmptyMessageDelayed(0, 3000);
        }
    };

    private void requestVersionCheck() {
        Log.e("SG2","requestVersionCheck");

        OTARepository.getInstance().getVersionInfo(getApplicationContext(), new VersionCallback() {
            @Override
            public void onSuccess(String qqmusic, int qqmusicVersion, String kaola, int kaolaVersion) {
                Log.e("SG2","getVersionInfo onSuccess");
                Log.e("SG2","getVersionInfo qqmusic : " + qqmusic);
                Log.e("SG2","getVersionInfo qqmusicVersion : " + qqmusicVersion);
                Log.e("SG2","getVersionInfo kaola : " + kaola);
                Log.e("SG2","getVersionInfo kaolaVersion : " + kaolaVersion);

                if (qqmusic.equals("") && kaola.equals("")) {
                    //둘다 최신 버전
                    return;
                }

                //업데이트 필요
                Intent intent = new Intent(OTAService.this, DialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(AppConst.INTENT_FROM_SERVICE, true);
                intent.putExtra(AppConst.INTENT_QQMUSIC_FILE_NAME, qqmusic);
                intent.putExtra(AppConst.INTENT_KAOLA_FILE_NAME, kaola);
                intent.putExtra(AppConst.INTENT_QQMUSIC_SERVER_VERSION, qqmusicVersion);
                intent.putExtra(AppConst.INTENT_KAOLA_SERVER_VERSION, kaolaVersion);
                startActivity(intent);
            }

            @Override
            public void onFail(int qqerror, int kaolaerror) {
                Log.e("SG2","requestVersionCheck onFail : " + qqerror + " / " + kaolaerror);
            }
        });
    }
}
