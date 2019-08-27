package com.kp.guessbg.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    private View mContentView;
    private TeamService teamService;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
    private Guess currentGuess;
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
        teamService = new TeamService();
        currentIndex = 0;
        Team team = teamService.getCurrentTeams().get(currentIndex);
        teamService.setAsCurrentGuesser(currentIndex);
        TextView teamDetails = findViewById(R.id.teamDetails);
        teamDetails.setText(String.format("Oтбор: \"%s\", №%d е на ред.", team.getName(), team.getId()));
        guessService = new GuessService(this);
        currentGuess = guessService.getRandomGuess();
        TextView guess = findViewById(R.id.word);
        String activity = getActivity(currentGuess.getActivity());
        String word = getWord(currentGuess.getWord());
        guess.setText(String.format("%s: \"%s\"", activity, word));

        countDownTimer = new CountDownTimer(60000, 1000) {



            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                timerTextField.setText(String.format("Оставащо време: %d", millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                Button startStopTimer = findViewById(R.id.startStopTimer);
                startStopTimer.setVisibility(View.INVISIBLE);
                timerTextField.setText("Спри!");
            }

        };

        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
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
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
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

    public void startTimerAndHideWord(View view) {
        final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.fullscreen_content_controls);
        final TextView word = (TextView) transitionsContainer.findViewById(R.id.word);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(transitionsContainer);
        }
        boolean visible = word.getVisibility() == View.VISIBLE;
        Button button = (Button) view;
        visible = !visible;
        if(visible) {
            button.setText("Покажи");
        } else {
            button.setText("Скрий");
        }
        word.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void startStopTimer(View view) {
        if(isPaused) {
            if (MINUTE != millisLeft) {
                countDownTimer = new CountDownTimer(millisLeft, 1000) {
                    @SuppressLint("DefaultLocale")
                    public void onTick(long millisUntilFinished) {
                        millisLeft = millisUntilFinished;
                        timerTextField.setText(String.format("Оставащо време: %d", millisUntilFinished / 1000));
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        Button startStopTimer = findViewById(R.id.startStopTimer);
                        startStopTimer.setVisibility(View.INVISIBLE);
                        timerTextField.setText("Спри!");
                    }
                };
            }
            countDownTimer.start();
            isPaused = false;
            Button startStopTimer = findViewById(R.id.startStopTimer);
            startStopTimer.setText("Спри");
        } else {
            Button startStopTimer = findViewById(R.id.startStopTimer);
            startStopTimer.setText("Продължи");
            countDownTimer.cancel();
            isPaused = true;
        }
    }
}
