import React, { memo, useCallback } from 'react'

import Modal from 'components/Modal/Modal'

import { useCancelConfirmDialog } from 'hooks/common'

import RequestAcceptForm from '../ReferralRequestAcceptForm/ReferralRequestAcceptForm'

import './ReferralRequestAcceptEditor.scss'

function ReferralRequestAcceptEditor({ isOpen, onClose, requestId, onSaveSuccess }) {
    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function submitSuccess() {
        onClose()
        onSaveSuccess()
    }

    const onSubmitSuccess = useCallback(submitSuccess, [])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Accept referral"
                    className="ReferralRequestAcceptEditor"
                >
                    <RequestAcceptForm
                        onClose={onCloseForm}
                        requestId={requestId}
                        onSubmitSuccess={onSubmitSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default memo(ReferralRequestAcceptEditor)
