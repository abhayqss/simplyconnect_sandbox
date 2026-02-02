package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.security.projection.ClientCareTeamMemberOutreachReportDetailsAware;
import com.scnsoft.eldermark.entity.client.report.CareTeamMemberOutreachReportItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientCareTeamMemberOutreachReportItemConverter implements Converter<ClientCareTeamMemberOutreachReportDetailsAware, CareTeamMemberOutreachReportItem> {

    @Override
    public CareTeamMemberOutreachReportItem convert(ClientCareTeamMemberOutreachReportDetailsAware source) {
        var target = new CareTeamMemberOutreachReportItem();
        target.setRoleName(source.getCareTeamRoleName());
        target.setFullName(source.getEmployeeFullName());
        target.setFirstName(source.getEmployeeFirstName());
        target.setLastName(source.getEmployeeLastName());
        return target;
    }
}
