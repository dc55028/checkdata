package org.dcomte.poc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

class ModuleCheckerConfiguration extends FieldCheckerConfiguration {
    private final File configurationFile;
    private String worksheetName;
    private String eventCode;
    private String countryCode;

    ModuleCheckerConfiguration(File configFile, String moduleName, String event, String country) throws IOException, NoSuchMethodException {
        if(!configFile.exists()) {
            throw new FileNotFoundException(configFile.getAbsolutePath() + " not found");
        }
        //TODO check that it's a XSSF workbook
        configurationFile = configFile;
        if("Common".equals(moduleName)) {
            throw new IllegalArgumentException("Module name cannot be 'Common'");
        }
        //TODO check that the worksheet exists
        worksheetName = moduleName;
        eventCode = event;
        //TODO check that event code is in list
        countryCode = country;
        //TODO check that country code is in list
        readCommonConfiguration();
        readConfiguration();
    }

    private void readCommonConfiguration() throws NoSuchMethodException {
        try (
                FileInputStream is = new FileInputStream(configurationFile);
                Workbook wb = new XSSFWorkbook(is)
        ) {
            wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            Sheet sheet = wb.getSheet("Common");
            int valueColumnIndex = 1;
            for (Row row : sheet) {
                if (!"".equals(row.getCell(0).getStringCellValue())) {
                    System.out.println("rownum: " + row.getRowNum());
                    if (row.getRowNum() == 0) {
                        assert ("Field Name".equals(row.getCell(0).getStringCellValue()));
                        assert ("Total".equals(row.getCell(1).getStringCellValue()));
                    } else {
                        String fieldName = row.getCell(0).getStringCellValue();
                        if (!"".equals(fieldName)) {
                            dynamicSetter(fieldName, getCellBooleanValue(row.getCell(valueColumnIndex)));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readConfiguration() throws IOException, NoSuchMethodException {
        try (
                FileInputStream is = new FileInputStream(configurationFile);
                Workbook wb = new XSSFWorkbook(is)
        ) {
            wb.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Sheet sheet = wb.getSheet(worksheetName);
            System.out.println(sheet.getSheetName());
            int eventColumn = 1;
            int countryColumn = 2;
            int valueColumn = 3;
            assert(!"Common".equals(worksheetName));
            for (Row row : sheet) {
                if (!"".equals(row.getCell(0).getStringCellValue())) {
                    System.out.println("rownum: " + row.getRowNum());
                    if (row.getRowNum() == 0) {
                        assert ("Field Name".equals(row.getCell(0).getStringCellValue()));
                        assert ("Event Code".equals(row.getCell(eventColumn).getStringCellValue()));
                        assert ("Country".equals(row.getCell(countryColumn).getStringCellValue()));
                        assert ("Total".equals(row.getCell(valueColumn).getStringCellValue()));
                    } else {

                        String fieldName = row.getCell(0).getStringCellValue();
                        if (!"".equals(fieldName)) {
                            for (Cell cell : row) {
                                if (!"".equals(cell.toString())) {
                                    System.out.println(cell.getColumnIndex() + ":" + cell);
                                }
                            }
                            String eventCode = row.getCell(eventColumn).getStringCellValue();
                            String countryCode = row.getCell(countryColumn).getStringCellValue();
                            if (("".equals(eventCode) || Objects.equals(eventCode, this.eventCode)) && ("".equals(countryCode) || Objects.equals(countryCode, this.countryCode))) {
                                System.out.println("Reading field value, filters match: " +
                                        "event: " + eventCode + "/" + this.eventCode + " " +
                                        "country: " + countryCode + "/" + this.countryCode + " "
                                        );
                                Boolean fieldValue = getCellBooleanValue(row.getCell(valueColumn));
                                System.out.println("got value: " + fieldValue);
                                dynamicSetter(fieldName, fieldValue);
                            } else {
                                System.out.println("field characteristics do not match: " + fieldName + "," + eventCode + "," + countryCode);
                            }
                        }
                    }
                }
            }
        }
    }

    private Boolean getCellBooleanValue(Cell cell) {
        Object fieldValue;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                fieldValue = cell.getNumericCellValue();
                if (fieldValue.equals(1d)) {
                    fieldValue = true;
                } else if (fieldValue.equals(0d)) {
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
        return (Boolean) fieldValue;
    }
}
