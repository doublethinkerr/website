package com.ffssr.website.controllers;

import com.ffssr.website.models.Competition;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.Post;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.models.repo.PostRepository;
import com.ffssr.website.services.Paged;
import com.ffssr.website.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
public class MainController {

    static public List<Post> globalSerach = new ArrayList<>();
    static public String searchString = "";

    static public Paged pagedNews;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CompetitionRepository competitionRepository;

    public static List<Document> docList = new ArrayList<>();

    public ArrayList<Document> getSidebarDocs(){
        int i = 0;
        ArrayList<Document> docList = new ArrayList<>();
        Iterable<Document> docs = documentRepository.findAll();
        Iterator<Document> iterator = docs.iterator();
        while (i<3){
            if (iterator.hasNext()) {
                docList.add(iterator.next());
            }
            i++;
        }
        return docList;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber, Model model) {
        Post lastNews = postRepository.findLast();
        model.addAttribute("lastNews", lastNews);
        Competition lastCompetition = competitionRepository.findLast();
        model.addAttribute("lastCompetition", lastCompetition);
        model.addAttribute("mainPost", NewsController.mainPost);
        docList = getSidebarDocs();
        model.addAttribute("docs", docList);
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("title", "Федерация котания на коньках");
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        pagedNews = postService.getPage(pageNumber, NewsController.countOfPosts);
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) throws IOException {
        String mainDescription = "";

        File doc = new File("C:\\aboutUs.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));

        String strng;
        while ((strng = obj.readLine()) != null)
            mainDescription+=strng;


        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("docs", docList);
        model.addAttribute("mainDescription", mainDescription);
        model.addAttribute("title", "О нас");
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        return "about";
    }

    @GetMapping("/liveStream")
    public String liveStream(Model model) {
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("videoDescription", NewsController.videoDescription);
        model.addAttribute("title", "Прямая трансляция");
        return "live-stream";
    }

    @PostMapping("/search")
    public String newsMainSearch(@RequestParam String search, Model model){
        searchString = search;
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                         @RequestParam(value = "size", required = false, defaultValue = "5") int size, Model model){
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("videoDescription", NewsController.videoDescription);
        Paged posts = postService.getPageStringSearch(searchString, pageNumber, size);
        model.addAttribute("posts", posts);
        model.addAttribute("title", "Поиск");
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        model.addAttribute("docs", getSidebarDocs());
        model.addAttribute("searchString", searchString);
        return "search";
    }


}
