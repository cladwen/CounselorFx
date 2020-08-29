/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;

import control.services.LocalConverter;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import model.Local;
import msgs.BaseMsgs;
import persistence.local.ListFactory;
import persistenceCommons.BundleManager;
import persistenceCommons.SettingsManager;

/**
 *
 * @author serguei
 */
public class InfoPaneModel {
    
    private static final BundleManager labels = SettingsManager.getInstance().getBundleManager();
    
    private ListFactory local;
    
    private StringProperty hexInfoTitle = new SimpleStringProperty();
    private StringProperty hexInfoContent = new SimpleStringProperty();
    
    
    private StringProperty cities = new SimpleStringProperty();
    private ObservableList<ProductModel> productData = FXCollections.observableArrayList();

    
    private StringProperty characters = new SimpleStringProperty();
    private StringProperty armies = new SimpleStringProperty();  
    private StringProperty artifacts = new SimpleStringProperty();
    
    public InfoPaneModel(ListFactory localFactory) {
        this.local = localFactory;
    }
    
    protected void setHex(String coordinate) {
        Local localHex = local.getLocal(coordinate);
        String hexInfo = String.format(labels.getString("TERRENO.CLIMA"), localHex.getTerreno().getNome(), BaseMsgs.localClima[localHex.getClima()]);
        hexInfoContent.setValue(hexInfo);
        hexInfoTitle.setValue("Hex " + coordinate);
        final Map<String, String> hexInfoMap = LocalConverter.getInfoMap(localHex);
        if (hexInfoMap.containsKey(labels.getString("CIDADES"))) {
            cities.setValue(hexInfoMap.get(labels.getString("CIDADES")));
        }
        
        if (hexInfoMap.containsKey(labels.getString("PERSONAGENS.LOCAL"))) {
            characters.setValue(hexInfoMap.get(labels.getString("PERSONAGENS.LOCAL")));
        } else {
            characters.setValue("");
        }
        
        if (hexInfoMap.containsKey("PRODUCTION")) {
             productData.clear();
            String listProd = hexInfoMap.get("PRODUCTION");          
            String[] prods = listProd.split("\\|");
            ProductModel productModel;                      
            
            for (int i = 1; i < prods.length; i++) {
                String[] prodItem = prods[i].split(" ");
                if (prodItem.length > 3) {
                    productModel = new ProductModel(prodItem[0], Integer.parseInt(prodItem[1]), Integer.parseInt(prodItem[2]), Integer.parseInt(prodItem[3]), Integer.parseInt(prodItem[4]));
                    productData.add(productModel);
                }                          
            }            
        }    
        
        if (hexInfoMap.containsKey("EXERCITOS")) {
            armies.setValue(hexInfoMap.get("EXERCITOS"));
        } else {
            armies.setValue("");
        }
        
        if (hexInfoMap.containsKey("ARTEFATOS")) {
            artifacts.setValue(hexInfoMap.get("ARTEFATOS"));
        } else {
            artifacts.setValue("");
        }
        
    }

    public StringProperty getHexInfoTitle() {
        return hexInfoTitle;
    }

    public StringProperty getHexInfoContent() {
        return hexInfoContent;
    }
    
    public StringProperty getCities() {
        return cities;
    }

    public StringProperty getCharacters() {
        return characters;
    }
    
    public ObservableList<ProductModel> getProductData() {
        return productData;
    }
    
    public StringProperty getArmies() {
        return armies;
    }

    public StringProperty getArtifacts() {
        return artifacts;
    }
    
}
