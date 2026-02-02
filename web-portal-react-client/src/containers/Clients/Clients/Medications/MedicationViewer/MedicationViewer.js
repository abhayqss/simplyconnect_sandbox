import React, { memo } from "react";

import { Button } from "reactstrap";

import { Modal } from "components";

import { MedicationDetails } from "containers/common/details";

import "./MedicationViewer.scss";

function MedicationViewer({
  isOpen,
  onClose,
  clientId,
  medicationId,
  isCanEdit,
  onDeleteMedication,
  onEditMedication,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      hasCloseBtn={false}
      title="View Medication"
      className="MedicationViewer"
      renderFooter={() => (
        <div className={isCanEdit ? "Medication-Viewer-Bottom-Btn-SpaceBetween" : ""}>
          {isCanEdit && (
            <div>
              <Button color="danger" outline onClick={onDeleteMedication}>
                Delete
              </Button>
              <Button color="success" outline onClick={onEditMedication}>
                Edit
              </Button>
            </div>
          )}
          <Button color="success" onClick={onClose}>
            Close
          </Button>
        </div>
      )}
    >
      <MedicationDetails clientId={clientId} medicationId={medicationId} />
    </Modal>
  );
}

export default memo(MedicationViewer);
