package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.Procedure;
import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ProceduresSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Procedures</h1>
 * “This section defines all interventional, surgical, diagnostic, or therapeutic procedures or treatments
 * pertinent to the patient historically at the time the document is generated. The section may contain all
 * procedures for the period of time being summarized, but should include notable procedures.” [CCD 3.14]
 *
 * <h2>Template ID = 2.16.840.1.113883.10.20.22.2.7.1</h2>
 * The common notion of "procedure" is broader than that specified by the HL7 Version 3 Reference Information Model (RIM).
 * Therefore this section contains procedure templates represented with three RIM classes: Act. Observation, and Procedure.
 * <ul>
 *      <li><b>Procedure act</b> is for procedures that alter the physical condition of a patient
 *              (splenectomy, appendectomy, hip replacement, creation of a gastrostomy, etc.)</li>
 *      <li><b>Observation act</b> is for procedures that result in new information about a patient but do not cause physical alteration
 *              (diagnostic imaging procedures, EEG, EKG, etc.)</li>
 *      <li><b>Act</b> is for all other types of procedures
 *              (dressing change, teaching or feeding a patient, providing comfort measures, etc.).</li>
 * </ul>
 *
 * @see Procedure
 * @see ProcedureActivity
 * @see Resident
 */
@Component
public class ProceduresFactory extends RequiredTemplateFactory implements ParsableSectionFactory<ProceduresSection, Procedure> {

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final String LEGACY_TABLE = "NWHIN_PROCEDURE";

    public ProceduresSection buildTemplateInstance(Collection<Procedure> procedures) {
        final ProceduresSection section = CCDFactory.eINSTANCE.createProceduresSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.12"));

        CE sectionCode = CcdUtils.createCE("47519-4", "History of Procedures", CodeSystem.LOINC);
        section.setCode(sectionCode);

        ST title = CcdUtils.createST("Procedures");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(procedures));

        if (CollectionUtils.isEmpty(procedures)) {
            Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setProcedure(SectionEntryFactory.buildNullProcedureActivity());
            section.getEntries().add(entry);
            return section;
        }

        for (Procedure procedure : procedures) {
            Set<Class> entriesReferredToSectionText = new HashSet<>();
            entriesReferredToSectionText.add(ProcedureActivity.class);

            if (!CollectionUtils.isEmpty(procedure.getActivities())) {
                for (ProcedureActivity procedureActivity : procedure.getActivities()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setProcedure(SectionEntryFactory.buildProcedureActivity(procedureActivity, entriesReferredToSectionText));
                    section.getEntries().add(entry);
                }
            }
            if (!CollectionUtils.isEmpty(procedure.getActs())) {
                for (ProcedureActivity act : procedure.getActs()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setAct(SectionEntryFactory.buildProcedureAct(act, entriesReferredToSectionText));
                    section.getEntries().add(entry);
                }
            }
            if (!CollectionUtils.isEmpty(procedure.getObservations())) {
                for (ProcedureActivity observation : procedure.getObservations()) {
                    Entry entry = CDAFactory.eINSTANCE.createEntry();
                    entry.setObservation(SectionEntryFactory.buildProcedureObservation(observation, entriesReferredToSectionText));
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

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Procedure</th>");
        sectionText.append("<th>Dates</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Procedure procedure : procedures) {

            if (procedure.getActs() != null) {
                for (ProcedureActivity procedureActivity : procedure.getActs()) {
                    addProcedureActivityToSectionText(procedureActivity, sectionText);
                }
            }
            if (procedure.getObservations() != null) {
                for (ProcedureActivity procedureActivity : procedure.getObservations()) {
                    addProcedureActivityToSectionText(procedureActivity, sectionText);
                }
            }
            if (procedure.getActivities() != null) {
                for (ProcedureActivity procedureActivity : procedure.getActivities()) {
                    addProcedureActivityToSectionText(procedureActivity, sectionText);
                }
            }

        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");
        return sectionText.toString();
    }

    private static void addProcedureActivityToSectionText(ProcedureActivity procedureActivity, StringBuilder sectionText) {
        sectionText.append("<tr>");

        sectionText.append("<td>");
        if (procedureActivity.getProcedureType() != null &&
                (!StringUtils.isEmpty(procedureActivity.getProcedureType().getDisplayName())
                || !StringUtils.isEmpty(procedureActivity.getProcedureTypeText()))) {
//            CcdUtils.addReferenceToSectionText(ProcedureType.class.getSimpleName() + procedureActivity.getProcedureType().getId(),
//                    procedureActivity.getProcedureType().getProcedureTypeText(), sectionText);
            String refId = ProcedureActivity.class.getSimpleName() + procedureActivity.getProcedureType().getId();
            String text = !StringUtils.isEmpty(procedureActivity.getProcedureTypeText()) ?
                    procedureActivity.getProcedureTypeText() : procedureActivity.getProcedureType().getDisplayName();
            if (sectionText.indexOf(refId) == -1)
                sectionText.append(String.format("<content ID=\"%s\">%s</content>", refId, StringEscapeUtils.escapeHtml4(text)));
            else
                sectionText.append(text);

        } else {
            //CcdUtils.addEmptyCellToSectionText(sectionText);
            sectionText.append("--");
        }
        sectionText.append("</td>");

        sectionText.append("<td>");
        if (procedureActivity.getProcedureStarted() != null) {
            sectionText.append(CcdUtils.addDateRangeToSectionText(procedureActivity.getProcedureStarted(), procedureActivity.getProcedureStopped()));
        } else {
            //CcdUtils.addEmptyCellToSectionText(sectionText);
            sectionText.append("--");
        }
        sectionText.append("</td>");

        sectionText.append("</tr>");
    }

    @Override
    public List<Procedure> parseSection(Resident resident, ProceduresSection proceduresSection) {
        if (!CcdParseUtils.hasContent(proceduresSection) || CollectionUtils.isEmpty(proceduresSection.getEntries())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        // TODO check on real data;
        // Map for merging related entries into procedures
        final Map<String, Procedure> procedureCache = new LinkedHashMap<>();
        for (Entry ccdProcedureEntry : proceduresSection.getEntries()) {
            ProcedureActivity procedureActivity = sectionEntryParseFactory.parseProcedureActivity(
                    ccdProcedureEntry.getProcedure(), resident, LEGACY_TABLE);
            if (procedureActivity != null) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getProcedure().getIds());
                Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getProcedure().getIds()));
                procedure.getActivities().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }

            procedureActivity = sectionEntryParseFactory.parseProcedureAct(ccdProcedureEntry.getAct(), resident,
                    LEGACY_TABLE);
            if (procedureActivity != null) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getAct().getIds());
                Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getAct().getIds()));
                procedure.getActs().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }

            procedureActivity = sectionEntryParseFactory.parseProcedureObservation(ccdProcedureEntry.getObservation(),
                    resident, LEGACY_TABLE);
            if (procedureActivity != null) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getObservation().getIds());
                Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getObservation().getIds()));
                procedure.getObservations().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }
        }

        return new ArrayList<>(procedureCache.values());
    }

    private static Procedure getOrCreateProcedure(Map<String, Procedure> procedureCache, String extension, Resident resident) {
        Procedure procedure = procedureCache.get(extension);
        if (extension == null || procedure == null) {
            procedure = new Procedure();
            procedure.setDatabase(resident.getDatabase());
            procedure.setResident(resident);

            procedure.setActivities(new HashSet<ProcedureActivity>());
            procedure.setActs(new HashSet<ProcedureActivity>());
            procedure.setObservations(new HashSet<ProcedureActivity>());
        }

        return procedure;
    }

}