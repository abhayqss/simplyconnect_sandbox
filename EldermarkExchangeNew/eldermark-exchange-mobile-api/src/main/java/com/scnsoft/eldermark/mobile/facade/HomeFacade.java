package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.mobile.dto.home.HomeSectionType;
import com.scnsoft.eldermark.mobile.dto.home.HomeSectionsDto;

import java.util.Set;

public interface HomeFacade {

    HomeSectionsDto getSections(Set<HomeSectionType> sectionTypes);

    void readCareTeamMemberUpdates(Long careTeamMemberId);
}
