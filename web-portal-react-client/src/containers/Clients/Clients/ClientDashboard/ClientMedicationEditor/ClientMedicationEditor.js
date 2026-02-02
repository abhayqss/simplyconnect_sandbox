import { SuccessDialog } from "../../../../../components/dialogs";
import { Modal } from "../../../../../components";
import React, { useState } from "react";
import "./ClientMedicationEditor.scss";
import ClientMedicationForm from "../ClientMedicationForm/ClientMedicationForm";
import { isInteger } from "lib/utils/Utils";

const ClientMedicationEditor = (props) => {
  const { isOpen, onSaveSuccess, medicationId, onClose, clientId } = props;

  const [isSaveSuccessDialogOpen, setIsSaveSuccessDialogOpen] = useState(false);

  const isEditing = isInteger(medicationId);

  const closeAllModals = () => {
    setIsSaveSuccessDialogOpen(false);
  };
  return (
    <>
      {isSaveSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title="The client record has been deactivated"
          buttons={[
            {
              text: "Close",
              onClick: closeAllModals,
            },
          ]}
        />
      )}

      {isOpen && (
        <Modal
          isOpen={isOpen}
          className="ClientMedicationEditor"
          title={isEditing ? "Edit Medication" : "Add Medication"}
          hasFooter={false}
          hasCloseBtn={true}
          onClose={onClose}
        >
          <ClientMedicationForm
            clientId={clientId}
            medicationId={medicationId}
            isEditing={isEditing}
            onCancel={onClose}
            onSubmitSuccess={onSaveSuccess}
          />
        </Modal>
      )}
    </>
  );
};

export default ClientMedicationEditor;
