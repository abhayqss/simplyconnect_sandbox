package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import org.springframework.stereotype.Service;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.HUD_MFSC;

@Service
public class HudMfscWorkbookGenerator extends BaseHudWorkbookGenerator {

    @Override
    public ReportType generatedReportType() {
        return HUD_MFSC;
    }

}
