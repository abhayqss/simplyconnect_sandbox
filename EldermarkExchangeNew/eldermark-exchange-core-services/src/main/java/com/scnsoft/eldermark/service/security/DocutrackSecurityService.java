package com.scnsoft.eldermark.service.security;

public interface DocutrackSecurityService {

    boolean canConfigureDocutrackInOrg(Long organizationId);

    boolean canConfigureDocutrackInAnyOrg();

}
