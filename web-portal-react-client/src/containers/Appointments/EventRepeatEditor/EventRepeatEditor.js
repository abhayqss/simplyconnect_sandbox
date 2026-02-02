import React, { useCallback } from 'react'

import { noop } from 'underscore'

import Modal from 'components/Modal/Modal'

import EventRepeatForm from '../EventRepeatForm/EventRepeatForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './EventRepeatEditor.scss'

function EventRepeatEditor({
    data,
    isOpen,
    onClose,
    defaults,
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

    function onSubmitSuccess(data) {
        onClose()
        onSubmit(data)
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [onClose, onSubmit])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                title="Repeat"
                className="EventRepeatEditor"
            >
                <EventRepeatForm
                    data={data}
                    defaults={defaults}
                    onClose={onCloseForm}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default EventRepeatEditor
