package com.scnsoft.eldermark.mobile.dto.signature;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory_;
import org.springframework.data.domain.Sort;

public class DocumentSignatureHistoryDto {

    private Long requestId;

    @EntitySort(DocumentSignatureHistory_.ACTION)
    private String actionName;
    private String actionTitle;

    @EntitySort.List({
            @EntitySort(DocumentSignatureHistory_.ACTOR_FIRST_NAME),
            @EntitySort(DocumentSignatureHistory_.ACTOR_LAST_NAME)
    })
    private String source;
    @EntitySort(joined = {DocumentSignatureHistory_.ACTOR_ROLE, CareTeamRole_.NAME})
    private String roleName;
    private String roleTitle;

    @DefaultSort(direction = Sort.Direction.DESC)
    private Long date;
    private String comments;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
