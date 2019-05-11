package org.dcomte.poc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Data
@EqualsAndHashCode
public class CommonConfiguration extends FieldConfiguration {

    protected final File configurationFile;
    protected final String eventCode;
    protected String sheetName;

    public CommonConfiguration(File file, String sheetName) throws IOException, NoSuchMethodException {
        configurationFile = file;
        this.sheetName = sheetName;
        eventCode = null;
        readConfiguration();
    }

    public void readConfiguration() throws IOException, NoSuchMethodException {
        try (
                FileInputStream is = new FileInputStream(configurationFile);
                Workbook wb = new XSSFWorkbook(is)
        ) {
            Sheet sheet = wb.getSheet(sheetName);
            System.out.println(sheet.getSheetName());
            int valueColumnIndex = -1;
            int eventColumnIndex = -1;
            for (Row row : sheet) {
                if (!"".equals(row.getCell(0).getStringCellValue())) {
                    System.out.println("rownum: " + row.getRowNum());
                    if (row.getRowNum() == 0) {
                        assert ("Field Name".equals(row.getCell(0).getStringCellValue()));

                        if ("Common".equals(sheetName)) {
                            assert ("Mandatory".equals(row.getCell(1).getStringCellValue()));
                            valueColumnIndex = 1;
                        } else {
                            assert ("Mandatory".equals(row.getCell(2).getStringCellValue()));
                            assert ("Event Code".equals(row.getCell(1).getStringCellValue()));
                            eventColumnIndex = 1;
                            valueColumnIndex = 2;
                        }
                        assert (valueColumnIndex != -1);
                    } else {
                        for (Cell cell : row) {
                            if (!"".equals(cell.getStringCellValue())) {
                                System.out.println(cell.getColumnIndex() + ":" + cell);
                            }
                        }

                        String fieldName = row.getCell(0).getStringCellValue();
                        String eventCode = null;
                        if (!"".equals(fieldName)) {
                            if (eventColumnIndex != -1) {
                                Cell eventCell = row.getCell(eventColumnIndex);
                                if (eventCell != null) {
                                    eventCode = eventCell.getStringCellValue();
                                }
                            }
                            Cell cell = row.getCell(valueColumnIndex);
                            Object fieldValue;
                            switch (cell.getCellTypeEnum()) {
                                case NUMERIC:
                                    fieldValue = cell.getNumericCellValue();
                                    if (fieldValue.equals(1)) {
                                        fieldValue = true;
                                    } else if (fieldValue.equals(0)) {
                                        fieldValue = false;
                                    } else {
                                        System.err.println("Unexpected value: '" + fieldValue + "', defaulting to FALSE");
                                        fieldValue = false;
                                    }
                                    break;
                                case BOOLEAN:
                                    fieldValue = cell.getBooleanCellValue();
                                    break;
                                case STRING:
                                    fieldValue = cell.getStringCellValue();
                                    if ("Y".equals(fieldValue)) {
                                        fieldValue = true;
                                    } else if ("N".equals(fieldValue)) {
                                        fieldValue = false;
                                    } else {
                                        System.err.println("Unexpected value: '" + fieldValue + "', defaulting to FALSE");
                                        fieldValue = false;
                                    }
                                    break;
                                default:
                                    /* default cell value to false if it's an unexpected value */
                                    fieldValue = false;
                            }
                            if (Objects.equals(eventCode, this.eventCode)) {
                                setDynamicValue(fieldName, fieldValue);
                            }
                        }
                    }
                }
            }
        }
    }
}
