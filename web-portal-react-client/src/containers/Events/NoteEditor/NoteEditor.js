import React, { memo } from 'react'

import { isNumber } from 'underscore'

import Modal from 'components/Modal/Modal'

import NoteForm from '../NoteForm/NoteForm'
import GroupNoteForm from '../GroupNoteForm/GroupNoteForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './NoteEditor.scss'

function NoteEditor({
    isOpen,
    noteId,
    isGroup,
    eventId,
    onClose,
    clientId,
    clientName,
    communityId,
    onSaveSuccess,
    organizationId
}) {
    const isEditMode = isNumber(noteId)
    const [CancelEditConfirmDialog, setIsCancelEditConfirmDialogOpen] = useCancelConfirmDialog()

    const onCancelEditor = isChangedForm => {
        if (isChangedForm) {
            setIsCancelEditConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    const Form = isGroup ? GroupNoteForm : NoteForm
    const title = `${isEditMode ? 'Edit' : 'Add'} ${isGroup ? 'Group Note' : 'Note'}`

    return (
        <>
            <CancelEditConfirmDialog onConfirm={onClose} />

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                className='NoteEditor'
                onClose={onCancelEditor}
                title={title}
            >
                <Form
                    noteId={noteId}
                    eventId={eventId}
                    onClose={onCancelEditor}
                    clientId={clientId}
                    clientName={clientName}
                    communityId={communityId}
                    organizationId={organizationId}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default memo(NoteEditor)