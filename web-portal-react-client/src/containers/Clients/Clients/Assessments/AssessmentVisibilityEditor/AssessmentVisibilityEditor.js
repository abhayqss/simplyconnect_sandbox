import React, { useCallback } from 'react'

import { Modal } from 'components'

import { useCancelConfirmDialog } from 'hooks/common'

import { ASSESSMENT_STATUSES } from 'lib/Constants'

import AssessmentVisibilityForm from '../AssessmentVisibilityForm/AssessmentVisibilityForm'

import './AssessmentVisibilityEditor.scss'

const { HIDDEN } = ASSESSMENT_STATUSES

function AssessmentVisibilityEditor(
    {
        isOpen,
        clientId,
        assessmentId,
        assessmentStatus,
        onSaveSuccess,
        onClose: onCloseCb
    }
) {
    const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

    const onClose = useCallback(hasChanges => {
        if (hasChanges) {
            toggleCancelConfirmDialog(true)
        } else {
            onCloseCb(true)
        }
    }, [onCloseCb, toggleCancelConfirmDialog])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title={`${assessmentStatus === HIDDEN ? 'Restore' : 'Hide'} assessment`}
                    className="AssessmentVisibilityEditor"
                >
                    <AssessmentVisibilityForm
                        clientId={clientId}
                        assessmentId={assessmentId}
                        assessmentStatus={assessmentStatus}
                        onClose={onClose}
                        onSubmitSuccess={onSaveSuccess}
                    />
                </Modal>
            )}

            <CancelConfirmDialog onConfirm={onCloseCb}/>
        </>
    )
}

export default AssessmentVisibilityEditor
