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

import ActivationForm from '../ClientActivationForm/ClientActivationForm'

import './ClientActivationEditor.scss'

function ClientActivationEditor({ clientId, onClose, isOpen, onSaveSuccess }) {
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
          title="The client record has been activated"
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
          className="ClientActivationEditor"
          title="Activate Client Record"
          hasFooter={false}
          hasCloseBtn={false}
        >
          <ActivationForm
            clientId={clientId}
            onCancel={onCancel}
            onSubmitSuccess={onSubmitSuccess}
          />
        </Modal>
      )}
    </>
  )
}

export default memo(ClientActivationEditor)
