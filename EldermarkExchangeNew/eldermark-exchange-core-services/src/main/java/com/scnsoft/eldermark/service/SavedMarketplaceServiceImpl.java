package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.SavedMarketplaceDao;
import com.scnsoft.eldermark.entity.SavedMarketplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedMarketplaceServiceImpl implements SavedMarketplaceService {

    @Autowired
    private SavedMarketplaceDao savedMarketplaceDao;

    @Override
    public void save(Long employeeId, Long marketplaceId) {
        var id = createSavedMarketplaceId(employeeId, marketplaceId);
        var marketplace = new SavedMarketplace();
        marketplace.setId(id);
        savedMarketplaceDao.save(marketplace);
    }

    @Override
    public void remove(Long employeeId, Long marketplaceId) {
        var id = createSavedMarketplaceId(employeeId, marketplaceId);
        savedMarketplaceDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(Long employeeId, Long marketplaceId) {
        var id = createSavedMarketplaceId(employeeId, marketplaceId);
        return savedMarketplaceDao.existsById(id);
    }

    private SavedMarketplace.Id createSavedMarketplaceId(Long employeeId, Long marketplaceId) {
        var id = new SavedMarketplace.Id();
        id.setEmployeeId(employeeId);
        id.setMarketplaceId(marketplaceId);
        return id;
    }


}
