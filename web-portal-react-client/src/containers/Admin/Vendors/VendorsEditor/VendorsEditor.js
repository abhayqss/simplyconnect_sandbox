import React, { memo, useState, useCallback } from "react";

import { noop, compact, isNumber } from "underscore";
import { useHistory, useParams } from "react-router-dom";
import Modal from "components/Modal/Modal";
import SuccessDialog from "components/dialogs/SuccessDialog/SuccessDialog";
import { useCancelConfirmDialog } from "hooks/common";
import { path } from "lib/utils/ContextUtils";
import "./Vendor.scss";
import VendorForm from "../VendorsForm/VendorForm";

function VendorEditor({
  isOpen,
  isEditVendor = false,
  vendorId,
  isValidationNeed,

  onClose,
  onSaveSuccess,
  vendorStatus,
  onActionConfirm,
  onActionRemove,
}) {
  const history = useHistory();

  const isEditMode = isNumber(vendorId) || isEditVendor;

  const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog();

  const onCancel = useCallback(
    (isChanged) => {
      if (isChanged) toggleCancelConfirmDialog(true);
      else onClose();
    },
    [onClose, toggleCancelConfirmDialog],
  );

  return (
    <>
      <Modal
        className="VendornEditor"
        isOpen={isOpen}
        hasFooter={false}
        hasCloseBtn={true}
        onClose={onClose}
        title={isEditMode ? "Edit vendor details" : "Add vendor details"}
      >
        <VendorForm
          vendorId={vendorId}
          isEditVendor={isEditVendor}
          isValidationNeed={isValidationNeed}
          onCancel={onClose}
          onSubmitSuccess={onSaveSuccess}
          vendorStatus={vendorStatus}
          onActionRemove={onActionRemove}
          onActionConfirm={onActionConfirm}
        />
      </Modal>
    </>
  );
}

VendorEditor.defaultProps = {
  onSaveSuccess: noop,
};

export default memo(VendorEditor);
