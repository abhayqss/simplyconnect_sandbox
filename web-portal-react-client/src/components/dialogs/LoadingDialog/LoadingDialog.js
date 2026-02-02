import React, { memo } from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { Modal } from 'reactstrap'

import { ReactComponent as Loader } from 'images/loader.svg'

import './LoadingDialog.scss'

function LoadingDialog(
	{
		text,
		isOpen,
		className,
		children
	}
) {
	return (
		<Modal
			fade={false}
			centered={true}
			isOpen={isOpen}
			className={cn('LoadingDialog', className)}>
			<div
				data-testid="loading-dialog"
				className="modal-body LoadingDialog-Body">
				<div className="LoadingDialog-IconContainer">
					<Loader className="LoadingDialog-Icon"/>
				</div>
				{text ? (
					<div className="LoadingDialog-Text">
						{text}
					</div>
				) : children}
			</div>
		</Modal>
	)
}

LoadingDialog.propTypes = {
	text: PTypes.string,
	isOpen: PTypes.bool,
	className: PTypes.string,
}

export default memo(LoadingDialog)
