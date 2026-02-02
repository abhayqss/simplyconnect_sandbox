package com.scnsoft.eldermark.facades.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.DocumentationOfDao;
import com.scnsoft.eldermark.dao.MedicalProfessionalDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.duke.MatchResult;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.facades.beans.ComprehensiveAssessmentBean;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentService;
import com.scnsoft.eldermark.services.carecoordination.ResidentAssessmentResultService;
import com.scnsoft.eldermark.services.carecoordination.ResidentMatcherService;
import com.scnsoft.eldermark.shared.BillingInfo;
import com.scnsoft.eldermark.shared.SpecialtyPhysicianDto;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.PharmacyDto;
import com.scnsoft.eldermark.shared.carecoordination.PrimaryCarePhysicianDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static com.scnsoft.eldermark.services.PersonService.getPersonTelecomValue;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by pzhurba on 05-Oct-15.
 */
@Component
public class PatientFacadeImpl implements PatientFacade {

    private static final Logger logger = LoggerFactory.getLogger(PatientFacadeImpl.class);
    private static final String PRIMARY_CARE_PHYSICIAN_ROLE = "Primary Physician";

    @Autowired
    private CareCoordinationResidentService careCoordinationResidentService;

    @Autowired
    private StateService stateService;

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private DocumentationOfDao documentationOfDao;

    @Autowired
    private ResidentMatcherService residentMatcherService;

    @Autowired
    private MedicalProfessionalDao medicalProfessionalDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private ResidentAssessmentResultService residentAssessmentResultService;

    @Override
    public PatientDto getPatientDto(Long patientId, boolean showSsn, boolean checkEditable) {
        long startTime = System.currentTimeMillis();
        CareCoordinationResident resident =careCoordinationResidentService.get(patientId);
        long stopTime1 = System.currentTimeMillis();
        System.out.println("careCoordinationResidentService.get:" + (stopTime1 - startTime) + "ms");
        PatientDto patientDto = convert(resident, showSsn, false, checkEditable);
        System.out.println("convert:" + (System.currentTimeMillis() - stopTime1) + "ms");
        return patientDto;
    }

    @Override
    public PatientDto getPatientDetailsDto(Long patientId) {
        CareCoordinationResident resident =careCoordinationResidentService.get(patientId);
        PatientDto patientDto = convert(resident, false, false, true);
        populateAdmittanceHistory(patientDto, resident);
        List<ComprehensiveAssessmentBean> comprehensiveAssessmentList = residentAssessmentResultService.parseComprehensiveAssessments(resident.getId());
        List<ContactWithRole> contacts = new ArrayList<>();
        contacts.addAll(medicalProfessionalDao.listByResidentIds(asList(resident.getId())));
        contacts.addAll(documentationOfDao.listByResidentIds(asList(resident.getId())));

        populatePhones(patientDto, resident, comprehensiveAssessmentList);
        populatePrimaryCarePhysicianInfo(patientDto, resident, comprehensiveAssessmentList, contacts);
        populateSpecialtyPhysicians(patientDto, comprehensiveAssessmentList, contacts);
        populatePharmacy(patientDto, resident, comprehensiveAssessmentList);
        populateBilling(patientDto, resident);
        return patientDto;
    }

    private void populateBilling(PatientDto patientDto, CareCoordinationResident resident){
        patientDto.setMedicareNumber(resident.getMedicareNumber());
        patientDto.setMedicaidNumber(resident.getMedicaidNumber());
        patientDto.setDentalPlan(resident.getDentalInsurance());

        List<BillingInfo> billingInfos = new ArrayList<>();
        if (resident.getInNetworkInsurance() != null || isNotBlank(resident.getInsurancePlanName())|| isNotBlank(resident.getMemberNumber()) || isNotBlank(resident.getGroupNumber())){
            billingInfos.add(new BillingInfo(
                    resident.getInNetworkInsurance() != null ? resident.getInNetworkInsurance().getDisplayName() : null,
                    resident.getInsurancePlanName(),
                    resident.getMemberNumber(),
                    resident.getGroupNumber()));
        };
        billingInfos.addAll(convertToBillingInfo(resident.getHealthPlans()));
        billingInfos.addAll(getBillingInfoFromAdt(resident));

        patientDto.setHealthPlans(formatHealthPlansString(unqiueBillingInfos(billingInfos)));
    }

