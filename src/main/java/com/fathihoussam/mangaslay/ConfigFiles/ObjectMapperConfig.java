package com.fathihoussam.mangaslay.ConfigFiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Map;

public class ObjectMapperConfig {
    public static ObjectMapper getCustomObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new VolumesDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
