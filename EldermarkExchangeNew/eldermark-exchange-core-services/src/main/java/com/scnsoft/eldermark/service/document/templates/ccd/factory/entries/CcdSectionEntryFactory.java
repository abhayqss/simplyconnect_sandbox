package com.scnsoft.eldermark.service.document.templates.ccd.factory.entries;

import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.ccd.codes.SectionTypeCode;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ActClassObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemHealthStatusObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ResultOrganizer;

import java.util.Set;

public class CcdSectionEntryFactory extends CdaSectionEntryFactory {

    public static final CcdSectionEntryFactory INSTANCE = new CcdSectionEntryFactory();

    private CcdSectionEntryFactory() {
    }

    @Override
    protected Observation createReactionObservation() {
        return CCDFactory.eINSTANCE.createReactionObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation buildReactionObservation(
            ReactionObservation reactionObservation,
            Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation)
                super.buildReactionObservation(reactionObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createSeverityObservation() {
        return CCDFactory.eINSTANCE.createSeverityObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation buildSeverityObservation(
            SeverityObservation severityObservation, Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation)
                super.buildSeverityObservation(severityObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createProblemObservation() {
        return CCDFactory.eINSTANCE.createProblemObservation();
    }

    @Override
    protected Observation createProblemStatusObservation() {
        return CCDFactory.eINSTANCE.createProblemStatusObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation buildProblemObservation(ProblemObservation problemObservation,
                                                                                           Set<Class<?>> entriesReferredToSectionText) {
        return (org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation)
                super.buildProblemObservation(problemObservation, entriesReferredToSectionText);
    }

    @Override
    protected Observation createAgeObservation() {
        return CCDFactory.eINSTANCE.createAgeObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.ccd.AgeObservation buildAgeObservation(String unit, Integer value) {
        return (org.openhealthtools.mdht.uml.cda.ccd.AgeObservation) super.buildAgeObservation(unit, value);
    }

    @Override
    protected void addHealthStatusObservation(Observation problemObservationCcd, ProblemObservation problemObservation) {
        ProblemHealthStatusObservation healthStatusObservation = CCDFactory.eINSTANCE.createProblemHealthStatusObservation();
        problemObservationCcd.addObservation(healthStatusObservation);
        ((EntryRelationship) healthStatusObservation.eContainer())
                .setTypeCode(x_ActRelationshipEntryRelationship.REFR);

        healthStatusObservation.setClassCode(ActClassObservation.OBS);
        healthStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.5");
        healthStatusObservation.getTemplateIds().add(templateId);

        CD code = CcdUtils.createCD(SectionTypeCode.HEALTH_STATUS_OBSERVATION);
        healthStatusObservation.setCode(code);

        if (problemObservation.getHealthStatusObservationText() != null) {
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
    protected ResultOrganizer createResultOrganizer() {
        return CCDFactory.eINSTANCE.createResultOrganizer();
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
    protected Observation createAllergyObservation() {
        return CCDFactory.eINSTANCE.createAlertObservation();
    }

    @Override
    protected Observation createAllergyStatusObservation() {
        return CCDFactory.eINSTANCE.createAlertStatusObservation();
    }

    @Override
    public org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation buildNullProblemObservation() {
        return (org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation) super.buildNullProblemObservation();
    }
}
