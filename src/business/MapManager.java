/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import helpers.SpriteDecorationIcon;
import business.converter.ConverterFactory;
import business.facade.ArtefatoFacade;
import business.facade.CidadeFacade;
import business.facade.ExercitoFacade;
import business.facade.JogadorFacade;
import business.facade.LocalFacade;
import business.facade.NacaoFacade;
import business.facade.PersonagemFacade;
import control.CounselorStateMachine;
import control.WorldFacadeCounselor;
import gui.animation.AnimatedImage;
import gui.factory.DrawingFactory;
import helpers.Hexagon;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
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
public final class MapManager {

    private static final Log log = LogFactory.getLog(MapManager.class);
    private static final int SCROLLBAR_SIZE = 15;
    private static final Color CITY_SPRITE_BORDER = Color.rgb(4, 2, 4);
    private static final Color CITY_SPRIT_FILL = Color.rgb(252, 254, 4);
    private final ImageFactory imageFactory;
    private final DrawingFactory drawingFactory;
    private final LocalFacade localFacade;
    private final NacaoFacade nationFacade;
    private final CidadeFacade cityFacade;
    private final ArtefatoFacade itemFacade;
    private final PersonagemFacade persFacade;
    private final JogadorFacade playerFacade;
    private final ExercitoFacade armyFacade;
    private static final int HEX_SIZE = 60;
    private int coordinateFontSize;
    private double zoomFactorCurrent;
    private double zoomFactorUndo = 1d;
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
    private boolean renderShips;
    private boolean renderCombats;
    private boolean renderOverrun;
    private boolean renderItems;
    private boolean renderCharacters;
    private String renderTerrainTile;
    private String cityColorType;
    private Point farPoint;
    private int xHexes;
    private int yHexes;
    private Jogador observer;
    private final List<Local> listAnimation = new ArrayList<>();
    private Collection<Local> localList;
    private final Pane aniPane;
    private final int[][] armySpacing4 = {{7, 36}, {17, 42}, {34, 42}, {46, 36}};
    private final int[][] armySpacing6 = {{6, 36}, {12, 39}, {18, 42}, {36, 42}, {40, 39}, {46, 36}};
    private final int[][] armySpacing8 = {{6, 36}, {12, 39}, {18, 42}, {24, 45}, {30, 45}, {36, 42}, {40, 39}, {46, 36}};
    private static final int DECORATION_ARMY_Y = 30;
    private static final int DECORATION_ARMY_X = 46;

    public MapManager() {
        this.itemFacade = new ArtefatoFacade();
        this.cityFacade = new CidadeFacade();
        this.nationFacade = new NacaoFacade();
        this.localFacade = new LocalFacade();
        this.persFacade = new PersonagemFacade();
        this.playerFacade = new JogadorFacade();
        this.armyFacade = new ExercitoFacade();
        this.drawingFactory = new DrawingFactory();
        this.imageFactory = new ImageFactory();
        aniPane = new Pane();
    }

    private AnimatedImage loadUfo() {
        AnimatedImage ufo = new AnimatedImage();
        Image[] imageArray = new Image[6];
        for (int i = 0; i < 6; i++) {
            imageArray[i] = new Image("resources/ufo_" + i + ".png");
        }
        ufo.frames = imageArray;
        ufo.duration = 0.100;
        return ufo;
    }

