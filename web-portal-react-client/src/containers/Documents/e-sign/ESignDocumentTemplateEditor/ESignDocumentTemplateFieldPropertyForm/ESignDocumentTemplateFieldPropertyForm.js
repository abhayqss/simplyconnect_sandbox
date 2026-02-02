import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PropTypes from 'prop-types'

import {
    Form,
    Button
} from 'reactstrap'

import { ErrorViewer, Loader } from 'components'
import { TextField } from 'components/Form'

import {
    useForm
} from 'hooks/common'

import Entity from "entities/e-sign/ESignDocumentTemplateFieldProperty"
import Validator from "validators/ESignDocumentTemplateFieldPropertyValidator"

import './ESignDocumentTemplateFieldPropertyForm.scss'


function getData(fields, hasValue) {
    const { label, value } = fields.toJS()

    return {
        label,
        values: hasValue ? value.trim().split("\n") : []
    }
}

function ESignDocumentTemplateFieldPropertyForm(
    {
        hasValue,
        fieldType,
        fieldLabel,
        fieldValue,

        onCancel,
        onChanged,
        onSubmitSuccess
    }
) {
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm('ESignDocumentTemplateFieldPropertyForm', Entity, Validator)

    const validationOptions = {
        included: {
            hasValue,
        }
    }

    function init() {
        const data = {
            label: fieldLabel || "",
            value: hasValue && Array.isArray(fieldValue) ? fieldValue.join("\n") : "",
        }

        changeFields(data, true)
    }

    function validateIf() {
        if (needValidation) {
            validate(validationOptions)
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault();

        setFetching(true)

        validate(validationOptions)
            .then(async () => {
                setNeedValidation(false)

                try {
                    onSubmitSuccess(getData(fields, hasValue))
                } catch (e) {
                    setError(e)
                }
            })
            .catch(() => {
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    const _onCancel = useCallback(
        () => onCancel(),
        [onCancel]
    )

    const onSubmit = useCallback(
        tryToSubmit,
        [
            fields,
            validate,
            hasValue
        ]
    )

    useEffect(init, [
        hasValue,
        fieldLabel,
        fieldValue,
        changeFields
    ])
    useEffect(validateIf, [
        validate,
        needValidation
    ])

    useEffect(() => {
        onChanged(isChanged)
    }, [isChanged, onChanged])

    return (
        <>
            <Form className="ESignDocumentTemplateFieldPropertyForm" onSubmit={onSubmit}>
                {isFetching && (
                    <Loader hasBackdrop />
                )}
                <div className="ESignDocumentTemplateFieldPropertyForm-Body">
                    <TextField
                        type="text"
                        name="label"
                        value={fields.label}
                        label={`${fieldType} Label*`}
                        errorText={errors.label}
                        maxLength={30}
                        onChange={changeField}
                    />

                    {hasValue && (
                        <TextField
                            type="textarea"
                            name="value"
                            placeholder="Each value should be entered from a new line."
                            value={fields.value}
                            label={`${fieldType} Values*`}
                            maxLength={256}
                            errorText={errors.value}
                            className="ESignDocumentTemplateFieldPropertyForm-TextAreaField"
                            onChange={changeField}
                        />
                    )}
                </div>
                <div className="ESignDocumentTemplateFieldPropertyForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={_onCancel}
                    >
                        Cancel
                    </Button>

                    <Button
                        color="success"
                        disabled={!isValid}
                    >
                        Save
                    </Button>
                </div>
            </Form>
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

ESignDocumentTemplateFieldPropertyForm.propTypes = {
    hasValue: PropTypes.bool,
    fieldType: PropTypes.string,
    fieldValue: PropTypes.array,
    fieldLabel: PropTypes.string,

    onCancel: PropTypes.func,
    onChanged: PropTypes.func,
    onSubmitSuccess: PropTypes.func

}


export default memo(ESignDocumentTemplateFieldPropertyForm)
