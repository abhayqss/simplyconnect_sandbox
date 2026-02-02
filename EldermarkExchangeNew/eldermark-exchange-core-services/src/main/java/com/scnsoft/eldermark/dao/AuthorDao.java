package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorDao extends JpaRepository<Author, Long> {
    List<Author> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
