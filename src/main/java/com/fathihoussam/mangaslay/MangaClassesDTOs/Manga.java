package com.fathihoussam.mangaslay.MangaClassesDTOs;

import java.util.List;

public class Manga {
    private String id;
    private String title;
    private List<String> tags;
    private String coverFile;
    private String authorName;
    private String chapterNumber;

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    // Custom method to get formatted chapter number
    public String getFormattedChapterNumber() {
        if ("Oneshot".equalsIgnoreCase(chapterNumber)) {
            return "Oneshot";
        }

        try {
            double num = Double.parseDouble(chapterNumber);
            if (num == (int) num) {
                return String.valueOf((int) num);
            } else {
                return String.valueOf(num);
            }
        } catch (NumberFormatException e) {
            return chapterNumber;
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(String coverFile) {
        this.coverFile = coverFile;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
