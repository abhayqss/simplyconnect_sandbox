package com.scnsoft.eldermark.consana.sync.client.services.queue.database.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentUpdateQueueDao;
import com.scnsoft.eldermark.consana.sync.client.model.ResidentUpdateDatabaseQueueBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResidentUpdateDatabaseQueueServiceImplTest {

    @Mock
    private ResidentUpdateQueueDao residentUpdateQueueDao;

    @InjectMocks
    private ResidentUpdateDatabaseQueueServiceImpl instance;

    private static final int BATCH_SIZE = 10;

    @BeforeEach
    private void injectBatchSize() {
        ReflectionTestUtils.setField(instance, "batchSize", BATCH_SIZE);
    }

    @Test
    void dequeueBatch_WhenDaoReturnsList_ShouldReturnItsStream() {
        @SuppressWarnings("unchecked") var list = (List<ResidentUpdateDatabaseQueueBody>) mock(List.class);
        @SuppressWarnings("unchecked") var stream = (Stream<ResidentUpdateDatabaseQueueBody>) mock(Stream.class);

        when(residentUpdateQueueDao.deque(BATCH_SIZE)).thenReturn(list);
        when(list.stream()).thenReturn(stream);
        when(stream.peek(any())).thenReturn(stream);

        var result = instance.dequeueBatch();

        assertEquals(stream, result);
    }
}