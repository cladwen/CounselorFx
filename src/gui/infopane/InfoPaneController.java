/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;



import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ListChangeListener;
import javafx.scene.text.Text;
import model.Artefato;

/**
 *
 * @author serguei
 */
public class InfoPaneController {
    
    private InfoPaneModel model;
    private InfoPane infoPane;
    private final ArtifactPopup artifactPopup = new ArtifactPopup();
    private ObservableStringValue coordinate = new SimpleStringProperty();
    
    
    private StringProperty hexProp = new SimpleStringProperty();
    
    public InfoPaneController(InfoPaneModel model, InfoPane infoPane) {
        this.model = model;
        this.infoPane = infoPane;
        initialize();
        
    }
    
    
    public void bind(ObservableStringValue coordinate) {
        this.coordinate = coordinate;
        this.coordinate.addListener(hex -> {
        this.coordinate = (SimpleStringProperty)hex;
          // this.hexProp.setValue(((SimpleStringProperty)hex).getValue());
          model.setHex(coordinate.getValue());
        });
    }
    
    private void initialize() {
        infoPane.getHexInfoPane().textProperty().bind(model.getHexInfoTitle());
        ((Text)infoPane.getHexInfoPane().getContent()).textProperty().bind(model.getHexInfoContent());
       
        infoPane.getCityText().textProperty().bind(model.getCities());  
        infoPane.getCityText().textProperty().addListener((observable, oldValue, newValue) -> {
      //     System.out.println("textfield changed from '" + oldValue + "' to '" + newValue + "'.");
            if (((String)newValue).trim().isEmpty()) {
               infoPane.getCitiesPane().setVisible(false);
               infoPane.getCitiesPane().setManaged(false);
            } else {
               infoPane.getCitiesPane().setVisible(true);
               infoPane.getCitiesPane().setManaged(true);
            }
            
        });
        infoPane.getTable().setItems(model.getProductData());
        model.getProductData().addListener(new ListChangeListener<ProductModel>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ProductModel> arg0) {
                infoPane.getTable().prefHeightProperty().bind(infoPane.getTable().fixedCellSizeProperty().multiply(Bindings.size(infoPane.getTable().getItems()).add(1.01)));
                infoPane.getTable().minHeightProperty().bind(infoPane.getTable().prefHeightProperty());
                infoPane.getTable().maxHeightProperty().bind(infoPane.getTable().prefHeightProperty());
                
                int rows = Bindings.size(infoPane.getTable().getItems()).get();
                boolean isEmpty = rows == 0;
                infoPane.getTable().setVisible(!isEmpty);
                infoPane.getTable().setManaged(!isEmpty);
            }
           
        });
        
        
        infoPane.getCharacterText().textProperty().bind(model.getCharacters());
        infoPane.getCharacterText().textProperty().addListener((observable, oldValue, newValue) -> {     
            if (((String)newValue).trim().isEmpty()) {
               infoPane.getCharactersPane().setVisible(false);
               infoPane.getCharactersPane().setManaged(false);
            } else {
               infoPane.getCharactersPane().setVisible(true);
               infoPane.getCharactersPane().setManaged(true);
            }
            
        });
        
        infoPane.getArmiesText().textProperty().bind(model.getArmies());
        infoPane.getArmiesText().textProperty().addListener((observable, oldValue, newValue) -> {     
            if (((String)newValue).trim().isEmpty()) {
               infoPane.getArmiesPane().setVisible(false);
               infoPane.getArmiesPane().setManaged(false);
            } else {
               infoPane.getArmiesPane().setVisible(true);
               infoPane.getArmiesPane().setManaged(true);
            }
            
        });
        
        infoPane.getArtifactsList().setItems(model.getArtifactData()); 
        
                
        
        infoPane.getArtifactsList().getItems().addListener((ListChangeListener.Change e) -> {        
            int rows = Bindings.size(infoPane.getArtifactsList().getItems()).get();
            
            infoPane.getArtifactsList().prefHeightProperty().bind(infoPane.getArtifactsList().fixedCellSizeProperty().multiply(Bindings.size(infoPane.getArtifactsList().getItems())));
            infoPane.getArtifactsList().minHeightProperty().bind(infoPane.getArtifactsList().prefHeightProperty());
            infoPane.getArtifactsList().maxHeightProperty().bind(infoPane.getArtifactsList().prefHeightProperty());
            
            boolean isEmpty = rows == 0;
            infoPane.getArtifactsPane().setVisible(!isEmpty);
            infoPane.getArtifactsPane().setManaged(!isEmpty);            
        });
        infoPane.getArtifactsList().setOnMouseClicked(e ->{ 
        
            Artefato artifact = (Artefato)infoPane.getArtifactsList().getSelectionModel().getSelectedItem();
            
            artifactPopup.hide();
            artifactPopup.setArtifacto(artifact);                       
            artifactPopup.show (infoPane.getArtifactsList(), e.getScreenX(), e.getScreenY());            
        });
      
    }
        
    
}
