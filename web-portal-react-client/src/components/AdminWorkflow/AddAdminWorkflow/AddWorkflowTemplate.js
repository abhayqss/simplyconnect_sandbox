import Modal from "../../Modal/Modal";
import React, { useEffect, useState } from "react";
import { Button, Col, Row } from "reactstrap";
import { CheckboxField, SelectField, TextField } from "../../Form";
import service from "services/DirectoryService";
import "./AddWorkflowTemplate.scss";
import adminWorkflowCreateService from "../../../services/AdminWorkflowCreateService";
import getWorkflowLibraryHooks from "../../../hooks/workflowLibraryHooks";

const AddWorkflowTemplate = (props) => {
  const {
    isOpen,
    onConfirm,
    onCancel,
    organizations,
    propOrganizationId,
    propCommunityId,
    isEdit,
    editData,
    setIsEdit,
    setEditData,
    editWorkflow,
  } = props;
  const [workflowTemplateName, setWorkflowTemplateName] = useState("");
  const [workflowCode, setWorkflowCode] = useState("");
  const [hasWorkflowCodeError, setHasWorkflowCodeError] = useState(false);
  const [servicePlanName, setServicePlanName] = useState("");
  const [hasWorkflowTemplateNameError, setHasWorkflowTemplateNameError] = useState();
  const [hasServicePlanNameError, setHasServicePlanNameError] = useState(false);
  const [documentESign, setDocumentESign] = useState(false);
  const [organizationId, setOrganizationId] = useState(propOrganizationId);
  const [hasOrganizationError] = useState();
  const [communityIds, setCommunityIds] = useState([propCommunityId]);
  const [hasCommunityError, setHasCommunityError] = useState();
  const [communityOptions, setCommunityOptions] = useState();
  const [categoryId, setCategoryId] = useState(null);
  const [hasCategoryError, setHasCategoryError] = useState();
  // 统一 hooks
  const { useWorkflowCategoryOptions } = getWorkflowLibraryHooks();
  const {
    data: categoryOptions = [],
    isLoading: isCategoryLoading,
    error: categoryQueryError,
  } = useWorkflowCategoryOptions(organizationId);

  const [workflowTypeOptions, setWorkflowTypeOptions] = useState([
    { value: false, label: "Ongoing Workflow", id: "ongoingWorkflow" },
    {
      value: false,
      label: "Score Workflow",
      id: "scoreWorkflow",
    },
  ]);

  const [isCreateServicePlan, setIsCreateServicePlan] = useState(false);

  useEffect(() => {
    if (isEdit) {
      setOrganizationId(editData.organizationId);
      setCategoryId(editData.categoryId);
      setWorkflowTemplateName(editData.name);
      setDocumentESign(!!editData.documentESign);
      setWorkflowTypeOptions([
        { value: editData.ongoingWorkflow, label: "Ongoing Workflow", id: "ongoingWorkflow" },
        { value: editData.scoreWorkflow, label: "Score Workflow", id: "scoreWorkflow" },
      ]);
      setWorkflowCode(editData.code || "");
    } else {
      setWorkflowCode("");
    }

    return () => {
      setEditData({});
      setIsEdit(false);
    };
  }, [editData, isEdit]);

  useEffect(() => {
    setOrganizationId(propOrganizationId);
  }, [propOrganizationId]);

  useEffect(() => {
    if (organizationId) {
      getCommunityOptions(organizationId);
    }
  }, [organizationId]);

  const getCommunityOptions = (props) => {
    service.findCommunities({ organizationId: props }).then((res) => {
      if (res.success) {
        const data = res?.data.map((item) => {
          return { value: item.id, text: item.name };
        });
        setCommunityOptions(data);
      }
    });
  };

  const changeWorkflowTemplateName = (fields, value) => {
    const sanitizedValue = value.replace(/\./g, "");
    setWorkflowTemplateName(sanitizedValue);
  };
  const changeServicePlanName = (fields, value) => {
    const sanitizedValue = value.replace(/\./g, "");
    setServicePlanName(sanitizedValue);
  };

  const changeCategoryId = (fields, value) => {
    setCategoryId(value);
  };
  const changeOrganizationIds = (fields, value) => {
    setOrganizationId(value);
  };
  const changeCommunityId = (fields, value) => {
    localStorage.setItem("triggerCurrentComId", JSON.stringify(value));
    setCommunityIds(value);
  };

  const getWorkflowTypeValue = (data) => {
    const item = workflowTypeOptions.find((item) => item.id === data);
    return item.value;
  };

  useEffect(() => {
    if (workflowTemplateName) {
      setHasWorkflowTemplateNameError(false);
    }
    if (categoryId) {
      setHasCategoryError(false);
    }

    if (communityIds.length > 0) {
      setHasCommunityError(false);
    }
    if (isCreateServicePlan) {
      if (servicePlanName) {
        setHasServicePlanNameError(false);
      }
    }
    if (!isCreateServicePlan && workflowCode) {
      setHasWorkflowCodeError(false);
    }
  }, [workflowTemplateName, categoryId, communityIds, servicePlanName, workflowCode, isCreateServicePlan]);

  const confirmAdd = () => {
    let hasError = false;

    if (communityIds.length === 0) {
      setHasCommunityError(true);
      hasError = true;
    }

    if (!isCreateServicePlan) {
      if (!workflowTemplateName) {
        setHasWorkflowTemplateNameError(true);
        hasError = true;
      }
      if (!categoryId) {
        setHasCategoryError(true);
        hasError = true;
      }
      if (!workflowCode) {
        setHasWorkflowCodeError(true);
        hasError = true;
      }
    }

    if (isCreateServicePlan) {
      if (!servicePlanName) {
        setHasServicePlanNameError(true);
        hasError = true;
      }
    }

    if (hasError) {
      return;
    }

    onConfirm({
      organizationId,
      communityIds,
      name: workflowTemplateName,
      categoryId,
      ongoingWorkflow: getWorkflowTypeValue("ongoingWorkflow"),
      scoreWorkflow: getWorkflowTypeValue("scoreWorkflow"),
      documentESign,
      isCreateServicePlan,
      servicePlanName,
      code: !isCreateServicePlan ? workflowCode : undefined,
    });
  };

  const editWorkflowTemplate = () => {
    const data = {
      organizationId,
      communityIds,
      name: workflowTemplateName,
      categoryId,
      ongoingWorkflow: getWorkflowTypeValue("ongoingWorkflow"),
      scoreWorkflow: getWorkflowTypeValue("scoreWorkflow"),
      documentESign,
      content: editData.content,
      code: editData.code,
      id: editData.id,
      status: "PUBLISHED",
    };
    adminWorkflowCreateService.createWorkflow(data).then((res) => {
      if (res.success) {
        editWorkflow();
      }
    });
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        title={`${isEdit ? "Edit" : "Add"} template`}
        className="workflow-modal"
        hasFooter={true}
        hasCloseBtn={true}
        onClose={onCancel}
        renderFooter={() => {
          return (
            <div>
              <Button outline color="success" onClick={onCancel}>
                Cancel
              </Button>

              {isEdit ? (
                <Button onClick={editWorkflowTemplate} color="success" outline>
                  Save
                </Button>
              ) : (
                <Button color="success" onClick={confirmAdd}>
                  Confirm
                </Button>
              )}
            </div>
          );
        }}
      >
        <div className="workflow-modal-content">
          <Row>
            <Col>
              <SelectField
                name="orgaizationId"
                value={organizationId}
                label="Organization*"
                isMultiple={false}
                hasSearchBox={false}
                isDisabled={true}
                hasError={hasOrganizationError}
                className="VendorsForm-TextField"
                errorText={hasOrganizationError ? "Please fill in the required field" : ""}
                onChange={changeOrganizationIds}
                options={organizations}
              />
            </Col>
            <Col>
              <SelectField
                name="communityIds"
                value={communityIds}
                label="Community*"
                isMultiple={true}
                hasSearchBox={false}
                hasError={hasCommunityError}
                className="VendorsForm-TextField"
                errorText={hasCommunityError ? "Please fill in the required field" : ""}
                onChange={changeCommunityId}
                options={communityOptions}
              />
            </Col>
          </Row>
          <Row>
            <Col lg={6} md={6} style={{ marginBottom: 20 }}>
              <CheckboxField
                type="text"
                name="isCreateServicePlan"
                value={isCreateServicePlan}
                label="Create Service Plan"
                onChange={() => {
                  setIsCreateServicePlan(!isCreateServicePlan);
                }}
              />
            </Col>

            {isCreateServicePlan && (
              <Col lg={6} md={6} style={{ marginBottom: 20 }}>
                <TextField
                  name="servicePlanName"
                  label={"Service Plan Name*"}
                  value={servicePlanName}
                  hasError={hasServicePlanNameError}
                  errorText={hasServicePlanNameError ? "Please fill in the required field" : ""}
                  onChange={changeServicePlanName}
                />
              </Col>
            )}
          </Row>

          {!isCreateServicePlan && (
            <>
              <Row>
                <Col>
                  <TextField
                    name="workflowTemplateName"
                    label={"Workflow Template Name*"}
                    value={workflowTemplateName}
                    hasError={hasWorkflowTemplateNameError}
                    errorText={hasWorkflowTemplateNameError ? "Please fill in the required field" : ""}
                    onChange={changeWorkflowTemplateName}
                  />
                </Col>
                <Col>
                  <SelectField
                    name="categoryId"
                    value={categoryId}
                    label="Workflow Category*"
                    isMultiple={false}
                    hasSearchBox={false}
                    hasError={hasCategoryError}
                    loading={isCategoryLoading}
                    className="VendorsForm-TextField"
                    errorText={hasCategoryError ? "Please fill in the required field" : ""}
                    onChange={changeCategoryId}
                    options={categoryOptions}
                  />
                </Col>
              </Row>
              <Row>
                {!isCreateServicePlan && (
                  <Col>
                    <TextField
                      name="workflowCode"
                      label="Workflow Template Code*"
                      value={workflowCode}
                      hasError={hasWorkflowCodeError}
                      errorText={hasWorkflowCodeError ? "Please fill in the required field" : ""}
                      onChange={(fields, value) => {
                        setWorkflowCode(value.replace(/\./g, ""));
                        setHasWorkflowCodeError(false);
                      }}
                    />
                  </Col>
                )}
              </Row>
            </>
          )}
        </div>
      </Modal>
    </>
  );
};

export default AddWorkflowTemplate;
