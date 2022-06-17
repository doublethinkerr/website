package com.ffssr.website.controllers;

import com.ffssr.website.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartAppCode {

    @Autowired
    private PostService postService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        //MainController.pagedNews = postService.getPage(1, NewsController.countOfPosts);


        MainController.searchString="boooooo";
        System.out.println("Yaaah, I am running........");
    }
}
