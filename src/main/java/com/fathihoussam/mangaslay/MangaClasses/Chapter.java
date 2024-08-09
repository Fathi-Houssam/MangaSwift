package com.fathihoussam.mangaslay.MangaClasses;

import java.util.Date;

public class Chapter {
    private float chapterNumber;
    private String chapterName;
    private String chapterId;
    private Date releaseDate;

    public float getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(float chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterName() {
        return chapterName != null ? chapterName : "";
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFormattedChapterNumber() {
        if (chapterNumber == -1) {
            return "Oneshot";  // Custom label for oneshots
        } else if (chapterNumber == (int) chapterNumber) {
            return String.valueOf((int) chapterNumber);
        } else {
            return String.valueOf(chapterNumber);
        }
    }

}
