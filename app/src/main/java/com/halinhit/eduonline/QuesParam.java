package com.halinhit.eduonline;

public class QuesParam {

    private int serial;
    private int numCorrect;
    private int numIncorrect;

    public QuesParam(int serial, int numCorrect, int numIncorrect) {
        this.serial = serial;
        this.numCorrect = numCorrect;
        this.numIncorrect = numIncorrect;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public int getNumCorrect() {
        return numCorrect;
    }

    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }

    public int getNumIncorrect() {
        return numIncorrect;
    }

    public void setNumIncorrect(int numIncorrect) {
        this.numIncorrect = numIncorrect;
    }
}
