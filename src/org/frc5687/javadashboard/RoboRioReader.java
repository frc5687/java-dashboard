package org.frc5687.javadashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import javafx.application.Platform;

import java.time.Instant;

/**
 * Created by Ben Bernard on 3/14/2016.
 */
public class RoboRioReader implements Runnable {

    private NetworkTable piTracker;
    private int lastWidth = 0;
    private int lastCenterX = 0;

    private int lastTargetWidth = 0;
    private int lastTargetCenterX = 0;

    @Override
    public void run() {
        long mills;


        while (true) {
            mills = Instant.now().toEpochMilli();
            // Process game state
            // Process targeting
            processTargeting();
            // Process robot stats

            try {
                // Make sure that this has taken AT LEAST 20 milliseconds.
                // If not, sleep until 20ms have passed
                long w = (Instant.now().toEpochMilli() - mills);
                if (w < 20) {
                    Thread.sleep(20 - w);
                }
            } catch (Exception e) {
            }
        }
    }

    private void processTargeting() {
        int width = 100;
        int centerX = 150;

        int targetWidth = 120;
        int targetCenterX = 140;

        // Read from networktables
        // If any changes, schedule updates
        if (width!=lastWidth || centerX!=lastCenterX) {
            lastCenterX = centerX;
            lastWidth = width;
            Platform.runLater(() -> {Main.instance.renderGoal(width, centerX);});
        }

        if (targetWidth!=lastTargetWidth || targetCenterX!=lastTargetCenterX) {
            lastTargetCenterX = targetCenterX;
            lastTargetWidth = targetWidth;
            Platform.runLater(() -> {Main.instance.renderBoulder(targetWidth, targetCenterX);});
        }

    }


}
