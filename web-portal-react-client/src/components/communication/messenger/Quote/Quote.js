import React, { memo } from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
	DateUtils as DU
} from 'lib/utils/Utils'

import { noop } from 'lib/utils/FuncUtils'

import { ReactComponent as Close } from 'images/cross-4.svg'

import { QUOTE_TYPES, QUOTE_TYPE_ICONS } from './lib/Constants'

import './Quote.scss'

const { format, formats } = DU

function Quote(
	{
		date,
		type,
		title,
		onClick,
		onClose,
		hasCloseBtn,
		className,
		children
	}
) {
	return (
		<div onClick={onClick} className={cn('Quote', className)}>
			<div className="Quote-Indicator"/>
			<div className="Quote-Content">
				<div className={cn("Quote-Header", hasCloseBtn && 'padding-right-35')}>
					<div className="Quote-Title">{title}</div>
					<div className="Quote-Date">{format(date, formats.time)}</div>
				</div>
				<div className="Quote-Body">
					{type !== QUOTE_TYPES.TEXT && QUOTE_TYPE_ICONS[type] && (
						<div className="margin-right-10">
							{QUOTE_TYPE_ICONS[type]()}
						</div>
					)}
					<div className="flex-1">
						{children}
					</div>
				</div>
				{hasCloseBtn && (
					<Close
						onClick={onClose}
						className="Quote-CloseBtn"
					/>
				)}
			</div>
		</div>
	)
}

Quote.propTypes = {
	type: PTypes.string,
	title: PTypes.string,
	date: PTypes.oneOfType([
		PTypes.number,
		PTypes.object
	]),
	onClick: PTypes.func,
	onClose: PTypes.func,
	className: PTypes.string
}

Quote.defaultProps = {
	onClose: noop
}

export default memo(Quote)