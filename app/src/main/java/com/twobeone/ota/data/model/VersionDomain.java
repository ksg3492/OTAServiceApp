package com.twobeone.ota.data.model;

import java.util.ArrayList;

public class VersionDomain {
    ArrayList<VersionInfo> result;
    String msg;
    String code;
    String httpStatus;

    public ArrayList<VersionInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<VersionInfo> result) {
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

    public class VersionInfo {
        String FILE_NAME;
        String FILE_TYPE;
        String FILE_VERSION;
        int FILE_VERSION_CODE;

        public String getFILE_NAME() {
            return FILE_NAME;
        }

        public void setFILE_NAME(String FILE_NAME) {
            this.FILE_NAME = FILE_NAME;
        }

        public String getFILE_TYPE() {
            return FILE_TYPE;
        }

        public void setFILE_TYPE(String FILE_TYPE) {
            this.FILE_TYPE = FILE_TYPE;
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
    }
}
