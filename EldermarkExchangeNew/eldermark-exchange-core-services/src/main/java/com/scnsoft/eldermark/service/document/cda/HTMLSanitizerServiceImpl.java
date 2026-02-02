package com.scnsoft.eldermark.service.document.cda;

import org.owasp.html.HtmlChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scnsoft.eldermark.service.document.cda.wrapers.PolicyFactoryWrapper;

import java.util.Arrays;

@Service
public class HTMLSanitizerServiceImpl implements HTMLSanitizerService {
    private static final Logger logger = LoggerFactory.getLogger(HTMLSanitizerServiceImpl.class);


    @Autowired
    private PolicyFactoryWrapper cdaNarrativePolicyFactoryWrapper;

    @Override
    public String sanitizeCdaNarrativeBlock(String input) {
        logger.info("Starting sanitizing HTML input.");
        final String result = cdaNarrativePolicyFactoryWrapper.getPolicyFactory().sanitize(input, new HtmlChangeListener<Object>() {

            @Override
            public void discardedTag(Object o, String elementName) {
                logger.info("Discarded tag: {}", elementName);
            }

            @Override
            public void discardedAttributes(Object o, String tagName, String... attributeNames) {
                logger.info("Discarded attributes {} in tag {}", Arrays.toString(attributeNames), tagName);
            }
        }, null);
        logger.info("End sanitizing HTML input.");
        return result;
    }
}
