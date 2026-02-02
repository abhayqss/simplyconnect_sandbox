import React, {
	useRef,
	useState,
	useEffect,
	useCallback
} from 'react'

import cn from 'classnames'

import PTypes from 'prop-types'

import { FormLabel } from 'react-bootstrap'

import { UncontrolledTooltip as Tooltip } from 'reactstrap'

import { noop } from 'lib/utils/FuncUtils'

import './CheckboxField.scss'

function CheckboxField(
	{
		id,
		name,
		label,
		value,
		tooltip,
		hasError,
		className,
		errorText,
		onChange,
		isRadio,
		isDisabled,
		renderLabelIcon
	}
) {
	const ref = useRef()

	const [, setReady] = useState(false)

	hasError = hasError || !!errorText

	const onClick = useCallback(() => {
		if (!isDisabled) onChange(name, !value)
	}, [name, value, onChange, isDisabled])

	//todo why <Tooltip> requires another render?
	useEffect(() => setReady(true), [])

	return (
		<div
			ref={ref}
			id={id || name}
			data-testid={`${id || name}_field`}
			className={cn(
				'CheckboxField form-group',
				isRadio && 'CheckboxField-Radio',
				isDisabled && 'CheckboxField_disabled',
				className
			)}
		>
			<div
				onClick={onClick}
				className={cn(
					'CheckboxField-Checkbox',
					{ 'CheckboxField-Checkbox__Error': hasError }
				)}
                data-testid={`${id || name}_field-checkbox`}
			>
				{value && (
					<span
						className="CheckboxField-CheckMark"
						data-testid={`${id || name}_field-check-mark`}
					/>
				)}
			</div>
			{hasError && (
				<div className="CheckboxField-Error">
					{errorText}
				</div>
			)}
			{label && (
				<>
					<FormLabel
						onClick={onClick}
						className="CheckboxField-Label"
                        data-testid={`${id || name}_field-label`}
                    >
						{label}
					</FormLabel>
					{renderLabelIcon && renderLabelIcon()}
				</>
			)}
			{tooltip && ref.current && (
				<Tooltip
					target={ref.current}
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
					{...tooltip}
				>
					{tooltip.text || tooltip.render()}
				</Tooltip>
			)}
		</div>
	)
}

CheckboxField.propTypes = {
	id: PTypes.oneOfType([PTypes.number, PTypes.string]),
	name: PTypes.oneOfType([PTypes.number, PTypes.string]),
	label: PTypes.oneOfType([PTypes.number, PTypes.string]),
	errorText: PTypes.string,
	value: PTypes.bool,
	isRadio: PTypes.bool,
	hasError: PTypes.bool,
	isDisabled: PTypes.bool,
	className: PTypes.string,
	onChange: PTypes.func,
	tooltip: PTypes.object,
	renderLabelIcon: PTypes.func
}

CheckboxField.defaultProps = {
	value: false,
	isRadio: false,
	hasError: false,
	isDisabled: false,
	onChange: noop,
	renderLabelIcon: noop
}

export default CheckboxField