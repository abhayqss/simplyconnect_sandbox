package com.scnsoft.eldermark.dto.notes;

public class RelatedNoteListItemDto {
    
    private Long id;
    private String author;
    private String authorRoleTitle;
    private Long date;
    private String statusName;
    private String statusTitle;
    private String subTypeTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorRoleTitle() {
        return authorRoleTitle;
    }

    public void setAuthorRoleTitle(String authorRoleTitle) {
        this.authorRoleTitle = authorRoleTitle;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
    }
}