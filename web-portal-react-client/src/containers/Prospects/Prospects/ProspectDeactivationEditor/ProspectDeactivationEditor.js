import React, {
	memo,
	useCallback
} from 'react'

import { Modal } from 'components'

import {
	ConfirmDialog,
	SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import DeactivationForm from '../ProspectDeactivationForm/ProspectDeactivationForm'

import './ProspectDeactivationEditor.scss'

function ProspectDeactivationEditor({ prospectId, isOpen, onClose, onSaveSuccess }) {
	const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
	const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

	const onSubmitSuccess = useCallback(() => {
		onSaveSuccess()
		toggleSaveSuccessDialog(true)
	}, [onSaveSuccess, toggleSaveSuccessDialog])

	const onCancel = useCallback(
		(isChanged) => {
			toggleSaveSuccessDialog(false)

			if (isChanged) {
				toggleCancelEditConfirmDialog(true)
			} else {
				toggleCancelEditConfirmDialog(false)
				onClose()
			}
		},
		[onClose, toggleCancelEditConfirmDialog, toggleSaveSuccessDialog]
	)

	const closeAllModals = useCallback(() => onCancel(), [onCancel])

	return (
		<>
			{isCancelEditConfirmDialogOpen && (
				<ConfirmDialog
					isOpen
					icon={Warning}
					confirmBtnText="OK"
					title="The updates will not be saved."
					onConfirm={closeAllModals}
					onCancel={toggleCancelEditConfirmDialog}
				/>
			)}

			{isSaveSuccessDialogOpen && (
				<SuccessDialog
					isOpen
					title="The prospect record has been deactivated"
					buttons={[
						{
							text: "Close",
							onClick: closeAllModals
						}
					]}
				/>
			)}

			{isOpen && (
				<Modal
					isOpen={isOpen}
					className="ProspectDeactivationEditor"
					title="Deactivate Prospect Record"
					hasFooter={false}
					hasCloseBtn={false}
				>
					<DeactivationForm
						prospectId={prospectId}
						onCancel={onCancel}
						onSubmitSuccess={onSubmitSuccess}
					/>
				</Modal>
			)}
		</>
	)
}

export default memo(ProspectDeactivationEditor)
