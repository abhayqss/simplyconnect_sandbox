package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
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
import org.springframework.util.CollectionUtils;

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
                if (participantPerson.getAddresses() != null) {
                    for (PersonAddress address : participantPerson.getAddresses()) {
                        CcdUtils.addConvertedAddress(associatedEntity.getAddrs(), address);
                    }
                }
                if (participantPerson.getTelecoms() != null) {
                    for (PersonTelecom telecom : participantPerson.getTelecoms()) {
                        CcdUtils.addConvertedTelecom(associatedEntity.getTelecoms(), telecom);
                    }
                }
                if (participantPerson.getNames() != null) {
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
    public List<Participant> parseSection(Resident resident, Collection<Participant1> participants) {
        if (CollectionUtils.isEmpty(participants)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        List<Participant> resultList = new ArrayList<>();
        for (Participant1 srcParticipant : participants) {

            Participant resultParticipant = new Participant();
            resultList.add(resultParticipant);
            resultParticipant.setResident(resident);
            resultParticipant.setDatabase(resident.getDatabase());
            resultParticipant.setDatabaseId(resident.getDatabaseId());

            if (CcdParseUtils.hasContent(srcParticipant.getTime())) {
                Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(srcParticipant.getTime());
                if (effectiveTime != null) {
                    resultParticipant.setTimeHigh(effectiveTime.getFirst());
                    resultParticipant.setTimeLow(effectiveTime.getSecond());
                }
            }

            AssociatedEntity associatedEntity = srcParticipant.getAssociatedEntity();
            Person person = personFactory.parse(associatedEntity, resident.getDatabase(), "NWHIN_PARTICIPANT");
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