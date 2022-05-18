package com.ffssr.website.models.repo;

import com.ffssr.website.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM Post ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Post findLast();
}