    public StackPane getCanvas() {
        //load map info
        //set observer
        observer = WorldFacadeCounselor.getInstance().getJogadorAtivo();
        setRenderingFlags();
        //load hexes
        ListFactory listFactory = new ListFactory();
        localList = listFactory.listLocais().values();
        if (farPoint == null) {
            //calculate max size for map
            getMapInfo(localList);
        }

        //prep canvas
        ResizableCanvas layer1 = new ResizableCanvas();
        layer1.setWidth(xHexes * HEX_SIZE * zoomFactorCurrent);
        layer1.setHeight((yHexes * HEX_SIZE * 3 / 4 + SCROLLBAR_SIZE) * zoomFactorCurrent);
        GraphicsContext gc = layer1.getGraphicsContext2D();
        gc.scale(zoomFactorCurrent, zoomFactorCurrent);

        //prep animation
        final long startNanoTime = System.nanoTime();
        AnimatedImage ufo = loadUfo();

        //animation timer
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                //short circuit loop if there's no changes to map
                if (!CounselorStateMachine.getInstance().isMapChanged()) {
                    //animation
//                    double time = (currentNanoTime - startNanoTime) / 1000000000.0;
//                    double x = 232 + 128 * Math.cos(time);
//                    double y = 232 + 128 * Math.sin(time);
//                    gc.drawImage(ufo.getFrame(time), 196 + x / 10, 196 + y / 10);
                    //FIXME: combat icon here leaves an artifact on screen. why?
                    return;
                }
                //profiling
                long timeStart = (new Date()).getTime();
                //log.info("Start map " + timeStart);

                setRenderingFlags();

                //gc.setFill(Color.GAINSBORO);
                //gc.fillRect(0, 0, layer1.getWidth(), layer1.getHeight());
                //actual rendering
                doRenderTerrain(gc, localList);
                doRenderFeatures(gc);
                doRenderHexagonGrid(gc, localList);
                doRenderCoordinateLabels(gc, localList);
                //profiling
                long timeFinish = (new Date()).getTime();
                log.info("finish map " + (timeFinish - timeStart));
                CounselorStateMachine.getInstance().setMapChanged(false);
            }
        }.start();
        StackPane pane = new StackPane();
        pane.getChildren().add(layer1);
        pane.getChildren().add(aniPane);
        aniPane.toFront();
        return pane;
    }

    private Canvas getCanvasOld() {

        //prep canvas
        ResizableCanvas canvas = new ResizableCanvas();
        canvas.setWidth(xHexes * HEX_SIZE * zoomFactorCurrent);
        canvas.setHeight((yHexes * HEX_SIZE * 3 / 4 + SCROLLBAR_SIZE) * zoomFactorCurrent);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //gc.scale(zoomFactorUndo, zoomFactorUndo);
        gc.scale(zoomFactorCurrent, zoomFactorCurrent);

        //prep animation
        final long startNanoTime = System.nanoTime();
        AnimatedImage ufo = loadUfo();

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                //short circuit loop if there's no changes to map
                if (!CounselorStateMachine.getInstance().isMapChanged()) {
                    //animation
//                    double time = (currentNanoTime - startNanoTime) / 1000000000.0;
//                    double x = 232 + 128 * Math.cos(time);
//                    double y = 232 + 128 * Math.sin(time);
//                    gc.drawImage(ufo.getFrame(time), 196 + x / 10, 196 + y / 10);
                    //FIXME: combat icon here leaves an artifact on screen. why?
                    return;
                }
                //profiling
                long timeStart = (new Date()).getTime();
                //log.info("Start map " + timeStart);

                setRenderingFlags();

                gc.setFill(Color.GAINSBORO);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                //actual rendering
                doRenderTerrain(gc, localList);
                doRenderFeatures(gc);
                doRenderHexagonGrid(gc, localList);
                doRenderCoordinateLabels(gc, localList);
                //profiling
                long timeFinish = (new Date()).getTime();
                log.info("finish map " + (timeFinish - timeStart));
                CounselorStateMachine.getInstance().setMapChanged(false);
            }
        }.start();

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
            Hexagon hex = new Hexagon(point.getX(), point.getY(), HEX_SIZE / 2);
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

    private ImageView getSprite() {
        // loads a sprite sheet, and specifies the size of one frame/cell
        SpriteDecorationIcon megaMan = new SpriteDecorationIcon("resources/megaman.png", 50, 49, this.zoomFactorCurrent); //searches for the image file in the classpath
        megaMan.setFPS(5); // animation will play at 5 frames per second
        //megaMan.pause();
        megaMan.label(4, "powerup"); // associates the fourth (zero-indexed) row of the sheet with "powerup"
        //megaMan.playTimes("powerup", 10); // plays "powerup" animation 10 times;
        //megaMan.limitRowColumns(2, 9);
        //megaMan.play(); // animates the first row of the sprite sheet
        megaMan.play("powerup");
        //megaMan.setX(100);
        //megaMan.setY(200);
        return megaMan;
    }

    private void doRenderCoordinateLabels(GraphicsContext gc, Collection<Local> listLocal) {
        //TODO wishlist: refactor map coordinates label to be a Text over canvas, out of GraphicsContext so that we can use CSS
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
                    point.getX() + HEX_SIZE / 2, point.getY() + coordinateFontSize
            );
        }
    }

    private void doRenderTerrain(GraphicsContext gc, Collection<Local> listaLocal) {
        //main loop
        for (Local local : listaLocal) {
            Point2D point = getPositionCanvas(local);
            final Image terrainImage = ImageFactory.getTerrainImage(ConverterFactory.terrainToIndex(localFacade.getTerrenoCodigo(local)), renderTerrainTile);
            //draw terrain
            gc.drawImage(terrainImage, point.getX(), point.getY());
            //roads, rivers, bridges, span, creek, landing, army tracks
            doRenderDecoration(local, gc, point);
            doRenderCity(local, gc, point);
            doRenderLandmark(local, gc, point);
            doRenderArmy(local, gc, point);
            //doRenderFeatures(local, gc, point);
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
            gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
        }

        if (cityFacade.getTamanho(city) > 0 && !cityFacade.isOculto(city)) {
            doDrawCity(city, gc, point);
        } else if (cityFacade.getTamanho(city) > 0 && cityFacade.isOculto(city)) {
            //hidden city
            final Image img = ImageFactory.getHiddenCityImage(cityFacade.getTamanho(city));
            gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
        }

        //draw docks
        final Image docksImg = ImageFactory.getDockImage(cityFacade.getDocas(city));
        gc.drawImage(docksImg, point.getX() + (HEX_SIZE - docksImg.getWidth()) / 2, point.getY() + 2);

        //draw capital
        if (cityFacade.isCapital(city)) {
            final Image img = ImageFactory.getCapitalImage();
            gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + 30);
        }
    }

    private void doDrawCity(Cidade city, GraphicsContext gc, Point2D point) {
        final int citySize = cityFacade.getTamanho(city);
        //drawingFactory.renderCity(gc, point, city.getTamanho(), cityFacade.getNacaoColorFill(city), cityFacade.getNacaoColorBorder(city));
        //regular visible city
        Color cityBorderColor;
        Color cityFillColor;
        switch (cityColorType) {
            case "2":
                //border color by alliance (RED vs BLUE VS YELLOW vs GREEN)
            try {
                cityBorderColor = DrawingFactory.getColor(city.getNacao().getTeamFlag());
            } catch (NullPointerException e) {
                cityBorderColor = DrawingFactory.getColor("gray");
            }
            //use nation's fill color
            cityFillColor = cityFacade.getNacaoColorFillFx(city);
            break;

            case "3":
                //Team diplomacy: border + fill, enemy = RED vs ally = BLUE
                if (playerFacade.isAlly(city.getNacao(), observer)) {
                    //mine
                    cityBorderColor = Color.BLUE;
                    cityFillColor = Color.DEEPSKYBLUE;
                } else {
                    //enemy
                    cityBorderColor = Color.RED;
                    cityFillColor = Color.TOMATO;
                }
                break;
            case "4":
                //my diplomacy: border + fill, enemy = RED vs ally = BLUE
                if (playerFacade.isMine(city, observer)) {
                    //mine
                    cityBorderColor = Color.BLUE;
                    cityFillColor = Color.DEEPSKYBLUE;
                } else {
                    //enemy
                    cityBorderColor = Color.RED;
                    cityFillColor = Color.TOMATO;
                }
                break;
            default:
                //paint cities according to nation colors
                cityBorderColor = cityFacade.getNacaoColorBorderFx(city);
                cityFillColor = cityFacade.getNacaoColorFillFx(city);
                break;
        }
        final Image img = ImageFactory.getCityImagePainted(citySize,
                CITY_SPRIT_FILL, cityFillColor,
                CITY_SPRITE_BORDER, cityBorderColor);
        gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + 34 - img.getHeight());
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
            final Image img = ImageFactory.getArmyShield(nation, WorldFacadeCounselor.getInstance().getCenario());
            gc.drawImage(img, point.getX() + armySpacing[nn][0], point.getY() + armySpacing[nn][1]);
            nn++;
        }
        //render ships
        if (renderShips && localFacade.isBarcos(local)) {
            gc.drawImage(ImageFactory.getShipsImage(), point.getX() + 34, point.getY() + 30);
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
            gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + (HEX_SIZE - img.getHeight()) / 2);
        }
    }

    private void doRenderFeatures(GraphicsContext gc) {
        if (!renderFeatures) {
            return;
        }
        aniPane.getChildren().clear();
        for (Local local : listAnimation) {
            Point2D point = getPositionCanvas(local);
            doRenderFeatures(local, gc, point);
        }
    }

    private void doRenderFeatures(Local local, GraphicsContext gc, Point2D point) {
        //renders combat icon
        if (renderCombats && localFacade.isCombatTookPlace(local)) {
            //what type of combat?
            if (localFacade.isCombatTookPlaceBigNavy(local)) {
                //TODO NEXT1:  Do this one now
                gc.drawImage(ImageFactory.getCombatBigNavyImage(), point.getX() + DECORATION_ARMY_X, point.getY() + DECORATION_ARMY_Y);
            } else if (localFacade.isCombatTookPlaceBigArmy(local)) {
                //TODO NEXT1:  Do this one now. find an example
                gc.drawImage(ImageFactory.getCombatBigArmyImage(), point.getX() + DECORATION_ARMY_X, point.getY() + DECORATION_ARMY_Y);
            } else {
                //none of the above
                //gc.drawImage(ImageFactory.getCombatImage(), point.getX() + DECORATION_ARMY_X, point.getY() + DECORATION_ARMY_Y);
                //final ImageView megaman = getSprite();
                final ImageView combatSprite = new ImageView(ImageFactory.getCombatImage());
                combatSprite.setPreserveRatio(true);
                combatSprite.setFitHeight(12 * zoomFactorCurrent);

                combatSprite.setX((point.getX() + DECORATION_ARMY_X) * zoomFactorCurrent);
                combatSprite.setY((point.getY() + DECORATION_ARMY_Y) * zoomFactorCurrent);

                createFlipAnimation(combatSprite, combatSprite);
                aniPane.getChildren().add(combatSprite);
            }
        }
        //TODO: Make more animations!
        //render overrun
        if (renderOverrun && localFacade.isOverrunTookPlace(local)) {
            gc.drawImage(ImageFactory.getExplosionImage(), point.getX() + 40, point.getY() + 36);
        }
        //render items
        if (renderItems) {
            for (Artefato item : localFacade.getArtefatos(local).values()) {
                if (itemFacade.isPosse(item)) {
                    gc.drawImage(ImageFactory.getItemKnownImage(), point.getX() + 11, point.getY() + 22);
                } else {
                    gc.drawImage(ImageFactory.getItemLostImage(), point.getX() + 46, point.getY() + 22);
                }
            }
        }
        //render chars
        if (renderCharacters) {
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
    }

    private void createFlipAnimation(final ImageView imgFront, final ImageView imgBack) {
        ScaleTransition stHideFront = new ScaleTransition(Duration.millis(1500), imgFront);
        stHideFront.setFromX(1);
        stHideFront.setToX(0);

        ScaleTransition stShowBack = new ScaleTransition(Duration.millis(1500), imgBack);
        stShowBack.setFromX(0);
        stShowBack.setToX(1);

        //endless loop for animation 
        stHideFront.setOnFinished((ActionEvent t) -> {
            stShowBack.play();
        });
        stShowBack.setOnFinished((ActionEvent t) -> {
            stHideFront.play();
        });
        //play
        stHideFront.play();
    }

    private void doRenderFogOfWar(GraphicsContext gc, Point2D point) {
        //render for of war
        //gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        //gc.setGlobalBlendMode(BlendMode.SOFT_LIGHT);
        gc.setGlobalAlpha(0.5d);
        final Image img = ImageFactory.getTerrainUnknownImage();
        gc.drawImage(img, point.getX() + (HEX_SIZE - img.getWidth()) / 2, point.getY() + (HEX_SIZE - img.getHeight()) / 2);
        gc.setGlobalAlpha(1d);
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

    private static Point2D getPositionCanvas(Local local) {
        //TODO wishlist: convert to static for improved performance
        //calculate position on canvas
        double x = LocalFacade.getCol(local) - 1;
        double y = LocalFacade.getRow(local) - 1;
        Point2D ret;
        if (y % 2 == 0) {
            ret = new Point2D(x * HEX_SIZE, y * HEX_SIZE * 3 / 4);
        } else {
            ret = new Point2D(x * HEX_SIZE + HEX_SIZE / 2, y * HEX_SIZE * 3 / 4);
        }
        return ret;
    }

    private Point getMapInfo(Collection<Local> listaLocal) {
        int[] ret = {0, 0};
        int row, col;
        for (Local local : listaLocal) {
            row = LocalFacade.getRow(local);
            col = LocalFacade.getCol(local);
            if (row > ret[0]) {
                ret[0] = row;
            }
            if (col > ret[1]) {
                ret[1] = col;
            }
            if (localFacade.isCombatTookPlace(local)) {
                listAnimation.add(local);
            }
        }
        this.farPoint = new Point(ret[1] * HEX_SIZE + HEX_SIZE / 2, ret[0] * HEX_SIZE * 3 / 4 + HEX_SIZE);
        xHexes = ret[1];
        yHexes = ret[0];
        return this.farPoint;
    }

    private void setRenderingFlags() {
        //direction based
        renderRoads = SettingsManager.getInstance().isConfig("MapRenderRoads", "1", "1");
        renderRivers = SettingsManager.getInstance().isConfig("MapRenderRiver", "1", "1");
        renderCreek = SettingsManager.getInstance().isConfig("MapRenderCreek", "1", "1");
        renderBridge = SettingsManager.getInstance().isConfig("MapRenderBridge", "1", "1");
        renderSpan = SettingsManager.getInstance().isConfig("MapRenderSpan", "1", "1");
        renderTracks = SettingsManager.getInstance().isConfig("MapRenderArmyTrack", "1", "1");
        renderLanding = SettingsManager.getInstance().isConfig("MapRenderLanding", "1", "1");
        directionBased = renderRoads || renderRivers || renderCreek || renderBridge || renderSpan || renderTracks || renderLanding;
        //other decorations
        renderLandmark = SettingsManager.getInstance().isConfig("MapRenderLandmark", "1", "1");
        renderCities = SettingsManager.getInstance().isConfig("MapRenderCity", "1", "1");
        renderForts = SettingsManager.getInstance().isConfig("MapRenderFortification", "1", "1");
        renderArmy = SettingsManager.getInstance().isConfig("MapRenderArmy", "1", "1");
        renderShips = SettingsManager.getInstance().isConfig("MapRenderShips", "1", "1");

        renderCharacters = SettingsManager.getInstance().isConfig("MapRenderCharacters", "1", "1");
        renderItems = SettingsManager.getInstance().isConfig("MapRenderItems", "1", "1");
        renderCombats = SettingsManager.getInstance().isConfig("MapRenderCombats", "1", "1");
        renderOverrun = SettingsManager.getInstance().isConfig("MapRenderOverrun", "1", "1");
        renderFeatures = renderCharacters || renderItems || renderCombats || renderOverrun;
        renderGrid = SettingsManager.getInstance().isConfig("MapRenderGrid", "1", "0");
        renderCoordinates = SettingsManager.getInstance().isConfig("MapRenderCoordinate", "1", "1");
        renderFogOfWar = !SettingsManager.getInstance().isWorldBuilder() && SettingsManager.getInstance().isConfig("MapRenderFogOfWar", "1", "1");
        final double[] zoomOptions = {.25d, .50d, 1.00d, 1.50d, 2.00d, 2.50d};
        try {
            zoomFactorCurrent = zoomOptions[SettingsManager.getInstance().getConfigAsInt("MapZoomPercent", "1")];
        } catch (Exception e) {
            //defaults to 100% if trouble
            zoomFactorCurrent = zoomOptions[1];
        }
        if (zoomFactorUndo != 1d && zoomFactorCurrent > 0) {
            zoomFactorUndo = 1d / zoomFactorCurrent;
        }
        coordinateFontSize = (int) Math.round(SettingsManager.getInstance().getConfigAsInt("MapCoordinatesSize", "8") / zoomFactorCurrent);
        armyIconDrawType = SettingsManager.getInstance().isConfig("MapDrawAllArmyIcons", "1", "1");
        cityColorType = SettingsManager.getInstance().getConfig("MapCityColorType", "1");
        renderTerrainTile = SettingsManager.getInstance().getConfig("MapTerrainTile", "1");
    }

    class ResizableCanvas extends Canvas {

        public ResizableCanvas() {
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> draw());
            heightProperty().addListener(evt -> draw());
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();

            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, width, height);

            gc.setStroke(Color.RED);
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(0, height, width, 0);
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

}
