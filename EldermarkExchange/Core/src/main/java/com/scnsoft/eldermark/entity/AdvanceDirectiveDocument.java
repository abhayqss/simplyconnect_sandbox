package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "AdvanceDirectiveDocument")
public class AdvanceDirectiveDocument extends BasicEntity {
    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advance_directive_id", nullable = false)
    private AdvanceDirective advanceDirective;

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AdvanceDirective getAdvanceDirective() {
        return advanceDirective;
    }

    public void setAdvanceDirective(AdvanceDirective advanceDirective) {
        this.advanceDirective = advanceDirective;
    }
}
