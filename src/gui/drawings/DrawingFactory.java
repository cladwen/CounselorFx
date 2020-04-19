/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.drawings;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
}
