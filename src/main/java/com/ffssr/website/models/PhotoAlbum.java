package com.ffssr.website.models;

import org.hibernate.mapping.Array;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;


@Entity
public class PhotoAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private ArrayList<String> images;

    private ArrayList<String> imagesMin;

    public ArrayList<String> getImagesMin() {
        return imagesMin;
    }

    public void setImagesMin(ArrayList<String> imagesMin) {
        this.imagesMin = imagesMin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public PhotoAlbum() {
    }

    public PhotoAlbum(String name, ArrayList<String> images, ArrayList<String> imagesMin) {
        this.name = name;
        this.images = images;
        this.imagesMin = imagesMin;
    }
    public PhotoAlbum(String name) {
        this.name = name;
    }
}
