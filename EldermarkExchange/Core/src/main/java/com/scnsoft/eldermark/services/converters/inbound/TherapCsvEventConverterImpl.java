package com.scnsoft.eldermark.services.converters.inbound;

import com.google.common.collect.ImmutableMap;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapEventCSV;
import com.scnsoft.eldermark.schema.Address;
import com.scnsoft.eldermark.schema.*;
import com.scnsoft.eldermark.schema.Organization;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.EventTypeService;
import com.scnsoft.eldermark.services.exceptions.BusinessException;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

@Component
public class TherapCsvEventConverterImpl implements TherapCsvEventConverter {

    private static final Logger logger = LoggerFactory.getLogger(TherapCsvEventConverterImpl.class);
    private static final int EVENT_TYPE_OTHER = 9999;

    private final Map<Integer, EventType> eventTypeMapping = new HashMap<>();
    private final Map<Integer, EventType> otherEventTypeMapping = new HashMap<>();
    private final Map<Integer, String> maritalStatusMapping = new HashMap<>();
    private final Map<Integer, Map<Integer, String>> eventSubTypeMapping = new HashMap<>();

    private final List<Integer> deathFlaggedEventTypes = Collections.singletonList(5);      // Death
    private final List<Integer> deathFlaggedEventOtherTypes = Collections.singletonList(14);  // Suicide

    private final EventTypeService eventTypeService;
    private final StateService stateService;
    private final CcdCodeDao ccdCodeDao;

    @Autowired
    public TherapCsvEventConverterImpl(EventTypeService eventTypeService, StateService stateService, CcdCodeDao ccdCodeDao) {
        this.eventTypeService = eventTypeService;
        this.stateService = stateService;
        this.ccdCodeDao = ccdCodeDao;
    }

