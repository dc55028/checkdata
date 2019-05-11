package org.dcomte.poc;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CommonConfigurationTest {
    @Test
    void readConfiguration() throws IOException, NoSuchMethodException {
        String configFileName = "CheckData.xlsx";
        File conf = new File(ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName).getFile());
        assertTrue(conf.exists());
        CommonConfiguration inst = new CommonConfiguration(conf);
        assertNotNull(inst);
        assertTrue(inst.getField1());
        assertFalse(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
        assertFalse(inst.getField6());
        assertFalse(inst.getField7());
        assertFalse(inst.getLastField());
    }
}