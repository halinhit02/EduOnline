package com.halinhit.eduonline;

public class Notification {

    private String idNotification;
    private String namePoster;
    private String uidPoster;
    private String content;
    private String urlPhoto;
    private String dateCreate;
    private String idClass;

    public Notification() {
    }

    public Notification(String idNotification, String namePoster, String uidPoster, String content, String urlPhoto, String dateCreate, String idClass) {
        this.idNotification = idNotification;
        this.namePoster = namePoster;
        this.uidPoster = uidPoster;
        this.content = content;
        this.urlPhoto = urlPhoto;
        this.dateCreate = dateCreate;
        this.idClass = idClass;
    }

    public String getNamePoster() {
        return namePoster;
    }

    public void setNamePoster(String namePoster) {
        this.namePoster = namePoster;
    }

    public String getUidPoster() {
        return uidPoster;
    }

    public void setUidPoster(String uidPoster) {
        this.uidPoster = uidPoster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
}
