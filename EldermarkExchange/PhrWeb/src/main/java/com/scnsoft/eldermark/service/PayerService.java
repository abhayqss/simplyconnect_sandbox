package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.PolicyActivityDao;
import com.scnsoft.eldermark.entity.Participant;
import com.scnsoft.eldermark.entity.PolicyActivity;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.web.entity.PayerInfoDto;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.scnsoft.eldermark.dao.healthdata.PolicyActivityDao.ORDER_BY_COVERAGE_PERIOD_START_DATE_DESC;
import static com.scnsoft.eldermark.dao.healthdata.PolicyActivityDao.ORDER_BY_ORG_NAME;

@Service
public class PayerService extends BasePhrService {

    @Autowired
    PolicyActivityDao policyActivityDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Transactional(readOnly = true)
    public List<PayerInfoDto> getUserPayers(Long userId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);

        final Sort sort = new Sort(ORDER_BY_COVERAGE_PERIOD_START_DATE_DESC, ORDER_BY_ORG_NAME);

        List<PayerInfoDto> result = new ArrayList<>();
        List<PolicyActivity> policyActivities = policyActivityDao.listResidentPolicyActivitiesWithoutDuplicates(activeResidentIds, sort);
        for (PolicyActivity policyActivity : policyActivities) {
            result.add(transform(policyActivity));
        }

        return result;
    }

    private static PayerInfoDto transform(PolicyActivity policyActivity) {
        PayerInfoDto payerInfo = new PayerInfoDto();
        PeriodDto period = new PeriodDto();
        payerInfo.setCoveragePeriod(period);
        Participant participant = policyActivity.getParticipant();
        if (participant != null) {
            if (participant.getTimeLow() != null) {
                period.setStartDate(participant.getTimeLow().getTime());
                period.setStartDateStr(DATE_TIME_FORMAT.format(participant.getTimeLow()));
            }
            if (participant.getTimeHigh() != null) {
                period.setEndDate(participant.getTimeHigh().getTime());
                period.setEndDateStr(DATE_TIME_FORMAT.format(participant.getTimeHigh()));
            }
        }
        payerInfo.setDataSource(DataSourceService.transform(policyActivity.getDatabase(), policyActivity.getPayer().getResidentId()));
        payerInfo.setCompanyName(policyActivity.getPayerOrganization() != null ? policyActivity.getPayerOrganization().getName() : null);
        payerInfo.setMemberId(policyActivity.getParticipantMemberId());

        return payerInfo;
    }
}
