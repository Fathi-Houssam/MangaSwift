package com.fathihoussam.mangaslay.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_mangas")
public class UserMangas {

    @Id
    @Column(name = "manga_id")
    private String mangaId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public UserMangas(String mangaId, User user) {
        this.mangaId = mangaId;
        this.user = user;
    }

    public String getMangaId() {
        return mangaId;
    }

    public void setMangaId(String mangaId) {
        this.mangaId = mangaId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserMangas(){}

}