    @PostConstruct
    void initMappings() {
        eventTypeMapping.put(1, eventTypeService.getByCode("GENERAL"));      // Injury
        eventTypeMapping.put(2, eventTypeService.getByCode("MERR"));    // Medication Error
        eventTypeMapping.put(3, eventTypeService.getByCode("CB"));      // Restraint Related to Behavior
        eventTypeMapping.put(4, eventTypeService.getByCode("CB"));      // Restraint Other
        eventTypeMapping.put(5, eventTypeService.getByCode("EADT"));    // Death

        otherEventTypeMapping.put(1, eventTypeService.getByCode("GENERAL"));    // Accident no apparent injury
        otherEventTypeMapping.put(3, eventTypeService.getByCode("CB"));         // Altercation
        otherEventTypeMapping.put(4, eventTypeService.getByCode("PA"));         // Assault
        otherEventTypeMapping.put(5, eventTypeService.getByCode("CB"));         // AWOL/Missing Person
        otherEventTypeMapping.put(22, eventTypeService.getByCode("CB"));        // Behavioral Issue
        otherEventTypeMapping.put(18, eventTypeService.getByCode("GENERAL"));   // Change of Condition
        otherEventTypeMapping.put(21, eventTypeService.getByCode("SEVA"));      // Complaint and/or Possible Litigation
        otherEventTypeMapping.put(20, eventTypeService.getByCode("CB"));        // Contraband
        otherEventTypeMapping.put(7, eventTypeService.getByCode("SEVA"));       // Exploitation
        otherEventTypeMapping.put(23, eventTypeService.getByCode("GENERAL"));   // Fall Without Injury
        otherEventTypeMapping.put(8, eventTypeService.getByCode("FIRE"));       // Fire
        otherEventTypeMapping.put(9, eventTypeService.getByCode("H"));          // Hospital
        otherEventTypeMapping.put(2, eventTypeService.getByCode("MHRI"));       // Inappropriate Alcohol/Drug Use
        otherEventTypeMapping.put(10, eventTypeService.getByCode("CI"));        // Law Enforcement Involvement
        otherEventTypeMapping.put(25, eventTypeService.getByCode("EADT"));      // Out of Home Placement
        otherEventTypeMapping.put(6, eventTypeService.getByCode("CI"));         // Possible Criminal Activity/Misconduct
        otherEventTypeMapping.put(16, eventTypeService.getByCode("ARD"));       // Potential Incident/Near Miss
        otherEventTypeMapping.put(17, eventTypeService.getByCode("MEDAL"));     // PRN Psychotropic Use
        otherEventTypeMapping.put(11, eventTypeService.getByCode("CB"));        // Property Damage
        otherEventTypeMapping.put(19, eventTypeService.getByCode("SEVA"));      // Security Breach
        otherEventTypeMapping.put(12, eventTypeService.getByCode("SEVA"));      // Sensitive Situation
        otherEventTypeMapping.put(13, eventTypeService.getByCode("USI"));       // Serious Illness
        otherEventTypeMapping.put(14, eventTypeService.getByCode("EADT"));      // Suicide
        otherEventTypeMapping.put(15, eventTypeService.getByCode("EADT"));      // Theft/Larceny Attempt
        otherEventTypeMapping.put(24, eventTypeService.getByCode("CB"));        // Threatening Behavior
        otherEventTypeMapping.put(9999, eventTypeService.getByCode("PRU"));     // Other

        maritalStatusMapping.put(1, "Divorced");            // Divorced
        maritalStatusMapping.put(2, "Married");             // Married
        maritalStatusMapping.put(3, "Legally Separated");   // Separated
        maritalStatusMapping.put(4, "Never Married");       // Single
        maritalStatusMapping.put(5, null);                  // Unknown
        maritalStatusMapping.put(6, "Widowed");             // Widowed

        eventSubTypeMapping.put(3, ImmutableMap.of(
                1, "Staff/Individual",
                2, "Individual/Individual",
                9999, "Other"));
        eventSubTypeMapping.put(4, ImmutableMap.of(
                1, "Aggressor",
                2, "Victim"));
        eventSubTypeMapping.put(20, ImmutableMap.of(
                1, "Weapon of Convenience",
                2, "Manufactured Weapon",
                3, "Drugs",
                9999, "Other"));
        eventSubTypeMapping.put(8, ImmutableMap.of(
                3, "Accidental/Cause Unknown",
                1, "Attempted/Caused by Individual",
                5, "False Alarm/Caused by Individual",
                4, "False Alarm/Equipment Failure",
                2, "Minor/Smoke"));
        eventSubTypeMapping.put(9, ImmutableMap.of(
                1, "Admission",
                2, "ER w/o admission"));
        eventSubTypeMapping.put(2, ImmutableMap.of(
                1, "Alcohol",
                2, "Illegal Drugs",
                3, "OTC Medication",
                4, "Prescription Medication"));
        eventSubTypeMapping.put(25, new HashMap<Integer, String>() {
            {
                put(1, "Crisis Placement");
                put(2, "Developmental Center");
                put(3, "Hospice Facility");
                put(4, "Hospital");
                put(5, "ICF");
                put(6, "Jail");
                put(7, "Nursing Home");
                put(8, "Rehab");
                put(9, "Respite");
            }
        });
        eventSubTypeMapping.put(14, ImmutableMap.of(
                1, "Attempt",
                2, "Threat"));
        eventSubTypeMapping.put(15, ImmutableMap.of(
                1, "Perpetrator",
                2, "Victim"));

    }

    @Override
    public Organization convertToOrganizationSchema(TherapEventCSV eventCSV) {
        if (StringUtils.isAnyEmpty(eventCSV.getOrganizationId(), eventCSV.getOrganizationName())) {
            throw new BusinessException("Mandatory organization id or name is missing, can't proceed");
        }

        final Organization organization = new Organization();

        organization.setID(eventCSV.getOrganizationId());
        organization.setName(eventCSV.getOrganizationName());
        organization.setPhone(eventCSV.getOrganizationPhone());
        organization.setEmail(eventCSV.getOrganizationEmail());

        return organization;
    }

    @Override
    public Community convertToCommunitySchema(TherapEventCSV eventCSV) {
        if (StringUtils.isAnyEmpty(eventCSV.getCommunityId(), eventCSV.getCommunityName())) {
            throw new BusinessException("Mandatory community id or name is missing, can't proceed");
        }

        final Community community = new Community();

        community.setID(eventCSV.getCommunityId());
        community.setName(eventCSV.getCommunityName());
        community.setPhone(eventCSV.getCommunityPhone());
        community.setEmail(eventCSV.getCommunityEmail());

        return community;
    }

