import { Button, Col, Row } from "reactstrap";
import React, { useEffect, useState } from "react";
import { Loader, Modal } from "../../../../components";
import { SelectField, TextField } from "../../../../components/Form";
import service from "../../../../services/DirectoryService";
import adminWorkflowCreateService from "../../../../services/AdminWorkflowCreateService";

const CopyCustomTemplate = (props) => {
  const { isOpen, onClose, onCancel, organizations, propOrganizationId, propCommunityId, currentCopyData } = props;

  const [hasOrganizationError] = useState();
  const [hasCommunityError] = useState();
  const [communityOptions, setCommunityOptions] = useState();
  const [hasServicePlanNameError] = useState(false);

  const [servicePlanName, setServicePlanName] = useState("");
  const [communityIds, setCommunityIds] = useState([propCommunityId]);

  const [organizationId, setOrganizationId] = useState(propOrganizationId);

  const [buttonDisabled, setButtonDisabled] = useState(false);

  useEffect(() => {
    if (currentCopyData) {
      setServicePlanName(currentCopyData?.name);
      setCommunityIds(currentCopyData?.communityIds);
    }
  }, [currentCopyData]);

  useEffect(() => {
    setOrganizationId(propOrganizationId);
  }, [propOrganizationId]);

  useEffect(() => {
    if (organizationId) {
      getCommunityOptions(organizationId);
    }
  }, [organizationId]);

  const changeOrganizationIds = (fields, value) => {
    setOrganizationId(value);
  };

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

  const changeCommunityId = (fields, value) => {
    setCommunityIds(value);
  };

  const changeServicePlanName = (fields, value) => {
    setServicePlanName(value);
  };

  const copyServicePlan = () => {
    const params = {
      id: currentCopyData.id,
      name: servicePlanName,
      communityIds: communityIds,
      organizationId: organizationId,
      status: "DRAFT",
    };

    setButtonDisabled(true);

    adminWorkflowCreateService
      .copyServicePlanTemplate(params)
      .then((res) => {
        if (res.success) {
          onClose();
          setButtonDisabled(false);
        }
      })
      .catch((err) => {
        setButtonDisabled(false);
      });
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        title={`Copy service plan template`}
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

              <Button onClick={copyServicePlan} color="success"  disabled={buttonDisabled}>
                Save
              </Button>
            </div>
          );
        }}
      >
        {buttonDisabled && <Loader hasBackdrop />}

        <div style={{ padding: 20 }}>
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
              <TextField
                name="servicePlanName"
                label={"Service Plan Name*"}
                value={servicePlanName}
                hasError={hasServicePlanNameError}
                errorText={hasServicePlanNameError ? "Please fill in the required field" : ""}
                onChange={changeServicePlanName}
              />
            </Col>
          </Row>
        </div>
      </Modal>
    </>
  );
};

export default CopyCustomTemplate;
