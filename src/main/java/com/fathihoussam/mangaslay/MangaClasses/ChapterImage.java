package com.fathihoussam.mangaslay.MangaClasses;

import java.util.ArrayList;
import java.util.List;

public class ChapterImage {

    private List<Float> allChapters;
    private String chapterNumber;
    private String baseUrl;
    private String hash;
    private List<String> data;
    private List<String> dataSaver;
    private int numberOfImages;


    public List<Float> getAllChapters() {
        return allChapters;
    }

    public void setAllChapters(List<Float> allChapters) {
        this.allChapters = allChapters;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
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
}
