package com.ffssr.website.services;

import com.ffssr.website.models.Post;
import com.ffssr.website.models.repo.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public Paged<Post> getPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by("id").descending());
        Page<Post> postPage = postRepository.findAll(request);

        return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
    }

    public Paged<Post> getPageStringSearch(String title, int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by("id"));
        Page<Post> postPage = (Page<Post>) postRepository.findAllByTitleContaining(title, request);

        return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
    }
}