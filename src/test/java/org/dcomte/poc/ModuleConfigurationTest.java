package org.dcomte.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ModuleConfigurationTest {
    String configFileName;
    File conf;

    @BeforeEach
    void setUp() {
        configFileName = "CheckData.xlsx";
        conf = new File(ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName).getFile());
        assertTrue(conf.exists());
    }

    @Test
    void testModule1() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration("MODULE1", conf);
        assertNotNull(inst);
        assertEquals("Y", inst.getField1());
        assertEquals("Y", inst.getField2());
        assertEquals("Y", inst.getField3());
        assertEquals("N", inst.getField4());
        assertEquals("N", inst.getField5());
        assertEquals("N", inst.getField6());
        assertEquals("N", inst.getField7());
        assertEquals("N", inst.getLastField());
    }

    @Test
    void testModule2() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration("MODULE2", conf);
        assertNotNull(inst);
        assertEquals("N", inst.getField1());
        assertEquals("N", inst.getField2());
        assertEquals("N", inst.getField3());
        assertEquals("N", inst.getField4());
        assertEquals("N", inst.getField5());
        assertEquals("Y", inst.getField6());
        assertEquals("N", inst.getField7());
        assertEquals("Y", inst.getLastField());
    }
}