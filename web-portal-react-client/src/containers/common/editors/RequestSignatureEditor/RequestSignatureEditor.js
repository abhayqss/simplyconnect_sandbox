import React, {
    memo,
    useCallback
} from 'react'

import {
    Modal
} from 'components'

import {
    ConfirmDialog,
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import './RequestSignatureEditor.scss'

function RequestSignatureEditor(
    {
        isOpen,
        onClose,
        children,
        onUploadSuccess
    }
) {
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

    function close() {
        toggleCancelEditConfirmDialog(false)
        onClose()
    }

    function cancel(isChanged) {
        if (isChanged) {
            toggleCancelEditConfirmDialog(true)
        } else {
            onClose()
        }
    }

    function handleSubmitSuccess(...args) {
        onClose()
        onUploadSuccess(...args)
    }

    const onCancel = useCallback(cancel, [onClose])
    const onSubmitSuccess = useCallback(handleSubmitSuccess, [onClose])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="RequestSignatureEditor"
                    title="Request Signature"
                    bodyClassName="RequestSignatureEditor-Body"
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
                    onCancel={toggleCancelEditConfirmDialog}
                />
            )}
        </>
    )
}

export default memo(RequestSignatureEditor)