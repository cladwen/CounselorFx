/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

/**
 *
 * @author serguei
 */
public class InfoPane extends VBox{
    
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();
    private final TitledPane hexInfoPane = new TitledPane("", new Text());
    private TitledPane charactersPane = new TitledPane(labels.getString("PERSONAGENS.LOCAL"), new TextFlow());
    private TitledPane citiesPane = new TitledPane();
    private TitledPane armiesPane = new TitledPane(labels.getString("EXERCITOS"), new TextFlow());   
    private TitledPane artifactsPane = new TitledPane(labels.getString("ARTEFATOS"), new TextFlow());

      
    
    private VBox cityBox = new VBox();
    private TextFlow cityTextFlow = null;
    private TableView table = new TableView();

    
    
    
    
    public InfoPane() {
        super(0);        
        this.getStylesheets().add(getClass().getResource("infoPane.css").toExternalForm());
        getChildren().add(hexInfoPane);
     
        cityTextFlow = new TextFlow();    
        cityTextFlow.getStyleClass().add("cityText");
        cityTextFlow.getChildren().add(new Text());
        
        cityBox.getChildren().add(cityTextFlow);
        initProductTable();
        cityBox.getChildren().add(table);
        
        citiesPane.setText(labels.getString("CIDADES"));
      
        citiesPane.setContent(cityBox);
        getChildren().add(citiesPane);
        charactersPane.getStyleClass().add("cityText");
       
        Text characterText = new Text();
        ((TextFlow)charactersPane.getContent()).getChildren().add(characterText);
        getChildren().add(charactersPane);
        
        Text armiesText = new Text();
        ((TextFlow)armiesPane.getContent()).getChildren().add(armiesText);
        getChildren().add(armiesPane);
        
        Text artifactsText = new Text();
        ((TextFlow)artifactsPane.getContent()).getChildren().add(artifactsText);
        getChildren().add(artifactsPane);
        
        
    }
    
     
    public TitledPane getHexInfoPane() {
        return hexInfoPane;
    }
    
    public TitledPane getCharactersPane() {
        return charactersPane;
    }
    public TitledPane getCitiesPane() {
        return citiesPane;
    }
    public Text getCityText() {
        return (Text)this.cityTextFlow.getChildren().get(0);
    }
    
    public Text getCharacterText() {
        return (Text)((TextFlow)this.charactersPane.getContent()).getChildren().get(0);
    }
    
    public Text getArmiesText() {
        return (Text)((TextFlow)this.armiesPane.getContent()).getChildren().get(0);
    }
    
     public Text getArtifactsText() {
        return (Text)((TextFlow)this.artifactsPane.getContent()).getChildren().get(0);
    }

    public TableView getTable() {
        return table;
    }
    
     public TitledPane getArmiesPane() {
        return armiesPane;
    }

    public TitledPane getArtifactsPane() {
        return artifactsPane;
    }

    private void initProductTable() {
        table.setVisible(false);
        table.setManaged(false);        
        table.getStyleClass().add("resources-table");
       
        
        
        TableColumn productCol = new TableColumn("Resource");
        productCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));//  productCol.setMinWidth(100);
        productCol.setCellValueFactory(new PropertyValueFactory<ProductModel, String>("name"));
                
        TableColumn totalCol = new TableColumn("Total");
        totalCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
   //     totalCol.setMinWidth(100);
        totalCol.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("total"));
        
        TableColumn prodlCol = new TableColumn("Production");
        prodlCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
   //     prodlCol.setMinWidth(100);
        prodlCol.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("production"));
        
        TableColumn storedCol = new TableColumn("Store");
        storedCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
   //     storedCol.setMinWidth(100);
        storedCol.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("stored"));
        
        TableColumn sellCol = new TableColumn("Sell");
        sellCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));        
   //     sellCol.setMinWidth(100);
        sellCol.setCellValueFactory(new PropertyValueFactory<ProductModel, Integer>("sell"));
        
        sellCol.setCellFactory(col -> new TableCell<ProductModel, Number>() {
            @Override
            public void updateItem(Number sell, boolean empty) {
                super.updateItem(sell, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%,d", sell.intValue()));
                }
            }
        });
        
        table.getColumns().addAll(productCol, totalCol, prodlCol, storedCol, sellCol);
        
        
      
       
    }
}