    private List<BillingInfo> unqiueBillingInfos(List<BillingInfo> billingInfos){
        List<BillingInfo> uniqueBillingInfos = new ArrayList<>();
        List<String> insurancePlanNames = new ArrayList<>();

        for (BillingInfo billingInfo : billingInfos){
            if (!insurancePlanNames.contains(billingInfo.getPlanName())){
                if (billingInfo.getPlanName() != null){
                    insurancePlanNames.add(billingInfo.getPlanName());
                }
                uniqueBillingInfos.add(billingInfo);
            }
        }
        return uniqueBillingInfos;
    }

    private List<BillingInfo> getBillingInfoFromAdt(CareCoordinationResident resident) {
        List<BillingInfo> billingInfos = new ArrayList<>();
        //todo performance optimization needed
//        List<AdtMessage> adtMessages = adtMessageDao.findByResidentId(resident.getId());
//        logger.info("[PatientFacadeImpl] adt messages number {}", adtMessages.size());
//
//        for (AdtMessage adtMessage : adtMessages) {
//            if (adtMessage instanceof IN1ListSegmentContainingMessage) {
//                List<IN1InsuranceSegment> in1InsuranceSegments = ((IN1ListSegmentContainingMessage) adtMessage).getIn1List();
//                if (isNotEmpty(in1InsuranceSegments)) {
//                    for (IN1InsuranceSegment in1InsuranceSegment : in1InsuranceSegments) {
//                        BillingInfo billingInfo = new BillingInfo();
//                        XONExtendedCompositeNameAndIdForOrganizations companyNameObj = in1InsuranceSegment.getInsuranceCompanyName();
//                        if (companyNameObj != null) {
//                            billingInfo.setInsuranceName(companyNameObj.getOrganizationName());
//                        }
//                        ;
//                        if (in1InsuranceSegment.getInsurancePlanId() != null) {
//                            billingInfo.setPlanName(in1InsuranceSegment.getInsurancePlanId().getText());
//                        }
//                        billingInfo.setGroupNumber(in1InsuranceSegment.getGroupNumber());
//                        billingInfos.add(billingInfo);
//                    }
//                }
//            }
//        }

        return billingInfos;
    }

    private List<BillingInfo> convertToBillingInfo(List<ResidentHealthPlan> healthPlans){
        List<BillingInfo> billingInfos = new ArrayList<>();
        for (ResidentHealthPlan healthPlan : healthPlans){
            billingInfos.add(new BillingInfo(null, healthPlan.getHealthPlanName(), healthPlan.getPolicyNumber(), healthPlan.getGroupNumber()));
        }
        return billingInfos;
    }

    private String formatHealthPlansString(List<BillingInfo> billingInfos){
        StringBuilder sb = new StringBuilder();
        for (BillingInfo billingInfo : billingInfos){
            sb.append(getDisplayHealthPlan(billingInfo));
        }
        return sb.toString();
    }

    private void populatePharmacy(PatientDto patientDto, CareCoordinationResident resident, List<ComprehensiveAssessmentBean> comprehensiveAssessmentList){
        List<PharmacyDto> pharmacyDtos = new ArrayList<>();
        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList){
            if (StringUtils.isNotBlank(comprehensiveAssessment.getPharmacyName())){
                PharmacyDto pharmacyDto = new PharmacyDto();
                pharmacyDto.setName(comprehensiveAssessment.getPharmacyName());
                pharmacyDto.setPhone(comprehensiveAssessment.getPharmacyPhone());
                List<String> addressDetails = asList(comprehensiveAssessment.getPharmacyStreet(),
                        comprehensiveAssessment.getPharmacyCity(),
                        comprehensiveAssessment.getSpecialtyState(),
                        comprehensiveAssessment.getPharmacyZipCode()
                        );
                pharmacyDto.setAddress(getDisplayAddress(addressDetails));
                pharmacyDtos.add(pharmacyDto);
                break;
            }
        }

