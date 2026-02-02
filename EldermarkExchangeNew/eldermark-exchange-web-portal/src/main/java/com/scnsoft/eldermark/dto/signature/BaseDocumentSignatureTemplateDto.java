package com.scnsoft.eldermark.dto.signature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureFieldUiLocation;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class BaseDocumentSignatureTemplateDto {

    @Size(min = 1, max = 36)
    private String title;

    @NotNull
    private Long organizationId;

    private List<Long> communityIds;

    @Valid
    private Configuration configuration;

    private DocumentSignatureTemplateStatus statusName;

    private String statusTitle;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public DocumentSignatureTemplateStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(DocumentSignatureTemplateStatus statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public static class Configuration {
        private List<@Valid AutoFillField> autoFillFields;
        private List<@Valid OrganizationAutoFillField> organizationAutoFillFields;
        private List<@Valid ToolboxRequesterField> toolboxRequesterFields;
        private List<@Valid ToolboxSignerField> toolboxSignerFields;
        private List<@Valid Rule> rules;
        @NotNull
        private List<@Valid FolderConfiguration> folders;

        public List<AutoFillField> getAutoFillFields() {
            return autoFillFields;
        }

        public void setAutoFillFields(List<AutoFillField> autoFillFields) {
            this.autoFillFields = autoFillFields;
        }

        public List<OrganizationAutoFillField> getOrganizationAutoFillFields() {
            return organizationAutoFillFields;
        }

        public void setOrganizationAutoFillFields(List<OrganizationAutoFillField> organizationAutoFillFields) {
            this.organizationAutoFillFields = organizationAutoFillFields;
        }

        public List<ToolboxRequesterField> getToolboxRequesterFields() {
            return toolboxRequesterFields;
        }

        public void setToolboxRequesterFields(List<ToolboxRequesterField> toolboxRequesterFields) {
            this.toolboxRequesterFields = toolboxRequesterFields;
        }

        public List<ToolboxSignerField> getToolboxSignerFields() {
            return toolboxSignerFields;
        }

        public void setToolboxSignerFields(List<ToolboxSignerField> toolboxSignerFields) {
            this.toolboxSignerFields = toolboxSignerFields;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }

        public List<FolderConfiguration> getFolders() {
            return folders;
        }

        public void setFolders(List<FolderConfiguration> folders) {
            this.folders = folders;
        }
    }

    public static class AutoFillField {

        private Long id;
        @NotNull
        private Long typeId;
        private String typeCode;
        private String typeTitle;
        private boolean isEditable;
        @Valid
        private Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getTypeId() {
            return typeId;
        }

        public void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public boolean getIsEditable() {
            return isEditable;
        }

        public void setIsEditable(boolean editable) {
            this.isEditable = editable;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class OrganizationAutoFillField {

        private Long id;
        @NotNull
        private Long typeId;
        private String typeCode;
        private String typeTitle;
        @Valid
        private Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getTypeId() {
            return typeId;
        }

        public void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class ToolboxRequesterField {

        private Long id;
        private String title;
        @NotNull
        private Long typeId;
        private String typeCode;
        private String typeTitle;
        @Valid
        private Location location;
        @NotNull
        private RequesterFieldProperties properties;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getTypeId() {
            return typeId;
        }

        public void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public RequesterFieldProperties getProperties() {
            return properties;
        }

        public void setProperties(RequesterFieldProperties properties) {
            this.properties = properties;
        }
    }

    public static class RequesterFieldProperties {
        @NotEmpty
        private String label;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> values;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    public static class ToolboxSignerField {
        private Long id;
        @NotEmpty
        private String title;
        @NotNull
        private Long typeId;
        private String typeCode;
        private String typeTitle;
        @Valid
        private Location location;
        private boolean isResizable;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getTypeId() {
            return typeId;
        }

        public void setTypeId(Long typeId) {
            this.typeId = typeId;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean getIsResizable() {
            return isResizable;
        }

        public void setIsResizable(boolean resizable) {
            isResizable = resizable;
        }
    }

    public static class Location extends DocumentSignatureFieldUiLocation {

        @NotNull
        private short pageNo;

        public short getPageNo() {
            return pageNo;
        }

        public void setPageNo(short pageNo) {
            this.pageNo = pageNo;
        }
    }

    public static class Rule {
        private Long dependentFieldId;
        @NotEmpty
        private String dependentFieldTitle;
        private Long fieldId;
        @NotEmpty
        private String fieldTitle;

        public String getDependentFieldTitle() {
            return dependentFieldTitle;
        }

        public void setDependentFieldTitle(String dependentFieldTitle) {
            this.dependentFieldTitle = dependentFieldTitle;
        }

        public String getFieldTitle() {
            return fieldTitle;
        }

        public void setFieldTitle(String fieldTitle) {
            this.fieldTitle = fieldTitle;
        }

        public Long getFieldId() {
            return fieldId;
        }

        public void setFieldId(Long fieldId) {
            this.fieldId = fieldId;
        }

        public Long getDependentFieldId() {
            return dependentFieldId;
        }

        public void setDependentFieldId(final Long dependentFieldId) {
            this.dependentFieldId = dependentFieldId;
        }
    }

    public static class FolderConfiguration {

        @NotNull
        private Long communityId;

        @NotNull
        private Long folderId;

        public Long getCommunityId() {
            return communityId;
        }

        public void setCommunityId(Long communityId) {
            this.communityId = communityId;
        }

        public Long getFolderId() {
            return folderId;
        }

        public void setFolderId(Long folderId) {
            this.folderId = folderId;
        }
    }
}
