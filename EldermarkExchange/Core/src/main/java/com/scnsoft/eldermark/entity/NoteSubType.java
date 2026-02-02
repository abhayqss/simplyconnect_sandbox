package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author sparuchnik
 */
@Immutable
@Entity
@Table(name = "NoteSubType")
public class NoteSubType implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum FollowUpCode {
        CM_24H("CM_24H"), CM_14D("CM_14D"), CM_ADDITIONAL("CM_ADDITIONAL");

        private String code;

        FollowUpCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static FollowUpCode getByCode(final String code) {
            for (FollowUpCode c : FollowUpCode.values()) {
                if (c.getCode().equals(code)) {
                    return c;
                }
            }
            return null;
        }
    }

    public enum EncounterCode {
        FACE_TO_FACE_ENCOUNTER("FACE_TO_FACE_ENCOUNTER"), NON_FACE_TO_FACE_ENCOUNTER("NON_FACE_TO_FACE_ENCOUNTER");

        private String code;

        EncounterCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static FollowUpCode getByCode(final String code) {
            for (FollowUpCode c : FollowUpCode.values()) {
                if (c.getCode().equals(code)) {
                    return c;
                }
            }
            return null;
        }
    }

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_up_code")
    private FollowUpCode followUpCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_code")
    private EncounterCode encounterCode;

    @Basic(optional = false)
    @Column(name = "position", nullable = false)
    private Long position;

    @Column(name = "hidden_phr", nullable = false)
    private boolean phrHidden;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "is_manual", nullable = false)
    private Boolean isManual;

    @Column(name = "allowed_for_group_note", nullable = false)
    private boolean allowedForGroupNote;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FollowUpCode getFollowUpCode() {
        return followUpCode;
    }

    public void setFollowUpCode(FollowUpCode followUpCode) {
        this.followUpCode = followUpCode;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public EncounterCode getEncounterCode() {
        return encounterCode;
    }

    public void setEncounterCode(EncounterCode encounterCode) {
        this.encounterCode = encounterCode;
    }

    public boolean isPhrHidden() {
        return phrHidden;
    }

    public void setPhrHidden(boolean phrHidden) {
        this.phrHidden = phrHidden;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsManual() {
        return isManual;
    }

    public void setIsManual(Boolean manual) {
        isManual = manual;
    }

    public boolean getAllowedForGroupNote() {
        return allowedForGroupNote;
    }

    public void setAllowedForGroupNote(boolean allowedForGroupNote) {
        this.allowedForGroupNote = allowedForGroupNote;
    }
}
