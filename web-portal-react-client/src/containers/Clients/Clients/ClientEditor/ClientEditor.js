import React, { memo, useCallback, useState } from "react";

import { compact, isNumber, noop } from "underscore";
import { useHistory } from "react-router-dom";

import Modal from "components/Modal/Modal";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";

import ClientForm from "../ClientForm/ClientForm";

import { useCancelConfirmDialog } from "hooks/common";

import { path } from "lib/utils/ContextUtils";

import "./ClientEditor.scss";
import useCanHaveHousingVouchersQuery from "hooks/business/directory/query/useCanHaveHousingVouchersQuery";
import { isInteger } from "lib/utils/Utils";

function ClientEditor({
  isOpen,
  isOnDashboard,

  clientId,
  isClientEmailRequired,

  isValidationNeed,

  communityId,
  organizationId,

  onClose,
  onSaveSuccess,

  isFromWorkflow = false,
}) {
  const history = useHistory();
  const isEditMode = isNumber(clientId);

  const [newClientId, setNewClientId] = useState(null);

  const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false);
  const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog();

  const onSubmitSuccess = useCallback((id) => {
    setNewClientId(id);
    toggleSuccessDialog(true);
  }, []);

  const onViewClient = useCallback(() => {
    history.push(path(`/clients/${newClientId}`));
  }, [history, newClientId]);

  const onCancel = useCallback(
    (isChanged) => {
      if (isChanged) toggleCancelConfirmDialog(true);
      else onClose();
    },
    [onClose, toggleCancelConfirmDialog],
  );

  const [isCanShowHousingVoucher, setIsCanShowHousingVoucher] = useState(false);
  useCanHaveHousingVouchersQuery(
    {
      organizationId: organizationId,
    },
    {
      enabled: isInteger(organizationId),
      onSuccess: (data) => {
        setIsCanShowHousingVoucher(data);
      },
    },
  );

  return (
    <>
      <CancelConfirmDialog onConfirm={onClose} />

      {isSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title={`The client record has been 
                        ${isEditMode ? "updated" : "created"}.`}
          buttons={compact([
            {
              text: "Close",
              outline: true,
              onClick: () => {
                onSaveSuccess(!isEditMode);
                toggleSuccessDialog(false);
              },
            },
            !isOnDashboard &&
              !isFromWorkflow && {
                text: "View record",
                onClick: onViewClient,
              },
          ])}
        />
      )}

      <Modal
        isOpen={isOpen}
        hasFooter={false}
        hasCloseBtn={false}
        title={isEditMode ? "Edit client details" : "Add new client"}
        className="ClientEditor"
      >
        <ClientForm
          clientId={clientId}
          isClientEmailRequired={isClientEmailRequired}
          isCanShowHousingVoucher={isCanShowHousingVoucher}
          isValidationNeed={isValidationNeed}
          communityId={communityId}
          organizationId={organizationId}
          onCancel={onCancel}
          onSubmitSuccess={onSubmitSuccess}
        />
      </Modal>
    </>
  );
}

ClientEditor.defaultProps = {
  onSaveSuccess: noop,
};

export default memo(ClientEditor);
