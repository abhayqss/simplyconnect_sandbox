import React, {
	useCallback
} from 'react'

import { noop } from 'underscore'

import { useCancelConfirmDialog } from 'hooks/common'

import { Modal } from 'components'

import { isInteger } from 'lib/utils/Utils'

import AppointmentForm from '../AppointmentForm/AppointmentForm'

import './AppointmentEditor.scss'

function AppointmentEditor(
	{
		isOpen,
		onClose,
		clientId,
		isDuplicating,
		appointmentId,
		appointmentDate,
		organizationId,
		onSaveSuccess = noop
	}
) {
	const isEditing = isInteger(appointmentId) && !isDuplicating

    const [CancelConfirmDialog, setCancelConfirmDialogOpen] = useCancelConfirmDialog()

	function closeIfNotChanged(hasChanges) {
		if (hasChanges) {
			setCancelConfirmDialogOpen(true)
		} else {
			onClose()
		}
	}

	const _onClose = useCallback(
		closeIfNotChanged,
		[onClose, setCancelConfirmDialogOpen]
	)

	return (
		<>
			<CancelConfirmDialog onConfirm={onClose}/>

			<Modal
				isOpen={isOpen}
				hasFooter={false}
				hasCloseBtn={false}
				title={`${isDuplicating ? 'Duplicate' : isEditing ? 'Edit' : 'Create'} Appointment`}
				className="AppointmentEditor"
			>
				<AppointmentForm
					clientId={clientId}
					isDuplicating={isDuplicating}
					appointmentId={appointmentId}
					appointmentDate={appointmentDate}
					organizationId={organizationId}
					onClose={_onClose}
					onSubmitSuccess={onSaveSuccess}
				/>
			</Modal>
		</>
	)
}

export default AppointmentEditor
