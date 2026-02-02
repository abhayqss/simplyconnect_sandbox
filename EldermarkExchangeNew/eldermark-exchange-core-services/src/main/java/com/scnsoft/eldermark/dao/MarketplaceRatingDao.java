package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MarketplaceRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketplaceRatingDao extends JpaRepository<MarketplaceRating, Long> {

    Optional<MarketplaceRating> findFirstByProviderName(String name);

    void deleteAllByIsManualIs(boolean isManual);
}
