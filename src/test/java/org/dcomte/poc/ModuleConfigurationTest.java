package org.dcomte.poc;

import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.FileNotFoundException;
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
    void testIncorrectFileThrowsException() {
        configFileName = "incorrectWorkbook.xlsx";
        URL resource = ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName);
        assertNotNull(resource);
        conf = new File(resource.getFile());
        assertTrue(conf.exists());
        assertThrows(NotOLE2FileException.class, () -> WorkbookFactory.create(conf));
    }

    @Test
    void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(new File("something"), "MODULE1", "LEASECHG", "IT");
        });
    }

    @Test
    void testIncorrectModuleName() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "Common", "LEASECHG", "IT");
        });
    }

    @Test
    void testFieldNoEventFilterNoCountryFilter() throws IOException, NoSuchMethodException {
        ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "MODULE1", "LEASECHG", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterNoCountryFilter() throws IOException, NoSuchMethodException {
        ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "MODULE1", "ASSET_DEP", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertTrue(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterCountryFilter() throws IOException, NoSuchMethodException {
        ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "MODULE1", "ASSET_THEFT", "IT");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertTrue(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldNoEventFilterCountryFilter() throws IOException, NoSuchMethodException {
        ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "MODULE1", "ASSET_THEFT", "UK");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        inst = new ModuleCheckerConfiguration(conf, "MODULE1", "ASSET_THEFT", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());

    }

    @Test
    void testWeirdValues() throws IOException, NoSuchMethodException {
        ModuleCheckerConfiguration inst = new ModuleCheckerConfiguration(conf, "MODULE2", "LEASECHG", "UK");
        assertNotNull(inst);
        assertFalse(inst.getField1());
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        assertFalse(inst.getField6());
    }
}