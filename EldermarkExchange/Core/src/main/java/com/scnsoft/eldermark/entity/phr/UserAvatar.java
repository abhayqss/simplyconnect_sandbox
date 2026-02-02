package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;

@Entity
@Table(name = "UserAvatar")
public class UserAvatar extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", nullable = false, unique = true, updatable = false, insertable = false)
    private Long userId;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "`file`", nullable = false)
    private byte[] file;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
