import React, {
    memo,
    useCallback
} from 'react'

import DocumentTitle from 'react-document-title'

import { Modal } from 'components'

import {
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import Form from '../DocumentFolderForm/DocumentFolderForm'

import './DocumentFolderEditor.scss'

function DocumentFolderEditor(
    {
        isOpen,
        onClose,
        folderId,
        communityId,
        organizationId,
        parentFolderId,
        canEditSecurity,
        isSecurityEnabled,
        onSaveSuccess
    }
) {
    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

    const isEditMode = Boolean(folderId)

    function close() {
        onClose()
        toggleCancelEditConfirmDialog()
    }

    const onCancel = useCallback((isChanged) => {
        if (isChanged) toggleCancelEditConfirmDialog()
        else onClose()
    }, [onClose, toggleCancelEditConfirmDialog])

    const onSubmitSuccess = useCallback(() => {
        onClose()
        onSaveSuccess()
        toggleSaveSuccessDialog()
    }, [onClose, onSaveSuccess, toggleSaveSuccessDialog])

    const title = isEditMode ? 'Edit folder' : 'Add folder'

    const successTitle = isEditMode ? 'The folder has been updated' : 'The folder has been created'

    return (
        <>
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
                    title={successTitle}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => toggleSaveSuccessDialog()
                        }
                    ]}
                />
            )}

            {isOpen && (
                <DocumentTitle title={`Simply Connect | Company Documents | ${title}`}>
                    <Modal
                        title={title}
                        isOpen={isOpen}
                        hasFooter={false}
                        hasCloseBtn={false}
                        className="DocumentFolderEditor"

                        onClose={onClose}
                    >
                        <Form
                            folderId={folderId}
                            communityId={communityId}
                            organizationId={organizationId}
                            parentFolderId={parentFolderId}
                            canEditSecurity={canEditSecurity}
                            isSecurityEnabled={isSecurityEnabled}

                            onCancel={onCancel}
                            onSubmitSuccess={onSubmitSuccess}
                        />
                    </Modal>
                </DocumentTitle>
            )}
        </>
    )
}

export default memo(DocumentFolderEditor)