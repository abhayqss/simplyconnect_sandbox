package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersMedClaimDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersTermedMemberDao;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersTermedMember;
import com.scnsoft.eldermark.service.healthpartners.processor.med.HealthPartnersMedClaimProcessor;
import com.scnsoft.eldermark.service.healthpartners.processor.rx.HealthPartnersRxClaimProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthPartnersRecordServiceImplTest {

    private static final Long COMMUNITY_ID = 154231L;

    @Mock
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Mock
    private HealthPartnersRxClaimProcessor healthPartnersRxClaimProcessor;

    @Mock
    private HealthPartnersTermedMemberDao healthPartnersTermedMemberDao;

    @Mock
    private HealthPartnersTermedMemberProcessor healthPartnersTermedMemberProcessor;

    @Mock
    private HealthPartnersMedClaimDao healthPartnersMedClaimDao;

    @Mock
    private HealthPartnersMedClaimProcessor healthPartnersMedClaimProcessor;

    @InjectMocks
    private HealthPartnersRecordServiceImpl instance;

    @Test
    void process_inputRxClaimInvalid_savesFailOnly() {
        //todo more test for validation
        var claim = new HealthPartnersRxClaim();

        instance.processRxClaim(claim, COMMUNITY_ID);

        verify(healthPartnersRxClaimDao).save(claim);
        verifyNoInteractions(healthPartnersRxClaimProcessor);
    }

    @Test
    void process_inputRxClaimValid_proceedProcessingAndSave() {
        var claim = new HealthPartnersRxClaim();
        claim.setMemberIdentifier("identifier");
        claim.setMemberFirstName("firstName");
        claim.setMemberLastName("lastName");
        claim.setBirthDate(LocalDate.now());

        doNothing().when(healthPartnersRxClaimProcessor).process(claim, COMMUNITY_ID);

        instance.processRxClaim(claim, COMMUNITY_ID);

        verify(healthPartnersRxClaimDao).save(claim);
    }

    @Test
    void process_inputTermedMemberInvalid_savesFailOnly() {
        //todo more test for validation
        var termedMember = new HealthPartnersTermedMember();

        instance.processTermedMember(termedMember, COMMUNITY_ID);

        verify(healthPartnersTermedMemberDao).save(termedMember);
        verifyNoInteractions(healthPartnersTermedMemberProcessor);
    }


    @Test
    void process_inputTermedMemberValid_proceedProcessingAndSave() {
        var termedMember = new HealthPartnersTermedMember();
        termedMember.setMemberIdentifier("identifier");
        termedMember.setMemberFirstName("firstName");
        termedMember.setMemberLastName("lastName");
        termedMember.setBirthDate(LocalDate.now());

        doNothing().when(healthPartnersTermedMemberProcessor).process(termedMember, COMMUNITY_ID);

        instance.processTermedMember(termedMember, COMMUNITY_ID);

        verify(healthPartnersTermedMemberDao).save(termedMember);
    }

    @Test
    void process_inputMedClaimInvalid_savesFailOnly() {
        //todo more test for validation
        var claim = new HealthPartnersMedClaim();

        instance.processMedClaim(claim, COMMUNITY_ID);

        verify(healthPartnersMedClaimDao).save(claim);
        verifyNoInteractions(healthPartnersMedClaimProcessor);
    }

    @Test
    void process_inputMedClaimValid_proceedProcessingAndSave() {
        var claim = new HealthPartnersMedClaim();
        claim.setMemberIdentifier("identifier");
        claim.setMemberFirstName("firstName");
        claim.setMemberLastName("lastName");
        claim.setBirthDate(LocalDate.now());
        claim.setDiagnosisCode("code");

        doNothing().when(healthPartnersMedClaimProcessor).process(claim, COMMUNITY_ID);

        instance.processMedClaim(claim, COMMUNITY_ID);

        verify(healthPartnersMedClaimDao).save(claim);
    }
}
