package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.ReleaseNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service("releaseNoteSecurityService")
public class ReleaseNoteSecurityServiceImpl extends BaseSecurityService implements ReleaseNoteSecurityService {

    @Autowired
    private ReleaseNoteService releaseNoteService;

    @Override
    public boolean canView() {
        return releaseNoteService.canViewList(currentUserFilter());
    }

    @Override
    public boolean canUpload() {
        return hasAnyPermission(Collections.singleton(Permission.ROLE_SUPER_ADMINISTRATOR));
    }

    @Override
    public boolean canDelete() {
        return hasAnyPermission(Collections.singleton(Permission.ROLE_SUPER_ADMINISTRATOR));
    }
}
