package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.pointclickcare.PccAdtRecordEntityDao;
import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetailsList;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.event.EventType;
import com.scnsoft.eldermark.entity.pointclickcare.PccAdtRecordEntity;
import com.scnsoft.eldermark.service.EventTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointClickCareEventFactoryImplTest {

    @Mock
    private PointClickCareApiGateway apiGateway;

    @Mock
    private EventTypeService eventTypeService;

    @Mock
    private BiFunction<PccADTRecordDetails, Client, PccAdtRecordEntity> pccAdtRecordConverter;

    @Mock
    private PccAdtRecordEntityDao pccAdtRecordEntityDao;

    @InjectMocks
    private PointClickCareEventFactoryImpl instance;

    @Test
    void createEvent() {
        var orgUuid = "1234asdf1234";
        var facId = 44L;
        var org = new Organization();
        org.setPccOrgUuid(orgUuid);
        var comm = new Community();
        comm.setPccFacilityId(facId);
        var client = new Client();
        client.setOrganization(org);
        client.setCommunity(comm);
        var situation = "situation";
        var eventTypeCode = "code";
        var pccAdtRecordId = 10L;

        var pccAdtList = new PccADTRecordDetailsList();
        var pccAdtRecord = new PccADTRecordDetails();
        pccAdtList.setData(List.of(pccAdtRecord));
        pccAdtRecord.setEnteredBy("James Bond");

        var adtEntity = new PccAdtRecordEntity();
        var eventType = new EventType();

        when(apiGateway.adtList(
                eq(orgUuid),
                argThat(filter -> filter.getAdtRecordIds().size() == 1 && filter.getAdtRecordIds().get(0).equals(pccAdtRecordId)
                        && filter.getFacId() == facId),
                eq(1),
                eq(1))).thenReturn(pccAdtList);
        when(pccAdtRecordConverter.apply(pccAdtRecord, client)).thenReturn(adtEntity);
        when(pccAdtRecordEntityDao.save(adtEntity)).thenReturn(adtEntity);
        when(eventTypeService.findByCode(eventTypeCode)).thenReturn(eventType);


        var event = instance.createEvent(client, eventTypeCode, situation, pccAdtRecordId);

        assertThat(event).isNotNull();
        assertThat(event.getClient()).isEqualTo(client);
        assertThat(event.getEventAuthor().getOrganization()).isEqualTo("PointClickCare");
        assertThat(event.getEventAuthor().getFirstName()).isEqualTo("James");
        assertThat(event.getEventAuthor().getLastName()).isEqualTo("Bond");
        assertThat(event.getEventAuthor().getRole()).isEmpty();
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getSituation()).isEqualTo(situation);
        assertThat(event.getPccAdtRecordEntity()).isEqualTo(adtEntity);
        assertThat(event.getIsFollowup()).isFalse();
        assertThat(event.getEventDateTime()).isNotNull();
    }
}