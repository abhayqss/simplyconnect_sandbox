package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;
import com.scnsoft.eldermark.shared.SearchScope;
import gov.hhs.fha.nhinc.entitypatientdiscovery.EntityPatientDiscovery;
import gov.hhs.fha.nhinc.entitypatientdiscovery.EntityPatientDiscoveryPortType;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.transform.subdisc.HL7PRPA201305Transforms;
import gov.hhs.fha.nhinc.transform.subdisc.HL7PatientTransforms;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class NhinPatientDiscoveryServiceImpl extends NhinAbstractService implements NhinPatientDiscoveryService {

    @Value("${connect.gateway.url.patientdiscovery}")
    private String wsUrl;

    private static final Logger logger = LoggerFactory.getLogger(com.scnsoft.eldermark.services.connect.NhinPatientDiscoveryServiceImpl.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public List<ResidentDto> patientDiscovery(ResidentFilter filter, String assigningAuthorityId, ExchangeUserDetails employeeInfo) {
        final RespondingGatewayPRPAIN201305UV02RequestType request = createPdRequest(filter, assigningAuthorityId);
        request.setAssertion(ConnectUtil.createAssertion(employeeInfo));
        request.setNhinTargetCommunities(ConnectUtil.createNhinTargetCommunitiesType(assigningAuthorityId));

        final EntityPatientDiscovery entityPatientDiscovery = new EntityPatientDiscovery();
        final EntityPatientDiscoveryPortType port = entityPatientDiscovery.getEntityPatientDiscoveryPortSoap();
        final BindingProvider provider = (BindingProvider) port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsUrl);

        applySSLParameters(provider, employeeInfo);

        final RespondingGatewayPRPAIN201306UV02ResponseType response = port.respondingGatewayPRPAIN201305UV02(request);

        return parsePdResponse(response);
    }


    private RespondingGatewayPRPAIN201305UV02RequestType createPdRequest(ResidentFilter filter, String assigningAuthorityId) {
        String ssnStr = filter.getSsn() != null ? filter.getSsn().toString() : null;
        String dateOfBirthStr = filter.getDateOfBirth() != null ? dateFormat.format(filter.getDateOfBirth()) : null;

        JAXBElement<PRPAMT201301UV02Person> person = HL7PatientTransforms.create201301PatientPerson(
                filter.getFirstName(), filter.getLastName(), filter.getGender().getAdministrativeGenderCode(),
                dateOfBirthStr, ssnStr);

        ADExplicit addr = new ADExplicit();
        addr.getContent().add(new JAXBElement<String>(new QName("urn:hl7-org:v3", "streetAddressLine"), String.class, filter.getStreet()));
        addr.getContent().add(new JAXBElement<String>(new QName("urn:hl7-org:v3", "city"), String.class, filter.getCity()));
        addr.getContent().add(new JAXBElement<String>(new QName("urn:hl7-org:v3", "state"), String.class, filter.getState()));
        addr.getContent().add(new JAXBElement<String>(new QName("urn:hl7-org:v3", "postalCode"), String.class, filter.getPostalCode()));
        addr.getContent().add(new JAXBElement<String>(new QName("urn:hl7-org:v3", "county"), String.class, "US"));
        person.getValue().getAddr().add(addr);

        II patId = null;

        PRPAMT201301UV02Patient patient = HL7PatientTransforms.create201301Patient(person, patId);

        PRPAIN201305UV02 msg = HL7PRPA201305Transforms.createPRPA201305(patient, ConnectUtil.EXCHANGE_HCID, assigningAuthorityId, ConnectUtil.EXCHANGE_HCID);

        RespondingGatewayPRPAIN201305UV02RequestType request = new RespondingGatewayPRPAIN201305UV02RequestType();
        request.setPRPAIN201305UV02(msg);

        return request;
    }

    private List<ResidentDto> parsePdResponse(RespondingGatewayPRPAIN201306UV02ResponseType response) {
        List<ResidentDto> nhinResults = new ArrayList<ResidentDto>();
        List<CommunityPRPAIN201306UV02ResponseType> communityResponseList = response.getCommunityResponse();

        if (NullChecker.isNotNullish(communityResponseList)) {
            for (CommunityPRPAIN201306UV02ResponseType communityPRPAIN201306UV02ResponseType : communityResponseList) {
                PRPAIN201306UV02 prpain201306UV02 = communityPRPAIN201306UV02ResponseType.getPRPAIN201306UV02();

                if (prpain201306UV02 != null) {
                    PRPAIN201306UV02MFMIMT700711UV01ControlActProcess controlActProcess = prpain201306UV02.getControlActProcess();

                    if (controlActProcess != null) {
                        List<PRPAIN201306UV02MFMIMT700711UV01Subject1> subjects = controlActProcess.getSubject();
                        if (NullChecker.isNotNullish(subjects)) {
                            for (PRPAIN201306UV02MFMIMT700711UV01Subject1 subject : subjects) {
                                PRPAIN201306UV02MFMIMT700711UV01RegistrationEvent registrationEvent = subject.getRegistrationEvent();

                                if (registrationEvent != null) {
                                    PRPAIN201306UV02MFMIMT700711UV01Subject2 subject2 = registrationEvent.getSubject1();

                                    if (subject2 != null) {
                                        PRPAMT201310UV02Patient prpamt201310UV02Patient = subject2.getPatient();

                                        if (prpamt201310UV02Patient != null) {

                                            JAXBElement<PRPAMT201310UV02Person> patientPerson = prpamt201310UV02Patient.getPatientPerson();

                                            if (patientPerson != null) {
                                                // QName patName = patientPerson.getName();

                                                PRPAMT201310UV02Person prpamt201310UV02Person = patientPerson.getValue();

                                                if (prpamt201310UV02Person != null) {
                                                    ResidentDto residentDto = new ResidentDto();
                                                    residentDto.setSearchScope(SearchScope.NWHIN);

                                                    List<II> idList = prpamt201310UV02Patient.getId();
                                                    if (NullChecker.isNotNullish(idList)) {
                                                        II id = idList.get(0);
                                                        residentDto.setId(id.getExtension());
                                                        residentDto.setDatabaseId(id.getRoot());
                                                        residentDto.setDatabaseName(OIDLookupService.lookup(id.getRoot()));
                                                        residentDto.setResidentNumber(id.getExtension());
                                                    }

                                                    List<PNExplicit> names = prpamt201310UV02Person.getName();

                                                    if (NullChecker.isNotNullish(names)) {
                                                        PNExplicit name = names.get(0);
                                                        List<Serializable> nameContent = name.getContent();
                                                        if (NullChecker.isNotNullish(nameContent)) {
                                                            int i = 0;
                                                            for (Serializable ns : nameContent) {
                                                                JAXBElement jaxbElement = (JAXBElement) ns;

//                                                                if (jaxbElement.getValue() instanceof EnExplicitFamily) {
                                                                if (jaxbElement.getDeclaredType().equals(EnExplicitFamily.class)) {
                                                                    EnExplicitFamily family = (EnExplicitFamily) jaxbElement.getValue();
                                                                    residentDto.setLastName(family.getContent());
//                                                                } else if (jaxbElement.getValue() instanceof EnExplicitGiven) {
                                                                } else if (jaxbElement.getDeclaredType().equals(EnExplicitGiven.class)) {
                                                                    EnExplicitGiven given = (EnExplicitGiven) jaxbElement.getValue();
                                                                    if (i++ == 0)
                                                                        residentDto.setFirstName(given.getContent());
                                                                    else
                                                                        residentDto.setMiddleName(given.getContent());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    List<TELExplicit> telExplicits = prpamt201310UV02Person.getTelecom();

                                                    if (NullChecker.isNotNullish(telExplicits)) {
                                                        TELExplicit telExplicit = telExplicits.get(0);
                                                        residentDto.setPhone(telExplicit.getValue());
                                                    }

                                                    CE genderCode = prpamt201310UV02Person.getAdministrativeGenderCode();
                                                    if (genderCode != null) {
                                                        residentDto.setGender(Gender.getGenderByCode(genderCode.getCode()));
                                                    }

                                                    TSExplicit tsExplicitBt = prpamt201310UV02Person.getBirthTime();
                                                    if (tsExplicitBt != null && NullChecker.isNotNullish(tsExplicitBt.getValue())) {
                                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                                                        try {
                                                            residentDto.setDateOfBirth(formatter.parse(tsExplicitBt.getValue()));
                                                        } catch (ParseException e) {
                                                            logger.error("Wrong Date Of Birth format: " + tsExplicitBt.getValue() + " for Resident: " + residentDto.getOrganizationId() + "." + residentDto.getId());
                                                        }
                                                    }

                                                    List<ADExplicit> adExplicits = prpamt201310UV02Person.getAddr();

                                                    if (NullChecker.isNotNullish(adExplicits)) {
                                                        ADExplicit adExplicit = adExplicits.get(0);
                                                        List<Serializable> conList = adExplicit.getContent();

                                                        if (NullChecker.isNotNullish(conList)) {
                                                            for (Serializable con : conList) {
                                                                JAXBElement jaxbElement = (JAXBElement) con;

                                                                if (jaxbElement.getDeclaredType().equals(AdxpExplicitStreetAddressLine.class)) {
                                                                    AdxpExplicitStreetAddressLine adxpExplicitStreetAddressLine = (AdxpExplicitStreetAddressLine) jaxbElement.getValue();
                                                                    residentDto.setStreetAddress(adxpExplicitStreetAddressLine.getContent());
                                                                } else if (jaxbElement.getDeclaredType().equals(AdxpExplicitCity.class)) {
                                                                    AdxpExplicitCity adxpExplicitCity = (AdxpExplicitCity) jaxbElement.getValue();
                                                                    residentDto.setCity(adxpExplicitCity.getContent());
                                                                } else if (jaxbElement.getDeclaredType().equals(AdxpExplicitState.class)) {
                                                                    AdxpExplicitState adxpExplicitState = (AdxpExplicitState) jaxbElement.getValue();
                                                                    residentDto.setState(adxpExplicitState.getContent());
                                                                } else if (jaxbElement.getDeclaredType().equals(AdxpExplicitPostalCode.class)) {
                                                                    AdxpExplicitPostalCode postalCode = (AdxpExplicitPostalCode) jaxbElement.getValue();
                                                                    residentDto.setPostalCode(postalCode.getContent());
                                                                }

                                                            }

                                                        }
                                                    }


                                                    List<PRPAMT201310UV02OtherIDs> otherIDsList = prpamt201310UV02Person.getAsOtherIDs();
                                                    if (NullChecker.isNotNullish(otherIDsList)) {
                                                        PRPAMT201310UV02OtherIDs prpamt201310UV02OtherIDs = otherIDsList.get(0);
                                                        List<II> ids = prpamt201310UV02OtherIDs.getId();

                                                        if (NullChecker.isNotNullish(ids)) {
                                                            II id = ids.get(0);
                                                            residentDto.setSsn(id.getExtension());
                                                        }
                                                    }

                                                    residentDto.setHashKey("");

                                                    nhinResults.add(residentDto);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return nhinResults;
    }
}

