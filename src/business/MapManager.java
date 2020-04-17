/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import business.facade.CidadeFacade;
import business.facade.LocalFacade;
import gui.drawings.DrawingFactory;
import helpers.Hexagon;
import java.awt.Point;
import java.util.Collection;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.Cidade;
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
    private final DrawingFactory drawingFactory = new DrawingFactory();
    private final LocalFacade localFacade = new LocalFacade();
    private final CidadeFacade cityFacade = new CidadeFacade();
    private Point farPoint;
    private int xHexes;
    private int yHexes;
    private final int hexSize;
    private final int coordinateFontSize;
    private final double zoomFactor;
    private static final int SCROLLBAR_SIZE = 15;
    private final boolean renderGrid;
    private boolean renderRoads;
    private boolean renderRivers;
    private boolean renderCreek;
    private boolean renderBridge;
    private boolean renderSpan;
    private boolean renderTracks;
    private boolean renderLanding;
    private boolean directionBased;
    private boolean renderLandmark;
    private boolean renderCities;
    private boolean renderForts;

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
        setRenderingFlags();
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
            //draw terrain
            gc.drawImage(imageFactory.getTerrainImage(local), point.getX(), point.getY());
            //roads, rivers, bridges, span, creek, landing, army tracks
            doRenderDecorations(local, gc, point);
            doRenderCity(local, gc, point);

        }
    }

    private void doRenderCity(Local local, GraphicsContext gc, Point2D point) {
        //cities
        if (!localFacade.isCidade(local)) {
            return;
        }
        Cidade city = localFacade.getCidade(local);
        //render fortification
        if (renderForts && cityFacade.isFortificacao(city)) {
            final Image img = ImageFactory.getFortificationImage(cityFacade.getFortificacao(city));
            gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
        }

        if (renderCities && cityFacade.getTamanho(city) > 0) {
            //FIXME: City color
            //Image colorCp = ColorFactory.setNacaoColor(
            //        this.desenhoCidades[+6 + cpEscondido],
            //      cityFacade.getNacaoColorFill(city),
            //    cityFacade.getNacaoColorBorder(city),
            //  form);
            if (!cityFacade.isOculto(city)) {
                //regular visible city
                final Image img = ImageFactory.getCityImage(cityFacade.getTamanho(city));
                gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
            } else {
                //hidden city
                final Image img = ImageFactory.getHiddenCityImage(cityFacade.getTamanho(city));
                gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
            }
        }

        //draw docks
        final Image docksImg = ImageFactory.getDockImage(cityFacade.getDocas(city));
        gc.drawImage(docksImg, point.getX() + (hexSize - docksImg.getWidth()) / 2, point.getY() + 2);

        //draw capital
        if (cityFacade.isCapital(city)) {
            final Image img = ImageFactory.getCapitalImage();
            gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 30);
        }
    }

    private void doRenderDecorations(Local local, GraphicsContext gc, Point2D point) {
        for (int direction = 1; direction < 7 && directionBased; direction++) {
            //draw roads
            if (renderRoads && localFacade.isEstrada(local, direction)) {
                gc.drawImage(ImageFactory.getRoadImage(direction), point.getX(), point.getY());
            }
            //draw rivers
            if (renderRivers && localFacade.isRio(local, direction)) {
                gc.drawImage(ImageFactory.getRiverImage(direction), point.getX(), point.getY());
            }
            //draw rivers
            if (renderCreek && localFacade.isRiacho(local, direction)) {
                gc.drawImage(ImageFactory.getCreekImage(direction), point.getX(), point.getY());
            }
            //draw army tracks
            if (renderTracks && localFacade.isRastroExercito(local, direction) && localFacade.isVisible(local)) {
                drawingFactory.renderTrackArmy(gc, direction, point);
//                    gc.drawImage(imageFactory.getTracksImage(direcao), point.getX(), point.getY());
            }
            //draw bridges
            if (renderBridge && localFacade.isPonte(local, direction)) {
                gc.drawImage(ImageFactory.getBridgeImage(direction), point.getX(), point.getY());
            }
            //draw bridges
            if (renderSpan && localFacade.isVau(local, direction)) {
                gc.drawImage(ImageFactory.getSpanImage(direction), point.getX(), point.getY());
            }
            //draw landing/coast
            if (renderLanding && localFacade.isLanding(local, direction)) {
                gc.drawImage(ImageFactory.getLandingImage(direction), point.getX(), point.getY());
            }
        }
    }

    private void setRenderingFlags() {
        //TODO: load from settings
        //direction based
        renderRoads = true;
        renderRivers = true;
        renderCreek = true;
        renderBridge = true;
        renderSpan = true;
        renderTracks = true;
        renderLanding = true;
        directionBased = renderRoads && renderRivers && renderCreek && renderBridge && renderSpan && renderTracks && renderLanding;
        //other decorations
        renderLandmark = true;
        renderCities = true;
        renderForts = true;
    }

    private void doRenderHexagonGrid(GraphicsContext gc, Collection<Local> listLocal) {
        //prepping styles
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(1.0);
        gc.setFont(new Font(coordinateFontSize));
        gc.setStroke(Color.RED);
        //calculate coordinates label relative position
//        int b = (hexSize/2)- coordinateFontSize;
        for (Local local : listLocal) {
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
