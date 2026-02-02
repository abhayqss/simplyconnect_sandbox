package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.MarketplaceRatingDao;
import com.scnsoft.eldermark.entity.MarketplaceRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MarketplaceRatingServiceImpl implements MarketplaceRatingService {

    public static final String RATING_SERVICE_TYPE_KEY = "SKILLED_NURSING";

    @Autowired
    private MarketplaceRatingDao marketplaceRatingDao;

    @Override
    @Transactional(readOnly = true)
    public Optional<Integer> getRatingByName(String name) {
        return marketplaceRatingDao.findFirstByProviderName(name).map(MarketplaceRating::getOverallRating);
    }
}
