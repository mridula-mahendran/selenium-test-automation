package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {

    /**
     * This part reads a specific row from a given sheet in an Excel file.
     * Returns a Map where keys are column headers and values are cell values.
     *
     * @param filePath  - path to the Excel file
     * @param sheetName - name of the sheet to read
     * @param rowNumber - row number to read (0-based, excluding header)
     * @return Map of column header to cell value
     */
    public static Map<String, String> getRowData(String filePath, String sheetName, int rowNumber) {
        Map<String, String> data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            // First row is the header row
            Row headerRow = sheet.getRow(0);
            // Data row (add 1 to skip header)
            Row dataRow = sheet.getRow(rowNumber + 1);

            if (headerRow == null || dataRow == null) {
                return data;
            }

            // Loop through each column, map header to cell value
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell headerCell = headerRow.getCell(i);
                Cell dataCell = dataRow.getCell(i);

                String header = getCellValueAsString(headerCell);
                String value = getCellValueAsString(dataCell);

                data.put(header, value);
            }

        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    /**
     * This part gets the total number of data rows in a sheet (excluding the header row).
     *
     * @param filePath  - path to the Excel file
     * @param sheetName - name of the sheet
     * @return number of data rows
     */
    public static int getRowCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            return sheet.getLastRowNum(); // excludes header since header is row 0

        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This part converts a cell value to a String regardless of the cell type.
     * Handles STRING, NUMERIC, BOOLEAN, and BLANK cell types.
     *
     * @param cell - the Excel cell to read
     * @return String value of the cell
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Avoid converting whole numbers to decimal format (e.g., 10.0 -> 10)
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((int) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}