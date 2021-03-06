package com.ffssr.website.controllers;

import com.ffssr.website.config.Translit;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.Post;
import com.ffssr.website.models.Unit;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.ContactRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.models.repo.PostRepository;
import com.ffssr.website.services.CompetitionService;
import com.ffssr.website.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class NewsController {

    static public String videoLink = "null";
    static public String videoDescription = "";

    static Unit mainPost = null;

    static public int defaultPageNumber = 1;
    static public int countOfPosts = 5;


    @Value("${uploadIMG.path}")
    private String uploadPathIMG;

    @Value("${uploadDOC.path}")
    private String uploadPathDOC;

    @Autowired
    private PostService postService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private DocumentRepository documentRepository;

    public ArrayList<Document> getSidebarDocs() {
        int i = 0;
        ArrayList<Document> docList = new ArrayList<>();
        Iterable<Document> docs = documentRepository.findAll();
        Iterator<Document> iterator = docs.iterator();
        while (i < 3) {
            if (iterator.hasNext()) {
                docList.add(iterator.next());
            }
            i++;
        }
        return docList;
    }

    @GetMapping("/blog")
    public String newsMain(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                           @RequestParam(value = "size", required = false, defaultValue = "3") int size, Model model) {

        model.addAttribute("posts", postService.getPage(pageNumber, countOfPosts));
        model.addAttribute("docs", getSidebarDocs());
        model.addAttribute("title", "??????????????");
        model.addAttribute("videoLink", videoLink);
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        model.addAttribute("string", MainController.searchString);
        return "blog-main";
    }

    @GetMapping("/blog/add")
    public String blogAdd(Model model) {
        model.addAttribute("title", "???????????????????? ??????????????");
        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd(@RequestParam String title,
                              @RequestParam String anons,
                              @RequestParam String full_text,
                              @RequestParam int types,
                              @RequestParam("file") MultipartFile file,
                              Model model) throws Exception {
        if (file.getSize() > 1024*1024) {
            model.addAttribute("statusMessage","You failed to upload " + file.getOriginalFilename() + "fileSize:" + file.getSize() + ";     maxFileSize 10mb");
            return "errorPage";
        }

            String msg = "default";

            Post post = new Post(title, anons, full_text, types);
            if (!file.isEmpty()) {

                File uploadDir = new File(uploadPathIMG);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + Translit.cyr2lat(file.getOriginalFilename());

                file.transferTo(new File(uploadPathIMG + "/" + resultFilename));
                post.setFilename(resultFilename);
            } else post.setFilename("null");
            if (types == 1) mainPost = post;
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("EEE, MMM d, ''yy");
            post.setDate(formatForDateNow.format(new Date()));
            postRepository.save(post);
            MainController.pagedNews = postService.getPage(defaultPageNumber, countOfPosts);
            return "redirect:/blog/admin";
    }


    @GetMapping("/blog/{id}")
    public String blogDetails(@PathVariable(value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        model.addAttribute("docs", MainController.docList);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("videoLink", videoLink);
        model.addAttribute("post", res);
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        model.addAttribute("title", "???????????? ??????????????");
        Post post2 = postRepository.findById(id).orElseThrow();
        post2.setViews(post2.getViews() + 1);
        postRepository.save(post2);
        return "blog-details";
    }

    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        model.addAttribute("title", "???????????????????????????? ??????????????");
        return "blog-edit";
    }

    @PostMapping("/blog/{id}/edit")
    public String blogPostUpdate(@PathVariable(value = "id") long id,
                                 @RequestParam String title,
                                 @RequestParam String anons,
                                 @RequestParam String full_text,
                                 @RequestParam int types,
                                 Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);
        post.setTypes(types);
        if (types == 1) mainPost = post;
        postRepository.save(post);

        return "redirect:/blog/admin";
    }

    @PostMapping("/blog/{id}/remove")
    public String blogPostDelete(@PathVariable(value = "id") long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow();
        postRepository.delete(post);
        return "redirect:/blog/admin";
    }


    @GetMapping("/blog/admin")
    public String newsMainAdmin(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                @RequestParam(value = "size", required = false, defaultValue = "3") int size, Model model) {
        model.addAttribute("posts", postService.getPage(pageNumber, size));
        model.addAttribute("docs", MainController.docList);
        model.addAttribute("title", "??????????????????????");
        model.addAttribute("calendarDocs", DocumentsController.calendarDocs);
        return "blog-main-admin";
    }

    @PostMapping("/blog/admin")
    public String blogAdm(@RequestParam String videolink,
                          @RequestParam String video_description,
                          @RequestParam int count, Model model) {
        String str = "";
        if (videolink == "") str = "null";
        else str = videolink;
        videoLink = str;
        videoDescription = video_description;
        countOfPosts = count;
        return "redirect:/blog/admin";
    }

    @GetMapping("/login?logout")
    public String blogLogout(Model model) {
        model.addAttribute("title", "?????????? ???? ??????????????");
        return "blog-main";
    }

    @GetMapping("/login")
    public String get(Model model) {
        model.addAttribute("title", "?????????? ??????????");
        return "login";
    }

}
