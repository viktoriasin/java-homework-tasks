package ru.sinvic.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import ru.sinvic.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        ClassLoader classLoader = getClass().getClassLoader();
        try (var in = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(fileName)))) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(in, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException nullPointerException) {
            throw new RuntimeException("File " + fileName + " could not be found.");
        }
    }
}
