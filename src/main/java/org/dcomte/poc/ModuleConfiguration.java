package org.dcomte.poc;

import java.io.File;
import java.io.IOException;

class ModuleConfiguration extends CommonConfiguration {

    ModuleConfiguration(String moduleName, File file) throws IOException, NoSuchMethodException {
        super(file);
        sheetName = moduleName;
        readConfiguration();
    }
}
