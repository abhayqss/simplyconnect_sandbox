import React from 'react'

import PTypes from 'prop-types'

import { Button } from 'reactstrap'

import { Modal } from 'components'

import Details from './ClientExpenseDetails/ClientExpenseDetails'

import './ClientExpenseViewer.scss'

function ClientExpenseViewer({ isOpen, onClose, clientId, expenseId }) {
	return (
		<Modal
			isOpen={isOpen}
			onClose={onClose}
			hasCloseBtn={false}
			title="View Expense"
			className="ClientExpenseViewer"
			renderFooter={() => (
				<Button color="success" onClick={onClose}>
					Close
				</Button>
			)}
		>
			<Details
				clientId={clientId}
				expenseId={expenseId}
			/>
		</Modal>
	)
}

ClientExpenseViewer.propTypes = {
	isOpen: PTypes.bool,
	clientId: PTypes.number,
	expenseId: PTypes.number,
	onClose: PTypes.func
}

export default ClientExpenseViewer