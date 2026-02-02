package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public abstract class BaseUploadData {

    private final String title;
    private final String originalFileName;
    private final String mimeType;
    private final InputStream inputStream;
    private final Employee author;
    private final Organization organization;
    private final String description;
    private final List<Long> categoryIds;

    public BaseUploadData(
        String title,
        String originalFileName,
        String mimeType,
        InputStream inputStream,
        Employee author,
        String description,
        Organization organization,
        List<Long> categoryIds
    ) {
        this.title = Objects.requireNonNull(title);
        this.originalFileName = originalFileName;
        this.mimeType = mimeType;
        this.inputStream = inputStream;
        this.author = author;
        this.description = description;
        this.organization = organization;
        this.categoryIds = categoryIds;
    }

    public BaseUploadData(
        MultipartFile doc,
        String customTitle,
        Employee author,
        String description,
        Organization organization,
        List<Long> categoryIds
    ) throws IOException {
        this(
            customTitle != null ? customTitle : doc.getOriginalFilename(),
            doc.getOriginalFilename(),
            doc.getContentType(),
            doc.getInputStream(),
            author,
            description,
            organization,
            categoryIds
        );
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Employee getAuthor() {
        return author;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }
}
