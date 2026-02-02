package com.scnsoft.eldermark.consana.sync.client.dao.impl;

import com.scnsoft.eldermark.consana.sync.client.model.entities.MergedClientView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.*;
import javax.persistence.criteria.*;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergedResidentsDaoFragmentImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private MergedResidentsDaoFragmentImpl instance;

    @SuppressWarnings("unchecked")
    @Test
    void getMergedResidentIds_ShouldReturnMergedResidentIds() {
        final CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        final CriteriaQuery<Long> criteriaQuery = Mockito.mock(CriteriaQuery.class);
        final Root<MergedClientView> root = Mockito.mock(Root.class);

        final Path clientIdExpression = Mockito.mock(Path.class);
        final Path mergedClientIdExpression = Mockito.mock(Path.class);

        final Predicate wherePredicate = Mockito.mock(Predicate.class);

        final TypedQuery<Long> query = Mockito.mock(TypedQuery.class);

        var residentId = 1L;
        var expectedList = List.of(residentId, residentId + 1L);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Long.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(MergedClientView.class)).thenReturn(root);

        when(root.get(MergedClientView.CLIENT_ID)).thenReturn(clientIdExpression);
        when(root.get(MergedClientView.MERGED_CLIENT_ID)).thenReturn(mergedClientIdExpression);

        when(criteriaBuilder.equal(clientIdExpression, residentId)).thenReturn(wherePredicate);

        when(criteriaQuery.select(mergedClientIdExpression)).thenReturn(criteriaQuery);
        when(criteriaQuery.where(wherePredicate)).thenReturn(criteriaQuery);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedList);

        var resultList = instance.getMergedResidentIds(residentId);

        assertIterableEquals(expectedList, resultList);
    }
}