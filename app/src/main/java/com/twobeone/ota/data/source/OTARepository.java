package com.twobeone.ota.data.source;

import android.content.Context;

import com.twobeone.ota.callback.DownloadCallback;
import com.twobeone.ota.callback.TokenCallback;
import com.twobeone.ota.callback.UpdateQueueCallback;
import com.twobeone.ota.callback.VersionCallback;
import com.twobeone.ota.data.model.FileDomain;

public class OTARepository {
    private static OTARepository otaRepository = null;
    private VersionLocalDataSource localDataSource;
    private VersionRemoteDataSource remoteDataSource;

    public static OTARepository getInstance() {
        if (otaRepository == null) {
            otaRepository = new OTARepository();
        }

        return otaRepository;
    }

    private OTARepository() {
        localDataSource = new VersionLocalDataSource();
        remoteDataSource = new VersionRemoteDataSource();
    }

    //local
    public void checkUpdateQueue(Context context, String type, int version, UpdateQueueCallback callback) {
        localDataSource.checkUpdateQueue(context, type, version, callback);
    }

    public void removeSharedPreference(Context context, String type) {
        localDataSource.removeSharedPreference(context, type);
    }

    public void setSharedPreference(Context context, String type, FileDomain.FileInfo info) {
        localDataSource.setSharedPreference(context, type, info);
    }


    //remote
    public void getVersionInfo(Context context, VersionCallback callback) {
        remoteDataSource.getVersionInfo(context, callback);
    }

    public void getAuthToken(TokenCallback callback) {
        remoteDataSource.getAuthToken(callback);
    }

    public void getFileInfo(Context context, String fileType, String filaName, String token, DownloadCallback callback) {
        remoteDataSource.getFileInfo(context, fileType, filaName, token, callback);
    }

    public void updateLog(String state, String fileName, String fileVersion) {
        remoteDataSource.updateLog(state, fileName, fileVersion);
    }
}
