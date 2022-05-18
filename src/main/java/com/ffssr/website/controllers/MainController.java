package com.ffssr.website.controllers;

import com.ffssr.website.models.Competition;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.Post;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.models.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
public class MainController {


    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PostRepository postRepository;

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
    public String home(Model model) {
        Post lastNews = postRepository.findLast();
        model.addAttribute("lastNews", lastNews);
        Competition lastCompetition = competitionRepository.findLast();
        model.addAttribute("lastCompetition", lastCompetition);
        model.addAttribute("mainPost", NewsController.mainPost);
        docList = getSidebarDocs();
        model.addAttribute("docs", docList);
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("title", "Федерация котания на коньках");
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
        return "about";
    }

    @GetMapping("/liveStream")
    public String liveStream(Model model) {
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("videoDescription", NewsController.videoDescription);
        model.addAttribute("title", "Прямая трансляция");
        return "live-stream";
    }



}
