import React, {
  memo,
  useCallback
} from 'react'

import { Modal } from 'components'

import {
  ConfirmDialog,
  SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import ActivationForm from '../ProspectActivationForm/ProspectActivationForm'

import './ProspectActivationEditor.scss'

function ProspectActivationEditor({ prospectId, onClose, isOpen, onSaveSuccess }) {
  const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
  const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

  const onSubmitSuccess = useCallback(() => {
    toggleSaveSuccessDialog(true);
    onSaveSuccess();
  }, [onSaveSuccess, toggleSaveSuccessDialog])

  const onCancel = useCallback(
    (isChanged) => {
      toggleSaveSuccessDialog(false)

      if (isChanged) {
        toggleCancelEditConfirmDialog(true)
      } else {
        toggleCancelEditConfirmDialog(false)
        onClose();
      }
    },
    [onClose, toggleSaveSuccessDialog, toggleCancelEditConfirmDialog]
  )

  const cancel = useCallback(() => onCancel(), [onCancel])

  return (
    <>
      {isCancelEditConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="OK"
          title="The updates will not be saved."
          onConfirm={cancel}
          onCancel={toggleCancelEditConfirmDialog}
        />
      )}

      {isSaveSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title="The prospect record has been activated"
          buttons={[
            {
              text: 'Close',
              onClick: cancel
            }
          ]}
        />
      )}

      {isOpen && (
        <Modal
          isOpen={isOpen}
          className="ProspectActivationEditor"
          title="Activate Prospect Record"
          hasFooter={false}
          hasCloseBtn={false}
        >
          <ActivationForm
            prospectId={prospectId}
            onCancel={onCancel}
            onSubmitSuccess={onSubmitSuccess}
          />
        </Modal>
      )}
    </>
  )
}

export default memo(ProspectActivationEditor)
