import React, { memo, useState, useCallback } from 'react'

import Modal from 'components/Modal/Modal'
import CancelConfirmDialog from 'components/dialogs/CancelConfirmDialog/CancelConfirmDialog'

import Form from '../DomainSelectionForm/DomainsSelectionForm'

import './DomainsSelectionEditor.scss'

function DomainsSelectionEditor({
    isOpen,
    domains,
    onClose,
    clientId,
    servicePlanId,
    onSaveSuccess,
}) {
    const [isCancelConfirmDialogOpen, setCancelConfirmDialogOpen] = useState(false)

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
    const onCloseForm = useCallback(closeIfNotChanged, [onClose])

    return (
        <>
            <CancelConfirmDialog
                isOpen={isCancelConfirmDialogOpen}
                onCancel={() => setCancelConfirmDialogOpen(false)}
                onConfirm={() => {
                    onClose()
                    setCancelConfirmDialogOpen(false)
                }}
            />

            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Select Domains"
                    className="DomainsSelectionEditor"
                >
                    <Form
                        domains={domains}
                        clientId={clientId}
                        onClose={onCloseForm}
                        servicePlanId={servicePlanId}
                        onSubmitSuccess={onSubmitSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default memo(DomainsSelectionEditor)
