package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureFieldData;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureRequestNotSubmittedFieldDataAdapter;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldDataAdapter;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.service.document.signature.provider.DocumentSignatureProvider;
import com.scnsoft.eldermark.service.document.signature.template.field.builder.SubmittedFieldService;
import com.scnsoft.eldermark.service.document.signature.template.field.value.FieldDefaultValueService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureTemplateFieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureTemplateFieldServiceImpl implements DocumentSignatureTemplateFieldService {

    @Autowired
    private DocumentSignatureProvider documentSignatureProvider;

    @Autowired
    private SubmittedFieldService submittedFieldService;

    @Autowired
    private FieldDefaultValueService fieldDefaultValueService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> findDefaults(DocumentSignatureTemplateContext context) {

        if (context.getDocument() != null) return Map.of();

        var fields = context.getTemplate().getFields().stream()
                .filter(it -> it.getDefaultValueType() != null)
                .map(it -> Pair.of(it.getName(), it.getDefaultValueType()))
                .distinct()
                .map(it -> Pair.of(it.getFirst(), fieldDefaultValueService.findDefaultValue(it.getSecond(), context)))
                .collect(Collectors.toList());

        return DocumentSignatureTemplateFieldUtils.makeFieldValueTree(fields);
    }

    @Override
    public List<DocumentSignatureRequestSubmittedField> createScSubmittedFields(DocumentSignatureTemplateContext context) {
        return submittedFieldService.constructScFields(context);
    }

    @Override
    public void addPdcFlowFieldsToRequest(DocumentSignatureTemplateContext context, DocumentSignatureRequest request) {

        var allSignatureIds = getAvailableSignatureFieldIds(context);
        var selectedSignatureIds = context.getSignatureAreaIds();

        var submittedFields = new ArrayList<DocumentSignatureFieldData>();
        var notSubmittedFields = new ArrayList<DocumentSignatureFieldData>();

        var fields = getAvailableFieldsData(context);

        fields.stream()
                .filter(field -> field.getPdcFlowType() != null)
                .forEach(field -> {
                    if (allSignatureIds.contains(field.getId())) {
                        if (selectedSignatureIds.contains(field.getId())) {
                            submittedFields.add(field);
                        } else {
                            notSubmittedFields.add(field);
                        }
                    } else {
                        if (isFieldRelatedValueInContext(context, field)) {
                            submittedFields.add(field);
                        } else {
                            notSubmittedFields.add(field);
                        }
                    }
                });

        addPdcFlowSubmittedFieldsToRequest(context, request, submittedFields);
        addNotSubmittedFieldsToRequest(request, notSubmittedFields);
    }

    private List<DocumentSignatureFieldData> getAvailableFieldsData(DocumentSignatureTemplateContext context) {
        if (context.getDocument() == null) {
            return context.getTemplate().getFields().stream()
                    .map(DocumentSignatureTemplateFieldDataAdapter::new)
                    .collect(Collectors.toList());
        } else {
            return context.getDocument().getSignatureRequest().getNotSubmittedFields().stream()
                    .map(DocumentSignatureRequestNotSubmittedFieldDataAdapter::new)
                    .collect(Collectors.toList());
        }
    }

    private Set<Long> getAvailableSignatureFieldIds(DocumentSignatureTemplateContext context) {
        if (context.getDocument() == null) {
            return context.getTemplate()
                    .getFields()
                    .stream()
                    .filter(it -> it.getPdcFlowType() == TemplateFieldPdcFlowType.SIGNATURE)
                    .map(DocumentSignatureTemplateField::getId)
                    .collect(Collectors.toSet());
        } else {
            var previousSignatureRequest = Objects.requireNonNull(context.getDocument().getSignatureRequest());
            return previousSignatureRequest.getNotSubmittedFields().stream()
                    .filter(it -> it.getPdcFlowType() == TemplateFieldPdcFlowType.SIGNATURE)
                    .map(DocumentSignatureRequestNotSubmittedField::getId)
                    .collect(Collectors.toSet());
        }
    }

    private void addNotSubmittedFieldsToRequest(
            DocumentSignatureRequest request,
            List<DocumentSignatureFieldData> notSubmittedFields
    ) {
        var fieldIdToNotSubmittedField = new HashMap<Long, DocumentSignatureRequestNotSubmittedField>();
        notSubmittedFields.forEach(field -> fieldIdToNotSubmittedField.put(field.getId(), addNonSubmittedFieldToRequest(request, field)));
        notSubmittedFields.forEach(field -> {
            var notSubmittedField = fieldIdToNotSubmittedField.get(field.getId());
            notSubmittedField.setRelatedField(fieldIdToNotSubmittedField.get(field.getRelatedFieldId()));
        });
    }

    private DocumentSignatureRequestNotSubmittedField addNonSubmittedFieldToRequest(
            DocumentSignatureRequest request,
            DocumentSignatureFieldData field
    ) {
        var newField = new DocumentSignatureRequestNotSubmittedField();
        newField.setSignatureRequest(request);
        newField.setPdcFlowType(field.getPdcFlowType());
        newField.setName(field.getName());
        populateFieldLocation(newField, field.getLocation());

        request.getNotSubmittedFields().add(newField);
        return newField;
    }

    private void addPdcFlowSubmittedFieldsToRequest(
            DocumentSignatureTemplateContext context,
            DocumentSignatureRequest request,
            List<DocumentSignatureFieldData> fields
    ) {
        fields.forEach(field -> addPdcFlowSubmittedFieldToRequest(context, request, field));
    }

    private void addPdcFlowSubmittedFieldToRequest(
            DocumentSignatureTemplateContext context,
            DocumentSignatureRequest request,
            DocumentSignatureFieldData field
    ) {
        // if field have sc type and pdcFlow type both then send field to pdcFlow only if value is not set on Step 2
        if (field.getScSourceFieldType() != null && context.getFieldValues().get(field.getName()) != null) {
            return;
        }

        var submittedField = new DocumentSignatureRequestSubmittedField();
        populateFieldLocation(submittedField, field.getLocation());
        documentSignatureProvider.prepareSubmittedFieldFromTemplate(field, submittedField);
        submittedField.setSignatureRequest(request);
        submittedFieldService.addStylesToSubmittedField(submittedField, field.getStyles());
        request.getSubmittedFields().add(submittedField);
    }

    private boolean isFieldRelatedValueInContext(DocumentSignatureTemplateContext context, DocumentSignatureFieldData field) {
        var relatedFieldId = field.getRelatedFieldId();
        var relatedFieldValue = field.getRelatedFieldValue();
        if (relatedFieldId == null) {
            return true;
        } else if (relatedFieldValue != null) {
            return context.getTemplate().getFields().stream()
                    .filter(it -> Objects.equals(it.getId(), relatedFieldId))
                    .map(it -> context.getFieldValues().get(it.getName()))
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .map(relatedFieldValue::equals)
                    .findFirst()
                    .orElse(false);
        } else {
            return context.getSignatureAreaIds().contains(relatedFieldId);
        }
    }

    private void populateFieldLocation(
            BaseDocumentSignatureFieldLocation target,
            BaseDocumentSignatureFieldLocation source
    ) {
        target.setPageNo(source.getPageNo());
        target.setBottomRightX(source.getBottomRightX());
        target.setBottomRightY(source.getBottomRightY());
        target.setTopLeftX(source.getTopLeftX());
        target.setTopLeftY(source.getTopLeftY());
    }
}

