/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
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
    private final ImageFactory imageFactory = new ImageFactory();

    private void setMapConfig(String cdProperty, String value) {
        SettingsManager.getInstance().setConfigAndSaveToFile(cdProperty, value);
        log.info(String.format("Changed %s to %s", cdProperty, value));
        CounselorStateMachine.getInstance().setMapChanged(true);
    }

    public List<Control> getUiElements() {
        List<Control> elements = new ArrayList<>();
        //TODO NEXT: Make actions on configs ; load defaults, save values to properties; redraw map
        //TODO: Fix elements Scale to screen DPI

        elements.add(configToggleButton("MapRenderRoads", ImageFactory.getRoadIcon(barHeight, true), ImageFactory.getRoadIcon(barHeight, false), "Draw roads on map"));
        elements.add(configToggleButton("MapRenderRiver", ImageFactory.getRiverIcon(barHeight, true), ImageFactory.getRiverIcon(barHeight, false), "Draw rivers on map"));
        elements.add(configToggleButton("MapRenderCreek", ImageFactory.getCreekIcon(barHeight, true), ImageFactory.getCreekIcon(barHeight, false), "Draw creeks on map"));
        elements.add(configToggleButton("MapRenderBridge", ImageFactory.getBridgeIcon(barHeight, true), ImageFactory.getBridgeIcon(barHeight, false), "Draw bridges on map"));
        elements.add(configToggleButton("MapRenderSpan", ImageFactory.getSpanIcon(barHeight, true), ImageFactory.getSpanIcon(barHeight, false), "Draw spans on map"));
        elements.add(configToggleButton("MapRenderLanding", ImageFactory.getLandingIcon(barHeight, true), ImageFactory.getLandingIcon(barHeight, false), "Draw landings on map"));

        elements.add(configToggleButton("MapRenderCity", ImageFactory.getCityIcon(barHeight, true), ImageFactory.getCityIcon(barHeight, false), "Draw cities on map"));
        elements.add(configToggleButton("MapRenderFortification", ImageFactory.getFortificationIcon(barHeight, true), ImageFactory.getFortificationIcon(barHeight, false), "Draw city's fortification on map"));
        elements.add(configToggleButton("MapRenderArmy", ImageFactory.getArmyIcon(barHeight, true), ImageFactory.getArmyIcon(barHeight, false), "Draw armies on map"));
        elements.add(configToggleButton("MapRenderArmyTrack", ImageFactory.getTrackIcon(barHeight, true), ImageFactory.getTrackIcon(barHeight, false), "Draw army's tracks on map"));
        final Node armyShieldIconOn = ImageFactory.getArmyShieldIcon(barHeight, true, WorldFacadeCounselor.getInstance().getCenario());
        final Node armyShieldIconOff = ImageFactory.getArmyShieldIcon(barHeight, false, WorldFacadeCounselor.getInstance().getCenario());
        elements.add(configToggleButton("MapDrawAllArmyIcons", armyShieldIconOn, armyShieldIconOff, "Draw one icon per army or consolidate armies from the same nation"));
        elements.add(configToggleButton("MapRenderFogOfWar", ImageFactory.getFogofwarIcon(barHeight, true), ImageFactory.getFogofwarIcon(barHeight, false), "Draw fog of war on map"));
        elements.add(configToggleButton("MapRenderGrid", ImageFactory.getGridIcon(barHeight, true), ImageFactory.getGridIcon(barHeight, false), "Draw grid on map"));
        elements.add(configToggleButton("MapRenderLandmark", ImageFactory.getLandmarkIcon(barHeight, true), ImageFactory.getLandmarkIcon(barHeight, false), "Draw landmarks on map"));
        //TODO NEXT: finish the list of configs
        elements.add(configToggleButton("MapRenderCharacters", ImageFactory.getCharacterIcon(barHeight, true), ImageFactory.getCharacterIcon(barHeight, false), "Draw characters on map"));
        elements.add(configToggleButton("MapRenderItems", ImageFactory.getItemIcon(barHeight, true), ImageFactory.getItemIcon(barHeight, false), "Draw magic items on map"));
        elements.add(configToggleButton("MapRenderCombats", ImageFactory.getCombatsIcon(barHeight, true), ImageFactory.getCombatsIcon(barHeight, false), "Draw combat icons on map"));
        elements.add(configToggleButton("MapRenderOverrun", ImageFactory.getOverrunIcon(barHeight, true), ImageFactory.getOverrunIcon(barHeight, false), "Draw overrun icons on map"));

        final String[] titlesCityColor = {"Default", "Alliance", "Team", "Diplomacy"};
        elements.add(getChoiceBox(titlesCityColor, "MapCityColorType", "1", "How cities are painted: Regular, Alliance, My enemies, Border"));
        //TODO NEXT: finish the list of configs
        final String[] terrainTile = {"Borderless", "Border", "3D", "Texture"};
        elements.add(getChoiceBox(terrainTile, "MapTerrainTile", "1", "Which terrain tileset to use"));
        elements.add(getZoomSlider());
        elements.add(getFontSlider());
        elements.add(configCoordinateToggle());

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

    public ToggleButton configToggleButton(final String cdProperty, Node iconOn, Node iconOff, final String tooltip) {
        final String onValue = "1";
        final String offValue = "0";
        final String defaultValue = "1";
        return configToggleButton(cdProperty, onValue, offValue, defaultValue, iconOn, iconOff, tooltip);
    }

    public ToggleButton configToggleButton(final String cdProperty, final String onValue, final String offValue, final String defaultValue, Node iconOn, Node iconOff, final String tooltip) {
        final boolean isSelected = SettingsManager.getInstance().isConfig(cdProperty, onValue, defaultValue);
        ToggleButton buttonIcon = new ToggleButton();
        buttonIcon.setWrapText(false);
        buttonIcon.setSelected(isSelected);
        buttonIcon.setTooltip(new Tooltip(tooltip));
        if (isSelected) {
            buttonIcon.setGraphic(iconOn);
        } else {
            buttonIcon.setGraphic(iconOff);
        }
        buttonIcon.setOnAction(e -> {
            if (buttonIcon.isSelected()) {
                //save property, prep map
                setMapConfig(cdProperty, onValue);
                //change icon to new state
                buttonIcon.setGraphic(iconOn);
            } else {
                //save property, prep map
                setMapConfig(cdProperty, offValue);
                //change icon to new state
                buttonIcon.setGraphic(iconOff);
            }
        });
        return buttonIcon;
    }

    public ToggleButton configCoordinateToggle() {
        //render coordinates
        final String onValue = "1";
        final String offValue = "0";
        final String defaultValue = "1";
        final String tooltip = "Toggle map coordinates on/off";
        String cdProperty = "MapRenderCoordinate";
        final String textOn = "0101";
        final String textOff = "\"  \"";
        final boolean isSelected = SettingsManager.getInstance().isConfig(cdProperty, onValue, defaultValue);
        final ToggleButton button = new ToggleButton();
        button.setWrapText(false);
        button.setSelected(isSelected);
        button.setTooltip(new Tooltip(tooltip));
        if (isSelected) {
            button.setText(textOn);
        } else {
            button.setText(textOff);
        }
        button.setOnAction(e -> {
            if (button.isSelected()) {
                //save property, prep map
                setMapConfig(cdProperty, onValue);
                //change icon to new state
                button.setText(textOn);
            } else {
                //save property, prep map
                setMapConfig(cdProperty, offValue);
                //change icon to new state
                button.setText(textOff);
            }
        });
        return button;
    }
}
