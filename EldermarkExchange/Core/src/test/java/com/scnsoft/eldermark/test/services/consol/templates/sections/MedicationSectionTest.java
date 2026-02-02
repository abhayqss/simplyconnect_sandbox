package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.consol.templates.sections.MedicationsFactory;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.PIVL_TS;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MedicationSectionTest {
    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        // 1. init
        Organization organizationMock = new Organization();

        OrganizationTelecom tel = new OrganizationTelecom();
        tel.setId(1L);
        tel.setValue("TelcomValue");
        tel.setUseCode("HP");

        OrganizationAddress addr = new OrganizationAddress();
        addr.setPostalCode("PostalCode");
        addr.setStreetAddress("StreetAddress");
        addr.setCity("City");
        addr.setState("State");
        addr.setCountry("Country");
        addr.setOrganization(organizationMock);

        List<OrganizationAddress> addrs = new ArrayList<>();
        addrs.add(addr);

        organizationMock.setId(2L);
        organizationMock.setName("ManufactorerName");
        organizationMock.setTelecom(tel);
        organizationMock.setAddresses(addrs);

        MedicationInformation medicationInformationMock = new MedicationInformation();
        medicationInformationMock.setId(3L);
        medicationInformationMock.setProductNameText("ProductNameText");
        CcdCode code = new CcdCode();
        code.setCode("ProductNameCode");
        code.setCodeSystem("ProductNameCodeSystem");
        medicationInformationMock.setProductNameCode(code);
        medicationInformationMock.setManufactorer(organizationMock);

        DrugVehicle drugVehicleMock = new DrugVehicle();
        drugVehicleMock.setId(4L);
        code = new CcdCode();
        code.setCode("DrugVehicleCode");
        code.setCodeSystem("DrugVehicleCodeSystem");
        drugVehicleMock.setCode(code);
        drugVehicleMock.setName("DrugVehicleName");

        List<DrugVehicle> drugVehicleMocks = new ArrayList<>();
        drugVehicleMocks.add(drugVehicleMock);

        Indication indicationMock = new Indication();
        indicationMock.setId(5L);
        code = new CcdCode();
        code.setCode("IndicationCode");
        indicationMock.setCode(code);
        code = new CcdCode();
        code.setCode("IndicationValue");
        indicationMock.setValue(code);
        indicationMock.setTimeLow(new Date());
        indicationMock.setTimeHigh(new Date());

        List<Indication> indicationMocks = new ArrayList<>();
        indicationMocks.add(indicationMock);

        Instructions instructionsMock = new Instructions();
        instructionsMock.setId(6L);
        code = new CcdCode();
        code.setCode("InstructionsCode");
        instructionsMock.setCode(code);
        instructionsMock.setText("InstructionsText");

        MedicationPrecondition medicationPreconditionMock = new MedicationPrecondition();
        medicationPreconditionMock.setId(7L);
        code = new CcdCode();
        code.setCode("MedicationPreconditionValue");
        medicationPreconditionMock.setValue(code);
        medicationPreconditionMock.setText("MedicationPreconditionText");

        List<MedicationPrecondition> medicationPreconditionMocks = new ArrayList<>();
        medicationPreconditionMocks.add(medicationPreconditionMock);

        ReactionObservation reactionObservationMock = new ReactionObservation();

        ImmunizationMedicationInformation immunizationMock = new ImmunizationMedicationInformation();
        immunizationMock.setId(8L);
        code = new CcdCode();
        code.setCode("ImmunizationCode");
        immunizationMock.setCode(code);
        immunizationMock.setText("ImmunizationText");
        immunizationMock.setManufactorer(organizationMock);
        immunizationMock.setLotNumberText("LotNumberText");

        Author authorMock = new Author();
        authorMock.setId(9L);
        authorMock.setTime(new Date());
        authorMock.setOrganization(organizationMock);

        MedicationSupplyOrder supplyMock = new MedicationSupplyOrder();
        supplyMock.setId(10L);
        supplyMock.setAuthor(authorMock);
        supplyMock.setStatusCode("SupplyStatusCode");
        supplyMock.setImmunizationMedicationInformation(immunizationMock);
        supplyMock.setInstructions(instructionsMock);
        supplyMock.setQuantity(123);
        supplyMock.setTimeHigh(new Date());
        supplyMock.setMedicationInformation(medicationInformationMock);
        supplyMock.setRepeatNumber(45);

        MedicationDispense dispenseMock = new MedicationDispense();
        dispenseMock.setId(11L);
        dispenseMock.setProvider(organizationMock);
        dispenseMock.setMedicationInformation(medicationInformationMock);
        dispenseMock.setPrescriptionNumber("PrescriptionNumber");
        dispenseMock.setStatusCode("DispenseStatusCode");
        dispenseMock.setDispenseDateLow(new Date());
        dispenseMock.setDispenseDateHigh(new Date());
        dispenseMock.setFillNumber(8);
        dispenseMock.setMedicationSupplyOrder(supplyMock);
        dispenseMock.setQuantity(77);
        dispenseMock.setImmunizationMedicationInformation(immunizationMock);

        List<MedicationDispense> dispenseMocks = new ArrayList<>();
        dispenseMocks.add(dispenseMock);

        Medication medicationMock = new Medication();
        medicationMock.setId(12L);
        code = new CcdCode();
        code.setCode("DeliveryMethod");
        medicationMock.setDeliveryMethod(code);
        medicationMock.setFreeTextSig("FreeTextSig");
        medicationMock.setStatusCode("MedicationStatus");
        medicationMock.setMedicationStarted(new Date());
        medicationMock.setMedicationStopped(new Date());
        medicationMock.setAdministrationTimingPeriod(4);
        medicationMock.setAdministrationTimingUnit("AdministrationTimingUnit");
        code = new CcdCode();
        code.setCode("AdministrationUnitCode");
        medicationMock.setAdministrationUnitCode(code);
        medicationMock.setRepeatNumber(111);
        medicationMock.setRepeatNumberMood("RepeatNumberMood");
        code = new CcdCode();
        code.setCode("route");
        medicationMock.setRoute(code);
        code = new CcdCode();
        code.setCode("site");
        medicationMock.setSite(code);
        medicationMock.setDoseUnits("DoseUnits");
        medicationMock.setDoseQuantity(3);
        medicationMock.setRateQuantity(4);
        medicationMock.setRateUnits("RateUnits");
        medicationMock.setMedicationInformation(medicationInformationMock);
        medicationMock.setMedicationSupplyOrder(supplyMock);
        medicationMock.setInstructions(instructionsMock);
        medicationMock.setReactionObservation(reactionObservationMock);
        medicationMock.setMedicationDispenses(dispenseMocks);
        medicationMock.setPreconditions(medicationPreconditionMocks);
        medicationMock.setIndications(indicationMocks);
        medicationMock.setDrugVehicles(drugVehicleMocks);

        List<Medication> medicationMocks = new ArrayList<>();
        medicationMocks.add(medicationMock);

        /*
        daoMock = EasyMock.createMock(MedicationDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(medicationMocks));
        EasyMock.replay(daoMock);*/

        MedicationsFactory medicationsFactory = new MedicationsFactory();
        // dependency on DAO is not required for parsing anymore
        //medicationsFactory.setMedicationDao(daoMock);

        // 2. test
        final MedicationsSection section = medicationsFactory.buildTemplateInstance(medicationMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);
        assertEquals("2.16.840.1.113883.10.20.22.2.1.1", section.getTemplateIds().get(0).getRoot());
        assertEquals("10160-0", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        List<SubstanceAdministration> substanceAdministrations = section.getSubstanceAdministrations();
        assertNotNull(substanceAdministrations);

        SubstanceAdministration substanceAdministration = substanceAdministrations.get(0);
        assertEquals("2.16.840.1.113883.10.20.22.4.16", substanceAdministration.getTemplateIds().get(0).getRoot());
        assertEquals(medicationMock.getId().toString(), substanceAdministration.getIds().get(0).getExtension());
        assertEquals(medicationMock.getDeliveryMethod().getCode(), substanceAdministration.getCode().getCode());
        assertEquals(medicationMock.getStatusCode(), substanceAdministration.getStatusCode().getCode());
        assertEquals("#" + Medication.class.getSimpleName() + medicationMock.getId(), substanceAdministration.getText().getReference().getValue());
        assertEquals(CcdUtils.formatSimpleDate(medicationMock.getMedicationStarted()), ((IVL_TS) substanceAdministration.getEffectiveTimes().get(0)).getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(medicationMock.getMedicationStopped()), ((IVL_TS) substanceAdministration.getEffectiveTimes().get(0)).getHigh().getValue());
        assertEquals(BigDecimal.valueOf(medicationMock.getAdministrationTimingPeriod()), ((PIVL_TS) substanceAdministration.getEffectiveTimes().get(1)).getPeriod().getValue());
        assertEquals(medicationMock.getAdministrationTimingUnit(), ((PIVL_TS) substanceAdministration.getEffectiveTimes().get(1)).getPeriod().getUnit());
        assertEquals(BigInteger.valueOf(medicationMock.getRepeatNumber()), substanceAdministration.getRepeatNumber().getValue());
        assertEquals(medicationMock.getDoseQuantity().toString(), substanceAdministration.getDoseQuantity().getValue().toString());
        assertEquals(medicationMock.getDoseUnits(), substanceAdministration.getDoseQuantity().getUnit());
        assertEquals(medicationMock.getRoute().getCode(), substanceAdministration.getRouteCode().getCode());
        assertEquals(medicationMock.getRoute().getCodeSystem(), substanceAdministration.getRouteCode().getCodeSystem());
        assertEquals(medicationMock.getSite().getCode(), substanceAdministration.getApproachSiteCodes().get(0).getCode());
        assertEquals(medicationMock.getSite().getCodeSystem(), substanceAdministration.getApproachSiteCodes().get(0).getCodeSystem());
        assertEquals(medicationMock.getRateQuantity().toString(), substanceAdministration.getRateQuantity().getValue().toString());
        assertEquals(medicationMock.getRateUnits(), substanceAdministration.getRateQuantity().getUnit());
        assertEquals(medicationMock.getAdministrationUnitCode().getCode(), substanceAdministration.getAdministrationUnitCode().getCode());
        assertEquals(medicationMock.getAdministrationUnitCode().getCodeSystem(), substanceAdministration.getAdministrationUnitCode().getCodeSystem());

        // test MedicationInformation
        Consumable consumable = substanceAdministration.getConsumable();
        assertNotNull(consumable);
        ManufacturedProduct manufacturedProduct = consumable.getManufacturedProduct();
        assertNotNull(manufacturedProduct);

        assertEquals("MANU", manufacturedProduct.getClassCode().getName());
        assertEquals("2.16.840.1.113883.10.20.22.4.23", manufacturedProduct.getTemplateIds().get(0).getRoot());
        assertEquals(medicationInformationMock.getId().toString(), manufacturedProduct.getIds().get(0).getExtension());
        assertEquals(medicationInformationMock.getProductNameCode().getCode(), manufacturedProduct.getManufacturedMaterial().getCode().getCode());
        assertEquals(medicationInformationMock.getProductNameCode().getCodeSystem(), manufacturedProduct.getManufacturedMaterial().getCode().getCodeSystem());
        assertEquals("#" + MedicationInformation.class.getSimpleName() + medicationInformationMock.getId(), manufacturedProduct.getManufacturedMaterial().getCode().getOriginalText().getReference().getValue());
//        assertTrue(section.getText().getText().contains(medicationInformationMock.getProductNameText()));

        org.eclipse.mdht.uml.cda.Organization organizationCcd = manufacturedProduct.getManufacturerOrganization();
        assertNotNull(organizationCcd);
        assertEquals(organizationMock.getId().toString(), organizationCcd.getIds().get(0).getExtension());
        assertEquals(organizationMock.getName(), organizationCcd.getNames().get(0).getText());

        // test DrugVehicle
        ParticipantRole participantRole = substanceAdministration.getParticipants().get(0).getParticipantRole();
        assertNotNull(participantRole);
        assertEquals("CSM", substanceAdministration.getParticipants().get(0).getTypeCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.24", participantRole.getTemplateIds().get(0).getRoot());
        assertEquals("412307009", participantRole.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.96", participantRole.getCode().getCodeSystem());
        assertEquals("MMAT", participantRole.getPlayingEntity().getClassCode().toString());
        assertEquals(drugVehicleMock.getCode().getCode(), participantRole.getPlayingEntity().getCode().getCode());
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
        assertEquals(BigInteger.valueOf(dispenseMock.getQuantity()), supply.getQuantity().getValue().toBigInteger());
        assertNotNull(supply.getProduct());
        assertNotNull(supply.getEntryRelationships());
        assertEquals("REFR", supply.getEntryRelationships().get(0).getTypeCode().getName());

        // test Precondition
        Precondition precondition = substanceAdministration.getPreconditions().get(0);
        assertNotNull(precondition);
        assertEquals("2.16.840.1.113883.10.20.22.4.25", precondition.getTemplateIds().get(0).getRoot());
        assertEquals(medicationPreconditionMock.getText(), precondition.getCriterion().getText().getText());
        assertEquals(medicationPreconditionMock.getValue().getCode(), ((CD) precondition.getCriterion().getValue()).getCode());

        // test Reaction Observation
        observation = substanceAdministration.getEntryRelationships().get(4).getObservation();
        assertEquals("CAUS", substanceAdministration.getEntryRelationships().get(4).getTypeCode().getName());
        assertNotNull(observation);
    }
}