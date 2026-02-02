package com.scnsoft.eldermark.consana.sync.client.dao.impl;

import com.scnsoft.eldermark.consana.sync.client.TestUtils;
import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResidentUpdateQueueDaoImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ResidentUpdateQueueDaoImpl instance;

    private static final String PROCEDURE_NAME = "dequeue_resident_update_queue";
    private static final int BATCH_SIZE = 12;

    @Test
    void deque_WhenCalled_ShouldCallProcedureAndReturnList() {
        var time = Instant.now();
        final StoredProcedureQuery storedProcedureQuery = Mockito.mock(StoredProcedureQuery.class);
        final List<Object[]> procedureResultList = List.of(
                new Object[]{
                        BigInteger.valueOf(1),
                        ResidentUpdateType.ALLERGY.toString(),
                        Timestamp.from(time)
                },
                new Object[]{
                        BigInteger.valueOf(2),
                        ResidentUpdateType.MEDICATION.toString(),
                        Timestamp.from(time)
                }
        );
        final List<ResidentUpdateDatabaseQueueBody> transformedList = List.of(
                TestUtils.buildResidentUpdateDatabaseQueueBody(
                        1L,
                        ResidentUpdateType.ALLERGY,
                        time.toEpochMilli()
                ),
                TestUtils.buildResidentUpdateDatabaseQueueBody(
                        2L,
                        ResidentUpdateType.MEDICATION,
                        time.toEpochMilli()
                ));

        when(entityManager.createStoredProcedureQuery(PROCEDURE_NAME)).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getResultList()).thenReturn(procedureResultList);

        var resultList = instance.deque(BATCH_SIZE);

        assertIterableEquals(transformedList,resultList);
        verify(storedProcedureQuery).registerStoredProcedureParameter("batchSize", Long.class, ParameterMode.IN);
        verify(storedProcedureQuery).setParameter("batchSize", (long) BATCH_SIZE);
    }
}