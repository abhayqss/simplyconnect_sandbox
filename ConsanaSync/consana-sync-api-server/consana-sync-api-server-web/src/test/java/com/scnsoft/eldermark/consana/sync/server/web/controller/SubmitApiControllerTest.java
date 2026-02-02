package com.scnsoft.eldermark.consana.sync.server.web.controller;

import com.scnsoft.eldermark.consana.sync.server.facade.ConsanaPatientUpdateFacade;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmitApiControllerTest {

    @Mock
    private ConsanaPatientUpdateFacade facade;

    @InjectMocks
    private SubmitApiController instance;

    @Test
    void sync_ShouldSendToFacade() {
        var input = new ConsanaSyncDto();

        var response = instance.sync(input);

        assertEquals(ResponseEntity.accepted().<Void>build(), response);
        verify(facade).convertAndSendToQueue(input);
    }
}