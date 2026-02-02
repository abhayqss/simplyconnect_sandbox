package com.scnsoft.eldermark.shared.phr;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author phomal
 * Created on 05/24/2017.
 */
public class SectionUpdateRequestVO {
    private String creator;
    private String target;
    private String section;
    private String comment;
    private String type;
    private String toEmail;
    private List<MultipartFile> attachments;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }
}
