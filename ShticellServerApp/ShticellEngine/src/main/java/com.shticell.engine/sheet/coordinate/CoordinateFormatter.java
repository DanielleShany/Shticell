package com.shticell.engine.sheet.coordinate;

public class CoordinateFormatter {

    public static String indexToCellId(int row, int column) {
        String columnLabel = getColumnLabel(column);
        int rowLabel = row + 1;
        return columnLabel + rowLabel;
    }

    public static int[] cellIdToIndex(String cellId) {
        cellId = cellId.toUpperCase();
        if (!cellId.matches("^[A-Z]+\\d+$")) {
            throw new IllegalArgumentException("Invalid cell ID format: " + cellId);
        }

        int i = 0;
        while (i < cellId.length() && Character.isLetter(cellId.charAt(i))) {
            i++;
        }

        String columnPart = cellId.substring(0, i);
        String rowPart = cellId.substring(i);

        int rowIndex = Integer.parseInt(rowPart) - 1;

        int columnIndex = getColumnIndex(columnPart);

        return new int[]{rowIndex, columnIndex};
    }

    public static int getColumnIndex(String columnLabel) {
        int columnIndex = 0;

        for (int i = 0; i < columnLabel.length(); i++) {
            columnIndex *= 26;
            columnIndex += (columnLabel.charAt(i) - 'A' + 1);
        }

        return columnIndex - 1;  // Make it 0-based
    }

    private static String getColumnLabel(int columnIndex) {
        StringBuilder columnLabel = new StringBuilder();

        while (columnIndex >= 0) {
            int remainder = columnIndex % 26;
            columnLabel.insert(0, (char) ('A' + remainder));
            columnIndex = (columnIndex / 26) - 1;
        }

        return columnLabel.toString();
    }
}