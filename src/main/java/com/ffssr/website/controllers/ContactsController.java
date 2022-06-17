package com.ffssr.website.controllers;

import com.ffssr.website.models.Contact;
import com.ffssr.website.models.Document;
import com.ffssr.website.models.repo.CompetitionRepository;
import com.ffssr.website.models.repo.ContactRepository;
import com.ffssr.website.models.repo.DocumentRepository;
import com.ffssr.website.models.repo.PostRepository;
import com.ffssr.website.services.CompetitionService;
import com.ffssr.website.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ContactsController {

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

    String yandexMapsApi = "https://api-maps.yandex.ru/services/constructor/1.0/js/?um=constructor%3Aa76643c010b3d1f28d844a73e97dc392ea027786ef66844a181ffff5a99a3477&amp;width=99%&amp;height=420&amp;lang=ru_RU&amp;scroll=true";

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

    @GetMapping("/contacts")
    public String blogContacts(Model model){
        List<Contact> contactList = (List<Contact>) contactRepository.findAll();
        model.addAttribute(contactList);
        model.addAttribute("videoLink", NewsController.videoLink);
        model.addAttribute("docs", MainController.docList);
        model.addAttribute("yandexMapsApi", yandexMapsApi);
        model.addAttribute("title", "Наши контакты");
        return "contacts";
    }

    @GetMapping("/contacts/admin")
    public String blogContactsAdmin(Model model){
        List contactList = (List) contactRepository.findAll();
        model.addAttribute("contactList", contactList);
        model.addAttribute("yandexMapsApi", yandexMapsApi);
        model.addAttribute("title", "Администрирование контактов");
        return "contacts-admin";
    }

    @PostMapping("/contacts/admin")
    public String blogContactsAdminPost(@RequestParam String number1, @RequestParam String number2, @RequestParam String number3,
                                        @RequestParam String email1, @RequestParam String email2,@RequestParam String email3,
                                        @RequestParam String social1, @RequestParam String social2, @RequestParam String social3,
                                        @RequestParam String adress1, @RequestParam String adressAPI, @RequestParam String textContacts, Model model) {
        yandexMapsApi = adressAPI;
        int i=0;
        Contact contact = new Contact();
        List<String> requestParam = Stream.of(number1,number2,number3,email1,email2,email3,social1,social2,social3, adress1, textContacts).collect(Collectors.toList());
        Iterable<Contact> contacts = contactRepository.findAll();
        Iterator<Contact> iterator = contacts.iterator();
        while (i<11){
            contact = iterator.next();
            contact.setName(requestParam.get(i));
            contactRepository.save(contact);
            i++;
        }

        return "redirect:/contacts/admin";
    }
}
