package com.scnsoft.eldermark.consana.sync.server.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadQueueDto {
    private String title;
    private String originalFileName;
    private String mimeType;
    private byte[] data;
    private Long clientId;
    private Long authorId;
    private String sharingOption;
    private String consanaMapId;
}
