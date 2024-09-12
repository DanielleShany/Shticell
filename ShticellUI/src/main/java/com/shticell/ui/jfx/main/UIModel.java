package com.shticell.ui.jfx.main;

import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class UIModel {

    private final StringProperty fullPath;
    private final StringProperty name;
    private final BooleanProperty isFileSelected;
    private final IntegerProperty columnWidth;
    private final IntegerProperty rowHeight;
    private IntegerProperty selectedCellVersion;
    private Map<String,StringProperty> cellIdtoCellValue;
    private StringProperty selectedCellId;
    private StringProperty selectedCellOriginalValue;

    public UIModel(Label fileFullPathLabel, Tab sheetNameTab, Button updateSelectedCellValueButton, GridPane sheetGridPane,
                   Label currentCellLabel, TextField selectedCellOriginalValueTextField, Label lastVersionUpdateLabel, AnchorPane versionSelectorComponent) {
        this.fullPath = new SimpleStringProperty( );
        this.name = new SimpleStringProperty( );
        this.isFileSelected = new SimpleBooleanProperty(false );
        this.columnWidth = new SimpleIntegerProperty( );
        this.rowHeight = new SimpleIntegerProperty( );
        this.selectedCellId = new SimpleStringProperty( );
        this.selectedCellOriginalValue = new SimpleStringProperty( );
        this.selectedCellVersion = new SimpleIntegerProperty();
        fileFullPathLabel.textProperty().bind( this.fullPath );
        sheetNameTab.textProperty().bind( this.name );
        updateSelectedCellValueButton.disableProperty().bind( this.isFileSelected.not());
        versionSelectorComponent.disableProperty().bind( this.isFileSelected.not() );
        currentCellLabel.textProperty().bind( this.selectedCellId );
        selectedCellOriginalValueTextField.textProperty().bindBidirectional( this.selectedCellOriginalValue );
        lastVersionUpdateLabel.textProperty().bind(this.selectedCellVersion.asString());

    }

    public void initializePropertiesForEachCell(GridPane sheetGridPane) {
        cellIdtoCellValue = new HashMap<>();
        for (int row = 0; row < sheetGridPane.getRowCount(); row++) {
            for (int col = 0; col < sheetGridPane.getColumnCount() ; col++) {
                // Create StringProperty for this cell
                String cellID = CoordinateFormatter.indexToCellId(row,col);
                cellIdtoCellValue.put(cellID,new SimpleStringProperty(""));
            }
        }
    }

    public IntegerProperty selectedCellVersionProperty() {
        return this.selectedCellVersion;
    }

    public StringProperty selectedCellOriginalValueProperty() {
        return this.selectedCellOriginalValue;
    }

    public StringProperty selectedCellIdProperty() {
        return this.selectedCellId;
    }

    public StringProperty cellIdProperty(String cellId) {
        return cellIdtoCellValue.get(cellId);
    }

    public StringProperty fullPathProperty( ) {
        return this.fullPath;
    }
    public StringProperty nameProperty( ) {
        return this.name;
    }
    public BooleanProperty isFileSelectedProperty( ) {
        return this.isFileSelected;
    }
    public IntegerProperty columnWidthProperty( ) {
        return this.columnWidth;
    }
    public IntegerProperty rowHeightProperty( ) {
        return this.rowHeight;
    }

}
