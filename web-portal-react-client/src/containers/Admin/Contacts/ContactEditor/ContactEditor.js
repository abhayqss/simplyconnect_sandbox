import React, {
    useState,
    useCallback
} from 'react'

import { noop } from 'underscore'

import { Modal } from 'components'

import {
    SuccessDialog,
    CancelConfirmDialog
} from 'components/dialogs'

import { isInteger } from 'lib/utils/Utils'

import ContactForm from '../ContactForm/ContactForm'

import './ContactEditor.scss'

export default function ContactEditor(
    {
        isOpen,

        clientId,
        contactId,
        canEditRole,
        organizationId,
        isPendingContact,
        isExpiredContact,

        onClose,
        onViewContact = noop,
        onSaveSuccess,
        onReInviteSuccess: onReInviteSuccessCb
    }
) {
    const [savedContactId, setSavedContactId] = useState(null)

    const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false)
    const [isCancelConfirmDialogOpen, toggleCancelConfirmDialog] = useState(false)

    let title = 'Create Contact'
    const isEditMode = isInteger(contactId)

    if (isEditMode) {
        title = 'Edit contact details'

        if (isPendingContact) {
            title = 'Pending Contact'
        }

        if (isExpiredContact) {
            title = 'Expired Contact'
        }
    }

    const onIntentToClose = useCallback(isChanged => {
        if (isChanged) toggleCancelConfirmDialog(true)
        else onClose()
    }, [onClose])

    const onSubmitSuccess = useCallback((id) => {
        toggleSuccessDialog(true)
        setSavedContactId(id ?? contactId)
    }, [contactId])

    const onReInviteSuccess = useCallback((...args) => {
        toggleSuccessDialog(true)
        onReInviteSuccessCb(...args)
    }, [onReInviteSuccessCb])

    return (
        <>
            <Modal
                title={title}
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                className='ContactEditor'
            >
                <ContactForm
                    clientId={clientId}
                    contactId={contactId}
                    onClose={onIntentToClose}
                    organizationId={organizationId}
                    isPendingContact={isPendingContact}
                    isExpiredContact={isExpiredContact}
                    canEditRole={canEditRole}
                    onSubmitSuccess={onSubmitSuccess}
                    onReInviteSuccess={onReInviteSuccess}
                />
            </Modal>

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={
                        isEditMode ? (
                            (isExpiredContact || isPendingContact) ?
                                'A new invitation has been sent.'
                                : 'Contact details have been updated.'
                        ) : 'Contact has been created.'
                    }
                    buttons={isEditMode ?
                        [
                            {
                                text: 'OK',
                                onClick: () => {
                                    onSaveSuccess(savedContactId)
                                    toggleSuccessDialog(false)
                                    onClose()
                                }
                            }
                        ] : [
                            {
                                text: 'Close',
                                outline: true,
                                className: 'min-width-120 margin-left-80',
                                onClick: () => {
                                    onSaveSuccess(savedContactId)
                                    toggleSuccessDialog(false)
                                    onClose()
                                }
                            },
                            {
                                text: 'View Details',
                                className: 'min-width-120 margin-right-80',
                                onClick: () => {
                                    onSaveSuccess(savedContactId)
                                    toggleSuccessDialog(false)
                                    onClose()
                                    onViewContact(savedContactId)
                                }
                            }
                        ]
                    }
                />
            )}

            {isCancelConfirmDialogOpen && (
                <CancelConfirmDialog
                    isOpen
                    title="The changes will not be saved"
                    onCancel={() => toggleCancelConfirmDialog(false)}
                    onConfirm={onClose}
                />
            )}
        </>
    )
}