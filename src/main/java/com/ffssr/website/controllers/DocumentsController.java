package com.ffssr.website.controllers;

import com.ffssr.website.config.Translit;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.Post;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.ContactRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.models.repo.PostRepository;
import com.ffssr.website.services.CompetitionService;
import com.ffssr.website.services.PostService;
import com.ibm.icu.text.Transliterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Controller
public class DocumentsController {

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

    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";

    public static List<Document> calendarDocs = new ArrayList<>();

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

    @GetMapping("/documents")
    public String blogDocument(Model model){
        List docs = getSidebarDocs();
        model.addAttribute("docs", docs);
        List<Document> allDocs = (List<Document>) documentRepository.findAll();
        Collections.reverse((List<Document>) allDocs);
        model.addAttribute("allDocs", allDocs);
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("title", "Документы");
        return "blog-document";
    }

    @GetMapping("/documents/admin")
    public String blogDocumentAdmin(Model model){
        List docs = getSidebarDocs();
        model.addAttribute("docs", docs);
        Iterable<Document> allDocs = documentRepository.findAll();
        model.addAttribute("allDocs", allDocs);
        model.addAttribute("title", "Документы админ");
        return "blog-document-admin";
    }


    @PostMapping("/documents/admin/{id}/remove")
    public String blogDocumentDelete(@PathVariable(value = "id") long id, Model model) {
        Document doc = documentRepository.findById(id).orElseThrow();
        documentRepository.delete(doc);
        MainController.docList.remove(doc);
        return "redirect:/documentAdmin";
    }

    @GetMapping("/blog/addDocumentCalendar")
    public String blogAddDocumentCalendar(Model model){
        model.addAttribute("title", "Добавление графика соревнований");
        return "blog-add-document";
    }

    @GetMapping("/blog/addDocumentCalendarClear")
    public String blogAddDocumentCalendarClear(Model model){
        calendarDocs.clear();
        return "redirect:/blog/admin";
    }

    @PostMapping("/blog/addDocumentCalendar")
    public String uploadFileCalendar(@RequestParam("file") MultipartFile file,
                                    @RequestParam String description
    ) {
        String origName = null;
        String name = null;

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                origName = file.getOriginalFilename();
                String str = Translit.cyr2lat(file.getOriginalFilename());
                name = str.replace (' ', '_');

                File dir = new File(uploadPathDOC);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File uploadedFile = new File(dir.getAbsolutePath() + File.separator + name);

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                stream.write(bytes);
                stream.flush();
                stream.close();

                Document doc = new Document(name, description, origName);

                documentRepository.save(doc);

                calendarDocs.add(doc);

                return "redirect:/documentAdmin";

            } catch (Exception e) {
                return "redirect:/documentAdmin";
            }
        } else {
            return "redirect:/documentAdmin";
        }
    }

    @GetMapping("/blog/addDocument")
    public String blogAddDocument(Model model){
        model.addAttribute("title", "Добавление документа");
        return "blog-add-document";
    }
    @PostMapping("/blog/addDocument")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam String description
                             ) {

        String origName = null;
        String name = null;

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();

                origName = file.getOriginalFilename();
                String str = Translit.cyr2lat(file.getOriginalFilename());
                name = str.replace (' ', '_');

                File dir = new File(uploadPathDOC);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File uploadedFile = new File(dir.getAbsolutePath() + File.separator + name);

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                stream.write(bytes);
                stream.flush();
                stream.close();

                Document doc = new Document(name, description, origName);

                documentRepository.save(doc);
                MainController.docList.add(doc);
                return "redirect:/documentAdmin";

            } catch (Exception e) {
                return "You failed to upload " + origName + " =&gt; " + e.getMessage();
            }
        } else {
            return "You failed to upload " + origName+ " because the file was empty.";
        }
    }
}
