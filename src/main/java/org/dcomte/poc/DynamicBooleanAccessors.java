package org.dcomte.poc;

import lombok.NonNull;

@SuppressWarnings("unused")
interface DynamicBooleanAccessors {
    void dynamicSetter(@NonNull String fieldName, @NonNull Boolean fieldValue) throws NoSuchMethodException;
    Boolean dynamicGetter(String fieldName) throws NoSuchMethodException;
}
