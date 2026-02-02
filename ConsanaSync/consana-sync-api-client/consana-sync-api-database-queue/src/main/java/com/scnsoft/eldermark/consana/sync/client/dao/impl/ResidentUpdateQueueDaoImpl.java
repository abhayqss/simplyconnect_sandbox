package com.scnsoft.eldermark.consana.sync.client.dao.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentUpdateQueueDao;
import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@SqlResultSetMapping(
        name = "ResidentUpdateDatabaseQueueBody",
        classes = {
                @ConstructorResult(
                        targetClass = ResidentUpdateDatabaseQueueBody.class,
                        columns = {
                                @ColumnResult(name = "residentId", type = Long.class),
                                @ColumnResult(name = "updateType", type = ResidentUpdateType.class),
                                @ColumnResult(name = "updateTime", type = Date.class)
                        }
                )
        }
)
public class ResidentUpdateQueueDaoImpl implements ResidentUpdateQueueDao {

    private final EntityManager entityManager;
    private static final String PROCEDURE_NAME = "dequeue_resident_update_queue";
    private static final String BATCH_SIZE_PARAM = "batchSize";

    @Autowired
    public ResidentUpdateQueueDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<ResidentUpdateDatabaseQueueBody> deque(int batchSize) {
        var storedProcedure = entityManager.createStoredProcedureQuery(PROCEDURE_NAME);
        storedProcedure.registerStoredProcedureParameter(BATCH_SIZE_PARAM, Long.class, ParameterMode.IN);

        storedProcedure.setParameter(BATCH_SIZE_PARAM, (long) batchSize);

        @SuppressWarnings("unchecked") var resultList = (List<Object[]>) storedProcedure.getResultList();
        return resultList.stream().map(this::resultMapper).collect(Collectors.toList());
    }

    private ResidentUpdateDatabaseQueueBody resultMapper(Object[] arr) {
        return new ResidentUpdateDatabaseQueueBody(
                mapResidentId(arr),
                mapUpdateType(arr),
                mapUpdateTime(arr)
        );
    }

    private Long mapResidentId(Object[] arr) {
        return ((BigInteger) arr[0]).longValue();
    }

    private ResidentUpdateType mapUpdateType(Object[] arr) {
        return ResidentUpdateType.valueOf((String) arr[1]);
    }

    private Long mapUpdateTime(Object[] arr) {
        return ((Timestamp) arr[2]).getTime();
    }
}

