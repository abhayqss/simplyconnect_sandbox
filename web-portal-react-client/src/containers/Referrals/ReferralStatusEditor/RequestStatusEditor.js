import React, { useCallback } from 'react'

import { Modal } from 'components'

import { useCancelConfirmDialog } from 'hooks/common'

import ReferralStatusForm from '../ReferralStatusForm/ReferralStatusForm'

import './ReferralStatusEditor.scss'

function RequestStatusEditor(
    {
        isOpen,
        requestId,
        onSaveSuccess,
        onClose: onCloseCb
    }
) {
    const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

    const onClose = useCallback(hasChanges => {
        if (hasChanges) {
            toggleCancelConfirmDialog(true)
        } else {
            onCloseCb()
        }
    }, [onCloseCb, toggleCancelConfirmDialog])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Change Referral Status"
                    className="ReferralStatusEditor"
                >
                    <ReferralStatusForm
                        requestId={requestId}
                        onClose={onClose}
                        onSubmitSuccess={onSaveSuccess}
                    />
                </Modal>
            )}

            <CancelConfirmDialog onConfirm={onCloseCb}/>
        </>
    )
}

export default RequestStatusEditor
