/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import business.converter.ConverterFactory;
import business.facade.LocalFacade;
import business.MapManager;
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
    private static final String[] bridgeNames = {"empty", "ponte_no", "ponte_ne", "ponte_l", "ponte_se", "ponte_so", "ponte_o"};
    private static final String[] creekNames = {"empty", "riacho_no", "riacho_ne", "riacho_l", "riacho_se", "riacho_so", "riacho_o"};
    private static final String[] landingNames = {"empty", "water_NW", "water_NE", "water_E", "water_SE", "water_SW", "water_W"};
    private static final String[] riverNames = {"empty", "rio_no", "rio_ne", "rio_l", "rio_se", "rio_so", "rio_o"};
    private static final String[] roadNames = {"empty", "road_no", "road_ne", "road_l", "road_se", "road_so", "road_o"};
    private static final String[] spanNames = {"empty", "vau_no", "vau_ne", "vau_l", "vau_se", "vau_so", "vau_o"};
    private static final String[] terrainNames = {"vazio", "mar", "costa", "litoral", "floresta", "planicie", "montanha", "colinas", "pantano", "deserto", "wasteland", "lago"};
    private static final String[] fortificationNames = {"vazio", "torre", "forte", "castelo", "fortaleza", "cidadela"};
    private static final String[] cityNames = {"vazio", "acampamento", "aldeia", "vila", "burgo", "cidade"};
    private static final String[] hiddenCityNames = {"vazio", "acampamento_hidden", "aldeia_hidden", "vila_hidden", "burgo_hidden", "cidade_hidden"};
    private static final String[] dockNames = {"vazio", "docas", "porto"};

    public Image getTerrainImage(Local local) {
        int idxTerrain = ConverterFactory.terrainToIndex(localFacade.getTerrenoCodigo(local));
        final String filename = terrainNames[idxTerrain];
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

    public static Image getRoadImage(int direcao) {
        return new Image("/images/mapa/hex_" + roadNames[direcao] + ".gif");
    }

    public static Image getRiverImage(int direcao) {
        return new Image("/images/mapa/hex_" + riverNames[direcao] + ".gif");
    }

    public static Image getCreekImage(int direcao) {
        return new Image("/images/mapa/hex_" + creekNames[direcao] + ".gif");
    }

    public static Image getBridgeImage(int direcao) {
        return new Image("/images/mapa/hex_" + bridgeNames[direcao] + ".gif");
    }

    public static Image getSpanImage(int direcao) {
        return new Image("/images/mapa/hex_" + spanNames[direcao] + ".gif");
    }

//    public Image getTracksImage(int direcao) {
//        return new Image("/images/mapa/hex_" + tr[direcao] + ".gif");
//    }
    public static Image getLandingImage(int direcao) {
        return new Image("/images/mapa/hex_" + landingNames[direcao] + ".gif");
    }

    public static Image getFortificationImage(int fortificationSize) {
        return new Image("/images/mapa/cp_" + fortificationNames[fortificationSize] + ".gif");
    }

    public static Image getCityImage(int citySize) {
        //TODO: draw, not image
        return new Image("/images/mapa/cp_" + cityNames[citySize] + ".gif");
    }

    public static Image getHiddenCityImage(int citySize) {
        return new Image("/images/mapa/cp_" + hiddenCityNames[citySize] + ".gif");
    }

    public static Image getDockImage(int dockSize) {
        return new Image("/images/mapa/cp_" + dockNames[dockSize] + ".gif");
    }

    public static Image getCapitalImage() {
        return new Image("/images/mapa/cp_capital.gif");
    }
}
