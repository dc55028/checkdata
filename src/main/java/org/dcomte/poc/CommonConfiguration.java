package org.dcomte.poc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class CommonConfiguration extends FieldConfiguration {
    private final File configurationFile;
    protected String sheetName;
    protected String eventCode;

    public CommonConfiguration(File file) throws IOException, NoSuchMethodException {
        configurationFile = file;
        sheetName = "Common";
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
                            if(eventColumnIndex != -1) {
                                Cell eventCell = row.getCell(eventColumnIndex);
                                if(eventCell != null) {
                                    eventCode = eventCell.getStringCellValue();
                                }
                            }
                            Cell cell = row.getCell(valueColumnIndex);
                            Object fieldValue;
                            switch (cell.getCellTypeEnum()) {
                                case BOOLEAN:
                                    fieldValue = cell.getBooleanCellValue();
                                    break;
                                case NUMERIC:
                                    fieldValue = cell.getNumericCellValue();
                                    break;
                                case STRING:
                                    fieldValue = cell.getStringCellValue();
                                    break;
                                default:
                                    fieldValue = "";
                            }
                            if(Objects.equals(eventCode, this.eventCode)) {
                                dynamicSetValue(fieldName, fieldValue);
                            }
                        }
                    }
                }
            }
        }
    }

}
