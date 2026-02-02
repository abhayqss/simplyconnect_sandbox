import React, {
    memo,
    useCallback
} from 'react'

import {
    Modal
} from 'components'

import {
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import './DocumentEditor.scss'

function DocumentEditor(
    {
        isOpen,
        onClose,
        children,
        onUploadSuccess
    }
) {
    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

    function close() {
        toggleCancelEditConfirmDialog(false)
        onClose()
    }

    function cancel(isChanged) {
        if (isChanged) toggleCancelEditConfirmDialog(true)
        else onClose()
    }

    function handleSubmitSuccess() {
        onClose()
        onUploadSuccess()
        toggleSaveSuccessDialog(true)
    }

    const onCancel = useCallback(cancel, [onClose])
    const onSubmitSuccess = useCallback(handleSubmitSuccess, [onClose])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="DocumentEditor"
                    title="Upload document"
                    bodyClassName="DocumentEditor-Body"
                    hasFooter={false}
                    hasCloseBtn={false}
                >
                    {children && children({ onCancel, onSubmitSuccess })}
                </Modal>
            )}

            {isCancelEditConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The updates will not be saved."
                    onConfirm={close}
                    onCancel={() => toggleCancelEditConfirmDialog()}
                />
            )}

            {isSaveSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="The document has been uploaded."
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => {
                                toggleSaveSuccessDialog(false)
                            }
                        }
                    ]}
                />
            )}
        </>
    )
}

export default memo(DocumentEditor)