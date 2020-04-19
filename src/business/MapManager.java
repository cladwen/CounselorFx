/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import business.facade.ArtefatoFacade;
import business.facade.CidadeFacade;
import business.facade.ExercitoFacade;
import business.facade.JogadorFacade;
import business.facade.LocalFacade;
import business.facade.PersonagemFacade;
import control.WorldFacadeCounselor;
import gui.drawings.DrawingFactory;
import helpers.Hexagon;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.Artefato;
import model.Cidade;
import model.Exercito;
import model.Habilidade;
import model.Jogador;
import model.Local;
import model.Nacao;
import model.Personagem;
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
    private static final int SCROLLBAR_SIZE = 15;
    private final ImageFactory imageFactory;
    private final DrawingFactory drawingFactory;
    private final LocalFacade localFacade;
    private final CidadeFacade cityFacade;
    private final ArtefatoFacade itemFacade;
    private final PersonagemFacade persFacade;
    private final JogadorFacade playerFacade;
    private final ExercitoFacade armyFacade;
    private final int hexSize = 60;
    private int coordinateFontSize;
    private double zoomFactor;
    private boolean renderGrid;
    private boolean renderCoordinates;
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
    private boolean renderArmy;
    private boolean renderFeatures;
    private boolean armyIconDrawType;
    private boolean renderFogOfWar;
    private Point farPoint;
    private int xHexes;
    private int yHexes;
    private Jogador observer;
    final int[][] armySpacing4 = {{7, 36}, {17, 42}, {34, 42}, {46, 36}};
    final int[][] armySpacing6 = {{6, 36}, {12, 39}, {18, 42}, {36, 42}, {40, 39}, {46, 36}};
    final int[][] armySpacing8 = {{6, 36}, {12, 39}, {18, 42}, {24, 45}, {30, 45}, {36, 42}, {40, 39}, {46, 36}};

    public MapManager() {
        this.itemFacade = new ArtefatoFacade();
        this.cityFacade = new CidadeFacade();
        this.localFacade = new LocalFacade();
        this.persFacade = new PersonagemFacade();
        this.playerFacade = new JogadorFacade();
        this.armyFacade = new ExercitoFacade();
        this.drawingFactory = new DrawingFactory();
        this.imageFactory = new ImageFactory();
        //TODO wishlist: optimize image rendering for animations. static vs dynamic. i.e. terrain and roads, vs armies and chars and events
    }

    public Canvas getCanvas() {
        setRenderingFlags();
        //set observer
        observer = WorldFacadeCounselor.getInstance().getJogadorAtivo();
        //profiling
        long timeStart = (new Date()).getTime();
        log.info("Start map " + timeStart);
        //load hexes
        ListFactory listFactory = new ListFactory();
        Collection<Local> listaLocal = listFactory.listLocais().values();
        if (farPoint == null) {
            //calculate max size for map
            getMapMaxSize(listaLocal);
        }

        //prep canvas
        Canvas canvas = new Canvas(xHexes * hexSize * zoomFactor, (yHexes * hexSize * 3 / 4 + SCROLLBAR_SIZE) * zoomFactor);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.scale(zoomFactor, zoomFactor);
        gc.setFill(Color.GAINSBORO);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //actual rendering
        doRenderTerrain(gc, listaLocal);
        doRenderHexagonGrid(gc, listaLocal);
        doRenderCoordinateLabels(gc, listaLocal);

        //profiling
        long timeFinish = (new Date()).getTime();
        log.info("finish map " + (timeFinish - timeStart));
        return canvas;
    }

    private void doRenderHexagonGrid(GraphicsContext gc, Collection<Local> listLocal) {
        if (!renderGrid && !renderFogOfWar) {
            return;
        }

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
            //for of war
            if (renderFogOfWar && !local.isVisible()) {
                //gc.setFill(Color.rgb(188, 143, 143, 0.5));
                //gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                doRenderFogOfWar(gc, point);
            }
            //draw grid
            if (renderGrid) {
                gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
            }
        }
    }

    private void doRenderCoordinateLabels(GraphicsContext gc, Collection<Local> listLocal) {
        //TODO NEXT: refactor map coordinates label to be a Text over canvas, out of GraphicsContext so that we can use CSS
        if (!renderCoordinates) {
            return;
        }

        //prepping styles
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(1.0);
        gc.setFont(new Font(coordinateFontSize));
        gc.setStroke(Color.RED);
        gc.setFill(Color.NAVY);
        for (Local local : listLocal) {
            //calculate position
            Point2D point = getPositionCanvas(local);
            // write coordinates
            gc.fillText(
                    local.getCoordenadas(),
                    point.getX() + hexSize / 2, point.getY() + coordinateFontSize
            );
        }
    }

    private void doRenderTerrain(GraphicsContext gc, Collection<Local> listaLocal) {
        //main loop
        for (Local local : listaLocal) {
            Point2D point = getPositionCanvas(local);
            //draw terrain
            gc.drawImage(imageFactory.getTerrainImage(local), point.getX(), point.getY());
            //roads, rivers, bridges, span, creek, landing, army tracks
            doRenderDecoration(local, gc, point);
            doRenderCity(local, gc, point);
            doRenderLandmark(local, gc, point);
            doRenderFeatures(local, gc, point);
            doRenderArmy(local, gc, point);
        }
    }

    private void doRenderCity(Local local, GraphicsContext gc, Point2D point) {
        //cities
        if (!renderCities || !localFacade.isCidade(local)) {
            return;
        }
        Cidade city = localFacade.getCidade(local);
        //render fortification
        if (renderForts && cityFacade.isFortificacao(city)) {
            final Image img = ImageFactory.getFortificationImage(cityFacade.getFortificacao(city));
            gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
        }

        if (cityFacade.getTamanho(city) > 0 && !cityFacade.isOculto(city)) {
            doDrawCity(city, gc, point);
        } else if (cityFacade.getTamanho(city) > 0 && cityFacade.isOculto(city)) {
            //hidden city
            final Image img = ImageFactory.getHiddenCityImage(cityFacade.getTamanho(city));
            gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
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

    private void doDrawCity(Cidade city, GraphicsContext gc, Point2D point) {
        //TODO: border color by alliance (RED vs BLUE), or Mine vs enemy (BLUE vs RED fill+border)
        final int citySize = cityFacade.getTamanho(city);
        //drawingFactory.renderCity(gc, point, city.getTamanho(), cityFacade.getNacaoColorFill(city), cityFacade.getNacaoColorBorder(city));
        //regular visible city
        final Image img = ImageFactory.getCityImagePainted(citySize, Color.rgb(252, 254, 4), cityFacade.getNacaoColorFillFx(city), Color.rgb(4, 2, 4), cityFacade.getNacaoColorBorderFx(city));
        gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
    }

    private void doRenderArmy(Local local, GraphicsContext gc, Point2D point) {
        if (!renderArmy || localFacade.getExercitos(local).isEmpty()) {
            return;
        }
        //decide which type of list to use
        Collection<Nacao> armyList;
        if (localFacade.getExercitos(local).size() < 9 && armyIconDrawType) {
            armyList = listNationArmies();
        } else {
            armyList = listNationArmiesDeDup();
        }
        //list nations 
        for (Exercito exercito : localFacade.getExercitos(local).values()) {
            armyList.add(armyFacade.getNacao(exercito));
        }
        //Decide spacing
        int[][] armySpacing = doArmySpacing(armyList.size());
        //render icons
        int nn = 0;
        for (Nacao nation : armyList) {
            final Image img = imageFactory.getArmyShield(nation, WorldFacadeCounselor.getInstance().getCenario());
            gc.drawImage(img, point.getX() + armySpacing[nn][0], point.getY() + armySpacing[nn][1]);
            nn++;
        }
    }

    private Collection<Nacao> listNationArmies() {
        final List<Nacao> armyList = new ArrayList<>();
        return armyList;
    }

    private Collection<Nacao> listNationArmiesDeDup() {
        final Set<Nacao> armyList = new HashSet<>();
        //monta a lista das nacoes com exercitos presentes no local
        return armyList;
    }

    private int[][] doArmySpacing(int size) {
        int[][] armySpacing;
        if (size < 5) {
            armySpacing = armySpacing4;
        } else if (size < 7) {
            armySpacing = armySpacing6;
        } else {
            armySpacing = armySpacing8;
        }
        return armySpacing;
    }

    private void doRenderLandmark(Local local, GraphicsContext gc, Point2D point) {
        if (!renderLandmark || !local.isVisible() || !localFacade.isTerrainLandmark(local)) {
            return;
        }
        //terrain features
        for (Habilidade landmark : localFacade.getTerrainLandmark(local)) {
            //imprime gold mine
            final Image img = imageFactory.getLandmark(landmark.getCodigo());
            gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + (hexSize - img.getHeight()) / 2);
        }
    }

    private void doRenderFeatures(Local local, GraphicsContext gc, Point2D point) {
        //TODO wishlist: Make animation!
        if (!renderFeatures) {
            return;
        }
        //render ships
        if (localFacade.isBarcos(local)) {
            gc.drawImage(ImageFactory.getShipsImage(), point.getX() + 34, point.getY() + 30);
        }
        //renders combat icon
        if (local.isVisible() && localFacade.isCombatTookPlace(local)) {
            //what type of combat?
            if (localFacade.isCombatTookPlaceBigNavy(local)) {
                gc.drawImage(ImageFactory.getCombatBigNavyImage(), point.getX() + 46, point.getY() + 30);
            } else if (localFacade.isCombatTookPlaceBigArmy(local)) {
                gc.drawImage(ImageFactory.getCombatBigArmyImage(), point.getX() + 46, point.getY() + 30);
            } else {
                //none of the above
                gc.drawImage(ImageFactory.getCombatImage(), point.getX() + 46, point.getY() + 30);
            }
        }
        //render overrun
        if (local.isVisible() && localFacade.isOverrunTookPlace(local)) {
            gc.drawImage(ImageFactory.getExplosionImage(), point.getX() + 40, point.getY() + 36);
        }
        //render items
        for (Artefato item : localFacade.getArtefatos(local).values()) {
            if (itemFacade.isPosse(item)) {
                gc.drawImage(ImageFactory.getItemKnownImage(), point.getX() + 11, point.getY() + 22);
            } else {
                gc.drawImage(ImageFactory.getItemLostImage(), point.getX() + 46, point.getY() + 22);
            }
        }
        //render chars
        for (Personagem pers : localFacade.getPersonagens(local).values()) {
            if (persFacade.isNpc(pers)) {
                gc.drawImage(ImageFactory.getCharNpcImage(), point.getX() + 12, point.getY() + 19);
            } else if (playerFacade.isMine(pers, observer)) {
                gc.drawImage(ImageFactory.getCharImage(), point.getX() + 04, point.getY() + 22);
            } else if (playerFacade.isAlly(pers, observer)) {
                gc.drawImage(ImageFactory.getCharAllyImage(), point.getX() + 7, point.getY() + 26);
            } else {
                gc.drawImage(ImageFactory.getCharOtherImage(), point.getX() + 12, point.getY() + 23);
            }
        }
    }

    private void doRenderFogOfWar(GraphicsContext gc, Point2D point) {
        //render for of war
        //gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        //gc.setGlobalBlendMode(BlendMode.SOFT_LIGHT);
        gc.setGlobalAlpha(0.5d);
        final Image img = ImageFactory.getTerrainUnknownImage();
        gc.drawImage(img, point.getX() + (hexSize - img.getWidth()) / 2, point.getY() + (hexSize - img.getHeight()) / 2);
    }

    private void doRenderDecoration(Local local, GraphicsContext gc, Point2D point) {
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

    private Point2D getPositionCanvas(Local local) {
        //TODO wishlist: convert to static for improved performance
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
        renderArmy = true;
        renderFeatures = true;
        //TODO wishlist: initializing here will not work once we move to animation
        renderGrid = SettingsManager.getInstance().isConfig("MapGridRender", "1", "0");
        renderCoordinates = SettingsManager.getInstance().isConfig("MapCoordinateRender", "1", "0");
        renderFogOfWar = !SettingsManager.getInstance().isWorldBuilder() && SettingsManager.getInstance().isConfig("FogOfWarType", "1", "1");
        zoomFactor = (double) SettingsManager.getInstance().getConfigAsInt("MapZoomPercent", "100") / 100;
        coordinateFontSize = (int) Math.round(SettingsManager.getInstance().getConfigAsInt("MapCoordinatesSize", "8") / zoomFactor);
        armyIconDrawType = SettingsManager.getInstance().isConfig("DrawAllArmyIcons", "1", "1");
    }
}
