package com.ffssr.website.models.repo;

import com.ffssr.website.models.Contact;
import org.springframework.data.repository.CrudRepository;

public interface ContactRepository extends CrudRepository<Contact, Long> {
}

