import React, { memo, useState, useCallback } from 'react'

import { Button } from 'reactstrap'

import { Modal } from 'components'
import { ConfirmDialog } from 'components/dialogs'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import ContactPermissionsForm from './ContactPermissionsForm/ContactPermissionsForm'

import './ContactPermissionPicker.scss'

function ContactPermissionPicker({
    isOpen,
    onClose,
    folderId,
    communityId,
    parentFolderId,
    selectedPermissions,

    onSaveSuccess
}) {
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useState(false)

    function close() {
        onClose()
        toggleCancelEditConfirmDialog(false)
    }

    function closeConfirmDialog() {
        toggleCancelEditConfirmDialog(false)
    }

    function cancel(isChanged) {
        if (isChanged) {
            toggleCancelEditConfirmDialog(true)
        } else {
            onClose()
        }
    }

    function save(selected) {
        onClose()
        onSaveSuccess(selected)
    }

    return (
        <>
            {isCancelEditConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The updates will not be saved."
                    onConfirm={close}
                    onCancel={closeConfirmDialog}
                />
            )}

            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="ContactPermissionPicker"
                    title="Choose Contacts"
                    hasFooter={false}
                    hasCloseBtn={false}
                >
                    <ContactPermissionsForm
                        hasSearch
                        folderId={folderId}
                        communityId={communityId}
                        parentFolderId={parentFolderId}
                        selectedPermissions={selectedPermissions}
                        onSubmit={save}
                        onCancel={cancel}
                    />
                </Modal>
            )}
        </>
    )
}

export default memo(ContactPermissionPicker)