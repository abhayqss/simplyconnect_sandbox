package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.BirthDateAware;

public interface ProspectListItemFieldsAware extends IdAware, NamesAvatarIdAware, BirthDateAware, CommunityNameAware, CreatedDateAware, ActiveAware {
    String getGenderDisplayName();
}
