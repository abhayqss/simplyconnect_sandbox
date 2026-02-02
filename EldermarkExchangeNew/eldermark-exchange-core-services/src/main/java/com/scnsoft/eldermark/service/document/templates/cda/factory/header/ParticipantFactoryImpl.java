package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.AssociatedEntity;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.Participant1;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.IVXB_TS;
import org.eclipse.mdht.uml.hl7.vocab.ParticipationType;
import org.eclipse.mdht.uml.hl7.vocab.RoleClassAssociative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Other involved parties
 *
 * @see Participant
 */
@Component
public class ParticipantFactoryImpl extends OptionalTemplateFactory implements ParticipantFactory {

    private static final String ROLE_CODE_CODE_SYSTEM = "2.16.840.1.113883.5.110";

    private final CcdCodeFactory ccdCodeFactory;
    private final CcdCodeDao ccdCodeDao;
    private final PersonFactory personFactory;

    @Value("${header.participants.enabled}")
    private boolean isTemplateIncluded;

    @Autowired
    public ParticipantFactoryImpl(CcdCodeFactory ccdCodeFactory, CcdCodeDao ccdCodeDao, PersonFactory personFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.ccdCodeDao = ccdCodeDao;
        this.personFactory = personFactory;
    }

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public List<Participant1> buildTemplateInstance(Collection<Participant> participants) {
        final List<Participant1> ccdParticipants = new ArrayList<>();

        for (Participant participant : participants) {
            final Participant1 ccdParticipant = CDAFactory.eINSTANCE.createParticipant1();

            ccdParticipant.setTypeCode(ParticipationType.IND);

            final Date timeLow = participant.getTimeLow();
            final Date timeHigh = participant.getTimeHigh();
            if (timeLow != null || timeHigh != null) {
                final IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
                if (timeLow != null) {
                    final IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                    low.setValue(CcdUtils.formatSimpleDate(timeLow));
                    effectiveTime.setLow(low);
                }
                if (timeHigh != null) {
                    final IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                    high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                    effectiveTime.setHigh(high);
                }
                ccdParticipant.setTime(effectiveTime);
            }

            final AssociatedEntity associatedEntity = CDAFactory.eINSTANCE.createAssociatedEntity();
            final Person participantPerson = participant.getPerson();
            if (participant.getPerson() != null) {
                if (participant.getRoleCode() != null) {
                    associatedEntity.setClassCode(RoleClassAssociative.valueOf(participant.getRoleCode().getCode()));
                }
                if (participant.getRelationship() != null) {
                    associatedEntity.setCode(CcdUtils.createCE(participant.getRelationship(),"2.16.840.1.113883.5.111"));
                }
                CcdUtils.addConvertedAddresses(participantPerson.getAddresses(), associatedEntity.getAddrs(), false);
                CcdUtils.addConvertedTelecoms(participantPerson.getTelecoms(), associatedEntity.getTelecoms(), false);

                if (CollectionUtils.isNotEmpty(participantPerson.getNames())) {
                    org.eclipse.mdht.uml.cda.Person person = CDAFactory.eINSTANCE.createPerson();
                    for (Name name : participantPerson.getNames()) {
                        CcdUtils.addConvertedName(person.getNames(), name);
                    }
                    associatedEntity.setAssociatedPerson(person);
                }
            }
            ccdParticipant.setAssociatedEntity(associatedEntity);
            ccdParticipants.add(ccdParticipant);
        }

        return ccdParticipants;
    }

    @Override
    public List<Participant> parseSection(Client client, Collection<Participant1> participants) {
        if (CollectionUtils.isEmpty(participants)) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        List<Participant> resultList = new ArrayList<>();
        for (Participant1 srcParticipant : participants) {

            Participant resultParticipant = new Participant();
            resultList.add(resultParticipant);
            resultParticipant.setClient(client);
            resultParticipant.setOrganization(client.getOrganization());
            resultParticipant.setOrganizationId(client.getOrganizationId());

            if (CcdParseUtils.hasContent(srcParticipant.getTime())) {
                Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(srcParticipant.getTime());
                if (effectiveTime != null) {
                    resultParticipant.setTimeHigh(effectiveTime.getFirst());
                    resultParticipant.setTimeLow(effectiveTime.getSecond());
                }
            }

            AssociatedEntity associatedEntity = srcParticipant.getAssociatedEntity();
            Person person = personFactory.parse(associatedEntity, client.getOrganization(), "NWHIN_PARTICIPANT");
            if (associatedEntity!=null) {
                if (CcdParseUtils.hasContent(associatedEntity.getCode())) {
                    resultParticipant.setRelationship(ccdCodeFactory.convert(associatedEntity.getCode()));
                }
                if (associatedEntity.getClassCode()!=null) {
                    // TODO replace dependency on CcdCodeDao with dependency on ccdCodeFactory
                    resultParticipant.setRoleCode(ccdCodeDao.getCcdCode(associatedEntity.getClassCode().getName(), ROLE_CODE_CODE_SYSTEM));
                }
            }
            resultParticipant.setPerson(person);
        }
        return resultList;
    }

}