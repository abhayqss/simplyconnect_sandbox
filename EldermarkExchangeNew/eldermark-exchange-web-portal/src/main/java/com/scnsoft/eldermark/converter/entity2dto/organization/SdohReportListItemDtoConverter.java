package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.SDoHReportListItemDto;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.service.security.SDoHSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.scnsoft.eldermark.dto.SDoHReportStatus.PENDING_REVIEW;
import static com.scnsoft.eldermark.dto.SDoHReportStatus.SENT_TO_UHC;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SdohReportListItemDtoConverter implements Converter<SdohReportLog, SDoHReportListItemDto> {

    @Autowired
    private SDoHSecurityService sDoHSecurityService;

    @Override
    public SDoHReportListItemDto convert(SdohReportLog reportLog) {
        var result = new SDoHReportListItemDto();

        result.setId(reportLog.getId());
        result.setPeriodStart(DateTimeUtils.toEpochMilli(reportLog.getPeriodStart()));
        result.setPeriodEnd(DateTimeUtils.toEpochMilli(reportLog.getPeriodEnd()));

        var status = reportLog.getSentToUhcDatetime() == null ? PENDING_REVIEW : SENT_TO_UHC;
        result.setStatusName(status.name());
        result.setStatusTitle(status.getDisplayName());

        result.setCanDownloadExcel(sDoHSecurityService.canDownloadExcel(reportLog.getId()));
        result.setCanDownloadZip(sDoHSecurityService.canDownloadZip(reportLog.getId()));
        result.setCanMarkAsSent(sDoHSecurityService.canMarkAsSent(reportLog.getId()));

        return result;
    }

}