        if (StringUtils.isNotBlank(resident.getCurrentPharmacyName())){
            PharmacyDto residentPharmacyDto = new PharmacyDto();
            residentPharmacyDto.setName(resident.getCurrentPharmacyName());
            pharmacyDtos.add(residentPharmacyDto);
        }

        List<Organization> pharmacyOrganizations = organizationDao.getPharmacyByResidentId(resident.getId());
        for (Organization organization : pharmacyOrganizations){
            PharmacyDto pharmacyOrg = new PharmacyDto();
            pharmacyOrg.setName(organization.getName());
            pharmacyOrg.setPhone(organization.getPhone());
            if (isNotEmpty(organization.getAddresses())){
                OrganizationAddress orgAddress = organization.getAddresses().get(0);
                List<String> addressDetails = asList(orgAddress.getStreetAddress(),
                        orgAddress.getCity(),
                        orgAddress.getState(),
                        orgAddress.getPostalCode());
                pharmacyOrg.setAddress(getDisplayAddress(addressDetails));
            }
            pharmacyDtos.add(pharmacyOrg);
        }

        patientDto.setPharmacyDtos(removeDuplicates(pharmacyDtos));
    }

    private List<PharmacyDto> removeDuplicates(List<PharmacyDto> pharmacyDtos){
        List<String> pharmacyNames = new ArrayList<>();
        List<PharmacyDto> uniquePharmacies = new ArrayList<>();
        for (PharmacyDto pharmacyDto : pharmacyDtos){
            if (!pharmacyNames.contains(pharmacyDto.getName())){
                pharmacyNames.add(pharmacyDto.getName());
                uniquePharmacies.add(pharmacyDto);
            }
        }
        return uniquePharmacies;
    }

    private void populatePhones(PatientDto patientDto, CareCoordinationResident resident, List<ComprehensiveAssessmentBean> comprehensiveAssessmentList){
        Person person = resident.getPerson();
        String cellPhone = getPersonTelecomValue(person, PersonTelecomCode.MC);
        String workPhone = getPersonTelecomValue(person, PersonTelecomCode.WP);
        String homePhone = getPersonTelecomValue(person, PersonTelecomCode.HP);

        if (cellPhone == null && isNotEmpty(comprehensiveAssessmentList)){
           cellPhone = parseCellPhoneFromComprehensive(comprehensiveAssessmentList);
        }
        if (workPhone == null && isNotEmpty(comprehensiveAssessmentList)){
            workPhone = parseWorkPhoneFromComprehensive(comprehensiveAssessmentList);
        }
        if (homePhone == null && isNotEmpty(comprehensiveAssessmentList)){
            homePhone = parseHomePhoneFromComprehensive(comprehensiveAssessmentList);
        }
        patientDto.setCellPhone(cellPhone);
        patientDto.setWorkPhone(workPhone);
        patientDto.setHomePhone(homePhone);
    }

    private void populateSpecialtyPhysicians(PatientDto patientDto, List<ComprehensiveAssessmentBean> comprehensiveAssessmentList, List<ContactWithRole> contacts){
        List<SpecialtyPhysicianDto> specialtyPhysicians = new ArrayList<>();

        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList){
            if (StringUtils.isNotBlank(comprehensiveAssessment.getSpecialtyFirstName())){
                SpecialtyPhysicianDto specialtyPhysician1 = new SpecialtyPhysicianDto();
                specialtyPhysician1.setName(comprehensiveAssessment.getSpecialtyFirstName() + " " + comprehensiveAssessment.getSpecialtyLastName());
                List<String> addressDetails = asList(comprehensiveAssessment.getSpecialtyStreet(),
                        comprehensiveAssessment.getSpecialtyCity(),
                        comprehensiveAssessment.getSpecialtyState(),
                        comprehensiveAssessment.getSpecialtyZipCode());
                specialtyPhysician1.setAddress(getDisplayAddress(addressDetails));
                specialtyPhysician1.setPhone(comprehensiveAssessment.getSpecialtyPhone());
                specialtyPhysician1.setRole(comprehensiveAssessment.getSpecialtyRole());
                specialtyPhysicians.add(specialtyPhysician1);
                break;
            }
        }

        for (ContactWithRole contact : contacts) {
            if (!PRIMARY_CARE_PHYSICIAN_ROLE.equals(contact.getRole()) && isNotEmpty(contact.getPersons())) {
                for (Person person : contact.getPersons()) {
                    if (person != null && isNotEmpty(person.getNames())) {
                        SpecialtyPhysicianDto medProfessional = new SpecialtyPhysicianDto();
                        medProfessional.setName(generateName(person));
                        medProfessional.setRole(contact.getRole());
                        if (isNotEmpty(person.getTelecoms())){
                            medProfessional.setPhone(getPersonTelecomValue(person, PersonTelecomCode.WP));
                        }
                        if(isNotEmpty(person.getAddresses())){
                            medProfessional.setAddress(person.getAddresses().get(0).getFullAddress());
                        }
                        if (!specialtyPhysicians.contains(medProfessional)){
                            specialtyPhysicians.add(medProfessional);
                        }
                    }
                }
            }
        }

        patientDto.setSpecialtyPhysicians(specialtyPhysicians);
    }

    private String generateName(Person person){
        return person.getNames().get(0).getGiven() + " " + person.getNames().get(0).getFamily();
    }

    private void populatePrimaryCarePhysicianInfo(PatientDto patientDto, CareCoordinationResident resident, List<ComprehensiveAssessmentBean> comprehensiveAssessmentList, List<ContactWithRole> contacts) {
        //String pcpName = resident.getPrimaryCarePhysician();
        String pcpName = null;
        List<PrimaryCarePhysicianDto> primaryCarePhysicians = new ArrayList<>();
        if (StringUtils.isNotBlank(pcpName)) {
            PrimaryCarePhysicianDto primaryCarePhysician1 = new PrimaryCarePhysicianDto();
            primaryCarePhysician1.setPrimaryCarePhysician(pcpName);
            primaryCarePhysicians.add(primaryCarePhysician1);
        }

        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList) {
            if (StringUtils.isNotBlank(comprehensiveAssessment.getPrimaryCarePhysicianFirstName())) {
                PrimaryCarePhysicianDto primaryCarePhysician2 = new PrimaryCarePhysicianDto();
                primaryCarePhysician2.setPrimaryCarePhysician(comprehensiveAssessment.getPrimaryCarePhysicianFirstName() + " " + comprehensiveAssessment.getPrimaryCarePhysicianLastName());
                primaryCarePhysician2.setPrimaryCarePhysicianPhone(comprehensiveAssessment.getPrimaryCarePhysicianPhone());
                List<String> addressDetails = asList(comprehensiveAssessment.getPcpStreet(),
                        comprehensiveAssessment.getPcpCity(),
                        comprehensiveAssessment.getPcpState(),
                        comprehensiveAssessment.getPcpZipCode());
                primaryCarePhysician2.setPrimaryCarePhysicianAddress(getDisplayAddress(addressDetails));
                primaryCarePhysicians.add(primaryCarePhysician2);
                break;
            }
        }

        for (ContactWithRole contact : contacts) {
            if (PRIMARY_CARE_PHYSICIAN_ROLE.equals(contact.getRole()) && isNotEmpty(contact.getPersons())) {
                for (Person person : contact.getPersons()) {
                    if (person != null) {
                        PrimaryCarePhysicianDto medProfessional = new PrimaryCarePhysicianDto();
                        medProfessional.setPrimaryCarePhysician(person.getNames().get(0).getGiven() + " " + person.getNames().get(0).getFamily());
                        if (isNotEmpty(person.getTelecoms())){
                            medProfessional.setPrimaryCarePhysicianPhone(getPersonTelecomValue(person, PersonTelecomCode.WP));
                        }
                        if(isNotEmpty(person.getAddresses())){
                            medProfessional.setPrimaryCarePhysicianAddress(person.getAddresses().get(0).getFullAddress());
                        }
                        primaryCarePhysicians.add(medProfessional);
                    }
                }
            }
        }

        patientDto.setPrimaryCarePhysicians(primaryCarePhysicians);
    }

    private String getDisplayHealthPlan(BillingInfo billing){
        StringBuilder sb = new StringBuilder();

        if (isNotBlank(billing.getInsuranceName())){
            sb.append("Network: ");
            sb.append(billing.getInsuranceName());
            appendDelimiter (isNotBlank(billing.getPlanName()) || isNotBlank(billing.getPolicyNumber()) || isNotBlank(billing.getGroupNumber()), sb);
        }
        if (isNotBlank(billing.getPlanName())){
            sb.append(sb.length() > 0 ? "plan: " : "Plan: ");
            sb.append(billing.getPlanName());
            appendDelimiter(isNotBlank(billing.getPolicyNumber()) || isNotBlank(billing.getGroupNumber()), sb);
        }
        if (isNotBlank(billing.getPolicyNumber())){
            sb.append(sb.length() > 0 ? "policy#: " : "Policy#: ");
            sb.append(billing.getPolicyNumber());
            appendDelimiter(isNotBlank(billing.getGroupNumber()), sb);
        }
        if (isNotBlank(billing.getGroupNumber())){
            sb.append(sb.length() > 0 ? "group#: " : "Group#: ");
            sb.append(billing.getGroupNumber());
            sb.append("; ");
        }
        return sb.toString();
    }

    private void appendDelimiter(boolean condition, StringBuilder sb){
        if (condition){
            sb.append(", ");
        } else {
            sb.append("; ");
        }
    }

    private String getDisplayAddress(List<String> addressDetails){
        List<String> notBlankAddressDetails = new ArrayList<>();
        for (String addressDetail : addressDetails ){
           if (StringUtils.isNotBlank(addressDetail)){
               notBlankAddressDetails.add(addressDetail);
           }
        }

        int i = 0;
        if (isNotEmpty(notBlankAddressDetails)){
            StringBuilder sb = new StringBuilder();
            for (String addressDetail : notBlankAddressDetails) {
                String delimiter = i < notBlankAddressDetails.size() - 1 ? ", " : "; ";
                sb.append( addressDetail + delimiter);
                i++;
            }
            return sb.toString();
        }
        return null;
    }

    private String parseCellPhoneFromComprehensive(List<ComprehensiveAssessmentBean> comprehensiveAssessmentList){
        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList){
            if (comprehensiveAssessment.getCellPhoneNumber() != null){
                return comprehensiveAssessment.getCellPhoneNumber();
            }
        }
        return null;
    }

    private String parseWorkPhoneFromComprehensive(List<ComprehensiveAssessmentBean> comprehensiveAssessmentList){
        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList){
            if (comprehensiveAssessment.getWorkPhoneNumber() != null){
                return comprehensiveAssessment.getWorkPhoneNumber();
            }
        }
        return null;
    }

    private String parseHomePhoneFromComprehensive(List<ComprehensiveAssessmentBean> comprehensiveAssessmentList){
        for (ComprehensiveAssessmentBean comprehensiveAssessment : comprehensiveAssessmentList){
            if (comprehensiveAssessment.getWorkPhoneNumber() != null){
                return comprehensiveAssessment.getHomePhoneNumber();
            }
        }
        return null;
    }

    @Override
    public PatientDto getEditPatientDto(Long patientId) {
        return convert(careCoordinationResidentService.get(patientId), true, true, false);
    }

    @Override
    public PatientDto getTransportationPatientDto(Long patientId) {
        CareCoordinationResident resident = careCoordinationResidentService.get(patientId);
        PatientDto patientDto =  convert(resident,false,false,false);
        Organization organization = resident.getFacility();
        if (StringUtils.isBlank(patientDto.getPhone()) && organization.getTelecom()!=null) {
            patientDto.setPhone(organization.getTelecom().getValue());
        }
        if (StringUtils.isBlank(patientDto.getEmail()) && StringUtils.isNotBlank(organization.getEmail())) {
            patientDto.setPhone(organization.getEmail());
        }

        if (patientDto.getAddress() == null) {
            OrganizationAddress organizationAddress;
            if (!CollectionUtils.isEmpty(organization.getAddresses())) {
                organizationAddress = organization.getAddresses().get(0);
                AddressDto addressDto = new AddressDto();
                if (!StringUtils.isEmpty(organizationAddress.getState())) {
                    addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(organizationAddress.getState())));
                }
                addressDto.setCity(organizationAddress.getCity());
                addressDto.setStreet(organizationAddress.getStreetAddress());
                addressDto.setZip(organizationAddress.getPostalCode());
                patientDto.setAddress(addressDto);
            }
        }
        return patientDto;

