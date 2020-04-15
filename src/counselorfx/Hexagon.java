/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import static java.lang.Math.sqrt;
import javafx.geometry.Point2D;

/**
 *
 * @author jmoura
 */
public final class Hexagon {

    private double xPixel;
    private double yPixel;
    private double[] listXCoord = new double[6];
    private double[] listYCoord = new double[6];
    private double a;
    private double sideSize;
    private Point2D centerPoint;

    public Hexagon(Double xPixel, Double yPixel, Double sideLenght) {
        //side size if 1/2 height
        sideSize = sideLenght;
        listXCoord = new double[6];
        listYCoord = new double[6];
        a = Math.sqrt((sideSize * sideSize) - ((sideSize / 2) * (sideSize / 2)));
        this.xPixel = xPixel;
        this.yPixel = yPixel;
        calculHex();
    }

    public Hexagon(Double xPixel, Double yPixel) {
        this(xPixel, yPixel, 30.5d);
    }

    private Hexagon() {
        listXCoord = new double[6];
        listYCoord = new double[6];
    }

    /**
     * @return the SIDE_SIZE
     */
    public double getSizeHex() {
        return sideSize;
    }

    /**
     * @return the listXCoord
     */
    public double[] getListXCoord() {
        return listXCoord;
    }

    /**
     * @return the listYCoord
     */
    public double[] getListYCoord() {
        return listYCoord;
    }

    /**
     * @param xPixel the xPixel to set
     */
    public void setxPixel(double xPixel) {
        this.xPixel = xPixel;
    }

    /**
     * @param yPixel the yPixel to set
     */
    public void setyPixel(double yPixel) {
        this.yPixel = yPixel;
    }

    /**
     * Calcul a list of X coordinates and Y coordinates for the points of hexagon
     */
    public void calculHex() {
        a = Math.sqrt((sideSize * sideSize) - ((sideSize / 2) * (sideSize / 2)));
        listXCoord[0] = xPixel;
        listYCoord[0] = yPixel - sideSize;
        listXCoord[1] = xPixel + a;
        listYCoord[1] = yPixel - (sideSize / 2);
        listXCoord[2] = xPixel + a;
        listYCoord[2] = yPixel + (sideSize / 2);
        listXCoord[3] = xPixel;
        listYCoord[3] = yPixel + sideSize;
        listXCoord[4] = xPixel - a;
        listYCoord[4] = yPixel + (sideSize / 2);
        listXCoord[5] = xPixel - a;
        listYCoord[5] = yPixel - (sideSize / 2);
        centerPoint = new Point2D(xPixel, yPixel);
    }

    //this was the other class for example.
    private double[] points;

    private Hexagon(double side) {
        double center = getH(side);
        points = new double[12];
        //     X                          Y
        points[0] = 0.0;
        points[1] = 0.0;
        points[2] = side;
        points[3] = 0.0;
        points[4] = side + (side / 2);
        points[5] = center;
        points[6] = side;
        points[7] = center * 2;
        points[8] = 0.0;
        points[9] = center * 2;
        points[10] = -side / 2;
        points[11] = center;

    }

    private double getH(double side) {
        return ((sqrt(3) / 2) * side);
    }

    private double[] getPoints() {
        return points;
    }

    /**
     * @return the centerPoint
     */
    public Point2D getCenterPoint() {
        return centerPoint;
    }
}
