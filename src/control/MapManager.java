/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import business.facade.LocalFacade;
import helpers.Hexagon;
import java.awt.Point;
import java.util.Collection;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.Local;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistence.local.ImageFactory;
import persistence.local.ListFactory;
import persistenceCommons.SettingsManager;

/**
 *
 * @author jmoura
 */
public class MapManager {

    private static final Log log = LogFactory.getLog(MapManager.class);
    private final ImageFactory imageFactory = new ImageFactory();
    private final LocalFacade localFacade = new LocalFacade();
    private Point farPoint;
    private int xHexes;
    private int yHexes;
    private final int hexSize;
    private final int coordinateFontSize;
    private final double zoomFactor;
    private static final int SCROLLBAR_SIZE = 15;
    private final boolean renderGrid;

    public MapManager() {
        this.hexSize = 60;
        //TODO: refactor map coordinates label to be a Text over canvas, out of GraphicsContext so that we can use CSS
        //FIXME: initializing on constructor will not work once we move to animation
        renderGrid = SettingsManager.getInstance().isConfig("MapGridRender", "1", "0");
        zoomFactor = (double) SettingsManager.getInstance().getConfigAsInt("MapZoomPercent", "100") / 100;
        coordinateFontSize = (int) Math.round(SettingsManager.getInstance().getConfigAsInt("MapCoordinatesSize", "8") / zoomFactor);
    }

    public Canvas getCanvas() {
        log.info("Start map");
        ListFactory listFactory = new ListFactory();
        Collection<Local> listaLocal = listFactory.listLocais().values();
        if (farPoint == null) {
            getMapMaxSize(listaLocal);
        }

        Canvas canvas = new Canvas(xHexes * hexSize * zoomFactor, (yHexes * hexSize * 3 / 4 + SCROLLBAR_SIZE) * zoomFactor);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.scale(zoomFactor, zoomFactor);
        gc.setFill(Color.GAINSBORO);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        doRenderTerrain(gc, listaLocal);
        if (renderGrid) {
            doRenderHexagonGrid(gc, listaLocal);
        }
        log.info("finish map");
        return canvas;
    }

    private void doRenderTerrain(GraphicsContext gc, Collection<Local> listaLocal) {
        //main loop
        for (Local local : listaLocal) {
            Point2D point = getPositionCanvas(local);
            gc.drawImage(imageFactory.getTerrainImage(local), point.getX(), point.getY());
            Hexagon hex = new Hexagon(point.getX(), point.getY(), hexSize / 2);
        }
    }

    private void doRenderHexagonGrid(GraphicsContext gc, Collection<Local> listaLocal) {
        //prepping styles
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(1.0);
        gc.setFont(new Font(coordinateFontSize));
        gc.setStroke(Color.RED);
        //calculate coordinates label relative position
//        int b = (hexSize/2)- coordinateFontSize;
        for (Local local : listaLocal) {
            //calculate position
            Point2D point = getPositionCanvas(local);
            Hexagon hex = new Hexagon(point.getX(), point.getY(), hexSize / 2);
            // draw filling
            gc.setFill(Color.rgb(188, 143, 143, 0.5));
            gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
            //draw grid
            gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);

            // write coordinates
            gc.setFill(Color.BLUE);
            gc.fillText(
                    local.getCoordenadas(),
                    hex.getCenterPoint().getX(), hex.getCenterPoint().getY() - (hexSize / 2) + coordinateFontSize
            );
        }
    }

    private void drawHexagon(GraphicsContext gc) {
        // Set fill color
        gc.setFill(Color.rgb(188, 143, 143, 0.5));
        gc.setStroke(Color.RED);
        //gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
        //gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
    }

    private Point2D getPositionCanvas(Local local) {
        //calculate position on canvas
        double x = localFacade.getCol(local) - 1;
        double y = localFacade.getRow(local) - 1;
        Point2D ret;
        if (y % 2 == 0) {
            ret = new Point2D(x * hexSize, y * hexSize * 3 / 4);
        } else {
            ret = new Point2D(x * hexSize + hexSize / 2, y * hexSize * 3 / 4);
        }
        return ret;
    }

    public Point getMapMaxSize(Collection<Local> listaLocal) {
        int[] ret = {0, 0};
        int row, col;
        for (Local local : listaLocal) {
            row = localFacade.getRow(local);
            col = localFacade.getCol(local);
            if (row > ret[0]) {
                ret[0] = row;
            }
            if (col > ret[1]) {
                ret[1] = col;
            }
        }
        this.farPoint = new Point(ret[1] * this.hexSize + this.hexSize / 2, ret[0] * this.hexSize * 3 / 4 + this.hexSize);
        xHexes = ret[1];
        yHexes = ret[0];
        return this.farPoint;
    }
}
