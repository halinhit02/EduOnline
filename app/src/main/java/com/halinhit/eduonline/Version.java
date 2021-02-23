package com.halinhit.eduonline;

public class Version {

    private float versionName;
    private String urlDownload;

    public Version() {
    }

    public Version(float versionName, String urlDownload) {
        this.versionName = versionName;
        this.urlDownload = urlDownload;
    }

    public float getVersionName() {
        return versionName;
    }

    public void setVersionName(float versionName) {
        this.versionName = versionName;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }
}
