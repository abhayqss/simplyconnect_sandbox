package com.scnsoft.eldermark.mobile.dto.careteam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.community.Community_;

public class CareTeamMemberListItemDto extends BaseCareTeamMemberDto<CareTeamContactListItem> {

    //properties needed for sorting
    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.COMMUNITY, Community_.NAME})
    @JsonIgnore
    private final String contactCommunityName = null;

    @DefaultSort
    @EntitySort.List(
            {
                    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {CareTeamMember_.EMPLOYEE, Employee_.LAST_NAME})
            }
    )
    @JsonIgnore
    private final String contactFullName = null;

    public String getContactCommunityName() {
        return contactCommunityName;
    }

    public String getContactFullName() {
        return contactFullName;
    }

}
