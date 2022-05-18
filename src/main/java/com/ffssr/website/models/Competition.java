package com.ffssr.website.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;

@Entity
public class Competition implements Unit{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title, anons, full_text;

    private int views;

    public ArrayList<String> getDocumentArrayList() {
        return documentArrayList;
    }

    public void setDocumentArrayList(ArrayList<String> documentArrayList) {
        this.documentArrayList = documentArrayList;
    }

    private ArrayList<String> documentArrayList = new ArrayList<>();

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    private int types;



    private String filename;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnons() {
        return anons;
    }

    public void setAnons(String anons) {
        this.anons = anons;
    }

    public String getFull_text() {
        return full_text;
    }

    public void setFull_text(String full_text) {
        this.full_text = full_text;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Competition() {
    }

    public Competition(String title, String anons, String full_text, int types) {
        this.title = title;
        this.anons = anons;
        this.full_text = full_text;
        this.types = types;
    }
}
