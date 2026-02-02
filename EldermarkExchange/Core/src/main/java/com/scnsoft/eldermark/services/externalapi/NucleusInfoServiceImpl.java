package com.scnsoft.eldermark.services.externalapi;

import com.scnsoft.eldermark.dao.externalapi.NucleusInfoDao;
import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author phomal
 * Created on 3/2/2018.
 */
@Service
public class NucleusInfoServiceImpl implements NucleusInfoService {
    private final NucleusInfoDao nucleusInfoDao;

    @Value("${nucleus.enabled}")
    private Boolean nucleusIntegrationEnabled;

    @Value("${nucleus.polling.auth.token}")
    private String nucleusPollingAuthToken;

    @Value("${nucleus.auth.token}")
    private String nucleusAuthToken;

    @Value("${nucleus.service.host}")
    private String nucleusHost;

    @Autowired
    public NucleusInfoServiceImpl(NucleusInfoDao nucleusInfoDao) {
        this.nucleusInfoDao = nucleusInfoDao;
    }

    @Override
    public String findByEmployeeId(Long employeeId) {
        final NucleusInfo info = nucleusInfoDao.findOneByEmployeeId(employeeId);
        return info == null ? null : info.getNucleusUserId();
    }

    @Override
    public String findByResidentId(Long residentId) {
        final NucleusInfo info = nucleusInfoDao.findOneByResidentId(residentId);
        return info == null ? null : info.getNucleusUserId();
    }

    @Override
    public List<NucleusInfo> findByResidentIds(Collection<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }
        return nucleusInfoDao.findAllByResidentIdIn(residentIds);
    }

    @Override
    public List<Long> findResidentIdsByNucleusId(String nucleusId) {
        return nucleusInfoDao.findResidentIdsByNucleusId(nucleusId);
    }

    @Override
    public List<Long> findEmployeeIdsByNucleusId(String nucleusId) {
        return nucleusInfoDao.findEmployeeIdsByNucleusId(nucleusId);
    }

    @Override
    public boolean isNucleusIntegrationEnabled() {
        return Boolean.TRUE.equals(nucleusIntegrationEnabled);
    }

    @Override
    public String getNucleusPollingAuthToken() {
        return nucleusPollingAuthToken;
    }

    @Override
    public String getNucleusAuthToken() {
        return nucleusAuthToken;
    }

    @Override
    public String getNucleusHost() {
        return nucleusHost;
    }

}


