import React, { memo, useMemo, useEffect, useCallback } from "react";

import cn from "classnames";
import PTypes from "prop-types";

import { map, last, reduce } from "underscore";

import { Col, Row, Form, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { TextField, FileField, SelectField, RadioGroupField } from "components/Form";

import { useScrollable, useSelectOptions } from "hooks/common";

import FU from "lib/utils/FileUtils";

import { ALLOWED_FILE_FORMATS, ALLOWED_FILE_FORMAT_MIME_TYPES } from "lib/Constants";

import "./DocumentForm.scss";

const { DOC, DOCX, PDF, XLS, XLSX, TXT, JPEG, JPG, PNG, TIFF, GIF } = ALLOWED_FILE_FORMATS;

const ADDITIONAL_FILE_MEME_TYPES = [".doc", ".docx"];

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XLS],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XLSX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[TXT],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPEG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
  ...ADDITIONAL_FILE_MEME_TYPES,
];

function DocumentForm({
  data,
  errors,
  children,
  isEditing,
  isSubmitDisabled,
  categories,
  documentTitle,
  organizationName,
  hasSharingSection,
  submitButtonText,
  className,
  onLayout,
  onChangeField,
  onCancel,
  onSubmit,
  renderParentFolderSection,
}) {
  const documentName = data?.document?.name;

  const { Scrollable, scroll } = useScrollable();

  const sharingOptions = useMemo(
    () => [
      { value: "MY_COMPANY", label: `Share with ${organizationName}` },
      { value: "ALL", label: "Share with all" },
    ],
    [organizationName],
  );

  const identifiedColors = useMemo(() => {
    return reduce(
      categories,
      (map, value) => {
        map[value.id] = value.color;
        return map;
      },
      {},
    );
  }, [categories]);

  const identifiedColorNames = useMemo(() => {
    return reduce(
      categories,
      (map, value) => {
        map[value.id] = value.name;
        return map;
      },
      {},
    );
  }, [categories]);

  const categoryOptions = useSelectOptions(categories, { textProp: "name" });

  const onChangeFileField = useCallback(
    (name, value) => {
      onChangeField(name, value ?? undefined);
      onChangeField("title", value?.name ?? "");
    },
    [onChangeField],
  );

  const onChangeTitleField = useCallback(
    (name, value) => {
      let title = value ?? "";

      const extension = FU.getFileExtension(documentName || documentTitle);

      if (extension) title += `.${extension}`;

      onChangeField(name, title);
    },
    [documentName, documentTitle, onChangeField],
  );

  const onChangeSelectField = useCallback(
    (name, value) => {
      onChangeField(name, value);
    },
    [onChangeField],
  );

  useEffect(() => {
    onLayout({ scroll });
  }, [scroll, onLayout]);

  return (
    <Form className={cn("DocumentForm", className)}>
      <Scrollable className="DocumentForm-Sections">
        <Row className="full-height">
          <Col lg={children ? 6 : 12} className="DocumentForm-Section DocumentForm-BaseSection">
            {!isEditing && (
              <Row className="margin-bottom-20">
                <Col md={12}>
                  <FileField
                    hasHint
                    name="document"
                    value={documentName}
                    label="Choose file*"
                    className="DocumentForm-FileField"
                    placeholder="Document is not chosen"
                    allowedTypes={ALLOWED_FILE_MIME_TYPES}
                    hasError={!!errors?.document}
                    errorText={errors?.document?.name || errors?.document?.size || errors?.document?.type}
                    hintText="Supported file types: Word, PDF, Excel, TXT, JPEG, GIF, PNG, TIFF | Max 20 mb"
                    onChange={onChangeFileField}
                  />
                </Col>
              </Row>
            )}
            <Row>
              <Col md={12}>
                <TextField
                  type="text"
                  name="title"
                  value={FU.getFileBaseName(data?.title)}
                  label="File Name*"
                  isDisabled={!(isEditing || data?.document?.size)}
                  className="DocumentForm-TextField"
                  errorText={errors.title}
                  maxLength={256}
                  hintText="Supported file types: Word, PDF, Excel, TXT, JPEG, GIF, PNG, TIFF | Max 20 mb"
                  onChange={onChangeTitleField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name="description"
                  value={data?.description}
                  label="Description"
                  numberOfRows={10}
                  className="DocumentForm-TextField"
                  errorText={errors.description}
                  maxLength={3950}
                  onChange={onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <div id="categoryIds">
                  <SelectField
                    name="categoryIds"
                    value={data?.categoryIds}
                    isMultiple
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    options={categoryOptions}
                    label="File Category"
                    className="DocumentForm-SelectField DocumentForm-CategorySelect"
                    onChange={onChangeSelectField}
                    errorText={errors.categoryIds}
                    isDisabled={!categoryOptions.length}
                    renderSelectedText={() => {
                      return (
                        <>
                          {map(data?.categoryIds, (id) => {
                            const color = identifiedColors[id];
                            const name = identifiedColorNames[id];
                            const isNotLast = last(data.categoryIds) !== id;

                            return (
                              <div key={id} className="DocumentForm-SelectedColor">
                                <div className="DocumentForm-ColorMark" style={{ backgroundColor: color }} />

                                <span>{`${name}${isNotLast ? "," : ""}`}</span>
                              </div>
                            );
                          })}
                        </>
                      );
                    }}
                    formatOptionText={({ text, value: id }) => {
                      const color = identifiedColors[id];

                      return (
                        <div className="DocumentForm-SelectFieldOption">
                          <span style={{ backgroundColor: color }} className="DocumentForm-ColorMark" />
                          <span>{text}</span>
                        </div>
                      );
                    }}
                  />
                </div>

                {!categoryOptions.length && (
                  <Tooltip placement="top" target="categoryIds">
                    No categories configured for your organization
                  </Tooltip>
                )}
              </Col>
            </Row>
            {renderParentFolderSection && renderParentFolderSection({ data, errors, onChangeField })}
            {hasSharingSection && (
              <div className="DocumentForm-Section DocumentForm-SharingSection">
                <Row>
                  <Col md={12}>
                    <RadioGroupField
                      view="col"
                      name="sharingOption"
                      className="DocumentForm-RadioGroupField"
                      selected={data?.sharingOption}
                      hasError={!!errors.sharingOption}
                      errorText={errors.sharingOption}
                      options={sharingOptions}
                      onChange={onChangeField}
                    />
                  </Col>
                </Row>
              </div>
            )}
            {data?.signature && (
              <TextField
                type="text"
                isDisabled
                name="signatureStatus"
                value={data?.signature.statusTitle}
                label="Signature status"
                className="DocumentForm-TextField"
              />
            )}
          </Col>
          {children && (
            <Col lg={6} className="DocumentForm-Section DocumentForm-OptionalSection">
              {children}
            </Col>
          )}
        </Row>
      </Scrollable>

      <div className="DocumentForm-Buttons">
        <Button outline color="success" onClick={onCancel}>
          Cancel
        </Button>
        <Button color="success" onClick={onSubmit} disabled={isSubmitDisabled}>
          {submitButtonText}
        </Button>
      </div>
    </Form>
  );
}

DocumentForm.propTypes = {
  data: PTypes.object,
  errors: PTypes.object,
  isEditing: PTypes.bool,
  isSubmitDisabled: PTypes.bool,
  hasSharingSection: PTypes.bool,
  categories: PTypes.arrayOf(PTypes.object),
  documentTitle: PTypes.string,
  organizationName: PTypes.string,
  submitButtonText: PTypes.string,
  onLayout: PTypes.func,
  onChangeField: PTypes.func,
  onCancel: PTypes.func,
  onSubmit: PTypes.func,
};

DocumentForm.defaultProps = {
  data: {},
  errors: {},
  submitButtonText: "Upload",
};

export default memo(DocumentForm);
