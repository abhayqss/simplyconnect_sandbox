package com.scnsoft.eldermark.service;

public interface SavedMarketplaceService {

    void save(Long employeeId, Long marketplaceId);

    void remove(Long employeeId, Long marketplaceId);

    boolean isExists(Long employeeId, Long marketplaceId);
}
