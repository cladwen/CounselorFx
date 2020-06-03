/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistence.local.ImageFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

/**
 *
 * @author jmoura
 */
public class ConfigControl {

    private static final Log log = LogFactory.getLog(ConfigControl.class);
    private final double barHeight = 18;
    private final Stage mainStage;
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();
//    private final ImageFactory imageFactory = new ImageFactory();

    public ConfigControl(Stage primaryStage) {
        mainStage = primaryStage;
    }

    private void setMapConfig(String cdProperty, String value) {
        setMapConfig(cdProperty, value, true);
    }

    private void setMapConfig(String cdProperty, String value, boolean mapChanged) {
        SettingsManager.getInstance().setConfigAndSaveToFile(cdProperty, value);
        log.debug(String.format(labels.getString("CHANGED.X.TO"), cdProperty, value)); //Changed %s to %s
        if (mapChanged) {
            CounselorStateMachine.getInstance().setMapChanged(true);
        }
    }

    public List<Control> getUiElements() {
        List<Control> elements = new ArrayList<>();
        elements.add(getToggleButton("MapRenderRoads", ImageFactory.getRoadIcon(barHeight, true), ImageFactory.getRoadIcon(barHeight, false), labels.getString("CONFIG.ROADS.TOOLTIP")));
        elements.add(getToggleButton("MapRenderRiver", ImageFactory.getRiverIcon(barHeight, true), ImageFactory.getRiverIcon(barHeight, false), labels.getString("CONFIG.RIVER.TOOLTIP")));
        elements.add(getToggleButton("MapRenderCreek", ImageFactory.getCreekIcon(barHeight, true), ImageFactory.getCreekIcon(barHeight, false), labels.getString("CONFIG.CREEK.TOOLTIP")));
        elements.add(getToggleButton("MapRenderBridge", ImageFactory.getBridgeIcon(barHeight, true), ImageFactory.getBridgeIcon(barHeight, false), labels.getString("CONFIG.BRIDGE.TOOLTIP")));
        elements.add(getToggleButton("MapRenderSpan", ImageFactory.getSpanIcon(barHeight, true), ImageFactory.getSpanIcon(barHeight, false), labels.getString("CONFIG.SPAN.TOOLTIP")));
        elements.add(getToggleButton("MapRenderLanding", ImageFactory.getLandingIcon(barHeight, true), ImageFactory.getLandingIcon(barHeight, false), labels.getString("CONFIG.LANDING.TOOLTIP")));

        elements.add(getToggleButton("MapRenderCity", ImageFactory.getCityIcon(barHeight, true), ImageFactory.getCityIcon(barHeight, false), labels.getString("CONFIG.CITY.TOOLTIP")));
        elements.add(getToggleButton("MapRenderFortification", ImageFactory.getFortificationIcon(barHeight, true), ImageFactory.getFortificationIcon(barHeight, false), labels.getString("CONFIG.FORTIFICATION.TOOLTIP")));
        elements.add(getToggleButton("MapRenderArmy", ImageFactory.getArmyIcon(barHeight, true), ImageFactory.getArmyIcon(barHeight, false), labels.getString("CONFIG.ARMY.TOOLTIP")));
        elements.add(getToggleButton("MapRenderArmyTrack", ImageFactory.getTrackIcon(barHeight, true), ImageFactory.getTrackIcon(barHeight, false), labels.getString("CONFIG.TRACK.TOOLTIP")));
        final Node armyShieldIconOn = ImageFactory.getArmyShieldIcon(barHeight, true, WorldFacadeCounselor.getInstance().getCenario());
        final Node armyShieldIconOff = ImageFactory.getArmyShieldIcon(barHeight, false, WorldFacadeCounselor.getInstance().getCenario());
        elements.add(getToggleButton("MapDrawAllArmyIcons", armyShieldIconOn, armyShieldIconOff, labels.getString("CONFIG.ARMYICON.TOOLTIP")));
        elements.add(getToggleButton("MapRenderFogOfWar", ImageFactory.getFogofwarIcon(barHeight, true), ImageFactory.getFogofwarIcon(barHeight, false), labels.getString("CONFIG.FOGOFWAR.TOOLTIP")));
        elements.add(getToggleButton("MapRenderGrid", ImageFactory.getGridIcon(barHeight, true), ImageFactory.getGridIcon(barHeight, false), labels.getString("CONFIG.GRID.TOOLTIP")));
        elements.add(getToggleButton("MapRenderLandmark", ImageFactory.getLandmarkIcon(barHeight, true), ImageFactory.getLandmarkIcon(barHeight, false), labels.getString("CONFIG.LANDMARK.TOOLTIP")));
        elements.add(getToggleButton("MapRenderCharacters", ImageFactory.getCharacterIcon(barHeight, true), ImageFactory.getCharacterIcon(barHeight, false), labels.getString("CONFIG.CHAR.TOOLTIP")));
        elements.add(getToggleButton("MapRenderItems", ImageFactory.getItemIcon(barHeight, true), ImageFactory.getItemIcon(barHeight, false), labels.getString("CONFIG.ITEM.TOOLTIP")));
        elements.add(getToggleButton("MapRenderCombats", ImageFactory.getCombatsIcon(barHeight, true), ImageFactory.getCombatsIcon(barHeight, false), labels.getString("CONFIG.COMBAT.TOOLTIP")));
        elements.add(getToggleButton("MapRenderOverrun", ImageFactory.getOverrunIcon(barHeight, true), ImageFactory.getOverrunIcon(barHeight, false), labels.getString("CONFIG.OVERRUN.TOOLTIP")));

        final String[] titlesCityColor = {labels.getString("DEFAULT"), labels.getString("ALIANCA"), labels.getString("TEAM"), labels.getString("DIPLOMACY")};
        elements.add(getChoiceBox(titlesCityColor, "MapCityColorType", "1", labels.getString("CONFIG.CITYCOLOR.TOOLTIP")));
        final String[] terrainTile = {labels.getString("BORDERLESS"), labels.getString("BORDER"), labels.getString("3D"), labels.getString("TEXTURE")};
        elements.add(getChoiceBox(terrainTile, "MapTerrainTile", "1", labels.getString("CONFIG.TILESET.TOOLTIP")));
        final String[] zoomOptionsDisplay = {"100%", "50%", "75%", "150%", "200%", "250%"};
        elements.add(getChoiceBox(zoomOptionsDisplay, "MapZoomPercent", "1", labels.getString("CONFIG.MAPZOOM.TOOLTIP")));
        final String[] styleNames = {labels.getString("PLAIN"), labels.getString("MATERIAL")};
        elements.add(getChoiceBoxUi(styleNames, "UiStyle", "1", labels.getString("CONFIG.SKIN.TOOLTIP")));
        elements.add(getFontSlider());
        elements.add(getCoordinateToggle());

        return elements;
    }

