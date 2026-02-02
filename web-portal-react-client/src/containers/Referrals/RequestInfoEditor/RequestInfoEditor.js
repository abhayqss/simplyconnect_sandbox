import React, { useState, useCallback } from 'react'

import Modal from 'components/Modal/Modal'
import SuccessDialog from 'components/dialogs/SuccessDialog/SuccessDialog'

import RequestInfoForm from '../RequestInfoForm/RequestInfoForm'
import ResponseInfoForm from '../ResponseInfoForm/ResponseInfoForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './RequestInfoEditor.scss'

function RequestInfoEditor({
    isOpen,
    onClose,
    onSubmit,
    requestId,
    referralId,
    infoRequestId,
}) {
    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()
    const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false)

    const isRequest = Number.isInteger(requestId)

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess() {
        onClose()
        setIsSuccessDialogOpen(true)
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    const Form = isRequest ? RequestInfoForm : ResponseInfoForm
    const title = isRequest ? 'Request info' : 'Reply to request info'

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={`A ${isRequest ? '' : 'reply to the '} request for information has been submitted.`}
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => {
                                onSubmit()
                                setIsSuccessDialogOpen(false)
                            }
                        },
                    ]}
                />
            )}

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title={title}
                className="RequestInfoEditor"
            >
                <Form
                    requestId={requestId}
                    onClose={onCloseForm}
                    referralId={referralId}
                    infoRequestId={infoRequestId}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default RequestInfoEditor
