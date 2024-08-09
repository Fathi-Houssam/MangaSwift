package com.fathihoussam.mangaslay.MangaClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fathihoussam.mangaslay.ConfigFiles.VolumesDeserializer;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregatedResponse {

    @JsonProperty("volumes")
    @JsonDeserialize(using = VolumesDeserializer.class)
    private Map<String, Volume> volumes;

    public Map<String, Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(Map<String, Volume> volumes) {
        this.volumes = volumes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Volume {
        @JsonProperty("chapters")
        private Map<String, Chapter> chapters;

        public Map<String, Chapter> getChapters() {
            return chapters;
        }

        public void setChapters(Map<String, Chapter> chapters) {
            this.chapters = chapters;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Chapter {
        @JsonProperty("chapter")
        private String chapter;

        public String getChapter() {
            return chapter;
        }

        public void setChapter(String chapter) {
            this.chapter = chapter;
        }
    }
}
