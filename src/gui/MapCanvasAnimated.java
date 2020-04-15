package gui;

import helpers.Hexagon;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import toSort.AnimatedImage;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasAnimated {

    public Canvas getCanvas() {

        Canvas canvas = new Canvas(512, 512);

        GraphicsContext gc = canvas.getGraphicsContext2D();

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
//                drawStarShape(gc);
                drawHexagonShape(gc);
                gc.drawImage(ufo.getFrame(t), 196 + x / 10, 196 + y / 10);
            }
        }.start();
        return canvas;
    }

    private void drawStarShape(GraphicsContext gc) {

        Random random = new Random(System.currentTimeMillis());

        double[] xpoints = {10, 85, 110, 135, 210, 160,
            170, 110, 50, 60};
        double[] ypoints = {85, 75, 10, 75, 85, 125,
            190, 150, 190, 125};
        gc.setFill(Color.rgb(random.nextInt(255), random.nextInt(255),
                random.nextInt(255), 0.9));

        gc.fillPolygon(xpoints, ypoints, xpoints.length);
    }

    private void drawHexagonShape(GraphicsContext gc) {
                // Set line width
                gc.setLineWidth(1.0);
                gc.setFill(Color.BLUE);

                // Draw a Text
                gc.strokeText("This is a stroked Text", 10, 50);
                gc.strokeText("This is a stroked Text with Max Width 300 px", 10, 100, 300);
                // Draw a filled Text
                gc.fillText("This is a filled Text", 10, 150);
                gc.fillText("This is a filled Text with Max Width 400 px", 10, 200, 400);
        for (double col = 0; col < 20; col++) {
            for (double row = 0; row < 20; row++) {
                Point2D ret;
                ret = new Point2D(col * 60, row * 45);
                if (row % 2 != 0) {
                    ret.add(30, 30);
                }
                Hexagon hex = new Hexagon(30d + ret.getX(), 30d + ret.getY());
                System.out.println(ret.toString());
                // Set fill color
                gc.setFill(Color.rgb(188, 143, 143, 0.5));
                gc.setStroke(Color.RED);
                gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);

            }
        }
    }

    public Polygon setComplete() {
        double rLen = 100 - 5;
        Polygon s1 = new Polygon(0, (rLen - 5) / 2,
                (rLen - 5) / 2, (rLen - 5) / 2,
                (rLen - 5) / 2, 0,
                (rLen + 5) / 2, 0,
                (rLen + 5) / 2, (rLen - 5) / 2,
                rLen, (rLen - 5) / 2,
                rLen, (rLen + 5) / 2,
                (rLen + 5) / 2, (rLen + 5) / 2,
                (rLen + 5) / 2, rLen,
                (rLen - 5) / 2, rLen,
                (rLen - 5) / 2, (rLen + 5) / 2,
                0, (rLen + 5) / 2);
        s1.setFill(Color.RED);
        s1.setRotate(45);
        s1.setStroke(Color.BLACK);
        s1.setStrokeWidth(3);
        return s1;
    }
}
