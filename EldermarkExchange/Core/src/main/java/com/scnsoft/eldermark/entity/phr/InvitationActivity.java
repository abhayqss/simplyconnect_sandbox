package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;

/**
 * @author phomal
 * Created on 5/3/2017.
 */
@Entity
@DiscriminatorValue("INVITATION")
public class InvitationActivity extends Activity {

    /**
     * Invitation status
     */
    public enum Status {
        SENT("Sent"),
        ACCEPTED("Accepted"),
        REJECTED("Rejected");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return value;
        }

        @JsonCreator
        public static Status fromValue(String text) {
            for (Status b : Status.values()) {
                if (b.value.equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvitationActivity.Status status;

    public InvitationActivity.Status getStatus() {
        return status;
    }

    public void setStatus(InvitationActivity.Status status) {
        this.status = status;
    }

}
