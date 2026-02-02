package com.scnsoft.eldermark.hl7v2.processor.imsurance;

import com.scnsoft.eldermark.hl7v2.processor.insurance.InsuranceUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class InsuranceUtilsTest {


    @ParameterizedTest
    @MethodSource("testBuildInsuranceCodeParams")
    void testBuildInsuranceCode(String input, String expectedOutcome) {
        Assertions.assertThat(InsuranceUtils.buildInsuranceCode(input)).isEqualTo(expectedOutcome);
    }

    private static Stream<Arguments> testBuildInsuranceCodeParams() {
        return Stream.of(
                Arguments.of("test1", "TEST1"),
                Arguments.of("test space", "TEST_SPACE"),
                Arguments.of("tes't o$t#h@e!r", "TES_T_O_T_H_E_R")
        );
    }


}