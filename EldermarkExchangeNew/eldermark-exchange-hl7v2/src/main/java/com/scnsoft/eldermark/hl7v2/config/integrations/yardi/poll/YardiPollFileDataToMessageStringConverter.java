package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

public class YardiPollFileDataToMessageStringConverter implements Converter<String, String> {

    private static final Logger logger = LoggerFactory.getLogger(YardiPollFileDataToMessageStringConverter.class);

    @Override
    public String convert(String source) {
        if (StringUtils.isEmpty(source)) {
            return StringUtils.EMPTY;
        }

        var messageStartIdx = source.indexOf("<response>");
        if (messageStartIdx == -1) {
            logger.info("Didn't find message start in Yardi payload");
            return StringUtils.EMPTY;
        }
        messageStartIdx += + "<response>".length();

        var messageEndIdx = source.indexOf("</response>", messageStartIdx);
        if (messageEndIdx == -1) {
            logger.info("Didn't find message end in Yardi payload");
            return StringUtils.EMPTY;
        }

        var messageEncoded = source.substring(messageStartIdx, messageEndIdx);
        return StringEscapeUtils.unescapeXml(messageEncoded);
    }
}
