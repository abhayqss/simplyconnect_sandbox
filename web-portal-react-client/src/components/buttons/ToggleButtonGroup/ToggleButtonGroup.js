import React, { memo } from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { map } from 'lib/utils/ArrayUtils'
import { noop } from 'lib/utils/FuncUtils'

import { ToggleButton } from '../'

function ToggleButtonGroup({ buttons, className, onClick }) {
	return (
		<div className={cn('ToggleButtonGroup', className)}>
			{map(buttons, btn => (
				<ToggleButton key={btn.id || btn.name} onClick={onClick} {...btn}>
					{btn.text}
				</ToggleButton>
			))}
		</div>
	)
}

ToggleButtonGroup.propTypes = {
	buttons: PTypes.arrayOf(PTypes.object),
	className: PTypes.string,
	onClick: PTypes.func
}

ToggleButtonGroup.defaultProps = {
	onClick: noop
}

export default memo(ToggleButtonGroup)