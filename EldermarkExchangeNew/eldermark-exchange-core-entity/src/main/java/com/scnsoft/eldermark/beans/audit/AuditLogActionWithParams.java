package com.scnsoft.eldermark.beans.audit;

import com.scnsoft.eldermark.entity.audit.AuditLogType;

import java.util.List;

public class AuditLogActionWithParams {

    private AuditLogAction action;
    private AuditLogActionGroup actionGroup;
    private List<String> params;
    private AuditLogType auditLogType;

    public AuditLogActionWithParams(AuditLogAction action) {
        this.action = action;
    }

    public AuditLogActionWithParams(AuditLogAction action, AuditLogType auditLogType) {
        this.action = action;
        this.auditLogType = auditLogType;
    }

    public AuditLogActionWithParams(AuditLogAction action, AuditLogActionGroup actionGroup, List<String> params) {
        this.action = action;
        this.actionGroup = actionGroup;
        this.params = params;
    }
    public AuditLogActionWithParams(AuditLogAction action, AuditLogActionGroup actionGroup, List<String> params, AuditLogType auditLogType) {
        this.action = action;
        this.actionGroup = actionGroup;
        this.params = params;
        this.auditLogType = auditLogType;
    }

    public AuditLogAction getAction() {
        return action;
    }

    public void setAction(AuditLogAction action) {
        this.action = action;
    }

    public AuditLogActionGroup getActionGroup() {
        return actionGroup;
    }

    public void setActionGroup(AuditLogActionGroup actionGroup) {
        this.actionGroup = actionGroup;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public AuditLogType getAuditLogType() {
        return auditLogType;
    }

    public void setAuditLogType(AuditLogType auditLogType) {
        this.auditLogType = auditLogType;
    }
}
