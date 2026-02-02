package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Document")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "res_db_alt_id", nullable = false)
    private String clientOrganizationAlternativeId;

    @Column(name = "author_db_alt_id", nullable = false)
    private String authorOrganizationAlternativeId;

    @Column(name = "res_legacy_id", nullable = false)
    private String clientLegacyId;

    @Column(name = "author_legacy_id", nullable = false)
    private String authorLegacyId;

    @Column(name = "document_title", nullable = false, columnDefinition = "nvarchar(255)")
    private String documentTitle;

    @Column(name = "original_file_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String originalFileName;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "creation_time", nullable = false)
    private Instant creationTime;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "hash_sum", nullable = false)
    private String hash;

    @Column(name = "eldermark_shared", nullable = false)
    private boolean eldermarkShared;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "consana_map_id")
    private String consanaMapId;
}
