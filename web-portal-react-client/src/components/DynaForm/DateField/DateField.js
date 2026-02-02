import React, { useCallback } from 'react'

import { DateField as Field } from 'components/Form'

import { first } from 'lib/utils/Utils'

import './DateField.scss'

export default function DateField(
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
    const title = schema.title ?? uiSchema['ui:title']
    const label = required ? title + '*' : title

    const _onChange = useCallback((name, value) => {
        onChange(value ? value.getTime() : undefined)
    }, [onChange])

    const errorText = first(rawErrors)

    return (
        <Field
            name={name}
            label={label}
            value={value}

            isDisabled={disabled}
            placeholder={placeholder}
            className={className}
            {...uiSchema['ui:options'] ?? {}}
            errorText={errorText}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}