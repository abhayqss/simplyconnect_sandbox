import React, { useEffect, useState } from "react";
import { Button, Col, Row } from "reactstrap";
import { Modal } from "../../../../components";
import { SelectField, TextField } from "../../../../components/Form";
import getWorkflowLibraryHooks from "../../../../hooks/workflowLibraryHooks";

const PublishWorkflowToOtherOrganization = (props) => {
  const { isOpen, onClose, onCancel, currentWorkflowData, isServicePlan = false } = props;

  const [hasOrganizationError, setHasOrganizationError] = useState(false);
  const [hasCommunityError, setHasCommunityError] = useState(false);
  const [hasWorkflowNameError, setHasWorkflowNameError] = useState(false);
  const [hasCategoryError, setHasCategoryError] = useState(false);

  const [workflowName, setWorkflowName] = useState("");
  const [communityIds, setCommunityIds] = useState([]);
  const [organizationId, setOrganizationId] = useState("");
  const [categoryId, setCategoryId] = useState("");

  const [workflowCode, setWorkflowCode] = useState("");
  const [hasWorkflowCodeError, setHasWorkflowCodeError] = useState(false);
  const [workflowCodeErrorText, setWorkflowCodeErrorText] = useState("");

  const [buttonDisabled, setButtonDisabled] = useState(false);

  // 统一 hooks 引用
  const {
    useOrganizationOptions,
    useCommunityOptions,
    useWorkflowCategoryOptions,
    useCreateWorkflowTemplate,
    useCreateServicePlanTemplate,
  } = getWorkflowLibraryHooks();

  const { data: organizationOptions = [], isLoading: isOrgLoading } = useOrganizationOptions();
  const { data: communityOptions = [], isLoading: isCommunityLoading } = useCommunityOptions(organizationId);
  const {
    data: categoryOptions = [],
    isLoading: isCategoryLoading,
    error: categoryQueryError,
  } = useWorkflowCategoryOptions(organizationId);

  const publishWorkflowMutation = useCreateWorkflowTemplate();
  const publishServicePlanMutation = useCreateServicePlanTemplate();

  useEffect(() => {
    if (currentWorkflowData) {
      setWorkflowName(currentWorkflowData?.name || "");
      // 不设置默认的组织和社区，让用户自己选择
      setCommunityIds([]);
      setOrganizationId("");
      setCategoryId("");
      setWorkflowCode("");
    }
  }, [currentWorkflowData]);

  const changeOrganizationIds = (fields, value) => {
    setOrganizationId(value);
    // 当组织改变时，清空社区和分类选择
    setCommunityIds([]);
    setCategoryId("");
    setHasOrganizationError(false);
  };

  const changeCommunityId = (fields, value) => {
    setCommunityIds(value);
    if (value.length === 0) {
      setHasCommunityError(true);
    } else {
      setHasCommunityError(false);
    }
  };

  const changeWorkflowName = (fields, value) => {
    const sanitizedValue = value.replace(/\./g, "");
    setWorkflowName(sanitizedValue);
    if (!value) {
      setHasWorkflowNameError(true);
    } else {
      setHasWorkflowNameError(false);
    }
  };

  const changeCategoryId = (fields, value) => {
    setCategoryId(value);
    if (!value) {
      setHasCategoryError(true);
    } else {
      setHasCategoryError(false);
    }
  };

  const changeWorkflowCode = (fields, value) => {
    const sanitizedValue = value.replace(/\./g, "");
    setWorkflowCode(sanitizedValue);
    setHasWorkflowCodeError(false);
    setWorkflowCodeErrorText("");
  };

  const publishWorkflowToOtherOrganization = () => {
    let hasError = false;

    if (!organizationId) {
      setHasOrganizationError(true);
      hasError = true;
    }

    if (!workflowName) {
      setHasWorkflowNameError(true);
      hasError = true;
    }

    // Category and code are only required for workflows, not service plans
    if (!isServicePlan) {
      if (!categoryId) {
        setHasCategoryError(true);
        hasError = true;
      }

      if (!workflowCode) {
        setHasWorkflowCodeError(true);
        hasError = true;
      }
    }

    if (communityIds.length === 0) {
      setHasCommunityError(true);
      hasError = true;
    }

    if (!hasError) {
      let params;

      if (isServicePlan) {
        // Service Plan parameters - create new template in target organization
        params = {
          name: workflowName,
          communityIds,
          organizationId,
          status: "PUBLISHED", // 直接发布状态
          content: currentWorkflowData.content || currentWorkflowData.templateContent, // 保持原有内容
        };
      } else {
        // Workflow parameters - create new template in target organization
        params = {
          name: workflowName,
          communityIds,
          organizationId,
          categoryId,
          code: workflowCode,
          status: "PUBLISHED", // 直接发布状态
          content: currentWorkflowData.content || currentWorkflowData.templateContent, // 保持原有内容
          documentESign: currentWorkflowData.documentESign || false,
          ongoingWorkflow: currentWorkflowData.ongoingWorkflow || false,
          scoreWorkflow: currentWorkflowData.scoreWorkflow || false,
          isCreateServicePlan: currentWorkflowData.isCreateServicePlan || false,
        };
      }

      setButtonDisabled(true);

      const mutation = isServicePlan ? publishServicePlanMutation : publishWorkflowMutation;

      mutation.mutate(params, {
        onSuccess: () => {
          onClose();
          setButtonDisabled(false);
        },
        onError: (error) => {
          setButtonDisabled(false);
          if (error?.message === "Template code already existed!") {
            setHasWorkflowCodeError(true);
            setWorkflowCodeErrorText("Template code already existed! Please enter a different code.");
          } else {
            // 处理其他错误
            console.error(`Publish ${isServicePlan ? "service plan" : "workflow"} error:`, error);
          }
        },
      });
    }
  };

  const handleCancel = () => {
    // 重置表单状态
    setWorkflowName("");
    setCommunityIds([]);
    setOrganizationId("");
    setCategoryId("");
    setWorkflowCode("");
    setHasOrganizationError(false);
    setHasCommunityError(false);
    setHasWorkflowNameError(false);
    setHasCategoryError(false);
    setHasWorkflowCodeError(false);
    setWorkflowCodeErrorText("");
    onCancel();
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        title={`Publish ${isServicePlan ? "Service Plan" : "Workflow"} to Other Organization`}
        className="workflow-modal"
        hasFooter={true}
        hasCloseBtn={true}
        onClose={handleCancel}
        renderFooter={() => {
          return (
            <div>
              <Button outline color="success" onClick={handleCancel} disabled={buttonDisabled}>
                Cancel
              </Button>
              <Button
                onClick={publishWorkflowToOtherOrganization}
                color="success"
                disabled={
                  hasWorkflowNameError ||
                  (isServicePlan ? false : hasCategoryError) ||
                  hasCommunityError ||
                  hasOrganizationError ||
                  (isServicePlan ? false : hasWorkflowCodeError) ||
                  buttonDisabled
                }
              >
                Publish
              </Button>
            </div>
          );
        }}
      >
        <div style={{ padding: 20 }}>
          <Row>
            <Col>
              <SelectField
                name="organizationId"
                value={organizationId}
                label="Organization*"
                isMultiple={false}
                hasSearchBox={true}
                isDisabled={false}
                hasError={hasOrganizationError}
                className="VendorsForm-TextField"
                errorText={hasOrganizationError ? "Please select an organization" : ""}
                onChange={changeOrganizationIds}
                options={organizationOptions}
                isLoading={isOrgLoading}
                placeholder="Select Organization"
              />
            </Col>
            <Col>
              <SelectField
                name="communityIds"
                value={communityIds}
                label="Community*"
                isMultiple={true}
                hasSearchBox={true}
                hasError={hasCommunityError}
                className="VendorsForm-TextField"
                errorText={hasCommunityError ? "Please select at least one community" : ""}
                onChange={changeCommunityId}
                options={communityOptions}
                isLoading={isCommunityLoading}
                placeholder="Select Communities"
                isDisabled={!organizationId}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={isServicePlan ? 12 : 6} md={isServicePlan ? 12 : 6} style={{ marginBottom: 20 }}>
              <TextField
                name="workflowName"
                label={`${isServicePlan ? "Service Plan" : "Workflow"} Template Name*`}
                value={workflowName}
                hasError={hasWorkflowNameError}
                errorText={
                  hasWorkflowNameError ? `Please enter a ${isServicePlan ? "service plan" : "workflow"} name` : ""
                }
                onChange={changeWorkflowName}
                placeholder={`Enter ${isServicePlan ? "service plan" : "workflow"} name`}
              />
            </Col>

            {!isServicePlan && (
              <Col>
                <SelectField
                  name="categoryId"
                  value={categoryId}
                  label="Workflow Category*"
                  isMultiple={false}
                  hasSearchBox={true}
                  hasError={hasCategoryError}
                  className="VendorsForm-TextField"
                  errorText={hasCategoryError ? "Please select a category" : ""}
                  onChange={changeCategoryId}
                  options={categoryOptions}
                  isLoading={isCategoryLoading}
                  placeholder="Select Category"
                  isDisabled={!organizationId}
                />
              </Col>
            )}
          </Row>

          {!isServicePlan && (
            <Row>
              <Col>
                <TextField
                  name="workflowCode"
                  label="Workflow Template Code*"
                  value={workflowCode}
                  hasError={hasWorkflowCodeError}
                  errorText={workflowCodeErrorText || (hasWorkflowCodeError ? "Please enter a workflow code" : "")}
                  onChange={changeWorkflowCode}
                  placeholder="Enter workflow code"
                />
              </Col>
            </Row>
          )}
        </div>
      </Modal>
    </>
  );
};

export default PublishWorkflowToOtherOrganization;
