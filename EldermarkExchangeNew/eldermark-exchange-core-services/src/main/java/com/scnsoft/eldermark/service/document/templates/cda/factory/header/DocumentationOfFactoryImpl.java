package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.document.ccd.DocumentationOf;
import com.scnsoft.eldermark.service.document.templates.cda.factory.entries.CdaSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
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

    private static CdaSectionEntryFactory cdaSectionEntryFactory = CdaSectionEntryFactory.INSTANCE;

    @Autowired
    private PersonFactory personFactory;

    @Override
    public DocumentationOf generateDefault(Client client) {
        checkNotNull(client);

        final DocumentationOf documentationOf = new DocumentationOf();
        documentationOf.setEffectiveTimeLow(DateTimeUtils.toDate(client.getAdmitDate()));
        documentationOf.setEffectiveTimeHigh(DateTimeUtils.toDate(client.getDischargeDate()));

        return documentationOf;
    }

    @Override
    public List<org.eclipse.mdht.uml.cda.DocumentationOf> buildTemplateInstance(Collection<DocumentationOf> documentationOfList) {
        final List<org.eclipse.mdht.uml.cda.DocumentationOf> ccdDocumentationOfs = new ArrayList<>();

        for (DocumentationOf documentationOf : documentationOfList) {
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

            if (CollectionUtils.isNotEmpty(documentationOf.getPersons())) {
                for (Person person : documentationOf.getPersons()) {
                    Performer1 performer = CDAFactory.eINSTANCE.createPerformer1();
                    performer.setTypeCode(x_ServiceEventPerformer.PRF);
                    AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                    II id;
                    if (person.getId() != null) {
                        //todo not npi
                        id = CcdUtils.getNpiId(person.getId().toString());
                    } else {
                        id = CcdUtils.getNullId();
                    }
                    assignedEntity.getIds().add(id);
                    if (person.getCode() != null) {
                        assignedEntity.setCode(CcdUtils.createCE(person.getCode(), "2.16.840.1.113883.6.101"));
                    }

                    CcdUtils.addConvertedAddresses(person.getAddresses(), assignedEntity.getAddrs(), true);
                    CcdUtils.addConvertedTelecoms(person.getTelecoms(), assignedEntity.getTelecoms(), true);

                    var assignedPerson = cdaSectionEntryFactory.buildPerson(person);
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
    public List<DocumentationOf> parseSection(Client client, Collection<org.eclipse.mdht.uml.cda.DocumentationOf> documentationOfs) {
        if (CollectionUtils.isEmpty(documentationOfs)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<DocumentationOf> resultList = new ArrayList<>();
        for (org.eclipse.mdht.uml.cda.DocumentationOf srcDocOf : documentationOfs) {

            ServiceEvent event = srcDocOf.getServiceEvent();
            if (!CcdParseUtils.hasContent(event)) continue;

            final DocumentationOf resultDocOf = new DocumentationOf();
            resultList.add(resultDocOf);
            resultDocOf.setClient(client);
            resultDocOf.setOrganization(client.getOrganization());
            resultDocOf.setOrganizationId(client.getOrganizationId());

            final Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(event.getEffectiveTime());
            if (effectiveTime != null) {
                resultDocOf.setEffectiveTimeHigh(effectiveTime.getFirst());
                resultDocOf.setEffectiveTimeLow(effectiveTime.getSecond());
            }

            final List<Person> resultPersons = new ArrayList<>();
            resultDocOf.setPersons(resultPersons);
            for (Performer1 srcPerformer : event.getPerformers()) {
                AssignedEntity assignedEntity = srcPerformer.getAssignedEntity();
                Person person = personFactory.parse(assignedEntity, client.getOrganization(), "NWHIN_DOC_OF");
                resultPersons.add(person);
            }
        }
        client.setDocumentationOfs(resultList);

        return resultList;
    }

}
