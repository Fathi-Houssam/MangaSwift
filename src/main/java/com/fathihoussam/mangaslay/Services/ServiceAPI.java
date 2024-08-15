package com.fathihoussam.mangaslay.Services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fathihoussam.mangaslay.MangaClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceAPI {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ServiceAPI(WebClient.Builder builder) {
        this.webClient = builder.build();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<List<Manga>> searchResult(String searchquery) {
        String url = UriComponentsBuilder.fromUriString("https://api.mangadex.org/manga")
                .queryParam("limit", 60)
                .queryParam("title", searchquery)
                .queryParam("includes[]", "cover_art")
                .queryParam("includes[]", "author")
                .queryParam("contentRating[]", "safe")
                .queryParam("contentRating[]", "suggestive")
                .queryParam("contentRating[]", "erotica")
                .queryParam("contentRating[]", "pornographic")
                .queryParam("availableTranslatedLanguage[]","en")
                .queryParam("hasAvailableChapters","true")
                .build()
                .toUriString();

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        MangaResponse mangaResponse = objectMapper.readValue(response, MangaResponse.class);
                        List<Manga> mangas = Arrays.stream(mangaResponse.getData())
                                .map(this::mapToManga)
                                .collect(Collectors.toList());
                        return fetchLatestChapters(mangas);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse response: " + e.getMessage()));
                    }
                });
    }

    private Manga mapToManga(MangaResponse.Data data) {
        Manga manga = new Manga();
        manga.setId(data.getId());
        manga.setTitle(data.getAttributes().getTitle().get("en"));

        List<String> tags = data.getAttributes().getTags().stream()
                .map(tag -> tag.getAttributes().getName().get("en"))
                .collect(Collectors.toList());
        manga.setTags(tags);

        String coverFile = data.getRelationships().stream()
                .filter(rel -> "cover_art".equals(rel.getType()))
                .map(rel -> rel.getAttributes().getFileName())
                .findFirst()  // Only take the first cover file found
                .orElse(null);
        manga.setCoverFile(coverFile);


        String authorName = data.getRelationships().stream()
                .filter(rel -> "author".equals(rel.getType()))
                .map(rel -> rel.getAttributes().getName())
                .findFirst()
                .orElse(null);
        manga.setAuthorName(authorName);

        return manga;
    }


    private Mono<List<Manga>> fetchLatestChapters(List<Manga> mangas) {
        List<Mono<Manga>> mangaMonos = mangas.stream()
                .map(this::fetchLatestChapter)
                .collect(Collectors.toList());

        return Mono.zip(mangaMonos, objects -> Arrays.stream(objects)
                .map(object -> (Manga) object)
                .collect(Collectors.toList()));
    }

    private Mono<Manga> fetchLatestChapter(Manga manga) {
        String url = UriComponentsBuilder.fromUriString("https://api.mangadex.org/manga/{id}/aggregate")
                .queryParam("translatedLanguage[]", "en")
                .buildAndExpand(manga.getId())
                .toUriString();

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        AggregatedResponse aggregatedResponse = objectMapper.readValue(response, AggregatedResponse.class);

                        // Find the latest chapter number as a String
                        String latestChapter = aggregatedResponse.getVolumes().values().stream()
                                .flatMap(volume -> volume.getChapters().values().stream())
                                .map(chapter -> {
                                    String chapterNumber = chapter.getChapter();
                                    if (chapterNumber == null || chapterNumber.equalsIgnoreCase("none")) {
                                        return "Oneshot";
                                    }
                                    return chapterNumber;
                                })
                                .max(Comparator.comparingDouble(ch -> {
                                    try {
                                        return Double.parseDouble(ch);
                                    } catch (NumberFormatException e) {
                                        return Double.MIN_VALUE;
                                    }
                                }))
                                .orElse("Oneshot");

                        manga.setChapterNumber(latestChapter);
                        return Mono.just(manga);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse response: " + e.getMessage()));
                    }
                });
    }

    public Mono<MangaDetails> generateDetails(String mangaId) {
        String feedUrl = UriComponentsBuilder.fromUriString("https://api.mangadex.org/manga/{id}/feed")
                .queryParam("translatedLanguage[]", "en")
                .queryParam("limit", 200)
                .queryParam("includeFutureUpdates", 1)
                .queryParam("includeEmptyPages", 0)
                .queryParam("includeFuturePublishAt", 0)
                .queryParam("includeExternalUrl", 0)
                .queryParam("contentRating[]", "safe")
                .queryParam("contentRating[]", "suggestive")
                .queryParam("contentRating[]", "erotica")
                .queryParam("contentRating[]", "pornographic")
                .queryParam("order[createdAt]", "asc")
                .queryParam("order[updatedAt]", "asc")
                .queryParam("order[publishAt]", "asc")
                .queryParam("order[readableAt]", "asc")
                .queryParam("order[volume]", "asc")
                .queryParam("order[chapter]", "asc")
                .buildAndExpand(mangaId)
                .toUriString();

        String detailsUrl = UriComponentsBuilder.fromUriString("https://api.mangadex.org/manga/{id}")
                .queryParam("includes[]", "cover_art")
                .buildAndExpand(mangaId)
                .toUriString();

        Mono<List<Chapter>> chaptersMono = webClient.get().uri(feedUrl)
                .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode rootNode = objectMapper.readTree(response);
                        List<Chapter> chaptersList = new ArrayList<>();

                        JsonNode dataNode = rootNode.path("data");
                        for (JsonNode chapterNode : dataNode) {
                            JsonNode attributes = chapterNode.path("attributes");

                            Chapter chapter = new Chapter();
                            String chapterNumberStr = attributes.path("chapter").asText(null);

                            if (chapterNumberStr == null || chapterNumberStr.equalsIgnoreCase("null")) {
                                chapter.setChapterNumber(-1);
                            } else {
                                try {
                                    chapter.setChapterNumber(Float.parseFloat(chapterNumberStr));
                                } catch (NumberFormatException e) {
                                    chapter.setChapterNumber(-1);
                                }
                            }

                            chapter.setChapterName(attributes.path("title").isNull() ? "" : attributes.path("title").asText());
                            chapter.setChapterId(chapterNode.path("id").asText());
                            chapter.setReleaseDate(objectMapper.convertValue(attributes.path("publishAt"), Date.class));

                            chaptersList.add(chapter);
                        }

                        chaptersList.sort(Comparator.comparingDouble(Chapter::getChapterNumber));
                        return Mono.just(chaptersList);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse feed response: " + e.getMessage()));
                    }
                });

        Mono<MangaDetails> detailsMono = webClient.get().uri(detailsUrl)
                .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode rootNode = objectMapper.readTree(response);
                        JsonNode dataNode = rootNode.path("data").path("attributes");
                        JsonNode relationshipsNode = rootNode.path("data").path("relationships");

                        MangaDetails mangaDetails = new MangaDetails();
                        mangaDetails.setTitle(dataNode.path("title").path("en").asText());
                        mangaDetails.setDescription(dataNode.path("description").path("en").asText());
                        mangaDetails.setReleaseYear(dataNode.path("year").asInt());
                        mangaDetails.setStatus(dataNode.path("status").asText());

                        // Extract cover URL
                        for (JsonNode relNode : relationshipsNode) {
                            if (relNode.path("type").asText().equals("cover_art")) {
                                String fileName = relNode.path("attributes").path("fileName").asText();
                                mangaDetails.setCoverURL(fileName);
                                break;
                            }
                        }

                        return Mono.just(mangaDetails);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse details response: " + e.getMessage()));
                    }
                });

        return chaptersMono.zipWith(detailsMono, (chapters, details) -> {
            details.setChapters(chapters);
            return details;
        }).onErrorResume(WebClientRequestException.class, e -> {
            System.err.println("Request cancelled or connection closed prematurely: " + e.getMessage());
            return Mono.empty();});
    }


    public Mono<ChapterImage> imageGetter(String chapterUrl) {
        String firstUrl = UriComponentsBuilder.fromUriString("https://api.mangadex.org/at-home/server/{chapterUrl}")
                .buildAndExpand(chapterUrl).toUriString();

        String secondUrl = UriComponentsBuilder.fromUriString("https://api.mangadex.org/chapter/{chapterUrl}")
                .queryParam("includes[]", "manga").buildAndExpand(chapterUrl).toUriString();

        ChapterImage chapterImage = new ChapterImage();

        return webClient.get().uri(firstUrl)
                .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        JsonNode rootNode = objectMapper.readTree(response);
                        JsonNode chapterNode = rootNode.path("chapter");

                        chapterImage.setBaseUrl(rootNode.path("baseUrl").asText());
                        chapterImage.setHash(chapterNode.path("hash").asText());

                        List<String> data = new ArrayList<>();
                        for (JsonNode dataNode : chapterNode.path("data")) {
                            data.add(dataNode.asText());
                        }
                        chapterImage.setData(data);

                        List<String> dataSaver = new ArrayList<>();
                        for (JsonNode dataSaverNode : chapterNode.path("dataSaver")) {
                            dataSaver.add(dataSaverNode.asText());
                        }
                        chapterImage.setDataSaver(dataSaver);

                        return Mono.just(chapterImage);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .flatMap(chapterImg -> webClient.get().uri(secondUrl)
                        .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> {
                            try {
                                JsonNode rootNode = objectMapper.readTree(response);
                                JsonNode relationshipsNode = rootNode.path("data").path("relationships");
                                String mangaId = null;

                                for (JsonNode relationship : relationshipsNode) {
                                    if (relationship.path("type").asText().equals("manga")) {
                                        mangaId = relationship.path("id").asText();
                                        break;
                                    }
                                }

                                chapterImg.setChapterNumber(rootNode.path("data").path("attributes").path("chapter").asText());
                                chapterImg.setNumberOfImages(rootNode.path("data").path("attributes").path("pages").asInt());

                                if (mangaId != null) {
                                    String chaptersUrl = UriComponentsBuilder.fromUriString("https://api.mangadex.org/manga/{id}/feed")
                                            .queryParam("translatedLanguage[]", "en")
                                            .queryParam("limit", 200)
                                            .queryParam("includeFutureUpdates", 1)
                                            .queryParam("includeEmptyPages", 0)
                                            .queryParam("includeFuturePublishAt", 0)
                                            .queryParam("includeExternalUrl", 0)
                                            .queryParam("contentRating[]", "safe")
                                            .queryParam("contentRating[]", "suggestive")
                                            .queryParam("contentRating[]", "erotica")
                                            .queryParam("contentRating[]", "pornographic")
                                            .queryParam("order[createdAt]", "asc")
                                            .queryParam("order[updatedAt]", "asc")
                                            .queryParam("order[publishAt]", "asc")
                                            .queryParam("order[readableAt]", "asc")
                                            .queryParam("order[volume]", "asc")
                                            .queryParam("order[chapter]", "asc")
                                            .buildAndExpand(mangaId)
                                            .toUriString();

                                    return webClient.get().uri(chaptersUrl)
                                            .header(HttpHeaders.USER_AGENT, "mangaslay/1.0")
                                            .accept(MediaType.APPLICATION_JSON)
                                            .retrieve()
                                            .bodyToMono(String.class)
                                            .flatMap(chaptersResponse -> {
                                                try {
                                                    JsonNode chaptersRootNode = objectMapper.readTree(chaptersResponse);
                                                    JsonNode chaptersDataNode = chaptersRootNode.path("data");

                                                    HashMap<String, String> allChapters = new LinkedHashMap<>();
                                                    for (JsonNode chapterNode : chaptersDataNode) {
                                                        String chapterNumberStr = chapterNode.path("attributes").path("chapter").asText();
                                                        String chapterId = chapterNode.path("id").asText();

                                                        if (chapterNumberStr == null || chapterNumberStr.isEmpty() || chapterNumberStr.equals("null")) {
                                                            allChapters.put("Oneshot", chapterId);
                                                        } else {
                                                            try {
                                                                float chapterNumber = Float.parseFloat(chapterNumberStr);
                                                                if (chapterNumber == Math.floor(chapterNumber)) {
                                                                    allChapters.put(String.valueOf((int) chapterNumber), chapterId); // Convert to int if no decimals
                                                                } else {
                                                                    allChapters.put(chapterNumberStr, chapterId); // Keep as float string if it has decimals
                                                                }
                                                            } catch (NumberFormatException e) {
                                                                allChapters.put("Oneshot", chapterId);
                                                            }
                                                        }
                                                    }
                                                    allChapters = allChapters.entrySet().stream()
                                                            .sorted(Map.Entry.comparingByKey(Comparator.comparingDouble(Double::parseDouble)))
                                                            .collect(Collectors.toMap(
                                                                    Map.Entry::getKey,
                                                                    Map.Entry::getValue,
                                                                    (oldValue, newValue) -> oldValue,
                                                                    LinkedHashMap::new
                                                            ));
                                                    // Sort the chapters map based on the chapter keys (numbers)
                                                    chapterImg.setAllChapters(allChapters);

                                                    return Mono.just(chapterImg);
                                                } catch (Exception e) {
                                                    return Mono.error(e);
                                                }
                                            });
                                } else {
                                    return Mono.just(chapterImg);
                                }
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        })
                );
    }



}
