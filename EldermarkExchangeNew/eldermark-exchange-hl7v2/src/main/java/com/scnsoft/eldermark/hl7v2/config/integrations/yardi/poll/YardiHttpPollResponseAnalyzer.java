package com.scnsoft.eldermark.hl7v2.config.integrations.yardi.poll;

import com.scnsoft.eldermark.hl7v2.poll.http.HttpPollResponseAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.List;

public class YardiHttpPollResponseAnalyzer implements HttpPollResponseAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(YardiHttpPollResponseAnalyzer.class);


    private final List<String> noFurtherMessagesMSAContent = List.of(
            "MSA|CR",
            "MSA|CE"
    );

    @Override
    public boolean isNoFurtherMessagesResponse(HttpResponse<String> response) {
        if (response == null) {
            logger.info("Response is null");
            return true;
        }

        if (StringUtils.isEmpty(response.body())) {
            logger.info("Response is empty");
            return true;
        }

        return noFurtherMessagesMSAContent.stream().anyMatch(response.body()::contains);
    }
}
