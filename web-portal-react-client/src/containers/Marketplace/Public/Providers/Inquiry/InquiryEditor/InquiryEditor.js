import React, {
	useState,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import {
	noop,
	compact
} from 'underscore'

import { Modal } from 'components'
import { SuccessDialog } from 'components/dialogs'

import { useCancelConfirmDialog } from 'hooks/common'

import InquiryForm from '../InquiryForm/InquiryForm'

import './InquiryEditor.scss'

function InquiryEditor(
    {
        isOpen,
        communityId,
        serviceCategories,
        onClose,
        onSaveSuccess
    }
) {
	const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false)
	const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

    const _onSaveSuccess = useCallback(() => {
		onSaveSuccess()
		toggleSuccessDialog(true)
	}, [onSaveSuccess])

    const onCancel = useCallback(isChanged => {
		if (isChanged) toggleCancelConfirmDialog(true)
		else onClose()
	}, [onClose, toggleCancelConfirmDialog])
        
    return (
        <>
            <CancelConfirmDialog onConfirm={onClose}/>

            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title='The inquiry has been submitted.'
                    text='Manager will contact you shortly, thanks!'
                    className="SubmitInquirySuccessDialog"
                    buttons={compact([
                        {
                            text: 'Close',
                            outline: true,
                            onClick: () => {
                                onClose()
                                toggleSuccessDialog(false)
                            }
                        }
                    ])}
                />
            )}

            <Modal
                isOpen={isOpen}
                hasFooter={false}
                hasCloseBtn={false}
                title="Create Inquiry"
                className="MarketplaceInquiryEditor"
            >
                <InquiryForm
                    communityId={communityId}
                    serviceCategories={serviceCategories}
                    onCancel={onCancel}
                    onSubmitSuccess={_onSaveSuccess}
                />
            </Modal>
        </>
    )
}

InquiryEditor.propTypes = {
	onClose: PTypes.func,
	onSaveSuccess: PTypes.func
}

InquiryEditor.defaultProps = {
	onClose: noop,
	onSaveSuccess: noop
}

export default InquiryEditor