package org.dcomte.poc;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Data
abstract class FieldConfiguration {
    Boolean field1 = false;
    Boolean field2 = false;
    Boolean field3 = false;
    Boolean field4 = false;
    Boolean field5 = false;
    Boolean field6 = false;
    Boolean field7 = false;
    Boolean lastField = false;

    @SuppressWarnings("unchecked")
    protected void setDynamicValue(@NonNull String fieldName, @NonNull Object fieldValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class aClass = this.getClass();
        Class[] paramTypes = new Class[1];
        paramTypes[0] = fieldValue.getClass(); // get the actual param type

        String methodName = "set" + fieldName; // fieldName String
        Method m;
        m = aClass.getMethod(methodName, paramTypes);
        m.invoke(this, fieldValue);
    }

    protected Boolean getDynamicValue(String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class aClass = this.getClass();
        Class[] paramTypes = new Class[1];
        paramTypes[0] = Boolean.class; // get the actual param type

        String methodName = "get" + fieldName;
        Method m;
        m = aClass.getMethod(methodName, paramTypes);
        return (Boolean) m.invoke(this);
    }
}
