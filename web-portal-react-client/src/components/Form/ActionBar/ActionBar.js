import React, { memo } from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import './ActionBar.scss'

function ActionBar({ children, className }) {
	return (
		<div className={cn('FormActionBar', className)}>
			{children}
		</div>
	)
}

ActionBar.propTypes = {
	className: PTypes.string
}

export default memo(ActionBar)