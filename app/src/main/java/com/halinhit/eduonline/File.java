package com.halinhit.eduonline;

public class File {

    private String title;
    private String urlFile;

    public File() {
    }

    public File(String title, String urlFile) {
        this.title = title;
        this.urlFile = urlFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }
}
