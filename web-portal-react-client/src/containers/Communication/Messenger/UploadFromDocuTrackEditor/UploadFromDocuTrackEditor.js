import React, {
    useCallback,
    useState
} from 'react'

import {
    Modal,
    ErrorViewer
} from 'components'

import {
    CancelConfirmDialog
} from 'components/dialogs'

import { UploadFromDocuTrackForm } from '../'

import './UploadFromDocuTrackEditor.scss'

export default function UploadFromDocuTrackEditor(
    {
        isOpen,
        conversationSid,
        onUploadSuccess,
        onClose: onCloseCb
    }
) {
    const [error, setError] = useState(null)
    const [isConfirmDialogOpen, toggleConfirmDialog] = useState(false)

    const onClose = useCallback(isChanged => {
        if (isChanged) toggleConfirmDialog(true)
        else onCloseCb()
    }, [onCloseCb])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Upload document from DocuTrack"
                    className="UploadFromDocuTrackEditor"
                >
                    <UploadFromDocuTrackForm
                        conversationSid={conversationSid}
                        onClose={onClose}
                        onSubmitSuccess={onUploadSuccess}
                        onSubmitFailure={error => setError(error)}
                    />
                </Modal>
            )}

            {isConfirmDialogOpen && (
                <CancelConfirmDialog
                    isOpen
                    onCancel={() => toggleConfirmDialog(false)}
                    onConfirm={() => {
                        onCloseCb()
                        toggleConfirmDialog(false)
                    }}
                />
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}