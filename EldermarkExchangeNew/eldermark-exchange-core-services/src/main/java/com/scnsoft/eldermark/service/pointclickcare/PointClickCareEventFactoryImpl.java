package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dao.pointclickcare.PccAdtRecordEntityDao;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import com.scnsoft.eldermark.dto.pointclickcare.filter.adt.PccAdtListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetails;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.pointclickcare.PccAdtRecordEntity;
import com.scnsoft.eldermark.service.EventTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
@Transactional
public class PointClickCareEventFactoryImpl implements PointClickCareEventFactory {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCareEventFactoryImpl.class);

    private final PointClickCareApiGateway apiGateway;
    private final EventTypeService eventTypeService;
    private final BiFunction<PccADTRecordDetails, Client, PccAdtRecordEntity> pccAdtRecordConverter;
    private final PccAdtRecordEntityDao pccAdtRecordEntityDao;

    @Autowired
    public PointClickCareEventFactoryImpl(PointClickCareApiGateway apiGateway,
                                          EventTypeService eventTypeService,
                                          BiFunction<PccADTRecordDetails, Client, PccAdtRecordEntity> pccAdtRecordConverter,
                                          PccAdtRecordEntityDao pccAdtRecordEntityDao) {
        this.apiGateway = apiGateway;
        this.eventTypeService = eventTypeService;
        this.pccAdtRecordConverter = pccAdtRecordConverter;
        this.pccAdtRecordEntityDao = pccAdtRecordEntityDao;
    }

    @Override
    public Event createEvent(Client client, String eventTypeCode, String defaultSituation, Long pccAdtRecordId) {
        logger.info("PointClickCare Event: Creating event for client {} with event type {}, default situation {}, pccAdtRecordId {}",
                client.getId(), eventTypeCode, defaultSituation, pccAdtRecordId);
        var pccAdtRecord = fetchAdtRecord(
                client.getOrganization().getPccOrgUuid(),
                client.getCommunity().getPccFacilityId(),
                pccAdtRecordId
        );
        logger.info("PointClickCare Event: Fetched ADT Record for pccAdtRecordId {}", pccAdtRecordId);

        var adtRecord = saveAdtRecord(pccAdtRecord, client);
        logger.info("PointClickCare Event: Saved ADT Record with pccAdtRecordId {} to DB, id {}", pccAdtRecordId, adtRecord.getId());

        var event = new Event();

        event.setClient(client);
        event.setPccAdtRecordEntity(adtRecord);

        event.setEventType(eventTypeService.findByCode(eventTypeCode));
        event.setIsFollowup(false);

        event.setEventDateTime(Instant.now());

        final EventAuthor author = new EventAuthor();
        if (StringUtils.isNotEmpty(pccAdtRecord.getEnteredBy())) {
            var spaceIdx = pccAdtRecord.getEnteredBy().indexOf(' ');
            String firstName;
            String lastName;
            if (spaceIdx == -1) {
                firstName = pccAdtRecord.getEnteredBy();
                lastName = "";
            } else {
                firstName = pccAdtRecord.getEnteredBy().substring(0, spaceIdx);
                lastName = pccAdtRecord.getEnteredBy().substring(Math.min(spaceIdx + 1, pccAdtRecord.getEnteredBy().length()));
            }
            author.setFirstName(firstName);
            author.setLastName(lastName);
        }

        author.setOrganization("PointClickCare");
        author.setRole("");
        event.setEventAuthor(author);
        event.setSituation(Optional.ofNullable(adtRecord.getActionType()).filter(StringUtils::isNotEmpty).orElse(defaultSituation));
        logger.info("PointClickCare Event: Event created");

        return event;
    }

    private PccADTRecordDetails fetchAdtRecord(String orgUuid, Long facId, Long pccAdtRecordId) {
        var filter = new PccAdtListFilter();
        filter.setAdtRecordIds(List.of(pccAdtRecordId));
        filter.setFacId(facId);

        var adtListResponse = apiGateway.adtList(orgUuid, filter, 1, 1);
        if (adtListResponse == null || CollectionUtils.isEmpty(adtListResponse.getData())) {
            throw new PointClickCareApiException("No adt records returned for id " + pccAdtRecordId);
        }
        return adtListResponse.getData().get(0);
    }

    private PccAdtRecordEntity saveAdtRecord(PccADTRecordDetails pccAdtRecord, Client client) {
        var adtEntity = pccAdtRecordConverter.apply(pccAdtRecord, client);
        adtEntity.setSavedAt(Instant.now());
        return pccAdtRecordEntityDao.save(adtEntity);
    }
}
