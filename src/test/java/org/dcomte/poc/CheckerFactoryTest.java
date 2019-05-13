package org.dcomte.poc;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class CheckerFactoryTest {
    private String configFileName;
    private File conf;
    private CheckerFactory f;

    @BeforeEach
    void setUp() throws NoSuchMethodException, IOException, InvalidFormatException {
        configFileName = "CheckData.xlsx";
        URL resource = ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName);
        assertNotNull(resource);
        conf = new File(resource.getFile());
        assertTrue(conf.exists());
        f = new CheckerFactory(conf);
    }

    @Test
    void testIncorrectFileFormatThrowsException() {
        String configFileName = "incorrectWorkbook.xlsx";
        URL resource = ClassLoaderUtils.getDefaultClassLoader().getResource(configFileName);
        assertNotNull(resource);
        File conf = new File(resource.getFile());
        assertTrue(conf.exists());
        assertThrows(NotOLE2FileException.class, () -> new CheckerFactory(conf));
    }

    @Test
    void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> new CheckerFactory(new File("something")));
    }

    @Test
    void testIncorrectModuleName() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            FieldCheckerConfiguration inst = f.getChecker("Common", "LEASECHG", "IT", "T");
        });
    }

    @Test
    void getCheckerTotal() {
        FieldCheckerConfiguration checker = f.getChecker("Module1", "ASSET_DEP", "IT", "T");
        assertNotNull(checker);
        assertTrue(true);
        assertTrue(checker.getField1());
        assertTrue(checker.getField2());
    }

    @Test
    void getCheckerDetail() {
        FieldCheckerConfiguration checker = f.getChecker("Module1", "ASSET_DEP", "IT", "D");
        assertNotNull(checker);
        assertFalse(checker.getField1());
        assertTrue(checker.getField2());
    }

    @Test
    void testFieldNoEventFilterNoCountryFilter() {
        FieldCheckerConfiguration inst = f.getChecker("MODULE1", "LEASECHG", "SP", "T");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterNoCountryFilter() {
        FieldCheckerConfiguration inst = f.getChecker("MODULE1", "ASSET_DEP", "SP", "T");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertTrue(inst.getField3());
        assertFalse(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldEventFilterCountryFilter() {
        FieldCheckerConfiguration inst = f.getChecker("MODULE1", "ASSET_THEFT", "IT", "T");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertTrue(inst.getField4());
        assertFalse(inst.getField5());
    }

    @Test
    void testFieldNoEventFilterCountryFilter() {
        FieldCheckerConfiguration inst = f.getChecker("MODULE1", "ASSET_THEFT", "UK", "T");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        inst = f.getChecker("MODULE1", "ASSET_THEFT", "SP", "T");
        assertNotNull(inst);
        assertTrue(inst.getField1()); // always true
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
    }

    @Test
    void testWeirdValues() {
        FieldCheckerConfiguration inst = f.getChecker("MODULE2", "LEASECHG", "UK", "T");
        assertNotNull(inst);
        assertFalse(inst.getField1());
        assertTrue(inst.getField2());
        assertFalse(inst.getField3());
        assertFalse(inst.getField4());
        assertTrue(inst.getField5());
        assertFalse(inst.getField6());
    }
}