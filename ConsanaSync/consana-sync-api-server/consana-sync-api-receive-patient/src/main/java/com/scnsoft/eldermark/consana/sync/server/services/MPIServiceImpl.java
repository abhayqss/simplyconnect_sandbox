package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.MPIDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.MPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(noRollbackFor = Exception.class)
public class MPIServiceImpl implements MPIService {

    @Autowired
    private MPIDao mpiDao;

    @Override
    public List<MPI> getAllByResidentId(Long residentId) {
        return mpiDao.getAllByResident_Id(residentId);
    }
}
