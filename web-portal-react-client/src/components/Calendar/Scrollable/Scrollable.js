import React, {
	memo,
	useRef,
	useEffect
} from 'react'

import PropTypes from 'prop-types'

import $ from 'jquery'
import 'jquery.scrollto'

import cn from 'classnames'

import { noop } from 'underscore'

import './Scrollable.scss'

function Scrollable(
	{
		style,
		offset,
		duration,
		children,
		className
	}
) {
	const ref = useRef()

	useEffect(() => {
		$(ref.current).scrollTo(offset, duration) || noop()
	}, [offset, duration])

	return (
		<div
			ref={ref}
			style={style}
			className={cn('CalendarScrollable', className)}
		>
			{children}
		</div>
	)
}

export default memo(Scrollable)

Scrollable.propTypes = {
	offset: PropTypes.number,
	duration: PropTypes.number,
	className: PropTypes.string
}

Scrollable.defaultProps = {
	offset: 0,
	duration: 0
}