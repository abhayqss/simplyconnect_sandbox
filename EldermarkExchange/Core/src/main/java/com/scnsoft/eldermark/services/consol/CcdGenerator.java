package com.scnsoft.eldermark.services.consol;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.DocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

// TODO move to another package?
@Component("CcdGenerator")
public class CcdGenerator implements ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CcdGenerator.class);

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CdaFacade cdaFacade;

    @Override
    @Transactional
    public Report generate(Long residentId) {
        return generate(residentId, false);
    }

    @Override
    @Transactional
    public Report generate(Long residentId, boolean aggregated, Integer timeZoneOffsetInMinutes) {
        //time zone offset is not used in CCD generation
        return generate(residentId, aggregated);
    }

    @Override
    public Report generate(Long residentId, List<Long> residentIds, Integer timeZoneOffsetInMinutes) {
        //time zone offset is not used in CCD generation
        return generate(residentId, residentIds);
    }

    @Override
    @Transactional
    public Report generate(Long residentId, List<Long> residentIds) {
        try {
            boolean inHtml = false;
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
            cdaFacade.exportXml(buffer, residentId, com.scnsoft.eldermark.cda.service.schema.DocumentType.CCDA_R1_1_CCD_V1, residentIds);

            final Resident resident = residentService.getResident(residentId);
            return createReportFromStream(resident, buffer, inHtml ? "text/html" : "text/xml");
        } catch (Exception e) {
            logger.error("CCD generation failed for " + residentId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Report generate(Long residentId, boolean aggregated) {
        try {
            boolean inHtml = false;
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
            if (inHtml)
                cdaFacade.exportHtml(buffer, residentId, com.scnsoft.eldermark.cda.service.schema.DocumentType.CCDA_R1_1_CCD_V1, aggregated);
            else
                cdaFacade.exportXml(buffer, residentId, com.scnsoft.eldermark.cda.service.schema.DocumentType.CCDA_R1_1_CCD_V1, aggregated);

            final Resident resident = residentService.getResident(residentId);
            return createReportFromStream(resident, buffer, inHtml ? "text/html" : "text/xml");
        } catch (Exception e) {
            logger.error("CCD generation failed for " + residentId, e);
            throw new RuntimeException(e);
        }
    }

    private static Report createReportFromStream(Resident resident, ByteArrayOutputStream buffer, String s) {
        String documentName = String.format("ccd_%s_%s.xml", resident.getFirstName(), resident.getLastName());

        Report document = new Report();
        document.setDocumentTitle(documentName);
        document.setMimeType(s);
        document.setDocumentType(DocumentType.CCD);
        document.setInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        return document;
    }

    @Override
    public Report metadata() {
        Report document = new Report();
        document.setDocumentTitle("CCD.XML");
        document.setMimeType("text/xml");
        document.setDocumentType(DocumentType.CCD);
        return document;
    }

}