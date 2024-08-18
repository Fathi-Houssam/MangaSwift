package com.fathihoussam.mangaslay.ConfigFiles;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fathihoussam.mangaslay.MangaClassesDTOs.AggregatedResponse.Volume;
import com.fathihoussam.mangaslay.MangaClassesDTOs.AggregatedResponse.Chapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolumesDeserializer extends JsonDeserializer<Map<String, Volume>> {

    @Override
    public Map<String, Volume> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Map<String, Volume> volumes = new HashMap<>();
        JsonNode rootNode = p.getCodec().readTree(p);

        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String volumeKey = field.getKey();
            JsonNode volumeNode = field.getValue();

            Volume volume = new Volume();
            Map<String, Chapter> chapters = new HashMap<>();

            if (volumeNode.has("chapters")) {
                JsonNode chaptersNode = volumeNode.get("chapters");

                if (chaptersNode.isArray()) {
                    int index = 0;
                    for (JsonNode chapterNode : chaptersNode) {
                        Chapter chapter = new Chapter();
                        chapter.setChapter(chapterNode.get("chapter").asText());
                        chapters.put(String.valueOf(index++), chapter);
                    }
                } else {
                    Iterator<Map.Entry<String, JsonNode>> chapterFields = chaptersNode.fields();
                    while (chapterFields.hasNext()) {
                        Map.Entry<String, JsonNode> chapterField = chapterFields.next();
                        String chapterKey = chapterField.getKey();
                        JsonNode chapterNode = chapterField.getValue();

                        Chapter chapter = new Chapter();
                        chapter.setChapter(chapterNode.get("chapter").asText());
                        chapters.put(chapterKey, chapter);
                    }
                }
            }

            volume.setChapters(chapters);
            volumes.put(volumeKey, volume);
        }

        return volumes;
    }
}
