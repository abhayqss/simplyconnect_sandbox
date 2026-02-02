import React, {
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import Switch from 'react-switch'
import { FormGroup, Label } from 'reactstrap'

import './FeatureSwitchField.scss'

function FeatureSwitchField(
    {
        name,
        label,
        isChecked,
        isDisabled,
        description,
        errorText,
        onChange: onChangeCb,
        className
    }
) {
    const onChange = useCallback(value => {
        onChangeCb(name, value)
    }, [name, onChangeCb])

    return (
        <FormGroup
            data-testid={`${name}_field`}
            className={cn('FeatureSwitchField', className)}
        >
            {label && (
                <Label className='FeatureSwitchField-Label'>
                    {label}
                </Label>
            )}
            {description && (
                <div className="FeatureSwitchField-Description">
                    {description}
                </div>
            )}
            <div className="FeatureSwitchField-SwitchBox">
                <Switch
                    name={name}
                    onColor='#0064ad'
                    offColor='#dddddd'
                    handleDiameter={18}
                    onChange={onChange}
                    disabled={isDisabled}
                    checked={isChecked}
                    data-testid={`${name}_field-switch`}
                    checkedIcon={(
                        <div
                            data-testid={`${name}_field-yes`}
                            className='FeatureSwitchField-Checked'
                        >
                            YES
                        </div>
                    )}
                    uncheckedIcon={(
                        <div
                            data-testid={`${name}_field-no`}
                            className='FeatureSwitchField-Unchecked'
                        >
                            NO
                        </div>
                    )}
                />
            </div>
            {errorText ? (
                <div className="SwitchField-Error">
                    {errorText}
                </div>
            ) : null}
        </FormGroup>
    )
}

FeatureSwitchField.propTypes = {
    name: PTypes.string,
    label: PTypes.string,
    isChecked: PTypes.bool,
    isDisabled: PTypes.bool,
    description: PTypes.string,
    errorText: PTypes.string,
    onChange: PTypes.func,
    className: PTypes.string
}

export default FeatureSwitchField