/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;


import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ListChangeListener;
import javafx.scene.text.Text;

/**
 *
 * @author serguei
 */
public class InfoPaneController {
    
    private InfoPaneModel model;
    private InfoPane infoPane;
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
        
        infoPane.getArtifactsText().textProperty().bind(model.getArtifacts());
        infoPane.getArtifactsText().textProperty().addListener((observable, oldValue, newValue) -> {     
            if (((String)newValue).trim().isEmpty()) {
               infoPane.getArtifactsPane().setVisible(false);
               infoPane.getArtifactsPane().setManaged(false);
            } else {
               infoPane.getArtifactsPane().setVisible(true);
               infoPane.getArtifactsPane().setManaged(true);
            }
            
        });
    }
        
    
}
