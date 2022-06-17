package com.ffssr.website.models.repo;

import com.ffssr.website.models.Post;
import com.ffssr.website.services.Paged;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {

    Page <Post> findAllByAnonsContaining(String title, Pageable pageable);
    List <Post> findAllByTitleContaining(String title, Pageable pageable);



    @Query(value = "SELECT * FROM Post ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Post findLast();
}
