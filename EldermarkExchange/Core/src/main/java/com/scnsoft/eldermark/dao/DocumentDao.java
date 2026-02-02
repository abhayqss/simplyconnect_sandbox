package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DocumentDao {
    Document findDocument(long id);
    Document findDocumentByUniqueId(String uniqueId);

    List<Document> queryForDocuments(Resident resident, Employee requestingEmployee);
    List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee, int offset, int limit);
    List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee, Pageable pageable);
    Long getDocumentCount(Resident resident, String filter, Employee requestingEmployee);

    List<Document> queryForDocuments(Collection<Resident> residents, Employee requestingEmployee);
    Long countDocuments(Collection<Resident> residents, Employee requestingEmployee);
    List<Document> queryForDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee, Pageable pageable);
    List<Document> queryForDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee, int offset, int limit);

    Long countDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee);

    List<Document> queryForDocuments(Resident resident, Employee requestingEmployee, List<Long> orSharedWith, boolean visibleOnly);

    void saveDocument(Document document);

    void makeInvisible(long id);
    void makeVisible(long id);
    void deleteDocument(long id);

    void updateDocument(Document document);

    List<Long> findAllIds();
    List<Long> findAllIds(Date createdOrDeletedFrom);

}
