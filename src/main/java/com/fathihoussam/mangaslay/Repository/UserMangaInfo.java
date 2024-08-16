package com.fathihoussam.mangaslay.Repository;

import com.fathihoussam.mangaslay.Entity.UserMangas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserMangaInfo extends JpaRepository<UserMangas, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_mangas (user_id, manga_id) VALUES (:userId, :mangaId)", nativeQuery = true)
    void insertUserMangas(@Param("userId") Long userId, @Param("mangaId") String mangaId);


    @Query("SELECT um.mangaId FROM UserMangas um WHERE um.user.id = :userId")
    List<String> findMangaIdByUserId(@Param("userId") Long userId);

}