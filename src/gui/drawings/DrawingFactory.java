/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.drawings;

import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 *
 * @author jmoura
 */
public class DrawingFactory {

    private static final int[][] coordRastros = {{8, 12}, {53, 12}, {60, 30}, {39, 59}, {23, 59}, {0, 30}};

    public void renderTrackArmy(GraphicsContext gc, int direction, Point2D point) {
        // Set line width
        gc.setLineWidth(3.0);
        // Set the Color
        gc.setStroke(Color.CRIMSON);
        // Set fill color
        gc.setFill(Color.RED);
        // Start the Path
        gc.beginPath();
        // Make different Paths
        gc.moveTo(point.getX() + 38, point.getY() + 38);
        gc.quadraticCurveTo(point.getX() + 38, point.getY() + 38,
                point.getX() + coordRastros[direction - 1][0],
                point.getY() + coordRastros[direction - 1][1]);
        gc.fill();
        // End the Path
        gc.closePath();
        // Draw the Path
        gc.stroke();

        //TODO: make it look like the old Counselor. Replace with animation?
        /*
        big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gc.setStroke(new BasicStroke(
                2f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                1f,
                new float[]{5f},
                0f));
         */
    }

    public void renderCity(GraphicsContext gc, Point2D point, int tamanho, Color nacaoColorFill, Color nacaoColorBorder) {
        // Set line width
        gc.setLineWidth(1.0);
        // Set the Color
        gc.setStroke(nacaoColorBorder);
        // Set fill color
        gc.setFill(nacaoColorFill);
        // Start the Path
        gc.beginPath();
        // Make different Paths
        gc.moveTo(point.getX(), point.getY());
        gc.lineTo(point.getX(), point.getY() + 38);
        gc.lineTo(point.getX() + 38, point.getY() + 38);
        gc.lineTo(point.getX() + 38, point.getY() - 38);
        gc.fill();
        // End the Path
        gc.closePath();
        // Draw the Path
        gc.stroke();
    }

    private Polygon getPolygon() {
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
}
