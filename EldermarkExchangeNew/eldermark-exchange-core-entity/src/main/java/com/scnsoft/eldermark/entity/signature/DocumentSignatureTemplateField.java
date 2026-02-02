package com.scnsoft.eldermark.entity.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureFieldPdcFlowTypeAware;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DocumentSignatureTemplateField")
public class DocumentSignatureTemplateField implements DocumentSignatureFieldPdcFlowTypeAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_template_id")
    private DocumentSignatureTemplate signatureTemplate;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "pdc_flow_type", nullable = false)
    private TemplateFieldPdcFlowType pdcFlowType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sc_source_field_type", nullable = false)
    private ScSourceTemplateFieldType scSourceFieldType;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_value_type", nullable = false)
    private TemplateFieldDefaultValueType defaultValueType;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DocumentSignatureTemplateFieldLocation> locations;

    @Column(name = "related_field_id")
    private Long relatedFieldId;

    @Column(name = "related_field_value")
    private String relatedFieldValue;

    @OneToMany(mappedBy = "templateField", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentSignatureTemplateFieldStyle> styles;

    @Column(name = "json_schema")
    private String jsonSchema;

    @Column(name = "json_ui_schema")
    private String jsonUiSchema;

    @ManyToOne
    @JoinColumn(name = "auto_fill_type_id")
    private DocumentSignatureTemplateAutoFillFieldType autoFillType;

    @Column(name = "auto_fill_type_id", insertable = false, updatable = false)
    private Long autoFillTypeId;

    @ManyToOne
    @JoinColumn(name = "org_auto_fill_type_id")
    private DocumentSignatureTemplateOrganizationAutoFillFieldType organizationAutoFillType;

    @Column(name = "org_auto_fill_type_id", insertable = false, updatable = false)
    private Long organizationAutoFillTypeId;

    @Column(name = "toolbox_signer_type_id", insertable = false, updatable = false)
    private Long toolboxSignerTypeId;

    @ManyToOne
    @JoinColumn(name = "toolbox_signer_type_id")
    private DocumentSignatureTemplateToolboxSignerFieldType toolboxSignerFieldType;

    @ManyToOne
    @JoinColumn(name = "toolbox_requester_type_id")
    private DocumentSignatureTemplateToolboxRequesterFieldType toolboxRequesterType;

    @Column(name = "toolbox_requester_type_id", insertable = false, updatable = false)
    private Long toolboxRequesterTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentSignatureTemplate getSignatureTemplate() {
        return signatureTemplate;
    }

    public void setSignatureTemplate(DocumentSignatureTemplate signatureTemplate) {
        this.signatureTemplate = signatureTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TemplateFieldPdcFlowType getPdcFlowType() {
        return pdcFlowType;
    }

    public ScSourceTemplateFieldType getScSourceFieldType() {
        return scSourceFieldType;
    }

    public void setScSourceFieldType(ScSourceTemplateFieldType type) {
        this.scSourceFieldType = type;
    }

    public void setPdcFlowType(TemplateFieldPdcFlowType generated_type) {
        this.pdcFlowType = generated_type;
    }

    public TemplateFieldDefaultValueType getDefaultValueType() {
        return defaultValueType;
    }

    public void setDefaultValueType(TemplateFieldDefaultValueType uiDefaultType) {
        this.defaultValueType = uiDefaultType;
    }

    public List<DocumentSignatureTemplateFieldLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<DocumentSignatureTemplateFieldLocation> locations) {
        this.locations = locations;
    }

    public Long getRelatedFieldId() {
        return relatedFieldId;
    }

    public void setRelatedFieldId(Long relatedFieldId) {
        this.relatedFieldId = relatedFieldId;
    }

    public String getRelatedFieldValue() {
        return relatedFieldValue;
    }

    public void setRelatedFieldValue(String relatedFieldValue) {
        this.relatedFieldValue = relatedFieldValue;
    }

    public List<DocumentSignatureTemplateFieldStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<DocumentSignatureTemplateFieldStyle> fieldStyles) {
        this.styles = fieldStyles;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getJsonUiSchema() {
        return jsonUiSchema;
    }

    public void setJsonUiSchema(String uiJsonSchema) {
        this.jsonUiSchema = uiJsonSchema;
    }

    public DocumentSignatureTemplateAutoFillFieldType getAutoFillType() {
        return autoFillType;
    }

    public void setAutoFillType(DocumentSignatureTemplateAutoFillFieldType autoFillType) {
        this.autoFillType = autoFillType;
    }

    public Long getAutoFillTypeId() {
        return autoFillTypeId;
    }

    public void setAutoFillTypeId(Long autoFillTypeId) {
        this.autoFillTypeId = autoFillTypeId;
    }

    public DocumentSignatureTemplateOrganizationAutoFillFieldType getOrganizationAutoFillType() {
        return organizationAutoFillType;
    }

    public void setOrganizationAutoFillType(DocumentSignatureTemplateOrganizationAutoFillFieldType organizationAutoFillType) {
        this.organizationAutoFillType = organizationAutoFillType;
    }

    public Long getOrganizationAutoFillTypeId() {
        return organizationAutoFillTypeId;
    }

    public void setOrganizationAutoFillTypeId(Long organizationAutoFillTypeId) {
        this.organizationAutoFillTypeId = organizationAutoFillTypeId;
    }

    public DocumentSignatureTemplateToolboxSignerFieldType getToolboxSignerFieldType() {
        return toolboxSignerFieldType;
    }

    public void setToolboxSignerFieldType(DocumentSignatureTemplateToolboxSignerFieldType toolboxSignerFieldType) {
        this.toolboxSignerFieldType = toolboxSignerFieldType;
    }

    public Long getToolboxSignerTypeId() {
        return toolboxSignerTypeId;
    }

    public void setToolboxSignerTypeId(final Long toolboxSignerTypeId) {
        this.toolboxSignerTypeId = toolboxSignerTypeId;
    }

    public DocumentSignatureTemplateToolboxRequesterFieldType getToolboxRequesterType() {
        return toolboxRequesterType;
    }

    public void setToolboxRequesterType(DocumentSignatureTemplateToolboxRequesterFieldType toolboxRequesterType) {
        this.toolboxRequesterType = toolboxRequesterType;
    }

    public Long getToolboxRequesterTypeId() {
        return toolboxRequesterTypeId;
    }

    public void setToolboxRequesterTypeId(Long toolboxRequesterTypeId) {
        this.toolboxRequesterTypeId = toolboxRequesterTypeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
