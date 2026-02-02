package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.DocumentationOf;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentationOfDaoImpl extends ResidentAwareDaoImpl<DocumentationOf> implements DocumentationOfDao {

    public DocumentationOfDaoImpl() {
        super(DocumentationOf.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (DocumentationOf documentationOf : this.listByResidentId(residentId)) {
            this.delete(documentationOf);
            ++count;
        }

        return count;
    }

}
