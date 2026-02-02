import React, {
	memo,
	useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { noop } from 'lib/utils/FuncUtils'

import { Button, IconButton } from '../'

function ToggleButton({ id, name, data, Icon, isChecked, className, onClick, ...props }) {
	const Btn = Icon ? IconButton : Button

	const _onClick = useCallback(e => {
		onClick(e, { id, name, data })
	}, [id, name, data, onClick])

	return (
		<Btn
			id={id}
			name={name}
			Icon={Icon}
			className={cn(
				'ToggleButton',
				className,
				isChecked && 'ToggleButton_checked'
			)}
			onClick={_onClick}
			{...props}
		/>
	)
}

ToggleButton.propTypes = {
	id: PTypes.oneOfType([PTypes.number, PTypes.string]),
	name: PTypes.oneOfType([PTypes.number, PTypes.string]),
	data: PTypes.any,
	Icon: PTypes.any,
	isChecked: PTypes.bool,
	className: PTypes.string,
	onClick: PTypes.func
}

ToggleButton.defaultProps = {
	onClick: noop
}

export default memo(ToggleButton)