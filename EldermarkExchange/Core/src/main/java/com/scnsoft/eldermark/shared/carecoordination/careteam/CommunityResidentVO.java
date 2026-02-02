package com.scnsoft.eldermark.shared.carecoordination.careteam;

public class CommunityResidentVO {
    private Long communityId;
    private Long residentId;


    public CommunityResidentVO(Long communityId, Long residentId) {
        this.residentId = residentId;
        this.communityId = communityId;
    }

    public Long getResidentId() {
        return residentId;
    }

    public Long getCommunityId() {
        return communityId;
    }

}
