import React, { useCallback, useEffect, useState } from "react";
import { isNumber } from "underscore";
import Modal from "components/Modal/Modal";
import "./FaxEditor.scss";
import FaxForm from "../FaxForm/FaxForm";
import { useAuthUser, useCancelConfirmDialog } from "hooks/common";
import service from "services/CommunityService";

function FaxEditor({ isOpen, FaxId, onClose, loginFax, onSaveSuccess }) {
  const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog();
  const [communityFax, setCommunityFax] = useState("");

  const isEditMode = () => {
    return isNumber(FaxId);
  };
  const onCancel = useCallback(
    (isChanged) => {
      if (isChanged) toggleCancelConfirmDialog(true);
      else onClose();
    },
    [onClose, toggleCancelConfirmDialog],
  );

  return (
    <>
      <CancelConfirmDialog onConfirm={onClose} />
      <Modal
        isOpen={isOpen}
        className="FaxEditor"
        hasCloseBtn={true}
        hasFooter={false}
        onClose={onClose}
        title={isEditMode() ? "Edit Fax" : "Send Fax"}
      >
        <FaxForm
          FaxId={FaxId}
          loginFax={loginFax}
          isEdit={isEditMode()}
          onClose={onClose}
          onCancel={onCancel}
          onSaveSuccess={onSaveSuccess}
        />
      </Modal>
    </>
  );
}

export default FaxEditor;
