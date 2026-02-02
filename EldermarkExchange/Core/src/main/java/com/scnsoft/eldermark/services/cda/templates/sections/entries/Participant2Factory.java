package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.cda.ParticipantRole;
import org.eclipse.mdht.uml.cda.PlayingEntity;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 4/24/2018.
 */
@Component
public class Participant2Factory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public Participant2Factory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public static class ParticipantWrapper {
        private List<Participant> verifiers = null;
        private Participant custodian = null;

        public List<Participant> getVerifiers() {
            return verifiers;
        }

        public void setVerifiers(List<Participant> verifiers) {
            this.verifiers = verifiers;
        }

        public void addVerifier(Participant verifier) {
            if (verifier == null) return;
            if (this.getVerifiers() == null) {
                this.setVerifiers(new ArrayList<Participant>());
            }
            this.verifiers.add(verifier);
        }

        public Participant getCustodian() {
            return custodian;
        }

        public void setCustodian(Participant custodian) {
            this.custodian = custodian;
        }
    }

    public static ParticipantWrapper parse(EList<Participant2> srcParticipants, Resident resident, String LEGACY_TABLE) {
        final ParticipantWrapper participantWrapperVO = new ParticipantWrapper();
        if (CollectionUtils.isEmpty(srcParticipants)) {
            return participantWrapperVO;
        }

        for (Participant2 srcParticipant : srcParticipants) {
            switch (srcParticipant.getTypeCode()) {
                case VRF:
                    final Participant verifier = parseVerifier(srcParticipant, resident, LEGACY_TABLE);
                    participantWrapperVO.addVerifier(verifier);
                    break;
                case CST:
                    final Participant custodian = parseCustodian(srcParticipant, resident, LEGACY_TABLE);
                    participantWrapperVO.setCustodian(custodian);
                    break;
            }
        }

        return participantWrapperVO;
    }

    private static Participant parseCustodian(Participant2 ccdParticipant, Resident resident, String LEGACY_TABLE) {
        final Participant custodian = new Participant();

        final ParticipantRole participantRole = ccdParticipant.getParticipantRole();
        if (participantRole != null) {
            custodian.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
        }
        custodian.setLegacyTable(LEGACY_TABLE);
        custodian.setDatabase(resident.getDatabase());
        custodian.setOrganization(resident.getFacility());

        // set name and telecoms to person; to org we probably should not set.
        // but this person can represent a custodian organization as well.
        if (ccdParticipant.getParticipantRole() != null) {
            final PlayingEntity ccdPlayingEntity = ccdParticipant.getParticipantRole().getPlayingEntity();
            if (CollectionUtils.isNotEmpty(ccdPlayingEntity.getNames())) {
                final Person person = CcdParseUtils.createPerson(ccdParticipant.getParticipantRole(), resident.getDatabase(), LEGACY_TABLE);
                custodian.setPerson(person);
            }
        }

        return custodian;
    }

    private static Participant parseVerifier(Participant2 ccdParticipant, Resident resident, String LEGACY_TABLE) {
        final Participant verifier = new Participant();

        final ParticipantRole participantRole = ccdParticipant.getParticipantRole();
        if (participantRole != null) {
            // TODO: inbound ID may be missing or its type may be String
            verifier.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
        }
        verifier.setLegacyTable(LEGACY_TABLE);
        verifier.setDatabase(resident.getDatabase());

        verifier.setTimeLow(CcdParseUtils.convertTsToDate(ccdParticipant.getTime()));
        if (participantRole != null && participantRole.getPlayingEntity() != null) {
            final PlayingEntity ccdPlayingEntity = participantRole.getPlayingEntity();
            if (CollectionUtils.isNotEmpty(ccdPlayingEntity.getNames())) {
                final Person person = CcdParseUtils.createPerson(participantRole, resident.getDatabase(), LEGACY_TABLE);
                verifier.setPerson(person);
            }
        }

        return verifier;
    }

    public Participant parseCoverageTarget(Participant2 participant2, PolicyActivity policyActivity, Database database, String LEGACY_TABLE) {
        final Participant participant = new Participant();
        participant.setDatabase(database);
        participant.setLegacyTable(LEGACY_TABLE);
        //ParticipantRole coveredParty = ccdPolicyActivity.getCoveredParty(); ?

        final ParticipantRole participantRole = participant2.getParticipantRole();
        if (participantRole != null) {
            participant.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
        }

        final Pair<Date, Date> time = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
        if (time != null) {
            participant.setTimeHigh(time.getFirst());
            participant.setTimeLow(time.getSecond());
        }

        // TODO test on real examples
        if (CcdParseUtils.hasContent(participantRole)) {
            participant.setRoleCode(ccdCodeFactory.convert(participantRole.getCode()));
            II participantRoleId = CcdParseUtils.getFirstNotEmptyValue(participantRole.getIds(), II.class);
            if (participantRoleId != null) {
                policyActivity.setParticipantMemberId(participantRoleId.getExtension());
            }

            final PlayingEntity playingEntity = participantRole.getPlayingEntity();
            if (playingEntity != null) {
                Person coverageTargetPerson = CcdParseUtils.createPerson(participantRole, database, LEGACY_TABLE);
                participant.setPerson(coverageTargetPerson);
                policyActivity.setParticipantDateOfBirth(CcdParseUtils.convertTsToDate(playingEntity.getSDTCBirthTime()));
            }
        }

        return participant;
    }

    public Participant parseHolder(Participant2 participant2, PolicyActivity policyActivity, Database database, String LEGACY_TABLE) {
        final Participant participant = new Participant();
        participant.setDatabase(database);
        participant.setLegacyTable(LEGACY_TABLE);
        //ParticipantRole subscriber = ccdPolicyActivity.getSubscriber();
        // TODO retrieve holder (subscriber participant) by ID?

        final ParticipantRole participantRole = participant2.getParticipantRole();
        if (participantRole != null) {
            participant.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
        }

        final Pair<Date, Date> highLowDate = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
        if (highLowDate != null) {
            participant.setTimeLow(highLowDate.getSecond());
        }

        // TODO test on real examples
        if (CcdParseUtils.hasContent(participantRole)) {
            participant.setRoleCode(ccdCodeFactory.convert(participantRole.getCode()));
            if (CollectionUtils.isNotEmpty(participantRole.getAddrs())) {
                final Person holderPerson = CcdParseUtils.createPerson(participantRole, database, LEGACY_TABLE);
                participant.setPerson(holderPerson);
            }
        }

        return participant;
    }

}
