package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Performer1;
import org.eclipse.mdht.uml.cda.ServiceEvent;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.IVXB_TS;
import org.eclipse.mdht.uml.hl7.vocab.ActClassRoot;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_ServiceEventPerformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The element {@code ClinicalDocument/documentationOf/serviceEvent} declares the primary “service event” and when it took place,
 * as well as the performers of the activity.
 * <p>
 * It represents the main act being documented, such as a colonoscopy or a cardiac stress study. In a provision of healthcare
 * {@code serviceEvent}, the care providers, PCP, or other longitudinal providers, are recorded within the {@code serviceEvent}.
 * If the document is about a single encounter, the providers associated can be recorded in the {@code componentOf/encompassingEncounter} template.
 *
 * @see DocumentationOf
 */
@Component
public class DocumentationOfFactoryImpl extends RequiredTemplateFactory implements DocumentationOfFactory {

    @Autowired
    private PersonFactory personFactory;

    @Override
    public DocumentationOf generateDefault(Resident resident) {
        checkNotNull(resident);

        final DocumentationOf documentationOf = new DocumentationOf();
        documentationOf.setEffectiveTimeLow(resident.getAdmitDate());
        documentationOf.setEffectiveTimeHigh(resident.getDischargeDate());

        return documentationOf;
    }

    @Override
    public List<org.eclipse.mdht.uml.cda.DocumentationOf> buildTemplateInstance(Collection<DocumentationOf> documentationOfList) {
        final List<org.eclipse.mdht.uml.cda.DocumentationOf> ccdDocumentationOfs = new ArrayList<>();

        for (com.scnsoft.eldermark.entity.DocumentationOf documentationOf : documentationOfList) {
            org.eclipse.mdht.uml.cda.DocumentationOf ccdDocumentationOf = CDAFactory.eINSTANCE.createDocumentationOf();

            ServiceEvent serviceEvent = CDAFactory.eINSTANCE.createServiceEvent();
            serviceEvent.setClassCode(ActClassRoot.PCPR);

            Date timeLow = documentationOf.getEffectiveTimeLow();
            Date timeHigh = documentationOf.getEffectiveTimeHigh();
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
            if (timeLow != null) {
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
            } else {
                low.setNullFlavor(NullFlavor.NI);
            }
            effectiveTime.setLow(low);
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            if (timeHigh != null) {
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
            } else {
                high.setNullFlavor(NullFlavor.NI);
            }
            effectiveTime.setHigh(high);
            serviceEvent.setEffectiveTime(effectiveTime);

            if (documentationOf.getPersons() != null) {
                for (Person person : documentationOf.getPersons()) {
                    Performer1 performer = CDAFactory.eINSTANCE.createPerformer1();
                    performer.setTypeCode(x_ServiceEventPerformer.PRF);
                    AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                    II id = DatatypesFactory.eINSTANCE.createII();
                    if (person.getId() != null) {
                        id.setRoot("2.16.840.1.113883.4.6");
                        id.setExtension(person.getId().toString());
                    } else {
                        id.setNullFlavor(NullFlavor.NI);
                    }
                    assignedEntity.getIds().add(id);
                    if (person.getCode() != null) {
                        assignedEntity.setCode(CcdUtils.createCE(person.getCode(),"2.16.840.1.113883.6.101"));
                    }
                    if (person.getAddresses() != null) {
                        for (PersonAddress address : person.getAddresses()) {
                            CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), address);
                        }
                    } else {
                        assignedEntity.getAddrs().add(CcdUtils.getNullAddress());
                    }
                    if (person.getTelecoms() != null) {
                        for (PersonTelecom telecom : person.getTelecoms()) {
                            CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), telecom);
                        }
                    } else {
                        assignedEntity.getTelecoms().add(CcdUtils.getNullTelecom());
                    }
                    org.eclipse.mdht.uml.cda.Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
                    if (person.getNames() != null) {
                        for (Name name : person.getNames()) {
                            CcdUtils.addConvertedName(assignedPerson.getNames(), name);
                        }
                    } else {
                        assignedPerson.getNames().add(CcdUtils.getNullName());
                    }
                    assignedEntity.setAssignedPerson(assignedPerson);
                    performer.setAssignedEntity(assignedEntity);
                    serviceEvent.getPerformers().add(performer);
                }
            }
            ccdDocumentationOf.setServiceEvent(serviceEvent);
            ccdDocumentationOfs.add(ccdDocumentationOf);
        }

        return ccdDocumentationOfs;
    }

    @Override
    public List<DocumentationOf> parseSection(Resident resident, Collection<org.eclipse.mdht.uml.cda.DocumentationOf> documentationOfs) {
        if (CollectionUtils.isEmpty(documentationOfs)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<DocumentationOf> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.DocumentationOf srcDocOf : documentationOfs) {

            ServiceEvent event = srcDocOf.getServiceEvent();
            if (!CcdParseUtils.hasContent(event)) continue;

            final DocumentationOf resultDocOf = new DocumentationOf();
            resultList.add(resultDocOf);
            resultDocOf.setResident(resident);
            resultDocOf.setDatabase(resident.getDatabase());
            resultDocOf.setDatabaseId(resident.getDatabaseId());

            final Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(event.getEffectiveTime());
            if (effectiveTime != null) {
                resultDocOf.setEffectiveTimeHigh(effectiveTime.getFirst());
                resultDocOf.setEffectiveTimeLow(effectiveTime.getSecond());
            }

            final List<Person> resultPersons = new ArrayList<>();
            resultDocOf.setPersons(resultPersons);
            for (Performer1 srcPerformer : event.getPerformers()) {
                AssignedEntity assignedEntity = srcPerformer.getAssignedEntity();
                Person person = personFactory.parse(assignedEntity, resident.getDatabase(), "NWHIN_DOC_OF");
                resultPersons.add(person);
            }
        }
        resident.setDocumentationOfs(resultList);

        return resultList;
    }

}
