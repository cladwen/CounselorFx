/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import business.converter.ConverterFactory;
import business.facade.LocalFacade;
import control.MapManager;
import javafx.scene.image.Image;
import model.Local;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.SettingsManager;

/**
 *
 * @author jmoura
 */
public class ImageFactory {

    private static final Log log = LogFactory.getLog(MapManager.class);
    private static final LocalFacade localFacade = new LocalFacade();
    private static final String[] terrainNames = {"vazio", "mar", "costa", "litoral", "floresta", "planicie", "montanha", "colinas", "pantano", "deserto", "wasteland", "lago"};

    public Image getTerrainImage(Local local) {
        int idxTerrain = ConverterFactory.terrainToIndex(localFacade.getTerrenoCodigo(local));
        return getDesenhoProperties(terrainNames[idxTerrain]);
    }

    public Image getDesenhoProperties(String filename) {
        if (SettingsManager.getInstance().isConfig("MapTiles", "2a", "2b")) {
            //feralonso bordless
            return new Image("/images/mapa/hex_2a_" + filename + ".png");
        } else if (SettingsManager.getInstance().isConfig("MapTiles", "2b", "2b")) {
            //bordless meppa
            return new Image("/images/mapa/hex_2b_" + filename + ".gif");
        } else if (SettingsManager.getInstance().isConfig("MapTiles", "2d", "2b")) {
            //bord meppa
            return new Image("/images/mapa/hex_" + filename + ".gif");
        } else if (SettingsManager.getInstance().isConfig("MapTiles", "3d", "2b")) {
            //3d from joao bordless
            return new Image("/images/mapa/hex_" + filename + ".png");
        } else {
            //bordless meppa
            return new Image("/images/mapa/hex_2b_" + filename + ".gif");
        }
    }
}
