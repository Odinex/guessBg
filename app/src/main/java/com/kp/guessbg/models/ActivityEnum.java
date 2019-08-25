package com.kp.guessbg.models;

/**
 * Created by KO on 25-Aug-19.
 */

public enum  ActivityEnum {
    SPEAK,DRAW,GESTURE;
    public static ActivityEnum valueOf(int i) {
        switch (i) {
            case 0: return SPEAK;
            case 1: return DRAW;
            case 2: return GESTURE;
        }
        return null;
    }
}
