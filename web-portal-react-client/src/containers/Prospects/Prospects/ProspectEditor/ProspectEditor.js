import React, {
	useCallback
} from 'react'

import { noop } from 'underscore'

import { useCancelConfirmDialog } from 'hooks/common'

import { Modal } from 'components'

import { isInteger } from 'lib/utils/Utils'

import ProspectForm from '../ProspectForm/ProspectForm'

import './ProspectEditor.scss'

function ProspectEditor(
	{
		isOpen,
		onClose,
		prospectId,
		communityId,
		organizationId,
		onSaveSuccess = noop
	}
) {
	const isEditing = isInteger(prospectId)

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
				title={`${isEditing ? 'Edit' : 'Create New'} Prospect`}
				className="ProspectEditor"
			>
				<ProspectForm
					prospectId={prospectId}
					communityId={communityId}
					organizationId={organizationId}
					onSubmitSuccess={onSaveSuccess}
					onClose={_onClose}
				/>
			</Modal>
		</>
	)
}

export default ProspectEditor
