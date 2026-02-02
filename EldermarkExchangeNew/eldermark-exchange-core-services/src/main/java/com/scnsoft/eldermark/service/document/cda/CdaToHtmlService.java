package com.scnsoft.eldermark.service.document.cda;

import java.io.InputStream;
import java.io.OutputStream;

public interface CdaToHtmlService {

    void cdaToHtml(InputStream cda, OutputStream target);

}
