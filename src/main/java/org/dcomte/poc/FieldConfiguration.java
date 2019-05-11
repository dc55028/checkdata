package org.dcomte.poc;

import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public abstract class FieldConfiguration {
    String field1 = "N";
    String field2 = "N";
    String field3 = "N";
    String field4 = "N";
    String field5 = "N";
    String field6 = "N";
    String field7 = "N";
    String lastField = "N";

    abstract void readConfiguration() throws IOException, NoSuchMethodException;

    @SuppressWarnings("unchecked")
    protected void dynamicSetValue(@NonNull String fieldName, Object fieldValue) throws NoSuchMethodException {
        Class aClass = this.getClass();
        Class[] paramTypes = new Class[1];
        paramTypes[0] = fieldValue.getClass(); // get the actual param type

        String methodName = "set" + fieldName; // fieldName String
        Method m;
        try {
            m = aClass.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException nsme) {
            throw new NoSuchMethodException("Invalid field '" + fieldName + "' found in configuration file");
        }

        try {
            m.invoke(this, fieldValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
