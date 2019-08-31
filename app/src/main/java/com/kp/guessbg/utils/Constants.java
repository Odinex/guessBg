package com.kp.guessbg.utils;

import com.kp.guessbg.models.GuessDto;

import static com.kp.guessbg.models.ActivityEnum.DRAW;
import static com.kp.guessbg.models.ActivityEnum.GESTURE;
import static com.kp.guessbg.models.ActivityEnum.SPEAK;

/**
 * Created by KO on 25-Aug-19.
 */

public final class Constants {
    public static  final GuessDto[] GUESS_DTOS = new GuessDto[] {
      new GuessDto("Кукер", SPEAK),new GuessDto("Кукер", GESTURE),new GuessDto("Кукер", DRAW),
            new GuessDto("Кукер", SPEAK),new GuessDto("Баба Марта", GESTURE),
            new GuessDto("Мартеница", GESTURE),new GuessDto("Борене с яйца", SPEAK),
            new GuessDto("Борене с яйца", GESTURE),new GuessDto("Борене с яйца", DRAW),
            new GuessDto("Бъдни вечер", DRAW),new GuessDto("Трифон Зарезан (или Ден на лозаря)", DRAW),
            new GuessDto("Гергьовден", GESTURE),new GuessDto("Еньовден", GESTURE),


    };
}
