import React from 'react'

import { Modal } from 'components'

import {
    ConfirmDialog,
    SuccessDialog
} from 'components/dialogs'

import Form from '../ContactUsForm/ContactUsForm'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import './ContactUsEditor.scss'

function ContactUsEditor({
    isOpen,
    onClose,
    onSaveSuccess
}) {
    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

    function close() {
        onClose()
        toggleCancelEditConfirmDialog()
    }

    function cancel(isChanged) {
        if (isChanged) {
            toggleCancelEditConfirmDialog()
        } else {
            onClose()
        }
    }

    function submitSuccess() {
        onClose()
        onSaveSuccess()
        toggleSaveSuccessDialog()
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
                    onCancel={() => toggleCancelEditConfirmDialog()}
                />
            )}

            {isSaveSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="Your request has been sent. The support team will reach out to you within 24 hours"
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => toggleSaveSuccessDialog()
                        }
                    ]}
                />
            )}

            {isOpen && (
                <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    className="ContactUsEditor"
                    title="Contact Us"
                    hasFooter={false}
                    hasCloseBtn={false}
                >
                    <Form
                        onCancel={cancel}
                        onSubmitSuccess={submitSuccess}
                    />
                </Modal>
            )}
        </>
    )
}

export default ContactUsEditor
