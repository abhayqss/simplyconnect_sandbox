import React, {
	memo,
	useState,
	useEffect,
	useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import {
	Button,
} from 'reactstrap'

import {
	noop,
	sortBy
} from 'underscore'

import {
	CancelConfirmDialog
} from 'components/dialogs'

import Modal from 'components/Modal/Modal'

import {
	isNotEqual
} from 'lib/utils/Utils'

import {
	isEmpty,
	isNotEqLength
} from 'lib/utils/ArrayUtils'

import './Directory.scss'

function Directory(
	{
		title,
		isOpen,
		isLoading,
		completeBtnText,
		itemUniqKeyField,
		selectedItems: defaultSelectedItems,
		children,

		className,

		onClose,
		onComplete,
	}
) {
	const [isChanged, setChanged] = useState(false)
	const [selectedItems, setSelectedItems] = useState(defaultSelectedItems)

	const [isCancelConfirmDialogOpen, toggleCancelConfirmDialog] = useState(false)

	const _onClose = useCallback(() => {
		if (isChanged) {
			toggleCancelConfirmDialog(true)
		} else {
			onClose()
		}
	}, [isChanged, onClose])

	const _onComplete = useCallback(() => {
		onClose()
		onComplete(selectedItems)
	}, [
		onClose,
		onComplete,
		selectedItems
	])

	useEffect(() => {
		const sortedSelected = sortBy(
			selectedItems, itemUniqKeyField
		)

		const sortedDefaultSelected = sortBy(
			defaultSelectedItems, itemUniqKeyField
		)

		setChanged(
			isNotEqLength(selectedItems, defaultSelectedItems)
			|| isNotEqual(sortedSelected, sortedDefaultSelected)
		)
	}, [
		selectedItems,
		itemUniqKeyField,
		defaultSelectedItems
	])

	return (
		<>
			<CancelConfirmDialog
				isOpen={isCancelConfirmDialogOpen}
				onCancel={() => toggleCancelConfirmDialog(false)}
				onConfirm={() => {
					onClose()
					toggleCancelConfirmDialog(false)
				}}
			/>

			{isOpen && (
				<Modal
					isOpen
					title={title}
					hasFooter={false}
					hasCloseBtn={false}
					className={cn("Directory", className)}
				>
					<div className="Directory-Body">
						<div className="Directory-Section">
							{children && children({ setSelectedItems })}
						</div>
						<div className="Directory-Actions">
							<Button
								outline
								color="success"
								onClick={_onClose}
								className="Directory-Action Directory-CloseAction"
							>
								Cancel
							</Button>
							<Button
								color="success"
								onClick={_onComplete}
								disabled={isLoading || isEmpty(selectedItems)}
								className="Directory-Action Directory-CompleteAction"
							>
								{completeBtnText}
							</Button>
						</div>
					</div>
				</Modal>
			)}
		</>
	)
}

Directory.propTypes = {
	title: PTypes.string,
	isOpen: PTypes.bool,
	isLoading: PTypes.bool,
	completeBtnText: PTypes.string,

	selectedItems: PTypes.array,
	itemUniqKeyField: PTypes.string,

	className: PTypes.string,

	onClose: PTypes.func,
	onComplete: PTypes.func,
}

Directory.defaultProps = {
	title: 'Select',
	selectedItems: [],
	onClose: noop,
	onComplete: noop,
}

export default memo(Directory)
