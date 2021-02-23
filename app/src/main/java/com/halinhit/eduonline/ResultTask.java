package com.halinhit.eduonline;

public class ResultTask {

    private String name;
    private String uidUser;
    private String score;
    private String answerUser;
    private String answerTask;
    private String showAnswer;
    private String urlFile;
    private String nameFile;
    private String idTask;
    private String idClass;
    private int numCorrect;
    private int numQuestion;
    private long timeDone;

    public ResultTask() {
    }

    public ResultTask(String name, String uidUser, String score, String answerUser, String answerTask, String showAnswer, String urlFile, String nameFile, String idTask, String idClass, int numCorrect, int numQuestion, long timeDone) {
        this.name = name;
        this.uidUser = uidUser;
        this.score = score;
        this.answerUser = answerUser;
        this.answerTask = answerTask;
        this.showAnswer = showAnswer;
        this.urlFile = urlFile;
        this.nameFile = nameFile;
        this.idTask = idTask;
        this.idClass = idClass;
        this.numCorrect = numCorrect;
        this.numQuestion = numQuestion;
        this.timeDone = timeDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAnswerUser() {
        return answerUser;
    }

    public void setAnswerUser(String answerUser) {
        this.answerUser = answerUser;
    }

    public String getAnswerTask() {
        return answerTask;
    }

    public void setAnswerTask(String answerTask) {
        this.answerTask = answerTask;
    }

    public int getNumCorrect() {
        return numCorrect;
    }

    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }

    public int getNumQuestion() {
        return numQuestion;
    }

    public void setNumQuestion(int numQuestion) {
        this.numQuestion = numQuestion;
    }

    public long getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(long timeDone) {
        this.timeDone = timeDone;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(String showAnswer) {
        this.showAnswer = showAnswer;
    }

    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}
