/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import model.Artefato;

/**
 *
 * @author serguei
 */
public class ArtifactPopup extends Popup {
    
    private final VBox artifactBox = new VBox();
    private final Text artifactDescription = new Text(); 
    private final Label artifactPower = new Label();
        
    
    public ArtifactPopup() {
        super();
        setAutoHide(true);
        artifactBox.getStylesheets().add(getClass().getResource("infoPane.css").toExternalForm());  
        artifactBox.getStyleClass().add("artifact-popup");
        artifactBox.getChildren().add(artifactDescription);
        artifactBox.getChildren().add(artifactPower);
        this.getContent().add(artifactBox);
   
    }
    
    public void setArtifacto(Artefato artefacto) {
        
        this.artifactDescription.setText(artefacto.getDescricao());
        artifactPower.setText(artefacto.getPrimario() + ": " + artefacto.getValor());
    }
    
}
