/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counselorfx;

import business.BussinessException;
import control.facade.WorldFacadeCounselor;
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
    private static final WorldFacadeCounselor WFC = WorldFacadeCounselor.getInstance();

    /**
     * Checks if there's an autoload file defined or passed as arg at launch. Then load it
     *
     * @param autoLoad
     */
    public void doAutoLoad(String autoLoad) {
        if (autoLoad == null || autoLoad.isEmpty()) {
            return;
        }
        try {
            log.info(labels.getString("AUTOLOADING.OPENING") + autoLoad);
            final File resultsFile = new File(autoLoad);
            //TODO: autoload_actions go here
            //TODO: Progress bar on loading
            //TODO: sync interface (status bar)
            //TODO: open map
            WFC.doStart(resultsFile);
        } catch (BussinessException ex) {
            //TODO: what if file not found?
            log.error(ex);
        }
    }

}
