package com.twobeone.ota;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twobeone.ota.callback.DownloadCallback;
import com.twobeone.ota.callback.TokenCallback;
import com.twobeone.ota.callback.UpdateQueueCallback;
import com.twobeone.ota.data.model.FileDomain;
import com.twobeone.ota.data.source.OTARepository;

import java.io.File;

public class MainActivity extends Activity {

    private AlertDialog checkDialog = null;
    private AlertDialog choiceDialog = null;
    private ProgressDialog mProgressDialog;

    private OTARepository otaRepository = null;

    private Intent mIntent = null;

    private String mQQMusicFileName = "";
    private String mKaolaFileName = "";
    private int mVersionQQ = 0;
    private int mVersionKaola = 0;

    private LinearLayout ll_main;
    private Button bt_qqmusic;
    private TextView tv_qqmusic_update;
    private Button bt_kaola;
    private TextView tv_kaola_update;
    private ProgressBar pb;
    private Button bt_stop;
    public static Context context;
    public static final int TYPE_QQMUSIC = 1000;
    public static final int TYPE_KAOLAFM = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("SG2","Activity onCreate");
        context = this;
        initView();
        GlobalStatus.init();

        mIntent = getIntent();
        if (mIntent != null) {
            if (mIntent.getBooleanExtra(AppConst.INTENT_FROM_SERVICE, false)) {
                Log.e("SG2","Activity INTENT_FROM_SERVICE : " + true);
                mQQMusicFileName = mIntent.getStringExtra(AppConst.INTENT_QQMUSIC_FILE_NAME);
                mKaolaFileName = mIntent.getStringExtra(AppConst.INTENT_KAOLA_FILE_NAME);
                mVersionQQ = mIntent.getIntExtra(AppConst.INTENT_QQMUSIC_SERVER_VERSION, -1);
                mVersionKaola = mIntent.getIntExtra(AppConst.INTENT_KAOLA_SERVER_VERSION, -1);

                if ((mQQMusicFileName == null && mKaolaFileName == null) || (mQQMusicFileName.equals("") && mKaolaFileName.equals(""))) {
                    Log.e("SG2","finish1");
                    finish();
                }
                if (mVersionQQ == -1 && mVersionKaola == -1) {
                    Log.e("SG2","finish2");
                    finish();
                }

//                if (checkDialog != null && !checkDialog.isShowing()) {
//                    checkDialog.show();
//                    checkDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//                }
            } else {
//                Log.e("SG2","startService");
//                Intent service = new Intent(getApplicationContext(), OTAService.class);
//                startService(service);

                finish();
            }
            setView();
        }
    }

    private void initView() {
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        bt_qqmusic = (Button) findViewById(R.id.bt_qqmusic);
        tv_qqmusic_update = (TextView) findViewById(R.id.tv_qqmusic_update);
        bt_kaola = (Button) findViewById(R.id.bt_kaola);
        tv_kaola_update = (TextView) findViewById(R.id.tv_kaola_update);
        pb = (ProgressBar) findViewById(R.id.pb);
        bt_stop = (Button) findViewById(R.id.bt_stop);

        mProgressDialog = new ProgressDialog(MainActivity.this);
    }

    private void setView() {

        if(ll_main.getVisibility() == View.GONE) {
            ll_main.setVisibility(View.VISIBLE);
        }

        if (!mQQMusicFileName.equals("")) {
            bt_qqmusic.setVisibility(View.VISIBLE);
            tv_qqmusic_update.setVisibility(View.GONE);
        } else {
            bt_qqmusic.setVisibility(View.GONE);
            tv_qqmusic_update.setVisibility(View.VISIBLE);
        }
        if (!mKaolaFileName.equals("")) {
            bt_kaola.setVisibility(View.VISIBLE);
            tv_kaola_update.setVisibility(View.GONE);
        } else {
            bt_kaola.setVisibility(View.GONE);
            tv_kaola_update.setVisibility(View.VISIBLE);
        }

        bt_qqmusic.setOnClickListener(onClickListener);
        bt_kaola.setOnClickListener(onClickListener);
        bt_stop.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_qqmusic:
                    otaRepository = OTARepository.getInstance();
                    otaRepository.checkUpdateQueue(MainActivity.this, AppConst.TYPE_QQMUSIC, mVersionQQ, new UpdateQueueCallback() {
                        @Override
                        public void onLocalUpdateExist(String filePath) {
                            //다운로드 받은 apk 파일 존재
                            File file = new File(filePath);

                            if (file.exists()) {
                                Log.e("SG2","이미 다운로드 받은 파일 존재");
                                showInstallDialog(AppConst.TYPE_QQMUSIC, filePath);
                            }
                        }

                        @Override
                        public void onLocalUpdateNonExist() {
                            requestToken(AppConst.TYPE_QQMUSIC);
                        }
                    });

                    break;
                case R.id.bt_kaola:
                    otaRepository = OTARepository.getInstance();
                    otaRepository.checkUpdateQueue(getApplicationContext(), AppConst.TYPE_KAOLA, mVersionKaola, new UpdateQueueCallback() {
                        @Override
                        public void onLocalUpdateExist(String filePath) {
                            //다운로드 받은 apk 파일 존재
                            File file = new File(filePath);

                            if (file.exists()) {
                                Log.e("SG2","이미 다운로드 받은 파일 존재");
//                                Toast.makeText(getBaseContext(), "설치 시작?", Toast.LENGTH_SHORT).show();
                                showInstallDialog(AppConst.TYPE_KAOLA, filePath);
                            }
                        }

                        @Override
                        public void onLocalUpdateNonExist() {
                            requestToken(AppConst.TYPE_KAOLA);
                        }
                    });

                    break;
                case R.id.bt_stop:
                    GlobalStatus.setDownloadForceStop(true);
                    break;
            }
        }
    };

    private void requestToken(final String type) {
        GlobalStatus.setDownloadForceStop(false);

        OTARepository.getInstance().getAuthToken(new TokenCallback() {
            @Override
            public void onSuccess(String token) {
                if (AppConst.TYPE_QQMUSIC.equals(type)) {
                    OTARepository.getInstance().getFileInfo(getApplicationContext(), type, mQQMusicFileName, token, new DownloadCallback() {
                        @Override
                        public void onSuccess(final String filePath, FileDomain.FileInfo info) {
                            Log.e("SG2","파일 다운로드 완료 : " + filePath);

                            new Handler(getBaseContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setProgress(0);
                                    showInstallDialog(type, filePath);
                                    setProgressDialog(false);
                                }
                            });
                        }

                        @Override
                        public void onPartialSuccess(String filePath, FileDomain.FileInfo info) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(false);
                                }
                            });
                            Log.e("SG2","파일 부분 다운로드 완료 : " + filePath);
                        }

                        @Override
                        public void onMD5Check() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(true);
                                }
                            });
                            Log.e("SG2","MD5 확인 중...");
                        }

                        @Override
                        public void onProgress(final int percent) {
                            new Handler(getBaseContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setMax(100);
                                    pb.setProgress(percent);
                                }
                            });
                        }

                        @Override
                        public void onFail(int errorType) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(false);
                                }
                            });
                            Log.e("SG2","파일 다운로드 실패 : " + errorType);
                            if (errorType == DownloadCallback.TYPE_ERROR_FILE_ERROR) {
                                Log.e("SG2","파일 다운로드 실패 : 파일 에러");
                            } else if (errorType == DownloadCallback.TYPE_ERROR_FILE_MD5_NOT_SAME) {
                                Log.e("SG2","파일 다운로드 실패 : MD5 에러");
                            } else if (errorType == DownloadCallback.TYPE_ERROR_STORAGE_FULL) {
                                Log.e("SG2","파일 다운로드 실패 : 디바이스 용량부족");
                            }
                        }
                    });
                } else if (AppConst.TYPE_KAOLA.equals(type)) {
                    OTARepository.getInstance().getFileInfo(getApplicationContext(), type, mKaolaFileName, token, new DownloadCallback() {
                        @Override
                        public void onSuccess(final String filePath, FileDomain.FileInfo info) {
                            Log.e("SG2","파일 다운로드 완료 : " + filePath);

                            new Handler(getBaseContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setProgress(0);
                                    showInstallDialog(type, filePath);
                                    setProgressDialog(false);
                                }
                            });
                        }

                        @Override
                        public void onPartialSuccess(String filePath, FileDomain.FileInfo info) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(false);
                                }
                            });
                            Log.e("SG2","파일 부분 다운로드 완료 : " + filePath);
                        }

                        @Override
                        public void onMD5Check() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(true);
                                }
                            });
                            Log.e("SG2","MD5 확인 중...");
                        }

                        @Override
                        public void onProgress(final int percent) {
                            new Handler(getBaseContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setMax(100);
                                    pb.setProgress(percent);
                                }
                            });
                        }

                        @Override
                        public void onFail(int errorType) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressDialog(false);
                                }
                            });
                            Log.e("SG2","파일 다운로드 실패 : " + errorType);
                            if (errorType == DownloadCallback.TYPE_ERROR_FILE_ERROR) {
                                Log.e("SG2","파일 다운로드 실패 : 파일 에러");
                            } else if (errorType == DownloadCallback.TYPE_ERROR_FILE_MD5_NOT_SAME) {
                                Log.e("SG2","파일 다운로드 실패 : MD5 에러");
                            } else if (errorType == DownloadCallback.TYPE_ERROR_STORAGE_FULL) {
                                Log.e("SG2","파일 다운로드 실패 : 디바이스 용량부족");
                            }
                        }
                    });
                }


            }

            @Override
            public void onFail() {

            }
        });
    }

    public void startInstallApk(String type, String filePath) {
        Log.e("sg2", "startInstallApk");
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        if (type.equals(AppConst.TYPE_QQMUSIC)) {
            startActivityForResult(intent, TYPE_QQMUSIC);
        }
        if (type.equals(AppConst.TYPE_KAOLA)) {
            startActivityForResult(intent, TYPE_KAOLAFM);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("kjw200", "resultCode : " + resultCode);
        if (requestCode == TYPE_QQMUSIC) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "설치가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                bt_qqmusic.setVisibility(View.GONE);
                tv_qqmusic_update.setVisibility(View.VISIBLE);
                OTARepository.getInstance().updateLog("Y", mQQMusicFileName, String.valueOf(mVersionQQ));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "설치가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "설치가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == TYPE_KAOLAFM) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "설치가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                bt_kaola.setVisibility(View.GONE);
                tv_kaola_update.setVisibility(View.VISIBLE);
                OTARepository.getInstance().updateLog("Y", mKaolaFileName, String.valueOf(mVersionKaola));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "설치가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "설치가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showInstallDialog (final String type, final String filePath) { // 다운완료 후 설치 다이얼로그
        AlertDialog.Builder installDialog = new AlertDialog.Builder(this);
        installDialog.setMessage("다운로드가 완료 되었습니다.\n지금 설치하시겠습니까?");
        installDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        installDialog.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startInstallApk(type, filePath);
                    }
                });
        installDialog.setCancelable(false);
        installDialog.show();
    }

    private void setProgressDialog (boolean isShowProgressDialog) { // MD5 체크 들어가기전 후 프로그래스 다이얼로그
        if (mProgressDialog != null) {
            if (isShowProgressDialog) {
                mProgressDialog.setMessage("확인중..");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            } else {
                if(mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }
}
