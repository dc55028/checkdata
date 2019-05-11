package org.dcomte.poc;

import lombok.Data;
import lombok.NonNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
abstract class FieldConfiguration {
    Boolean field1 = false;
    Boolean field2 = false;
    Boolean field3 = false;
    Boolean  field4 = false;
    Boolean  field5 = false;
    Boolean  field6 = false;
    Boolean  field7 = false;
    Boolean  lastField = false;

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
