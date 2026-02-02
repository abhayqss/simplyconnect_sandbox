package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.*;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AuditLoggingDaoImpl implements AuditLoggingDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void logOperation(AuditLog entry) {
        entityManager.persist(entry);
    }

    @Override
    public void logOperation(AuditLogAction action, Long employeeId, List<Long> residentIds, List<Long> documentIds) {
        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setDate(new Date());
        entry.setRemoteAddress(SecurityUtils.getRemoteAddress());

        if(employeeId != null) {
            entry.setEmployee(entityManager.getReference(Employee.class, employeeId));
        }

        List<Resident> residentRefs = new ArrayList<Resident>();
        if(residentIds != null) {
            for(Long id : residentIds) {
                residentRefs.add(entityManager.getReference(Resident.class, id));
            }
            entry.setResidents(residentRefs);
        }

        List<Document> documentRefs = new ArrayList<Document>();
        if(documentIds != null) {
            for(Long id : documentIds) {
                documentRefs.add(entityManager.getReference(Document.class, id));
            }
            entry.setDocuments(documentRefs);
        }

        entityManager.persist(entry);
    }
}