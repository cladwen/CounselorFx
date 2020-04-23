package gui;

import business.MapManager;
import control.CounselorStateMachine;
import control.WorldLoader;
import counselorfx.CounselorFx;
import helpers.SpriteMegaMan;
import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasBasic {

    private static final Log log = LogFactory.getLog(CounselorFx.class);
    private final MapManager mapManager;
    private final Stage mainStage;
    private final FileChooser fileChooser = new FileChooser();

    public MapCanvasBasic(Stage primaryStage) {
        this.mapManager = new MapManager();
        mainStage = primaryStage;
    }

    public Pane getSceneContent(StackPane root) {
        //build main GUI
        if (CounselorStateMachine.getInstance().getCurrentState().isWorldLoaded()) {
            //world is loaded
            return this.getMainPanel();
        } else {
            //there's no world. Ask for file
            return this.getOpenButton(root);
        }
    }

    public String getWindowTitle() {
        if (CounselorStateMachine.getInstance().getCurrentState().isWorldLoaded()) {
            return String.format("%s - Counselor FX", CounselorStateMachine.getInstance().getWorldFilename());
        } else {
            return "Counselor FX";
        }
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

    private StackPane getOpenButton(StackPane root) {
        fileChooser.setInitialDirectory(new File("."));
        //        fileChooser.setInitialFileName("myfile.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Clash Files", "*.egf")
        );
        Button button = new Button("Select File");
        button.setOnAction(e -> {
            loadlWorldStage(root);
        });
        return new StackPane(button);
    }

    private void loadlWorldStage(StackPane root) {
        //select file
        File resultsFile = fileChooser.showOpenDialog(mainStage);
        if (resultsFile == null) {
            //no file selected, no changes to UI
            return;
        }
        //load world
        WorldLoader wl = new WorldLoader();
        wl.doLoadWorld(resultsFile);
        //change scene
        if (!CounselorStateMachine.getInstance().getCurrentState().isWorldLoaded()) {
            //world is NOT loaded, keep asking to select file.
            return;
        }
        setSceneAnimation(root);
    }

    private void setSceneAnimation(StackPane root) {
        Node view1 = root.getChildren().get(0);
        final Pane view2 = this.getMainPanel();
        root.getChildren().add(view2);
        double width = root.getWidth();
        KeyFrame start = new KeyFrame(Duration.ZERO,
                new KeyValue(view2.translateXProperty(), width),
                new KeyValue(view1.translateXProperty(), 0));
        KeyFrame end = new KeyFrame(Duration.seconds(1),
                new KeyValue(view2.translateXProperty(), 0),
                new KeyValue(view1.translateXProperty(), -width));
        Timeline slide = new Timeline(start, end);
        slide.setOnFinished(e -> root.getChildren().remove(view1));
        slide.play();
    }
}
