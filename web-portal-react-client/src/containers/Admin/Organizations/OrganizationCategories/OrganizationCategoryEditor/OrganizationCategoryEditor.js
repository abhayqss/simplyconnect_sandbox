import React, { useState, useCallback } from 'react'

import { noop } from 'underscore'

import Modal from 'components/Modal/Modal'
import SuccessDialog from 'components/dialogs/SuccessDialog/SuccessDialog'

import CategoryForm from '../OrganizationCategoryForm/OrganizationCategoryForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './OrganizationCategoryEditor.scss'

const SUCCESS_TITLES = {
    NEW: 'The category has been created.',
    EDIT: 'The category has been updated.'
}

function OrganizationCategoryEditor({
    isOpen,
    onClose,
    category,
    organizationId,
    onSubmit = noop,
}) {
    const [successTitle, setSuccessTitle] = useState(SUCCESS_TITLES.NEW)

    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()
    const [isSuccessDialogOpen, setIsSuccessDialogOpen] = useState(false)

    const isEditMode = !!category

    function closeIfNotChanged(hasChanges) {
        if (hasChanges) {
            setCancelConfirmDialogOpen(true)
        } else {
            onClose()
        }
    }

    function onSubmitSuccess(id) {
        onClose()
        onSubmit(id)
        setIsSuccessDialogOpen(true)
        setSuccessTitle(isEditMode ? SUCCESS_TITLES.EDIT : SUCCESS_TITLES.NEW)
    }

    function onCloseSuccessDialog() {
        setIsSuccessDialogOpen(false)
    }

    const onSaveSuccess = useCallback(onSubmitSuccess, [onClose, onSubmit, isEditMode])
    const onCloseForm = useCallback(closeIfNotChanged, [onClose, setCancelConfirmDialogOpen])

    return (
        <>
            <CancelConfirmDialog onConfirm={onClose} />

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={successTitle}
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: onCloseSuccessDialog
                        },
                    ]}
                />
            )}

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                className="OrganizationCategoryEditor"
                title={isEditMode ? 'Edit a Category' : 'Create a Category'}
            >
                <CategoryForm
                    category={category}
                    onClose={onCloseForm}
                    organizationId={organizationId}
                    onSubmitSuccess={onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default OrganizationCategoryEditor
