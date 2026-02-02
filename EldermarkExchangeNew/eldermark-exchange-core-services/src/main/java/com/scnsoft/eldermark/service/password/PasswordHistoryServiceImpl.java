package com.scnsoft.eldermark.service.password;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.scnsoft.eldermark.dao.password.OrganizationPasswordSettingsDao;
import com.scnsoft.eldermark.dao.password.PasswordHistoryDao;
import com.scnsoft.eldermark.dao.specification.OrganizationPasswordSettingSpecification;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.PasswordHistory;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    @Autowired
    private PasswordHistoryDao passwordHistoryDao;

    @Autowired
    private OrganizationPasswordSettingSpecification orgPasswordSettingSpecification;

    @Autowired
    private OrganizationPasswordSettingsDao orgPasswordSettingsDao;

    @Override
    @Transactional
    public void enablePasswordHistory(Long databaseId) {
        passwordHistoryDao.enablePasswordHistory(databaseId);
    }

    @Override
    @Transactional
    public void addCurrentPasswordToHistoryIfEnabled(Employee employee) {
        var byOrganizationIdAndType = orgPasswordSettingSpecification.byOrganizationIdAndType(employee.getOrganizationId(),
                PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT);

        orgPasswordSettingsDao.findOne(byOrganizationIdAndType)
                .filter(databasePasswordSettings -> databasePasswordSettings.getEnabled() && databasePasswordSettings.getValue() > 0)
                .ifPresent(organizationPasswordSettings -> {
                    PasswordHistory passwordHistory = new PasswordHistory();
                    passwordHistory.setEmployee(employee);
                    passwordHistory.setPassword(employee.getPassword());
                    passwordHistoryDao.save(passwordHistory);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordHistory> findAllByEmployeeId(Long employeeId) {
        return passwordHistoryDao.findAllByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordHistory> findLatestByEmployeeId(Long employeeId, int count) {
        return passwordHistoryDao.findAllByEmployeeIdOrderByIdDesc(employeeId, PageRequest.of(0, count));
    }
}
