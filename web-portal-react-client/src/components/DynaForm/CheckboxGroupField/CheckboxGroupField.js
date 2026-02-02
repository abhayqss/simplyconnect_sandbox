import React, { useMemo, useCallback } from 'react'

import { CheckboxGroupField as Field } from 'components/Form'

import { first } from 'lib/utils/Utils'

import './CheckboxGroupField.scss'

export default function CheckboxGroupField(
    {
        id,
        name,
        schema,
        uiSchema,
        idSchema,
        formData = [],
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

    const options = useMemo(() => {
        return schema.items.enum.map((title, index) => {
            return {
                value: false,
                label: title
            }
        })
    }, [schema])

    const value = useMemo(() => {
        return options.map((option) => {
            return {
                ...option,
                value: formData?.includes(option.label) ?? false
            }
        })
    }, [options, formData])

    const _onChange = useCallback((name, options) => {
        const value = options
            .filter(option => option.value)
            .map(option => option.label)

        onChange(value)
    }, [onChange])

    const errorText = first(rawErrors)

    return (
        <Field
            name={name}
            label={label}
            value={value}

            className={className}
            options={options}
            {...uiSchema['ui:options'] ?? {}}
            errorText={errorText}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}