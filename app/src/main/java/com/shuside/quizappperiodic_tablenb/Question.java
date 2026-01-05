package com.shuside.quizappperiodic_tablenb;

import java.util.ArrayList;

public class Question {
    private String questionText;
    private String answer;
    private String imageFilePath;
    private String soundFilePath;
    private String hint;
    private ArrayList<String> choices;
    private boolean hasBeenAnswered;

    public Question(String questionText, String hint, String answer, String imageFilePath, String soundFilePath, ArrayList<String> choices) {
        this.questionText = questionText;
        this.answer = answer;
        this.hint = hint;
        this.imageFilePath = imageFilePath;
        this.soundFilePath = soundFilePath;
        this.hasBeenAnswered = false;
        this.choices = choices;
    }

    // Getters
    public String getImageFilePath() {return imageFilePath;}
    public String getSoundFilePath() {return soundFilePath;}
    public String getAnswer() {return answer;}
    public String getHint() {return hint;}
    public String getQuestionText() {return questionText;}
    public ArrayList<String> getChoices() {return choices;}
    public boolean hasBeenAnswered() {return hasBeenAnswered;}

    // Setters
    public void setSoundFilePath(String soundFilePath) {this.soundFilePath = soundFilePath;}
    public void setAnswer(String answer) {this.answer = answer;}
    public void setChoices(ArrayList<String> choices) {this.choices = choices;}
    public void setHint(String hint) {this.hint = hint;}
    public void setQuestionText(String questionText) {this.questionText = questionText;}
    public void setImageFilePath(String imageFilePath) {this.imageFilePath = imageFilePath;}
    public void setHasBeenAnswered(boolean answered) {this.hasBeenAnswered = answered;}
}