package com.scnsoft.eldermark.service.rxnorm;

import com.scnsoft.eldermark.dao.NationalDrugCodeDao;
import com.scnsoft.eldermark.entity.NationalDrugCode;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import com.scnsoft.eldermark.service.rxnorm.dto.NDCStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NdcToRxnormResolverImplTest {

    @Mock
    private RxNormApiGateway rxNormApiGateway;

    @Mock
    private CcdCodeCustomService ccdCodeService;

    @Mock
    private NationalDrugCodeDao nationalDrugCodeDao;

    @Mock
    private CachingRxNormVersionResolver cachingRxNormVersionResolver;

    @InjectMocks
    private NdcToRxnormResolverImpl instance;

    @Nested
    class NdcCachedTestCase {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void resolve_cachedSameVersion_ReturnsCodeFromCache(boolean codeIsNull) {
            var rxNormVersion = "version1";
            var ndc = "123444321";
            var ndcEntity = new NationalDrugCode();
            ndcEntity.setDatasetVersion(rxNormVersion);

            var code = codeIsNull ? null : new CcdCode();
            ndcEntity.setRxNormCcdCode(code);

            when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.of(ndcEntity));
            when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);

            var resolved = instance.resolve(ndc);

            if (codeIsNull) {
                assertFalse(resolved.isPresent());
            } else {
                assertTrue(resolved.isPresent());
                assertSame(code, resolved.get());
            }

            verifyNoMoreInteractions(rxNormApiGateway);
            verifyNoMoreInteractions(nationalDrugCodeDao);
            verifyNoInteractions(ccdCodeService);
        }

        @Nested
        class CachedDifferentVersionTestCase {

            @ParameterizedTest
            @ValueSource(strings = {"ACTIVE", "OBSOLETE"})
            void resolve_codeNotChangedAndStatusToResolvable_updatesCacheAndCodeNotSearched(String status) {
                var rxNormVersion = "version1";
                var ndc = "123444321";
                var rxNormCode = "rxNormCode";
                var ndcEntity = new NationalDrugCode();
                ndcEntity.setDatasetVersion("prevVersion");

                var oldCode = new CcdCode();
                oldCode.setCode(rxNormCode);
                ndcEntity.setRxNormCcdCode(oldCode);

                var ndcStatus = createNDCStatus(status, rxNormCode);

                when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.of(ndcEntity));
                when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);
                when(rxNormApiGateway.getNDCStatus(ndc)).thenReturn(ndcStatus);

                var resolved = instance.resolve(ndc);

                assertTrue(resolved.isPresent());
                assertSame(oldCode, resolved.get());

                assertEquals(rxNormVersion, ndcEntity.getDatasetVersion());
                assertEquals(status, ndcEntity.getStatus());
                assertSame(oldCode, ndcEntity.getRxNormCcdCode());

                verifyNoMoreInteractions(rxNormApiGateway);
                verify(nationalDrugCodeDao).save(ndcEntity);
                verifyNoInteractions(ccdCodeService);
            }

            @ParameterizedTest
            @ValueSource(strings = {"ACTIVE", "OBSOLETE"})
            void resolve_codeChangedAndStatusToResolvable_updatesCacheAndCodeSearched(String status) {
                var rxNormVersion = "version1";
                var ndc = "123444321";
                var rxNormCode = "rxNormCode";
                var ndcEntity = new NationalDrugCode();
                ndcEntity.setDatasetVersion("prevVersion");

                var oldCode = new CcdCode();
                oldCode.setCode(rxNormCode + "old");
                ndcEntity.setRxNormCcdCode(oldCode);

                var newCode = new CcdCode();

                var ndcStatus = createNDCStatus(status, rxNormCode);

                when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.of(ndcEntity));
                when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);
                when(rxNormApiGateway.getNDCStatus(ndc)).thenReturn(ndcStatus);
                when(ccdCodeService.findOrCreate(ndcStatus.getCode(), ndcStatus.getDisplayName(), CodeSystem.RX_NORM))
                        .thenReturn(Optional.of(newCode));

                var resolved = instance.resolve(ndc);

                assertTrue(resolved.isPresent());
                assertSame(newCode, resolved.get());

                assertEquals(rxNormVersion, ndcEntity.getDatasetVersion());
                assertEquals(status, ndcEntity.getStatus());
                assertSame(newCode, ndcEntity.getRxNormCcdCode());

                verifyNoMoreInteractions(rxNormApiGateway);
                verify(nationalDrugCodeDao).save(ndcEntity);
            }

            @ParameterizedTest
            @ValueSource(strings = {"ALIEN", "UNKNOWN"})
            void resolve_statusToNotResolvable_updatesCacheAndCodeSetNull(String status) {
                var rxNormVersion = "version1";
                var ndc = "123444321";
                var rxNormCode = "rxNormCode";
                var ndcEntity = new NationalDrugCode();
                ndcEntity.setDatasetVersion("prevVersion");

                var oldCode = new CcdCode();
                oldCode.setCode(rxNormCode);
                ndcEntity.setRxNormCcdCode(oldCode);

                var ndcStatus = createNDCStatus(status, rxNormCode);

                when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.of(ndcEntity));
                when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);
                when(rxNormApiGateway.getNDCStatus(ndc)).thenReturn(ndcStatus);

                var resolved = instance.resolve(ndc);

                assertTrue(resolved.isEmpty());

                assertEquals(rxNormVersion, ndcEntity.getDatasetVersion());
                assertEquals(status, ndcEntity.getStatus());
                assertNull(ndcEntity.getRxNormCcdCode());

                verifyNoMoreInteractions(rxNormApiGateway);
                verify(nationalDrugCodeDao).save(ndcEntity);
                verifyNoInteractions(ccdCodeService);
            }
        }
    }

    @Nested
    class NdcNotCachedTestCase {

        @Captor
        private ArgumentCaptor<NationalDrugCode> nationalDrugCodeArgumentCaptor;

        @ParameterizedTest
        @ValueSource(strings = {"ACTIVE", "OBSOLETE"})
        void resolve_Resolvable_updatesCacheAndCodeSearched(String status) {
            var rxNormVersion = "version1";
            var ndc = "123444321";
            var rxNormCode = "rxNormCode";

            var code = new CcdCode();
            var ndcStatus = createNDCStatus(status, rxNormCode);

            when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.empty());
            when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);
            when(rxNormApiGateway.getNDCStatus(ndc)).thenReturn(ndcStatus);
            when(ccdCodeService.findOrCreate(ndcStatus.getCode(), ndcStatus.getDisplayName(), CodeSystem.RX_NORM))
                    .thenReturn(Optional.of(code));
            when(nationalDrugCodeDao.save(nationalDrugCodeArgumentCaptor.capture())).thenAnswer(returnsFirstArg());

            var resolved = instance.resolve(ndc);

            assertTrue(resolved.isPresent());
            assertSame(code, resolved.get());

            var ndcEntity = nationalDrugCodeArgumentCaptor.getValue();
            assertEquals(rxNormVersion, ndcEntity.getDatasetVersion());
            assertEquals(status, ndcEntity.getStatus());
            assertSame(code, ndcEntity.getRxNormCcdCode());

            verifyNoMoreInteractions(rxNormApiGateway);
        }


        @ParameterizedTest
        @ValueSource(strings = {"ALIEN", "UNKNOWN"})
        void resolve_NotResolvable_updatesCacheAndCodeNull(String status) {
            var rxNormVersion = "version1";
            var ndc = "123444321";
            var rxNormCode = "rxNormCode";

            var code = new CcdCode();
            var ndcStatus = createNDCStatus(status, rxNormCode);

            when(nationalDrugCodeDao.findFirstByNationalDrugCode(ndc)).thenReturn(Optional.empty());
            when(cachingRxNormVersionResolver.getRxNormVersion()).thenReturn(rxNormVersion);
            when(rxNormApiGateway.getNDCStatus(ndc)).thenReturn(ndcStatus);
            when(nationalDrugCodeDao.save(nationalDrugCodeArgumentCaptor.capture())).thenAnswer(returnsFirstArg());

            var resolved = instance.resolve(ndc);

            assertTrue(resolved.isEmpty());

            var ndcEntity = nationalDrugCodeArgumentCaptor.getValue();
            assertEquals(rxNormVersion, ndcEntity.getDatasetVersion());
            assertEquals(status, ndcEntity.getStatus());
            assertNull(ndcEntity.getRxNormCcdCode());

            verifyNoInteractions(ccdCodeService);
            verifyNoMoreInteractions(rxNormApiGateway);
        }
    }

    private NDCStatus createNDCStatus(String status, String code) {
        var NDCStatus = new NDCStatus();
        NDCStatus.setStatus(status);
        NDCStatus.setCode(code);
        NDCStatus.setDisplayName("displayName");
        return NDCStatus;
    }
}