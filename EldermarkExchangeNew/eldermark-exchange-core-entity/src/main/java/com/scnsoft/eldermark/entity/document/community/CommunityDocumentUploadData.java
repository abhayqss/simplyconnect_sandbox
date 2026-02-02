package com.scnsoft.eldermark.entity.document.community;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.BaseUploadData;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class CommunityDocumentUploadData extends BaseUploadData {

    private final Community community;
    private final DocumentFolder folder;

    public CommunityDocumentUploadData(
        MultipartFile doc,
        String customTitle,
        Employee author,
        Community community,
        DocumentFolder folder,
        String description,
        List<Long> categoryIds
    ) throws IOException {
        super(doc, customTitle, author, description, community.getOrganization(), categoryIds);
        this.community = community;
        this.folder = folder;
    }

    public Community getCommunity() {
        return community;
    }

    public DocumentFolder getFolder() {
        return folder;
    }
}
