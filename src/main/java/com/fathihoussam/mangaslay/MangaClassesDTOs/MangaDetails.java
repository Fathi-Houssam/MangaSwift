package com.fathihoussam.mangaslay.MangaClassesDTOs;

import java.util.*;

public class MangaDetails {

    private String title;
    private String description;
    private int releaseYear;
    private String status;
    private String coverURL;

    private List<Chapter> chapters;
    public String getCoverURL() {
        return coverURL;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}