package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SavedMarketplace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedMarketplaceDao extends JpaRepository<SavedMarketplace, SavedMarketplace.Id> {
}
