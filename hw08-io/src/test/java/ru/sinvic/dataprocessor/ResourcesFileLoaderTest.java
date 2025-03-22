package ru.sinvic.dataprocessor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.sinvic.model.Measurement;

class ResourcesFileLoaderTest {

    @Test
    void testResourcesFileLoaderTest() {
        var inputDataFileName = "inputData.json";
        ResourcesFileLoader resourcesFileLoader = new ResourcesFileLoader(inputDataFileName);
        assertDoesNotThrow(resourcesFileLoader::load);

        List<Measurement> load = resourcesFileLoader.load();
        assertEquals(load.size(), 9);
        assertEquals(load.getFirst(), new Measurement("val1", 0.0));
    }
}
