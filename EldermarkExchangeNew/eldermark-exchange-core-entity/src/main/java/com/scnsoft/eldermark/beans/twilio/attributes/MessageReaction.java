package com.scnsoft.eldermark.beans.twilio.attributes;

import java.util.Objects;

public class MessageReaction {
    private String authorIdentity;
    private Long id;

    public MessageReaction() {
    }

    public MessageReaction(String authorIdentity, Long id) {
        this.authorIdentity = authorIdentity;
        this.id = id;
    }

    public String getAuthorIdentity() {
        return authorIdentity;
    }

    public void setAuthorIdentity(String authorIdentity) {
        this.authorIdentity = authorIdentity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageReaction that = (MessageReaction) o;
        return Objects.equals(authorIdentity, that.authorIdentity) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorIdentity, id);
    }
}
