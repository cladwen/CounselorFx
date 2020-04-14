/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import static java.lang.Math.sqrt;

/**
 *
 * @author jmoura
 */
public final class Hexagon {

    double[] points;
    double center;

    private double xPixel;
    private double yPixel;
    private double[] listXCoord = new double[6];
    private double[] listYCoord = new double[6];
    private double a;
    private final double SIDE_SIZE = 30.5;

    public Hexagon(Double xPixel, Double yPixel) {

        listXCoord = new double[6];
        listYCoord = new double[6];
        a = Math.sqrt((SIDE_SIZE * SIDE_SIZE) - ((SIDE_SIZE / 2) * (SIDE_SIZE / 2)));
        this.xPixel = xPixel;
        this.yPixel = yPixel;
        calculHex();
    }

    public Hexagon() {
        listXCoord = new double[6];
        listYCoord = new double[6];
    }

    /**
     * @return the SIDE_SIZE
     */
    public double getSizeHex() {
        return SIDE_SIZE;
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
        a = Math.sqrt((SIDE_SIZE * SIDE_SIZE) - ((SIDE_SIZE / 2) * (SIDE_SIZE / 2)));
        listXCoord[0] = xPixel;
        listYCoord[0] = yPixel - SIDE_SIZE;
        listXCoord[1] = xPixel + a;
        listYCoord[1] = yPixel - (SIDE_SIZE / 2);
        listXCoord[2] = xPixel + a;
        listYCoord[2] = yPixel + (SIDE_SIZE / 2);
        listXCoord[3] = xPixel;
        listYCoord[3] = yPixel + SIDE_SIZE;
        listXCoord[4] = xPixel - a;
        listYCoord[4] = yPixel + (SIDE_SIZE / 2);
        listXCoord[5] = xPixel - a;
        listYCoord[5] = yPixel - (SIDE_SIZE / 2);

    }

    public Hexagon(double side) {
        center = getH(side);
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

    public double[] getPoints() {
        return points;
    }
}
