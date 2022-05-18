package com.ffssr.website.services;

import com.ffssr.website.models.Competition;
import com.ffssr.website.models.repo.CompetitionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }


    public Paged<Competition> getPage(int pageNumber, int size) {
        PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by("id").descending());
        Page<Competition> competitionPage = competitionRepository.findAll(request);

        return new Paged<>(competitionPage, Paging.of(competitionPage.getTotalPages(), pageNumber, size));
    }
}