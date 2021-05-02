package com.daniel.transmetrics;

import com.daniel.transmetrics.repository.entity.Holiday;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class TestUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .registerModule(new ParameterNamesModule());

    public static <T> T getFromFileAsObject(Class<T> tClass, String path) throws JsonProcessingException {
        String rawResponse = loadFileAsString(path);
        return MAPPER.readValue(rawResponse, tClass);
    }

    public static String loadFileAsString(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(path).toURI())));
        } catch (IOException | URISyntaxException | NullPointerException e) {
            throw new RuntimeException("file not found at " + path, e);
        }
    }

    public static Holiday createTestHoliday(String countryCode) {
        return Holiday.builder()
                .countryId(countryCode)
                .date(LocalDate.now())
                .id(1L)
                .name("Test holiday")
                .oneLiner("Oneliner description of the test holiday")
                .subtype(new DecimalNode(BigDecimal.ONE))
                .types(new DecimalNode(BigDecimal.ONE))
                .uid("test uid")
                .url("Test url")
                .urlId("test url ID")
                .build();
    }
}
