package com.kp.guessbg.models;

/**
 * Created by KO on 25-Aug-19.
 */

public class GuessDto {
    private String word;
    private ActivityEnum activityEnum;

    public GuessDto(String word, ActivityEnum activityEnum) {
        this.word = word;
        this.activityEnum = activityEnum;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ActivityEnum getActivityEnum() {
        return activityEnum;
    }

    public void setActivityEnum(ActivityEnum activityEnum) {
        this.activityEnum = activityEnum;
    }
}
