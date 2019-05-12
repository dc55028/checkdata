package org.dcomte.poc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldConfigurationTest {
    private FieldConfiguration fc;

    @BeforeEach
    void setUp() {
        fc = new FieldConfiguration();
    }

    @Test
    void testIncorrectFieldNameInGetter() {
        Assertions.assertThrows(NoSuchMethodException.class, () -> fc.dynamicGetter("test"));
    }

    @Test
    void testIncorrectFieldNameInSetter() {
        Assertions.assertThrows(NoSuchMethodException.class, () -> fc.dynamicSetter("test", true));
    }

    @Test
    void setDynamicValue() throws NoSuchMethodException {
        assertFalse(fc.getField1());
        fc.dynamicSetter("Field1", true);
        assertTrue(fc.getField1());
    }

    @Test
    void getDynamicValue() throws NoSuchMethodException {
        assertFalse(fc.getField1());
        assertFalse(fc.dynamicGetter("Field1"));
        fc.setField2(true);
        assertTrue(fc.getField2());
        assertTrue(fc.dynamicGetter("Field2"));
    }
}
