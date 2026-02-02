package com.scnsoft.eldermark.openxds.api.facade;

import com.scnsoft.eldermark.dao.AdmittanceHistoryDao;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.PIDSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.message.PV1SegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import com.scnsoft.eldermark.openxds.api.beans.AdtType;
import com.scnsoft.eldermark.openxds.api.dto.AdtDto;
import com.scnsoft.eldermark.openxds.api.exceptions.OpenXdsApiException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Service
public class AdtEventFacadeImpl implements AdtEventFacade {

    private static final Logger logger = LoggerFactory.getLogger(AdtEventFacadeImpl.class);

    private Map<AdtType, String> EVENT_TRIGGERING_ADT_TYPES;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @PostConstruct
    public void postConstruct() {
        EVENT_TRIGGERING_ADT_TYPES = new EnumMap<>(AdtType.class);

        var eadtCode = "EADT";
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A01, eadtCode);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A03, eadtCode);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A04, eadtCode);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A05, eadtCode);

        var pruCode = "PRU";
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A08, pruCode);
        EVENT_TRIGGERING_ADT_TYPES.put(AdtType.A60, pruCode);
    }

    @Override
    @Transactional
    public void create(AdtDto adtDto) {
        if (!EVENT_TRIGGERING_ADT_TYPES.containsKey(adtDto.getAdtType())) {
            logger.warn("Unsupported ADT message type: " + adtDto.getAdtType());
            return;
        }
        var event = createAdtEvent(adtDto);
        eventService.save(event);
    }

    private Event createAdtEvent(AdtDto adtDto) {

        var adtResident = clientService.findById(adtDto.getResidentId());

        if (adtResident == null) {
            logger.warn("ADT Resident id=[{}] not found!", adtDto.getResidentId());
            throw new OpenXdsApiException("ADT Resident id=[" + adtDto.getResidentId() + "] not found!");
        }

        if (Boolean.TRUE.equals(adtDto.getIsNewPatient())) {
//            try {
//                xdsRegistryConnectorService.saveCcdInRegistry(adtResident.getId());
//            } catch (NHINIoException e) {
//                logger.error(e.getMessage(), e);
//            }
        }
        Event eventEntity = new Event();
        eventEntity.setAdtMsgId(adtDto.getMsgId());
        if (adtDto.getMsgId() != null) {
            final AdtMessage adtMessage = adtMessageDao.findById(adtDto.getMsgId()).orElseThrow();

            if (!adtMessage.getClass().equals(adtDto.getAdtType().getEntityClass())) {
                throw new OpenXdsApiException("Adt message and provided atd types don't match: " +
                        adtMessage.getClass().getSimpleName() +
                        " vs " + adtDto.getAdtType().getEntityClass().getSimpleName());
            }
            if (adtMessage instanceof PV1SegmentContainingMessage) {
                final PV1SegmentContainingMessage pvMes = (PV1SegmentContainingMessage) adtMessage;
                if (pvMes.getPv1() != null && "E".equals(pvMes.getPv1().getPatientClass().getRawCode())) {
                    eventEntity.setIsErVisit(true);
                }
            } else {
                logger.info("Incoming adt message [{}] is not of type PV1SegmentContainingMessage", adtMessage.getId());
            }

            processAdmitDischargeDates(adtMessage, adtResident);
            processDeathDate(adtMessage, adtResident);
            clientService.save(adtResident);

        } else {
            logger.warn("Adt message id is not present");
        }
        eventEntity.setClient(adtResident);
        eventEntity.setEventType(eventTypeService.findByCode(EVENT_TRIGGERING_ADT_TYPES.get(adtDto.getAdtType())));
        eventEntity.setIsFollowup(false);

        eventEntity.setEventDateTime(Instant.now());

        final EventAuthor author = new EventAuthor();
        author.setFirstName("ADT");
        author.setLastName("Repository");
        author.setOrganization("XDS.b");
        author.setRole("");
        eventEntity.setEventAuthor(author);
        eventEntity.setSituation(adtDto.getAdtType().getDescription());

        return eventEntity;
    }


    public void processAdmitDischargeDates(AdtMessage adt, Client client) {
        if (!(adt instanceof PV1SegmentContainingMessage)) {
            return;
        }
        final PV1SegmentContainingMessage adtMessage = (PV1SegmentContainingMessage) adt;
        var patientVisitSegment = adtMessage.getPv1();

        //update resident's admit/discharge date fields
        var adtDischargeDatetime = patientVisitSegment.getDischargeDatetime();
        var latestExistingDischargeDate = client.getDischargeDate();
        if (adtDischargeDatetime != null) {
            if (latestExistingDischargeDate == null || latestExistingDischargeDate.isBefore(adtDischargeDatetime)) {
                client.setDischargeDate(adtDischargeDatetime);
            }
        }
        var adtAdmitDateTime = patientVisitSegment.getAdmitDatetime();
        var latestExistingAdmitDate = client.getAdmitDate();
        if (adtAdmitDateTime != null) {
            if (latestExistingAdmitDate == null || latestExistingAdmitDate.isBefore(adtAdmitDateTime)) {
                client.setAdmitDate(adtAdmitDateTime);
            }
        }

        //create/update resident admittance history record
        AdmittanceHistory admittanceHistory = null;
        if (adtAdmitDateTime != null) {
            admittanceHistory = admittanceHistoryDao.findByClientIdAndAdmitDate(client.getId(), adtAdmitDateTime);
        }
        if (admittanceHistory == null && adtDischargeDatetime != null) {
            admittanceHistory = admittanceHistoryDao.findByClientIdAndDischargeDate(client.getId(), adtDischargeDatetime);
        }
        if (admittanceHistory == null) {
            admittanceHistory = new AdmittanceHistory();
        }

        admittanceHistory.setAdmitDate(adtAdmitDateTime);
        admittanceHistory.setDischargeDate(adtDischargeDatetime);

        var organization = client.getOrganization();
        admittanceHistory.setOrganization(organization);
        admittanceHistory.setCommunityId(client.getCommunityId());
        if (organization != null) {
            admittanceHistory.setOrganizationId(organization.getId());
        }
        admittanceHistory.setClient(client);
        admittanceHistoryDao.save(admittanceHistory);
    }

    public void processDeathDate(AdtMessage adt, Client client) {
        if (!(adt instanceof PIDSegmentContainingMessage)) {
            return;
        }
        final PIDSegmentContainingMessage adtMessage = (PIDSegmentContainingMessage) adt;
        final PIDPatientIdentificationSegment patientIdentificationSegment = adtMessage.getPid();

        if (patientIdentificationSegment.getPatientDeathDateAndTime() != null) {
            client.setDeathDate(patientIdentificationSegment.getPatientDeathDateAndTime());
        }
    }
}
