package com.fathihoussam.mangaslay.Controllers;


import com.fathihoussam.mangaslay.MangaClasses.Manga;
import com.fathihoussam.mangaslay.MangaClasses.MangaDetails;
import com.fathihoussam.mangaslay.Services.ServiceAPI;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MainController {

    private final ServiceAPI serviceAPI;

    @Autowired
    public MainController(ServiceAPI serviceAPI) {
        this.serviceAPI = serviceAPI;
    }

    @GetMapping("/")
    public String hello(Model model, HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");

        if (sessionId != null) {
            model.addAttribute("sessionId", sessionId);
        }

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
                .block(); // Blocking for simplicity, consider using asynchronous processing in production
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


}


