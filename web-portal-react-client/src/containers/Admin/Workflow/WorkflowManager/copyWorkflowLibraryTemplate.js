import React, { useEffect, useState } from "react";
import { Button, Col, Row } from "reactstrap";
import { Modal } from "../../../../components";
import { SelectField, TextField } from "../../../../components/Form";
import getWorkflowLibraryHooks from "../../../../hooks/workflowLibraryHooks";

const CopyWorkflowLibraryTemplate = (props) => {
  const { isOpen, onClose, onCancel, organizations, propOrganizationId, propCommunityId, currentCopyData } = props;

  const [hasOrganizationError] = useState(false);
  const [hasCommunityError, setHasCommunityError] = useState(false);
  const [hasWorkflowNameError, setHasWorkflowNameError] = useState(false);
  const [hasCategoryError, setHasCategoryError] = useState(false);

  const [workflowName, setWorkflowName] = useState("");
  const [communityIds, setCommunityIds] = useState([propCommunityId]);
  const [organizationId, setOrganizationId] = useState(propOrganizationId);

  const [categoryId, setCategoryId] = useState();

  const [workflowCode, setWorkflowCode] = useState("");
  const [hasWorkflowCodeError, setHasWorkflowCodeError] = useState(false);
  const [workflowCodeErrorText, setWorkflowCodeErrorText] = useState("");

  const [buttonDisabled, setButtonDisabled] = useState(false);

  // 统一 hooks 引用
  const { useOrganizationOptions, useCommunityOptions, useWorkflowCategoryOptions, useCopyWorkflowLibraryTemplate } =
    getWorkflowLibraryHooks();

  const { data: organizationOptions = [], isLoading: isOrgLoading } = useOrganizationOptions();
  const { data: communityOptions = [], isLoading: isCommunityLoading } = useCommunityOptions(organizationId);
  const {
    data: categoryOptions = [],
    isLoading: isCategoryLoading,
    error: categoryQueryError,
  } = useWorkflowCategoryOptions(organizationId);

  const copyWorkflowMutation = useCopyWorkflowLibraryTemplate();

  useEffect(() => {
    if (currentCopyData) {
      setWorkflowName(currentCopyData?.name);
      setCommunityIds(currentCopyData?.communityIds);
      setCategoryId(currentCopyData?.categoryId);
    }
  }, [currentCopyData]);

  useEffect(() => {
    setOrganizationId(propOrganizationId);
  }, [propOrganizationId]);

  const changeOrganizationIds = (fields, value) => {
    setOrganizationId(value);
  };

  // const getCategoryOptions = () => {
  //   adminWorkflowCategoryService.getAllCategory({ organizationId }).then((res) => {
  //     if (res.success) {
  //       const data = res.data.map((item) => {
  //         return { value: item.id, text: item.categoryName };
  //       });
  //       setCategoryOptions(data);
  //       if (data.length === 0) {
  //         setHasCategoryError(true);
  //       } else {
  //         setHasCategoryError(false);
  //       }
  //     }
  //   });
  // };

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

  const copyServicePlan = () => {
    let hasError = false;
    if (!workflowName) {
      setHasWorkflowNameError(true);
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

    if (!hasError) {
      const params = {
        id: currentCopyData.id,
        name: workflowName,
        communityIds,
        organizationId,
        categoryId,
        code: workflowCode,
      };

      setButtonDisabled(true);

      copyWorkflowMutation.mutate(params, {
        onSuccess: () => {
          onClose();
          setButtonDisabled(false);
        },
        onError: (error) => {
          setButtonDisabled(false);
          if (error?.message === "Template code already existed!") {
            setHasWorkflowCodeError(true);
            setWorkflowCodeErrorText("Template code already existed! Please enter a different code.");
          }
        },
      });
    }
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        title={`Copy Workflow template`}
        className="workflow-modal"
        hasFooter={true}
        hasCloseBtn={true}
        onClose={onCancel}
        renderFooter={() => {
          return (
            <div>
              <Button outline color="success" onClick={onCancel} disabled={buttonDisabled}>
                Cancel
              </Button>
              <Button
                onClick={copyServicePlan}
                color="success"
                disabled={hasWorkflowNameError || hasCategoryError || hasCommunityError || buttonDisabled}
              >
                Save
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
                hasSearchBox={false}
                isDisabled={true}
                hasError={hasOrganizationError}
                className="VendorsForm-TextField"
                errorText={hasOrganizationError ? "Please fill in the required field" : ""}
                onChange={changeOrganizationIds}
                options={organizationOptions}
                isLoading={isOrgLoading}
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
                isLoading={isCommunityLoading}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={6} md={6} style={{ marginBottom: 20 }}>
              <TextField
                name="workflowName"
                label={"Workflow Template Name*"}
                value={workflowName}
                hasError={hasWorkflowNameError}
                errorText={hasWorkflowNameError ? "Please fill in the required field" : ""}
                onChange={changeWorkflowName}
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
                className="VendorsForm-TextField"
                errorText={hasCategoryError ? "Please fill in the required field" : ""}
                onChange={changeCategoryId}
                options={categoryOptions}
                isLoading={isCategoryLoading}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <TextField
                name="workflowCode"
                label={"Workflow Template Code*"}
                value={workflowCode}
                hasError={hasWorkflowCodeError}
                errorText={workflowCodeErrorText || (hasWorkflowCodeError ? "Please fill in the required code" : "")}
                onChange={(fields, value) => {
                  setWorkflowCode(value.replace(/\./g, ""));
                  setHasWorkflowCodeError(false);
                  setWorkflowCodeErrorText("");
                }}
              />
            </Col>
          </Row>
        </div>
      </Modal>
    </>
  );
};

export default CopyWorkflowLibraryTemplate;
