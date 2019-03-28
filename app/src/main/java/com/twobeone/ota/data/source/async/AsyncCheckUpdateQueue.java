package com.twobeone.ota.data.source.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.twobeone.ota.AppConst;
import com.twobeone.ota.MainActivity;
import com.twobeone.ota.Util;
import com.twobeone.ota.callback.UpdateQueueCallback;
import com.twobeone.ota.data.model.FileDomain;

import java.io.File;

public class AsyncCheckUpdateQueue extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private int mErrorCode = -1;

    private String mType;
    private int mVersion;
    private String mFilePath = "";
    private UpdateQueueCallback callback;
    private ProgressDialog progressDialog;

    public AsyncCheckUpdateQueue(Context context, String type, int version, UpdateQueueCallback callback) {
        this.mContext = context;

        this.mType = type;
        this.mVersion = version;
        this.callback = callback;
        this.progressDialog = new ProgressDialog(MainActivity.context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("체크중입니다..");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //1.폴더 판별
        if (AppConst.TYPE_QQMUSIC.equals(mType)) {
            String dirPathQQ = android.os.Environment.getExternalStorageDirectory() + AppConst.DIRECTORY_ROOT + AppConst.TYPE_QQMUSIC + "/";
            File dirQQ = new File(dirPathQQ);

            if (!dirQQ.exists()) {
                mErrorCode = UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST;
                dirQQ.mkdirs();

                SharedPreferences pref = mContext.getSharedPreferences(AppConst.PREF_OTA, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(AppConst.TYPE_QQMUSIC);
                editor.apply();
            } else {
                //2.파일 판별
                mErrorCode = checkFile(AppConst.TYPE_QQMUSIC, mVersion, dirPathQQ);

                Log.e("SG2","이미 다운로드 된 파일 판별 : " + mErrorCode);
                if (mErrorCode == UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST || mErrorCode == UpdateQueueCallback.TYPE_ERROR_FILE_DIFFERENT) {
                    deleteFiles(dirQQ);
                }
            }
        } else if (AppConst.TYPE_KAOLA.equals(mType)) {
            String dirPathKaola = android.os.Environment.getExternalStorageDirectory() + AppConst.DIRECTORY_ROOT + AppConst.TYPE_KAOLA + "/";
            File dirKaola = new File(dirPathKaola);

            if (!dirKaola.exists()) {
                mErrorCode = UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST;
                dirKaola.mkdirs();

                SharedPreferences pref = mContext.getSharedPreferences(AppConst.PREF_OTA, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove(AppConst.TYPE_KAOLA);
                editor.apply();
            } else {
                //2.파일 판별
                mErrorCode = checkFile(AppConst.TYPE_KAOLA, mVersion, dirPathKaola);

                if (mErrorCode == UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST || mErrorCode == UpdateQueueCallback.TYPE_ERROR_FILE_DIFFERENT) {
                    deleteFiles(dirKaola);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mErrorCode == -1) {
            //로컬 업데이트 정상
            callback.onLocalUpdateExist(mFilePath);
        } else {
            callback.onLocalUpdateNonExist();
        }
        progressDialog.dismiss();
    }

    private int checkFile(String prefInfo, int serverVersion, String directoryPath) {
        int type = -1;

        SharedPreferences pref = mContext.getSharedPreferences(AppConst.PREF_OTA, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        if (pref.contains(prefInfo)) {
            Log.e("SG2","Preference 존재");
            //다운로드 시 저장했던 정보와 비교
            FileDomain.FileInfo versionInfo = gson.fromJson(pref.getString(prefInfo, ""), FileDomain.FileInfo.class);

            if (versionInfo != null && !"".equals(versionInfo.getFILE_NAME())) {
                //버전 확인
                if (serverVersion != versionInfo.getFILE_VERSION_CODE()) {
                    type = UpdateQueueCallback.TYPE_ERROR_FILE_DIFFERENT;
                } else {
                    String filePath = directoryPath + versionInfo.getFILE_NAME();
                    File file = new File(filePath);
                    if (file.exists()) {
                        //사이즈 확인
                        if (Long.compare(file.length(), versionInfo.getFILE_SIZE()) != 0) {
                            type = UpdateQueueCallback.TYPE_ERROR_FILE_NEED_DOWNLOAD_CONTINUE;
                        } else {
                            //md5 확인
                            Log.e("SG2","이미 다운로드 된 파일 MD5 판별");
                            String localMd5 = Util.checkMD5(file);
                            String prefMd5 = versionInfo.getFILE_MD5();

                            if (localMd5.equals(prefMd5)) {
                                mFilePath = filePath;
                            } else {
                                type = UpdateQueueCallback.TYPE_ERROR_FILE_DIFFERENT;
                            }
                        }
                    } else {
                        type = UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST;
                    }
                }
            } else {
                type = UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST;
            }
        } else {
            type = UpdateQueueCallback.TYPE_ERROR_FILE_NOT_EXIST;
        }

        return type;
    }

    private void deleteFiles(File folder) {
        for (File f : folder.listFiles()) {
            try {
                f.delete();
            } catch (Exception e) { }
        }
    }
}
