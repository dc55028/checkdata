package org.dcomte.poc;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
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
        assertThrows(NotOfficeXmlFileException.class, () -> {
            try (
                    FileInputStream is = new FileInputStream(conf);
                    Workbook wb = new XSSFWorkbook(is)
            ) {
                @SuppressWarnings("unused")
                Sheet sheet = wb.getSheet("NotFound");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            ModuleConfiguration inst = new ModuleConfiguration(new File("something"), "MODULE1", "LEASECHG", "IT");
        });
    }

    @Test
    void testIncorrectModuleName() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            ModuleConfiguration inst = new ModuleConfiguration(conf, "Common", "LEASECHG", "IT");
        });
    }

    @Test
    void testFieldNoEventFilterNoCountryFilter() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration(conf, "MODULE1", "LEASECHG", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterNoCountryFilter() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration(conf, "MODULE1", "ASSET_DEP", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertTrue(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterCountryFilter() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration(conf, "MODULE1", "ASSET_THEFT", "IT");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertTrue(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldNoEventFilterCountryFilter() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration(conf, "MODULE1", "ASSET_THEFT", "UK");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        inst = new ModuleConfiguration(conf, "MODULE1", "ASSET_THEFT", "SP");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());

    }

    @Test
    void testWeirdValues() throws IOException, NoSuchMethodException {
        ModuleConfiguration inst = new ModuleConfiguration(conf, "MODULE2", "LEASECHG", "UK");
        assertNotNull(inst);
        assertFalse(inst.getField1());
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        assertFalse(inst.getField6());
    }
}