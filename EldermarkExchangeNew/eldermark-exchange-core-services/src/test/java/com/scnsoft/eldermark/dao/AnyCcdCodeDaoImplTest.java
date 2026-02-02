package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import com.scnsoft.eldermark.entity.document.UnknownCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.DiagnosisCcdCode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnyCcdCodeDaoImplTest {

    @Mock
    private ConcreteCcdCodeDao concreteCcdCodeDao;

    @Mock
    private DiagnosisCcdCodeDao diagnosisCcdCodeDao;

    @Mock
    private UnknownCcdCodeDao unknownCcdCodeDao;

    @Mock
    private InterpretiveCcdCodeDao interpretiveCcdCodeDao;

    @InjectMocks
    private AnyCcdCodeDaoImpl instance;

    @Nested
    public class GetCcdCode {

        @Nested
        public class ConcreteCodePresent {

            @Test
            void inputDisplayNameIsNull_ShouldReturnFoundCode() {
                var code = "code";
                var codeSystem = "codeSystem";
                var valueSet = "valueSet";

                var concreteCode = createConcreteCode(code, codeSystem, null, valueSet);


                when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet))
                        .thenReturn(concreteCode);


                var result = instance.getCcdCode(code, codeSystem, null, valueSet);


                assertEquals(concreteCode, result);
            }

            @Test
            void foundCodeHasSameDisplayName_ShouldReturnFoundCode() {
                var code = "code";
                var codeSystem = "codeSystem";
                var valueSet = "valueSet";
                var displayName = "displayName";

                var concreteCode = createConcreteCode(code, codeSystem, displayName, valueSet);


                when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet))
                        .thenReturn(concreteCode);


                var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);


                assertEquals(concreteCode, result);
            }

            @Nested
            public class FoundCodeHasDifferentDisplayName {

                @Test
                void hasInterpretiveCodeWithSameName_ShouldReturnInterpretiveCode() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";
                    var interpretiveDisplayName = "displayNameInterpretive";

                    var concreteCode = createConcreteCode(code, codeSystem, displayName, valueSet);
                    var interpretive1 = createInterpretive(concreteCode, interpretiveDisplayName);


                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet))
                            .thenReturn(concreteCode);
                    when(interpretiveCcdCodeDao.getCcdCode(concreteCode, interpretiveDisplayName))
                            .thenReturn(interpretive1);


                    var result = instance.getCcdCode(code, codeSystem, interpretiveDisplayName, valueSet);


                    assertEquals(interpretive1, result);
                }

                @Test
                void hasNoInterpretiveCode_ShouldReturnConcreteCode() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";
                    var interpretiveDisplayName = "displayNameInterpretive";

                    var concreteCode = createConcreteCode(code, codeSystem, displayName, valueSet);


                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet))
                            .thenReturn(concreteCode);
                    when(interpretiveCcdCodeDao.getCcdCode(concreteCode, interpretiveDisplayName))
                            .thenReturn(null);


                    var result = instance.getCcdCode(code, codeSystem, interpretiveDisplayName, valueSet);


                    assertEquals(concreteCode, result);
                }

                private InterpretiveCcdCode createInterpretive(ConcreteCcdCode concreteCcdCode, String displayName) {
                    var interpretive = new InterpretiveCcdCode();
                    interpretive.setCode(concreteCcdCode.getCode());
                    interpretive.setCodeSystem(concreteCcdCode.getCodeSystem());
                    interpretive.setCodeSystemName(concreteCcdCode.getCodeSystemName());
                    interpretive.setReferredCcdCode(concreteCcdCode);
                    interpretive.setDisplayName(displayName);
                    return interpretive;
                }
            }

            private ConcreteCcdCode createConcreteCode(String code, String codeSystem, String displayName, String valueSet) {
                var concreteCcdCode = new ConcreteCcdCode();
                concreteCcdCode.setCode(code);
                concreteCcdCode.setCodeSystem(codeSystem);
                concreteCcdCode.setDisplayName(displayName);
                concreteCcdCode.setValueSet(valueSet);
                return concreteCcdCode;
            }
        }

        @Nested
        public class DiagnosisCodePresent {

            @Nested
            public class InputDisplayNameIsEmpty {

                //todo better names

                @Test
                void hasCodeWithNullDiagnosisAndPresentName_selectsCodeWithNullDiagnosisSetupAndPresentName() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeNullSetup = createDiagnosisCode(code, codeSystem, displayName, null);
                    var diagnosisCodeNullSetupNullName = createDiagnosisCode(code, codeSystem, null, null);
                    var diagnosisCodeWithSetup = createDiagnosisCode(code, codeSystem, displayName, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetup, diagnosisCodeNullSetup, diagnosisCodeNullSetupNullName));

                    var result = instance.getCcdCode(code, codeSystem, null, valueSet);

                    assertEquals(diagnosisCodeNullSetup, result);
                }

                @Test
                void hasCodeWithNullDiagnosis_selectsCodeWithNullDiagnosisSetup() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeNullSetupNullName = createDiagnosisCode(code, codeSystem, null, null);
                    var diagnosisCodeWithSetup = createDiagnosisCode(code, codeSystem, displayName, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetup, diagnosisCodeNullSetupNullName));

                    var result = instance.getCcdCode(code, codeSystem, null, valueSet);

                    assertEquals(diagnosisCodeNullSetupNullName, result);
                }

                @Test
                void hasOnlyCodesWithDiagnosisSetup_selectsCodeWithPresentDiagnosisSetupAndPresentName() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeWithSetupNullName = createDiagnosisCode(code, codeSystem, null, 5L);
                    var diagnosisCodeWithSetupWithName = createDiagnosisCode(code, codeSystem, displayName, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetupWithName, diagnosisCodeWithSetupNullName));

                    var result = instance.getCcdCode(code, codeSystem, null, valueSet);

                    assertEquals(diagnosisCodeWithSetupWithName, result);
                }

                @Test
                void hasOnlyCodesWithDiagnosisSetup_selectsCodeWithPresentDiagnosisSetup() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeWithSetupNullName = createDiagnosisCode(code, codeSystem, null, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetupNullName));

                    var result = instance.getCcdCode(code, codeSystem, null, valueSet);

                    assertEquals(diagnosisCodeWithSetupNullName, result);
                }
            }

            @Nested
            public class InputDisplayNamePresent {
                //todo better names

                @Test
                void selectsCodeWithNullDiagnosisSetupAndSameName() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeNullSetupMatchingName = createDiagnosisCode(code, codeSystem, displayName, null);
                    var diagnosisCodeNullSetupNonMatchingName = createDiagnosisCode(code, codeSystem, "non-matching_name", null);
                    var diagnosisCodeWithSetup = createDiagnosisCode(code, codeSystem, displayName, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetup, diagnosisCodeNullSetupNonMatchingName,
                                    diagnosisCodeNullSetupMatchingName));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertEquals(diagnosisCodeNullSetupMatchingName, result);
                }

                @Test
                void selectsCodeWithNullDiagnosisSetup() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeNullSetupAnotherName = createDiagnosisCode(code, codeSystem, "another-name", null);
                    var diagnosisCodeWithSetup = createDiagnosisCode(code, codeSystem, displayName, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetup, diagnosisCodeNullSetupAnotherName));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertEquals(diagnosisCodeNullSetupAnotherName, result);
                }

                @Test
                void selectsCodeWithPresentDiagnosisSetupAndSameName() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeWithSetupAnotherName = createDiagnosisCode(code, codeSystem, "another-name", 5L);
                    var diagnosisCodeWithSetupSameName = createDiagnosisCode(code, codeSystem, displayName, 5L);
                    var diagnosisCodeWithSetupNullName = createDiagnosisCode(code, codeSystem, null, 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetupAnotherName, diagnosisCodeWithSetupSameName,
                                    diagnosisCodeWithSetupNullName));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertEquals(diagnosisCodeWithSetupSameName, result);
                }

                @Test
                void selectsCodeWithPresentDiagnosisSetup() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var diagnosisCodeWithSetupNullName = createDiagnosisCode(code, codeSystem, null, 5L);
                    var diagnosisCodeWithSetupAnotherName = createDiagnosisCode(code, codeSystem, "another-name", 5L);

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Arrays.asList(diagnosisCodeWithSetupNullName, diagnosisCodeWithSetupAnotherName));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertEquals(diagnosisCodeWithSetupAnotherName, result);
                }
            }

            private DiagnosisCcdCode createDiagnosisCode(String code, String codeSystem, String displayName, Long diagnosisSetupId) {
                var diagnosisCode = new DiagnosisCcdCode();
                diagnosisCode.setCode(code);
                diagnosisCode.setCodeSystem(codeSystem);
                diagnosisCode.setDisplayName(displayName);
                diagnosisCode.setDiagnosisSetupId(diagnosisSetupId);
                return diagnosisCode;
            }
        }

        @Nested
        public class UnknownCode {


            @Test
            void inputDisplayNameIsNull_returnsNull() {
                var code = "code";
                var codeSystem = "codeSystem";
                var valueSet = "valueSet";
                var displayName = "displayName";

                var unkCode1 = createUnknownCode(code, codeSystem, displayName);
                var unkCode2 = createUnknownCode(code, codeSystem, displayName + "2");

                when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                        .thenReturn(Collections.emptyList());

                when(unknownCcdCodeDao.getCcdCodes(code, codeSystem)).thenReturn(Arrays.asList(unkCode1, unkCode2));

                var result = instance.getCcdCode(code, codeSystem, null, valueSet);

                assertNull(result);
            }

            @Nested
            public class InputDisplayNameIsNotNull {

                @Test
                void hasNoMatchingName_returnsNull() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var unkCode1 = createUnknownCode(code, codeSystem, "non-matching");
                    var unkCode2 = createUnknownCode(code, codeSystem, "non-matching-2");

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Collections.emptyList());
                    when(unknownCcdCodeDao.getCcdCodes(code, codeSystem)).thenReturn(Arrays.asList(unkCode1, unkCode2));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertNull(result);
                }

                @Test
                void hasCodeWithSameName_returnsCodeWithSameName() {
                    var code = "code";
                    var codeSystem = "codeSystem";
                    var valueSet = "valueSet";
                    var displayName = "displayName";

                    var unkCode1 = createUnknownCode(code, codeSystem, displayName);
                    var unkCode2 = createUnknownCode(code, codeSystem, "non-matching-2");

                    when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
                    when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                            .thenReturn(Collections.emptyList());
                    when(unknownCcdCodeDao.getCcdCodes(code, codeSystem)).thenReturn(Arrays.asList(unkCode1, unkCode2));

                    var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

                    assertEquals(unkCode1, result);
                }
            }


            private UnknownCcdCode createUnknownCode(String code, String codeSystem, String displayName) {
                var unknownCode = new UnknownCcdCode();
                unknownCode.setCode(code);
                unknownCode.setCodeSystem(codeSystem);
                unknownCode.setDisplayName(displayName);
                return unknownCode;
            }
        }
    }

    @Test
    void noCodesFound_returnsNull() {
        var code = "code";
        var codeSystem = "codeSystem";
        var valueSet = "valueSet";
        var displayName = "displayName";

        when(concreteCcdCodeDao.getCcdCode(code, codeSystem, valueSet)).thenReturn(null);
        when(diagnosisCcdCodeDao.findAllByCodeAndCodeSystem(code, codeSystem))
                .thenReturn(Collections.emptyList());
        when(unknownCcdCodeDao.getCcdCodes(code, codeSystem)).thenReturn(Collections.emptyList());

        var result = instance.getCcdCode(code, codeSystem, displayName, valueSet);

        assertNull(result);
    }
}