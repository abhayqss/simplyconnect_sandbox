package com.scnsoft.eldermark.service.inbound.philips;

import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventCSV;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.EventTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
public class PhilipsEventServiceImpl implements PhilipsEventService {

    private static final Logger logger = LoggerFactory.getLogger(PhilipsEventServiceImpl.class);

    private final ZoneId EVENT_ZONE_ID = ZoneId.of("America/New_York");
    private final String BLEEDING_INJURY = "Bleeding/Injury";

    @Autowired
    private EventService eventService;
    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private ClientService clientService;


    @Override
    @Transactional
    public Event createEvent(PhilipsEventCSV philipsEventCSV) {
        var result = new Event();
        result.setClient(clientService.findById(Long.valueOf(philipsEventCSV.getMrn())));
        if (result.getClient() == null) {
            throw new PhilipsFileProcessingException("unknown client for mrn: " + philipsEventCSV.getMrn());
        }
        result.setEventAuthor(createEventAuthor());

        var eventDateTime = philipsEventCSV.getCreatedDate().atZone(EVENT_ZONE_ID).toInstant();
        result.setEventDateTime(eventDateTime);

        result.setEventType(eventTypeService.findByCode(getEventTypeCode(philipsEventCSV.getSituation())));
        result.setSituation(philipsEventCSV.getSituation());
        result.setAssessment(philipsEventCSV.getOutcome());
        if (philipsEventCSV.getSituation().equals(BLEEDING_INJURY)) {
            result.setInjury(true);
        }
        Event saved = eventService.save(result);
        logger.warn("saved event id {}", saved.getId());
        return saved;
    }

    private EventAuthor createEventAuthor() {
        var result = new EventAuthor();
        result.setFirstName("Philips");
        result.setLastName("device");
        result.setRole("");
        result.setOrganization("Philips");
        return result;
    }

    private String getEventTypeCode(String situation) {
        switch (situation) {
            case "PanicAttack":
            case "Anxious":
                return "CB";
            case "Assaulted":
            case "AutomobileAccident":
            case "BreathingProblem":
            case "Burglary":
            case "Confused":
            case "DomesticProblem":
            case "MedEquipIssue":
            case "NoResponse":
            case "ReportinganEvent":
            case "ReportingCrime":
            case "RequestAssistance":
            case "RequestNurse/Aide":
            case "RequestResponder":
            case "RequestTransportation":
            case "SeeCaseNotes":
            case "SubOK":
            case "Unknown":
            case "UnknownAlarm":
            case "UtilityProblem":
                return "REMOTE";
            case "Bleeding/Injury":
            case "BrokenBone":
                return "SI";

            case "Can'tWalk":
            case "ChestPain":
            case "DiabeticReaction":
            case "Diarrhea":
            case "Dizziness":
            case "Fever":
            case "Illness":
            case "Nausea/Vomiting":
            case "PainNonchest":
            case "Seizure":
            case "Stroke/Numbness":
                return "GENERAL";

            case "Fell":
                return "FALL";
            case "Fire":
                return "FIRE";

            case "RequestMedication":
                return "MEDAL";

            case "SubinHospital":
                return "H";

            case "Unconscious":
                return "ME";
            case "Deceased":
                return "DSCS";
        }
        throw new PhilipsFileProcessingException("situation is unknown");
    }
}
