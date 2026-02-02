package com.scnsoft.eldermark.dto;

public class ReleaseNoteNotificationDto {

    private Long id;
    private String whatsNew;
    private String bugFixes;

    public ReleaseNoteNotificationDto(Long id, String whatsNew, String bugFixes) {
        this.id = id;
        this.whatsNew = whatsNew;
        this.bugFixes = bugFixes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getBugFixes() {
        return bugFixes;
    }

    public void setBugFixes(String bugFixes) {
        this.bugFixes = bugFixes;
    }
}
