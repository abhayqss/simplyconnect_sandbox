package com.scnsoft.eldermark.validation;

import javax.validation.groups.Default;

public final class ValidationGroups {
    private ValidationGroups() {
    }

    public interface Create extends Default {};
    public interface Update extends Default {};
    public interface OrganizationFeatures extends Default {}
    public interface CommunitySignatureConfig extends Default {}
}
