package com.kp.guessbg.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kp.guessbg.R;
import com.kp.guessbg.models.ActivityEnum;
import com.kp.guessbg.models.Guess;
import com.kp.guessbg.models.Team;
import com.kp.guessbg.services.GuessService;
import com.kp.guessbg.services.TeamService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GuessActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    public static final long MINUTE = 60000L;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar


        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private GuessService guessService;
    private CountDownTimer countDownTimer;
    public long millisLeft = MINUTE;
    private boolean isPaused = true;
    private TextView timerTextField;
    private int currentIndex;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guess);
        timerTextField = findViewById(R.id.timerTextField) ;
        mVisible = true;
        ImageButton expand = findViewById(R.id.expand);
        expand.setVisibility(View.INVISIBLE);
        guessService = new GuessService(this);
        Team team = TeamService.getCurrentTeam();
        currentIndex = team.getId();
        resetTextFields(team);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                long secondsLeft = millisUntilFinished / 1000;
                timerTextField.setText(String.format("Оставащо време: %d", secondsLeft));
                if(secondsLeft == 10) {
                    new Runnable() {
                        public void run() {
                            playSound();
                        }

                        void playSound() {
                            MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.whistle);
                            mp.start();
                        }

                    }.run();
                }
            }

            public void onFinish() {
                Button startStopTimer = findViewById(R.id.startStopTimer);
                startStopTimer.setVisibility(View.INVISIBLE);
                timerTextField.setText("Спри!");
                new Runnable() {
                    public void run() {
                        playSound();
                    }

                    void playSound() {
                        MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.bell);
                        mp.start();
                    }

                }.run();
            }

        };
    }

    private void resetTextFields(Team team) {
        TextView teamDetails = findViewById(R.id.teamDetails);
        teamDetails.setText(String.format("Oтбор: \"%s\", №%d е на ред.", team.getName(), team.getId()+1));

        Guess currentGuess = guessService.getRandomGuess();
        TextView guess = findViewById(R.id.word);
        String activity = getActivity(currentGuess.getActivity());
        String word = getWord(currentGuess.getWord());
        guess.setText(String.format("%s: \"%s\"", activity, word));
    }

    private String getWord(String word) {
        if(word != null) {
            return word;
        } else {
            return "Хоро";
        }
    }

    private String getActivity(Integer activity) {
        ActivityEnum activityEnum = ActivityEnum.valueOf(activity);
        if(activityEnum != null) {
            return activityEnum.getValue();
        } else {
            return ActivityEnum.DRAW.getValue();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        ImageButton expand = findViewById(R.id.expand);
        expand.setVisibility(View.INVISIBLE);
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
        ImageButton expand = findViewById(R.id.expand);
        expand.setVisibility(View.VISIBLE);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void expand(View view) {
        hide();
    }

    public void showHideWord(View view) {
        final ViewGroup transitionsContainer = findViewById(R.id.fullscreen_content_controls);
        final TextView word = transitionsContainer.findViewById(R.id.word);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(transitionsContainer);
        }
        boolean visible = word.getVisibility() == View.VISIBLE;
        Button button = (Button) view;
        visible = !visible;
        button.setText(visible ? "Скрий" : "Покажи");
        word.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void startStopTimer(View view) {
        if(isPaused) {
            if(((Button) view).getText().equals("Начало")) {
                Button guessed = findViewById(R.id.guessedButton);
                guessed.setVisibility(View.VISIBLE);
                Button failed = findViewById(R.id.failedButton);
                failed.setVisibility(View.VISIBLE);
            }
            if (MINUTE != millisLeft) {
                countDownTimer = new CountDownTimer(millisLeft, 1000) {
                    @SuppressLint("DefaultLocale")
                    public void onTick(long millisUntilFinished) {
                        millisLeft = millisUntilFinished;
                        long secondsLeft = millisUntilFinished / 1000;
                        timerTextField.setText(String.format("Оставащо време: %d", secondsLeft));
                        if(secondsLeft == 10) {
                            new Runnable() {
                                public void run() {
                                    playSound();
                                }

                                void playSound() {
                                    MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.whistle);
                                    mp.start();
                                }

                            }.run();
                        }
                    }

                    public void onFinish() {
                        Button startStopTimer = findViewById(R.id.startStopTimer);
                        startStopTimer.setVisibility(View.INVISIBLE);
                        timerTextField.setText("Спри!");
                        new Runnable() {
                            public void run() {
                                playSound();
                            }

                            void playSound() {
                                MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.bell);
                                mp.start();
                            }

                        }.run();
                    }
                };
            }
            countDownTimer.start();
            isPaused = false;
            Button startStopTimer = findViewById(R.id.startStopTimer);
            startStopTimer.setText("Пауза");
        } else {
            Button startStopTimer = findViewById(R.id.startStopTimer);
            startStopTimer.setText("Продължи");
            countDownTimer.cancel();
            isPaused = true;
        }
    }

    public void onGuessed(View view) {
        // TODO complicate points and guess
        Intent in;
        if(TeamService.hasWon(currentIndex, 1)) {
            in=new Intent(GuessActivity.this,ResultsActivity.class);
        } else {
            Team next = TeamService.getNextTeam(currentIndex);
            currentIndex = next.getId();
            in=new Intent(GuessActivity.this,GuessActivity.class);
        }
        countDownTimer.cancel();
        finish();
        startActivity(in);
    }

    public void onFailed(View view) {
        Team next = TeamService.getNextTeam(currentIndex);
        currentIndex = next.getId();
        Intent in=new Intent(GuessActivity.this,GuessActivity.class);
        countDownTimer.cancel();
        finish();
        startActivity(in);
    }

    public void showResults(View view) {
        Intent in=new Intent(GuessActivity.this,ResultsActivity.class);
        countDownTimer.cancel();
        finish();
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in=new Intent(GuessActivity.this,MenuActivity.class);
        countDownTimer.cancel();
        finish();
        startActivity(in);
    }
}
