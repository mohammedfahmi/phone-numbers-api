package com.jumia.phonenumbersapi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class JsonTestUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writer();

    private static JsonNode getJsonTestCaseNode(String jsonFile, String testCase) throws IOException {
        JsonNode jsonNode = mapper.readTree(Paths.get(".","src","test","resources", jsonFile).toFile());
        return jsonNode.get(testCase);
    }
    public static Object readJsonNodeValue (String jsonFile, String testCase, TypeReference<?> typeReference) throws IOException {
        ObjectReader reader =mapper.readerFor(typeReference);
        return reader.readValue(getJsonTestCaseNode(jsonFile, testCase));
    }
    public static String readJsonNodeValueAsString(String jsonFile, String testCase) throws IOException {
        return getJsonTestCaseNode(jsonFile, testCase).toString();
    }
    public static Object readJsonNodePropertyValue(String jsonFile, String testCase, String node, TypeReference<?> typeReference) throws IOException {
        ObjectReader reader =mapper.readerFor(typeReference);
        return reader.readValue(getJsonTestCaseNode(jsonFile, testCase).get(node));
    }
    public static String readJsonNodePropertyValueAsString(String jsonFile, String testCase, String node) throws IOException {
        return getJsonTestCaseNode(jsonFile, testCase).get(node).toString();
    }

    public static  String objectToJson(Object object) throws JsonProcessingException {
        return writer.writeValueAsString(object);
    }
}
