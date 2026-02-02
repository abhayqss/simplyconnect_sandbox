package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.ExternalEmployeeInboundReferralCommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.ExternalEmployeeInboundReferralCommunity;
import com.scnsoft.eldermark.entity.community.Community;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static com.scnsoft.eldermark.entity.CareTeamRoleCode.ROLE_EXTERNAL_USER;
import static com.scnsoft.eldermark.service.CareCoordinationConstants.EXTERNAL_COMPANY_ID;

@Service
@Transactional
public class ExternalEmployeeInboundReferralCommunityServiceImpl implements ExternalEmployeeInboundReferralCommunityService {

    @Autowired
    private ExternalEmployeeInboundReferralCommunityDao externalEmployeeInboundReferralCommunityDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private EmployeeDao employeeDao;

    @SuppressFBWarnings(value = "HARD_CODE_PASSWORD", justification = "It is not possible to login with this password")
    @Override
    public ExternalEmployeeInboundReferralCommunity create(String loginName, Community community) {
        var employee = new Employee();
        var org = organizationDao.findBySystemSetup_LoginCompanyId(EXTERNAL_COMPANY_ID);
        employee.setOrganization(org);
        employee.setOrganizationId(org.getId());
        employee.setLoginName(loginName);
        employee.setPassword("password");
        employee.setCompany(org.getSystemSetup().getLoginCompanyId());
        employee.setStatus(EmployeeStatus.PENDING);
        CareCoordinationConstants.setLegacyId(employee);
        employee.setCreatedAutomatically(false);
        employee.setModifiedTimestamp(Instant.now().toEpochMilli());
        employee.setFirstName("");
        employee.setLastName("");
        employee.setCommunity(communityDao.findByOrganizationId(org.getId()).get(0));
        employee.setCareTeamRole(careTeamRoleService.get(ROLE_EXTERNAL_USER));
        employee = employeeDao.save(employee);
        CareCoordinationConstants.updateLegacyId(employee);

        return create(employee, community);
    }

    @Override
    public ExternalEmployeeInboundReferralCommunity create(Employee employee, Community community) {
        var externalEmployeeCommunity = new ExternalEmployeeInboundReferralCommunity();
        externalEmployeeCommunity.setEmployee(employee);
        externalEmployeeCommunity.setCommunity(community);
        return externalEmployeeInboundReferralCommunityDao.save(externalEmployeeCommunity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExternalEmployeeInboundReferralCommunity> findAllByCommunityId(long communityId) {
        return externalEmployeeInboundReferralCommunityDao.findAllByCommunityId(communityId);
    }

    @Override
    public void deleteAll(Collection<ExternalEmployeeInboundReferralCommunity> externalEmployees) {
        externalEmployeeInboundReferralCommunityDao.deleteAll(externalEmployees);
    }

    @Override
    public boolean isCommunitySharedForAnyEmployee(Collection<Long> employeeIds, Long communityId) {
        return externalEmployeeInboundReferralCommunityDao.existsByEmployeeIdInAndCommunityId(employeeIds, communityId);
    }

    @Override
    public boolean isExternalEmployee(Employee employee) {
        var orgId = organizationDao.findBySystemSetup_LoginCompanyId(EXTERNAL_COMPANY_ID, IdAware.class).getId();
        return orgId.equals(employee.getOrganizationId());
    }
}
