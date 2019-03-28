package com.twobeone.ota;

import android.app.ProgressDialog;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.UUID;

public class Util {

    public static String getUniqueId() {
        return "test1";
//        return UUID.randomUUID().toString();
    }

    public static String checkMD5(File file) {
        Log.e("sg2", "MD5 체크시작");
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DigestInputStream dis = new DigestInputStream(bis, md5);

            while(dis.read() != -1) ;

            byte[] hash = md5.digest();

            dis.close();
            return byteArray2Hex(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byteArray2Hex(byte[] hash) {
        try {
            String resultData;
            Formatter formatter = new Formatter();
            for(byte b : hash) {
                formatter.format("%02X", b);
            }
            resultData = formatter.toString();
            formatter.close();
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
