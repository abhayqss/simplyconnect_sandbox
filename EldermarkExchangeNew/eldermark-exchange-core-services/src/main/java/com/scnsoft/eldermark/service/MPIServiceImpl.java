package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MPIServiceImpl implements MPIService {

    @Autowired
    private MPIDao mpiDao;

    @Value("${home.community.id}")
    private String assigningAuthorityId;

    @Value("${home.community.id.namespace}")
    private String assigningAuthorityNamespace;

    @Override
    @Transactional(readOnly = true)
    public boolean existsMPI(Client client) {
        return client.getId() != null && mpiDao.existsByClientId(client.getId());
    }

    @Override
    public MPI createMPI(Client client) {
        MPI personIdentifier = new MPI();

        personIdentifier.setMerged("N");
        personIdentifier.setSurvivingPatientId(null);
        personIdentifier.setDeleted("N");
        personIdentifier.setPatientId(client.getId().toString());
        personIdentifier.setClient(client);
        personIdentifier.setAssigningAuthorityUniversalType("ISO");
        personIdentifier.setAssigningAuthorityUniversal(assigningAuthorityId);
        personIdentifier.setAssigningAuthorityNamespace(assigningAuthorityNamespace);
        personIdentifier.setAssigningAuthority(assigningAuthorityNamespace + "&" + assigningAuthorityId + "&ISO");

        return mpiDao.save(personIdentifier);
    }
}