    @Override
    public Patient convertToPatientSchema(TherapEventCSV eventCSV) {
        if (StringUtils.isAnyEmpty(eventCSV.getIndividualFirstname(), eventCSV.getIndividualLastname(),
                eventCSV.getIndividualSsn(), eventCSV.getIndividualGenger())
                || eventCSV.getIndividualDateOfBirth() == null) {
            throw new BusinessException("Any of INDIVIDUALFIRSTNAME, INDIVIDUALLASTNAME, INDIVIDUALSSN, INDIVIDUALGENDER, INDIVIDUALDATEOFBIRTH is missing");

        }
        final Patient patient = new Patient();

        patient.setPatientId(eventCSV.getIndividualIdNumber());
        patient.setName(convertToPatientName(eventCSV));
        patient.setDateOfBirth(toXmlGregorianCalendar(eventCSV.getIndividualDateOfBirth()));
        patient.setSSN(eventCSV.getIndividualSsn());
        patient.getAddress().add(convertToPatientAddress(eventCSV));
        patient.setGender(eventCSV.getIndividualGenger());
        patient.setMaritalStatus(maritalStatusMapping.get(eventCSV.getIndividualMaritalStatus()));

        return patient;
    }

    private Address convertToPatientAddress(TherapEventCSV eventCSV) {
        if (StringUtils.isAnyEmpty(eventCSV.getIndividualAddressZip(), eventCSV.getIndividualAddressState(),
                eventCSV.getIndividualAddressCity(), eventCSV.getIndividualAddressStreet())) {
            return null;
        }
        final Address address = new Address();

        address.setZip(eventCSV.getIndividualAddressZip());
        address.setState(eventCSV.getIndividualAddressState());
        address.setCity(eventCSV.getIndividualAddressCity());
        address.setStreet(eventCSV.getIndividualAddressStreet());

        return address;
    }

