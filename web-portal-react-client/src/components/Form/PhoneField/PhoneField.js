import React, {
    useCallback
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import { noop } from 'underscore'

import PhoneInput from 'react-phone-input-2'

import {
    Label,
    FormGroup,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import './PhoneField.scss'

export default function PhoneField(
    {
        name,
        label,
        country,
        tooltip,
        errorText,
        isDisabled,
        className,
        renderLabelIcon,
        onChange,
        ...props
    }
) {
    const _onChange = useCallback(value => {
        onChange(name, value)
    }, [name, onChange])

    return (
        <FormGroup
            data-testid={`${name}_field`}
            className={cn(
                'PhoneField',
                className,
                { 'PhoneField_disabled': isDisabled }
            )}
        >
            {label ? (
                <>
                    <Label
                        className='PhoneField-Label'
                        data-testid={`${name}_field-label`}
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
            <PhoneInput
                country={country}
                onChange={_onChange}
                isValid={!errorText}
                disabled={isDisabled}
                inputProps={{ 'data-testid': `${name}_field-input` }}
                inputClass={cn("PhoneField-Input", { 'is-invalid': errorText })}
                {...props}
            />
            {errorText ? (
                <div className='PhoneField-Error'>
                    {errorText}
                </div>
            ) : null}
        </FormGroup>
    )
}

PhoneField.propTypes = {
    ...PhoneInput.propTypes,
    name: PTypes.string,
    label: PTypes.string,
    tooltip: PTypes.any,
    errorText: PTypes.string,
    isDisabled: PTypes.bool,
    className: PTypes.string,
    country: PTypes.string,
    renderLabelIcon: PTypes.func,
    onChange: PTypes.func
}

PhoneField.defaultProps = {
    country: 'us',
    autoFormat: false,
    onChange: noop
}