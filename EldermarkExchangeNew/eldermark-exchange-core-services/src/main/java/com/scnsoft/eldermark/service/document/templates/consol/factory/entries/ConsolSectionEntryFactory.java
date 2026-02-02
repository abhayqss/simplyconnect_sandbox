package com.scnsoft.eldermark.service.document.templates.consol.factory.entries;

import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.ccd.codes.SectionTypeCode;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.Organizer;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ActClassObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ResultOrganizer;

import java.util.Set;

public class ConsolSectionEntryFactory extends CdaSectionEntryFactory {

    public static final ConsolSectionEntryFactory INSTANCE = new ConsolSectionEntryFactory();

    private ConsolSectionEntryFactory() {
    }

    @Override
    protected org.openhealthtools.mdht.uml.cda.consol.ReactionObservation createReactionObservation() {
        return ConsolFactory.eINSTANCE.createReactionObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.ReactionObservation buildReactionObservation(ReactionObservation reactionObservation, Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.consol.ReactionObservation)
                super.buildReactionObservation(reactionObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createSeverityObservation() {
        return ConsolFactory.eINSTANCE.createSeverityObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.SeverityObservation buildSeverityObservation(
            SeverityObservation severityObservation, Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.consol.SeverityObservation)
                super.buildSeverityObservation(severityObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createProblemObservation() {
        return ConsolFactory.eINSTANCE.createProblemObservation();
    }

    @Override
    protected Observation createProblemStatusObservation() {
        return ConsolFactory.eINSTANCE.createProblemStatus();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.ProblemObservation buildProblemObservation(
            ProblemObservation problemObservation, Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.consol.ProblemObservation)
                super.buildProblemObservation(problemObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createAgeObservation() {
        return ConsolFactory.eINSTANCE.createAgeObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.AgeObservation buildAgeObservation(String unit, Integer value) {
        return (org.openhealthtools.mdht.uml.cda.consol.AgeObservation) super.buildAgeObservation(unit, value);
    }

    @Override
    protected void addHealthStatusObservation(Observation problemObservationCcd, ProblemObservation problemObservation) {

        var healthStatusObservation = ConsolFactory.eINSTANCE.createHealthStatusObservation();
        problemObservationCcd.addObservation(healthStatusObservation);
        ((EntryRelationship) healthStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

        healthStatusObservation.setClassCode(ActClassObservation.OBS);
        healthStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.5");
        healthStatusObservation.getTemplateIds().add(templateId);

        CD code = CcdUtils.createCD(SectionTypeCode.HEALTH_STATUS_OBSERVATION);
        healthStatusObservation.setCode(code);

        if (StringUtils.isNotEmpty(problemObservation.getHealthStatusObservationText())) {
            ED text = DatatypesFactory.eINSTANCE.createED();
            text.addText(problemObservation.getHealthStatusObservationText());
            healthStatusObservation.setText(text);
        }

        CS statusCode1 = DatatypesFactory.eINSTANCE.createCS("completed");
        healthStatusObservation.setStatusCode(statusCode1);

        CD value = CcdUtils.createCDWithDefaultDisplayName(problemObservation.getHealthStatusCode(),
                problemObservation.getHealthStatusObservationText(), CodeSystem.SNOMED_CT.getOid());
        healthStatusObservation.getValues().add(value);
    }

    @Override
    protected Organizer createResultOrganizer() {
        return ConsolFactory.eINSTANCE.createResultOrganizer();
        //originally CDAFactory.eINSTANCE.createOrganizer()
    }

    @Override
    public ResultOrganizer buildResultOrganizer(Result result) {
        return (ResultOrganizer) super.buildResultOrganizer(result);
    }

    @Override
    public ResultOrganizer buildNullResultOrganizer() {
        return (ResultOrganizer) super.buildNullResultOrganizer();
    }

    @Override
    protected org.openhealthtools.mdht.uml.cda.consol.AllergyObservation createAllergyObservation() {
        return ConsolFactory.eINSTANCE.createAllergyObservation();
    }

    @Override
    protected org.openhealthtools.mdht.uml.cda.consol.AllergyStatusObservation createAllergyStatusObservation() {
        return ConsolFactory.eINSTANCE.createAllergyStatusObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.AllergyObservation buildAllergyObservation(AllergyObservation allergyObservation) {
        return (org.openhealthtools.mdht.uml.cda.consol.AllergyObservation) super.buildAllergyObservation(allergyObservation);
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.AllergyObservation buildNullAllergyObservation() {
        return (org.openhealthtools.mdht.uml.cda.consol.AllergyObservation) super.buildNullAllergyObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.consol.ProblemObservation buildNullProblemObservation() {
        return (org.openhealthtools.mdht.uml.cda.consol.ProblemObservation) super.buildNullProblemObservation();
    }
}
