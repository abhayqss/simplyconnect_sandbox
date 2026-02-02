package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.UserManual;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "AuditLogRelation_UserManual")
public class AuditLogUserManualRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "user_manual_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserManual userManual;

    @Column(name = "user_manual_id", nullable = false)
    private Long userManualId;

    @Column(name = "user_manual_title")
    private String userManualTitle;

    public UserManual getUserManual() {
        return userManual;
    }

    public void setUserManual(UserManual userManual) {
        this.userManual = userManual;
    }

    public Long getUserManualId() {
        return userManualId;
    }

    public void setUserManualId(Long userManualId) {
        this.userManualId = userManualId;
    }

    public String getUserManualTitle() {
        return userManualTitle;
    }

    public void setUserManualTitle(String userManualTitle) {
        this.userManualTitle = userManualTitle;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(userManualId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Stream.ofNullable(userManualTitle)
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.USER_MANUAL;
    }
}
