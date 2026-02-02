package com.scnsoft.eldermark.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class XmlToMapParserTest {

    @Test
    void parseEscaped_Test1() {
        var responseEscapedStr = "&lt;Results Version=\"511\">&lt;ClientID>&lt;![CDATA[1111]]&gt;&lt;/ClientID>&lt;/Results>";

        var result = XmlToMapParser.parseEscaped(responseEscapedStr);

        Assertions.assertThat(result)
                .containsEntry("ClientID", "1111")
                .size().isEqualTo(1);
    }

    @Test
    void parseEscaped_Test2() {
        var responseEscapedStr = "&lt;test&gt;&lt;level1a&gt;contains level 1a&lt;/level1a&gt;&lt;level1b&gt;&lt;level2&gt;contains level 2&lt;/level2&gt;&lt;/level1b&gt;&lt;/test&gt;";

        var result = XmlToMapParser.parseEscaped(responseEscapedStr);

        Assertions.assertThat(result)
                .containsEntry("level1a", "contains level 1a")
                .containsEntry("level2", "contains level 2")
                .size().isEqualTo(2);
    }
}