    private ChoiceBox getChoiceBox(String[] displayValues, final String cdProperty, final String defaultValue, final String tooltip) {
        final List<String> asList = Arrays.asList(displayValues);
        ChoiceBox buttonChoice = new ChoiceBox();
        buttonChoice.getItems().addAll(asList);
        buttonChoice.setTooltip(new Tooltip(tooltip));
        try {
            buttonChoice.setValue(displayValues[SettingsManager.getInstance().getConfigAsInt(cdProperty, defaultValue) - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            //default to first option if trouble
            buttonChoice.setValue(displayValues[0]);
        }
        buttonChoice.setOnAction(e -> {
            int index = asList.indexOf(buttonChoice.getValue()) + 1;
            setMapConfig(cdProperty, index + "");
        });
        return buttonChoice;
    }

    private ChoiceBox getChoiceBoxUi(String[] displayValues, final String cdProperty, final String defaultValue, final String tooltip) {
        final List<String> asList = Arrays.asList(displayValues);
        ChoiceBox buttonChoice = new ChoiceBox();
        buttonChoice.getItems().addAll(asList);
        buttonChoice.setTooltip(new Tooltip(tooltip));
        try {
            buttonChoice.setValue(displayValues[SettingsManager.getInstance().getConfigAsInt(cdProperty, defaultValue) - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            //default to first option if trouble
            buttonChoice.setValue(displayValues[0]);
        }
        buttonChoice.setOnAction(e -> {
            int index = asList.indexOf(buttonChoice.getValue()) + 1;
            setMapConfig(cdProperty, index + "", false);
            updateStyle(mainStage.getScene());
        });
        return buttonChoice;
    }

    private Slider getFontSlider() {
        //coordinateFontSize = ;
        Slider fontSlider = new Slider(8, 40, SettingsManager.getInstance().getConfigAsInt("MapCoordinatesSize", "8"));
        fontSlider.setMajorTickUnit(10d);
        fontSlider.setMinorTickCount(4);
        fontSlider.setSnapToTicks(true);
        fontSlider.setShowTickMarks(true);
        fontSlider.setShowTickLabels(true);
        fontSlider.setTooltip(new Tooltip(labels.getString("CONFIG.MAPCOORDINATESSIZE.TOOLTIP")));
        fontSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
            setMapConfig("MapCoordinatesSize", String.valueOf(newValue.intValue()));
        });
        return fontSlider;
    }

    private ToggleButton getToggleButton(Node icon, String tooltip) {
        ToggleButton buttonIcon = new ToggleButton();
        buttonIcon.setWrapText(false);
        buttonIcon.setSelected(false);
        buttonIcon.setTooltip(new Tooltip(tooltip));
        buttonIcon.setGraphic(icon);
        return buttonIcon;
    }

    public Pane getConfigBar() {
        //Config bar
        FlowPane configBar = new FlowPane();
        configBar.setAlignment(Pos.BASELINE_CENTER);
        configBar.setHgap(0);
        configBar.getChildren().addAll(getUiElements());
        //set size change when mouse over
        configBar.setScaleX(.5);
        configBar.setScaleY(.5);
        configBar.setOnMouseEntered((MouseEvent e) -> {
            configBar.setScaleX(1);
            configBar.setScaleY(1);
        });

        configBar.setOnMouseExited((MouseEvent e) -> {
            configBar.setScaleX(.5);
            configBar.setScaleY(.5);
        });

        //gear icon and status msgs
        FlowPane statusBar = new FlowPane();
        configBar.setAlignment(Pos.BASELINE_CENTER);
        configBar.setHgap(0);
        //add a dismiss icon at the end
        ToggleButton dismissIcon = getToggleButton(ImageFactory.getConfigIcon(barHeight, false), labels.getString("CONFIG.DISMISSCONFIG.TOOLTIP"));
        ToggleButton displayIcon = getToggleButton(ImageFactory.getConfigIcon(barHeight / 2, true), labels.getString("CONFIG.SHOWCONFIG.TOOLTIP"));
        //set size change when mouse over
        displayIcon.setScaleX(.5);
        displayIcon.setScaleY(.5);
        displayIcon.setOnMouseEntered((MouseEvent e) -> {
            displayIcon.setScaleX(1);
            displayIcon.setScaleY(1);
        });

        displayIcon.setOnMouseExited((MouseEvent e) -> {
            displayIcon.setScaleX(.5);
            displayIcon.setScaleY(.5);
        });

        configBar.getChildren().add(dismissIcon);
        statusBar.getChildren().add(displayIcon);

        StackPane root = new StackPane(configBar);
        //at last, set actions
        setMenuSwapAnimation(root, configBar, statusBar, dismissIcon);
        setMenuBackAnimation(root, configBar, statusBar, displayIcon);
        return root;
    }

    private void setMenuSwapAnimation(Pane navList, Pane view1, Pane view2, ToggleButton icon) {
        //add event trigger
        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            //set animation
            navList.getChildren().add(view2);
            double width = navList.getWidth();
            KeyFrame start = new KeyFrame(Duration.ZERO,
                    new KeyValue(view2.translateXProperty(), width),
                    new KeyValue(view1.translateXProperty(), 0));
            KeyFrame end = new KeyFrame(Duration.seconds(1),
                    new KeyValue(view2.translateXProperty(), 0),
                    new KeyValue(view1.translateXProperty(), -width));
            Timeline slide = new Timeline(start, end);
            slide.setOnFinished(e -> navList.getChildren().remove(view1));
            slide.play();
        });
    }

