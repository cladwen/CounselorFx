package control;

import business.MapManager;
import gui.CounselorFx;
import gui.MapCanvasAnimated;
import helpers.SpriteMegaMan;
import java.io.File;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

// Animation of Earth rotating around the sun. (Hello, world!)
public class MapCanvasBasic {

    private static final Log log = LogFactory.getLog(CounselorFx.class);
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();
    private final MapManager mapManager;
    private final Stage mainStage;
    private final FileChooser fileChooser = new FileChooser();
    private final ConfigControl configControl;
    private BorderPane bPane;

    public MapCanvasBasic(Stage primaryStage) {
        mainStage = primaryStage;
        mapManager = new MapManager();
        configControl = new ConfigControl(primaryStage);
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

    public void setSceneStyle(final Scene scene) {
        configControl.updateStyle(scene);
    }

    public String getWindowTitle() {
        if (CounselorStateMachine.getInstance().getCurrentState().isWorldLoaded()) {
            return String.format("%s - Counselor FX", CounselorStateMachine.getInstance().getWorldFilename());
        } else {
            return "Counselor FX";
        }
    }

    private ScrollPane getMapPane() {
        final StackPane mapCanvas = this.mapManager.getCanvas();

        //TODO wishlist: remove the gray square from the scroll bar CSS.
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mapCanvas);
//        scrollPane.setPrefSize(mapCanvas.getWidth(), mapCanvas.getHeight());
//        scrollPane.setMaxSize(mapCanvas.getWidth() + 15, mapCanvas.getHeight() + 15);
//        scrollPane.setPrefSize(10000, 10000);
//        scrollPane.setMaxSize(10000 + 15,10000 + 15);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.pannableProperty().set(true);
//        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent");
        return scrollPane;
    }

    private VBox getSideBarGameInfo() {
        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setSpacing(5);
        vbox.getChildren().addAll(this.mapManager.getHexCoordinate(), this.mapManager.getHexInfo());
        return vbox;
    }

    private Pane getSideBar() {
        final SpriteMegaMan megaman = this.getMegaman();
        megaman.setY(500);
        megaman.setX(50);
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.setSpacing(50);
        MapCanvasAnimated mca = new MapCanvasAnimated();
        vbox.getChildren().addAll(new Label("Game information go here"), mca.getCanvas(), megaman);
        return vbox;
    }

    public SpriteMegaMan getMegaman() {
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
        //MenuItem filemenu1 = new MenuItem("New");
        //MenuItem filemenu2 = new MenuItem("Save");
        //MenuItem filemenu3 = new MenuItem("Exit");
        //fileMenu.getItems().addAll(filemenu1, filemenu2, filemenu3);
        Menu editMenu = new Menu("Edit");
        //MenuItem EditMenu1 = new MenuItem("Cut");
        //MenuItem EditMenu2 = new MenuItem("Copy");
        //MenuItem EditMenu3 = new MenuItem("Paste");
        //editMenu.getItems().addAll(EditMenu1, EditMenu2, EditMenu3);
        Menu toolBar = new Menu("ToolBar Here");
        menubar.getMenus().addAll(fileMenu, editMenu, toolBar);
        return menubar;
    }

    private BorderPane getMainPanel() {
        //create main panel
        bPane = new BorderPane();
        bPane.setTop(getMenuTop());
        bPane.setLeft(getSideBar());
        bPane.setCenter(getMapPane());
        final VBox hexInfoBar = getSideBarGameInfo();
        //hexInfoBar.setMaxWidth(200);
        bPane.setRight(hexInfoBar);
        bPane.setBottom(configControl.getConfigBar());
        return bPane;
    }

    private StackPane getOpenButton(StackPane root) {
        fileChooser.setInitialDirectory(new File(SettingsManager.getInstance().getConfig("LastFolder", ".")));
        //        fileChooser.setInitialFileName("myfile.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(labels.getString("FILTRO.RESULTADO"), "*.rr.egf")
        );
        Button button = new Button(labels.getString("ABRIR.TURNO"));
        button.setTooltip(new Tooltip(labels.getString("ABRIR.TURNO.TOOLTIP")));
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
        //save last folder
        SettingsManager.getInstance().setConfigAndSaveToFile("LastFolder", resultsFile.getParent());
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
