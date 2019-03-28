package com.twobeone.ota;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class DialogActivity extends Activity {

    private AlertDialog checkDialog = null;

    private Intent mIntent = null;

    private String mQQMusicFileName = "";
    private String mKaolaFileName = "";
    private int mVersionQQ = 0;
    private int mVersionKaola = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);
        makeDialog();

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

                if (checkDialog != null && !checkDialog.isShowing()) {
                    checkDialog.show();
                    checkDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            } else {
                finish();
            }

        }
    }

    private void makeDialog() {
        {
            if (checkDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);

                builder.setMessage("최신버전 업데이트가 있습니다.\n업데이트 하시겠습니까?");
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setPositiveButton("업데이트",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(DialogActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra(AppConst.INTENT_FROM_SERVICE, true);
                                intent.putExtra(AppConst.INTENT_QQMUSIC_FILE_NAME, mQQMusicFileName);
                                intent.putExtra(AppConst.INTENT_KAOLA_FILE_NAME, mKaolaFileName);
                                intent.putExtra(AppConst.INTENT_QQMUSIC_SERVER_VERSION, mVersionQQ);
                                intent.putExtra(AppConst.INTENT_KAOLA_SERVER_VERSION, mVersionKaola);
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.setCancelable(false);
                checkDialog = builder.create();
            }
        }
    }
}
