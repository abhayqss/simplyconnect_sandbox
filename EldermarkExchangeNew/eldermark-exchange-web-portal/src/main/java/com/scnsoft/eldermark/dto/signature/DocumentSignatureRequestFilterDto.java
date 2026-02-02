package com.scnsoft.eldermark.dto.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.validation.SpELAssert;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@SpELAssert.List(
        @SpELAssert(
                value = "(communityId != null && organizationId == null) || (organizationId != null && communityId == null)",
                message = "communityId or organizationId shouldn't be null"
        )
)
public class DocumentSignatureRequestFilterDto {

    private Long communityId;

    private Long organizationId;

    @NotEmpty
    private List<DocumentSignatureStatus> statuses;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<DocumentSignatureStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<DocumentSignatureStatus> statuses) {
        this.statuses = statuses;
    }
}
