import React, { useCallback } from 'react'

import { CheckboxField as Field } from 'components/Form'

import './CheckboxField.scss'

export default function CheckboxField(
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
            placeholder={placeholder}
            className={className}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}