package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.entity.community.CommunityPicture;
import org.springframework.stereotype.Component;

@Component
public class CommunityPictureEntityConverter extends BaseAttachmentEntityConverter<CommunityPicture> {

    @Override
    protected CommunityPicture getAttachment() {
        return new CommunityPicture();
    }
}