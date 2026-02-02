package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.*;

import javax.validation.constraints.NotNull;
import java.util.List;

public class BaseDocumentSignatureTemplateData {

    private String title;
    private Organization organization;
    private List<Community> communities;
    private Employee author;
    @NotNull
    private Configuration configuration;
    private DocumentSignatureTemplateStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public static class Configuration {
        private List<AutoFillField> autoFillFields;
        private List<OrganizationAutoFillField> organizationAutoFillFields;
        private List<ToolboxRequesterField> toolboxRequesterFields;
        private List<ToolboxSignerField> toolboxSignerFields;
        private List<Rule> rules;
        private List<Long> folderIds;


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

        public void setRules(final List<Rule> rules) {
            this.rules = rules;
        }

        public List<Long> getFolderIds() {
            return folderIds;
        }

        public void setFolderIds(List<Long> folderIds) {
            this.folderIds = folderIds;
        }
    }

    public static class AutoFillField {

        private Long id;
        private DocumentSignatureTemplateAutoFillFieldType type;
        private boolean editable;
        private Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public DocumentSignatureTemplateAutoFillFieldType getType() {
            return type;
        }

        public void setType(DocumentSignatureTemplateAutoFillFieldType type) {
            this.type = type;
        }

        public boolean isEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
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
        private DocumentSignatureTemplateOrganizationAutoFillFieldType type;
        private Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public DocumentSignatureTemplateOrganizationAutoFillFieldType getType() {
            return type;
        }

        public void setType(DocumentSignatureTemplateOrganizationAutoFillFieldType type) {
            this.type = type;
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
        private DocumentSignatureTemplateToolboxRequesterFieldType type;
        private DocumentSignatureTemplateFieldProperties properties;
        private Location location;

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

        public DocumentSignatureTemplateToolboxRequesterFieldType getType() {
            return type;
        }

        public void setType(DocumentSignatureTemplateToolboxRequesterFieldType type) {
            this.type = type;
        }

        public DocumentSignatureTemplateFieldProperties getProperties() {
            return properties;
        }

        public void setProperties(DocumentSignatureTemplateFieldProperties properties) {
            this.properties = properties;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static class ToolboxSignerField {

        private Long id;
        private String title;
        private DocumentSignatureTemplateToolboxSignerFieldType type;
        private Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public DocumentSignatureTemplateToolboxSignerFieldType getType() {
            return type;
        }

        public void setType(DocumentSignatureTemplateToolboxSignerFieldType type) {
            this.type = type;
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

        public void setTitle(final String title) {
            this.title = title;
        }
    }

    public static class Location extends DocumentSignatureFieldUiLocation {

        private short pageNo;

        public short getPageNo() {
            return pageNo;
        }

        public void setPageNo(short pageNo) {
            this.pageNo = pageNo;
        }
    }

    public static class Rule {
        private String dependentFieldTitle;
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
    }

    public DocumentSignatureTemplateStatus getStatus() {
        return status;
    }

    public void setStatus(final DocumentSignatureTemplateStatus status) {
        this.status = status;
    }
}
