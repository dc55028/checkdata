package org.dcomte.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ModuleConfigurationTest {
    private String configFileName;
    private File conf;

    @BeforeEach
    void setUp() {
        configFileName = "CheckData.xlsx";
        URL resource = ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName);
        assertNotNull(resource);
        conf = new File(resource.getFile());
        assertTrue(conf.exists());
    }

    @Test
    void testModule1() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration("MODULE1", conf);
        assertNotNull(inst);
        assertTrue(inst.getField1());
        assertTrue(inst.getField2());
        assertTrue(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
        assertFalse(inst.getField6());
        assertFalse(inst.getField7());
        assertFalse(inst.getLastField());
    }

    @Test
    void testModule2() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration("MODULE2", conf);
        assertNotNull(inst);
        assertFalse(inst.getField1());
        assertFalse(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
        assertTrue(inst.getField6());
        assertFalse(inst.getField7());
        assertTrue(inst.getLastField());
    }
}