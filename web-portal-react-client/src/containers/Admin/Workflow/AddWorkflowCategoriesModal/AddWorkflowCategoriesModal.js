import "./AddWorkflowCategoriesModal.scss";
import Modal from "../../../../components/Modal/Modal";
import { Button, Col, Form, Row } from "reactstrap";
import { SelectField, TextField } from "../../../../components/Form";
import React, { useCallback, useEffect, useState } from "react";
import getWorkflowLibraryHooks from "../../../../hooks/workflowLibraryHooks";
import { Loader } from "../../../../components";
import { useForm } from "../../../../hooks/common";
import WorkflowCategoryEntity from "entities/WorkflowCategory";
import WorkflowCategoryFormSchemeValidator from "../../../../validators/WorkflowCategoryFormSchemeValidator";
import { isNumber } from "underscore";
import { useQueryClient } from "@tanstack/react-query";

const AddWorkflowCategoriesModal = (props) => {
  const {
    isOpen,
    onCancel,
    onConfirm,
    isSuperAdmin,
    categoryId = null,
    categoryOrganizationId = null,
    editCategoryName = null,
  } = props;
  // 使用统一hook
  const { useOrganizationOptions } = getWorkflowLibraryHooks();
  const { data: organizationOptions = [], isLoading: isOrgLoading } = useOrganizationOptions();
  const [isFetching, setIsFetching] = useState(false);
  const [isValidationNeed, setValidationNeed] = useState(props.isValidationNeed);

  // 获取 TanStack Query 的 queryClient
  const queryClient = useQueryClient();

  let isEditing = isNumber(categoryId);

  useEffect(() => {
    isEditing && changeField("organizationId", categoryOrganizationId);
    isEditing && changeField("workflowCategoryName", editCategoryName);
  }, [isEditing]);
  const { fields, errors, validate, isChanged, clearFields, changeField, changeFields } = useForm(
    "WorkflowCategory",
    WorkflowCategoryEntity,
    WorkflowCategoryFormSchemeValidator,
  );

  const onValidate = useCallback(
    (options) => {
      return validate(options).then(() => Promise.resolve());
    },
    [fields, validate],
  );

  const onConfirmAddCategory = async (e = null) => {
    e && e.preventDefault();
    onValidate(fields)
      .then(() => {
        onConfirm(
          categoryId
            ? {
                organizationId: categoryOrganizationId,
                categoryId,
                categoryName: fields?.workflowCategoryName?.trim(),
              }
            : {
                organizationId: fields.organizationId,
                categoryName: fields?.workflowCategoryName?.trim(),
              },
        );

        queryClient.invalidateQueries(["workflow-category"]);
        // 关闭弹窗
        onCancel();
      })
      .catch(() => {
        setValidationNeed(true);
        setIsFetching(false);
      })
      .finally(() => {
        setIsFetching(false);
      });
  };
  return (
    <>
      <Modal
        isOpen={isOpen}
        title={isEditing ? "Edit Workflow Category" : "Add Workflow Category"}
        className="workflow-modal"
        hasFooter={false}
        hasCloseBtn={true}
        onClose={onCancel}
      >
        <Form className="WorkflowCategory is-invalid" onSubmit={onConfirmAddCategory}>
          {isFetching && <Loader isCentered style={{ position: "fixed" }} hasBackdrop />}
          <div className="WorkflowCategory workflow-modal-content ">
            <Row>
              <Col>
                <TextField
                  name="workflowCategoryName"
                  label={"Workflow Category Name*"}
                  value={fields.workflowCategoryName}
                  errorText={errors.workflowCategoryName}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectField
                  name="organizationId"
                  value={fields.organizationId}
                  label="Organization*"
                  isMultiple={false}
                  hasSearchBox={true}
                  // isDisabled={!isSuperAdmin || !!categoryOrganizationId}
                  className="WorkflowForm-SelectField"
                  errorText={errors.organizationId}
                  onChange={changeField}
                  options={organizationOptions}
                  isLoading={isOrgLoading}
                />
              </Col>
            </Row>
          </div>
        </Form>
        <div className="workflow-category-buttons">
          <Button outline color="success" onClick={onCancel}>
            Cancel
          </Button>

          <Button color="success" onClick={onConfirmAddCategory}>
            Confirm
          </Button>
        </div>
      </Modal>
    </>
  );
};
export default AddWorkflowCategoriesModal;
