package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorDaoImpl extends ResidentAwareDaoImpl<Author> implements AuthorDao {

    public AuthorDaoImpl() {
        super(Author.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Author author : this.listByResidentId(residentId)) {
            this.delete(author);
            ++count;
        }

        return count;
    }

}
