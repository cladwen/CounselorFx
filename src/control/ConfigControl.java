/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistence.local.ImageFactory;
import persistenceCommons.SettingsManager;

/**
 *
 * @author jmoura
 */
public class ConfigControl {

    private static final Log log = LogFactory.getLog(ConfigControl.class);
    private final double barHeight = 18;

    private void setMapConfig(String cdProperty, String value) {
        SettingsManager.getInstance().setConfigAndSaveToFile(cdProperty, value);
        log.info(String.format("Changed %s to %s", cdProperty, value));
        CounselorStateMachine.getInstance().setMapChanged(true);
    }

    public List<Control> getUiElements() {
        List<Control> elements = new ArrayList<>();
        //TODO NEXT: Make actions on configs ; load defaults, save values to properties; redraw map
        //TODO: Fix elements Scale to screen DPI

        final String[] titlesCityColor = {"Default", "Alliance", "Team", "Diplomacy"};
        elements.add(getChoiceBox(titlesCityColor, "MapCityColorType", "1", "How cities are painted. Regular, Alliance, My enemies, Border."));
        elements.add(getZoomSlider());
        elements.add(getFontSlider());

        //armyIconDrawType = SettingsManager.getInstance().isConfig("MapDrawAllArmyIcons", "1", "1");
        ToggleButton armyIconsType = new ToggleButton("Individual");
        armyIconsType.setTooltip(new Tooltip("Army icons are consolidated or individual"));
        armyIconsType.setOnAction(e -> {
            if (armyIconsType.isSelected()) {
                armyIconsType.setText("Individual");
            } else {
                armyIconsType.setText("Consolidate");
            }
        });
        armyIconsType.setSelected(true);
        elements.add(armyIconsType);

        elements.add(configCoordinateToggle());
        elements.add(configRoads("MapRenderRoads", "1", "0", "1", "Draw roads on map"));

        //TODO: finish the list of configs
//        renderRivers = SettingsManager.getInstance().isConfig("MapRenderRiver", "1", "1");
//        renderCreek = SettingsManager.getInstance().isConfig("MapRenderCreek", "1", "1");
//        renderBridge = SettingsManager.getInstance().isConfig("MapRenderBridge", "1", "1");
//        renderSpan = SettingsManager.getInstance().isConfig("MapRenderSpan", "1", "1");
//        renderTracks = SettingsManager.getInstance().isConfig("MapRenderArmyTrack", "1", "1");
//        renderLanding = SettingsManager.getInstance().isConfig("MapRenderLanding", "1", "1");
//        renderFogOfWar = !SettingsManager.getInstance().isWorldBuilder() && SettingsManager.getInstance().isConfig("MapRenderFogOfWar", "1", "1");
//        renderLandmark = SettingsManager.getInstance().isConfig("MapRenderLandmark", "1", "1");
//        renderCities = SettingsManager.getInstance().isConfig("MapRenderCities", "1", "1");
//        renderForts = SettingsManager.getInstance().isConfig("MapRenderForst", "1", "1");
//        renderArmy = SettingsManager.getInstance().isConfig("MapRenderArmy", "1", "1");
//        renderFeatures = SettingsManager.getInstance().isConfig("MapRenderFeature", "1", "1");
//        renderGrid = SettingsManager.getInstance().isConfig("MapRenderGrid", "1", "0");
        return elements;
    }

    private Slider getZoomSlider() {
        double zoomFactor = (double) SettingsManager.getInstance().getConfigAsInt("MapZoomPercent", "100");
        Slider zoomSlider = new Slider(50, 200, zoomFactor);
        zoomSlider.setMajorTickUnit(100d);
        zoomSlider.setMinorTickCount(50);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setTooltip(new Tooltip("Map zoom"));
        //FIXME: need to store previous value and recalculate to current, to be able to enable here.
        zoomSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
            setMapConfig("MapZoomPercent", String.valueOf(newValue.intValue()));
        });
        return zoomSlider;
    }

    private Slider getFontSlider() {
        //coordinateFontSize = ;
        Slider fontSlider = new Slider(8, 40, SettingsManager.getInstance().getConfigAsInt("MapCoordinatesSize", "8"));
        fontSlider.setMajorTickUnit(10d);
        fontSlider.setMinorTickCount(4);
        fontSlider.setSnapToTicks(true);
        fontSlider.setShowTickMarks(true);
        fontSlider.setShowTickLabels(true);
        fontSlider.setTooltip(new Tooltip("Coordinates font size on map"));
        fontSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
            setMapConfig("MapCoordinatesSize", String.valueOf(newValue.intValue()));
        });
        return fontSlider;
    }

    private ChoiceBox getChoiceBox(String[] displayValues, final String cdProperty, final String defaultValue, final String tooltip) {
        final List<String> asList = Arrays.asList(displayValues);
        ChoiceBox cityColor = new ChoiceBox();
        cityColor.getItems().addAll(asList);
        cityColor.setTooltip(new Tooltip(tooltip));
        cityColor.setValue(displayValues[SettingsManager.getInstance().getConfigAsInt(cdProperty, defaultValue) - 1]);
        cityColor.setOnAction(e -> {
            int index = asList.indexOf(cityColor.getValue()) + 1;
            setMapConfig(cdProperty, index + "");
        });
        return cityColor;
    }

    public ToggleButton configRoads(final String cdProperty, final String onValue, final String offValue, final String defaultValue, final String tooltip) {
        ToggleButton roads = new ToggleButton();
        roads.setWrapText(true);
        roads.setSelected(SettingsManager.getInstance().isConfig(cdProperty, onValue, defaultValue));
        roads.setTooltip(new Tooltip(tooltip));
        roads.setGraphic(ImageFactory.getRoadIcon(barHeight));
        roads.setOnAction(e -> {
            if (roads.isSelected()) {
                setMapConfig(cdProperty, onValue);
            } else {
                setMapConfig(cdProperty, offValue);
            }
        });
        return roads;
    }

    public ToggleButton configCoordinateToggle() {
        //render coordinates
        ToggleButton coordinateToggle = new ToggleButton("0101");
        coordinateToggle.setSelected(SettingsManager.getInstance().isConfig("MapRenderCoordinate", "1", "1"));
        coordinateToggle.setTooltip(new Tooltip("Toggle map coordinates on/off"));
        coordinateToggle.setOnAction(e -> {
            if (coordinateToggle.isSelected()) {
                coordinateToggle.setText("0101");
                setMapConfig("MapRenderCoordinate", "1");
            } else {
                coordinateToggle.setText("    ");
                setMapConfig("MapRenderCoordinate", "0");
            }
        });
        return coordinateToggle;
    }

}
