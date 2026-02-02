import React, { useState, useCallback } from 'react'

import Modal from 'components/Modal/Modal'
import SuccessDialog from 'components/dialogs/SuccessDialog/SuccessDialog'

import RequestDeclineForm from '../RequestDeclineForm/RequestDeclineForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './RequestDeclineEditor.scss'

function RequestDeclineEditor({ isOpen, onClose, requestId, onSubmit }) {
    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()
    const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false)

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess() {
        onClose()
        onSubmit()
        setIsSuccessDialogOpen(true)
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The request has been declined."
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => setIsSuccessDialogOpen(false)
                        },
                    ]}
                />
            )}

            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Decline the referral request"
                    className="RequestDeclineEditor"
                >
                    <RequestDeclineForm
                        requestId={requestId}
                        onClose={onCloseForm}
                        onSubmitSuccess={onSaveSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default RequestDeclineEditor
