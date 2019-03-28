package com.twobeone.ota.data.model;

import java.util.ArrayList;

public class FileDomain {
    ArrayList<FileInfo> result;
    String msg;
    String code;
    String httpStatus;

    public ArrayList<FileInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<FileInfo> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public class FileInfo {
        String FILE_NAME;
        String FILE_MD5;
        String FILE_VERSION;
        int FILE_VERSION_CODE;
        long FILE_SIZE;
        String FILE_URL;

        public String getFILE_NAME() {
            return FILE_NAME;
        }

        public void setFILE_NAME(String FILE_NAME) {
            this.FILE_NAME = FILE_NAME;
        }

        public String getFILE_MD5() {
            return FILE_MD5;
        }

        public void setFILE_MD5(String FILE_MD5) {
            this.FILE_MD5 = FILE_MD5;
        }

        public String getFILE_VERSION() {
            return FILE_VERSION;
        }

        public void setFILE_VERSION(String FILE_VERSION) {
            this.FILE_VERSION = FILE_VERSION;
        }

        public int getFILE_VERSION_CODE() {
            return FILE_VERSION_CODE;
        }

        public void setFILE_VERSION_CODE(int FILE_VERSION_CODE) {
            this.FILE_VERSION_CODE = FILE_VERSION_CODE;
        }

        public long getFILE_SIZE() {
            return FILE_SIZE;
        }

        public void setFILE_SIZE(long FILE_SIZE) {
            this.FILE_SIZE = FILE_SIZE;
        }

        public String getFILE_URL() {
            return FILE_URL;
        }

        public void setFILE_URL(String FILE_URL) {
            this.FILE_URL = FILE_URL;
        }
    }
}
