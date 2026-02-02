import React, { useCallback } from 'react'

import { TextField as Field } from 'components/Form'

import { first } from 'lib/utils/ArrayUtils'

import './TextField.scss'

const types = ['text','textarea', 'number', 'email', 'password']

function getType(type) {
    return types.includes(type) ? type : 'text'
}

export default function TextField(
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
    let type = 'text'
    if (schema.format) type = getType(schema.format)
    if (uiSchema['ui:widget']) type = getType(uiSchema['ui:widget'])

    const _onChange = useCallback((name, value) => {
        onChange(value ? value : undefined)
    }, [onChange])

    return (
        <Field
            type={type}
            name={name}
            value={value}

            isDisabled={disabled}
            placeholder={placeholder}
            className={className}
            errorText={first(rawErrors)}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}