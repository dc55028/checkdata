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
        assertEquals("Y", inst.getField1());
        assertEquals("N", inst.getField2());
        assertEquals("N", inst.getField3());
        assertEquals("N", inst.getField4());
        assertEquals("N", inst.getField5());
        assertEquals("N", inst.getField6());
        assertEquals("N", inst.getField7());
        assertEquals("N", inst.getLastField());
    }
}