//        AddressDto addressDto = patientDto.getAddress();
//        if (organizationAddress!=null) {
//            if (StringUtils.isBlank(addressDto.getStreet())){
//                addressDto.setStreet(organizationAddress.getStreetAddress());
//            }
//            if (StringUtils.isBlank(addressDto.getCity())){
//                addressDto.setCity(organizationAddress.getCity());
//            }
//            if (StringUtils.isBlank(addressDto.getState())){
//                addressDto.setState(organizationAddress.getState());
//            }
//            if (StringUtils.isBlank(addressDto.getStreet())){
//                addressDto.setStreet(organizationAddress.getStreetAddress());
//            }
    }

    @Override
    public Long createOrEditPatient(Long patientId, PatientDto patientDto) {
        Long communityId = patientDto.getCommunityId();
        if (communityId == null) {
            communityId = SecurityUtils.getAuthenticatedUser().getCommunityId();
        }
        CareCoordinationResident resident = careCoordinationResidentService.createOrUpdateResident( communityId, patientId, patientDto);
        return resident.getId();
    }

    @Override
    public Boolean toggleActivation(Long patientId) {
        Long communityId = SecurityUtils.getAuthenticatedUser().getCommunityId();
        return careCoordinationResidentService.toggleResidentActivation(communityId, patientId);
    }

