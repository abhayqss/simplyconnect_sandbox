package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.entity.SystemSetup;

/**
 * Created by averazub on 3/21/2016.
 */
public class OrganizationFilterDto {
    private Long id;
    private String name;
    private Boolean isService;
    private boolean isEldermark;
    private String loginCompanyId;

    public OrganizationFilterDto(Long id, String name, Boolean isService, boolean isEldermark, String loginCompanyId) {
        this.id = id;
        this.name = name;
        this.isService = isService;
        this.isEldermark = isEldermark;
        this.loginCompanyId = loginCompanyId;
    }

    public OrganizationFilterDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsService() {
        return isService;
    }

    public void setIsService(Boolean isService) {
        this.isService = isService;
    }

    public boolean isEldermark() {
        return isEldermark;
    }

    public void setIsEldermark(boolean isEldermark) {
        this.isEldermark = isEldermark;
    }

    public String getLoginCompanyId() {
        return loginCompanyId;
    }

    public void setLoginCompanyId(String loginCompanyId) {
        this.loginCompanyId = loginCompanyId;
    }
}
