package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.document.Document;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DocumentDao extends AppJpaRepository<Document, Long>, IdProjectionRepository<Long> {

    @Modifying
    @Query("update Document set isCDA = :isCda where id=:id")
    void setIsCda(@Param("id") Long id, @Param("isCda") Boolean isCda);

    Document findFirstByUniqueId(String uniqueId);

    boolean existsByCategoryChainIdsContains(Long categoryChainId);

    Collection<Document> findAllByFolder_Id(Long folderId);
}
