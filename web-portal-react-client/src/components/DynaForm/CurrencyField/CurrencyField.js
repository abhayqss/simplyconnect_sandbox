import React, { useCallback } from 'react'

import { TextField as Field } from 'components/Form'

import './CurrencyField.scss'

const CURRENCY_REGEXP = new RegExp("^[0-9]+[.]{0,1}[0-9]{0,2}$");

export default function CurrencyField(
    {
        name,
        schema,
        uiSchema,
        disabled,
        className,
        formData: value,

        onBlur,
        onFocus,
        onChange,
    }
) {
    const label = (
        schema.title ?? uiSchema['ui:title']
    )

    const _onChange = useCallback((name, valueCandidate) => {
        if (!valueCandidate || CURRENCY_REGEXP.test(valueCandidate)) {
            onChange(valueCandidate)
        } else {
            onChange(value)
        }
    }, [onChange, value]);

    return (
        <Field
            name={name}
            label={label}
            value={value || ""}

            isDisabled={disabled}
            className={className}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}