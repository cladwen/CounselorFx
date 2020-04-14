/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import fxsprite.SpriteMegaMan;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    private Canvas getMap() {
        MapCanvas mc = new MapCanvas();
        //TODO: render image
        //TODO: render 2nd image, merged
        //TODO: SVG graphs?
        return mc.getCanvas();
    }

    private BorderPane getMainPanel() {
        //create main panel
        BorderPane bPane = new BorderPane();
        bPane.setTop(getMenuTop());
        bPane.setLeft(new Label("Game information go here"));
        bPane.setRight(new Label("Game Action/Orders go here"));
        // loads a sprite sheet, and specifies the size of one frame/cell
        SpriteMegaMan megaMan = new SpriteMegaMan("fxsprite/megaman.png", 50, 49); //searches for the image file in the classpath
        megaMan.setFPS(5); // animation will play at 5 frames per second
        megaMan.play(); // animates the first row of the sprite sheet
        megaMan.pause();
        megaMan.label(4, "powerup"); // associates the fourth (zero-indexed) row of the sheet with "powerup"
        megaMan.playTimes("powerup", 10); // plays "powerup" animation 10 times;
        megaMan.setX(100);
        megaMan.setY(200);
        bPane.setBottom(megaMan);

        bPane.setCenter(getMap());
        return bPane;
    }

    private MenuBar getMenuTop() {
        //create menu
        MenuBar menubar = new MenuBar();
        Menu FileMenu = new Menu("File");
        MenuItem filemenu1 = new MenuItem("New");
        MenuItem filemenu2 = new MenuItem("Save");
        MenuItem filemenu3 = new MenuItem("Exit");
        FileMenu.getItems().addAll(filemenu1, filemenu2, filemenu3);
        Menu EditMenu = new Menu("Edit");
        MenuItem EditMenu1 = new MenuItem("Cut");
        MenuItem EditMenu2 = new MenuItem("Copy");
        MenuItem EditMenu3 = new MenuItem("Paste");
        EditMenu.getItems().addAll(EditMenu1, EditMenu2, EditMenu3);
        menubar.getMenus().addAll(FileMenu, EditMenu);
        return menubar;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bPane = getMainPanel();

        //go scene
        Scene scene = new Scene(bPane, 1000, 800);
        scene.getStylesheets().add("counselorfx/style.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
