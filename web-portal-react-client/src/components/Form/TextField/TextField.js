import React, {
	memo,
	useRef,
	useEffect,
	useCallback
} from 'react'

import cn from 'classnames'
import PropTypes from 'prop-types'

import Inputmask from 'inputmask'

import {
	Label,
	Input,
	FormGroup,
	UncontrolledTooltip as Tooltip
} from 'reactstrap'

import {
	isNumber,
	isNullOrUndefined
} from 'lib/utils/Utils'

import {
	noop
} from 'lib/utils/FuncUtils'

import './TextField.scss'

function TextField(
	{
		id,
		type,
		name,
		mask,
		label,
		tooltip,
		innerSpan,
		innerSpanText,
		className,
		maxDigits,
		numberOfRows,
		placeholder,
		renderIcon,
		renderLabelIcon,
		isDisabled,
		hasHint,
		errorText,
		autoComplete,
		maxLength,
		tabIndex,
		onBlur,
		onFocus,
		onChange,
		onKeyDown,
		onEnterKeyDown,
		...props
	}
) {
	const inputRef = useRef()

	const value = props.value ?? ''
	const hasError = props.hasError || !!errorText
	const isControlled = Object.prototype.hasOwnProperty.call(props, 'value')

	const _onBlur = useCallback(e => {
		let value = e.target.value
		onBlur && onBlur(name, value)
	}, [name, onBlur])

	const _onFocus = useCallback(e => {
		let value = e.target.value
		onFocus && onFocus(name, value)
	}, [name, onFocus])

	const _onChange = useCallback(e => {
		let value = e.target.value

		if (isNullOrUndefined(maxLength)) {
			onChange && onChange(name, value)
		}

		if (isNumber(maxLength) && value?.length <= maxLength) {
			onChange && onChange(name, value)
		}
	}, [name, onChange, maxLength])

	const _onKeyDown = useCallback(e => {
		const value = e.target.value

		onKeyDown(name, value)

		if (e.key.toLowerCase() === 'enter') {
			onEnterKeyDown(name, value)
		}
	}, [name, onKeyDown, onEnterKeyDown])

	useEffect(() => {
		Inputmask(mask).mask(inputRef.current)
	}, [mask])

	return (
		<FormGroup
			data-testid={`${name}_field`}
			className={cn(
				'TextField',
				className,
				isDisabled ? 'TextField_disabled' : null
			)}
		>
			{label ? (
				<>
					<Label
						data-testid={`${name}_field-label`}
						className="TextField-Label"
					>
						{label}
					</Label>
					{renderLabelIcon && renderLabelIcon()}
					{tooltip && (
						<Tooltip
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
				</>
			) : null}
			{innerSpan ? <span className='inner-span'>{innerSpanText}</span> : null}
			<Input
				id={id}
				type={type}

				name={name}
				{...isControlled && { value }}

				innerRef={inputRef}

				max={maxDigits}
				invalid={hasError}
				tabIndex={tabIndex}
				rows={numberOfRows}
				disabled={isDisabled}
				maxLength={maxLength}
				data-testid={`${name}_field-input`}
				title={isDisabled ? value : null}

				placeholder={placeholder}
				autoComplete={autoComplete}

				className="TextField-Input"

				onBlur={_onBlur}
				onFocus={_onFocus}
				onChange={_onChange}
				onKeyDown={_onKeyDown}
			/>
			{hasError ? (
				<div
					data-testid={`${name}_field-error`}
					className={`TextField-${hasHint ? 'Hint' : 'Error'}`}
				>
					{errorText}
				</div>
			) : null}
			{renderIcon && renderIcon(value)}
		</FormGroup>
	)
}

TextField.propTypes = {
	type: PropTypes.oneOf(['text', 'textarea', 'number', 'email', 'password', 'date']),
	name: PropTypes.string,
	label: PropTypes.string,
	value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	className: PropTypes.string,
	placeholder: PropTypes.string,
	autoComplete: PropTypes.string,
	maxLength: PropTypes.number,
	numberOfRows: PropTypes.number,

	mask: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
	tooltip: PropTypes.object,

	tabIndex: PropTypes.number,
	maxDigits: PropTypes.number,

	hasHint: PropTypes.bool,
	hasError: PropTypes.bool,
	isDisabled: PropTypes.bool,

	errorText: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
	renderIcon: PropTypes.func,
	renderLabelIcon: PropTypes.func,
	onBlur: PropTypes.func,
	onFocus: PropTypes.func,
	onChange: PropTypes.func,
	onKeyDown: PropTypes.func,
	onEnterKeyDown: PropTypes.func
}

TextField.defaultProps = {
	type: 'text',
	tabIndex: 1,
	hasHint: false,
	isDisabled: false,
	errorText: '',
	autoComplete: 'off',

	onBlur: noop,
	onFocus: noop,
	onChange: noop,
	onKeyDown: noop,
	onEnterKeyDown: noop
}

export default memo(TextField)