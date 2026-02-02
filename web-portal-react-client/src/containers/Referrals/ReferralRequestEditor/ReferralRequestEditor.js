import React, { memo, useState, useCallback } from "react";

import { noop, merge } from "lodash";

import { useHistory } from "react-router-dom";

import { Modal } from "components";
import { SuccessDialog } from "components/dialogs";

import { useAuthUser } from "hooks/common/redux";

import { useCancelConfirmDialog } from "hooks/common";

import ReferralRequestForm from "../ReferralRequestForm/ReferralRequestForm";

import { SYSTEM_ROLES } from "lib/Constants";

import { path } from "lib/utils/ContextUtils";

import "./ReferralRequestEditor.scss";

const { PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES;

function ReferralRequestEditor({
  isOpen,
  onClose,
  marketplace,
  communityId,
  organizationId,
  isFeaturedCommunity,
  onSaveSuccess = noop,
  successDialog,
  isFromVendor = false,
  isOrganizationDisabled = false,
  isFromSearch,
  vendorId,
  isAssociation,
  isClinicalVendor = false,
}) {
  const user = useAuthUser();
  const history = useHistory();
  const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog();
  const [newRequestId, setNewRequestId] = useState(null);
  const [isSaveSuccessDialogOpen, setIsSaveSuccessDialogOpen] = useState(false);

  function onViewRequest() {
    setIsSaveSuccessDialogOpen(false);
    history.push(path(`/outbound-referrals/${newRequestId}`));
  }

  function closeIfNotChanged(hasChanges) {
    if (hasChanges) {
      setCancelConfirmDialogOpen(true);
    } else {
      onClose();
    }
  }

  function onSubmitSuccess(id) {
    onClose();
    onSaveSuccess();
    setNewRequestId(id);
    setIsSaveSuccessDialogOpen(true);
  }

  const submitSuccess = useCallback(onSubmitSuccess, []);

  const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen]);

  const canViewRequest = ![PERSON_RECEIVING_SERVICES].includes(user?.roleName);

  const buttons = merge(
    {
      cancel: {
        text: "Close",
        outline: true,
        onClick: () => setIsSaveSuccessDialogOpen(false),
      },
      ...(canViewRequest && {
        view: {
          text: "View request",
          onClick: onViewRequest,
        },
      }),
    },
    successDialog?.buttons,
  );
  const buttonsForAssociation = merge(
    {
      cancel: {
        text: "Close",
        outline: true,
        onClick: () => setIsSaveSuccessDialogOpen(false),
      },
    },
    successDialog?.buttons,
  );

  return (
    <>
      <CancelConfirmDialog onConfirm={onClose} />

      {isSaveSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          className="ReferralRequestEditor-SuccessDialog"
          title="The request has been submitted."
          text={isAssociation ? "" : successDialog?.text}
          buttons={isAssociation ? Object.values(buttonsForAssociation) : Object.values(buttons)}
        />
      )}

      {isOpen && (
        <Modal isOpen hasFooter={false} hasCloseBtn={false} title="Referral request" className="ReferralRequestEditor">
          <ReferralRequestForm
            isFromSearch={isFromSearch}
            isClinicalVendor={isClinicalVendor}
            vendorID={vendorId}
            isOrganizationDisabled={isOrganizationDisabled}
            isFromVendor={isFromVendor}
            marketplace={marketplace}
            communityId={communityId}
            organizationId={organizationId}
            isFeaturedCommunity={isFeaturedCommunity}
            onClose={onCloseForm}
            onSubmitSuccess={submitSuccess}
          />
        </Modal>
      )}
    </>
  );
}

export default memo(ReferralRequestEditor);
