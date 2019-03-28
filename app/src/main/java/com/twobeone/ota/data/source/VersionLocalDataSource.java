package com.twobeone.ota.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.twobeone.ota.AppConst;
import com.twobeone.ota.callback.UpdateQueueCallback;
import com.twobeone.ota.data.model.FileDomain;
import com.twobeone.ota.data.source.async.AsyncCheckUpdateQueue;

public class VersionLocalDataSource {
    public void checkUpdateQueue(Context context, String type, int version, UpdateQueueCallback callback) {
        new AsyncCheckUpdateQueue(context, type, version, callback).execute();
    }

    public void removeSharedPreference(Context context, String type) {
        SharedPreferences pref = context.getSharedPreferences(AppConst.PREF_OTA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(type);
        editor.apply();
    }

    public void setSharedPreference(Context context, String type, FileDomain.FileInfo info) {
        //save
        try {
            SharedPreferences pref = context.getSharedPreferences(AppConst.PREF_OTA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            final Gson gson = new Gson();
            String serializedObject = gson.toJson(info);

            editor.putString(type, serializedObject);
            editor.apply();
        } catch (Exception e) {
            Log.e("SG2","setSharedPreference Error: ", e);
        }
    }
}
