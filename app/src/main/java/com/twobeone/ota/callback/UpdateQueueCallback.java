package com.twobeone.ota.callback;

public interface UpdateQueueCallback {
    int TYPE_ERROR_FILE_DIFFERENT = 0;          //기존파일 삭제(md5 checksum이 다르다던가..등등)
    int TYPE_ERROR_FILE_NEED_DOWNLOAD_CONTINUE = 1;     //이어받기 필요
    int TYPE_ERROR_FILE_NOT_EXIST = 2;                  //파일 미존재

    void onLocalUpdateExist(String filePath);

    void onLocalUpdateNonExist();
}
