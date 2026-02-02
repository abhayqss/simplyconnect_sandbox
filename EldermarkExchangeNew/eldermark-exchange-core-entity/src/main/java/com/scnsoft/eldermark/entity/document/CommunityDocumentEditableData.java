package com.scnsoft.eldermark.entity.document;

import java.util.List;

public class CommunityDocumentEditableData extends DocumentEditableData {

    private Long parentId;
    private Long communityId;


    public CommunityDocumentEditableData(Long id, String title, String description, List<Long> categoryIds, Long parentId, Long communityId) {
        super(id, title, description, categoryIds);
        this.parentId = parentId;
        this.communityId = communityId;
    }

    public Long getParentId() {
        return parentId;
    }

    public Long getCommunityId() {
        return communityId;
    }
}
