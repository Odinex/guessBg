package com.kp.guessbg.models;

/**
 * Created by KO on 25-Aug-19.
 */

public enum  ActivityEnum {
    SPEAK("Опиши"),DRAW("Нарисувай"),GESTURE("С мимики");

    private final String value;

    ActivityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActivityEnum valueOf(int i) {
        switch (i) {
            case 0: return SPEAK;
            case 1: return DRAW;
            case 2: return GESTURE;
        }
        return null;
    }
}
