package com.halinhit.eduonline;

import java.io.Serializable;

public class Task implements Serializable {

    private String id;
    private String title;
    private String date;
    private String urlFile;
    private String timeDone;
    private String dateFinish;
    private int complete;
    private int numQuestion;
    private String answer;
    private String showAnswer;
    private String idClass;
    private String uidCreator;

    public Task() {
    }

    public Task(String id, String title, String date, String urlFile, String timeDone, String dateFinish, int complete, int numQuestion, String answer, String showAnswer, String idClass, String uidCreator) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.urlFile = urlFile;
        this.timeDone = timeDone;
        this.dateFinish = dateFinish;
        this.complete = complete;
        this.numQuestion = numQuestion;
        this.answer = answer;
        this.showAnswer = showAnswer;
        this.idClass = idClass;
        this.uidCreator = uidCreator;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public String getUidCreator() {
        return uidCreator;
    }

    public void setUidCreator(String uidCreator) {
        this.uidCreator = uidCreator;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(String timeDone) {
        this.timeDone = timeDone;
    }

    public String getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(String dateFinish) {
        this.dateFinish = dateFinish;
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public void setNumQuestion(int numQuestion) {
        this.numQuestion = numQuestion;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(String showAnswer) {
        this.showAnswer = showAnswer;
    }
}
