package com.scnsoft.eldermark.converter.fax;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.scnsoft.eldermark.dto.notification.lab.LabEventFaxNotificationDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class LabEventFaxContentConverter extends AbstractITextPdfFaxContentConverter<LabEventFaxNotificationDto> {

    @Override
    protected void createDocumentBody(Document document, LabEventFaxNotificationDto faxDto) throws DocumentException, IOException {

        final float contentTableIndentationAfter = 12f;
        document.add(createHeaderTable());

        addIndentedTable(document, createFaxDetailsTable(faxDto), CONTENT_INDENTATION, 15f);
        document.add(new LineSeparator());


        //todo investigate merging lab order documents content
    }
}
