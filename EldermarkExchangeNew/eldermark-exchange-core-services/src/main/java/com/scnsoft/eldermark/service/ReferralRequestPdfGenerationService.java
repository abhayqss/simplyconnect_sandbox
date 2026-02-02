package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;

import java.io.IOException;
import java.time.ZoneId;

public interface ReferralRequestPdfGenerationService {

    DocumentReport generatePdfReport(ReferralRequest referralRequest, ZoneId zoneId) throws DocumentException, IOException;
}
