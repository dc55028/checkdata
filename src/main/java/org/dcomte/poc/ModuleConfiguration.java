package org.dcomte.poc;

import java.io.File;
import java.io.IOException;

public class ModuleConfiguration extends CommonConfiguration implements ConfigurationReader {

    ModuleConfiguration(String moduleName, File file) throws IOException, NoSuchMethodException {
        super(file);
        sheetName = moduleName;
        readConfiguration();
    }
}
