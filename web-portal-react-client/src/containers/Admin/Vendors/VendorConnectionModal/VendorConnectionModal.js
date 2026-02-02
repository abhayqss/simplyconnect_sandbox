import "./VendorConnectionModal.scss";
import Modal from "components/Modal/Modal";
import React, { useEffect, useState } from "react";
import VendorConnectionModalSelect from "./VendorConnectionModalSelect";
import VendorConnectionModalConfirm from "./VendorConnectionModalConfirm";
import Tabs from "components/Tabs/Tabs";
import { map } from "underscore";
import { Button } from "reactstrap";
import adminVendorService from "../../../../services/AdminVendorService";

const VendorConnectionModal = (props) => {
  const {
    isOpen,
    onClose,
    tab,
    originLinkData = [],
    vendorId,
    AddVendorAssociateCommunities,
    AddVendorAssociateOrganizations,
  } = props;
  const [step, setStep] = useState(0);
  const [stepOneSelectedData, setStepOneSelectedData] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [getTab, setGetTab] = useState(tab);

  useEffect(() => {
    if (getTab === 1) {
      const data = originLinkData?.map((item) => {
        return item.organization;
      });
      setStepOneSelectedData(data);
    } else {
      setStepOneSelectedData(originLinkData);
    }
  }, [getTab, originLinkData, isOpen]);

  useEffect(() => {
    setStep(0);
  }, [isOpen]);

  const tabText = () => {
    if (tab === 0) {
      return "Communities";
    } else if (tab === 1) {
      return "Organizations";
    } else if (tab === 2) {
      return "Refer History";
    } else if (tab === 3) {
      return "Team";
    }
  };
  const onNext = () => {
    if (stepOneSelectedData.length > 0) {
      return setStep(1);
    }
  };

  const onConfirm = () => {
    setIsFetching(true);
    const referIds = [];
    stepOneSelectedData.map((item) => {
      referIds.push(item.id);
    });
    if (tab === 0) {
      AddVendorAssociateCommunities({ vendorId, referIds });
    } else if (tab === 1) {
      AddVendorAssociateOrganizations({ vendorId, referIds });
    }
  };

  const onStepOneCancel = () => {
    onClose();
  };
  const onStepTwoCancel = () => {
    setStepOneSelectedData([]);
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
                <Button color="success" onClick={onConfirm} disabled={!stepOneSelectedData.length}>
                  Submit
                </Button>
              </>
            )}
          </>
        )}
      >
        <div className="Association-connection-modal-content">
          <Tabs
            className="Association-connection-modal-tabs"
            items={[
              { title: "Select", isActive: step === 0 },
              { title: "Confirm", isActive: step === 1, isDisabled: !stepOneSelectedData?.length },
            ]}
            onChange={(index) => {
              onChangeTopTabs(index);
            }}
          />
          {step === 0 && (
            <VendorConnectionModalSelect
              isFetching={isFetching}
              tab={tab}
              stepOneSelectedData={stepOneSelectedData}
              setStepOneSelectedData={setStepOneSelectedData}
            />
          )}
          {step === 1 && (
            <VendorConnectionModalConfirm
              isSubmitting={isSubmitting}
              tab={tab}
              stepOneSelectedData={stepOneSelectedData}
              setStepOneSelectedData={setStepOneSelectedData}
            />
          )}
        </div>
      </Modal>
    </>
  );
};

export default VendorConnectionModal;
