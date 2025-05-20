package com.fathihoussam.mangaslay.Controllers;
import com.fathihoussam.mangaslay.MangaClassesDTOs.Manga;
import com.fathihoussam.mangaslay.MangaClassesDTOs.MangaDetails;
import com.fathihoussam.mangaslay.Repository.UserMangaInfo;
import com.fathihoussam.mangaslay.Services.ServiceAPI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {

    private final ServiceAPI serviceAPI;
    private final UserMangaInfo usermangainfo;

    @Autowired
    public MainController(ServiceAPI serviceAPI, UserMangaInfo userMangaInfo) {
        this.serviceAPI = serviceAPI;
        this.usermangainfo = userMangaInfo;
    }

    @GetMapping("/")
    public String hello(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");

        List<String> libraryMangaIds = usermangainfo.findMangaIdByUserId(userId);
        List<Manga> mangas = serviceAPI.libraryMangas(libraryMangaIds).block();
        model.addAttribute("librarymangas", mangas);
        return "IndexPage";

    }

    @GetMapping("/searchresult")
    public String SearchProcessing(@RequestParam("mangasearch") String mangasearchname, Model model) {
        List<Manga> mangas = serviceAPI.searchResult(mangasearchname).block();
        model.addAttribute("mangas", mangas);
        return "ResultPage";
    }

    @GetMapping("/chapter")
    public String chapterProcessing(@RequestParam("mangaId") String mangaId, Model model) {
        model.addAttribute("mangaid",mangaId);
        Mono<MangaDetails> detailsMono = serviceAPI.generateDetails(mangaId);


        detailsMono
                .doOnNext(detail -> model.addAttribute("detail", detail))
                .doOnError(e -> model.addAttribute("error", "Failed to fetch manga details: " + e.getMessage()))
                .block();
        return "ChapterPage";
    }


    @GetMapping("/chapter-info")
    public Mono<String> receiveChapterInfo(@RequestParam("chapterId") String chapterId, Model model) {
        return serviceAPI.imageGetter(chapterId)
                .doOnNext(chapterImage -> {
                    model.addAttribute("details2", chapterImage);
                    model.addAttribute("range", IntStream.rangeClosed(1, chapterImage.getNumberOfImages()).boxed().collect(Collectors.toList()));
                })
                .then(Mono.just("ViewerPage"));
    }

    @PostMapping("/saveManga")
    public ResponseEntity<String> saveManga(@RequestParam("mangaId") String mangaId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
            }


            usermangainfo.insertUserMangas(userId, mangaId);

            return ResponseEntity.ok("Manga saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving manga: " + e.getMessage());
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteMangaById(@RequestParam("mangaId") String mangaId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            usermangainfo.deleteByMangaId(mangaId, userId);
            return ResponseEntity.ok("Manga deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting manga: " + e.getMessage());
        }
    }

    @RestController
    @RequestMapping("/proxy-image")
    public class ImageProxyController {

        private final WebClient webClient;

        public ImageProxyController(WebClient.Builder builder) {
            this.webClient = builder.baseUrl("https://uploads.mangadex.org").build();
        }

        @GetMapping("/covers/{mangaId}/{fileName:.+}")
        public Mono<ResponseEntity<byte[]>> proxyCoverImage(
                @PathVariable String mangaId,
                @PathVariable String fileName) {

            return webClient.get()
                    .uri("/covers/{mangaId}/{fileName}", mangaId, fileName)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .map(body -> ResponseEntity
                            .ok()
                            .contentType(getContentType(fileName))
                            .body(body));
        }

        @GetMapping("/{quality}/{hash}/{fileName:.+}")
        public Mono<ResponseEntity<byte[]>> proxyChapterImage(
                @PathVariable String quality,
                @PathVariable String hash,
                @PathVariable String fileName) {

            return webClient.get()
                    .uri("/{quality}/{hash}/{fileName}", quality, hash, fileName)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .map(body -> ResponseEntity
                            .ok()
                            .contentType(getContentType(fileName))
                            .body(body));
        }

        private MediaType getContentType(String filename) {
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
            if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }



}


