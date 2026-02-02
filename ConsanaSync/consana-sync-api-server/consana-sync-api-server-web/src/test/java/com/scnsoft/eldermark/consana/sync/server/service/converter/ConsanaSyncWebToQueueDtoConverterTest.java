package com.scnsoft.eldermark.consana.sync.server.service.converter;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.server.common.model.dto.ReceiveConsanaPatientQueueDto;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ConsanaSyncWebToQueueDtoConverterTest {

    @InjectMocks
    private ConsanaSyncWebToQueueDtoConverter instance;

    @Test
    void convert_PatientUpdate() {
        var input1 = new ConsanaSyncDto("1", "orgId1", "comId1", ConsanaSyncDto.UpdateTypeEnum.PATIENT_UPDATE);
        var expected1 = new ReceiveConsanaPatientQueueDto("1", "orgId1", "comId1", ConsanaPatientUpdateType.PATIENT_UPDATE);

        var result1 = instance.convert(input1);

        assertEquals(expected1, result1);
    }

    @Test
    void convert_mapClosed() {
        var input2 = new ConsanaSyncDto("2", "orgId2", "comId2", ConsanaSyncDto.UpdateTypeEnum.MAP_CLOSED);
        var expected2 = new ReceiveConsanaPatientQueueDto("2", "orgId2", "comId2", ConsanaPatientUpdateType.MAP_CLOSED);

        var result2 = instance.convert(input2);

        assertEquals(expected2, result2);
    }
}