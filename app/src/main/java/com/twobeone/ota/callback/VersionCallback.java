package com.twobeone.ota.callback;

public interface VersionCallback {
    int TYPE_ERROR_NOT_INSTALLED = 0;
    int TYPE_ERROR_SERVER_ERROR = 1;

    //qqmusic, kaola : 업데이트 파일이름
    void onSuccess(String qqmusic, int qqmusicVersion, String kaola, int kaolaVersion);

    void onFail(int qqerror, int kaolaerror);
}
