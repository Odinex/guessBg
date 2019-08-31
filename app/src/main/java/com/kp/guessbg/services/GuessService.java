package com.kp.guessbg.services;

import android.content.Context;

import com.kp.guessbg.database.config.DatabaseHandler;
import com.kp.guessbg.models.Guess;
import com.kp.guessbg.models.GuessDto;
import com.kp.guessbg.utils.Constants;

/**
 * Created by KO on 25-Aug-19.
 */

public class GuessService {
    private DatabaseHandler databaseHandler;

    public GuessService(Context context) {
        this.databaseHandler = new DatabaseHandler(context);
        for(GuessDto guessDto : Constants.GUESS_DTOS) {
            databaseHandler.addGuess(guessDto);
        }
    }

    public Guess getRandomGuess() {
        return databaseHandler.getRandomGuess();
    }
}
