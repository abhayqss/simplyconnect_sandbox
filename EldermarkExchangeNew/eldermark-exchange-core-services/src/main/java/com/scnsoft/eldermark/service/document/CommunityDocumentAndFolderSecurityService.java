package com.scnsoft.eldermark.service.document;

import java.util.List;

public interface CommunityDocumentAndFolderSecurityService {

    boolean canDownloadAll(List<String> ids);

    boolean canViewList();
}
