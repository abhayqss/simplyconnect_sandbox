import React, {
    useCallback
} from 'react'

import { Modal } from 'components'

import { useCancelConfirmDialog } from 'hooks/common'

import ContactToIndividualConfirmForm from '../ContactToIndividualConfirmForm/ContactToIndividualConfirmForm'

import './ContactToIndividualConfirmPopup.scss'

const ContactToIndividualConfirmPopup = ({
    isOpen,
    inquiryId,
    inquiryDate,
    onClose,
    onSaveSuccess
}) => {
	const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

    const _onSaveSuccess = useCallback(() => {
		onSaveSuccess()
	}, [onSaveSuccess])

    const onCancel = useCallback(isChanged => {
		if (isChanged) toggleCancelConfirmDialog(true)
		else onClose()
	}, [onClose, toggleCancelConfirmDialog])

    return (
        <>
			<CancelConfirmDialog onConfirm={onClose}/>
            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title="Mark as done"
                className="ContactToIndividualConfirmPopup"
            >
                <ContactToIndividualConfirmForm
                    inquiryId={inquiryId}
                    inquiryDate={inquiryDate}
                    onCancel={onCancel}
                    onSubmitSuccess={_onSaveSuccess}
                />
            </Modal>
        </>
    )
}

export default ContactToIndividualConfirmPopup