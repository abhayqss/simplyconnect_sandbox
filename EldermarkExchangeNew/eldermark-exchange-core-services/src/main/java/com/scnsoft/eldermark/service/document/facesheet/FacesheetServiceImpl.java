package com.scnsoft.eldermark.service.document.facesheet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.beans.ClientProblemFilter;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.Telecom;
import com.scnsoft.eldermark.entity.careteam.CareHistory;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;
import com.scnsoft.eldermark.entity.client.ClientNotes;
import com.scnsoft.eldermark.entity.client.ClientOrder;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.facesheet.*;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.GT1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.message.IN1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.shared.FaceSheetDto;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.MaskFormatter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FacesheetServiceImpl implements FacesheetService {

    private static final Logger logger = LoggerFactory.getLogger(FacesheetServiceImpl.class);

    private static final Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font HELVETICA_17_BOLD = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);
    private static final Font HELVETICA_8 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font HELVETICA_8_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private static final Font HELVETICA_9 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static final Font HELVETICA_9_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static final Font HELVETICA_9_BOLD_ITALIC = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC);
    private final static String PHONE_PATTERN = "^[0-9\\-\\+()\\s]+$";
    private static final String UNKNOWN_VALUE = "-";
    private static final String PHONE_MASK = "# (###) ###-####";
    private static final String PHONE_MASK_WITHOUT_COUNTRY_CODE = "1 (###) ###-####";

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Autowired
    private AllergyDao allergyDao;

    @Autowired
    private CareHistoryDao careHistoryDao;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentationOfDao documentationOfDao;

    @Autowired
    private EventJpaDao eventJpaDao;

    @Autowired
    private FacesheetService facesheetService;

    @Autowired
    private ClientMedProfessionalDao medicalProfessionalDao;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private GuardianDao guardianDao;

    @Autowired
    private ParticipantDao participantDao;

    @Autowired
    private PaySourceHistoryDao paySourceHistoryDao;

    @Autowired
    private PharmacyDao pharmacyDao;

    @Autowired
    private ClientCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private ClientProblemService clientProblemService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public FaceSheetDto construct(long clientId, boolean aggregated) {
        Client client = clientService.getById(clientId);

        FaceSheetDto facesheetDto = new FaceSheetDto();
        facesheetDto.setFaceSheetPrintedTime(Instant.now());

        Collection<Client> mergedClients;
        List<Long> mergedClientsIds = new ArrayList<>();
        if (aggregated) {
            mergedClientsIds = clientService.findAllMergedClientsIds(clientId);
            mergedClients = clientService.findAllMergedClients(client);
        } else {
            mergedClientsIds.add(clientId);
            mergedClients = Collections.singleton(client);
        }

        try {
            buildDemographics(facesheetDto, client, mergedClientsIds);
            buildContacts(facesheetDto, mergedClientsIds);
            buildMedicalContacts(facesheetDto, client, mergedClientsIds);
            buildBilling(facesheetDto, client, mergedClientsIds);
            buildMedicalInformation(facesheetDto, clientId, mergedClients, mergedClientsIds);
            buildNotesAlerts(facesheetDto, client, mergedClients, mergedClientsIds);
            buildAdvanceDirectives(facesheetDto, mergedClients, mergedClientsIds);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        return facesheetDto;
    }

    @Override
    @Transactional
    public DocumentReport generate(Long clientId, boolean aggregated, ZoneId zoneId) {
        FaceSheetDto faceSheet = facesheetService.construct(clientId, aggregated);

        Client client = clientService.getById(clientId);
        String documentName = String.format("Facesheet_%s_%s.pdf", client.getFirstName(), client.getLastName());

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024)) {
            writeToStream(faceSheet, buffer, zoneId);
            DocumentReport document = new DocumentReport();
            document.setDocumentTitle(documentName);
            document.setMimeType("application/pdf");
            document.setInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            return document;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public DocumentReport metadata() {
        DocumentReport document = new DocumentReport();
        document.setDocumentTitle("FACESHEET.PDF");
        document.setMimeType("application/pdf");
        document.setDocumentType(DocumentType.FACESHEET);
        return document;
    }

    private void buildDemographics(FaceSheetDto facesheetDto, Client client, List<Long> mergedClientsIds) {
        Community community = client.getCommunity();
        if (community != null) {
            facesheetDto.setCompanyName(community.getName());

            if (CollectionUtils.isNotEmpty(community.getAddresses())) {
                CommunityAddress address = community.getAddresses().get(0);
                if (address != null) {
                    facesheetDto.setCompanyAddress1(showEmptyIfBlank(address.getStreetAddress()));
                    facesheetDto.setCompanyAddress2(CareCoordinationUtils.concat(" ", address.getCity(), address.getState(), address.getPostalCode()));
                }
            }

            if (community.getTelecom() != null) {
                CommunityTelecom telecom = community.getTelecom();
                facesheetDto.setCompanyPhone(formatPhoneNumber(telecom.getValue()));
            } else if (StringUtils.isNotBlank(community.getPhone())) {
                facesheetDto.setCompanyPhone(formatPhoneNumber(community.getPhone()));
            }
        }

        facesheetDto.setResidentName(client.getFirstName() + " "
                + (StringUtils.isNotBlank(client.getMiddleName()) ? client.getMiddleName() + " " : "")
                + client.getLastName());

        facesheetDto.setPreferredName(client.getPreferredName());
        facesheetDto.setDob(client.getBirthDate());

        if (client.getBirthDate() != null) {
            LocalDate today = LocalDate.now();
            Period period = Period.between(client.getBirthDate(), today);
            if (period.getYears() < 1) {
                facesheetDto.setAge(period.getMonths() + " month(s)");
            } else {
                facesheetDto.setAge(String.valueOf(period.getYears()));
            }
        }

        facesheetDto.setGender(getDisplayName(client.getGender()));
        facesheetDto.setReligion(getDisplayName(client.getReligion()));
        facesheetDto.setMaritalStatus(getDisplayName(client.getMaritalStatus()));
        facesheetDto.setRace(getDisplayName(client.getRace()));

        if (CollectionUtils.isNotEmpty(client.getLanguages())) {
            Language language = client.getLanguages().get(0);
            facesheetDto.setPrimaryLanguage(getDisplayName(language.getCode()));
        }

        facesheetDto.setVeteran(client.getVeteran());
        facesheetDto.setAdmissionDate(client.getAdmitDate());
        facesheetDto.setUnit(client.getUnitNumber());

        var top1 = PageRequest.of(0, 1);
        List<CareHistory> careHistory = careHistoryDao.listByClientIds(mergedClientsIds, top1);
        if (CollectionUtils.isNotEmpty(careHistory)) {
            if (careHistory.get(0) != null) {
                facesheetDto.setStartOfCare(careHistory.get(0).getStartDate());
            }
        }

        List<AdmittanceHistory> admittanceHistories = admittanceHistoryDao.listByClientIds(mergedClientsIds);
        if (CollectionUtils.isNotEmpty(admittanceHistories)) {
            Instant latestAdmitDate = null;
            for (AdmittanceHistory admittanceHistory : admittanceHistories) {
                if (admittanceHistory.getAdmitDate() != null
                        && (admittanceHistory.getCommunityId() == null || (client.getCommunity() != null
                        && client.getCommunity().getId().equals(admittanceHistory.getOrganizationId())))
                        && (latestAdmitDate == null || latestAdmitDate.isBefore(admittanceHistory.getAdmitDate()))) {
                    latestAdmitDate = admittanceHistory.getAdmitDate();
                }
            }
            facesheetDto.setReadmissionDate(latestAdmitDate);
        }

        for (Telecom telecom : client.getPerson().getTelecoms()) {
            if (PersonTelecomCode.HP.name().equals(telecom.getUseCode())) {
                facesheetDto.setHomePhone(telecom.getValue());
            } else if (PersonTelecomCode.EMAIL.name().equals(telecom.getUseCode())
                    && StringUtils.isBlank(facesheetDto.getEmail())) {
                facesheetDto.setEmail(telecom.getValue());
            } else {
                facesheetDto.setOtherPhone(telecom.getValue());
            }
        }

        if (null != ObjectUtils.firstNonNull(client.getPrevAddrStreet(), client.getPrevAddrCity(),
                client.getPrevAddrState(), client.getPrevAddrZip())) {
            String prevAddr = String.format("%s %s %s %s", client.getPrevAddrStreet(), client.getPrevAddrCity(),
                    client.getPrevAddrState(), client.getPrevAddrZip());
            facesheetDto.setPreviousAddress(prevAddr);
        }


        List<String> countyAdmittedFroms = new ArrayList<>();
        List<String> livingStatuses = new ArrayList<>();
        for (AdmittanceHistory admittanceHistory : admittanceHistories) {
            if (admittanceHistory.getCountyAdmittedFrom() != null)
                countyAdmittedFroms.add(admittanceHistory.getCountyAdmittedFrom());
            if (admittanceHistory.getLivingStatus() != null)
                livingStatuses.add(admittanceHistory.getLivingStatus().getDescription());
        }
        facesheetDto.setAdmittedFrom(StringUtils.join(livingStatuses, ", "));
        facesheetDto.setCountyAdmittedFrom(StringUtils.join(countyAdmittedFroms, ", "));
    }

    private String formatPhoneNumber(String number) {
        if (StringUtils.isEmpty(number) || Pattern.compile("\\D").matcher(number).find()) {
            return number;
        }
        if (number.length() == 10) {
            return applyPhoneMask(number, PHONE_MASK_WITHOUT_COUNTRY_CODE);
        }
        if (number.length() == 11 && number.startsWith("1")) {
            return applyPhoneMask(number, PHONE_MASK);
        }
        return number;
    }

    private String applyPhoneMask(String data, String mask) {
        try {
            var maskFormatter = new MaskFormatter(mask);
            maskFormatter.setValueContainsLiteralCharacters(false);
            return maskFormatter.valueToString(data);
        } catch (ParseException e) {
            logger.warn("Couldn't apply phone mask {} for {}: {}", mask, data, ExceptionUtils.getStackTrace(e));
        }
        return data;
    }

    private Instant getNullableDate(Date date) {
        return Optional.ofNullable(date).map(Date::toInstant).orElse(null);
    }

    private void buildContacts(FaceSheetDto facesheetDto, Collection<Long> mergedClientsIds) {
        Set<FaceSheetDto.Contact> contacts = new TreeSet<>();
        Pattern pattern = Pattern.compile(PHONE_PATTERN);

        List<ContactWithRelationship> contactsWithRelationship = new ArrayList<>(guardianDao.listByResidentIds(mergedClientsIds));
        contactsWithRelationship.addAll(participantDao.listCcdParticipants(mergedClientsIds));
        for (ContactWithRelationship contactWithRelationship : contactsWithRelationship) {
            Person participantPerson = contactWithRelationship.getPerson();
            if (participantPerson != null) {
                FaceSheetDto.Contact contact = facesheetDto.new Contact();
                contact.setRelationship(getDisplayName(contactWithRelationship.getRelationship()));
                setFullName(contact, participantPerson, true);
                setContactAddress(contact, participantPerson);
                setContactTelecomWithEmail(contact, participantPerson, pattern);
                contacts.add(contact);
            }
        }

        List<ClientCareTeamMember> residentCareTeamMembers = residentCareTeamMemberDao.findByClient_IdInAndIncludeInFaceSheetIsTrue(mergedClientsIds);
        for (ClientCareTeamMember residentCareTeamMember : residentCareTeamMembers) {
            if (residentCareTeamMember.getIncludeInFaceSheet()) {
                FaceSheetDto.Contact contact = facesheetDto.new Contact();
                Employee employee = residentCareTeamMember.getEmployee();
                if (employee != null) {
                    if (StringUtils.isNotBlank(employee.getFullName())) {
                        contact.setName(employee.getFullName());
                    }
                    if (residentCareTeamMember.getCareTeamRelationship() != null) {
                        contact.setRelationship(residentCareTeamMember.getCareTeamRelationship().getName());
                    }
                    Person participantPerson = employee.getPerson();
                    if (participantPerson != null) {
                        setContactAddress(contact, participantPerson);
                        setContactTelecomWithEmail(contact, participantPerson, pattern);
                    }
                    contacts.add(contact);
                }
            }
        }

        facesheetDto.setContactList(new ArrayList<>(contacts));
    }

    private void buildMedicalContacts(FaceSheetDto facesheetDto, Client client, List<Long> mergedClientsIds) {
        Set<FaceSheetDto.MedicalProfessional> medProfessionalDtos = new HashSet<>();
        List<ContactWithRole> contacts = new ArrayList<>();
        contacts.addAll(medicalProfessionalDao.listByClientIds(mergedClientsIds));
        contacts.addAll(documentationOfDao.findByClient_IdIn(mergedClientsIds));
        for (ContactWithRole contact : contacts) {
            if (CollectionUtils.isNotEmpty(contact.getPersons())) {
                for (Person person : contact.getPersons()) {
                    FaceSheetDto.MedicalProfessional medProfessionalDto = facesheetDto.new MedicalProfessional();
                    if (Strings.isNotBlank(contact.getRole())) {
                        medProfessionalDto.setRole(contact.getRole());
                    } else if (contact instanceof DocumentationOf) {
                        medProfessionalDto.setRole("Clinician");
                    }
                    StringBuilder info = new StringBuilder();
                    if (StringUtils.isNotBlank(contact.getCommuntiyName())) {
                        appendDataWithComma(info, contact.getCommuntiyName());
                    }
                    if (person != null) {
                        StringBuilder personInfo = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(person.getNames())) {
                            appendDataWithComma(personInfo, person.getNames().get(0).getGiven() + " " +
                                    (StringUtils.isNotBlank(person.getNames().get(0).getMiddle()) ? person.getNames().get(0).getMiddle() + " " : "") +
                                    person.getNames().get(0).getFamily());
                        }
                        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
                            Address address = person.getAddresses().get(0);
                            if (address != null) {
                                appendDataWithComma(personInfo, address.getStreetAddress() + " " + address.getCity() + " " +
                                        address.getState() + " " + address.getPostalCode());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(person.getTelecoms())) {
                            appendDataWithComma(personInfo, convertTelecoms(person.getTelecoms()));
                        }
                        medProfessionalDto.setPersonRelatedData(personInfo.toString());
                        info.append(personInfo);
                    }
                    if (StringUtils.isNotBlank(contact.getNpi())) {
                        appendDataWithComma(info, "NPI: " + contact.getNpi());
                    }
                    medProfessionalDto.setData(info.toString());
                    medProfessionalDtos.add(medProfessionalDto);
                }
            }
        }
        facesheetDto.setMedicalProfessional(new ArrayList<>(medProfessionalDtos));

        List<Community> pharmacies = pharmacyDao.listPharmaciesAsCommunity(mergedClientsIds);
        Set<String> pharmacyNames = new HashSet<>();
        for (Community pharmacy : pharmacies) {
            StringBuilder info = new StringBuilder();
            if (StringUtils.isNotBlank(pharmacy.getName())) {
                appendDataWithComma(info, pharmacy.getName());
            }
            if (pharmacy.getTelecom() != null && StringUtils.isNotBlank(pharmacy.getTelecom().getValue())) {
                appendDataWithComma(info, pharmacy.getTelecom().getValue());
            }
            pharmacyNames.add(info.toString());
        }
        if (StringUtils.isNotBlank(client.getCurrentPharmacyName())) {
            pharmacyNames.add(client.getCurrentPharmacyName());
        }
        facesheetDto.setPharmacy(StringUtils.join(pharmacyNames, "; "));

        facesheetDto.setHospitalPref(client.getHospitalPreference());
        facesheetDto.setTransportation(client.getTransportationPreference());
        facesheetDto.setAmbulance(client.getAmbulancePreference());
    }

    private void appendDataWithComma(StringBuilder builder, String str) {
        showEmptyIfBlank(str);
        var data = str.trim();
        if (Strings.isEmpty(data)) {
            return;
        }
        if (Strings.isNotEmpty(builder)) {
            builder.append(", ");
        }
        builder.append(data);
    }

    private void buildBilling(FaceSheetDto facesheetDto, Client client, List<Long> mergedClientsIds) {
        Set<FaceSheetDto.Contact> responsibleParties = new TreeSet<>();

        Collection<Participant> participants = participantDao.listResponsibleParties(mergedClientsIds);
        for (Participant participant : participants) {
            Person participantPerson = participant.getPerson();
            if (participantPerson != null) {
                FaceSheetDto.Contact contact = facesheetDto.new Contact();
                contact.setRelationship(getDisplayName(participant.getRelationship()));
                setFullName(contact, participantPerson, false);
                setContactAddress(contact, participantPerson);
                setContactTelecom(contact, participantPerson);
                responsibleParties.add(contact);
            }
        }


        List<Long> adtMsgIds = eventJpaDao.getAdtMessageIdsForClients(mergedClientsIds);
        List<AdtMessage> adtMessages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(adtMsgIds)) {
            adtMessages = adtMessageDao.findAllById(adtMsgIds);
        }
        for (AdtMessage adtMessage : adtMessages) {
            if (adtMessage instanceof GT1ListSegmentContainingMessage) {
                GT1ListSegmentContainingMessage guarantorMessage = (GT1ListSegmentContainingMessage) adtMessage;
                if (CollectionUtils.isNotEmpty(guarantorMessage.getGt1List())) {
                    for (AdtGT1GuarantorSegment guarantorSegment : guarantorMessage.getGt1List()) {
                        FaceSheetDto.Contact contact = facesheetDto.new Contact();
                        if (CollectionUtils.isNotEmpty(guarantorSegment.getGuarantorNameList())) {
                            List<String> names = new ArrayList<>();
                            for (XPNPersonName personName : guarantorSegment.getGuarantorNameList()) {
                                names.add(personName.getFirstName() + " " + personName.getLastName());
                            }
                            contact.setName(StringUtils.join(names, ","));
                        }
                        if (CollectionUtils.isNotEmpty(guarantorSegment.getGuarantorAddressList())) {
                            XADPatientAddress personAddress = guarantorSegment.getGuarantorAddressList().get(0);
                            if (StringUtils.isNotBlank(personAddress.getStreetAddress())) {
                                contact.setAddress1(showEmptyIfBlank(personAddress.getStreetAddress()));
                                contact.setAddress2(showEmptyIfBlank(personAddress.getCity()) + " "
                                        + showEmptyIfBlank(personAddress.getState()) + " "
                                        + showEmptyIfBlank(personAddress.getZip()));
                            }
                        }

                        if (CollectionUtils.isNotEmpty(guarantorSegment.getGuarantorPhNumHomeList())) {
                            List<String> telecoms = new ArrayList<>();
                            for (XTNPhoneNumber personTelecom : guarantorSegment.getGuarantorPhNumHomeList()) {
                                StringBuilder telecomValue = new StringBuilder();
                                telecomValue.append("Phone: ");
                                if (StringUtils.isNotBlank(personTelecom.getAreaCode())) {
                                    telecomValue.append(personTelecom.getAreaCode());
                                }
                                if (StringUtils.isNotBlank(personTelecom.getPhoneNumber())) {
                                    telecomValue.append(personTelecom.getPhoneNumber());
                                } else if (StringUtils.isNotBlank(personTelecom.getTelephoneNumber())) {
                                    telecomValue.append(personTelecom.getTelephoneNumber());
                                }
                                String telecom = telecomValue.toString();
                                if (StringUtils.isNotBlank(telecom))
                                    telecoms.add(telecom);
                            }

                            contact.setPhone(StringUtils.join(telecoms, ", "));
                        }
                        responsibleParties.add(contact);
                    }
                }
            }
        }

        facesheetDto.setResponsibleParty(new ArrayList<>(responsibleParties));

        facesheetDto.setPrimaryPayType(paySourceHistoryDao.listByClientIds(mergedClientsIds).stream()
                .map(PaySourceHistory::getPaySource)
                .collect(Collectors.joining(", ")));

        facesheetDto.setSsn(ClientUtils.formatSsn(client.getSsnLastFourDigits()));
        facesheetDto.setMedicareNumber(client.getMedicareNumber());
        facesheetDto.setMedicaidNumber(client.getMedicaidNumber());

        Set<String> healthPlans = new HashSet<>();
        Set<String> networksAndPlansDuplicatesCheck = new HashSet<>();
        if (client.getInNetworkInsurance() != null) {
            String network = client.getInNetworkInsurance().getDisplayName();
            String plan = client.getInsurancePlan();
            String networkWithPlan = buildHealthPlanUniquenessString(network, plan);
            if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                String policy = client.getMemberNumber();
                String group = client.getGroupNumber();
                healthPlans.add(buildHealthPlanString(network, plan, policy, group));
            }
        }
        if (client.getHealthPlans() != null) {
            for (ClientHealthPlan healthPlan : client.getHealthPlans()) {
                String plan = healthPlan.getHealthPlanName();
                String networkWithPlan = buildHealthPlanUniquenessString(null, plan);
                if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                    String policy = healthPlan.getPolicyNumber();
                    String group = healthPlan.getGroupNumber();
                    healthPlans.add(buildHealthPlanString(null, plan, policy, group));
                }
            }
        }
        for (AdtMessage adtMessage : adtMessages) {
            if (adtMessage instanceof IN1ListSegmentContainingMessage) {
                IN1ListSegmentContainingMessage insuranceMessage = (IN1ListSegmentContainingMessage) adtMessage;
                if (CollectionUtils.isNotEmpty(insuranceMessage.getIn1List())) {
                    for (IN1InsuranceSegment insuranceSegment : insuranceMessage.getIn1List()) {
                        String network = Optional.of(insuranceSegment.getInsuranceCompanyName())
                                .map(XONExtendedCompositeNameAndIdForOrganizations::getOrganizationName)
                                .orElse(null);
                        String plan = Optional.of(insuranceSegment.getInsurancePlanId())
                                .map(CECodedElement::getText)
                                .orElse(null);
                        String networkWithPlan = buildHealthPlanUniquenessString(network, plan);
                        if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                            String group = insuranceSegment.getGroupNumber();
                            healthPlans.add(buildHealthPlanString(network, plan, null, group));
                        }
                    }
                }
            }
        }

        facesheetDto.setHealthPlanNumber(StringUtils.join(healthPlans, "; "));

        if (client.getDentalInsurance() != null) {
            Set<String> dentalPlans = new HashSet<>();
            String[] rows = client.getDentalInsurance().split("\\r+");
            for (String healthPlan : rows) {
                String[] columns = healthPlan.split("\\t");
                if (columns.length == 3) {
                    dentalPlans.add(String.format("%s, policy#: %s, group#: %s", columns[0], columns[1], columns[2]));
                }
            }
            if (CollectionUtils.isNotEmpty(dentalPlans)) {
                facesheetDto.setDentalPlanNumber(StringUtils.join(dentalPlans, "; "));
            } else {
                facesheetDto.setDentalPlanNumber(client.getDentalInsurance());
            }
        }
    }

    private void buildMedicalInformation(FaceSheetDto facesheetDto, Long clientId, Collection<Client> mergedClients, List<Long> mergedClientsIds) throws ParseException {
        Collection<Allergy> allergies = allergyDao.listByClientIds(mergedClientsIds);
        Map<String, FaceSheetDto.Allergy> activeAllergies = new HashMap<>();
        for (Allergy allergy : allergies) {
            Collection<AllergyObservation> allergyObservationList = allergy.getAllergyObservations();
            for (AllergyObservation allergyObservation : allergyObservationList) {
                // adds allergy if it is not contained already
                if (StringUtils.isNotBlank(allergyObservation.getProductText())
                        && !activeAllergies.containsKey(allergyObservation.getProductText())) {
                    FaceSheetDto.Allergy facesheetAllergy = facesheetDto.new Allergy();
                    facesheetAllergy.setSubstance(allergyObservation.getProductText());
                    facesheetAllergy.setType(allergyObservation.getAdverseEventTypeText());
                    facesheetAllergy.setReaction(allergyObservation.getReactionObservations().stream()
                            .map(ReactionObservation::getReactionText)
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(", ")));
                    facesheetAllergy.setStartDate(getNullableDate(allergyObservation.getTimeLow()));
                    if (allergyObservation.getOrganization() != null) {
                        facesheetAllergy.setDataSource(allergyObservation.getOrganization().getName());
                    }
                    activeAllergies.put(allergyObservation.getProductText(), facesheetAllergy);
                }
            }
        }
        facesheetDto.setAllergies(activeAllergies.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));


        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var problemFilter = new ClientProblemFilter();
        problemFilter.setClientId(clientId);
        problemFilter.setIncludeActive(true);
        problemFilter.setIncludeOther(true);
        var problems = clientProblemService.find(problemFilter, permissionFilter);
        List<FaceSheetDto.Diagnosis> activeDiagnosis = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(problems)) {
            for (ClientProblem clientProblem : problems) {
                var problemObservation = clientProblem.getProblemObservation();
                FaceSheetDto.Diagnosis facesheetDiagnosis = facesheetDto.new Diagnosis();
                facesheetDiagnosis.setDiagnosis(problemObservation.getProblemName());
                facesheetDiagnosis.setCode(problemObservation.getProblemIcdCode());
                facesheetDiagnosis.setCodeSet(problemObservation.getProblemIcdCodeSet());
                if (problemObservation.getProblemType() != null) {
                    facesheetDiagnosis.setType(problemObservation.getProblemType().getDisplayName());
                }
                facesheetDiagnosis.setIdentified(getNullableDate(problemObservation.getProblemDateTimeLow()));
                if (problemObservation.getOrganization() != null || problemObservation.getManual()) {
                    if (problemObservation.getManual()) {
                        facesheetDiagnosis.setDataSource("Simply Connect HIE");
                    } else {
                        facesheetDiagnosis.setDataSource(problemObservation.getOrganization().getName());
                    }
                }
                activeDiagnosis.add(facesheetDiagnosis);
            }
        }

        facesheetDto.setDiagnosis(activeDiagnosis.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));

        Map<String, FaceSheetDto.Order> activeOrders = new HashMap<>();
        for (Client mergedClient : mergedClients) {
            if (CollectionUtils.isNotEmpty(mergedClient.getOrders())) {
                for (ClientOrder order : mergedClient.getOrders()) {
                    if (order.isActive() && !activeOrders.containsKey(order.getName())) {
                        FaceSheetDto.Order facesheetOrder = facesheetDto.new Order();
                        facesheetOrder.setName(order.getName());
                        facesheetOrder.setStartDate(order.getStartDate());
                        activeOrders.put(order.getName(), facesheetOrder);
                    }
                }
            }
        }
        facesheetDto.setOrders(activeOrders.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
    }

    private void buildNotesAlerts(FaceSheetDto facesheetDto, Client client, Collection<Client> mergedClients, List<Long> mergedClientsIds) throws ParseException {
        facesheetDto.setEvacuationStatus(client.getEvacuationStatus());

        List<FaceSheetDto.Note> activeNotes = new ArrayList<>();
        for (Client mergedClient : mergedClients) {
            if (CollectionUtils.isNotEmpty(mergedClient.getAlertNotes())) {
                for (ClientNotes note : mergedClient.getAlertNotes()) {
                    if (note.isActive()) {
                        FaceSheetDto.Note activeNote = facesheetDto.new Note();
                        activeNote.setNote(note.getNote());
                        activeNote.setDate(note.getStartDate());
                        activeNotes.add(activeNote);
                    }
                }
            }
        }

        activeNotes.sort(Collections.reverseOrder());
        facesheetDto.setNotes(activeNotes);
    }

    private void buildAdvanceDirectives(FaceSheetDto facesheetDto, Collection<Client> mergedClients, List<Long> mergedClientsIds) {
        Collection<AdvanceDirective> advanceDirectiveList = advanceDirectiveDao.findByClient_IdIn(mergedClientsIds);
        Set<FaceSheetDto.AdvanceDirective> advanceDirectives = new HashSet<>();
        for (AdvanceDirective advanceDirective : advanceDirectiveList) {
            if (StringUtils.isNotBlank(getDisplayName(advanceDirective.getType()))) {
                FaceSheetDto.AdvanceDirective facesheetAdvanceDirective = facesheetDto.new AdvanceDirective();
                facesheetAdvanceDirective.setType(getDisplayName(advanceDirective.getType()));
                facesheetAdvanceDirective.setCode(advanceDirective.getType().getCode());
                facesheetAdvanceDirective.setCodeSet(advanceDirective.getType().getCodeSystem());
                if (CollectionUtils.isNotEmpty(advanceDirective.getVerifiers())) {
                    Set<String> verifiers = new HashSet<>();
                    for (Participant verifier : advanceDirective.getVerifiers()) {
                        if (verifier.getPerson() != null) {
                            StringBuilder facesheetVerifier = new StringBuilder();
                            if (CollectionUtils.isNotEmpty(verifier.getPerson().getNames())) {
                                Name name = verifier.getPerson().getNames().get(0);
                                if (StringUtils.isNotBlank(name.getPrefix())) {
                                    facesheetVerifier.append(name.getPrefix())
                                            .append(" ");
                                }
                                facesheetVerifier.append(name.getGiven())
                                        .append(" ")
                                        .append(name.getFamily());
                            }
                            verifiers.add(facesheetVerifier.toString());
                        }
                    }
                    facesheetAdvanceDirective.setVerification(StringUtils.join(verifiers, ", "));
                }

                facesheetAdvanceDirective.setDateStarted(getNullableDate(advanceDirective.getTimeLow()));
                if (advanceDirective.getOrganization() != null) {
                    facesheetAdvanceDirective.setDataSource(advanceDirective.getOrganization().getName());
                }
                advanceDirectives.add(facesheetAdvanceDirective);
            }
        }

        for (Client client : mergedClients) {
            if (StringUtils.isNotBlank(client.getAdvanceDirectiveFreeText())) {
                FaceSheetDto.AdvanceDirective facesheetAdvanceDirective = facesheetDto.new AdvanceDirective();
                facesheetAdvanceDirective.setType(client.getAdvanceDirectiveFreeText());
                if (client.getOrganization() != null) {
                    facesheetAdvanceDirective.setDataSource(client.getOrganization().getName());
                }
                advanceDirectives.add(facesheetAdvanceDirective);
            }
        }

        facesheetDto.setAdvanceDirectives(advanceDirectives.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList()));
    }

    private String buildContactInfoForBilling(FaceSheetDto.Contact contact) {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        if (StringUtils.isNotBlank(contact.getName())) {
            result.append(contact.getName());
            isFirst = false;
        }
        if (StringUtils.isNotBlank(contact.getAddress1())) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(", ");
            }
            result.append(contact.getAddress1());
        }
        if (StringUtils.isNotBlank(contact.getAddress2())) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(" ");
            }
            result.append(contact.getAddress2());
        }
        if (StringUtils.isNotBlank(contact.getPhone())) {
            if (!isFirst) {
                result.append(", ");
            }
            result.append(contact.getPhone());
        }
        return result.toString();
    }


    private String buildHealthPlanString(String network, String plan, String policy, String group) {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        if (StringUtils.isNotBlank(network)) {
            result.append("Network: ");
            result.append(network);
            isFirst = false;
        }
        if (StringUtils.isNotBlank(plan)) {
            if (isFirst) {
                result.append("Plan: ");
                isFirst = false;
            } else {
                result.append(", plan: ");
            }
            result.append(plan);
        }
        if (StringUtils.isNotBlank(policy)) {
            if (isFirst) {
                result.append("Policy#: ");
                isFirst = false;
            } else {
                result.append(", policy#: ");
            }
            result.append(policy);
        }
        if (StringUtils.isNotBlank(group)) {
            if (isFirst) {
                result.append("Group#: ");
            } else {
                result.append(", group#: ");
            }
            result.append(group);
        }
        return result.toString();
    }

    private String buildHealthPlanUniquenessString(String network, String plan) {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isNotBlank(network)) {
            result.append("1.network:")
                    .append(network);
        }
        if (StringUtils.isNotBlank(plan)) {
            result.append("2.plan:")
                    .append(plan);
        }
        return result.toString();
    }

    private PdfPCell createCellWithNoteValue(FaceSheetDto.Note note) {
        PdfPCell cell = new PdfPCell();
        Phrase p = new Phrase();
        boolean firstLine = true;
        if (StringUtils.isNotBlank(note.getSubjective())) {
            p.add(new Phrase("Subjective: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getSubjective(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
            firstLine = false;
        }
        if (StringUtils.isNotBlank(note.getObjective())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            } else {
                firstLine = false;
            }
            p.add(new Phrase("Objective: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getObjective(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        if (StringUtils.isNotBlank(note.getAssessment())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            } else {
                firstLine = false;
            }
            p.add(new Phrase("Assessment: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getAssessment(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        if (StringUtils.isNotBlank(note.getPlan())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            }
            p.add(new Phrase("Plan: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getPlan(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        cell.setPhrase(p);
        return cell;
    }

    private PdfPCell createCellWithTextField(String label, String value) {
        return createCellWithTextField(label, value, HELVETICA_9_BOLD_ITALIC);
    }

    private PdfPCell createCellWithTextField(String label, String value, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setMinimumHeight(15);

        Phrase p = new Phrase();
        p.add(new Phrase(label, labelFont));
        p.add(new Phrase("  ", HELVETICA_9));
        p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_9));
        cell.setPhrase(p);

        return cell;
    }

    private PdfPCell createCellWithTextField(String label, String value, int columnSpan) {
        PdfPCell cell = createCellWithTextField(label, value);
        cell.setColspan(columnSpan);
        return cell;
    }

    private void createDocumentBody(Document document, FaceSheetDto faceSheetDto, ZoneId zoneId) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);

        String dateOfBirth = null;
        if (faceSheetDto.getDob() != null) {
            dateOfBirth = DateTimeUtils.formatLocalDate(faceSheetDto.getDob(), zoneId);
        }
        table.addCell(createCellWithTextField("DOB:", dateOfBirth));

        table.addCell(createCellWithTextField("Age:", faceSheetDto.getAge()));
        table.addCell(createCellWithTextField("Gender:", faceSheetDto.getGender()));
        table.addCell(createCellWithTextField("Religion:", faceSheetDto.getReligion()));
        table.addCell(createCellWithTextField("Marital Status:", faceSheetDto.getMaritalStatus()));
        table.addCell(createCellWithTextField("Race:", faceSheetDto.getRace()));
        table.addCell(createEmptyRow(3));
        table.addCell(createCellWithTextField("Primary Language:", faceSheetDto.getPrimaryLanguage()));
        table.addCell(createCellWithTextField("Veteran:", faceSheetDto.getVeteran(), 2));
        table.addCell(createCellWithTextField("Admission Date:", displayDate(faceSheetDto.getAdmissionDate(), zoneId)));
        table.addCell(createCellWithTextField("Unit:", faceSheetDto.getUnit()));
        table.addCell(createCellWithTextField("Start Of Care:", displayDate(faceSheetDto.getStartOfCare(), zoneId)));
        table.addCell(createCellWithTextField("Date Of Current Readmission:",
                displayDate(faceSheetDto.getReadmissionDate(), zoneId), 3));
        table.addCell(createEmptyRow(3));
        table.addCell(createCellWithTextField("Home Phone:", faceSheetDto.getHomePhone()));
        table.addCell(createCellWithTextField("Other Phone:", faceSheetDto.getOtherPhone()));
        table.addCell(createCellWithTextField("Email:", faceSheetDto.getEmail()));
        table.addCell(createCellWithTextField("Previous Address:", faceSheetDto.getPreviousAddress(), 3));
        table.addCell(createCellWithTextField("Admitted From:", faceSheetDto.getAdmittedFrom(), 3));
        table.addCell(createCellWithTextField("County Admitted From:", faceSheetDto.getCountyAdmittedFrom(), 3));
        document.add(table);

        Paragraph paragraph = new Paragraph("CONTACTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Name", HELVETICA_9));
        table.addCell(new Phrase("Relationship", HELVETICA_9));
        table.addCell(new Phrase("Address", HELVETICA_9));
        table.addCell(new Phrase("Telecom", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getContactList())) {
            for (FaceSheetDto.Contact contact : faceSheetDto.getContactList()) {
                table.addCell(new Phrase(showUnknownIfBlank(contact.getName()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(contact.getRelationship()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(Objects.toString(contact.getAddress1(), "") + " "
                        + Objects.toString(contact.getAddress2(), "")), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(contact.getPhone()), HELVETICA_9));
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("MEDICAL CONTACTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        if (CollectionUtils.isNotEmpty(faceSheetDto.getMedicalProfessional())) {
            for (FaceSheetDto.MedicalProfessional medicalProfessional : faceSheetDto.getMedicalProfessional()) {
                table.addCell(
                        createCellWithTextField(medicalProfessional.getRole() + ":", medicalProfessional.getData()));
            }
        } else {
            table.addCell(createCellWithTextField("Medical Professional:", UNKNOWN_VALUE));
        }
        table.addCell(createCellWithTextField("Pharmacy:", faceSheetDto.getPharmacy()));
        table.addCell(createCellWithTextField("Hospital Pref:", faceSheetDto.getHospitalPref()));
        table.addCell(createCellWithTextField("Transportation:", faceSheetDto.getTransportation()));
        table.addCell(createCellWithTextField("Ambulance:", faceSheetDto.getAmbulance()));
        document.add(table);

        paragraph = new Paragraph("BILLING", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        if (CollectionUtils.isNotEmpty(faceSheetDto.getResponsibleParty())) {
            for (FaceSheetDto.Contact contact : faceSheetDto.getResponsibleParty()) {
                table.addCell(createCellWithTextField("Responsible Party:", buildContactInfoForBilling(contact)));
            }
        }
        table.addCell(createCellWithTextField("Primary Pay Type:", faceSheetDto.getPrimaryPayType()));
        table.addCell(createCellWithTextField("SSN:", faceSheetDto.getSsn()));
        table.addCell(createCellWithTextField("Medicare #:", faceSheetDto.getMedicareNumber()));
        table.addCell(createCellWithTextField("Medicaid #:", faceSheetDto.getMedicaidNumber()));

        table.addCell(createCellWithTextField("Health Plans:", faceSheetDto.getHealthPlanNumber()));
        table.addCell(createCellWithTextField("Dental Plan:", faceSheetDto.getDentalPlanNumber()));
        document.add(table);

        paragraph = new Paragraph("MEDICAL INFORMATION", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        paragraph = new Paragraph("ALLERGIES", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Substance", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Reaction(s)", HELVETICA_9));
        table.addCell(new Phrase("Start date", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getAllergies())) {
            for (FaceSheetDto.Allergy allergy : faceSheetDto.getAllergies()) {
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getSubstance()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getReaction()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(allergy.getStartDate(), zoneId)), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getDataSource()), HELVETICA_9));
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("DIAGNOSIS", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Diagnosis", HELVETICA_9));
        table.addCell(new Phrase("Code", HELVETICA_9));
        table.addCell(new Phrase("Code Set", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Identified", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getDiagnosis())) {
            for (FaceSheetDto.Diagnosis diagnosis : faceSheetDto.getDiagnosis()) {
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getDiagnosis()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getCode()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getCodeSet()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(diagnosis.getIdentified(), zoneId)), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getDataSource()), HELVETICA_9));
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("ORDERS", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Name", HELVETICA_9));
        table.addCell(new Phrase("Start date", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getOrders())) {
            for (FaceSheetDto.Order order : faceSheetDto.getOrders()) {
                table.addCell(new Phrase(showUnknownIfBlank(order.getName()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(order.getStartDate(), zoneId)), HELVETICA_9));
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("NOTES / ALERTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        table.addCell(createCellWithTextField("Evacuation Status:", faceSheetDto.getEvacuationStatus()));
        document.add(table);

        table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Date", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Note", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getNotes())) {
            for (FaceSheetDto.Note note : faceSheetDto.getNotes()) {
                table.addCell(new Phrase(showUnknownIfBlank(displayDateTime(note.getDate(), zoneId)), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(note.getType()), HELVETICA_9));
                if (StringUtils.isNotBlank(note.getNote())) {
                    table.addCell(new Phrase(showUnknownIfBlank(note.getNote()), HELVETICA_9));
                }
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("ADVANCE DIRECTIVES", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Code", HELVETICA_9));
        table.addCell(new Phrase("Code Set", HELVETICA_9));
        table.addCell(new Phrase("Verification", HELVETICA_9));
        table.addCell(new Phrase("Date started", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getAdvanceDirectives())) {
            for (FaceSheetDto.AdvanceDirective advanceDirective : faceSheetDto.getAdvanceDirectives()) {
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getCode()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getCodeSet()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getVerification()), HELVETICA_9));
                table.addCell(
                        new Phrase(showUnknownIfBlank(displayDate(advanceDirective.getDateStarted(), zoneId)), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getDataSource()), HELVETICA_9));
            }
        } else {
            for (int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);
    }

    private void createDocumentFooter(Document document, PdfContentByte pdfContentByte) {
        Rectangle page = document.getPageSize();

        PdfPTable footer = new PdfPTable(1);
        PdfPCell nextPageCell = new PdfPCell(new Phrase("CONTINUED ON NEXT PAGE", HELVETICA_8_ITALIC));
        nextPageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nextPageCell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(nextPageCell);

        footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), pdfContentByte);
    }

    private void createDocumentHeader(Document document, FaceSheetDto faceSheetDto, int pageNumber, int totalNumber,
                                      PdfContentByte pdfContentByte, ZoneId zoneId) {
        Rectangle page = document.getPageSize();

        PdfPTable header = new PdfPTable(6);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);

        PdfPCell companyInfo = new PdfPCell();
        Phrase content = new Phrase();
        content.add(new Phrase(12, showUnknownIfBlank(faceSheetDto.getCompanyName()) + "\n", HELVETICA_8));
        if (StringUtils.isNotBlank(faceSheetDto.getCompanyAddress1())
                || StringUtils.isNotBlank(faceSheetDto.getCompanyAddress2())) {
            content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyAddress1()) + "\n", HELVETICA_8));
            content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyAddress2()) + "\n", HELVETICA_8));
        }
        content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyPhone()) + "\n", HELVETICA_8));
        content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyFax()) + "\n", HELVETICA_8));
        companyInfo.setPhrase(content);
        companyInfo.setHorizontalAlignment(Element.ALIGN_LEFT);
        companyInfo.setVerticalAlignment(Element.ALIGN_BOTTOM);
        companyInfo.setBorder(Rectangle.BOTTOM);
        companyInfo.setColspan(3);

        String printedTime = "";
        if (faceSheetDto.getFaceSheetPrintedTime() != null) {
            printedTime = DateTimeUtils.formatDateTime(faceSheetDto.getFaceSheetPrintedTime(), zoneId);
        }

        PdfPCell docInfo = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Face Sheet\n", HELVETICA_17_BOLD));
        content.add(new Phrase(String.format("\nPage %d of %d\n", pageNumber, totalNumber), HELVETICA_9));
        content.add(new Phrase(String.format("Printed: %s", showUnknownIfBlank(printedTime)), HELVETICA_9));
        docInfo.setPhrase(content);
        docInfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        docInfo.setVerticalAlignment(Element.ALIGN_BOTTOM);
        docInfo.setBorder(Rectangle.BOTTOM);
        docInfo.setColspan(3);

        PdfPCell residentNameCell = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Client Name:  ", HELVETICA_9_BOLD));
        content.add(new Phrase(showUnknownIfBlank(faceSheetDto.getResidentName()), HELVETICA_9));
        residentNameCell.setPhrase(content);
        residentNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        residentNameCell.setBorder(Rectangle.BOTTOM);
        residentNameCell.setColspan(3);
        residentNameCell.setMinimumHeight(20);

        PdfPCell preferredNameCell = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Preferred Name:  ", HELVETICA_9_BOLD));
        content.add(new Phrase(showUnknownIfBlank(faceSheetDto.getPreferredName()), HELVETICA_9));
        preferredNameCell.setPhrase(content);
        preferredNameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        preferredNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        preferredNameCell.setBorder(Rectangle.BOTTOM);
        preferredNameCell.setColspan(3);
        preferredNameCell.setMinimumHeight(20);

        header.addCell(companyInfo);
        header.addCell(docInfo);
        header.addCell(residentNameCell);
        header.addCell(preferredNameCell);

        header.writeSelectedRows(0, -1, document.leftMargin(),
                page.getHeight() - document.topMargin() + header.getTotalHeight(), pdfContentByte);
    }

    private PdfPCell createEmptyRow(int columnSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setMinimumHeight(15);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(columnSpan);
        return cell;
    }

    private String displayDate(Instant date, ZoneId zoneId) {
        return (date == null) ? UNKNOWN_VALUE : DateTimeUtils.formatDate(date, zoneId);
    }

    private String displayDateTime(Instant date, ZoneId zoneId) {
        return (date == null) ? UNKNOWN_VALUE : DateTimeUtils.formatDateTimeWithZone(date, zoneId);
    }

    private String getDisplayName(CcdCode code) {
        return code != null ? code.getDisplayName() : null;
    }

    private void setContactAddress(FaceSheetDto.Contact contact, Person participantPerson) {
        if (CollectionUtils.isNotEmpty(participantPerson.getAddresses())) {
            Address address = participantPerson.getAddresses().get(0);
            if (address != null) {
                contact.setAddress1(showEmptyIfBlank(address.getStreetAddress()));
                contact.setAddress2(showEmptyIfBlank(address.getCity()) + " " + showEmptyIfBlank(address.getState())
                        + " " + showEmptyIfBlank(address.getPostalCode()));
            }
        }
    }

    private void setFullName(FaceSheetDto.Contact contact, Person participantPerson, boolean withMiddleName) {
        if (CollectionUtils.isNotEmpty(participantPerson.getNames())) {
            Name name = participantPerson.getNames().get(0);
            if (name != null)
                contact.setName(name.getGiven() + " "
                        + (StringUtils.isNotBlank(name.getMiddle()) && withMiddleName ? name.getMiddle() + " " : "")
                        + name.getFamily());
        }
    }

    private void setContactTelecomWithEmail(FaceSheetDto.Contact contact, Person participantPerson, Pattern pattern) {
        if (CollectionUtils.isNotEmpty(participantPerson.getTelecoms())) {
            Set<String> telecoms = new HashSet<>();
            for (Telecom telecom : participantPerson.getTelecoms()) {
                if (StringUtils.isNotBlank(telecom.getValue())) {
                    String value = telecom.getValue();
                    if (value.startsWith("E-mail")) {
                        value = value.replace("E-mail", "");
                    }
                    boolean validPhone = pattern.matcher(value).matches();
                    if (!validPhone) {
                        telecoms.add(value);
                    } else {
                        StringBuilder telecomValue = new StringBuilder();
                        if (StringUtils.isNotBlank(telecom.getUseCode())) {
                            telecomValue.append(telecom.getUseCode());
                            telecomValue.append(": ");
                        }
                        telecomValue.append(value);
                        telecoms.add(telecomValue.toString());
                    }
                }
            }

            contact.setPhone(StringUtils.join(telecoms, "\n"));
        }
    }

    private void setContactTelecom(FaceSheetDto.Contact contact, Person participantPerson) {
        if (CollectionUtils.isNotEmpty(participantPerson.getTelecoms())) {
            contact.setPhone(convertTelecoms(participantPerson.getTelecoms()));
        }
    }

    private String convertTelecoms(List<PersonTelecom> telecoms) {
        List<String> sumTelecoms = new ArrayList<>();
        for (Telecom telecom : telecoms) {
            StringBuilder telecomValue = new StringBuilder();
            if (StringUtils.isNotBlank(telecom.getUseCode())) {
                telecomValue.append(telecom.getUseCode());
                telecomValue.append(": ");
            }
            if (StringUtils.isNotBlank(telecom.getValue())) {
                telecomValue.append(telecom.getValue());
                sumTelecoms.add(telecomValue.toString());
            }
        }
        return StringUtils.join(sumTelecoms, ", ");
    }

    private String showEmptyIfBlank(String obj) {
        return Optional.ofNullable(obj).orElse("");
    }

    private String showUnknownIfBlank(String str) {
        return (StringUtils.isBlank(str)) ? UNKNOWN_VALUE : str;
    }

    private void writeToStream(FaceSheetDto faceSheetDto, OutputStream out, ZoneId zoneId) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 20, 20, 80, 40);

        // First pass: create document
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, buffer);

        document.open();
        createDocumentBody(document, faceSheetDto, zoneId);
        document.close();

        // Second pass: add the header and the footer
        PdfReader reader = new PdfReader(buffer.toByteArray());
        PdfStamper stamper = new PdfStamper(reader, out);

        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            createDocumentHeader(document, faceSheetDto, i, n, stamper.getOverContent(i), zoneId);
            if (i != n) {
                createDocumentFooter(document, stamper.getOverContent(i));
            }
        }

        stamper.close();
        reader.close();
    }
}
