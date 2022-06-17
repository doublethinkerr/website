package com.ffssr.website.controllers;

import com.ffssr.website.config.Translit;
import com.ffssr.website.models.PhotoAlbum;
import com.ffssr.website.models.repo.PhotoAlbumRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class PhotoAlbumController {

    @Value("${uploadIMG.path}")
    private String uploadPathIMG;

    @Autowired
    PhotoAlbumRepository photoAlbumRepository;

    @GetMapping("/photo")
    public String photo(Model model) {
        List<PhotoAlbum> photoAlbumList = photoAlbumRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("photoAlbumList", photoAlbumList);
        model.addAttribute("title", "Фотографии");
        return "photo";
    }

    @GetMapping("/photo/admin")
    public String photoAdmin(Model model) {
        List<PhotoAlbum> photoAlbumList = photoAlbumRepository.findAll();
        model.addAttribute("photoAlbumList", photoAlbumList);
        model.addAttribute("title", "Фотографии админ");
        return "photo-admin";
    }

    @GetMapping("/photo/add")
    public String photoAddGet(Model model) {
        model.addAttribute("title", "Добавление фотоальбома");
        return "photo-add";
    }

    @PostMapping("/photo/add")
    public String photoAddPost(@RequestParam String name,
                               @RequestParam("files") MultipartFile[] files,
                               Model model) throws IOException {
        PhotoAlbum photoAlbum = new PhotoAlbum(name);
        ArrayList<String> imageName = new ArrayList<>();
        ArrayList<String> imageNameMin = new ArrayList<>();
        for(MultipartFile file : files) {
            File uploadDir = new File(uploadPathIMG);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + Translit.cyr2lat(file.getOriginalFilename());
            file.transferTo(new File(uploadPathIMG + "/" + resultFilename));
            Thumbnails.of(uploadPathIMG + "/" + resultFilename).
                    size(1280,1024).
                    toFile(uploadPathIMG + "/min." + resultFilename);
            imageName.add(resultFilename);
            imageNameMin.add("/min." + resultFilename);
        }
        photoAlbum.setImages(imageName);
        photoAlbum.setImagesMin(imageNameMin);
        photoAlbumRepository.save(photoAlbum);
        return "redirect:/photo/admin";
    }

    @GetMapping("/photo/{id}")
    public String photoDetails(@PathVariable(value = "id") long id, Model model){
        if(!photoAlbumRepository.existsById(id)) {
            return "redirect:/photo";
        }
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(id).get();
        model.addAttribute("name", photoAlbum.getName());
        model.addAttribute("album", photoAlbum);
        model.addAttribute("photoAlbum", photoAlbum.getImages());
        model.addAttribute("photoAlbumMin", photoAlbum.getImagesMin());
        model.addAttribute("title", "Детали фотоальбома");
        return "photo-details";
    }

    @GetMapping("/photo/{id}/edit")
    public String photoEdit(@PathVariable(value = "id") long id, Model model) {
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(id).get();
        model.addAttribute("title", "Редактирование фотоальбома");
        model.addAttribute("id", photoAlbum.getId());
        model.addAttribute("name", photoAlbum.getName());
        model.addAttribute("album", photoAlbum);
        model.addAttribute("photoAlbum", photoAlbum.getImages());
        model.addAttribute("photoAlbumMin", photoAlbum.getImagesMin());
        return "photo-edit";
    }

    @PostMapping("/photo/{id}/edit")
    public String photoEditPost(@PathVariable(value = "id") long id,
                                @RequestParam String name,
                                @RequestParam("files") MultipartFile[] files,
                                Model model) throws IOException {
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(id).orElseThrow();
        photoAlbum.setName(name);

        ArrayList<String> imageName = photoAlbum.getImages();
        ArrayList<String> imageNameMin = photoAlbum.getImagesMin();
        for(MultipartFile file : files) {
            File uploadDir = new File(uploadPathIMG);

            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + Translit.cyr2lat(file.getOriginalFilename());
            file.transferTo(new File(uploadPathIMG + "/" + resultFilename));
            Thumbnails.of(uploadPathIMG + "/" + resultFilename).
                    size(1280,1024).
                    toFile(uploadPathIMG + "/min." + resultFilename);
            imageName.add(resultFilename);
            imageNameMin.add("/min." + resultFilename);
        }
        photoAlbum.setImages(imageName);
        photoAlbum.setImagesMin(imageNameMin);
        photoAlbumRepository.save(photoAlbum);
        return "redirect:/photo/admin";
    }


    @PostMapping("/photo/{id}/remove")
    public String photoDelete(@PathVariable(value = "id") long id, Model model) {
        PhotoAlbum photoAlbum = photoAlbumRepository.findById(id).orElseThrow();
        photoAlbumRepository.delete(photoAlbum);
        return "redirect:/photo/admin";
    }




}
