package com.scnsoft.eldermark.services.carecoordination;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by averazub on 5/20/2016.
 */
public interface FileService {
    public String uploadOrganizationLogo(Long organizationId, MultipartFile file);
    public void deleteOrganizationLogo(Long organizationId);
    public String uploadCommunityLogo(Long communityId, MultipartFile file);
    public void deleteCommunityLogo(Long communityId);

}
