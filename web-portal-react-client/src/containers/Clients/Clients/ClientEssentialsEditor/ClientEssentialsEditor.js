import React, { useCallback } from 'react'

import { noop } from 'underscore'

import Modal from 'components/Modal/Modal'

import ClientEssentialsForm from '../ClientEssentialsForm/ClientEssentialsForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './ClientEssentialsEditor.scss'

function ClientEssentialsEditor({
    data,
    isOpen,
    onClose,
    onSubmit = noop,
}) {
    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess(client) {
        onClose()
        onSubmit(client)
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
                title="Edit Record"
                className="ClientEssentialsEditor"
            >
                <ClientEssentialsForm
                    initialData={data}
                    onClose={onCloseForm}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default ClientEssentialsEditor
