package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Author;
import org.eclipse.mdht.uml.cda.Organization;
import org.eclipse.mdht.uml.cda.Person;
import org.eclipse.mdht.uml.cda.Procedure;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.AgeObservation;
import org.openhealthtools.mdht.uml.cda.consol.ReactionObservation;
import org.openhealthtools.mdht.uml.cda.consol.SeverityObservation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class SectionEntryFactoryTest {

    private final Random random = new Random();

    @Test
    public void pleaseDontFailWhenNoTests() throws Exception {
        // ultimate unbreakable test
    }

/*
    @Test
    public void testBuildReactionObservation() throws Exception {
        ReactionObservation result = SectionEntryFactory.buildReactionObservation(new com.scnsoft.eldermark.entity.ReactionObservation(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildSeverityObservation() throws Exception {
        SeverityObservation result = SectionEntryFactory.buildSeverityObservation(new com.scnsoft.eldermark.entity.SeverityObservation(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildAuthor() throws Exception {
        Author result = SectionEntryFactory.buildAuthor(new com.scnsoft.eldermark.entity.Author(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildPerson() throws Exception {
        Person result = SectionEntryFactory.buildPerson(new com.scnsoft.eldermark.entity.Person(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildPerformer2() throws Exception {
        Performer2 result = SectionEntryFactory.buildPerformer2(new com.scnsoft.eldermark.entity.Person(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildMedicationActivity() throws Exception {
        SubstanceAdministration result = SectionEntryFactory.buildMedicationActivity(new Medication(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNullMedicationActivity() throws Exception {
        SubstanceAdministration result = SectionEntryFactory.buildNullMedicationActivity();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildMedicationDispense() throws Exception {
        Supply result = SectionEntryFactory.buildMedicationDispense(new MedicationDispense(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildMedicationSupplyOrder() throws Exception {
        Supply result = SectionEntryFactory.buildMedicationSupplyOrder(new MedicationSupplyOrder(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildImmunizationMedicationInformation() throws Exception {
        ManufacturedProduct result = SectionEntryFactory.buildImmunizationMedicationInformation(new ImmunizationMedicationInformation(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNullImmunizationMedicationInformation() throws Exception {
        ManufacturedProduct result = SectionEntryFactory.buildNullImmunizationMedicationInformation();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildMedicationInformation() throws Exception {
        ManufacturedProduct result = SectionEntryFactory.buildMedicationInformation(new MedicationInformation(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNullMedicationInformation() throws Exception {
        ManufacturedProduct result = SectionEntryFactory.buildNullMedicationInformation();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildInstructions() throws Exception {
        Act result = SectionEntryFactory.buildInstructions(new Instructions(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildIndication() throws Exception {
        Observation result = SectionEntryFactory.buildIndication(new Indication(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildDrugVehicle() throws Exception {
        ParticipantRole result = SectionEntryFactory.buildDrugVehicle(new DrugVehicle(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildMedicationPrecondition() throws Exception {
        Precondition result = SectionEntryFactory.buildMedicationPrecondition(new MedicationPrecondition(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildOrganization() throws Exception {
        Organization result = SectionEntryFactory.buildOrganization(new com.scnsoft.eldermark.entity.Organization(Long.valueOf(1)), true);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildProblemObservation() throws Exception {
        org.openhealthtools.mdht.uml.cda.consol.ProblemObservation result = SectionEntryFactory.buildProblemObservation(new ProblemObservation(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNullProblemObservation() throws Exception {
        org.openhealthtools.mdht.uml.cda.consol.ProblemObservation result = SectionEntryFactory.buildNullProblemObservation();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildProcedureActivity() throws Exception {
        Procedure result = SectionEntryFactory.buildProcedureActivity(new ProcedureActivity(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNullProcedureActivity() throws Exception {
        Procedure result = SectionEntryFactory.buildNullProcedureActivity();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildProcedureAct() throws Exception {
        Act result = SectionEntryFactory.buildProcedureAct(new ProcedureActivity(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildProcedureObservation() throws Exception {
        Observation result = SectionEntryFactory.buildProcedureObservation(new ProcedureActivity(Long.valueOf(1)), new HashSet<Class>(Arrays.asList(Class.forName("com.scnsoft.eldermark.services.consol.templates.sections.SectionEntryFactory"))));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildProductInstance() throws Exception {
        ParticipantRole result = SectionEntryFactory.buildProductInstance(new ProductInstance(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildServiceDeliveryLocation() throws Exception {
        ParticipantRole result = SectionEntryFactory.buildServiceDeliveryLocation(new ServiceDeliveryLocation(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildAgeObservation() throws Exception {
        AgeObservation result = SectionEntryFactory.buildAgeObservation("unit", Integer.valueOf(0));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildNonMedicalActivity() throws Exception {
        Supply result = SectionEntryFactory.buildNonMedicalActivity(new MedicalEquipment(Long.valueOf(1)));
        Assert.assertEquals(null, result);
    }

    @Test
    public void testInitReferenceRanges() throws Exception {
        SectionEntryFactory.initReferenceRanges(null, Arrays.<String>asList("String"));
    }*/
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme