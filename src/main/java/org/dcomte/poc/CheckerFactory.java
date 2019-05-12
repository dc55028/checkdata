package org.dcomte.poc;

import lombok.NonNull;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

class CheckerFactory {
    private final File configurationFile;
    private final FieldCheckerConfiguration commonTotal;
    private final FieldCheckerConfiguration commonDetail;

    CheckerFactory(File configFile) throws NoSuchMethodException, IOException, InvalidFormatException {
        File tmpConfigFile = null;
        if(!configFile.exists()) {
            System.err.println(configFile.getAbsolutePath() + " not found");
        }
        //check that it's a valid workbook
        if(WorkbookFactory.create(configFile) != null) {
            tmpConfigFile = configFile;
        }

        configurationFile = tmpConfigFile;
        if(configurationFile != null) {
            commonTotal = readCommonConfiguration("T");
            commonDetail = readCommonConfiguration("D");
        } else {
            commonTotal = null;
            commonDetail = null;
        }
    }

    private FieldCheckerConfiguration readCommonConfiguration(String totalOrDetails) throws NoSuchMethodException {
        if(!"T".equals(totalOrDetails) && !"D".equals(totalOrDetails)) {
            throw new IllegalArgumentException("Invalid argument value: " + totalOrDetails);
        }
        FieldCheckerConfiguration checker = new FieldCheckerConfiguration();
        try (
            Workbook wb = WorkbookFactory.create(configurationFile)
        ) {
            wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            Sheet sheet = wb.getSheet("Common");
            int valueColumnIndex;
            String valueColumnTitle;
            if("T".equals(totalOrDetails)) {
                valueColumnIndex = 1;
                valueColumnTitle = "Total";
            } else {
                valueColumnIndex = 2;
                valueColumnTitle = "Detail";
            }
            for (Row row : sheet) {
                if (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK) {
                    System.out.println("rownum: " + row.getRowNum());
                    if (row.getRowNum() == 0) {
                        assert ("Field Name".equals(row.getCell(0).getStringCellValue()));
                        assert (valueColumnTitle.equals(row.getCell(valueColumnIndex).getStringCellValue()));
                    } else {
                        String fieldName = row.getCell(0).getStringCellValue();
                        if (!"".equals(fieldName)) {
                            dynamicSetter(checker, fieldName, getCellBooleanValue(row.getCell(valueColumnIndex)));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return checker;
    }

    private void dynamicSetter(Object target, @NonNull String fieldName, @NonNull Object fieldValue) throws NoSuchMethodException {
        Class aClass = target.getClass();
        Class[] paramTypes = new Class[1];
        paramTypes[0] = fieldValue.getClass(); // get the actual param type

        String methodName = "set" + fieldName; // fieldName String
        @SuppressWarnings("unchecked")
        Method m = aClass.getMethod(methodName, paramTypes);

        try {
            m.invoke(target, fieldValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            //ignored
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

    public FieldCheckerConfiguration getChecker(String moduleName, String eventCode, String countryCode, String totalOrDetails) {
        FieldCheckerConfiguration checker;
        if("Common".equalsIgnoreCase(moduleName)) {
            throw new IllegalArgumentException("Incorrect module name: " + moduleName);
        }
        switch (totalOrDetails) {
            case "T":
                checker = commonTotal;
                break;
            case "D":
                checker = commonDetail;
                break;
            default:
                throw new IllegalArgumentException("Illegal argument : " + totalOrDetails);
        }
        try (
            Workbook wb = WorkbookFactory.create(configurationFile)
        ) {
            wb.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            Sheet sheet = wb.getSheet(moduleName);
            wb.setActiveSheet(wb.getSheetIndex(moduleName));
            System.out.println("Sheet " + wb.getSheetName(wb.getActiveSheetIndex()));
            int eventColumn = 1;
            int countryColumn = 2;
            int valueColumn;
            String valueColumnTitle;
            if("T".equals(totalOrDetails)) {
                valueColumn = 3;
                valueColumnTitle = "Total";
            } else {
                valueColumn = 4;
                valueColumnTitle = "Detail";
            }
            System.out.println("Using data column " + valueColumn + " (" + valueColumnTitle + ")");
            // read fields configuration
            for (Row row : sheet) {
                if (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK) {
                    System.out.println("rownum: " + row.getRowNum());
                    if (row.getRowNum() == 0) {
                        assert ("Field Name".equals(row.getCell(0).getStringCellValue()));
                        assert ("Event Code".equals(row.getCell(1).getStringCellValue()));
                        assert ("Country".equals(row.getCell(2).getStringCellValue()));
                        assert (valueColumnTitle.equals(row.getCell(valueColumn).getStringCellValue()));
                    } else {
                        String fieldName = row.getCell(0).getStringCellValue();
                        if (!"".equals(fieldName)) {
                            Cell eventCell = row.getCell(eventColumn);
                            String rowEventCode = Objects.nonNull(eventCell) ? eventCell.getStringCellValue() : "";
                            Cell countryCell = row.getCell(countryColumn);
                            String rowCountryCode = Objects.nonNull(countryCell) ? countryCell.getStringCellValue() : "";
                            if (("".equals(rowEventCode) || Objects.equals(rowEventCode, eventCode)) && ("".equals(rowCountryCode) || Objects.equals(rowCountryCode, countryCode))) {
                                System.out.println("Reading field value for " + fieldName + ", filters match: " +
                                        "event: " + rowEventCode + "/" + eventCode + " " +
                                        "country: " + rowCountryCode + "/" + countryCode + " "
                                );
                                Boolean fieldValue = getCellBooleanValue(row.getCell(valueColumn));
                                System.out.println("got value: " + fieldValue);
                                dynamicSetter(checker, fieldName, fieldValue);
                            } else {
                                System.out.println("Field " + fieldName + " characteristics do not match: " +
                                        "event: " + rowEventCode + "/" + eventCode + " " +
                                        "country: " + rowCountryCode + "/" + countryCode + " "
                                );
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return checker;
    }
}