    private void setMenuBackAnimation(Pane navList, Pane view1, Pane view2, ToggleButton buttonIcon) {
        buttonIcon.setOnAction(event -> {
            navList.getChildren().add(view1);
            double width = navList.getWidth();
            KeyFrame start = new KeyFrame(Duration.ZERO,
                    new KeyValue(view1.translateXProperty(), width),
                    new KeyValue(view2.translateXProperty(), 0));
            KeyFrame end = new KeyFrame(Duration.seconds(1),
                    new KeyValue(view1.translateXProperty(), 0),
                    new KeyValue(view2.translateXProperty(), -width));
            Timeline slide = new Timeline(start, end);
            slide.setOnFinished(e -> navList.getChildren().remove(view2));
            slide.play();
        });
    }

    private ToggleButton getToggleButton(final String cdProperty, Node iconOn, Node iconOff, final String tooltip) {
        final String onValue = "1";
        final String offValue = "0";
        final String defaultValue = "1";
        return getToggleButton(cdProperty, onValue, offValue, defaultValue, iconOn, iconOff, tooltip);
    }

    private ToggleButton getToggleButton(final String cdProperty, final String onValue, final String offValue, final String defaultValue, Node iconOn, Node iconOff, final String tooltip) {
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

    private ToggleButton getCoordinateToggle() {
        //render coordinates
        final String onValue = "1";
        final String offValue = "0";
        final String defaultValue = "1";
        final String tooltip = labels.getString("CONFIG.COORDINATES.TOOLTIP");
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

    public void updateStyle(Scene scene) {
        String styleId = SettingsManager.getInstance().getConfig("UiStyle", "1");
        switch (styleId) {
            case "2":
                scene.getStylesheets().add("resources/styles/material-fx-v0_3.css");
                break;
            default:
                scene.getStylesheets().add("resources/styles/default-style.css");
                break;
        }
    }
}
