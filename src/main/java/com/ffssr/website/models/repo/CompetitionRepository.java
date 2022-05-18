package com.ffssr.website.models.repo;

import com.ffssr.website.models.Competition;
import com.ffssr.website.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @Query(value = "SELECT * FROM Competition ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Competition findLast();
}
