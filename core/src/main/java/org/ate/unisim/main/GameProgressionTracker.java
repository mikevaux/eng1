package org.ate.unisim.main;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Tracks the progress of the game as a 'countdown timer'.
 */
public class GameProgressionTracker {
    /**
     * The remaining time in seconds.
     */
    private int remainingTime = 60 * 5;
    /**
     * The actual `Timer` instance, to will be scheduled / cancelled.
     */
    Timer timer;

    /**
     * Creates a new `GameProgressionTracker`.
     */
    GameProgressionTracker() {}

    /**
     * Starts the countdown.
     */
    void startCountdown() {
        TimerTask decrement = new TimerTask() {
            @Override
            public void run() {
                remainingTime -= 1;
            }
        };

        // n.b. This must be created afresh for each resumption.
        timer = new Timer();
        timer.scheduleAtFixedRate(decrement, 0, 1000);
    }

    /**
     * "Pauses" the countdown.
     * Note that technically this "cancels" the countdown of the current second.
     */
    void pauseCountdown() {
        timer.cancel();
    }

    /**
     * Resumes the countdown.
     * (Just an alias for `startCountdown` at the moment, as there is currently no technical difference).
     */
    void resumeCountdown() {
        startCountdown();
    }

    /**
     * Returns true if the remaining time is zero, i.e. the game is over.
     *
     * @return true if the game is over.
     */
    boolean isGameOver() {
        return remainingTime == 0;
    }

    /**
     * Returns the remaining time formatted to mm:ss, e.g. 03:21.
     *
     * @return the formatted remaining time.
     */
    String formatRemainingTime() {
        int t = remainingTime;
        int seconds = t % 60;
        int minutes = t / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
