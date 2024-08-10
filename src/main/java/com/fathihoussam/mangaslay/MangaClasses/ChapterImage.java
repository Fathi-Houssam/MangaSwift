package com.fathihoussam.mangaslay.MangaClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.*;

public class ChapterImage {

    private HashMap<String,String> allChapters;
    private String chapterNumber;
    private String baseUrl;
    private String hash;
    private List<String> data;
    private List<String> dataSaver;
    private int numberOfImages;

    public HashMap<String, String> getAllChapters() {
        return allChapters;
    }


    public void setAllChapters(HashMap<String, String> allChapters) {
        this.allChapters = allChapters.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    // Custom comparator to sort by numeric value
                    if (e1.getKey().equals("Oneshot")) return -1;
                    if (e2.getKey().equals("Oneshot")) return 1;
                    return Float.compare(Float.parseFloat(e1.getKey()), Float.parseFloat(e2.getKey()));
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new)); // Keep the order
    }
    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public List<String> getDataSaver() {
        return dataSaver;
    }

    public void setDataSaver(List<String> dataSaver) {
        this.dataSaver = dataSaver;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    // Method to get a formatted chapter list
    public String getFormattedListofChapters() {
        StringBuilder formattedChapters = new StringBuilder();

        for (Map.Entry<String, String> entry : allChapters.entrySet()) {
            String chapterKey = entry.getKey(); // This is the chapter number (or "Oneshot")
            formattedChapters.append(chapterKey).append(", ");
        }

        // Remove the last ", " if present
        if (formattedChapters.length() > 0) {
            formattedChapters.setLength(formattedChapters.length() - 2);
        }

        return formattedChapters.toString();
    }

}
