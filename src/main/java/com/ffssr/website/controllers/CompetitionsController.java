package com.ffssr.website.controllers;

import com.ffssr.website.config.Translit;
import com.ffssr.website.models.Competition;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.services.CompetitionService;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CompetitionsController {

    @Value("${uploadDOC.path}")
    private String uploadPathDOC;

    @Value("${uploadIMG.path}")
    private String uploadPathIMG;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";

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

    @GetMapping("/competition")
    public String competition(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                              @RequestParam(value = "size", required = false, defaultValue = "3") int size, Model model) {

        model.addAttribute("competitions", competitionService.getPage(pageNumber, NewsController.countOfPosts));
        model.addAttribute("docs", getSidebarDocs());
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("title", "Соревнования");
        return "competition-main";
    }

    @GetMapping("/competition/add")
    public String competitionAdd(Model model){
        model.addAttribute("title", "Добавление соревнований");
        return "competition-add";
    }

    @PostMapping("/competition/add")
    public String competitionPostAdd(@RequestParam String title,
                              @RequestParam String anons,
                              @RequestParam String full_text,
                              @RequestParam int types,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("files") MultipartFile[] files,
                              Model model) throws IOException {

        ArrayList <String> documents = new ArrayList<>();
        Competition competition = new Competition(title, anons, full_text,types);

        if (!file.isEmpty()){

            File uploadDir = new File(uploadPathIMG);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + Translit.cyr2lat(file.getOriginalFilename());

            file.transferTo(new File(uploadPathIMG + "/" + resultFilename));
            competition.setFilename(resultFilename);
        }
        else competition.setFilename("null");
        if (types == 1) NewsController.mainPost = competition;
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        competition.setDate(formatForDateNow.format(new Date()));

        for(MultipartFile f : files) {
            String origName = null;
            String name = null;

            if (!f.isEmpty()) {
                try {
                    byte[] bytes = f.getBytes();
                    origName = f.getOriginalFilename();
                    String str = Translit.cyr2lat(f.getOriginalFilename());
                    name = str.replace(' ', '_');
                    File dir = new File(uploadPathDOC);

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File uploadedFile = new File(dir.getAbsolutePath() + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                    stream.write(bytes);
                    stream.flush();
                    stream.close();
                    documents.add(name);
                } catch (Exception e) {
                    return "You failed to upload " + origName + " =&gt; " + e.getMessage();
                }
            }
        }
        competition.setDocumentArrayList(documents);

        competitionRepository.save(competition);
        return "redirect:/competition/";
    }

    @GetMapping("/competition/{id}")
    public String competitionDetails(@PathVariable(value = "id") long id, Model model){
        if(!competitionRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Competition competition = competitionRepository.findById(id).orElseThrow();
        ArrayList<String> documents = competition.getDocumentArrayList();

        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("documents", documents);
        model.addAttribute("post", competition);
        model.addAttribute("title", "Детали соревнований");
        model.addAttribute("docs", MainController.docList);
        Competition competition2 = competitionRepository.findById(id).orElseThrow();
        competition2.setViews(competition2.getViews()+1);
        competitionRepository.save(competition2);
        return "competition-details";
    }

    @GetMapping("/competition/{id}/edit")
    public String competitionEdit(@PathVariable(value = "id") long id, Model model){
        if(!competitionRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Competition> competition = competitionRepository.findById(id);
        ArrayList<Competition> res = new ArrayList<>();
        competition.ifPresent(res::add);
        model.addAttribute("post", res);
        model.addAttribute("title", "Редактирование соревнований");
        return "competition-edit";
    }

    @PostMapping("/competition/{id}/edit")
    public String competitionPostUpdate(@PathVariable(value = "id") long id,
                                 @RequestParam String title,
                                 @RequestParam String anons,
                                 @RequestParam int types,
                                 @RequestParam String full_text,
                                 @RequestParam("files") MultipartFile[] files,
                                 Model model)
    {
        Competition competition = competitionRepository.findById(id).orElseThrow();
        ArrayList<String> documents = competition.getDocumentArrayList();
        competition.setTitle(title);
        competition.setAnons(anons);
        competition.setFull_text(full_text);
        competition.setTypes(types);
        if (types == 1) NewsController.mainPost = competition;

        for(MultipartFile f : files) {
            String origName = null;
            String name = null;
            if (!f.isEmpty()) {
                try {
                    byte[] bytes = f.getBytes();
                    origName = f.getOriginalFilename();
                    String str = Translit.cyr2lat(f.getOriginalFilename());
                    name = str.replace(' ', '_');
                    File dir = new File(uploadPathDOC);

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File uploadedFile = new File(dir.getAbsolutePath() + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(uploadedFile));
                    stream.write(bytes);
                    stream.flush();
                    stream.close();
                    documents.add(name);
                } catch (Exception e) {
                    return "You failed to upload " + origName + " =&gt; " + e.getMessage();
                }
            }
            competition.setDocumentArrayList(documents);
            competitionRepository.save(competition);
        }
        return "redirect:/blog/admin";
    }

    @PostMapping("/competition/{id}/remove")
    public String competitionPostDelete(@PathVariable(value = "id") long id, Model model) {
        Competition competition = competitionRepository.findById(id).orElseThrow();
        competitionRepository.delete(competition);
        return "redirect:/competition/admin";
    }

    @GetMapping("/competition/admin")
    public String competitionMainAdmin(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                       @RequestParam(value = "size", required = false, defaultValue = "3") int size,Model model){
/*        Iterable<Competition> competitions = competitionRepository.findAll();
        Collections.reverse((List<Competition>) competitions);*/
        model.addAttribute("competitions", competitionService.getPage(pageNumber, NewsController.countOfPosts));
        model.addAttribute("docs", MainController.docList);
        model.addAttribute("title", "Администрирование соревнований");
        return "competition-main-admin";
    }

    @PostMapping("/competition/admin")
    public String blogAdm(@RequestParam String videolink,
                          @RequestParam String video_description,
                          @RequestParam int count, Model model) {
        String str = "";
        if (videolink=="") str = "null";
        else str = videolink;
        NewsController.videoLink = str;
        NewsController.videoDescription = video_description;
        NewsController.countOfPosts = count;

        return "redirect:/competition/admin";
    }
}
