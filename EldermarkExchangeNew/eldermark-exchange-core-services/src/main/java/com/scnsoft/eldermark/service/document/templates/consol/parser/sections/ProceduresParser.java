package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;
import com.scnsoft.eldermark.entity.document.ccd.ProcedureActivity;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ProcedureActivityFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.Entry;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.cda.consol.ProceduresSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <h1>Procedures</h1> “This section defines all interventional, surgical,
 * diagnostic, or therapeutic procedures or treatments pertinent to the patient
 * historically at the time the document is generated. The section may contain
 * all procedures for the period of time being summarized, but should include
 * notable procedures.” [CCD 3.14]
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
@Component("consol.ProceduresParser")
public class ProceduresParser extends AbstractParsableSection<Entry, ProceduresSection, Procedure>
        implements ParsableSection<ProceduresSection, Procedure> {
    private static final String LEGACY_TABLE = "NWHIN_PROCEDURE";

    private final ProcedureActivityFactory procedureActivityFactory;

    @Autowired
    public ProceduresParser(ProcedureActivityFactory procedureActivityFactory) {
        this.procedureActivityFactory = procedureActivityFactory;
    }

    @Override
    public boolean isEntryIgnored(final Entry entry) {
        if (entry.getProcedure() == null ||
                entry.getProcedure().getCode() == null ||
                NullFlavor.UNK.equals(entry.getProcedure().getCode().getNullFlavor())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSectionIgnored(ProceduresSection proceduresSection) {
        return !CcdParseUtils.hasContent(proceduresSection) || CollectionUtils.isEmpty(proceduresSection.getEntries());
    }

    @Override
    public List<Procedure> doParseSection(Client resident, ProceduresSection proceduresSection) {
        Objects.requireNonNull(resident);

        // TODO not working as intended
        // Map for merging related entries into procedures
        final Map<String, Procedure> procedureCache = new LinkedHashMap<>();
        for (Entry ccdProcedureEntry : proceduresSection.getEntries()) {
            if (isEntryIgnored(ccdProcedureEntry)) {
                continue;
            }
            ProcedureActivity procedureActivity = procedureActivityFactory
                    .parseProcedureActivity(ccdProcedureEntry.getProcedure(), resident, LEGACY_TABLE);
            if (filterProcedureActivity(procedureActivity)) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getProcedure().getIds());
                final Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getProcedure().getIds()));
                procedure.getActivities().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }

            procedureActivity = procedureActivityFactory.parseProcedureAct(ccdProcedureEntry.getAct(), resident,
                    LEGACY_TABLE);
            if (filterProcedureActivity(procedureActivity)) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getAct().getIds());
                final Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getAct().getIds()));
                procedure.getActs().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }

            procedureActivity = procedureActivityFactory.parseProcedureObservation(ccdProcedureEntry.getObservation(),
                    resident, LEGACY_TABLE);
            if (filterProcedureActivity(procedureActivity)) {
                final String extension = CcdParseUtils.getFirstIdExtensionStr(ccdProcedureEntry.getObservation().getIds());
                final Procedure procedure = getOrCreateProcedure(procedureCache, extension, resident);
                procedure.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedureEntry.getObservation().getIds()));
                procedure.getObservations().add(procedureActivity);
                procedureCache.put(extension, procedure);
            }
        }
        return new ArrayList<>(procedureCache.values());
    }

    private boolean filterProcedureActivity(ProcedureActivity procedureActivity) {
        return (procedureActivity != null && StringUtils.isNotEmpty(procedureActivity.getProcedureTypeText()));
    }

    private static Procedure getOrCreateProcedure(Map<String, Procedure> procedureCache, String extension, Client resident) {
        Procedure procedure = procedureCache.get(extension);
        if (extension == null || procedure == null) {
            procedure = new Procedure();
            procedure.setOrganization(resident.getOrganization());
            procedure.setClient(resident);

            procedure.setActivities(new HashSet<>());
            procedure.setActs(new HashSet<>());
            procedure.setObservations(new HashSet<>());
        }

        return procedure;
    }
}
