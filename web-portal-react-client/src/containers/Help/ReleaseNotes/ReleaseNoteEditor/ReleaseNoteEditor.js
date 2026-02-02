import React, {
    memo,
    useState,
    useCallback
} from 'react'

import { Modal } from 'components'

import {
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import ReleaseNoteForm from '../ReleaseNoteForm/ReleaseNoteForm'

import './ReleaseNoteEditor.scss'

function ReleaseNoteEditor({ isOpen, noteId, onClose, onSaveSuccess }) {
    const [isSaveSuccessDialogOpen, setIsSaveSuccessDialogOpen] = useState(false)
    const [isCancelEditConfirmDialogOpen, setIsCancelEditConfirmDialogOpen] = useState(false)

    function onCloseEditor() {
        setIsCancelEditConfirmDialogOpen(false)
        onClose()
    }

    const onCancel = useCallback(isChanged => {
        if (isChanged) {
            setIsCancelEditConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }, [onClose])

    const onSubmitSuccess = useCallback(() => {
        onClose()
        setIsSaveSuccessDialogOpen(true)
    }, [onClose])

    return (
        <>
            {isCancelEditConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The updates will not be saved."
                    onConfirm={onCloseEditor}
                    onCancel={() => setIsCancelEditConfirmDialogOpen(false)}
                />
            )}

            {isSaveSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The release notes document has been uploaded."
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => {
                                setIsSaveSuccessDialogOpen(false)
                                onSaveSuccess()
                            }
                        }
                    ]}
                />
            )}

            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="ReleaseNoteEditor"
                    title="Upload Release Notes Document"
                    hasFooter={false}
                    hasCloseBtn={false}
                >
                    <ReleaseNoteForm
                        noteId={noteId}
                        onCancel={onCancel}
                        onSubmitSuccess={onSubmitSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default memo(ReleaseNoteEditor)