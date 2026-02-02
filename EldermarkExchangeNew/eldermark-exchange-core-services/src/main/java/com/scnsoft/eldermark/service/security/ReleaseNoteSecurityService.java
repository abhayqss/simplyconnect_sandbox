package com.scnsoft.eldermark.service.security;

public interface ReleaseNoteSecurityService {
    boolean canView();

    boolean canUpload();

    boolean canDelete();
}
