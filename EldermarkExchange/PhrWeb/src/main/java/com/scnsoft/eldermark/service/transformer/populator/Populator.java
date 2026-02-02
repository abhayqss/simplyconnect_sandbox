package com.scnsoft.eldermark.service.transformer.populator;

/**
 * is used to populate target object from source
 * @param <S>
 * @param <T>
 */
public interface Populator<S, T> {
    /**
     *
     * @param src source object with required information for populating
     * @param target object to be populated
     */
    void populate(S src, T target);
}
