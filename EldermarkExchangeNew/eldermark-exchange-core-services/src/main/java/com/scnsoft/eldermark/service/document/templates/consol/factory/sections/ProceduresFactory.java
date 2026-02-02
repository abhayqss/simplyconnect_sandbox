package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;
import com.scnsoft.eldermark.entity.document.ccd.ProcedureActivity;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProceduresSection;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <h1>Procedures</h1> &ldquo;This section defines all interventional, surgical,
 * diagnostic, or therapeutic procedures or treatments pertinent to the patient
 * historically at the time the document is generated. The section may contain
 * all procedures for the period of time being summarized, but should include
 * notable procedures.&rdquo; [CCD 3.14]
 *
 * <h2>Template ID = 2.16.840.1.113883.10.20.22.2.7.1</h2> The common notion of
 * "procedure" is broader than that specified by the HL7 Version 3 Reference
 * Information Model (RIM). Therefore this section contains procedure templates
 * represented with three RIM classes: Act. Observation, and Procedure.
 * <ul>
 * <li><b>Procedure act</b> is for procedures that alter the physical condition
 * of a patient (splenectomy, appendectomy, hip replacement, creation of a
 * gastrostomy, etc.)</li>
 * <li><b>Observation act</b> is for procedures that result in new information
 * about a patient but do not cause physical alteration (diagnostic imaging
 * procedures, EEG, EKG, etc.)</li>
 * <li><b>Act</b> is for all other types of procedures (dressing change,
 * teaching or feeding a patient, providing comfort measures, etc.).</li>
 * </ul>
 *
 * @see Procedure
 * @see ProcedureActivity
 * @see Client
 */
@Component("consol.ProceduresFactory")
public class ProceduresFactory extends RequiredTemplateFactory implements SectionFactory<ProceduresSection, Procedure> {

    // private static final String LEGACY_TABLE = "NWHIN_PROCEDURE";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.7.1";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

    public ProceduresSection buildTemplateInstance(Collection<Procedure> procedures) {
        final ProceduresSection section = ConsolFactory.eINSTANCE.createProceduresSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        CE sectionCode = CcdUtils.createCE("47519-4", "History of Procedures", CodeSystem.LOINC);
        section.setCode(sectionCode);

        ST title = CcdUtils.createST("Procedures");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(procedures));

        if (CollectionUtils.isEmpty(procedures)) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setProcedure(consolSectionEntryFactory.buildNullProcedureActivity());
            section.getEntries().add(entry);
            return section;
        }

        for (Procedure procedure : procedures) {
            Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
            entriesReferredToSectionText.add(ProcedureActivity.class);

            if (!CollectionUtils.isEmpty(procedure.getActivities())) {
                for (ProcedureActivity procedureActivity : procedure.getActivities()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setProcedure(consolSectionEntryFactory.buildProcedureActivity(procedureActivity,
                            entriesReferredToSectionText));
                    section.getEntries().add(entry);
                }
            }
            if (!CollectionUtils.isEmpty(procedure.getActs())) {
                for (ProcedureActivity act : procedure.getActs()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setAct(consolSectionEntryFactory.buildProcedureAct(act, entriesReferredToSectionText));
                    section.getEntries().add(entry);
                }
            }
            if (!CollectionUtils.isEmpty(procedure.getObservations())) {
                for (ProcedureActivity observation : procedure.getObservations()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(
                            consolSectionEntryFactory.buildProcedureObservation(observation, entriesReferredToSectionText));
                    section.getEntries().add(entry);
                }
            }
        }

        return section;
    }

    private static String buildSectionText(Collection<Procedure> procedures) {

        if (CollectionUtils.isEmpty(procedures)) {
            return "No known procedures.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Procedure</th>" +
                "<th>Dates</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Procedure procedure : procedures) {

            if (CollectionUtils.isNotEmpty(procedure.getActs())) {
                for (ProcedureActivity procedureActivity : procedure.getActs()) {
                    addProcedureActivityToSectionText(procedureActivity, body);
                }
            }
            if (CollectionUtils.isNotEmpty(procedure.getObservations())) {
                for (ProcedureActivity procedureActivity : procedure.getObservations()) {
                    addProcedureActivityToSectionText(procedureActivity, body);
                }
            }
            if (CollectionUtils.isNotEmpty(procedure.getActivities())) {
                for (ProcedureActivity procedureActivity : procedure.getActivities()) {
                    addProcedureActivityToSectionText(procedureActivity, body);
                }
            }
        }

        if (body.length() == 0) {
            return "No known procedures.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);
        sectionText.append("</table>");

        return sectionText.toString();
    }

    private static void addProcedureActivityToSectionText(ProcedureActivity procedureActivity,
                                                          StringBuilder body) {
        body.append("<tr>");

        body.append("<td>");
        var text = StringUtils.defaultIfEmpty(procedureActivity.getProcedureTypeText(), CcdUtils.displayName(procedureActivity.getProcedureType()));
        if (StringUtils.isNotEmpty(text)) {
            CcdUtils.addReferenceToSectionText(ProcedureActivity.class.getSimpleName() + procedureActivity.getId(),
                    text, body);
        } else {
            CcdUtils.addEmptyCellToSectionText(body);
        }
        body.append("</td>");

        body.append("<td>");
        if (procedureActivity.getProcedureStarted() != null) {
            body.append(CcdUtils.addDateRangeToSectionText(procedureActivity.getProcedureStarted(),
                    procedureActivity.getProcedureStopped()));
        } else {
            CcdUtils.addEmptyCellToSectionText(body);
        }
        body.append("</td>");

        body.append("</tr>");
    }

}