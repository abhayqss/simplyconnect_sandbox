import React, {
    useState,
    useCallback
} from 'react'

import { noop, compact } from 'underscore'

import {
    Modal,
    ErrorViewer
} from 'components'

import {
    SuccessDialog,
    CancelConfirmDialog
} from 'components/dialogs'

import { SendToDocuTrackForm } from '../'

import './SendToDocuTrackEditor.scss'

export default function SendToDocuTrackEditor(
    {
        isOpen,
        media,
        onClose: onCloseCb,
        onSendSuccessConfirmed,
        onSendSuccess: onSendSuccessCb = noop
    }
) {
    const [error, setError] = useState(null)
    const [isConfirmDialogOpen, toggleConfirmDialog] = useState(false)
    const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false)

    const onClose = useCallback(isChanged => {
        if (isChanged) toggleConfirmDialog(true)
        else onCloseCb()
    }, [onCloseCb])

    const onSendSuccess = useCallback(() => {
        onSendSuccessCb()
        toggleSuccessDialog(true)
    }, [onSendSuccessCb])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    hasFooter={false}
                    hasCloseBtn={false}
                    title="Send Document to DocuTrack"
                    className="SendToDocuTrackEditor"
                >
                    <SendToDocuTrackForm
                        media={media}
                        onClose={onClose}
                        onSubmitSuccess={onSendSuccess}
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

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="Document has been sent to Docutrack"
                    buttons={compact([
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => {
                                onSendSuccessConfirmed()
                                toggleSuccessDialog(false)
                            }
                        }
                    ])}
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