/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.persist;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.SettingsManager;

/**
 *
 * @author serguei
 */
public class PersistUIState {
    
    private static final Log log = LogFactory.getLog(PersistUIState.class);
    private static PersistUIState instance;
    private SettingsManager settingsManager;
    
    public static PersistUIState getInstance() {
        if (instance == null) {
            instance = new PersistUIState();
        }
        return instance;
    }
    
    private PersistUIState() {
        settingsManager = SettingsManager.getInstance();
    }
    
    private final Set<PersistElement> loadFunctions = new HashSet<>();
    
    
    public Set<PersistElement> getLoadFunctions() {
        return loadFunctions;
    }
    
    
    public void load() {       
        log.info("Loading " + loadFunctions.size() + " functions.");
        loadFunctions.forEach(t -> t.loadStates(settingsManager.getConfig(t.getName())));
     
    }
    
    public void save() {
        log.info("Saving user interface state.");
        loadFunctions.forEach(t -> settingsManager.setConfig(t.getName(), t.getState()));
        settingsManager.saveToFile();
    }
   
    
    
}

