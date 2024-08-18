package com.fathihoussam.mangaslay.MangaClassesDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MangaResponse {
    private Data[] data;

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String id;
        private DataAttributes attributes;
        private List<Relationship> relationships;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public DataAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(DataAttributes attributes) {
            this.attributes = attributes;
        }

        public List<Relationship> getRelationships() {
            return relationships;
        }

        public void setRelationships(List<Relationship> relationships) {
            this.relationships = relationships;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DataAttributes {
            private Map<String, String> title;
            private List<Tag> tags;

            public Map<String, String> getTitle() {
                return title;
            }

            public void setTitle(Map<String, String> title) {
                this.title = title;
            }

            public List<Tag> getTags() {
                return tags;
            }

            public void setTags(List<Tag> tags) {
                this.tags = tags;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Tag {
                private TagAttributes attributes;

                public TagAttributes getAttributes() {
                    return attributes;
                }

                public void setAttributes(TagAttributes attributes) {
                    this.attributes = attributes;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class TagAttributes {
                    private Map<String, String> name;

                    public Map<String, String> getName() {
                        return name;
                    }

                    public void setName(Map<String, String> name) {
                        this.name = name;
                    }
                }
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Relationship {
            private String type;
            private RelationshipAttributes attributes;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public RelationshipAttributes getAttributes() {
                return attributes;
            }

            public void setAttributes(RelationshipAttributes attributes) {
                this.attributes = attributes;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class RelationshipAttributes {
                @JsonProperty("fileName")
                private String fileName;

                @JsonProperty("name")
                private String name;

                public String getFileName() {
                    return fileName;
                }

                public void setFileName(String fileName) {
                    this.fileName = fileName;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }
}
