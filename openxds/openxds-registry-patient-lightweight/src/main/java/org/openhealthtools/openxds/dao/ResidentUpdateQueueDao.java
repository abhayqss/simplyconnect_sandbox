package org.openhealthtools.openxds.dao;

public interface ResidentUpdateQueueDao {

    void pushResidentUpdate(Long residentId);

    void pushResidentMerge(Long residentId1, Long residentId2);
}
