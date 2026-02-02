package com.scnsoft.eldermark.service.security;

public interface UserManualSecurityService {

    boolean canView();

    boolean canUpload();

    boolean canDelete();
}
