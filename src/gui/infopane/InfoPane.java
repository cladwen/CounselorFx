/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;

import gui.CounselorFx;
import gui.persist.PersistElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Artefato;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

/**
 *
 * @author serguei
 */
public class InfoPane extends VBox implements PersistElement {
    
    private static final Log log = LogFactory.getLog(CounselorFx.class);
    
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();
    private final TitledPane hexInfoPane = new TitledPane("", new Text());
    private final TitledPane charactersPane = new TitledPane(labels.getString("PERSONAGENS.LOCAL"), new TextFlow());
    private TitledPane citiesPane = new TitledPane();
    private TitledPane armiesPane = new TitledPane(labels.getString("EXERCITOS"), new TextFlow());   
    private TitledPane artifactsPane = new TitledPane(labels.getString("ARTEFATOS"), new VBox());     
    
    private VBox cityBox = new VBox();
    private TextFlow cityTextFlow = null;
    private TableView table = new TableView();
    private ListView<Artefato> artifactListView= new ListView();
    
    private List<TitledPane> titledPaneList = new ArrayList<>();
        
    
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
        
        artifactsPane.setContent(artifactListView);       
        artifactsPane.setVisible(false);
        artifactsPane.setManaged(false);   
        getChildren().add(artifactsPane);  
        initArtifactList();
        
        titledPaneList.add(hexInfoPane);
        titledPaneList.add(citiesPane);
        titledPaneList.add(charactersPane);
        titledPaneList.add(armiesPane);
        titledPaneList.add(artifactsPane);
        
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
    
     public ListView getArtifactsList() {
        return artifactListView;
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
    
    private void initArtifactList() {
     //   artifactListView.setVisible(false);
     //   artifactListView.setManaged(false);        
        artifactListView.getStyleClass().add("artifact-list");
        artifactListView.setCellFactory(artifact -> new ListCell<Artefato>() {
            @Override
            protected void updateItem(Artefato item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(item.getNome());
                    if (item.isPosse()) {
                        sb.append(" (");
                        sb.append(item.getOwner().getNome());
                        sb.append(")");
                    }
                    setText(sb.toString());
                }
            }
            
        });
    }

 
    @Override
    public String getName() {
        return this.getClass().getName();
    }
  
    @Override
    public void loadStates(String args) {
        final boolean[] states = new boolean[getChildren().size()];
        if (args != null) {
             String[] stringValue = args.split("\\|");
             Boolean[] booleanValue = Arrays.stream(stringValue).map(t -> Boolean.valueOf(t)).toArray(Boolean[]::new);
             for (int i = 0; i < states.length; i++) {
                 states[i] = booleanValue[i];
             }
        }
        IntStream.range(0, states.length).forEach(i -> ((TitledPane)getChildren().get(i)).expandedProperty().set(states[i]));
    }

    @Override
    public String getState() {
        return getChildren().stream().map(pane -> ((TitledPane)pane).expandedProperty().getValue().toString()).collect(Collectors.joining("|"));     
    }
       
        
       
    
}
