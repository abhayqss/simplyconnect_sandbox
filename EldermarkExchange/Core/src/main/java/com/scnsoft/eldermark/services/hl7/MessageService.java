package com.scnsoft.eldermark.services.hl7;

import com.scnsoft.eldermark.entity.hl7.Hl7Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private ParsingService parsingService;

    @Autowired
    private PdfService pdfService;

    public void processIncomingMessage(String message) {
        try {
            Hl7Message hl7Message = parsingService.parseMessage(message);
            //pdfService.constructPdf(hl7Message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
