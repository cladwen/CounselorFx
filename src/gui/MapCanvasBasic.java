package gui;

import business.MapManager;
import control.CounselorStateMachine;
import control.WorldLoader;
import helpers.SpriteMegaMan;
import java.io.File;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import persistenceCommons.SettingsManager;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasBasic {

    private final MapManager mapManager;
    private final FileChooser fileChooser = new FileChooser();

    public MapCanvasBasic() {
        this.mapManager = new MapManager();
    }

    public Scene getScene(Stage primaryStage) {
        final Scene scene;
        //build main GUI
        if (CounselorStateMachine.getInstance().getCurrentState().isWorldLoaded()) {
            //world is loaded
            scene = new Scene(this.getMainPanel(), 1000, 800);
            primaryStage.setTitle(String.format("%s - Counselor FX", SettingsManager.getInstance().getWorldFilename()));
        } else {
            //there's no world. Ask for file
            scene = new Scene(this.getOpenButton(primaryStage), 1000, 800);
            primaryStage.setTitle("Counselor FX");
        }
        //go scene
        scene.getStylesheets().add("resources/style.css");
        return scene;
    }

    private Canvas getCanvas() {
        return this.mapManager.getCanvas();
    }

    private ScrollPane getScrollPane() {
        final Canvas mapCanvas = this.getCanvas();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mapCanvas);
        scrollPane.setPrefSize(mapCanvas.getWidth(), mapCanvas.getHeight());
        scrollPane.setMaxSize(mapCanvas.getWidth() + 15, mapCanvas.getHeight() + 15);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.pannableProperty().set(true);
//        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent");
        return scrollPane;
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

    private VBox getOpenButton(Stage primaryStage) {
        fileChooser.setInitialDirectory(new File("."));
        //        fileChooser.setInitialFileName("myfile.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Clash Files", "*.egf")
        );
        Button button = new Button("Select File");
        button.setOnAction(e -> {
            loadlWorldStage(primaryStage);
        });
        VBox vBox = new VBox(button);
        return vBox;
    }

    private void loadlWorldStage(Stage primaryStage) {
        //TODO NEXT: Add animation to switch scene
        //select file
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        //load world
        WorldLoader wl = new WorldLoader();
        wl.doLoadWorld(selectedFile);
        //change scene
        final Scene scene = primaryStage.getScene();
        final Scene nextScene = getScene(primaryStage);
        Parent root = nextScene.getRoot();
//Create a timeline instance
        Timeline timeline = new Timeline();
//Create a keyValue. We need to slide in -- We gradually decrement Y value to Zero
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
//Create keyframe of 1s with keyvalue kv
        KeyFrame kf = new KeyFrame(Duration.seconds(15), kv);
//Add frame to timeline
        timeline.getKeyFrames().add(kf);

//Start animation
        timeline.play();
        primaryStage.setScene(nextScene);
    }
}
