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

import { isInteger } from 'lib/utils/Utils'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import UserManualForm from '../UserManualForm/UserManualForm'

import './UserManualEditor.scss'

function UserManualEditor({ manualId, isOpen, onClose, onSaveSuccess }) {
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
                    title="The manual has been uploaded."
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
                    className="UserManualEditor"
                    title="Upload Manual"
                    hasFooter={false}
                    hasCloseBtn={false}
                >
                    <UserManualForm
                        manualId={manualId}
                        onCancel={onCancel}
                        onSubmitSuccess={onSubmitSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default memo(UserManualEditor)