import "./AssociationConnectionModal.scss";
import Modal from "../../../../components/Modal/Modal";
import React, { useEffect, useState } from "react";
import AssociationConnectionModalSelect from "./AssociationConnectionModalSelect";
import AssociationConnectionModalConfirm from "./AssociationConnectionModalConfirm";
import Tabs from "../../../../components/Tabs/Tabs";
import Scrollable from "../../../../components/Calendar/Scrollable/Scrollable";
import { map } from "underscore";
import { Button } from "reactstrap";
import adminAssociationsService from "../../../../services/AssociationsService";
import { Loader } from "../../../../components";

const AssociationConnectionModal = (props) => {
  const {
    isOpen,
    onClose,
    tab,
    modalConfirm,
    finalSelectedData,
    setFinalSelectedData,
    stepOneSelectedData,
    setStepOneSelectedData,
    isSubmitting,
  } = props;
  const [step, setStep] = useState(0);

  useEffect(() => {
    setStep(0);
  }, []);

  const tabText = () => {
    if (tab === 0) {
      return "Communities";
    } else if (tab === 1) {
      return "Vendors";
    } else if (tab === 2) {
      return "Organizations";
    }
  };
  const onNext = () => {
    if (stepOneSelectedData.length > 0) {
      return setStep(1);
    } else {
      alert("error, nothing select");
    }
  };

  const onConfirm = () => {
    modalConfirm();
  };

  const onStepOneCancel = () => {
    setStepOneSelectedData([]);
    onClose();
  };
  const onStepTwoCancel = () => {
    onClose();
  };

  const onChangeTopTabs = (index) => {
    if (index === 1) {
      if (stepOneSelectedData.length === 0) {
        alert("error", "no data select");
      } else {
        setStep(index);
      }
    } else {
      setStep(index);
    }
  };

  return (
    <>
      <Modal
        isOpen={isOpen}
        className="Association-connection-modal"
        hasCloseBtn={true}
        hasFooter={true}
        onClose={onClose}
        title={"Link" + " " + tabText()}
        renderFooter={() => (
          <>
            {step === 0 && (
              <>
                <Button outline color="success" onClick={onStepOneCancel}>
                  Cancel
                </Button>
                <Button color="success" onClick={onNext} disabled={!stepOneSelectedData.length}>
                  Select
                </Button>
              </>
            )}
            {step === 1 && (
              <>
                <Button outline color="success" onClick={onStepTwoCancel}>
                  Cancel
                </Button>
                <Button color="success" onClick={onConfirm} disabled={!finalSelectedData.length}>
                  Submit
                </Button>
              </>
            )}
          </>
        )}
      >
        <div className={`Association-connection-modal-content`} style={{ position: "relative" }}>
          {isSubmitting && <Loader isCentered hasBackdrop />}
          <Tabs
            className="Association-connection-modal-tabs"
            items={[
              { title: "Select", isActive: step === 0 },
              { title: "Confirm", isActive: step === 1, isDisabled: !stepOneSelectedData.length },
            ]}
            onChange={(index) => {
              onChangeTopTabs(index);
            }}
          />
          {step === 0 && (
            <AssociationConnectionModalSelect
              tab={tab}
              stepOneSelectedData={stepOneSelectedData}
              setStepOneSelectedData={setStepOneSelectedData}
            />
          )}
          {step === 1 && (
            <AssociationConnectionModalConfirm
              tab={tab}
              stepOneSelectedData={stepOneSelectedData}
              selectedData={finalSelectedData}
              setSelectedData={setFinalSelectedData}
              isSubmitting={isSubmitting}
              setStepOneSelectedData={setStepOneSelectedData}
            />
          )}
        </div>
      </Modal>
    </>
  );
};

export default AssociationConnectionModal;
