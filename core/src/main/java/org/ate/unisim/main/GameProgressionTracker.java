package org.ate.unisim.main;

import java.util.Timer;
import java.util.TimerTask;

public class GameProgressionTracker {
    // Remaining time in seconds
    private int remainingTime = 60 * 5;
    Timer timer;

    GameProgressionTracker() {}

    void pauseCountdown() {
        timer.cancel();
    }

    void resumeCountdown() {
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

    boolean isGameOver() {
        return remainingTime == 0;
    }

    String displayRemainingTime() {
        int t = remainingTime;
        int seconds = t % 60;
        int minutes = t / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
