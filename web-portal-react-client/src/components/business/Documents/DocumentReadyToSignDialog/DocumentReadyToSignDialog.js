import React, {
	memo,
	useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	Input,
	InputGroup
} from 'reactstrap'

import { Button } from 'components/buttons'
import { SuccessDialog } from 'components/dialogs'

import { ReactComponent as Close } from 'images/delete.svg'

import './DocumentReadyToSignDialog.scss'

function DocumentReadyToSignDialog(
	{
		title,
		text,
		buttons,
		className,
		pinCode,
		signBtnText,
		onClose,
		onSign,
		...props
	}
) {

	const _onClose = useCallback(() => onClose(), [onClose])
	const _onSign = useCallback(() => onSign(), [onSign])

	return (
		<SuccessDialog
			title={title}
			buttons={buttons}
			className={cn('DocumentReadyToSignDialog', className)}
			{...props}
		>
			<div className="DocumentReadyToSignDialog-CloseButton" onClick={_onClose}>
				<Close />
			</div>
			{text && (
				<div className="margin-bottom-12">{text}</div>
			)}
			{pinCode && (
				<div className="d-flex flex-column align-items-center">
					<p className="DocumentReadyToSignDialog-Text">
						Please copy this PIN and paste it to open the document.
					</p>
					<p className="DocumentReadyToSignDialog-PinLabel">PIN</p>
					<InputGroup className="DocumentReadyToSignDialog-PinSection">
						<Input
							className="DocumentReadyToSignDialog-PinInput"
							readOnly
							defaultValue={pinCode}
						/>
						<Button
							color="success"
							onClick={_onSign}
							className="DocumentReadyToSignDialog-Button"
						>
							{signBtnText}
						</Button>
					</InputGroup>
				</div>
			)}
		</SuccessDialog>
	)
}

DocumentReadyToSignDialog.propTypes = {
	className: PTypes.string
}

DocumentReadyToSignDialog.defaultProps = {

}

export default memo(DocumentReadyToSignDialog)