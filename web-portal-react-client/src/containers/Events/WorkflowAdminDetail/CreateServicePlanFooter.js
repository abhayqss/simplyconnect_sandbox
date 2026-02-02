import { CheckboxField } from "../../../components/Form";
import { Button } from "reactstrap";
import React from "react";

export const CreateServicePlanFooter = (props) => {
  const { isFetching = false, setShowCreateServicePlanModel } = props;

  const onClose = () => {
    setShowCreateServicePlanModel(false);
  };

  const onHide = () => {};

  const onSave = () => {};

  return (
    <>
      <div className="AssessmentToServicePlanEditor-ActionGroup">
        <CheckboxField
          name="isAddedVisible"
          // value={areAvailableNeedsVisible}
          label="Show Added"
          isDisabled={isFetching}
          className="AssessmentToServicePlanEditor-Action"
          // onChange={(name, value) => setAvailableNeedsVisible(value)}
        />
        <CheckboxField
          name="isHiddenVisible"
          // value={areExcludedQuestionsVisible}
          label="Show Hidden"
          isDisabled={isFetching}
          className="AssessmentToServicePlanEditor-Action"
          // onChange={(name, value) => setExcludedQuestionsVisible(value)}
        />
      </div>
      <div className="AssessmentToServicePlanEditor-ActionGroup">
        <Button
          outline
          color="success"
          disabled={isFetching}
          onClick={onClose}
          className="AssessmentToServicePlanEditor-Action"
        >
          Cancel
        </Button>
        <Button
          outline
          color="success"
          // onClick={selected}
          // disabled={isFetching || isEmpty(selected) || areAllSelectedQuestionsExcluded}
          className="AssessmentToServicePlanEditor-Action"
        >
          Hide
          {/*Hide{selected.length && !areAllSelectedQuestionsExcluded ? ` (${selected.length})` : ''}*/}
        </Button>
        <Button
          color="success"
          onClick={onSave}
          // disabled={isFetching || isEmpty(selected)}
          className="AssessmentToServicePlanEditor-Action AssessmentToServicePlanEditor-SaveBtn"
        >
          Add<span className="AssessmentToServicePlanEditor-SaveBtnOptText"> to Service Plan</span>
          {/*{selected.length ? ` (${selected.length})` : ""}*/}
        </Button>
      </div>
    </>
  );
};
