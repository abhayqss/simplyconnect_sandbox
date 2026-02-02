package com.scnsoft.eldermark.service.pointclickcare;

public interface PointClickCareSyncService {

    void syncCommunity(Long communityId);

    void updateNewPatients(Long communityId);
}
