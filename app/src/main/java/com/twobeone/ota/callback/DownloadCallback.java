package com.twobeone.ota.callback;

import com.twobeone.ota.data.model.FileDomain;

public interface DownloadCallback {
    int TYPE_ERROR_DOWNLOAD_FAIL = 1;                //다운로드 에러
    int TYPE_ERROR_TOKEN_ERROR = 2;                 //토큰 에러
    int TYPE_ERROR_SERVER_ERROR = 3;                 //서버 통신 에러
    int TYPE_ERROR_STORAGE_FULL = 4;                 //저장공간 부족
    int TYPE_ERROR_FILE_ERROR = 5;                   //파일 관련 에러
    int TYPE_ERROR_FILE_MD5_NOT_SAME = 6;            //MD5 정보 불일치

    void onSuccess(String filePath, FileDomain.FileInfo info);

    void onPartialSuccess(String filePath, FileDomain.FileInfo info);

    void onMD5Check();

    void onProgress(int percent);

    void onFail(int errorType);
}
