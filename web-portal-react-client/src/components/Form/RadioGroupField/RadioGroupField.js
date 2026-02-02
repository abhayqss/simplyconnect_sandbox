import React, { Component, Fragment } from 'react'

import PropTypes from 'prop-types'

import cn from 'classnames'

import { map } from 'underscore'

import {
    Label,
    FormGroup
} from 'reactstrap'

import { Radio, RadioGroup } from 'react-radio-group'

import './RadioGroupField.scss'

export default class RadioGroupField extends Component {

    static propTypes = {
        name: PropTypes.string,
        title: PropTypes.string,
        label: PropTypes.string,
        errorText: PropTypes.string,
        className: PropTypes.string,
        containerClass: PropTypes.string,

        options: PropTypes.array,

        hasError: PropTypes.bool,
        isDisabled: PropTypes.bool,
        view: PropTypes.oneOf(['col', 'row']),

        selected: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number,
            PropTypes.bool
        ]),

        onChange: PropTypes.func,
        renderTitleIcon: PropTypes.func
    }

    static defaultProps = {
        options: [],
        hasError: false,
        isDisabled: false,
        view: 'col',
        errorText: '',

        onChange: function() {},
        renderTitleIcon: function() {},
    }

    onChange = (option) => {
        const { name, onChange: cb } = this.props
        cb(name, option)
    }

    render() {
        const {
            view,
            name,
            title,
            label,
            options,
            selected,
            className,
            errorText,
            isDisabled,
            containerClass,
            renderTitleIcon
        } = this.props

        const hasError = this.props.hasError || !!errorText

        return (
            <FormGroup
                data-testid={`${name}_field`}
                className={cn(
                    'RadioGroupField',
                    isDisabled && 'RadioGroupField_disabled',
                    { 'RadioGroupField_row': view === 'row' },
                    containerClass
                )}
            >
                {(label || title) ? (
                    <>
                        <Label
                            data-testid={`${name}_field-label`}
                            className='RadioGroupField-Title'>
                            {label || title}
                        </Label>
                        {renderTitleIcon && renderTitleIcon()}
                    </>
                ) : null}
                <RadioGroup
                    name={name}
                    selectedValue={selected}
                    onChange={this.onChange}
                    className={cn(
                        "form-control",
                        { 'is-invalid': hasError },
                        "RadioGroupField-Body",
                        className
                    )}
                >
                    {map(options, (radio, index) => (
                        <Label
                            key={index}
                            className={cn(
                                "Radio",
                                radio.isDisabled && 'Radio_disabled'
                            )}
                        >
                            <Radio disabled={radio.isDisabled || isDisabled} value={radio.value}/>
                            <span className="Radio-Label">{radio.label}</span>
                            <span
                                data-testid={`${name}_field-${radio.value}-check-mark`}
                                className={cn(
                                    'Radio-CheckMark',
                                    { 'Radio-CheckMark_checked': selected === radio.value },
                                    hasError && 'Radio-CheckMarkError'
                                )}
                            />
                        </Label>
                    ))}
                </RadioGroup>
                {hasError ? (
                    <div className="RadioGroupField-Error">
                        {errorText}
                    </div>
                ) : null}
            </FormGroup>
        )
    }
}