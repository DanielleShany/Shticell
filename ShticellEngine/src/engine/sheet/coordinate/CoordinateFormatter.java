package engine.sheet.coordinate;

public class CoordinateFormatter {

    // Converts a 0-based row and column index into a cell ID (e.g., (11, 26) -> "AA12")
    public static String indexToCellId(int row, int column) {
        String columnLabel = getColumnLabel(column);
        int rowLabel = row + 1;  // Rows are 1-based, so add 1 to the row index
        return columnLabel + rowLabel;
    }

    // Converts a cell ID (e.g., "AA12") into a 0-based row and column index
    public static int[] cellIdToIndex(String cellId) {
        // Validate the format of the cellId
        if (!cellId.matches("^[A-Z]+\\d+$")) {
            throw new IllegalArgumentException("Invalid cell ID format: " + cellId);
        }

        // Split the cellId into the column part (letters) and row part (numbers)
        int i = 0;
        while (i < cellId.length() && Character.isLetter(cellId.charAt(i))) {
            i++;
        }

        String columnPart = cellId.substring(0, i);
        String rowPart = cellId.substring(i);

        // Convert the row part to an integer and subtract 1 to make it 0-based
        int rowIndex = Integer.parseInt(rowPart) - 1;

        // Convert the column part to a 0-based column index
        int columnIndex = getColumnIndex(columnPart);

        return new int[]{rowIndex, columnIndex};
    }

    // Converts a column label (e.g., "AA") to a 0-based column index
    public static int getColumnIndex(String columnLabel) {
        int columnIndex = 0;

        for (int i = 0; i < columnLabel.length(); i++) {
            columnIndex *= 26;
            columnIndex += (columnLabel.charAt(i) - 'A' + 1);
        }

        return columnIndex - 1;  // Make it 0-based
    }

    // Converts a 0-based column index to a column label (e.g., 26 -> "AA")
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
