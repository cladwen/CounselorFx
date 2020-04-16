/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import gui.MapCanvasBasic;
import control.WorldLoader;
import gui.MapCanvasAnimated;
import helpers.SpriteMegaMan;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.SettingsManager;
import persistenceCommons.SysApoio;

/**
 * Base panel to launch the new Counselor
 *
 * Move it to GitHub, make it public?
 *
 * No Swing in PbmCommons?
 *
 *
 *
 * @author John
 */
public class CounselorFx extends Application {

    private static final Log log = LogFactory.getLog(CounselorFx.class);

    private ScrollPane getScrollPane() {
        final MapCanvasBasic mc = new MapCanvasBasic();
        final Canvas mapCanvas = mc.getCanvas();
        //TODO: refactor GUI stuff to GUI class
        //TODO: SVG graphs?

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mapCanvas);
        scrollPane.setPrefSize(mapCanvas.getWidth(), mapCanvas.getHeight());
        scrollPane.setMaxSize(mapCanvas.getWidth(), mapCanvas.getHeight());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.pannableProperty().set(true);
//        scrollPane.setStyle("-fx-focus-color: transparent;");
        scrollPane.setStyle("-fx-background-color: transparent");

//        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private BorderPane getMainPanel() {

        //create main panel
        BorderPane bPane = new BorderPane();
        bPane.setTop(getMenuTop());
        bPane.setLeft(new Label("Game information go here"));
        bPane.setCenter(getScrollPane());
        bPane.setRight(getSpaceSunEarth());
        bPane.setBottom(setMegaman());

        return bPane;
    }

    private static VBox getSpaceSunEarth() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setSpacing(50);
        MapCanvasAnimated mca = new MapCanvasAnimated();
        vbox.getChildren().addAll(new Label("Game Action/Orders go here"), mca.getCanvas());
        return vbox;
    }

    private SpriteMegaMan setMegaman() {
        // loads a sprite sheet, and specifies the size of one frame/cell
        SpriteMegaMan megaMan = new SpriteMegaMan("resources/megaman.png", 50, 49); //searches for the image file in the classpath
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

    private MenuBar getMenuTop() {
        //create menu
        MenuBar menubar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem filemenu1 = new MenuItem("New");
        MenuItem filemenu2 = new MenuItem("Save");
        MenuItem filemenu3 = new MenuItem("Exit");
        fileMenu.getItems().addAll(filemenu1, filemenu2, filemenu3);
        Menu editMenu = new Menu("Edit");
        MenuItem EditMenu1 = new MenuItem("Cut");
        MenuItem EditMenu2 = new MenuItem("Copy");
        MenuItem EditMenu3 = new MenuItem("Paste");
        editMenu.getItems().addAll(EditMenu1, EditMenu2, EditMenu3);
        Menu toolBar = new Menu("ToolBar Here");
        menubar.getMenus().addAll(fileMenu, editMenu, toolBar);
        return menubar;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bPane = getMainPanel();

        //go scene
        Scene scene = new Scene(bPane, 1000, 800);
        scene.getStylesheets().add("resources/style.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void setBasicConfig() {
        log.info("Starting...");
        //TODO: Upgrade versions when building
        log.info("Counselor version: " + SysApoio.getVersionClash("version_counselor"));
        log.info("Commons version: " + SysApoio.getVersionClash("version_commons"));
        final SettingsManager sm = SettingsManager.getInstance();
        sm.setConfigurationMode("Client");
        sm.setLanguage(sm.getConfig("language", "en"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // set basic configurations for Counselor
        setBasicConfig();
        //load game file
        WorldLoader wl = new WorldLoader();
        wl.doAutoLoad(args);

        //TODO: sync interface (status bar)
        //TODO: open map
        //TODO: Progress bar on loading
        //launch GUI
        launch(args);
    }
}
