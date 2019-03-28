package com.twobeone.ota;

public class GlobalStatus {
    private static boolean downloadForceStop = false;

    public static void init() {
        downloadForceStop = false;
    }

    public static boolean isDownloadForceStop() {
        return downloadForceStop;
    }

    public static void setDownloadForceStop(boolean downloadForceStop) {
        GlobalStatus.downloadForceStop = downloadForceStop;
    }
}
