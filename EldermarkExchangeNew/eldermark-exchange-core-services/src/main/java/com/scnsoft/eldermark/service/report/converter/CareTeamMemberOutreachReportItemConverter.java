package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.security.projection.CommunityCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.entity.client.report.CareTeamMemberOutreachReportItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CareTeamMemberOutreachReportItemConverter implements Converter<CommunityCareTeamMemberOutreachReportDetailsAware, CareTeamMemberOutreachReportItem> {
    @Override
    public CareTeamMemberOutreachReportItem convert(CommunityCareTeamMemberOutreachReportDetailsAware source) {
        var target = new CareTeamMemberOutreachReportItem();
        target.setRoleName(source.getCareTeamRoleName());
        target.setFullName(source.getEmployeeFullName());
        target.setFirstName(source.getEmployeeFirstName());
        target.setLastName(source.getEmployeeLastName());
        return target;
    }
}
