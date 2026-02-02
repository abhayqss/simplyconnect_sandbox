package com.scnsoft.eldermark.dto.notes;

public class NoteHistoryListItemDto {

    private Long id;
    private String statusName;
    private String statusTitle;
    private Long modifiedDate;
    private String author;
    private String authorRoleTitle;
    private Boolean archived;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
