package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;

import java.util.List;

public interface ReportSecurityService {

    boolean canGenerate();

    boolean canGenerateForCommunities(ReportType reportType, List<Long> communityIds);
}
