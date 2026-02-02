package com.scnsoft.eldermark.projection;

import com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.FavouritePropertyProcessor;

public interface IsFavouriteEvaluatedAware {
    String IS_FAVOURITE_PROPERTY_NAME = "isFavourite";

    @EvaluatedProperty(FavouritePropertyProcessor.class)
    Boolean getIsFavourite();
}
