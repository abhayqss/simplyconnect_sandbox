import React, { useMemo, useCallback } from 'react'

import { RadioGroupField as Field } from 'components/Form'

import { first } from 'lib/utils/Utils'

import './RadioGroupField.scss'

export default function RadioGroupField(
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
        onChange(value)
    }, [onChange])

    const options = useMemo(() => {
        return schema.enum.map((value, index) => {
            return {
                value,
                label: schema.enumNames?.[index] ?? value
            }
        })
    }, [schema])

    const errorText = first(rawErrors)

    return (
        <Field
            name={name}
            label={label}
            selected={value}

            isDisabled={disabled}
            className={className}
            options={options}
            errorText={errorText}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}