package com.scnsoft.eldermark.dao;

public interface MPIUnmergedResidentsDao {
    void insert(Long residentId, Long residentId2);
    boolean exists(Long residentId, Long residentId2);
}
