import React, {
	memo,
	useCallback
} from 'react'

import { noop } from 'underscore'

import { Modal } from 'components'

import {
	SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import HIEConsentPolicyForm from '../HIEConsentPolicyForm/HIEConsentPolicyForm'

import './HIEConsentPolicyEditor.scss'

function HIEConsentPolicyEditor({
	isOpen,
	onClose = noop,
	onSaveSuccess = noop
}) {
	const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()

	const onSubmitSuccess = useCallback(() => {
		toggleSaveSuccessDialog(true)
		onSaveSuccess()
	}, [onSaveSuccess, toggleSaveSuccessDialog])

	return (
		<>
			{isSaveSuccessDialogOpen && (
				<SuccessDialog
					isOpen
					title="Thank you, you can review and update your Consent in the Mobile app."
					buttons={[
						{
							text: 'Close',
							onClick: () => {
								toggleSaveSuccessDialog()
								
								onClose()
							}
						}
					]}
				/>
			)}

			{isOpen && (
				<Modal
					isOpen={isOpen}
					className="HIEConsentPolicyEditor"
					title="HIE Opt In / Opt Out"
					hasFooter={false}
					hasCloseBtn={false}
				>
					<HIEConsentPolicyForm onSubmitSuccess={onSubmitSuccess}/>
				</Modal>
			)}
		</>
	)
}

export default memo(HIEConsentPolicyEditor)