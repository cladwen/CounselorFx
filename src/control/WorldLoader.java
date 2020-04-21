/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import business.BussinessException;
import counselorfx.CounselorFx;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

/**
 *
 * @author jmoura
 */
public class WorldLoader {

    private static final Log log = LogFactory.getLog(CounselorFx.class);
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();

    /**
     * Checks if there's an autoload file defined or passed as arg at launch.Then load it
     *
     * Start WorldFacadeCounselor first instance
     *
     * @param args
     */
    public void doAutoLoad(String[] args) {
        final String autoLoad;
        if (args.length == 1) {
            //file name passed as parameter from BAT or OS
            autoLoad = args[0];
        } else {
            //load from saved settings
            autoLoad = SettingsManager.getInstance().getConfig("autoLoad");
        }
        if (autoLoad == null || autoLoad.isEmpty()) {
            //no file to load
            //TODO NEXT: file selection
            log.info(labels.getString("AUTOLOADING.OPENING") + "none");
            CounselorStateMachine.getInstance().setCurrentStateNoWorld();
            return;
        }
        log.info(labels.getString("AUTOLOADING.OPENING") + autoLoad);
        final File resultsFile = new File(autoLoad);
        doLoadWorld(resultsFile);
    }

    public void doLoadWorld(final File resultsFile) {
        try {
            WorldFacadeCounselor.getInstance().doStart(resultsFile);
            CounselorStateMachine.getInstance().setCurrentStateOrderEntry();
            SettingsManager.getInstance().setWorldFilename(resultsFile.getName());
        } catch (BussinessException ex) {
            //TODO: what if file not found?
            CounselorStateMachine.getInstance().setCurrentStateNoWorld();
            log.error(ex);
        }
    }
}
