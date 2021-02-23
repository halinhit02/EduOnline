package com.halinhit.eduonline;

public class InforUser {

    private String uid;
    private String name;

    public InforUser() {
    }

    public InforUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
