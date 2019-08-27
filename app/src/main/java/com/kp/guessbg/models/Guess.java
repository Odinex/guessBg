package com.kp.guessbg.models;

/**
 * Created by KO on 25-Aug-19.
 */

public class Guess {
    private Integer id;
    private String word;
    private Integer activity;
    private Integer countOfUsages;

    public Guess(int i, String string, Integer activity, int countOfUsages) {
        this.id = i;
        this.word = string;
        this.activity = activity;
        this.countOfUsages = countOfUsages;
    }

    public Integer getCountOfUsages() {
        return countOfUsages;
    }

    public void setCountOfUsages(Integer countOfUsages) {
        this.countOfUsages = countOfUsages;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
