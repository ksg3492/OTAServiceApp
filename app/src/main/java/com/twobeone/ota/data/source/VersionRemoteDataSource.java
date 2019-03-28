package com.twobeone.ota.data.source;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.twobeone.ota.AppConst;
import com.twobeone.ota.GlobalStatus;
import com.twobeone.ota.Util;
import com.twobeone.ota.callback.DownloadCallback;
import com.twobeone.ota.callback.FileInfoCallback;
import com.twobeone.ota.callback.TokenCallback;
import com.twobeone.ota.callback.VersionCallback;
import com.twobeone.ota.data.model.FileDomain;
import com.twobeone.ota.data.model.LogDomain;
import com.twobeone.ota.data.model.TokenDomain;
import com.twobeone.ota.data.model.VersionDomain;
import com.twobeone.ota.network.RetrofitService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionRemoteDataSource {

    public void getVersionInfo(final Context context, final VersionCallback callback) {
        Call<VersionDomain> call = RetrofitService.getInstance.getVersionInfo(Util.getUniqueId());
        call.enqueue(new Callback<VersionDomain>() {
            @Override
            public void onResponse(Call<VersionDomain> call, Response<VersionDomain> response) {
                if (response != null && response.isSuccessful()) {
                    VersionDomain domain = response.body();

                    if (domain != null) {
                        if ("0".equals(domain.getCode())) {
                            //내부 앱 버전체크
                            ArrayList<VersionDomain.VersionInfo> infos = domain.getResult();
                            VersionDomain.VersionInfo qqMusicInfo = null;
                            VersionDomain.VersionInfo kaolaInfo = null;

                            for (VersionDomain.VersionInfo info : infos) {
                                if (info.getFILE_TYPE().equals(AppConst.TYPE_QQMUSIC)) {
                                    qqMusicInfo = info;
                                } else if (info.getFILE_TYPE().equals(AppConst.TYPE_KAOLA)) {
                                    kaolaInfo = info;
                                }
                            }

                            checkAppVersion(context, qqMusicInfo, kaolaInfo, callback);
                        } else {
                            //실패
                            callback.onFail(VersionCallback.TYPE_ERROR_SERVER_ERROR, VersionCallback.TYPE_ERROR_SERVER_ERROR);
                        }
                    }
                } else {
                    callback.onFail(VersionCallback.TYPE_ERROR_SERVER_ERROR, VersionCallback.TYPE_ERROR_SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Call<VersionDomain> call, Throwable t) {
                callback.onFail(VersionCallback.TYPE_ERROR_SERVER_ERROR, VersionCallback.TYPE_ERROR_SERVER_ERROR);
            }
        });
    }

    public void getAuthToken(final TokenCallback callback) {
        Call<TokenDomain> call = RetrofitService.getInstance.getAuthToken(Util.getUniqueId());
        call.enqueue(new Callback<TokenDomain>() {
            @Override
            public void onResponse(Call<TokenDomain> call, Response<TokenDomain> response) {
                if (response != null && response.isSuccessful()) {
                    TokenDomain domain = response.body();

                    if (domain != null) {
                        if ("0".equals(domain.getCode())) {
                            //성공
                            callback.onSuccess(domain.getToken());
                        } else {
                            //실패
                            callback.onFail();
                        }
                    }
                } else {
                    callback.onFail();
                }
            }

            @Override
            public void onFailure(Call<TokenDomain> call, Throwable t) {
                callback.onFail();
            }
        });
    }

    public void getFileInfo(final Context context, final String fileType, final String filaName, final String token, final DownloadCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("deviceSerial", Util.getUniqueId());
        map.put("fileName", filaName);
//        map.put("token", token);

        Call<FileDomain> call = RetrofitService.getInstance.getFileInfo(map);
        call.enqueue(new Callback<FileDomain>() {
            @Override
            public void onResponse(Call<FileDomain> call, Response<FileDomain> response) {
                if (response != null && response.isSuccessful()) {
                    FileDomain domain = response.body();

                    if (domain != null) {
                        if ("0".equals(domain.getCode())) {
                            //성공
                            getFileDownload(context, fileType, filaName, token, domain.getResult().get(0), callback);
                        } else {
                            //실패
                            callback.onFail(DownloadCallback.TYPE_ERROR_TOKEN_ERROR);
                        }
                    }
                } else {
                    callback.onFail(DownloadCallback.TYPE_ERROR_TOKEN_ERROR);
                }
            }

            @Override
            public void onFailure(Call<FileDomain> call, Throwable t) {
                callback.onFail(DownloadCallback.TYPE_ERROR_TOKEN_ERROR);
            }
        });
    }

    //internal Method

    private long getExternalAvailableMemory() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            return availableBlocks * blockSize;
        }

        return 0;
    }

    private void getFileDownload(final Context context, final String fileType, String fileName, String token, final FileDomain.FileInfo info, final DownloadCallback callback) {
        //디바이스 용량 체크
        if (getExternalAvailableMemory() > info.getFILE_SIZE()) {
            final String path = android.os.Environment.getExternalStorageDirectory() + AppConst.DIRECTORY_ROOT + fileType + "/" + info.getFILE_NAME();
            final RandomAccessFile output = makeFile(path);

            if (output == null) {
                callback.onFail(DownloadCallback.TYPE_ERROR_FILE_ERROR);

                return;
            }

            String accept = "bytes";
//            String range = "bytes=" + getByte(output) + "-";
            String range = String.valueOf(getByte(output));

            Map<String, String> map = new HashMap<>();
            map.put("deviceSerial", Util.getUniqueId());
            map.put("fileName", fileName);
            map.put("token", token);

            Call<ResponseBody> call = RetrofitService.getInstance.getFileDownload(accept, range, map);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (response != null && response.isSuccessful()) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                //download info save
                                OTARepository.getInstance().setSharedPreference(context, fileType, info);

                                boolean success = writeResponseBodyToDisk(output, response.body(), callback);

                                File file = new File(path);

                                if (success) {
                                    if (file.exists()) {
                                        callback.onMD5Check();

                                        String downloadMD5 = Util.checkMD5(file);
                                        String serverMD5 = info.getFILE_MD5();

                                        Log.e("SG2","다운 MD5 : " + downloadMD5);
                                        Log.e("SG2","서버 MD5 : " + serverMD5);

                                        if (downloadMD5 != null && serverMD5 != null &&
                                                !downloadMD5.equals("") && downloadMD5.equals(serverMD5)) {
                                            callback.onSuccess(path, info);
                                        } else {
                                            try {
                                                file.delete();
                                            } catch (Exception e) {

                                            }

                                            OTARepository.getInstance().removeSharedPreference(context, fileType);
                                            callback.onFail(DownloadCallback.TYPE_ERROR_FILE_MD5_NOT_SAME);
                                        }
                                    } else {
                                        OTARepository.getInstance().removeSharedPreference(context, fileType);
                                        callback.onFail(DownloadCallback.TYPE_ERROR_DOWNLOAD_FAIL);
                                    }
                                } else {
                                    if (file.exists()) {
                                        callback.onPartialSuccess(path, info);
                                    } else {
                                        OTARepository.getInstance().removeSharedPreference(context, fileType);
                                        callback.onFail(DownloadCallback.TYPE_ERROR_DOWNLOAD_FAIL);
                                    }
                                }
                                return null;
                            }
                        }.execute();
                    } else {
                        callback.onFail(DownloadCallback.TYPE_ERROR_SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onFail(DownloadCallback.TYPE_ERROR_SERVER_ERROR);
                }
            });
        } else {
            callback.onFail(DownloadCallback.TYPE_ERROR_STORAGE_FULL);
        }
    }

    public void updateLog(String state, String fileName, String fileVersion) {
        Map<String, String> map = new HashMap<>();
        map.put("deviceSerial", Util.getUniqueId());

        map.put("fileName", fileName);
        map.put("fileVersion", fileVersion);

        Call<LogDomain> call = RetrofitService.getInstance.updateLog(map);
        call.enqueue(new Callback<LogDomain>() {
            @Override
            public void onResponse(Call<LogDomain> call, Response<LogDomain> response) {
                if (response != null && response.isSuccessful()) {
                    if("OK".equals(response.body().getHttpStatus())) { // 서버에 로그 완료
                        Log.e("kjw", "로그 기록 완료 : " );
                        Log.e("kjw", "로그 기록 메시지 : " + response.body().getMsg());
                    }
                } else {
                    Log.e("kjw", "로그 기록 실패 : ");
                }
            }
            @Override
            public void onFailure(Call<LogDomain> call, Throwable t) {
                Log.e("kjw", "로그 기록 실패 : " + t.getMessage());
            }
        });
    }

    private boolean writeResponseBodyToDisk(RandomAccessFile output, ResponseBody body, DownloadCallback callback) {
        try{
            InputStream inputStream = null;

            try {
                byte[] fileReader = new byte[128];

                long remain = body.contentLength();
                long downloaded_size = getByte(output);
                long filelength = remain + downloaded_size;
                long fileSizeDownloaded = 0;

                Log.e("SG2", "downloaded size : " + downloaded_size);
                Log.e("SG2", "remain : " + remain);
                Log.e("SG2", "filelength : " + filelength);

                inputStream = body.byteStream();

                int read = 0;
                int prevPercent = 0;
                if (downloaded_size != 0 && downloaded_size != -1) {
                    fileSizeDownloaded = downloaded_size;
                }
                if (downloaded_size < filelength) {
                    while (true) {
                        read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        if (GlobalStatus.isDownloadForceStop()) {
                            return false;
                        }

                        output.write(fileReader, 0, read);

                        fileSizeDownloaded += read;

                        int percent = (int) (fileSizeDownloaded * 100 / filelength);
                        if (prevPercent != percent) {
                            prevPercent = percent;

                            callback.onProgress(percent);
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (output != null) {
                    output.close();
                }
            }

        } catch (Exception e) {
            return false;
        }
    }

    private RandomAccessFile makeFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return new RandomAccessFile(file.getAbsoluteFile(), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private long getByte(RandomAccessFile output) {
        long file_size = 0;
        try {
            file_size = output.length();
            output.seek(file_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file_size;
    }

    private void checkAppVersion(Context context, VersionDomain.VersionInfo qqmusic, VersionDomain.VersionInfo kaola, VersionCallback callback) {
        String qqmusicFileName = "";
        String kaolaFileName = "";

        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(AppConst.APP_QQMUSIC_PACKAGE, 0);
            int verCode = pinfo.versionCode;
            Log.e("SG2",AppConst.APP_QQMUSIC_PACKAGE + " 's VersionCode : " + verCode);

            if (verCode < qqmusic.getFILE_VERSION_CODE()) {
                qqmusicFileName = qqmusic.getFILE_NAME();
            }
        } catch (PackageManager.NameNotFoundException e) {
            qqmusicFileName = qqmusic.getFILE_NAME();
            Log.e("SG2",AppConst.APP_QQMUSIC_PACKAGE + " is Not Installed");
        }

        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(AppConst.APP_KAOLA_PACKAGE, 0);
            int verCode = pinfo.versionCode;
            Log.e("SG2",AppConst.APP_KAOLA_PACKAGE + " 's VersionCode : " + verCode);

            if (verCode < kaola.getFILE_VERSION_CODE()) {
                kaolaFileName = kaola.getFILE_NAME();
            }
        } catch (PackageManager.NameNotFoundException e) {
            kaolaFileName = kaola.getFILE_NAME();
            Log.e("SG2",AppConst.APP_KAOLA_PACKAGE + " is Not Installed");
        }

        callback.onSuccess(qqmusicFileName, qqmusic.getFILE_VERSION_CODE(), kaolaFileName, kaola.getFILE_VERSION_CODE());
    }
}
