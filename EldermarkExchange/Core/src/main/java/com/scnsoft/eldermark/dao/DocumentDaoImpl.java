package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.Resident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;


@Component
public class DocumentDaoImpl implements DocumentDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Document> queryForDocuments(Resident resident, Employee requestingEmployee) {
        TypedQuery<Document> query = createQuery(resident, null, requestingEmployee, null, false, null, true);
        return query.getResultList();
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee,
                                            int offset, int limit) {
        TypedQuery query = createQuery(resident, filter, requestingEmployee, null, false, null, true);
        query.setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, String filter, Employee requestingEmployee,
                                            Pageable pageable) {
        TypedQuery query = createQuery(resident, filter, requestingEmployee, null, false, pageable.getSort(), true);
        query.setFirstResult(pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }


    @Override
    public Long getDocumentCount(Resident resident, String filter, Employee requestingEmployee) {
        TypedQuery query = createQuery(resident, filter, requestingEmployee, null, true, null, true);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Document> queryForDocuments(Collection<Resident> residents, Employee requestingEmployee) {
        List<Document> allDocuments = new ArrayList<Document>();
        for (Resident resident : residents) {
            List<Document> documents = queryForDocuments(resident, requestingEmployee);
            allDocuments.addAll(documents);
        }

        return allDocuments;
    }

    @Override
    public Long countDocuments(Collection<Resident> residents, Employee requestingEmployee) {
        Long totalCount = 0L;
        for (Resident resident : residents) {
            Long documentCount = getDocumentCount(resident, null, requestingEmployee);
            totalCount += documentCount;
        }

        return totalCount;
    }

    @Override
    public List<Document> queryForDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee, Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        Sort sort = pageable == null ? null : pageable.getSort();
        TypedQuery<Document> query = createQuery(residentIds, null, requestingEmployee, null, false, sort, true);
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    @Override
    public List<Document> queryForDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee, int offset, int limit) {
        if (CollectionUtils.isEmpty(residentIds) || limit < 1) {
            return Collections.emptyList();
        }

        TypedQuery<Document> query = createQuery(residentIds, null, requestingEmployee, null, false, null, true);
        query.setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Long countDocumentsByResidentIdIn(Collection<Long> residentIds, Employee requestingEmployee) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }

        TypedQuery query = createQuery(residentIds, null, requestingEmployee, null, true, null, true);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Document> queryForDocuments(Resident resident, Employee requestingEmployee, List<Long> orSharedWith, boolean visibleOnly) {
        TypedQuery<Document> query = createQuery(resident, null, requestingEmployee, orSharedWith, false, null, visibleOnly);
        return query.getResultList();
    }

    private TypedQuery createQuery(Resident resident, String filter, Employee requestingEmployee, List<Long> orSharedWith, boolean count, Sort sort,
                                   boolean visibleOnly) {
        StringBuilder sb = new StringBuilder();
        if (count) {
            sb.append("select count(doc) ");
        } else {
            sb.append("select doc ");
        }

        sb.append("from Document doc ");
        sb.append(" left join doc.sharedWithDatabases db");
        sb.append(" left join doc.labResearchOrder labOrder");

        sb.append(" WHERE doc.residentDatabaseAlternativeId = :residentDatabaseAlternativeId");
        sb.append(" and (labOrder.status IS NULL OR labOrder.status = :labOrderStatus)");
        sb.append(" and doc.residentLegacyId = :residentLegacyId");
        if (visibleOnly)
            sb.append(" and doc.visible = TRUE");
        sb.append(" and doc.deletionTime IS NULL");

        if (!StringUtils.isEmpty(filter)) {
            sb.append(" and lower(doc.documentTitle) like :filter");
        }

        if (requestingEmployee != null) {
            sb.append(" and (");
            sb.append("   doc.eldermarkShared = TRUE" );     // shared with all
            sb.append("   OR db.id = :employeeDatabaseId "); // shared with employee's company

            if (!CollectionUtils.isEmpty(orSharedWith)) {
                sb.append("   OR db.id IN :sharedWith ");    // shared with some companies
            }
            sb.append(")");
        } else {
            // in case of self-access (requestingEmployee == null) -> show all documents
        }

        if (sort != null) {
            sb.append(" order by ");

            Map<String, String> colMap = new HashMap<String, String>();
            colMap.put("documentTitle", " doc.documentTitle :DIRECTION");
            colMap.put("size", " doc.size :DIRECTION");
            colMap.put("creationTime", " doc.creationTime :DIRECTION");
            colMap.put("authorPerson", " author.firstName :DIRECTION, author.lastName :DIRECTION");

            boolean docTitleOrder = false;
            for (Sort.Order order : sort) {
                sb.append(colMap.get(order.getProperty()).replace(":DIRECTION", order.isAscending() ? "ASC" : "DESC"));
                if (order.getProperty().equals("documentTitle")) docTitleOrder = true;
            }

            if(!docTitleOrder){
                sb.append(", doc.documentTitle ASC");
            }
        } else if(!count){
            sb.append(" order by doc.documentTitle ASC");
        }

        String queryStr = sb.toString();
        TypedQuery query;
        if (count) {
            query = entityManager.createQuery(queryStr, Long.class);
        } else {
            query = entityManager.createQuery(queryStr, Document.class);
        }
        query.setParameter("residentDatabaseAlternativeId", resident.getDatabaseAlternativeId());
        query.setParameter("labOrderStatus", LabResearchOrderStatus.REVIEWED);
        query.setParameter("residentLegacyId", resident.getLegacyId());
        if (requestingEmployee != null) {
            query.setParameter("employeeDatabaseId", requestingEmployee.getDatabaseId());
        }
        if (!StringUtils.isEmpty(filter)) {
            query.setParameter("filter", "%" + filter.toLowerCase() + "%");
        }
        if (!CollectionUtils.isEmpty(orSharedWith)) {
            query.setParameter("sharedWith", orSharedWith);
        }
        return query;
    }

    // TODO translate to CriteriaBuilder API
    private TypedQuery createQuery(Collection<Long> residentIds, String filter, Employee requestingEmployee, List<Long> orSharedWith, boolean count,
                                   Sort sort, boolean visibleOnly) {
        StringBuilder sb = new StringBuilder();
        if (count) {
            sb.append("SELECT count(doc) ");
        } else {
            sb.append("SELECT doc ");
        }

        sb.append("FROM Document doc");
        sb.append(" LEFT JOIN doc.sharedWithDatabases db");

        sb.append(" WHERE 1=1");

        if (requestingEmployee != null) {
            sb.append(" AND (");
            sb.append("   doc.eldermarkShared = TRUE" );     // shared with all
            sb.append("   OR db.id = :employeeDatabaseId "); // shared with employee's company

            if (!CollectionUtils.isEmpty(orSharedWith)) {
                sb.append("   OR db.id IN :sharedWith ");    // shared with some companies
            }
            sb.append(")");
        } else {
            // in case of self-access (requestingEmployee == null) -> show all documents
        }

        String subquery = "SELECT doc.id FROM DocumentLight doc " +
                " INNER JOIN doc.residentLegacy r " +
                " INNER JOIN r.database db " +
                " WHERE r.id IN :residentIds AND db.alternativeId = doc.residentDatabaseAlternativeId";
        if (visibleOnly) {
            subquery += " AND doc.visible = TRUE";
        }
        subquery += " AND doc.deletionTime IS NULL";

        if (!StringUtils.isEmpty(filter)) {
            subquery += " AND lower(doc.documentTitle) LIKE :filter";
        }

        sb.append(" AND doc.id IN (" + subquery + ")");

        if (sort != null) {
            sb.append(" ORDER BY ");

            Map<String, String> colMap = new HashMap<String, String>();
            colMap.put("documentTitle", " doc.documentTitle :DIRECTION");
            colMap.put("size", " doc.size :DIRECTION");
            colMap.put("creationTime", " doc.creationTime :DIRECTION");
            colMap.put("authorPerson", " author.firstName :DIRECTION, author.lastName :DIRECTION");

            boolean docTitleOrder = false;
            for (Sort.Order order : sort) {
                sb.append(colMap.get(order.getProperty()).replace(":DIRECTION", order.isAscending() ? "ASC" : "DESC"));
                if (order.getProperty().equals("documentTitle")) {
                    docTitleOrder = true;
                }
            }

            if (!docTitleOrder) {
                sb.append(", doc.documentTitle ASC");
            }
        } else if (!count) {
            // default sort
            sb.append(" ORDER BY doc.documentTitle ASC");
        }

        String queryStr = sb.toString();
        TypedQuery query;
        if (count) {
            query = entityManager.createQuery(queryStr, Long.class);
        } else {
            query = entityManager.createQuery(queryStr, Document.class);
        }
        query.setParameter("residentIds", residentIds);
        if (requestingEmployee != null) {
            query.setParameter("employeeDatabaseId", requestingEmployee.getDatabaseId());
        }
        if (!StringUtils.isEmpty(filter)) {
            query.setParameter("filter", "%" + filter.toLowerCase() + "%");
        }
        if (!CollectionUtils.isEmpty(orSharedWith)) {
            query.setParameter("sharedWith", orSharedWith);
        }
        return query;
    }

    public void saveDocument(Document document) {
        entityManager.persist(document);
    }

    public Document findDocument(long id) {
        return entityManager.find(Document.class, id);
    }

    @Override
    public Document findDocumentByUniqueId(String uniqueId) {
        List<Document> results =  entityManager
                .createQuery("select d from Document d where d.uniqueId=:uniqueId and d.visible=1", Document.class)
                .setParameter("uniqueId", uniqueId)
                .getResultList();
        return results.size()!=1?null:results.get(0);
    }

    public void makeInvisible(long id) {
        Document document = findDocument(id);

        document.setVisible(false);
        entityManager.merge(document);
    }

    @Override
    public void makeVisible(long id) {
        Document document = findDocument(id);

        document.setVisible(true);
        entityManager.merge(document);
    }

    @Override
    public void deleteDocument(long id) {
        Document document = findDocument(id);

        document.setDeletionTime(new Date());
        document.setVisible(false);

        entityManager.merge(document);
    }

    @Override
    public void updateDocument(Document document) {
        entityManager.merge(document);
    }

    @Override
    public List<Long> findAllIds() {
        String query = "Select doc.id from Document doc ";
        return entityManager.createQuery(query, Long.class).getResultList();
    }

    @Override
    public List<Long> findAllIds(Date fromTime) {
        String query = "Select doc.id from Document doc WHERE doc.creationTime>=:fromTime OR doc.deletionTime>=:fromTime";
        return entityManager
                .createQuery(query, Long.class)
                .setParameter("fromTime", fromTime)
                .getResultList();
    }

}
