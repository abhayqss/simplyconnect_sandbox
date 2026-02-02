import React, { useCallback } from 'react'

import { noop } from 'underscore'

import Modal from 'components/Modal/Modal'

import LabResultsReviewForm from '../LabResultsReviewForm/LabResultsReviewForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './LabResultsReviewEditor.scss'

function LabResultsReviewEditor({
    isOpen,
    onClose,
    onSubmit = noop,
    communityIds,
    organizationId,
}) {
    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()

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
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title="Review Pending Results"
                className="LabResultsReviewEditor"
            >
                <LabResultsReviewForm
                    communityIds={communityIds}
                    onClose={onCloseForm}
                    onSubmitSuccess={onSaveSuccess}
                    organizationId={organizationId}
                />
            </Modal>
        </>
    )
}

export default LabResultsReviewEditor
