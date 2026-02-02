import React, {
    useMemo,
    useCallback
} from 'react'

import { SelectField as Field } from 'components/Form'

import './SelectField.scss'

export default function SelectField(
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

    const options = useMemo(() => {
        return schema?.enum?.map((value, index) => {
            return {
                value,
                text: schema.enumNames?.[index] ?? value
            }
        })
    }, [schema])

    return (
        <Field
            name={name}
            label={label}
            value={value}

            isDisabled={disabled}
            placeholder={placeholder}
            className={className}
            options={options}
            {...uiSchema['ui:options'] ?? {}}

            onBlur={onBlur}
            onFocus={onFocus}
            onChange={_onChange}
        />
    )
}