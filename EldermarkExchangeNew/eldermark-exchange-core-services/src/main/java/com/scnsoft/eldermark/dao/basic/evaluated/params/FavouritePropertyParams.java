package com.scnsoft.eldermark.dao.basic.evaluated.params;

public class FavouritePropertyParams extends EvaluatedPropertyParams {
    private final Long addedToFavouriteByEmployeeId;
    private final Class<?> entityClass;
    private final String addedAsFavouriteToEmployeeIdsAttr;

    public FavouritePropertyParams(Long addedToFavouriteByEmployeeId, Class<?> entityClass, String addedAsFavouriteToEmployeeIdsAttr) {
        this.addedToFavouriteByEmployeeId = addedToFavouriteByEmployeeId;
        this.entityClass = entityClass;
        this.addedAsFavouriteToEmployeeIdsAttr = addedAsFavouriteToEmployeeIdsAttr;
    }

    public Long getAddedToFavouriteByEmployeeId() {
        return addedToFavouriteByEmployeeId;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getAddedAsFavouriteToEmployeeIdsAttr() {
        return addedAsFavouriteToEmployeeIdsAttr;
    }
}
