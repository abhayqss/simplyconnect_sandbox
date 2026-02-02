package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.MedicationSupplyOrder;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.vocab.ActRelationshipType;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.ImmunizationsSection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ImmunizationsFactoryTest {
    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        // 1. init
        Community communityMock = new Community();

        CommunityTelecom tel = new CommunityTelecom();
        tel.setId(1L);
        tel.setValue("TelcomValue");
        tel.setUseCode("HP");

        CommunityAddress addr = new CommunityAddress();
        addr.setPostalCode("PostalCode");
        addr.setStreetAddress("StreetAddress");
        addr.setCity("City");
        addr.setState("State");
        addr.setCountry("Country");
        addr.setCommunity(communityMock);

        List<CommunityAddress> addrs = new ArrayList<>();
        addrs.add(addr);

        communityMock.setId(2L);
        communityMock.setName("ManufactorerName");
        communityMock.setTelecom(tel);
        communityMock.setAddresses(addrs);

        MedicationInformation medicationInformationMock = new MedicationInformation();
        medicationInformationMock.setId(3L);
        medicationInformationMock.setProductNameText("ProductNameText");
        CcdCode ccdCode = new CcdCode();
        ccdCode.setCode("ProductNameCode");
        medicationInformationMock.setProductNameCode(ccdCode);
        medicationInformationMock.setManufactorer(communityMock);

        DrugVehicle drugVehicleMock = new DrugVehicle();
        drugVehicleMock.setId(4L);
        ccdCode = new CcdCode();
        ccdCode.setCode("DrugVehicleCode");
        drugVehicleMock.setCode(ccdCode);
        drugVehicleMock.setName("DrugVehicleName");

        List<DrugVehicle> drugVehicleMocks = new ArrayList<>();
        drugVehicleMocks.add(drugVehicleMock);

        Indication indicationMock = new Indication();
        indicationMock.setId(5L);
        ccdCode = new CcdCode();
        ccdCode.setCode("IndicationCode");
        indicationMock.setCode(ccdCode);
        ccdCode = new CcdCode();
        ccdCode.setCode("IndicationValue");
        indicationMock.setValue(ccdCode);
        indicationMock.setTimeLow(new Date());
        indicationMock.setTimeHigh(new Date());

        List<Indication> indicationMocks = new ArrayList<>();
        indicationMocks.add(indicationMock);

        Instructions instructionsMock = new Instructions();
        instructionsMock.setId(6L);
        ccdCode = new CcdCode();
        ccdCode.setCode("InstructionsCode");
        instructionsMock.setCode(ccdCode);
        instructionsMock.setText("InstructionsText");

        ReactionObservation reactionObservationMock = new ReactionObservation();

        ImmunizationMedicationInformation immunizationMedicationInformationMock = new ImmunizationMedicationInformation();
        immunizationMedicationInformationMock.setId(8L);
        ccdCode = new CcdCode();
        ccdCode.setCode("ImmunizationCode");
        ccdCode.setCodeSystem("2.16.840.1.113883.6.59");
        immunizationMedicationInformationMock.setCode(ccdCode);
        immunizationMedicationInformationMock.setText("ImmunizationText");
        immunizationMedicationInformationMock.setManufactorer(communityMock);
        immunizationMedicationInformationMock.setLotNumberText("LotNumberText");

        Author authorMock = new Author();
        authorMock.setId(9L);
        authorMock.setTime(new Date());
        authorMock.setCommunity(communityMock);

        MedicationSupplyOrder supplyMock = new MedicationSupplyOrder();
        supplyMock.setId(10L);
        supplyMock.setAuthor(authorMock);
        supplyMock.setStatusCode("SupplyStatusCode");
        supplyMock.setImmunizationMedicationInformation(immunizationMedicationInformationMock);
        supplyMock.setInstructions(instructionsMock);
        supplyMock.setQuantity(123);
        supplyMock.setTimeHigh(new Date());
        supplyMock.setMedicationInformation(medicationInformationMock);
        supplyMock.setRepeatNumber(45);

        MedicationDispense dispenseMock = new MedicationDispense();
        dispenseMock.setId(11L);
        dispenseMock.setProvider(communityMock);
        dispenseMock.setMedicationInformation(medicationInformationMock);
        dispenseMock.setPrescriptionNumber("PrescriptionNumber");
        dispenseMock.setStatusCode("DispenseStatusCode");
        dispenseMock.setDispenseDateLow(new Date());
        dispenseMock.setDispenseDateHigh(new Date());
        dispenseMock.setFillNumber(8);
        dispenseMock.setMedicationSupplyOrder(supplyMock);
        dispenseMock.setQuantity(BigDecimal.valueOf(77L));
        dispenseMock.setImmunizationMedicationInformation(immunizationMedicationInformationMock);

        List<MedicationDispense> dispenseMocks = new ArrayList<>();
        dispenseMocks.add(dispenseMock);

        MedicationPrecondition medicationPreconditionMock = new MedicationPrecondition();
        medicationPreconditionMock.setId(7L);
        ccdCode = new CcdCode();
        ccdCode.setCode("MedicationPreconditionValue");
        medicationPreconditionMock.setValue(ccdCode);
        medicationPreconditionMock.setText("MedicationPreconditionText");

        List<MedicationPrecondition> medicationPreconditionMocks = new ArrayList<>();
        medicationPreconditionMocks.add(medicationPreconditionMock);

        ImmunizationRefusalReason immunizationRefusalReasonMock = new ImmunizationRefusalReason();
        immunizationRefusalReasonMock.setId(5L);
        immunizationRefusalReasonMock.setCode(TestUtil.createCcdCodeMock());

        Immunization immunizationMock = new Immunization();
        immunizationMock.setId(12L);
        immunizationMock.setCode(TestUtil.createCcdCodeMock());
        immunizationMock.setMoodCode("EVN");
        immunizationMock.setRefusal(true);
        immunizationMock.setText("Text");
        immunizationMock.setImmunizationStarted(new Date());
        immunizationMock.setImmunizationStopped(new Date());
        immunizationMock.setStatusCode("MedicationStatus");
        immunizationMock.setAdministrationUnitCode(TestUtil.createCcdCodeMock());
        immunizationMock.getAdministrationUnitCode().setCodeSystem("aaaaaaa");
        immunizationMock.setRepeatNumber(111);
        immunizationMock.setRepeatNumberMood("RepeatNumberMood");
        immunizationMock.setRoute(TestUtil.createCcdCodeMock());
        immunizationMock.getRoute().setCodeSystem("aaaaaaa");
        immunizationMock.setSite(TestUtil.createCcdCodeMock());
        immunizationMock.setDoseUnits("DoseUnits");
        immunizationMock.setDoseQuantity(3);
        immunizationMock.setImmunizationMedicationInformation(immunizationMedicationInformationMock);
        immunizationMock.setDrugVehicles(drugVehicleMocks);
        immunizationMock.setInstructions(instructionsMock);
        immunizationMock.setIndications(indicationMocks);
        immunizationMock.setMedicationSupplyOrder(supplyMock);
        immunizationMock.setReactionObservation(reactionObservationMock);
        immunizationMock.setMedicationDispense(dispenseMock);
        immunizationMock.setPreconditions(medicationPreconditionMocks);
        immunizationMock.setImmunizationRefusalReason(immunizationRefusalReasonMock);

        Set<Immunization> immunizationMocks = new HashSet<>();
        immunizationMocks.add(immunizationMock);

        /*
        ImmunizationDao daoMock = EasyMock.createMock(ImmunizationDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(immunizationMocks);
        EasyMock.replay(daoMock);*/

        ImmunizationsFactory immunizationsFactory = new ImmunizationsFactory();
        // dependency on DAO is not required for parsing anymore
        //immunizationsFactory.setImmunizationDao(daoMock);

        // 2. test
        final ImmunizationsSection section = immunizationsFactory.buildTemplateInstance(immunizationMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);
        assertEquals("2.16.840.1.113883.10.20.1.6", section.getTemplateIds().get(0).getRoot());
        assertEquals("11369-6", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        List<SubstanceAdministration> substanceAdministrations = section.getSubstanceAdministrations();
        assertNotNull(substanceAdministrations);

        SubstanceAdministration substanceAdministration = substanceAdministrations.get(0);
        assertEquals("2.16.840.1.113883.10.20.22.4.52", substanceAdministration.getTemplateIds().get(0).getRoot());
        assertEquals("SBADM", substanceAdministration.getClassCode().getName());
        assertEquals(immunizationMock.getMoodCode(), substanceAdministration.getMoodCode().getName());
        assertEquals(immunizationMock.getRefusal(), substanceAdministration.getNegationInd());
        assertEquals(immunizationMock.getId().toString(), substanceAdministration.getIds().get(0).getExtension());
        assertEquals(immunizationMock.getCode().getCode(), substanceAdministration.getCode().getCode());
        assertEquals(immunizationMock.getCode().getCodeSystem(), substanceAdministration.getCode().getCodeSystem());
        assertEquals(immunizationMock.getStatusCode(), substanceAdministration.getStatusCode().getCode());
        assertEquals(immunizationMock.getText(), substanceAdministration.getText().getText());
        assertEquals(CcdUtils.formatSimpleDate(immunizationMock.getImmunizationStarted()), ((IVL_TS) substanceAdministration.getEffectiveTimes().get(0)).getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(immunizationMock.getImmunizationStopped()), ((IVL_TS) substanceAdministration.getEffectiveTimes().get(0)).getHigh().getValue());
        assertEquals(BigInteger.valueOf(immunizationMock.getRepeatNumber()), substanceAdministration.getRepeatNumber().getValue());
        assertEquals(immunizationMock.getDoseQuantity().toString(), substanceAdministration.getDoseQuantity().getValue().toString());
        assertEquals(immunizationMock.getDoseUnits(), substanceAdministration.getDoseQuantity().getUnit());

        CdaTestUtils.assertCodeTranslation(immunizationMock.getRoute(), substanceAdministration.getRouteCode(), false);

        assertEquals(immunizationMock.getSite().getCode(), substanceAdministration.getApproachSiteCodes().get(0).getCode());
        assertEquals(immunizationMock.getSite().getCodeSystem(), substanceAdministration.getApproachSiteCodes().get(0).getCodeSystem());

        CdaTestUtils.assertCodeTranslation(immunizationMock.getAdministrationUnitCode(), substanceAdministration.getAdministrationUnitCode(), false);

        // test MedicationInformation
        Consumable consumable = substanceAdministration.getConsumable();
        assertNotNull(consumable);
        ManufacturedProduct manufacturedProduct = consumable.getManufacturedProduct();
        assertNotNull(manufacturedProduct);

        assertEquals("MANU", manufacturedProduct.getClassCode().getName());
        assertEquals("2.16.840.1.113883.10.20.22.4.54", manufacturedProduct.getTemplateIds().get(0).getRoot());
        assertEquals(immunizationMedicationInformationMock.getId().toString(), manufacturedProduct.getIds().get(0).getExtension());
        assertEquals(immunizationMedicationInformationMock.getCode().getCode(), manufacturedProduct.getManufacturedMaterial().getCode().getCode());
        assertEquals(CodeSystem.RETIRED_CODE_SYSTEM_OIDS.get(immunizationMedicationInformationMock.getCode().getCodeSystem()).getOid(),
                manufacturedProduct.getManufacturedMaterial().getCode().getCodeSystem());
        assertEquals("#" + ImmunizationMedicationInformation.class.getSimpleName() + immunizationMedicationInformationMock.getId(), manufacturedProduct.getManufacturedMaterial().getCode().getOriginalText().getReference().getValue());
//        assertTrue(section.getText().getText().contains(immunizationMedicationInformationMock.getText()));
        assertEquals(immunizationMedicationInformationMock.getLotNumberText(), manufacturedProduct.getManufacturedMaterial().getLotNumberText().getText());

        org.eclipse.mdht.uml.cda.Organization organizationCcd = manufacturedProduct.getManufacturerOrganization();
        assertNotNull(organizationCcd);
        assertEquals(communityMock.getId().toString(), organizationCcd.getIds().get(0).getExtension());
        assertEquals(communityMock.getName(), organizationCcd.getNames().get(0).getText());

        // test DrugVehicle
        ParticipantRole participantRole = substanceAdministration.getParticipants().get(0).getParticipantRole();
        assertNotNull(participantRole);
        assertEquals("CSM", substanceAdministration.getParticipants().get(0).getTypeCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.24", participantRole.getTemplateIds().get(0).getRoot());
        assertEquals("412307009", participantRole.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.96", participantRole.getCode().getCodeSystem());
        assertEquals("MMAT", participantRole.getPlayingEntity().getClassCode().toString());
        assertEquals(drugVehicleMock.getCode().getCode(), participantRole.getPlayingEntity().getCode().getCode());
        assertEquals(drugVehicleMock.getCode().getCodeSystem(), participantRole.getPlayingEntity().getCode().getCodeSystem());
        assertEquals(drugVehicleMock.getName(), participantRole.getPlayingEntity().getNames().get(0).getText());

        // test Indication
        Observation observation = substanceAdministration.getEntryRelationships().get(0).getObservation();
        assertNotNull(observation);
        assertEquals("RSON", substanceAdministration.getEntryRelationships().get(0).getTypeCode().toString());
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.19", observation.getTemplateIds().get(0).getRoot());
        assertEquals(indicationMock.getId().toString(), observation.getIds().get(0).getExtension());
        assertEquals(indicationMock.getCode().getCode(), observation.getCode().getCode());
        assertEquals(indicationMock.getCode().getCodeSystem(), observation.getCode().getCodeSystem());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(indicationMock.getTimeLow()), observation.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(indicationMock.getTimeHigh()), observation.getEffectiveTime().getHigh().getValue());
        assertEquals(indicationMock.getValue().getCode(), ((CD) observation.getValues().get(0)).getCode());
        assertEquals(indicationMock.getValue().getCodeSystem(), ((CD) observation.getValues().get(0)).getCodeSystem());

        // test Instructions
        Act act = substanceAdministration.getEntryRelationships().get(1).getAct();
        assertNotNull(act);
        assertEquals("SUBJ", substanceAdministration.getEntryRelationships().get(1).getTypeCode().toString());
        assertEquals(true, substanceAdministration.getEntryRelationships().get(1).getInversionInd().booleanValue());
        assertEquals("ACT", act.getClassCode().toString());
        assertEquals("INT", act.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.20", act.getTemplateIds().get(0).getRoot());
        assertEquals("completed", act.getStatusCode().getCode());
        assertEquals(instructionsMock.getCode().getCode(), act.getCode().getCode());
        assertEquals(instructionsMock.getCode().getCodeSystem(), act.getCode().getCodeSystem());

        // test Medication Supply
        Supply supply = substanceAdministration.getEntryRelationships().get(2).getSupply();
        assertEquals("REFR", substanceAdministration.getEntryRelationships().get(2).getTypeCode().getName());
        assertNotNull(supply);
        assertEquals("SPLY", supply.getClassCode().toString());
        assertEquals("INT", supply.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.17", supply.getTemplateIds().get(0).getRoot());
        assertEquals(supplyMock.getId().toString(), supply.getIds().get(0).getExtension());
        assertEquals(supplyMock.getStatusCode(), supply.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(supplyMock.getTimeHigh()), ((IVL_TS) supply.getEffectiveTimes().get(0)).getHigh().getValue());
        assertEquals(BigInteger.valueOf(supplyMock.getRepeatNumber()), supply.getRepeatNumber().getValue());
        assertEquals(BigInteger.valueOf(supplyMock.getQuantity()), supply.getQuantity().getValue().toBigInteger());
        assertNotNull(supply.getProduct());
        assertNotNull(supply.getEntryRelationships());
        assertEquals("SUBJ", supply.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(true, supply.getEntryRelationships().get(0).getInversionInd().booleanValue());

        // test Medication Dispense
        supply = substanceAdministration.getEntryRelationships().get(3).getSupply();
        assertEquals("REFR", substanceAdministration.getEntryRelationships().get(3).getTypeCode().getName());
        assertNotNull(supply);
        assertEquals("SPLY", supply.getClassCode().toString());
        assertEquals("EVN", supply.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.18", supply.getTemplateIds().get(0).getRoot());
        assertEquals(dispenseMock.getId().toString(), supply.getIds().get(0).getExtension());
        assertEquals(dispenseMock.getStatusCode(), supply.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(dispenseMock.getDispenseDateLow()), ((IVL_TS) supply.getEffectiveTimes().get(0)).getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(dispenseMock.getDispenseDateHigh()), ((IVL_TS) supply.getEffectiveTimes().get(0)).getHigh().getValue());
        assertEquals(BigInteger.valueOf(dispenseMock.getFillNumber()), supply.getRepeatNumber().getValue());
        assertEquals(dispenseMock.getQuantity(), supply.getQuantity().getValue());
        assertNotNull(supply.getProduct());
        assertNotNull(supply.getEntryRelationships());
        assertEquals("REFR", supply.getEntryRelationships().get(0).getTypeCode().getName());

        // test Precondition
        Precondition precondition = substanceAdministration.getPreconditions().get(0);
        assertNotNull(precondition);
        assertEquals(ActRelationshipType.PRCN, precondition.getTypeCode());
        assertEquals("2.16.840.1.113883.10.20.22.4.25", precondition.getCriterion().getTemplateIds().get(0).getRoot());
        assertEquals(medicationPreconditionMock.getText(), precondition.getCriterion().getText().getText());
        assertEquals(medicationPreconditionMock.getValue().getCode(), ((CD) precondition.getCriterion().getValue()).getCode());

        // test Reaction Observation
        observation = substanceAdministration.getEntryRelationships().get(4).getObservation();
        assertEquals("CAUS", substanceAdministration.getEntryRelationships().get(4).getTypeCode().getName());
        assertNotNull(observation);

        // test Immunization Refusal Reason
        observation = substanceAdministration.getEntryRelationships().get(5).getObservation();
        assertEquals("RSON", substanceAdministration.getEntryRelationships().get(5).getTypeCode().getName());
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.53", observation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals(immunizationRefusalReasonMock.getId().toString(), observation.getIds().get(0).getExtension());
        assertEquals(immunizationRefusalReasonMock.getCode().getCode(), observation.getCode().getCode());
        assertEquals(immunizationRefusalReasonMock.getCode().getCodeSystem(), observation.getCode().getCodeSystem());
    }
}