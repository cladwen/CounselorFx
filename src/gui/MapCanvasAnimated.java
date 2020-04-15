package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.AnimationTimer;
import gui.animation.AnimatedImage;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasAnimated {

    public Canvas getCanvas() {

        Canvas canvas = new Canvas(256, 256);
//        canvas.setScaleX(0.5);
//        canvas.setScaleY(0.5);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.scale(0.5, 0.5);
        Image earth = new Image("resources/earth.png");
        Image sun = new Image("resources/sun.png");
        Image space = new Image("resources/space.png");

        //Frames with independent images
        AnimatedImage ufo = new AnimatedImage();
        Image[] imageArray = new Image[6];
        for (int i = 0; i < 6; i++) {
            imageArray[i] = new Image("resources/ufo_" + i + ".png");
        }
        ufo.frames = imageArray;
        ufo.duration = 0.100;

        final long startNanoTime = System.nanoTime();

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                double x = 232 + 128 * Math.cos(t);
                double y = 232 + 128 * Math.sin(t);

                gc.drawImage(space, 0, 0);
                gc.drawImage(earth, x, y);
                gc.drawImage(sun, 196, 196);
                gc.drawImage(ufo.getFrame(t), 196 + x / 10, 196 + y / 10);
            }
        }.start();

        return canvas;
    }
}
