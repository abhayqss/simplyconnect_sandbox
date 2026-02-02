import React, {
	memo,
	useCallback
} from "react"

import {
	Modal
} from 'components'

import {
	ConfirmDialog,
	SuccessDialog
} from 'components/dialogs'

import { useToggle } from 'hooks/common'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import AppointmentCancelForm from '../AppointmentCancelForm/AppointmentCancelForm'

import './AppointmentCancelEditor.scss'

function AppointmentCancelEditor(
    {
      isOpen,
      appointmentId,
      onClose,
      onCancelSuccess
    }
) {
	const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useToggle()
	const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

	const _onCancelSuccess = useCallback(() => {
		onCancelSuccess()
		toggleSaveSuccessDialog(true)
	}, [toggleSaveSuccessDialog, onCancelSuccess])

	const onCloseForm = useCallback(
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

	const closeAllModals = useCallback(() => {
		toggleSaveSuccessDialog(false)
		toggleCancelEditConfirmDialog(false)
		onClose()
	}, [onClose, toggleSaveSuccessDialog, toggleCancelEditConfirmDialog])

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
					title="The appointment has been canceled"
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
                    title="Cancel appointment"
                    isOpen={isOpen}
                    hasFooter={false}
                    hasCloseBtn={false}
                    className="AppointmentCancelEditor"
                >
					<AppointmentCancelForm
						appointmentId={appointmentId}

						onClose={onCloseForm}
						onCancelSuccess={_onCancelSuccess}
					/>
				</Modal>
			)}
		</>
	)
}

export default memo(AppointmentCancelEditor)
