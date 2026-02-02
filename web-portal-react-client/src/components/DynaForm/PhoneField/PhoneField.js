import React, { useCallback } from 'react'

import { PhoneField as Field } from 'components/Form'

import './PhoneField.scss'

export default function PhoneField(
    {
        id,
        name,
        schema,
        uiSchema,
        idSchema,
        formData: value,
        errorSchema,
        registry,
        placeholder,
        required,
        disabled,
        readonly,
        autofocus,
        onChange,
        onKeyChange,
        onBlur,
        onFocus,
        formContext,
        className,
        rawErrors
    }
) {
    const label = (
        schema.title ?? uiSchema['ui:title']
    )

    const _onChange = useCallback((name, value) => {
        onChange(value)
    }, [onChange])

    return (
        <Field
            name={name}
            label={label}
            value={value}

            isDisabled={disabled}
            className={className}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}