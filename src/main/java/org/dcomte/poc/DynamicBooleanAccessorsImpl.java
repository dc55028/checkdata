package org.dcomte.poc;

import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class DynamicBooleanAccessorsImpl implements DynamicBooleanAccessors {
    @SuppressWarnings("unchecked")
    public void dynamicSetter(@NonNull String fieldName, @NonNull Boolean fieldValue) throws NoSuchMethodException {
        Class aClass = this.getClass();
        Class[] paramTypes = new Class[1];
        paramTypes[0] = fieldValue.getClass(); // get the actual param type

        String methodName = "set" + fieldName; // fieldName String
        Method m = aClass.getMethod(methodName, paramTypes);

        try {
            m.invoke(this, fieldValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            //ignored
        }
    }

    @SuppressWarnings("unchecked")
    public Boolean dynamicGetter(String fieldName) throws NoSuchMethodException {
        Class aClass = this.getClass();

        String methodName = "get" + fieldName;
        Method m = aClass.getMethod(methodName);
        try {
            return (Boolean) m.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            //ignored
            return false;
        }
    }
}
