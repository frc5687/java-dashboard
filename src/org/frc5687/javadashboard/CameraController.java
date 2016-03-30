package org.frc5687.javadashboard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;

public class CameraController implements Runnable {
    ImageView robotView;
    ImageView poleView;

    VideoCapture robotStream;
    VideoCapture poleStream;

    Pane overlay;

    public CameraController(ImageView robotView, ImageView poleView, Pane overlay) {
        this.robotView = robotView;
        this.poleView = poleView;
        this.overlay = overlay;
    }

    public void run() {
        start();
        long mills;
        Mat robotFrame = new Mat();
        Mat poleFrame = new Mat();

        while (true) {
            mills = Instant.now().toEpochMilli();

            if (robotStream!=null) {
                capture(robotStream, robotFrame);
                render(robotFrame, robotView);
            }

            if (poleStream!=null) {
                capture(poleStream, poleFrame);
                render(poleFrame, poleView);
            }

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

    public void start() {
        System.loadLibrary("opencv_java310");
        System.loadLibrary("opencv_ffmpeg310_64");

        robotStream = new VideoCapture("http://192.168.1.22:1180/?action=stream&file=video.mjpg");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            robotStream = null;
        }

        poleStream = new VideoCapture(0);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            poleStream = null;
        }
    }
    public void capture(VideoCapture stream,  Mat frame) {
        stream.read(frame);
    }


    public void render(Mat frame, ImageView view) {
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", frame, byteMat);
        Image image = new Image(new ByteArrayInputStream(byteMat.toArray()));
        view.setImage(image);
    }
}
