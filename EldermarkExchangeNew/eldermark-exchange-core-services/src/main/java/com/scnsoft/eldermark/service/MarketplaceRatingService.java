package com.scnsoft.eldermark.service;

import java.util.Optional;

public interface MarketplaceRatingService {

    Optional<Integer> getRatingByName(String name);
}