//    public List<PatientListItemDto>  listMergedResidents(long patientId) {
////        List<PatientListItemDto>  mergedResidents = careCoordinationResidentService.listMergedResidents(patientId);
////        List <PatientDto> patientDtos = new ArrayList();
////        for (CareCoordinationResident ccResident:mergedResidents){
////            patientDtos.add(convert(ccResident, false, false));
////        }
////        return patientDtos;
//        return careCoordinationResidentService.listMergedResidents(patientId);
//    }

    private PatientDto convert(CareCoordinationResident resident, boolean showSsn, boolean createEdit, boolean checkEditable) {
        final PatientDto result = new PatientDto();

        result.setId(resident.getId());
        result.setFirstName(resident.getFirstName());
        result.setLastName(resident.getLastName());
        result.setMiddleName(resident.getMiddleName());
        result.setBirthDate(resident.getBirthDate());
        result.setOrganization(resident.getDatabase().getName());
        result.setOrganizationId(resident.getDatabase().getId());
        result.setCommunity(resident.getFacility().getName());
        result.setCommunityId(resident.getFacility().getId());
        if (checkEditable) {
            result.setEditable(careCoordinationResidentService.isResidentEditable(resident));
        }
        result.setActive(resident.getActive());
        result.setHashKey(resident.getHashKey());

        if (resident.getGender() != null) {
            if (createEdit) {
                result.setGender(resident.getGender().getCode());
            }
            else {
                result.setGender(resident.getGender().getDisplayName());
            }

            result.setGenderId(resident.getGender().getId());
        }
        if (showSsn) {
            if (StringUtils.isNotBlank(resident.getSocialSecurity())) {
                result.setSsn(resident.getSocialSecurity());
            }
        } else {
            if (StringUtils.isNotBlank(resident.getSsnLastFourDigits())) {
                result.setSsn("###-##-" + resident.getSsnLastFourDigits());
            }
        }
        if (resident.getMaritalStatus() != null) {
            result.setMaritalStatus(resident.getMaritalStatus().getDisplayName());
            /*if (createEdit) {
                result.setMaritalStatus(resident.getMaritalStatus().getCode());
            } else {
                result.setMaritalStatus(resident.getMaritalStatus().getDisplayName());
            }*/
        }
        if (!CollectionUtils.isEmpty(resident.getPerson().getAddresses())) {
            result.setAddress(toDto(resident.getPerson().getAddresses().get(0)));
        }

        result.setEmail(PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.EMAIL));
        result.setPhone(PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.HP));
        result.setCellPhone(PersonService.getPersonTelecomNormalizedValue(resident.getPerson(), PersonTelecomCode.MC));

        result.setInsuranceId(resident.getInNetworkInsurance() == null ? null : resident.getInNetworkInsurance().getId());
        result.setInsurancePlan(resident.getInsurancePlanName());
        result.setGroupNumber(resident.getGroupNumber());
        result.setMemberNumber(resident.getMemberNumber());
        result.setMedicareNumber(resident.getMedicareNumber());
        result.setMedicaidNumber(resident.getMedicaidNumber());
        result.setRetained(resident.getRetained());
        //result.setPrimaryCarePhysician(resident.getPrimaryCarePhysician());
        result.setIntakeDate(resident.getIntakeDate());
        result.setReferralSource(resident.getReferralSource());
        result.setCurrentPharmacyName(resident.getCurrentPharmacyName());
        result.setStatus(resident.getStatus());

        //TODO only 2 devices supported for phase 1 dose health
        if (!CollectionUtils.isEmpty(resident.getDevices())) {
            result.setDeviceID(resident.getDevices().get(0).getDeviceId());
            if (resident.getDevices().size() > 1) {
                result.setDeviceIDSecondary(resident.getDevices().get(1).getDeviceId());
            }
        }
        result.setDeathDate(resident.getDeathDate());

        return result;
    }

    private AddressDto toDto(PersonAddress address) {
        final AddressDto result = new AddressDto();
        if (!StringUtils.isEmpty(address.getState())) {
            result.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(address.getState())));
        }
        result.setCity(address.getCity());
        result.setStreet(address.getStreetAddress());
        result.setZip(address.getPostalCode());
        return result;
    }


    public  List<PatientDto>  findMatchedPatients(PatientDto dto) {
        MatchResult matchResult = residentMatcherService.findMatchedPatients(dto, true);
        if (matchResult.getMatchResultType().equals(MatchResult.MatchResultType.NO_MATCH)) {
            return null;

        }
        List<PatientDto> patientDtoList = new ArrayList<PatientDto>();
        for (CareCoordinationResident resident:matchResult.getMatchedRecords()) {
            patientDtoList.add (convert(resident,true,false,false));
        }
        for (CareCoordinationResident resident:matchResult.getProbablyMatchedRecords()) {
            patientDtoList.add (convert(resident,true,false,false));
        }
        return patientDtoList;
    }

    @Override
    public List<PatientListItemDto> getMergedResidents(Long patientId, Boolean showDeactivated) {
        return careCoordinationResidentService.getMergedResidents(patientId, showDeactivated);
    }

