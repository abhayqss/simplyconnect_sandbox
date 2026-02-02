package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Person;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.EncounterActivities;
import org.openhealthtools.mdht.uml.cda.consol.EncountersSection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * <h1>Encounters</h1> “This section is used to list and describe any healthcare
 * encounters pertinent to the patient’s current health status or historical
 * health history.” [CCD 3.15]
 *
 * @see Encounter
 * @see Indication
 * @see ProblemObservation
 * @see ServiceDeliveryLocation
 * @see Resident
 * @see CcdCode
 */
@Component("consol.EncountersFactory")
public class EncountersFactory extends OptionalTemplateFactory implements SectionFactory<EncountersSection, Encounter> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.22";

    @Value("${section.encounters.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public EncountersSection buildTemplateInstance(Collection<Encounter> encounters) {
        final EncountersSection section = ConsolFactory.eINSTANCE.createEncountersSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("46240-8", "History of encounters", CodeSystem.LOINC);
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Encounters");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(encounters));

        if (CollectionUtils.isEmpty(encounters)) {
            return section;
        }

        for (Encounter encounter : encounters) {
            final Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setEncounter(buildEncounterActivity(encounter));
            section.getEntries().add(entry);
        }

        return section;
    }

    public org.eclipse.mdht.uml.cda.Encounter buildEncounterActivity(Encounter encounter) {
        final EncounterActivities e = ConsolFactory.eINSTANCE.createEncounterActivities();

        e.setClassCode(ActClass.ENC);
        e.setMoodCode(x_DocumentEncounterMood.EVN);

        e.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.49"));

        e.getIds().add(CcdUtils.getId(encounter.getId()));
        CcdUtils.addConsanaId(e.getIds(), encounter.getConsanaId());

        CD code = CcdUtils.createCDWithDefaultDisplayName(encounter.getEncounterType(),
                encounter.getEncounterTypeText(), "4 2.16.840.1.113883.6.12");
        e.setCode(code);

        if (encounter.getEncounterTypeText() != null) {
            String refId = Encounter.class.getSimpleName() + encounter.getId();
            ED originalText = DatatypesFactory.eINSTANCE.createED();
            TEL ref = DatatypesFactory.eINSTANCE.createTEL();
            ref.setValue("#" + refId);
            originalText.setReference(ref);
            code.setOriginalText(originalText);
        }

        e.setEffectiveTime(CcdUtils.createCenterTime(encounter.getEffectiveTime()));

        if (encounter.getDispositionCode() != null) {
            // 3rd party do not have property "sdtc:dischargeDispositionCode"
        }

        if (CollectionUtils.isNotEmpty(encounter.getEncounterPerformers())) {
            for (EncounterPerformer encounterPerformer : encounter.getEncounterPerformers()) {
                Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
                AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                assignedEntity.getIds().add(CcdUtils.getId(encounterPerformer.getId()));
                // what code system should be used?

                if (encounterPerformer.getPerformer() != null) {

                    Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
                    if (encounterPerformer.getPerformer().getNames() != null) {
                        for (Name name : encounterPerformer.getPerformer().getNames()) {
                            CcdUtils.addConvertedName(assignedPerson.getNames(), name);
                        }
                    } else {
                        assignedPerson.getNames().add(CcdUtils.getNullName());
                    }
                    assignedEntity.setAssignedPerson(assignedPerson);
                } else {
                    assignedEntity.getIds().add(DatatypesFactory.eINSTANCE.createII());
                }

                CE code1 = CcdUtils.createCE(encounterPerformer.getProviderCode());
                assignedEntity.setCode(code1);
                performer2.setAssignedEntity(assignedEntity);
                e.getPerformers().add(performer2);
            }
        }

        if (CollectionUtils.isNotEmpty(encounter.getServiceDeliveryLocations())) {
            for (ServiceDeliveryLocation serviceDeliveryLocation : encounter.getServiceDeliveryLocations()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.LOC);
                participant2
                        .setParticipantRole(SectionEntryFactory.buildServiceDeliveryLocation(serviceDeliveryLocation));
                e.getParticipants().add(participant2);
            }
        }

        if (CollectionUtils.isNotEmpty(encounter.getIndications())) {
            for (Indication indication : encounter.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(SectionEntryFactory.buildIndication(indication));
                e.getEntryRelationships().add(entryRelationship);
            }
        }

        if (encounter.getProblemObservation() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setAct(buildEncounterDiagnosis(encounter.getProblemObservation()));
            e.getEntryRelationships().add(entryRelationship);
        }

        return e;
    }

    public Act buildEncounterDiagnosis(com.scnsoft.eldermark.entity.ProblemObservation problemObservation) {
        Act a = CDAFactory.eINSTANCE.createAct();
        a.setClassCode(x_ActClassDocumentEntryAct.ACT);
        a.setMoodCode(x_DocumentActMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.80");
        a.getTemplateIds().add(templateId);

        CE code = CcdUtils.createCE("29308-4", "Diagnosis", CodeSystem.LOINC);
        a.setCode(code);

        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        entryRelationship.setInversionInd(false);
        entryRelationship.setObservation(
                SectionEntryFactory.buildProblemObservation(problemObservation, Collections.<Class>emptySet()));
        a.getEntryRelationships().add(entryRelationship);

        return a;
    }

    private static String buildSectionText(Collection<Encounter> encounters) {

        if (CollectionUtils.isEmpty(encounters)) {
            return "No known encounters.";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Encounter</th>");
        sectionText.append("<th>Performer</th>"); // We track only codes
        sectionText.append("<th>Location</th>");
        sectionText.append("<th>Date</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Encounter encounter : encounters) {
            sectionText.append("<tr>");

            sectionText.append("<td>");
            if (encounter.getEncounterTypeText() != null) {
                CcdUtils.addReferenceToSectionText(Encounter.class.getSimpleName() + encounter.getId(),
                        encounter.getEncounterTypeText(), sectionText);
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            for (EncounterPerformer encounterPerformer : encounter.getEncounterPerformers()) {
                sectionText.append("<td>");
                if (encounterPerformer != null && encounterPerformer.getProviderCode() != null) {
                    String prefix = "";
                    if (encounterPerformer.getProviderCode().getDisplayName() != null) {
                        sectionText.append(prefix);
                        sectionText.append(StringEscapeUtils.escapeHtml4(encounterPerformer.getProviderCode().getDisplayName()));
                        prefix = "; ";
                    }
                } else {
                    CcdUtils.addEmptyCellToSectionText(sectionText);
                }
                sectionText.append("</td>");
            }

            sectionText.append("<td>");
            if (!CollectionUtils.isEmpty(encounter.getServiceDeliveryLocations())) {
                String prefix = "";
                for (ServiceDeliveryLocation location : encounter.getServiceDeliveryLocations()) {
                    if (location.getName() != null) {
                        sectionText.append(prefix);
                        sectionText.append(StringEscapeUtils.escapeHtml4(location.getName()));
                        prefix = "; ";
                    }
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(sectionText);
            }
            sectionText.append("</td>");

            CcdUtils.addDateCell(encounter.getEffectiveTime(), sectionText);

            sectionText.append("</tr>");
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

}
