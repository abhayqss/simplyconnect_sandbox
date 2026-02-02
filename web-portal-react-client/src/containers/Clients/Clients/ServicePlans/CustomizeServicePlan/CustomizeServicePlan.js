import { Modal } from "../../../../../components";
import "./index.scss";
import { CheckboxField, SelectField } from "../../../../../components/Form";
import React, { useEffect, useState } from "react";
import { Button } from "reactstrap";
import adminWorkflowCreateService from "../../../../../services/AdminWorkflowCreateService";
import ServicePlanEditModule from "./ServicePlanEditModule";
import { ErrorDialog, SuccessDialog } from "../../../../../components/dialogs";

const CustomizeServicePlan = (props) => {
  const {
    setShowAddNewPlanModel,
    showAddNewPlanModel,
    noCustomizePlanClick,
    clientId,
    refresh,
    showServicePlanEditModal,
    setShowServicePlanEditModal,
    servicePlanTemplate,
    setServicePlanTemplate,
    isEditServicePlan,
    servicePlanTemplatesData,
    setIsEditServicePlan,
    editServicePlanId,
    setEditServicePlanId,
    editServicePlanTemplateId,
    setEditServicePlanTemplateId,
    editServiceScoring,
    clientDetailData,
  } = props;
  const [isCustomizedServicePlan, setIsCustomizedServicePlan] = useState(false);
  const [servicePlanModels, setServicePlanModels] = useState([]);

  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [showErrorDialog, setShowErrorDialog] = useState(false);

  useEffect(() => {
    if (clientId) {
      adminWorkflowCreateService.getAllServicePlanTemplates(clientId).then((res) => {
        const result = res.data.map((item) => ({ text: item.name, value: item.id }));
        setServicePlanModels(result);
      });
    }
  }, [clientId]);

  const changeCategoryId = (fields, value) => {
    setEditServicePlanTemplateId(value);
  };

  const linkToServicePlan = (id) => {
    // 获取client详情，填充预填字段

    adminWorkflowCreateService.getDetailOfCurrentServicePlanTemplates(id).then((res) => {
      setServicePlanTemplate(res.data);
      setShowAddNewPlanModel(false);
      setIsCustomizedServicePlan(false);
      setShowServicePlanEditModal(true);
    });
  };

  return (
    <>
      <Modal
        isOpen={showAddNewPlanModel}
        title={`Add New Plan`}
        className="serviceplanModel"
        hasFooter={false}
        hasCloseBtn={true}
        // isCentered={true}
        onClose={() => {
          setShowAddNewPlanModel(false);
          setIsCustomizedServicePlan(false);
        }}
      >
        <CheckboxField
          type="text"
          name="isItACustomizedTemplate"
          label="Customized Service Plan"
          value={isCustomizedServicePlan}
          onChange={() => {
            setIsCustomizedServicePlan(!isCustomizedServicePlan);
          }}
        />

        {!isCustomizedServicePlan && (
          <Button color="success" className="AddServicePlanBtn" onClick={() => noCustomizePlanClick()}>
            Add New Plan
          </Button>
        )}

        {isCustomizedServicePlan && (
          <SelectField
            name="servicePlanTemplateId"
            value={editServicePlanTemplateId}
            label="Service Plan Model*"
            isMultiple={false}
            hasSearchBox={false}
            // hasError={hasCategoryError}
            className="VendorsForm-TextField"
            // errorText={hasCategoryError ? "Please fill in the required field" : ""}
            onChange={changeCategoryId}
            options={servicePlanModels}
          />
        )}

        {isCustomizedServicePlan && (
          <Button
            color="success"
            className="AddServicePlanBtn"
            disabled={servicePlanModels.length === 0}
            onClick={() => {
              linkToServicePlan(editServicePlanTemplateId);
              setIsEditServicePlan(false);
            }}
          >
            Add New Service Plan
          </Button>
        )}
      </Modal>

      {showServicePlanEditModal && (
        <ServicePlanEditModule
          setShowServicePlanEditModal={setShowServicePlanEditModal}
          showServicePlanEditModal={showServicePlanEditModal}
          servicePlanTemplate={servicePlanTemplate}
          templateId={editServicePlanTemplateId}
          clientId={clientId}
          setShowSuccessDialog={setShowSuccessDialog}
          isEditServicePlan={isEditServicePlan}
          servicePlanTemplatesData={servicePlanTemplatesData}
          editServicePlanId={editServicePlanId}
          setEditServicePlanId={setEditServicePlanId}
          setEditServicePlanTemplateId={setEditServicePlanTemplateId}
          editServiceScoring={editServiceScoring}
          showErrorDialog={showErrorDialog}
          clientDetailData={clientDetailData}
        />
      )}

      {showSuccessDialog && (
        <SuccessDialog
          isOpen
          title={`The Service Plan has been ${isEditServicePlan ? "edited" : "added"} successfully.`}
          buttons={[
            {
              text: "OK",
              onClick: () => {
                refresh();
                setShowSuccessDialog(false);
              },
            },
          ]}
        />
      )}

      {showErrorDialog && (
        <ErrorDialog
          isOpen
          title="Service plan save failed, please contact the administrator."
          buttons={[
            {
              text: "Close",
              onClick: () => {
                setShowErrorDialog(false);
              },
            },
          ]}
        />
      )}
    </>
  );
};

export default CustomizeServicePlan;