//    @Override
//    public Long getCommunityId(Long patientId) {
//        return careCoordinationResidentService.getCommunityId(patientId);
//    }

    private void populateAdmittanceHistory(PatientDto patientDto, CareCoordinationResident resident) {
        List<AdmittanceHistory> admittanceHistories = careCoordinationResidentService.getAdmittanceHistoryForPatientInCommunity(patientDto.getId(), patientDto.getCommunityId());
        if (CollectionUtils.isNotEmpty(admittanceHistories) || resident.getAdmitDate() != null || resident.getDischargeDate() != null) {
            TreeSet<Date> admitDates = new TreeSet<>();
            TreeSet<Date> dischargeDates = new TreeSet<>();
            CollectionUtils.addIgnoreNull(admitDates, resident.getAdmitDate());
            CollectionUtils.addIgnoreNull(dischargeDates, resident.getDischargeDate());
            if (CollectionUtils.isNotEmpty(admittanceHistories)) {
                for (AdmittanceHistory admittanceHistory : admittanceHistories) {
                    CollectionUtils.addIgnoreNull(admitDates, admittanceHistory.getAdmitDate());
                    CollectionUtils.addIgnoreNull(dischargeDates, admittanceHistory.getDischargeDate());
                }
            }
            patientDto.setAdmitDates(admitDates.descendingSet());
            patientDto.setDischargeDates(dischargeDates.descendingSet());
        }
    }
}
