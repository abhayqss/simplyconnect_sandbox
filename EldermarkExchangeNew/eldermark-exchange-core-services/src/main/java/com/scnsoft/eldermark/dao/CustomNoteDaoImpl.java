package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.Note_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CustomNoteDaoImpl implements CustomNoteDao {

    private final EntityManager entityManager;

    @Autowired
    public CustomNoteDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, List<Long>> findGroupNoteClientIds(Specification<Note> specification) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);
        var root = crq.from(Note.class);
        var clientJoin = root.join(Note_.noteClientIds);
        crq.multiselect(
                root.get(Note_.id).alias(Note_.ID),
                clientJoin.alias(Note_.CLIENT_ID)
        );
        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();

        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(Note_.ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(Note_.CLIENT_ID, Long.class),
                                Collectors.toList())
                ));
    }
}