    private PersonName convertToPatientName(TherapEventCSV eventCSV) {
        final PersonName personName = new PersonName();

        personName.setFirstName(eventCSV.getIndividualFirstname());
        personName.setLastName(eventCSV.getIndividualLastname());

        return personName;
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(Calendar individualDateOfBirth) {
        if (individualDateOfBirth == null) {
            return null;
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(individualDateOfBirth.getTime());

        return new XMLGregorianCalendarImpl(gregorianCalendar);
    }


    @Override
    public Event convertToEvent(TherapEventCSV eventCSV, CareCoordinationResident resident) {
        final EventAuthor eventAuthor = buildEventAuthor(eventCSV);
        if (eventAuthor == null) {
            throw new BusinessException("Couldn't create EventAuthor, missing any of any of FORMAUTHORFIRSTNAME, FORMAUTHORLASTNAME, FORMAUTHORROLE, FORMAUTHORORGANIZATION");
        }

        final Event event = new Event();
        event.setResident(resident);
        event.setEventAuthor(eventAuthor);

        event.setEventType(convertToEventType(eventCSV));
        event.setAssessment(eventCSV.getAssessmentNarrative());
        event.setBackground(eventCSV.getBackgroundNarrative());

        event.setOrganization(eventCSV.getOrganizationName());

        event.setCommunity(eventCSV.getCommunityName());

        event.setIsFollowup(StringUtils.isNotEmpty(eventCSV.getFollowUpDetails()) || eventCSV.getFollowUpExpected() == null);
        event.setFollowup(eventCSV.getFollowUpDetails());

        event.setIsInjury(BooleanUtils.toBoolean(eventCSV.getHasInjury()));

        event.setIsManual(false);
        event.setLocation(eventCSV.getEventLocation());
        event.setSituation(buildSituation(eventCSV));
        event.setIsErVisit(BooleanUtils.toBoolean(eventCSV.getErVisit()));
        event.setIsOvernightIn(BooleanUtils.toBoolean(eventCSV.getOvernightInPatient()));

        event.setEventDatetime(calculateEventDatetime(eventCSV));
        event.setEventManager(buildEventManager(eventCSV));

        event.setEventRn(buildEventRN(eventCSV));
        event.setEventTreatingPhysician(buildTreatingPhysician(eventCSV));
        event.setEventTreatingHospital(buildEventTreatingHospital(eventCSV));

        event.setPersonResponsible(eventCSV.getPersonResponsible());
        event.setEnteredBy(eventCSV.getEnteredBy());

        boolean deathIndicator = resolveDeathIndicator(eventCSV);
        resident.setDeathIndicator(deathIndicator);
        event.setDeathIndicator(deathIndicator);
        if (deathIndicator) {
            final Date deathDate = calculateDeathDateTime(eventCSV);
            resident.setDeathDate(deathDate);
            event.setDeathDate(deathDate);
        }
        return event;
    }

    private String buildSituation(TherapEventCSV eventCSV) {
        final StringBuilder result = new StringBuilder();
        final Map<Integer, String> otherTypeMap = eventSubTypeMapping.get(eventCSV.getOtherEventType());
        if (otherTypeMap != null) {
            String subType = otherTypeMap.get(eventCSV.getOtherEventSubType());
            if (StringUtils.isNotEmpty(subType)) {
                result.append("Event subtype: ").append(subType).append(".\n");
            }
        }
        if (StringUtils.isNotEmpty(eventCSV.getSituationNarrative())) {
            result.append(eventCSV.getSituationNarrative());
        }
        return result.toString();
    }

    private EventType convertToEventType(TherapEventCSV eventCSV) {
        if (shouldTakeEventOtherType(eventCSV)) {
            return otherEventTypeMapping.get(eventCSV.getOtherEventType());
        }
        if (eventCSV.getEventType() != null) {
            return eventTypeMapping.get(eventCSV.getEventType());
        }
        return null;
    }

    private boolean resolveDeathIndicator(TherapEventCSV eventCSV) {
        if (shouldTakeEventOtherType(eventCSV)) {
            return deathFlaggedEventOtherTypes.contains(eventCSV.getOtherEventType());
        }
        return deathFlaggedEventTypes.contains(eventCSV.getEventType());
    }

    private boolean shouldTakeEventOtherType(TherapEventCSV eventCSV) {
        return (eventCSV.getEventType() == null || eventCSV.getEventType().equals(EVENT_TYPE_OTHER)) && eventCSV.getOtherEventType() != null;
    }

    private Date calculateDeathDateTime(TherapEventCSV eventCSV) {
        if (eventCSV.getEventDeathDiscoveredDate() == null) {
            return null;
        }
        final Calendar calendar = prepareCalendarWithDefaultTime(eventCSV.getEventTime());
        copyCalendarYDM(eventCSV.getEventDeathDiscoveredDate(), calendar);
        return calendar.getTime();
    }

    private Date calculateEventDatetime(TherapEventCSV eventCSV) {
        final Calendar calendar = prepareCalendarWithDefaultTime(eventCSV.getEventTime());

        if (eventCSV.getEventDay() != null && eventCSV.getEventMonth() != null && eventCSV.getEventYear() != null) {
            calendar.set(
                    eventCSV.getEventYear(),
                    eventCSV.getEventMonth() - 1,
                    eventCSV.getEventDay());

        } else if (eventCSV.getEventDate() != null) {
            copyCalendarYDM(eventCSV.getEventDate(), calendar);
        } else {
            throw new BusinessException("No event date was provided");
        }

        return calendar.getTime();
    }

    private Calendar prepareCalendarWithDefaultTime(Calendar time) {
        final Calendar calendar = new GregorianCalendar();
        if (time == null) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        } else {
            calendar.setTime(time.getTime());
        }
        return calendar;
    }

    private void copyCalendarYDM(Calendar source, Calendar target) {
        target.set(source.get(Calendar.YEAR), source.get(Calendar.MONTH), source.get(Calendar.DAY_OF_MONTH));
    }

    private EventManager buildEventManager(TherapEventCSV eventCSV) {
        boolean namePresent = StringUtils.isNoneEmpty(eventCSV.getManagerFirstname(), eventCSV.getManagerLastname());
        if (!namePresent) {
            logger.info("Missing name for EventManager, not creating");
            return null;
        }

        final EventManager eventManager = new EventManager();
        eventManager.setFirstName(eventCSV.getManagerFirstname());
        eventManager.setLastName(eventCSV.getManagerLastname());
        eventManager.setPhone(eventCSV.getManagerPhone());
        eventManager.setEmail(eventCSV.getManagerEmail());

        return eventManager;
    }

    private EventAuthor buildEventAuthor(TherapEventCSV eventCSV) {
        if (StringUtils.isAnyEmpty(eventCSV.getFormAuthorFirstName(), eventCSV.getFormAuthorLastName(),
                eventCSV.getFormAuthorOrganization(), eventCSV.getFormAuthorRole())) {
            logger.info("Missing some field for EventAuthor, not creating");
            return null;
        }

        final EventAuthor eventAuthor = new EventAuthor();
        eventAuthor.setFirstName(eventCSV.getFormAuthorFirstName());
        eventAuthor.setLastName(eventCSV.getFormAuthorLastName());
        eventAuthor.setOrganization(eventCSV.getFormAuthorOrganization());
        eventAuthor.setRole(eventCSV.getFormAuthorRole());

        return eventAuthor;
    }

    private EventRN buildEventRN(TherapEventCSV eventCSV) {
        boolean namePresent = StringUtils.isNoneEmpty(eventCSV.getRnFirstname(), eventCSV.getRnLastname());
        if (!namePresent) {
            logger.info("Missing name for EventRN, not creating");
            return null;
        }

        final EventRN eventRN = new EventRN();
        eventRN.setFirstName(eventCSV.getRnFirstname());
        eventRN.setLastName(eventCSV.getRnLastname());

        boolean addressPresent = StringUtils.isNoneEmpty(eventCSV.getRnZip(), eventCSV.getRnState(), eventCSV.getRnCity(), eventCSV.getRnStreet());
        if (addressPresent) {
            final EventAddress rnAddress = new EventAddress();
            rnAddress.setZip(eventCSV.getRnZip());
            rnAddress.setState(stateService.findByAbbrOrFullName(eventCSV.getRnState()));
            rnAddress.setCity(eventCSV.getRnCity());
            rnAddress.setStreet(eventCSV.getRnStreet());

            eventRN.setEventAddress(rnAddress);
        }

        return eventRN;
    }

    private EventTreatingPhysician buildTreatingPhysician(TherapEventCSV eventCSV) {
        boolean namePresent = StringUtils.isNoneEmpty(eventCSV.getTreatingPhysicianFirstname(), eventCSV.getTreatingPhysicianLastname());
        if (!namePresent) {
            logger.info("Missing name for EventTreatingPhysician, not creating");
            return null;
        }
        final EventTreatingPhysician eventTreatingPhysician = new EventTreatingPhysician();

        eventTreatingPhysician.setFirstName(eventCSV.getTreatingPhysicianFirstname());
        eventTreatingPhysician.setLastName(eventCSV.getTreatingPhysicianLastname());

        boolean addressPresent = StringUtils.isNoneEmpty(eventCSV.getTreatingPhysicianZip(), eventCSV.getTreatingPhysicianCity(),
                eventCSV.getTreatingPhysicianState(), eventCSV.getTreatingPhysicianStreet());
        if (addressPresent) {
            final EventAddress eventAddress = new EventAddress();
            eventAddress.setZip(eventCSV.getTreatingPhysicianZip());
            eventAddress.setState(stateService.findByAbbrOrFullName(eventCSV.getTreatingPhysicianState()));
            eventAddress.setCity(eventCSV.getTreatingPhysicianCity());
            eventAddress.setStreet(eventCSV.getTreatingPhysicianStreet());
            eventTreatingPhysician.setEventAddress(eventAddress);
        }

        eventTreatingPhysician.setPhone(eventCSV.getTreatingPhysicianPhone());

        return eventTreatingPhysician;
    }


    private EventTreatingHospital buildEventTreatingHospital(TherapEventCSV eventCSV) {
        if (StringUtils.isEmpty(eventCSV.getTreatingHospitalName())) {
            logger.info("Missing name for EventTreatingHospital, not creating");
            return null;
        }

        final EventTreatingHospital eventTreatingHospital = new EventTreatingHospital();
        eventTreatingHospital.setName(eventCSV.getTreatingHospitalName());

        boolean addressPresent = StringUtils.isNoneEmpty(eventCSV.getTreatingHospitalZip(), eventCSV.getTreatingHospitalState(),
                eventCSV.getTreatingHospitalCity(), eventCSV.getTreatingHospitalStreet());
        if (addressPresent) {
            final EventAddress eventAddress = new EventAddress();
            eventAddress.setZip(eventCSV.getTreatingHospitalZip());
            eventAddress.setState(stateService.findByAbbrOrFullName(eventCSV.getTreatingHospitalState()));
            eventAddress.setCity(eventCSV.getTreatingHospitalCity());
            eventAddress.setStreet(eventCSV.getTreatingHospitalStreet());

            eventTreatingHospital.setEventAddress(eventAddress);
        }

        eventTreatingHospital.setPhone(eventCSV.getTreatingHospitalPhone());

        return eventTreatingHospital;
    }
}
