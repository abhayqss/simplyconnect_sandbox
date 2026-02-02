import React, {
	useRef,
	useMemo,
	useState,
	useEffect
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { noop } from 'underscore'

import { UncontrolledTooltip } from 'reactstrap'

import './IconButton.scss'

function Tooltip(props) {
	return (
		<UncontrolledTooltip
			trigger="click hover"
			modifiers={[
				{
					name: 'offset',
					options: { offset: [0, 6] }
				},
				{
					name: 'preventOverflow',
					options: { boundary: document.body }
				}
			]}
			{...props}
		/>
	)
}

export default function IconButton(
	{
		id,
		name,
		Icon,
		disabled,
		size = 36,
		tooltip,
		hasTip = true, //@deprecated
		renderTip, //@deprecated
		tipText = '', //@deprecated
		tipPlace = 'left-start', //@deprecated
		className,
		onClick = noop,
		tipTrigger = 'click hover', //@deprecated
		tipClassName, //@deprecated
		shouldHighLight = true,
        children
	}
) {
	const ref = useRef()

	const [, setTooltipReady] = useState(false)

	const style = useMemo(() => (
		{ width: size, height: size }
	), [size])

	useEffect(() => setTooltipReady(true), [])

	return (
		<div
            ref={ref}
			onClick={onClick}
			className={cn(
				'IconButton',
				className,
				{ 'IconButton_disabled': disabled },
				{ 'IconButton_highlighted': shouldHighLight }
			)}
			data-testid={name}
		>
            {!children && (
                <Icon id={id || name} style={style} className="IconButton-Icon"/>
            )}

            {children && (
                <div className="d-flex flex-row">
                    <Icon id={id || name} style={style} className="IconButton-Icon"/>
                    <div className="IconButton-Body">{children}</div>
                </div>
            )}

			{tooltip && ref.current && typeof tooltip === 'string' && (
				<Tooltip target={ref.current}>
					{tooltip}
				</Tooltip>
			)}

			{tooltip && ref.current && typeof tooltip === 'object' && (
				<Tooltip target={ref.current} {...tooltip}>
					{tooltip.text ?? tooltip.render()}
				</Tooltip>
			)}

			{hasTip && ref.current && (tipText || renderTip) && (
				<Tooltip
					placement={tipPlace}
					target={ref.current}
					trigger={tipTrigger}
					className={tipClassName}
				>
					{tipText || renderTip()}
				</Tooltip>
			)}
		</div>
	)
}

IconButton.propTypes = {
	name: PTypes.string,
	disabled: PTypes.bool,
	tipText: PTypes.string,
	tipPlace: PTypes.string,
	renderTip: PTypes.func,
	className: PTypes.string,
	tooltip: PTypes.oneOfType([PTypes.string, PTypes.object])
}