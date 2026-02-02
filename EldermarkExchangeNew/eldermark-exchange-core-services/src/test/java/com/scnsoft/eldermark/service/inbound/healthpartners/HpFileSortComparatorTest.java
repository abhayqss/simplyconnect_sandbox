package com.scnsoft.eldermark.service.inbound.healthpartners;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HpFileSortComparatorTest {

    HpFileSortComparator instance = new HpFileSortComparator();

    @Test
    void testComparator() {
        var files = Stream.of(
                        "CONSANA_MEDICAL_20220125_142301.txt",
                        "CONSANA_MEDICAL_20220222_091128.txt",
                        "CONSANA_RX_20220125_142231.txt",
                        "CONSANA_RX_20220222_090918.txt",
                        "CONSANA_TERMED_MEMBERS_20220124_132051.txt",
                        "CONSANA_TERMED_MEMBERS_20220125_142344.txt",
                        "CONSANA_TERMED_MEMBERS_20220222_091218.txt"
                )
                .map(File::new)
                .collect(Collectors.toList());


        var expected = Arrays.asList(
                "CONSANA_TERMED_MEMBERS_20220124_132051.txt",

                "CONSANA_MEDICAL_20220125_142301.txt",
                "CONSANA_RX_20220125_142231.txt",
                "CONSANA_TERMED_MEMBERS_20220125_142344.txt",

                "CONSANA_MEDICAL_20220222_091128.txt",
                "CONSANA_RX_20220222_090918.txt",
                "CONSANA_TERMED_MEMBERS_20220222_091218.txt"
        );

        var result = files.stream()
                .sorted(instance)
                .map(File::getName)
                .collect(Collectors.toList());

        assertThat(result).containsExactlyElementsOf(expected);
    }

}