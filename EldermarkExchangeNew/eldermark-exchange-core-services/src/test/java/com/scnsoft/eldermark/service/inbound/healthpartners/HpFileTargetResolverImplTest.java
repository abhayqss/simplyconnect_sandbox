package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HpFileTargetResolverImplTest {

    private static final Long COMMUNITY_ID = 1L;
    private static final Long TEST_COMMUNITY_ID = 2L;

    private static final String hpOrgAlternativeId = "Health_Partners";
    private static final String hpCommunityOid = "Health_Partners_Claims";
    private static final String hpTestOrgAlternativeId = "Health_Partners_Test";
    private static final String hpTestCommunityOid = "Health_Partners_Claims_Test";

    @Mock
    private CommunityDao communityDao;

    private HpFileTargetResolverImpl instance;

    @BeforeEach
    void init() {
        instance = new HpFileTargetResolverImpl(hpOrgAlternativeId, hpCommunityOid, hpTestOrgAlternativeId,
                hpTestCommunityOid, communityDao);
    }

    @Test
    void resolveTargetCommunityId_SFTPFile_shouldReturnSftpCommunityId() {
        when(communityDao.findByOrganization_AlternativeIdAndOid(hpOrgAlternativeId, hpCommunityOid, IdAware.class))
                .thenReturn(Optional.of(() -> COMMUNITY_ID));

        var result = instance.resolveTargetCommunityId(HpFileSource.SFTP);

        assertEquals(COMMUNITY_ID, result);
    }

    @Test
    void resolveTargetCommunityId_TESTFile_shouldReturnTESTCommunityId() {
        when(communityDao.findByOrganization_AlternativeIdAndOid(hpTestOrgAlternativeId, hpTestCommunityOid, IdAware.class))
                .thenReturn(Optional.of(() -> TEST_COMMUNITY_ID));

        var result = instance.resolveTargetCommunityId(HpFileSource.TESTING);

        assertEquals(TEST_COMMUNITY_ID, result);
    }
}