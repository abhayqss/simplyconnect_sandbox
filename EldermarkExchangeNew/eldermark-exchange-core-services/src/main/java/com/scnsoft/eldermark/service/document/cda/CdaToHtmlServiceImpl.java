package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.exception.CdaTransformationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class CdaToHtmlServiceImpl implements CdaToHtmlService {

    private static final Logger logger = LoggerFactory.getLogger(CdaToHtmlServiceImpl.class);

    @Autowired
    private Templates lantanaTransformationTemplate;

    @Override
    public void cdaToHtml(InputStream cda, OutputStream target) {
        try {
            var transformer = lantanaTransformationTemplate.newTransformer();
            transformer.transform(new StreamSource(cda), new StreamResult(target));
        } catch (TransformerException e) {
            logger.warn("Error during cda -(xsl)-> html transformation", e);
            throw new CdaTransformationException(e);
        }
    }
}
