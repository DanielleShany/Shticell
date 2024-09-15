package com.shticell.ui.jfx.sheet;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.main.UIModel;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SheetGridManager {

    private MainController mainController;
    private ContextMenuFactory contextMenuFactory;
    private GridPane sheetGridPane;
    private UIModel uiModel;
    private Engine engine;
    private Map<String, Label> cellIDtoLabel;

    public SheetGridManager(GridPane sheetGridPane, UIModel uiModel, Engine engine, MainController mainController) {
        this.sheetGridPane = sheetGridPane;
        this.sheetGridPane.getStylesheets().add(getClass().getResource("sheet1.css").toExternalForm());
        this.uiModel = uiModel;
        this.engine = engine;
        this.cellIDtoLabel = new HashMap<String, Label>();
        this.mainController = mainController;
        this.contextMenuFactory = new ContextMenuFactory(this);
    }

    public void createSheetGridPane(SheetDTO sheet) {
        sheetGridPane.getChildren().clear();
        sheetGridPane.getColumnConstraints().clear();
        sheetGridPane.getRowConstraints().clear();
//        sheetGridPane.addColumn(0);
//        sheetGridPane.addRow(0);

        int numRows = sheet.getProperties().getNumRows();
        int numColumns = sheet.getProperties().getNumCols();
        int rowHeight = sheet.getProperties().getRowHeight();
        int colWidth = sheet.getProperties().getColWidth();

        addColumnAndRowConstraints(numColumns, colWidth, numRows, rowHeight);
        addColumnsAndRowHeaders(numColumns, colWidth, numRows, rowHeight);
        uiModel.initializePropertiesForEachCell(sheetGridPane);
        populateSheetGridPane(sheet, numColumns, colWidth, numRows, rowHeight);
    }


    private void addColumnAndRowConstraints(int numColumns, int colWidth, int numRows, int rowHeight) {
        // Constraints for columns and rows
        for (int i = 0; i <= numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(colWidth); // width of each column
            sheetGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i <= numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowHeight); // height of each row
            sheetGridPane.getRowConstraints().add(rowConst);
        }
        sheetGridPane.getColumnConstraints().get(0).setPrefWidth(20);
    }

    private void addColumnsAndRowHeaders(int numColumns, int colWidth, int numRows, int rowHeight) {
        // Adding column headers (A, B, C, ...)
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if (col != 0) {
                final int columnIndex = col;
                label.setOnContextMenuRequested(event -> {
                    ContextMenu contextMenu = contextMenuFactory.createColumnContextMenu(columnIndex);
                    contextMenu.show(label, event.getScreenX(), event.getScreenY());
                });
                label.setText(colLabel);
            }
            label.setPrefWidth(colWidth);
            label.getStyleClass().add("header");
            sheetGridPane.add(label, col, 0); // Adding to the first row (row 0)
        }

        // Adding row headers (1, 2, 3, ...)
        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.setPrefWidth(20);
            label.getStyleClass().add("header");
            final int rowIndex = row;
            label.setOnContextMenuRequested(event -> {
                ContextMenu contextMenu = contextMenuFactory.createRowContextMenu(rowIndex);
                contextMenu.show(label, event.getScreenX(), event.getScreenY());
            });
            sheetGridPane.add(label, 0, row); // Adding to the first column (column 0)
        }
    }

    private void populateSheetGridPane(SheetDTO sheet, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numColumns; col++) {
                String cellID = CoordinateFormatter.indexToCellId(row - 1, col - 1);
                StringProperty currentCellProperty = uiModel.cellIdProperty(cellID);
                Label label = new Label();
                label.textProperty().bind(currentCellProperty);
                CellDTO cellDTO = sheet.getCell(row - 1, col - 1);
                if (cellDTO != null) {
                    currentCellProperty.set(cellDTO.getEffectiveValue().toString());
                }
                cellIDtoLabel.put(cellID, label);
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(colWidth);
                label.getStyleClass().add("cell");
                ContextMenu contextMenu = contextMenuFactory.createCellContextMenu(label);
                label.setContextMenu(contextMenu);
                mainController.addMouseClickEventForCell(cellID, label);
                sheetGridPane.add(label, col, row);
            }
        }
    }

    public String getColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index > 0) {
            index--; // Decrease index to make it 0-based
            columnName.insert(0, (char) ('A' + (index % 26)));
            index = index / 26;
        }
        return columnName.toString();
    }

    public void highlightDependenciesAndInfluences(CellDTO cellDTO) {
        if (cellDTO != null) {
            for (CellDTO dependencyCell : cellDTO.getDependsOn()) {
                String dependencyCellId = CoordinateFormatter.indexToCellId(dependencyCell.getCoordinate().getRow(),
                        dependencyCell.getCoordinate().getColumn());
                Label dependencyLabel = cellIDtoLabel.get(dependencyCellId);
                if (dependencyLabel != null) {
                    dependencyLabel.getStyleClass().add("dependency-cell");
                }
            }
            for (CellDTO influencedCell : cellDTO.getInfluencingOn()) {
                String influencedCellId = CoordinateFormatter.indexToCellId(influencedCell.getCoordinate().getRow(),
                        influencedCell.getCoordinate().getColumn());
                Label influencedLabel = cellIDtoLabel.get(influencedCellId);
                if (influencedLabel != null) {
                    influencedLabel.getStyleClass().add("influence-cell");
                }
            }
        }
    }

    public void resetCellBorders() {
        for (Label label : cellIDtoLabel.values()) {
            label.getStyleClass().removeAll("dependency-cell", "influence-cell");
        }
    }


    public void setColumnAlignment(int columnIndex, Pos alignment) {
        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeColumnIndex = GridPane.getColumnIndex(node);
            Integer nodeRowIndex = GridPane.getRowIndex(node);
            if (nodeColumnIndex != null && nodeColumnIndex == columnIndex
                    && node instanceof Label
                && nodeRowIndex>0&& nodeRowIndex!=null) {
                Label label = (Label) node;
                label.setAlignment(alignment);
            }
        }
    }

    public void setColumnWidth(int columnIndex, double width) {
        // Ensure the column constraints list is large enough
        while (sheetGridPane.getColumnConstraints().size() <= columnIndex) {
            sheetGridPane.getColumnConstraints().add(new ColumnConstraints());
        }

        // Get the constraint for the specific column
        ColumnConstraints columnConstraints = sheetGridPane.getColumnConstraints().get(columnIndex);

        // Set the width
        columnConstraints.setPrefWidth(width);
        columnConstraints.setMinWidth(width);
        columnConstraints.setMaxWidth(width);

        // Update the width of all cells in this column
        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeColumnIndex = GridPane.getColumnIndex(node);
            if (nodeColumnIndex != null && nodeColumnIndex == columnIndex) {
                if (node instanceof Region) {
                    Region region = (Region) node;
                    region.setPrefWidth(width);
                    region.setMaxWidth(width);
                }
            }
        }

        // Refresh the layout
        sheetGridPane.requestLayout();
    }

    public void setRowHeight(int rowIndex, double height) {
        // Ensure the row constraints list is large enough
        while (sheetGridPane.getRowConstraints().size() <= rowIndex) {
            sheetGridPane.getRowConstraints().add(new RowConstraints());
        }

        // Get the constraint for the specific row
        RowConstraints rowConstraints = sheetGridPane.getRowConstraints().get(rowIndex);

        // Set the height
        rowConstraints.setPrefHeight(height);
        rowConstraints.setMinHeight(height);
        rowConstraints.setMaxHeight(height);

        // Update the height of all cells in this row
        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeRowIndex = GridPane.getRowIndex(node);
            if (nodeRowIndex != null && nodeRowIndex == rowIndex) {
                if (node instanceof Region) {
                    Region region = (Region) node;
                    region.setPrefHeight(height);
                    region.setMaxHeight(height);
                }
            }
        }

        // Refresh the layout
        sheetGridPane.requestLayout();
    }


    public void setSheetStyle(int styleNumber) {
        String stylesheet = String.format("sheet%d.css", styleNumber);
        this.sheetGridPane.getStylesheets().clear();
        this.sheetGridPane.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
    }
}


