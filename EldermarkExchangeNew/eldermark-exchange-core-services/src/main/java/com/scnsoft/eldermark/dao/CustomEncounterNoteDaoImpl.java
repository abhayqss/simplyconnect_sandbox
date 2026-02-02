package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.EncounterNote_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomEncounterNoteDaoImpl implements CustomEncounterNoteDao {

    private static final int BATCH_SIZE = 2000;

    private final EntityManager entityManager;

    @Autowired
    public CustomEncounterNoteDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Map<Long, Set<Long>> findClientIdsByEncounterNoteIdMap(List<Long> ids) {
        return IntStream.iterate(0, i -> i < ids.size(), i -> i + BATCH_SIZE)
                .mapToObj(i -> ids.subList(i, Math.min(i + BATCH_SIZE, ids.size())))
                .map(this::findClientIdsOfListOfIds)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(tuple -> tuple.get(EncounterNote_.ID, Long.class), Collectors.mapping(tuple -> tuple.get(EncounterNote_.CLIENT_ID, Long.class), Collectors.toSet())));
    }

    @Override
    public Map<Long, List<Long>> findGroupNoteClientIds(Specification<EncounterNote> specification) {
        var cb = entityManager.getCriteriaBuilder();

        var crq = cb.createTupleQuery();
        crq.distinct(true);
        var root = crq.from(EncounterNote.class);
        var clientJoin = root.join(EncounterNote_.noteClientIds);
        crq.multiselect(
                root.get(EncounterNote_.id).alias(EncounterNote_.ID),
                clientJoin.alias(EncounterNote_.CLIENT_ID)
        );
        crq.where(specification.toPredicate(root, crq, cb));

        var typed = entityManager.createQuery(crq);

        var resultList = typed.getResultList();

        return resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(EncounterNote_.ID, Long.class),
                        Collectors.mapping(tuple -> tuple.get(EncounterNote_.CLIENT_ID, Long.class),
                                Collectors.toList())
                ));
    }

    private List<Tuple> findClientIdsOfListOfIds(List<Long> ids) {
        var cb = entityManager.getCriteriaBuilder();
        var crq = cb.createQuery(Tuple.class);
        crq.distinct(true);
        var root = crq.from(EncounterNote.class);
        var clientJoin = root.join(EncounterNote_.noteClientIds);

        crq.multiselect(
                root.get(EncounterNote_.id).alias(EncounterNote_.ID),
                clientJoin.alias(EncounterNote_.CLIENT_ID)
        );
        crq.where(root.get(EncounterNote_.id).in(ids));
        var typed = entityManager.createQuery(crq);
        return typed.getResultList();
    }
}
