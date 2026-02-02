package com.scnsoft.eldermark.hl7v2.hapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashRemovingInDatePrimitiveTypeCorrectorTest {

    DashRemovingInDatePrimitiveTypeCorrector instance = new DashRemovingInDatePrimitiveTypeCorrector();

    @Test
    void correctWithoutNanos() {
        assertThat(instance.correct("2017-06-22")).isEqualTo("20170622");
    }

    @Test
    void correctWithNanosPositiveOffset() {
        assertThat(instance.correct("20220629164753.512+0300")).isEqualTo("20220629164753.512+0300");
    }

    @Test
    void correctWithNanosNegativeOffset() {
        assertThat(instance.correct("2022-06-29164753.512-0300")).isEqualTo("20220629164753.512-0300");
    }
}