package gui;

import business.MapManager;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasBasic {

    private final MapManager mapManager;

    public MapCanvasBasic() {
        this.mapManager = new MapManager();
    }

    public Canvas getCanvas() {
        return this.mapManager.getCanvas();
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
