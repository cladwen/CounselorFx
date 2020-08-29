/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.infopane;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author serguei
 */
public class ProductModel {
    
    private final StringProperty name;
    
    private final IntegerProperty total;
    private final IntegerProperty production;
    private final IntegerProperty stored;
    private final IntegerProperty sell;
    
    public ProductModel(String name, int total, int production, int stored, int sell) {
        this.name = new SimpleStringProperty(name);
        this.total = new SimpleIntegerProperty(total);
        this.production = new SimpleIntegerProperty(production);
        this.stored = new SimpleIntegerProperty(stored);
        this.sell = new SimpleIntegerProperty(sell);
    }

    public String getName() {
        return name.get();
    }

    public Integer getStored() {
        return stored.get();
    }

    public Integer getTotal() {
        return total.get();
    }

    public Integer getSell() {
        return sell.get();
    }

    public Integer getProduction() {
        return production.get();
    }
    
    
}
