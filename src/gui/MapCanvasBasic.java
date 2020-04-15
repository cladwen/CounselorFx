package gui;

import helpers.Hexagon;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextAlignment;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasBasic {

    private final int xHexes, yHexes, hexSize;

    public MapCanvasBasic() {
        this.xHexes = 42;
        this.yHexes = 74;
        this.hexSize = 60;
        //WorldFacadeCounselor.getInstance().get;
    }

    public Canvas getCanvas() {
        Canvas canvas = new Canvas((xHexes + 1) * hexSize, (yHexes + 1) * hexSize * 3 / 4);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.scale(0.5, 0.5);
        doRenderDeserts(gc);
        return canvas;
    }

    private void doRenderDeserts(GraphicsContext gc) {
        Image desert = new Image("resources/hex_2b_deserto.gif");
        //centering text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (double x = 0; x < xHexes; x++) {
            for (double y = 0; y < yHexes; y++) {
                Point2D ret;
                if (y % 2 == 0) {
                    ret = new Point2D(x * hexSize, y * hexSize * 3 / 4);
                } else {
                    ret = new Point2D(x * hexSize + hexSize / 2, y * hexSize * 3 / 4);
                }

                gc.drawImage(desert, ret.getX(), ret.getY());
                Hexagon hex = new Hexagon(30d + ret.getX(), 30d + ret.getY());
                //System.out.println(ret.toString());
                // Set fill color
                gc.setFill(Color.rgb(188, 143, 143, 0.5));
                gc.setStroke(Color.RED);
//                gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                //gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                // Set line width
                gc.setLineWidth(1.0);
                gc.setFill(Color.BLUE);
                // Draw a filled Text
                gc.fillText(
                        String.format("%s %s", (int) x + 1, (int) y + 1),
                        hex.getCenterPoint().getX(), hex.getCenterPoint().getY());
            }
        }
    }

    private void doDrawHexagons(GraphicsContext gc) {
        //centering text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (double x = 0; x < 10; x++) {
            for (double y = 0; y < 3; y++) {
                Point2D ret;
                if (y % 2 == 0) {
                    ret = new Point2D(x * 60, y * 45);
                } else {
                    ret = new Point2D(x * 60 + 30, y * 45);
                }

                Hexagon hex = new Hexagon(30d + ret.getX(), 30d + ret.getY());
                //System.out.println(ret.toString());
                // Set fill color
                gc.setFill(Color.rgb(188, 143, 143, 0.5));
                gc.setStroke(Color.RED);
                gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                // Set line width
                gc.setLineWidth(1.0);
                gc.setFill(Color.BLUE);
                // Draw a filled Text
                gc.fillText(String.format("%s %s\n%s %s", hex.getCenterPoint().getX(), hex.getCenterPoint().getY(), (int) ret.getX(), (int) ret.getY()), hex.getCenterPoint().getX(), hex.getCenterPoint().getY());
            }
        }
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
