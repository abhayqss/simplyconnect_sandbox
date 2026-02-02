package com.scnsoft.eldermark.services.facesheet;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.dao.carecoordination.EventJpaDao;
import com.scnsoft.eldermark.dao.carecoordination.NoteDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.entity.xds.datatype.XPNPersonName;
import com.scnsoft.eldermark.entity.xds.datatype.XTNPhoneNumber;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.GT1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.message.IN1ListSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.FaceSheetDto;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FacesheetServiceImpl implements FacesheetService {
    @Autowired
    private ResidentService residentService;

    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private AllergyDao allergyDao;

    @Autowired
    private ParticipantDao participantDao;

    @Autowired
    private PharmacyDao pharmacyDao;

    @Autowired
    private MedicalProfessionalDao medicalProfessionalDao;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private PaySourceHistoryDao paySourceHistoryDao;

    @Autowired
    private CareHistoryDao careHistoryDao;

    @Autowired
    private GuardianDao guardianDao;

    @Autowired
    private DocumentationOfDao documentationOfDao;

    @Autowired
    private EventJpaDao eventJpaDao;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private NoteDao noteDao;
    
    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    private final static String PHONE_PATTERN = "^[0-9\\-\\+()\\s]+$";
    private final static String SSN_GROUPS = "^(\\d{3})(\\d{2})(\\d{4})$";

    @Override
    public FaceSheetDto construct(long residentId, boolean aggregated) {
        Resident resident = residentService.getResident(residentId);

        FaceSheetDto facesheetDto = new FaceSheetDto();
        facesheetDto.setFaceSheetPrintedTime(new Date());

        final Set<Resident> mergedResidents;
        final List<Long> residentIds = new ArrayList<Long>();
        if (aggregated) {
            mergedResidents = residentService.getDirectMergedResidents(resident);
            mergedResidents.add(resident);
            CollectionUtils.collect(mergedResidents, new BeanToPropertyValueTransformer("id"), residentIds);
        } else {
            mergedResidents = Collections.singleton(resident);
            residentIds.add(residentId);
        }

        buildDemographics(facesheetDto, resident, residentIds);
        buildContacts(facesheetDto, residentIds);
        buildMedicalContacts(facesheetDto, resident, residentIds);
        buildBilling(facesheetDto, resident, residentIds);
        buildMedicalInformation(facesheetDto, residentIds);
        buildNotesAlertsAndOrders(facesheetDto, mergedResidents, residentIds);
        buildAdvanceDirectives(facesheetDto, mergedResidents, residentIds);

        return facesheetDto;
    }

    private void buildDemographics(FaceSheetDto facesheetDto, Resident resident, List<Long> residentIds) {
        Organization facility = resident.getFacility();
        if(facility != null) {
            facesheetDto.setCompanyName(facility.getName());

            if (CollectionUtils.isNotEmpty(facility.getAddresses())) {
                OrganizationAddress address = facility.getAddresses().get(0);
                if(address != null) {
                    facesheetDto.setCompanyAddress1(Objects.toString(address.getStreetAddress(), "") + " " + Objects.toString(address.getCity(), ""));
                    facesheetDto.setCompanyAddress2(Objects.toString(address.getState(), "") + " " + Objects.toString(address.getPostalCode(), ""));
                }
            }

            if(facility.getTelecom() != null) {
                OrganizationTelecom telecom = facility.getTelecom();
                facesheetDto.setCompanyPhone(telecom.getValue());
            } else if (StringUtils.isNotBlank(facility.getPhone())) {
                facesheetDto.setCompanyPhone(facility.getPhone());
            }
        }

        facesheetDto.setResidentName(resident.getFirstName() + " " +  (StringUtils.isNotBlank(resident.getMiddleName()) ? resident.getMiddleName() + " " : "" ) + resident.getLastName());

        facesheetDto.setPreferredName(resident.getPreferredName());

        facesheetDto.setMedicalRecordNumber(resident.getMedicalRecordNumber());

        facesheetDto.setDob(resident.getBirthDate());

        facesheetDto.setGender(getDisplayName(resident.getGender()));

        facesheetDto.setReligion(getDisplayName(resident.getReligion()));
        facesheetDto.setMaritalStatus(getDisplayName(resident.getMaritalStatus()));
        facesheetDto.setRace(getDisplayName(resident.getRace()));

        facesheetDto.setAdmissionDate(resident.getAdmitDate());

        for (Telecom telecom : resident.getPerson().getTelecoms()) {
            if (PersonTelecomCode.HP.name().equals(telecom.getUseCode())) {
                facesheetDto.setHomePhone(telecom.getValue());
            } else if (PersonTelecomCode.EMAIL.name().equals(telecom.getUseCode()) && StringUtils.isBlank(facesheetDto.getEmail())) {
                facesheetDto.setEmail(telecom.getValue());
            } else {
                facesheetDto.setOtherPhone(telecom.getValue());
            }
        }

        if (CollectionUtils.isNotEmpty(resident.getLanguages())) {
            Language language = resident.getLanguages().get(0);
            facesheetDto.setPrimaryLanguage(getDisplayName(language.getCode()));
        }

        if (null != ObjectUtils.firstNonNull(
                resident.getPrevAddrStreet(), resident.getPrevAddrCity(),
                resident.getPrevAddrState(), resident.getPrevAddrZip())) {
            String prevAddr = String.format("%s %s %s %s",
                    resident.getPrevAddrStreet(), resident.getPrevAddrCity(),
                    resident.getPrevAddrState(), resident.getPrevAddrZip());
            facesheetDto.setPreviousAddress(prevAddr);
        }

        if(resident.getBirthDate() != null) {
            LocalDate birthDate = new LocalDate(resident.getBirthDate().getTime());
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birthDate, now);
            int yearsAge = age.getYears();
            if (yearsAge < 1) {
                Days days = Days.daysBetween(birthDate, now);
                facesheetDto.setAge(String.valueOf(days.getDays()));
            } else {
                facesheetDto.setAge(String.valueOf(yearsAge));
            }

        }

        facesheetDto.setVeteran(resident.getVeteran());

        facesheetDto.setUnit(resident.getUnitNumber());

        List<AdmittanceHistory> admittanceHistories = admittanceHistoryDao.listByResidentIds(residentIds, null);
        if (CollectionUtils.isNotEmpty(admittanceHistories)) {
            Date latestAdmitDate = null;
            for (AdmittanceHistory admittanceHistory : admittanceHistories ) {
                if (admittanceHistory.getAdmitDate() != null
                        && (admittanceHistory.getOrganizationId() == null || (resident.getFacility() != null && resident.getFacility().getId().equals(admittanceHistory.getOrganizationId())))
                        && (latestAdmitDate == null || latestAdmitDate.before(admittanceHistory.getAdmitDate()))) {
                    latestAdmitDate = admittanceHistory.getAdmitDate();
                }
            }
            facesheetDto.setReadmissionDate(latestAdmitDate);
        }
        List<String> countyAdmittedFroms = new ArrayList<String>();
        List<String> livingStatuses = new ArrayList<String>();
        for (AdmittanceHistory admittanceHistory : admittanceHistories) {
            if(admittanceHistory.getCountyAdmittedFrom() != null)
                countyAdmittedFroms.add(admittanceHistory.getCountyAdmittedFrom());
            if(admittanceHistory.getLivingStatus() != null)
                livingStatuses.add(admittanceHistory.getLivingStatus().getDescription());
        }
        facesheetDto.setCountyAdmittedFrom(StringUtils.join(countyAdmittedFroms, ", "));
        facesheetDto.setAdmittedFrom(StringUtils.join(livingStatuses, ", "));

        final Pageable top1 = new PageRequest(0 , 1);
        List<CareHistory> careHistory = careHistoryDao.listByResidentIds(residentIds, top1);
        if (CollectionUtils.isNotEmpty(careHistory)) {
            if (careHistory.get(0) != null) {
                facesheetDto.setStartOfCare(careHistory.get(0).getStartDate());
            }
        }

        facesheetDto.setEvacuationStatus(resident.getEvacuationStatus());
    }

    private void buildContacts(FaceSheetDto facesheetDto, Collection<Long> residentIds) {
        Set<FaceSheetDto.Contact> contacts = new TreeSet<>();
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        
        List<ResidentCareTeamMember> residentCareTeamMembers= residentCareTeamMemberDao.getCareTeamMembersToBeIncludedInFacesheet(residentIds);
        for(ResidentCareTeamMember residentCareTeamMember : residentCareTeamMembers) {
            FaceSheetDto.Contact contact = facesheetDto.new Contact();
            Employee employee = residentCareTeamMember.getEmployee();
            if(employee !=null) {
                if (StringUtils.isNotBlank(employee.getFullName())) {
                    contact.setName(employee.getFullName());
                }
                if(residentCareTeamMember.getCareTeamRelationship()!=null) {
                    contact.setRelationship(residentCareTeamMember.getCareTeamRelationship().getName());
                }
                Person participantPerson = employee.getPerson();
                if(participantPerson != null) {
                    setContactAddress(contact,participantPerson);
                    setContactTelecom(contact,participantPerson, pattern);
                }
                contacts.add(contact);
            }
        }

        List<ContactWithRelationship> contactsWithRelationship = new ArrayList<>();
        contactsWithRelationship.addAll(participantDao.listCcdParticipants(residentIds));
        contactsWithRelationship.addAll(guardianDao.listByResidentIds(new ArrayList<>(residentIds)));

        for (ContactWithRelationship contactWithRelationship : contactsWithRelationship) {
            Person participantPerson = contactWithRelationship.getPerson();
            if(participantPerson != null) {
                FaceSheetDto.Contact contact = facesheetDto.new Contact();
                contact.setRelationship(getDisplayName(contactWithRelationship.getRelationship()));
                if (CollectionUtils.isNotEmpty(participantPerson.getNames())) {
                    Name name = participantPerson.getNames().get(0);
                    if (name != null)
                        contact.setName(name.getGiven() + " " +  (StringUtils.isNotBlank(name.getMiddle()) ? name.getMiddle() + " " : "" ) + name.getFamily());
                }
                setContactAddress(contact,participantPerson);
                setContactTelecom(contact,participantPerson, pattern);
                contacts.add(contact);
                
            }
        }

        facesheetDto.setContactList(new ArrayList<>(contacts));
    }
    
    private void setContactAddress(FaceSheetDto.Contact contact, Person participantPerson) {
        if (CollectionUtils.isNotEmpty(participantPerson.getAddresses())) {
            Address address = participantPerson.getAddresses().get(0);
            if (address != null) {
                contact.setAddress1(Objects.toString(address.getStreetAddress(), ""));
                contact.setAddress2(Objects.toString(address.getCity(), "") + " " + Objects.toString(address.getState(), "") + " " + Objects.toString(address.getPostalCode(), ""));
            }
        }
    }
    
    private void setContactTelecom(FaceSheetDto.Contact contact, Person participantPerson, Pattern pattern) {
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

    private void buildMedicalContacts(FaceSheetDto facesheetDto, Resident resident, List<Long> residentIds) {
        Set<FaceSheetDto.MedicalProfessional> medProfessionalDtos = new HashSet<>();
        List<ContactWithRole> contacts = new ArrayList<>();
        contacts.addAll(medicalProfessionalDao.listByResidentIds(residentIds));
        contacts.addAll(documentationOfDao.listByResidentIds(residentIds));
        for (ContactWithRole contact : contacts) {
            if (CollectionUtils.isNotEmpty(contact.getPersons())) {
                for (Person person : contact.getPersons()) {
                    FaceSheetDto.MedicalProfessional medProfessionalDto = facesheetDto.new MedicalProfessional();
                    medProfessionalDto.setRole(contact.getRole());
                    StringBuilder info = new StringBuilder();
                    if(StringUtils.isNotBlank(contact.getOrganizationName())) {
                        info.append(contact.getOrganizationName());
                        info.append(", ");
                    }
                    if (person != null) {
                        StringBuilder personInfo = new StringBuilder();
                        if (CollectionUtils.isNotEmpty(person.getNames())) {
                            personInfo.append(person.getNames().get(0).getGiven() + " " + person.getNames().get(0).getFamily() + " ");
                        }
                        for (Telecom telecom : person.getTelecoms()) {
                            personInfo.append(telecom.getValue() + " ");
                        }
                        medProfessionalDto.setPersonRelatedData(personInfo.toString());
                        info.append(personInfo);
                    }
                    if(StringUtils.isNotBlank(contact.getNpi())) {
                        info.append("NPI: ");
                        info.append(contact.getNpi());
                    }
                    medProfessionalDto.setData(info.toString());
                    medProfessionalDtos.add(medProfessionalDto);
                }
            }
        }
        facesheetDto.setMedicalProfessional(new ArrayList<>(medProfessionalDtos));

        List<Organization> pharmacies = pharmacyDao.listPharmaciesAsOrganization(residentIds);
        Set<String> pharmacyNames = new HashSet<String>();
        for(Organization pharmacy : pharmacies) {
            String info = "";
            if (StringUtils.isNotBlank(pharmacy.getName())) {
                info += pharmacy.getName();
            }
            if (pharmacy.getTelecom() != null && StringUtils.isNotBlank(pharmacy.getTelecom().getValue())) {
                info += ", " + pharmacy.getTelecom().getValue();
            }
            pharmacyNames.add(info);
        }
        if (StringUtils.isNotBlank(resident.getCurrentPharmacyName())) {
            pharmacyNames.add(resident.getCurrentPharmacyName());
        }
        facesheetDto.setPharmacy(StringUtils.join(pharmacyNames, "; "));

        facesheetDto.setHospitalPref(resident.getHospitalPreference());
        facesheetDto.setTransportation(resident.getTransportationPreference());
        facesheetDto.setAmbulance(resident.getAmbulancePreference());
    }

    private void buildBilling(FaceSheetDto facesheetDto, Resident resident, List<Long> residentIds) {

        List<Long> adtMsgIds = eventJpaDao.getAdtMessageIdsForResidents(residentIds);
        List<AdtMessage> adtMessages = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(adtMsgIds)) {
            adtMessages = adtMessageDao.findAll(adtMsgIds);
        }

        Set<FaceSheetDto.Contact> responsibleParties = new TreeSet<>();
        for (AdtMessage adtMessage : adtMessages) {
            if (adtMessage instanceof GT1ListSegmentContainingMessage) {
                GT1ListSegmentContainingMessage guarantorMessage = (GT1ListSegmentContainingMessage)adtMessage;
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
                                contact.setAddress1(Objects.toString(personAddress.getStreetAddress(), ""));
                                contact.setAddress2(Objects.toString(personAddress.getCity(), "") + " " + Objects.toString(personAddress.getState(), "") + " " + Objects.toString(personAddress.getZip(), ""));
                            }
                        }
                        if (CollectionUtils.isNotEmpty(guarantorSegment.getGuarantorPhNumHomeList())) {
                            List<String> telecoms = new ArrayList<String>();

                            for(XTNPhoneNumber personTelecom: guarantorSegment.getGuarantorPhNumHomeList()) {
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
                                if(StringUtils.isNotBlank(telecom))
                                    telecoms.add(telecom);
                            }

                            contact.setPhone(StringUtils.join(telecoms, ", "));
                        }
                        responsibleParties.add(contact);

                    }
                }
            }
        }

        Collection<Participant> participants = participantDao.listResponsibleParties(residentIds);
        for(Participant participant: participants) {
            Person participantPerson = participant.getPerson();
            if(participantPerson != null) {
                FaceSheetDto.Contact contact = facesheetDto.new Contact();
                contact.setRelationship(getDisplayName(participant.getRelationship()));
                if (CollectionUtils.isNotEmpty(participantPerson.getNames()))
                    contact.setName(participantPerson.getNames().get(0).getGiven() +
                            " " + participantPerson.getNames().get(0).getFamily());
                if (CollectionUtils.isNotEmpty(participantPerson.getAddresses())) {
                    contact.setAddress1(Objects.toString(participantPerson.getAddresses().get(0).getStreetAddress(), ""));
                    contact.setAddress2(Objects.toString(participantPerson.getAddresses().get(0).getCity(), "") +
                            " " + Objects.toString(participantPerson.getAddresses().get(0).getState(), "") +
                            " " + Objects.toString(participantPerson.getAddresses().get(0).getPostalCode(), ""));
                }
                if (CollectionUtils.isNotEmpty(participantPerson.getTelecoms())) {
                    List<String> telecoms = new ArrayList<String>();
                    for(Telecom telecom: participantPerson.getTelecoms()) {
                        StringBuilder telecomValue = new StringBuilder();
                        if (StringUtils.isNotBlank(telecom.getUseCode())) {
                            telecomValue.append(telecom.getUseCode());
                            telecomValue.append(": ");
                        }
                        if(StringUtils.isNotBlank(telecom.getValue()))
                            telecomValue.append(telecom.getValue());
                            telecoms.add(telecomValue.toString());
                    }
                    contact.setPhone(StringUtils.join(telecoms, ", "));
                }
                responsibleParties.add(contact);
            }
        }

        facesheetDto.setResponsibleParty(new ArrayList<>(responsibleParties));

        facesheetDto.setSsn(formatSsn(resident.getSocialSecurity()));
        facesheetDto.setMedicareNumber(resident.getMedicareNumber());
        facesheetDto.setMedicaidNumber(resident.getMedicaidNumber());

        Set<String> primaryPayTypes = new HashSet<String>();
        for (PaySourceHistory paySourceHistory : paySourceHistoryDao.listByResidentIds(residentIds)) {
            primaryPayTypes.add(paySourceHistory.getPaySource());
        }
        facesheetDto.setPrimaryPayType(StringUtils.join(primaryPayTypes, ", "));

        Set<String> healthPlans = new HashSet<>();
        Set<String> networksAndPlansDuplicatesCheck = new HashSet<>();
        if(resident.getHealthPlans() != null) {
            for(ResidentHealthPlan healthPlan: resident.getHealthPlans()) {
                String network = null;
                String plan = healthPlan.getHealthPlanName();
                String networkWithPlan = buildHealthPlanUniquenessString(network, plan);
                if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                    String policy = healthPlan.getPolicyNumber();
                    String group = healthPlan.getGroupNumber();
                    healthPlans.add(buildHealthPlanString(network, plan, policy, group));
                }
            }
        }
        if (resident.getInNetworkInsurance() != null) {
            String network = resident.getInNetworkInsurance().getDisplayName();
            String plan = resident.getInsurancePlan() != null ? resident.getInsurancePlan().getDisplayName() : null;
            String networkWithPlan = buildHealthPlanUniquenessString(network, plan);
            if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                String policy = resident.getMemberNumber();
                String group = resident.getGroupNumber();
                healthPlans.add(buildHealthPlanString(network, plan, policy, group));
            }
        }
        for (AdtMessage adtMessage : adtMessages) {
            if (adtMessage instanceof IN1ListSegmentContainingMessage) {
                IN1ListSegmentContainingMessage insuranceMessage = (IN1ListSegmentContainingMessage)adtMessage;
                if (CollectionUtils.isNotEmpty(insuranceMessage.getIn1List())) {
                    for (IN1InsuranceSegment insuranceSegment : insuranceMessage.getIn1List()) {
                        String network = insuranceSegment.getInsuranceCompanyName() != null ? insuranceSegment.getInsuranceCompanyName().getOrganizationName() : null;
                        String plan = insuranceSegment.getInsurancePlanId() != null ? insuranceSegment.getInsurancePlanId().getText() : null;
                        String networkWithPlan = buildHealthPlanUniquenessString(network, plan);
                        if (networksAndPlansDuplicatesCheck.add(networkWithPlan)) {
                            String policy = null;
                            String group = insuranceSegment.getGroupNumber();
                            healthPlans.add(buildHealthPlanString(network, plan, policy, group));
                        }
                    }
                }
            }
        }
        facesheetDto.setHealthPlanNumber(StringUtils.join(healthPlans, "; "));

        if(resident.getDentalInsurance() != null) {
            Set<String> dentalPlans = new HashSet<>();
            String[] rows = resident.getDentalInsurance().split("\\r+");
            for(String healthPlan: rows) {
                String[] columns = healthPlan.split("\\t");
                if(columns.length == 3) {
                    dentalPlans.add(String.format("%s, policy#: %s, group#: %s", columns[0], columns[1], columns[2]));
                }
            }
            if (CollectionUtils.isNotEmpty(dentalPlans)) {
                facesheetDto.setDentalPlanNumber(StringUtils.join(dentalPlans, "; "));
            } else {
                facesheetDto.setDentalPlanNumber(resident.getDentalInsurance());
            }
        }

    }

    private String formatSsn(String ssn) {
        String result = ssn;
        if (StringUtils.isNotBlank(ssn) && ssn.length() == 9) {
            Pattern ssnPattern = Pattern.compile(SSN_GROUPS);
            Matcher matcher = ssnPattern.matcher(ssn);
            if (matcher.find()) {
                result = String.format("%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        return result;
    }

    private String buildHealthPlanUniquenessString(String network, String plan) {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isNotBlank(network)) {
            result.append("1.network:");
            result.append(network);
        }
        if (StringUtils.isNotBlank(plan)) {
            result.append("2.plan:");
            result.append(plan);
        }
        return result.toString();
    }

    private String buildHealthPlanString(String network, String plan, String policy, String group) {
        StringBuilder result = new StringBuilder();
        Boolean isFirst = true;
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


    private void buildMedicalInformation(FaceSheetDto facesheetDto, List<Long> residentIds) {
        Collection<Allergy> allergies = allergyDao.listByResidentIds(residentIds);
        Map<String, FaceSheetDto.Allergy> activeAllergies = new HashMap<>();
        for (Allergy allergy : allergies) {
            Collection<AllergyObservation> allergyObservationList = allergy.getAllergyObservations();
            for (AllergyObservation allergyObservation : allergyObservationList) {
                // adds allergy if it is not contained already
                if (StringUtils.isNotBlank(allergyObservation.getProductText()) &&
                        !activeAllergies.containsKey(allergyObservation.getProductText())) {
                    FaceSheetDto.Allergy facesheetAllergy = facesheetDto.new Allergy();
                    facesheetAllergy.setSubstance(allergyObservation.getProductText());
                    facesheetAllergy.setStartDate(allergyObservation.getTimeLow());
                    if (CollectionUtils.isNotEmpty(allergyObservation.getReactionObservations())) {
                        List<String> reactions = new ArrayList<>();
                        for (ReactionObservation reactionObservation : allergyObservation.getReactionObservations()) {
                            reactions.add(reactionObservation.getReactionText());
                        }
                        facesheetAllergy.setReaction(StringUtils.join(reactions, ","));
                    }
                    facesheetAllergy.setType(allergyObservation.getAdverseEventTypeText());
                    if (allergyObservation.getDatabase() != null) {
                        facesheetAllergy.setDataSource(allergyObservation.getDatabase().getName());
                    }
                    activeAllergies.put(allergyObservation.getProductText(), facesheetAllergy);
                }
            }
        }
        List<FaceSheetDto.Allergy> facesheetAllergies = new ArrayList<>(activeAllergies.values());
        Collections.sort(facesheetAllergies, Collections.<FaceSheetDto.Allergy>reverseOrder());
        facesheetDto.setAllergies(facesheetAllergies);


        Collection<Problem> problems = problemDao.listByResidentIds(residentIds);
        Map<String, FaceSheetDto.Diagnosis> activeDiagnosis = new HashMap<>();
        for (Problem problem : problems) {
            if (ProblemStatusCode.ACTIVE.getCode().equalsIgnoreCase(problem.getStatusCode()) ||
                    ProblemStatusCode.ACTIVE.getDisplayName().equalsIgnoreCase(problem.getStatusCode())) {
                List<ProblemObservation> problemObservationList = problem.getProblemObservations();
                if (CollectionUtils.isNotEmpty(problemObservationList)) {
                    for (ProblemObservation problemObservation : problemObservationList) {
                        // adds problem if it is not contained already
                        if (StringUtils.isNotBlank(problemObservation.getProblemName()) &&
                                !activeDiagnosis.containsKey(problemObservation.getProblemName())) {
                            FaceSheetDto.Diagnosis facesheetDiagnosis = facesheetDto.new Diagnosis();
                            facesheetDiagnosis.setDiagnosis(problemObservation.getProblemName());
                            facesheetDiagnosis.setCode(problemObservation.getProblemIcdCode());
                            facesheetDiagnosis.setCodeSet(problemObservation.getProblemIcdCodeSet());
                            if (problemObservation.getProblemType() != null) {
                                facesheetDiagnosis.setType(problemObservation.getProblemType().getDisplayName());
                            }
                            facesheetDiagnosis.setIdentified(problemObservation.getProblemDateTimeLow());
                            if (problemObservation.getDatabase() != null || problemObservation.getManual()) {
                                if (problemObservation.getManual()) {
                                    facesheetDiagnosis.setDataSource("Simply Connect HIE");
                                } else {
                                    facesheetDiagnosis.setDataSource(problemObservation.getDatabase().getName());
                                }
                            }
                            activeDiagnosis.put(problemObservation.getProblemName(), facesheetDiagnosis);
                        }
                    }
                }
            }
        }
        List<FaceSheetDto.Diagnosis> facesheetDiagnosis = new ArrayList<>(activeDiagnosis.values());
        Collections.sort(facesheetDiagnosis, Collections.<FaceSheetDto.Diagnosis>reverseOrder());
        facesheetDto.setDiagnosis(facesheetDiagnosis);
    }

    private void buildNotesAlertsAndOrders(FaceSheetDto facesheetDto, Collection<Resident> residents, List<Long> residentIds) {
        List<FaceSheetDto.Note> activeNotes = new ArrayList<>();
        for (Resident resident : residents) {
            if (CollectionUtils.isNotEmpty(resident.getAlertNotes())) {
                for (ResidentNotes note : resident.getAlertNotes()) {
                    if (note.isActive()) {
                        FaceSheetDto.Note activeNote = facesheetDto.new Note();
                        activeNote.setNote(note.getNote());
                        activeNote.setDate(note.getStartDate());
                        activeNotes.add(activeNote);
                    }
                }
            }
        }
        List<Note> notes = noteDao.getTop3ResidentNotesExcludingPatientUpdate(residentIds, new PageRequest(0, 3));
        for (Note note : notes) {
            FaceSheetDto.Note activeNote = facesheetDto.new Note();
            activeNote.setDate(note.getLastModifiedDate());
            activeNote.setType(note.getSubType().getDescription());
            activeNote.setSubjective(note.getSubjective());
            activeNote.setObjective(note.getObjective());
            activeNote.setAssessment(note.getAssessment());
            activeNote.setPlan(note.getPlan());
            activeNotes.add(activeNote);
        }
        Collections.sort(activeNotes, Collections.<FaceSheetDto.Note>reverseOrder());
        facesheetDto.setNotes(activeNotes);


        Map<String, FaceSheetDto.Order> activeOrders = new HashMap<>();
        for (Resident resident : residents) {
            if (CollectionUtils.isNotEmpty(resident.getOrders())) {
                for (ResidentOrder order : resident.getOrders()) {
                    if (order.isActive() && !activeOrders.containsKey(order.getName())) {
                        FaceSheetDto.Order facesheetOrder = facesheetDto.new Order();
                        facesheetOrder.setName(order.getName());
                        facesheetOrder.setStartDate(order.getStartDate());
                        activeOrders.put(order.getName(), facesheetOrder);
                    }
                }
            }
        }
        List<FaceSheetDto.Order> facesheetOrders = new ArrayList<>(activeOrders.values());
        Collections.sort(facesheetOrders, Collections.<FaceSheetDto.Order>reverseOrder());
        facesheetDto.setOrders(facesheetOrders);
    }

    private void buildAdvanceDirectives(FaceSheetDto facesheetDto, Collection<Resident> residents, List<Long> residentIds) {
        Collection<AdvanceDirective> advanceDirectiveList = advanceDirectiveDao.listByResidentIds(residentIds);

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
                                    facesheetVerifier.append(name.getPrefix());
                                    facesheetVerifier.append(" ");
                                }
                                facesheetVerifier.append(name.getGiven());
                                facesheetVerifier.append(" ");
                                facesheetVerifier.append(name.getFamily());
                            }
                            verifiers.add(facesheetVerifier.toString());
                        }
                    }
                    facesheetAdvanceDirective.setVerification(StringUtils.join(verifiers, ", "));
                }
                facesheetAdvanceDirective.setDateStarted(advanceDirective.getTimeLow());
                if (advanceDirective.getDatabase() != null) {
                    facesheetAdvanceDirective.setDataSource(advanceDirective.getDatabase().getName());
                }
                advanceDirectives.add(facesheetAdvanceDirective);
            }
        }

        for (Resident resident : residents) {
            if (StringUtils.isNotBlank(resident.getAdvanceDirectiveFreeText())) {
                FaceSheetDto.AdvanceDirective facesheetAdvanceDirective = facesheetDto.new AdvanceDirective();
                facesheetAdvanceDirective.setType(resident.getAdvanceDirectiveFreeText());
                if (resident.getDatabase() != null) {
                    facesheetAdvanceDirective.setDataSource(resident.getDatabase().getName());
                }
                advanceDirectives.add(facesheetAdvanceDirective);
            }
        }

        List<FaceSheetDto.AdvanceDirective> facesheetAdvanceDirectives = new ArrayList<>(advanceDirectives);
        Collections.sort(facesheetAdvanceDirectives, Collections.<FaceSheetDto.AdvanceDirective>reverseOrder());
        facesheetDto.setAdvanceDirectives(facesheetAdvanceDirectives);
    }

    private String getDisplayName(CcdCode code) {
        return code != null ? code.getDisplayName() : null;
    }
}
