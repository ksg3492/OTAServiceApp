package com.twobeone.ota;

public class GlobalStatus {
    private static boolean downloadForceStop = false;
    private static String currentUpdateType = null;

    public static void init() {
        downloadForceStop = false;
        currentUpdateType = null;
    }

    public static boolean isDownloadForceStop() {
        return downloadForceStop;
    }

    public static void setDownloadForceStop(boolean downloadForceStop) {
        GlobalStatus.downloadForceStop = downloadForceStop;
    }

    public static String getCurrentUpdateType() {
        return currentUpdateType;
    }

    public static void setCurrentUpdateType(String currentUpdateType) {
        GlobalStatus.currentUpdateType = currentUpdateType;
    }
}
