package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.MpiMergedClientsDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MpiMergedClients;
import com.scnsoft.eldermark.entity.MpiMergedClients_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MpiMergedClientsServiceImpl implements MpiMergedClientsService {

    @Autowired
    private MpiMergedClientsDao mpiMergedClientsDao;

    @Override
    public MpiMergedClients mergeClients(Client client1, Client client2) {
        return mergeClients(client1, client2, null);
    }

    @Override
    public MpiMergedClients mergeClients(Client client1, Client client2, Double confidence) {
        return findExistingMpiMerged(client1, client2).map(mpiMerged -> {
            if (!mpiMerged.isMerged()) {
                mpiMerged.setMerged(true);
                if (confidence != null) {
                    mpiMerged.setDukeConfidence(confidence);
                }
                return mpiMergedClientsDao.save(mpiMerged);
            }
            return mpiMerged;
        }).orElseGet(() -> {
            var mpiMerged = new MpiMergedClients();

            mpiMerged.setSurvivingClient(client1);
            mpiMerged.setSurvivingClientId(client1.getId());

            mpiMerged.setMergedClient(client2);
            mpiMerged.setMergedClientId(client2.getId());

            mpiMerged.setDukeConfidence(confidence);

            mpiMerged.setMerged(true);

            return mpiMergedClientsDao.save(mpiMerged);
        });
    }

    private Optional<MpiMergedClients> findExistingMpiMerged(Client client1, Client client2) {
        return mpiMergedClientsDao.findFirst((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get(MpiMergedClients_.survivingClientId), client1.getId()),
                                    criteriaBuilder.equal(root.get(MpiMergedClients_.mergedClientId), client2.getId())
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get(MpiMergedClients_.survivingClientId), client2.getId()),
                                    criteriaBuilder.equal(root.get(MpiMergedClients_.mergedClientId), client1.getId())
                            )
                    ), MpiMergedClients.class);
    }
}
