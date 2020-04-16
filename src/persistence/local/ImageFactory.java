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

    public static int terrainToIndex(String cdTerrain) {
        /*
         * 1 'E', '0101' alto mar<br> 2 'C', '0102' costa<br> 3 'L', '0203'
         * litoral<br> 4 'F', '0206' floresta<br> 5 'P', '0308' planicie<br> 6
         * 'M', '0604' montanha<br> 7 'H', '0710' colinas<br> 8 'S', '1509'
         * pantano<br> 9 'D', '2538' deserto<br>
         */
        try {
            switch (cdTerrain.charAt(0)) {
                case 'E':
                    return 1;
                case 'C':
                    return 2;
                case 'L':
                    return 3;
                case 'F':
                    return 4;
                case 'P':
                    return 5;
                case 'M':
                    return 6;
                case 'H':
                    return 7;
                case 'S':
                    return 8;
                case 'D':
                    return 9;
                case 'W':
                    return 10;
                case 'K':
                    return 11;
                default:
                    return 0;
            }
        } catch (NullPointerException npe) {
            return 0;
        }
    }
}
