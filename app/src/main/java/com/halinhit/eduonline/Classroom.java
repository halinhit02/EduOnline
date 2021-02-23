package com.halinhit.eduonline;

import java.io.Serializable;

public class Classroom implements Serializable {

    private String id;
    private String title;
    private int numMember;
    private String uidCreator;
    private String nameCreator;
    private String codeSecure;
    private String dateCreate;

    public Classroom() {
    }

    public Classroom(String id, String title, int numMember, String uidCreator, String nameCreator, String codeSecure, String dateCreate) {
        this.id = id;
        this.title = title;
        this.numMember = numMember;
        this.uidCreator = uidCreator;
        this.nameCreator = nameCreator;
        this.codeSecure = codeSecure;
        this.dateCreate = dateCreate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumMember() {
        return numMember;
    }

    public void setNumMember(int numMember) {
        this.numMember = numMember;
    }

    public String getUidCreator() {
        return uidCreator;
    }

    public void setUidCreator(String uidCreator) {
        this.uidCreator = uidCreator;
    }

    public String getNameCreator() {
        return nameCreator;
    }

    public void setNameCreator(String nameCreator) {
        this.nameCreator = nameCreator;
    }

    public String getCodeSecure() {
        return codeSecure;
    }

    public void setCodeSecure(String codeSecure) {
        this.codeSecure = codeSecure;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }
}
