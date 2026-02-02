package com.scnsoft.eldermark.service.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v251.group.ORM_O01_ORDER_DETAIL;
import ca.uhn.hl7v2.model.v251.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v251.message.ORM_O01;
import ca.uhn.hl7v2.model.v251.segment.*;
import com.google.common.collect.ImmutableMap;
import com.scnsoft.eldermark.dao.IntegrityInsuranceDao;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderORM;
import com.scnsoft.eldermark.exception.HL7GenerationException;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.util.HL7ConversionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class ApolloORMGeneratorImpl implements ApolloORMGenerator {

    private static final String UNKNOWN_INSURANCE_IN1_3_CODE = "UNKNOWN";

    @Autowired
    private IntegrityInsuranceDao integrityInsuranceDao;

    @Autowired
    private Environment environment;

    private String processingMode;

    private static final Map<String, String> GENDER_MAPPING = ImmutableMap.of(
            "M", "M",
            "F", "F",
            "UN", "U");

    @PostConstruct
    void fillProcessingMode() {
        processingMode = Stream.of(environment.getActiveProfiles())
                .filter("prod"::equals)
                .findFirst()
                .map(p -> "P")
                .orElse("T");
    }

    @Override
    public LabResearchOrderORM generate(LabResearchOrder order) {
        try {
            var orm = new ORM_O01();
            orm.initQuickstart("ORM", "O01", processingMode);

            fillMSH(orm.getMSH(), order);
            fillPatientGroup(orm.getPATIENT(), order);

            var firstOrderGroup = orm.getORDER(0);
            fillORC(firstOrderGroup.getORC(), order);
            fillDG1List(firstOrderGroup.getORDER_DETAIL(), order);

            fillOBRList(orm, order);

            LabResearchOrderORM result = new LabResearchOrderORM();
            result.setOrder(order);
            result.setOrmRaw(orm.encode());

            return result;

        } catch (IOException | HL7Exception e) {
            throw new HL7GenerationException(e);
        }

    }

    private void fillMSH(MSH msh, LabResearchOrder order) throws DataTypeException {
        msh.getMsh3_SendingApplication().getHd1_NamespaceID().setValue("SimplyConnect");
        msh.getMsh4_SendingFacility().getHd1_NamespaceID().setValue("SimplyConnect");

        msh.getMsh5_ReceivingApplication().getHd1_NamespaceID().setValue("APOLLO");
        msh.getMsh6_ReceivingFacility().getHd1_NamespaceID().setValue("TNILK");

        msh.getMsh10_MessageControlID().setValue(order.getId().toString());
    }

    private void fillPatientGroup(ORM_O01_PATIENT ormPatientGroup, LabResearchOrder order) throws DataTypeException {
        fillPID(ormPatientGroup.getPID(), order);

        if (StringUtils.isNotEmpty(order.getNotes())) {
            fillNTE(ormPatientGroup.getNTE(), order);
        }

        fillIN1(ormPatientGroup.getINSURANCE().getIN1(), order);
        fillPV1(ormPatientGroup.getPATIENT_VISIT().getPV1(), order);
    }

    private void fillPID(PID pid, LabResearchOrder order) throws DataTypeException {
        pid.getPid1_SetIDPID().setValue("1");

        var client = order.getClient();

        //plain identifiers are used because Apollo requires length <= 25 - way too small for full HL7 identifier
        pid.getPid2_PatientID().getCx1_IDNumber().setValue(client.getId().toString());
        pid.getPid3_PatientIdentifierList(0).getCx1_IDNumber().setValue(client.getId().toString());

        var pid5 = pid.getPid5_PatientName(0);
        pid5.getXpn1_FamilyName().getFn1_Surname().setValue(client.getLastName());
        pid5.getXpn2_GivenName().setValue(client.getFirstName());
        pid5.getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof().setValue(client.getMiddleName());

        pid.getPid7_DateTimeOfBirth().getTs1_Time().setValue(HL7ConversionUtils.toHL7DTFormat(order.getBirthDate()));
        if (order.getGender() != null) {
            pid.getPid8_AdministrativeSex().setValue(GENDER_MAPPING.get(order.getGender().getCode()));
        }
        if (order.getRace() != null) {
            pid.getPid10_Race(0).getCe1_Identifier().setValue(order.getRace().getCode());
            pid.getPid10_Race(0).getCe2_Text().setValue(order.getRace().getDisplayName());
        }

        var pid11 = pid.getPid11_PatientAddress(0);
        pid11.getXad1_StreetAddress().getSad1_StreetOrMailingAddress().setValue(order.getAddress());
        pid11.getXad3_City().setValue(order.getCity());
        pid11.getXad4_StateOrProvince().setValue(order.getState().getName());
        pid11.getXad5_ZipOrPostalCode().setValue(order.getZipCode());

        pid.getPid13_PhoneNumberHome(0).getXtn1_TelephoneNumber().setValue(order.getPhone());
        pid.getPid19_SSNNumberPatient().setValue(client.getSocialSecurity());
    }

    private void fillNTE(NTE nte, LabResearchOrder order) throws DataTypeException {
        nte.getNte1_SetIDNTE().setValue("1");
        nte.getNte3_Comment(0).setValue(order.getNotes());
    }

    private void fillIN1(IN1 in1, LabResearchOrder order) throws DataTypeException {
        in1.getIn11_SetIDIN1().setValue("1");

        var insuranceId = UNKNOWN_INSURANCE_IN1_3_CODE;
        var insuranceName = order.getInNetworkInsurance();
        if (StringUtils.isNotEmpty(order.getInNetworkInsurance())) {
            var integrityInsurance = integrityInsuranceDao.findFirstByName(insuranceName);
            if (integrityInsurance.isPresent()) {
                insuranceId = integrityInsurance.get().getIntegrityId();
            }
        }

        in1.getIn13_InsuranceCompanyID(0).getCx1_IDNumber().setValue(insuranceId);
        in1.getIn14_InsuranceCompanyName(0).getXon1_OrganizationName().setValue(insuranceName);

        var holderFullName = order.getPolicyHolderName();
        in1.getIn117_InsuredSRelationshipToPatient().getCe1_Identifier().setValue(order.getPolicyHolder().getHL7Code());
        if (StringUtils.isNotBlank(holderFullName)) {
            var in116 = in1.getIn116_NameOfInsured(0);
            String lastName;
            String firstName = null;
            var firstSpace = holderFullName.indexOf(" ");
            if (firstSpace == -1) {
                lastName = holderFullName;
            } else {
                lastName = holderFullName.substring(0, firstSpace);
                firstName = holderFullName.substring(firstSpace + 1);
            }
            in116.getXpn1_FamilyName().getFn1_Surname().setValue(lastName);
            in116.getXpn2_GivenName().setValue(firstName);
        }

        in1.getIn118_InsuredSDateOfBirth().getTs1_Time().setValue(HL7ConversionUtils.toHL7DTFormat(order.getPolicyHolderDOB()));
        in1.getIn136_PolicyNumber().setValue(order.getPolicyNumber());
    }

    private void fillPV1(PV1 pv1, LabResearchOrder order) throws DataTypeException {
        pv1.getPv11_SetIDPV1().setValue("1");

        var assignedPL = pv1.getPv13_AssignedPatientLocation();
        assignedPL.getPl1_PointOfCare().setValue(order.getClinic());
        assignedPL.getPl9_LocationDescription().setValue(order.getClinicAddress());
    }

    private void fillORC(ORC orc, LabResearchOrder order) throws DataTypeException {
        orc.getOrc1_OrderControl().setValue("NW");
        orc.getOrc2_PlacerOrderNumber().getEi1_EntityIdentifier().setValue(order.getRequisitionNumber());
    }

    private void fillDG1List(ORM_O01_ORDER_DETAIL detailGroup, LabResearchOrder order) throws DataTypeException {
        var icd10Codes = order.getIcd10Codes();
        if (CollectionUtils.isNotEmpty(order.getIcd10Codes())) {
            for (int i = 0; i < icd10Codes.size(); i++) {
                var dg1 = detailGroup.getDG1(i);
                dg1.getDg11_SetIDDG1().setValue(String.valueOf(i + 1));
                dg1.getDg13_DiagnosisCodeDG1().getCe1_Identifier().setValue(icd10Codes.get(i));
            }
        }
    }

    private void fillOBRList(ORM_O01 orm, LabResearchOrder order) throws DataTypeException {
        //todo - move to entity and handle requested services on frontend
        var requestedServices = Collections.singletonList(LabResearchOrderService.COVID_CODE); //covid-19 is the only supported service

        for (int i = 0; i < requestedServices.size(); i++) {
            var obr = orm.getORDER(i).getORDER_DETAIL().getOBR();

            obr.getObr1_SetIDOBR().setValue(String.valueOf(i + 1));
            obr.getObr2_PlacerOrderNumber().getEi1_EntityIdentifier().setValue(order.getRequisitionNumber());
            obr.getObr4_UniversalServiceIdentifier().getCe1_Identifier().setValue(requestedServices.get(i));
            obr.getObr7_ObservationDateTime().getTs1_Time().setValue(HL7ConversionUtils.toHL7TSFormatWithSecondsPrecision(order.getSpecimenDate()));

            var obr16 = obr.getObr16_OrderingProvider(0);
            obr16.getXcn2_FamilyName().getFn1_Surname().setValue(order.getProviderLastName());
            obr16.getXcn3_GivenName().setValue(order.getProviderFirstName());
        }
    }
}
