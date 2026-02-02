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

import ClientExpenseForm from '../ClientExpenseForm/ClientExpenseForm'

import { useCancelConfirmDialog } from 'hooks/common'

import './ClientExpenseEditor.scss'

function ClientExpenseEditor(
	{
		isOpen,
		clientId,
		onClose,
		onSaveSuccess
	}
) {
	const [isSuccessDialogOpen, toggleSuccessDialog] = useState(false)
	const [CancelConfirmDialog, toggleCancelConfirmDialog] = useCancelConfirmDialog()

	const _onSaveSuccess = useCallback(id => {
		onSaveSuccess(id)
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
					title={"The expense has been added."}
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
				title="Add Client Expense"
				className="ClientExpenseEditor"
			>
				<ClientExpenseForm
					clientId={clientId}
					onCancel={onCancel}
					onSubmitSuccess={_onSaveSuccess}
				/>
			</Modal>
		</>
	)
}

ClientExpenseEditor.propTypes = {
	onClose: PTypes.func,
	onSaveSuccess: PTypes.func
}

ClientExpenseEditor.defaultProps = {
	onClose: noop,
	onSaveSuccess: noop
}

export default ClientExpenseEditor