package com.scnsoft.eldermark.services.fax;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;

import java.io.IOException;

public interface FaxContentGenerator<T> {

    byte[] generateFaxContent(FaxDto faxdto, T dto) throws DocumentException, IOException;

}
