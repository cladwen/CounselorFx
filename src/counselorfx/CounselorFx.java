/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import gui.MapCanvasBasic;
import control.WorldLoader;
import javafx.application.Application;
import javafx.scene.Scene;
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        //build main GUI
        final MapCanvasBasic mc = new MapCanvasBasic();

        //go scene
        Scene scene = new Scene(mc.getMainPanel(), 1000, 800);
        scene.getStylesheets().add("resources/style.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void setBasicConfig() {
        log.info("Starting...");
        //FIXME: Upgrade versions when building
        log.info("Counselor version: " + SysApoio.getVersionClash("version_counselor"));
        log.info("Commons version: " + SysApoio.getVersionClash("version_commons"));
        final SettingsManager sm = SettingsManager.getInstance();
        sm.setConfigurationMode("Client");
        sm.setLanguage(sm.getConfig("language", "en"));
    }

    /**
     * Animation ideas: Map transition to show on load
     *
     * Fog of war transition to set on
     *
     * Progress bar on loading
     *
     * Dragons/NPCs on map
     *
     * Army/PC movement for new orders
     *
     * Event Icons: Combat, overrun, battle, item location, NPC encounter
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // set basic configurations for Counselor
        setBasicConfig();
        //load game file
        WorldLoader wl = new WorldLoader();
        wl.doAutoLoad(args);

        //TODO: sync interface (status bar)
        //launch GUI
        launch(args);
    }
}
