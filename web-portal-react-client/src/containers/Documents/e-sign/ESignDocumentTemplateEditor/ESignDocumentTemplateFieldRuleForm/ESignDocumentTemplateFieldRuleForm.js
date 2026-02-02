import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import PropTypes from 'prop-types'

import {
    Form,
    Button
} from 'reactstrap'

import {
    map,
    chain,
    sortBy
} from 'underscore'

import {
    Loader,
    IconButton,
    ErrorViewer
} from 'components'
import { WarningDialog } from 'components/dialogs'
import { SelectField } from 'components/Form'

import {
    useCustomFormFieldChange,
    useForm
} from 'hooks/common'

import Entity from "entities/e-sign/ESignDocumentTemplateFieldRule"
import Validator from "validators/ESignDocumentTemplateFieldRuleValidator"

import { ReactComponent as DeleteIcon } from "images/templateBuilder/delete.svg";

import './ESignDocumentTemplateFieldRuleForm.scss'

const UNIQ_FIELD_ERROR_MESSAGE = "Field should be unique";

function valueTextMapper(o) {
    const text = typeof o === "string" ? o : o.title;

    return { value: text, text }
}
function ESignDocumentTemplateFieldRuleForm(
    {
        rules,
        fields,
        signatures,

        onCancel,
        onChanged,
        onSubmitSuccess
    }
) {
    const [isDuplicateWarningDialogOpen, toggleDuplicateWarningDialog] = useState(false);

    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        fields: formFields
    } = useForm('ESignDocumentTemplateFieldRuleForm', Entity, Validator)

    const {
        changeSelectField
    } = useCustomFormFieldChange(changeField)

    const mappedFields = useMemo(() => sortBy(
        map(fields, valueTextMapper), "text"
    ), [fields])

    const mappedSignatures = useMemo(() => sortBy(
        map(signatures, valueTextMapper), "text"
    ), [signatures])

    const rulesValue = useMemo(() => {
        const value = formFields.value;

        return Array.isArray(value) ? value : value.toJS();
    }, [formFields])

    const nonUniqFields = useMemo(() => {
        const nonUniqFields = chain(rulesValue)
            .groupBy('field')
            .filter(o => o.length > 1)
            .flatten()
            .pluck('field')
            .compact().value();

        return new Set(nonUniqFields)
    }, [rulesValue])


    function init() {
        const mappedRules = rules.map(({ id, field, signature }) => ({
            id,
            field,
            signature
        }))
        const initialValue = mappedRules.length ? mappedRules : [{
            id: Math.random(),
            field: null,
            signature: null
        }];

        changeField("value", initialValue, true)
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault();

        setFetching(true)

        validate()
            .then(async () => {
                setNeedValidation(false)

                try {
                    onSubmitSuccess(rulesValue)
                } catch (e) {
                    setError(e)
                }
            })
            .catch((e) => {
                if (typeof e.value === "string") {
                    toggleDuplicateWarningDialog(true);
                }

                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    const onDelete = useCallback((id) => () => {
        const filteredRules = rulesValue
            .filter(({ id: currentId }) => id !== currentId);

        changeField("value", filteredRules)
    }, [rulesValue, changeField])

    const onAdd = useCallback(() => {
        changeField("value",
            [
                ...rulesValue,
                {
                    id: Math.random(),
                    field: null,
                    signature: null
                }
            ]
        );
    }, [
        rulesValue,
        changeField
    ])

    const _onCancel = useCallback(
        () => onCancel(isChanged),
        [onCancel, isChanged]
    )

    const onSubmit = useCallback(
        tryToSubmit,
        [
            validate,
            rulesValue
        ]
    )

    useEffect(init, [signatures, changeField])

    useEffect(validateIf, [
        validate,
        needValidation
    ])


    useEffect(() => {
        onChanged(isChanged)
    }, [isChanged, onChanged])

    return (
        <>
            <Form className="ESignDocumentTemplateFieldRuleForm" onSubmit={onSubmit}>
                {isFetching && (
                    <Loader hasBackdrop />
                )}
                <div className="ESignDocumentTemplateFieldRuleForm-Body">
                    {map(rulesValue, ({ id, field, signature }, idx) => (
                        <div
                            key={id}
                            className='d-flex flex-no-wrap justify-content-between'
                        >
                            <SelectField
                                label="Field*"
                                value={field}
                                options={mappedFields}
                                name={`value.${idx}.field`}
                                hasError={errors?.value?.[idx]?.field || nonUniqFields.has(field)}
                                errorText={errors?.value?.[idx]?.field
                                    || (nonUniqFields.has(field) ? UNIQ_FIELD_ERROR_MESSAGE : '')}
                                className="ESignDocumentTemplateFieldRuleForm-SelectField"

                                onChange={changeSelectField}
                            />

                            <span className='ESignDocumentTemplateFieldRuleForm-Text'>visible if</span>

                            <SelectField
                                value={signature}
                                label="Signature*"
                                options={mappedSignatures}
                                name={`value.${idx}.signature`}
                                hasError={errors?.value?.[idx]?.signature}
                                errorText={errors?.value?.[idx]?.signature}
                                className="ESignDocumentTemplateFieldRuleForm-SelectField"

                                onChange={changeSelectField}
                            />

                            <span className='ESignDocumentTemplateFieldRuleForm-Text'>selected</span>

                            <IconButton
                                size={20}
                                shouldHighLight={false}
                                className="ESignDocumentTemplateFieldRuleForm-DeleteAction"
                                Icon={DeleteIcon}
                                onClick={onDelete(id)}
                            />
                        </div>
                    ))}
                </div>
                <div className="ESignDocumentTemplateFieldRuleForm-Buttons">
                    <Button
                        color="success"
                        className='mr-auto margin-left-0'
                        onClick={onAdd}
                    >
                        Add Rule
                    </Button>

                    <Button
                        outline
                        color="success"
                        onClick={_onCancel}
                    >
                        Cancel
                    </Button>

                    <Button
                        color="success"
                        disabled={!isValid || nonUniqFields.size || !rulesValue.length}
                    >
                        Apply
                    </Button>
                </div>
            </Form>

            <WarningDialog
                isOpen={isDuplicateWarningDialogOpen}
                title="Please delete duplicate rules to proceed"
                buttons={[
                    {
                        text: 'Close',
                        color: 'success',
                        onClick: () => toggleDuplicateWarningDialog(false)
                    }
                ]}
            />

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

ESignDocumentTemplateFieldRuleForm.propTypes = {
    rules: PropTypes.array,
    fields: PropTypes.array,
    signatures: PropTypes.array,

    onCancel: PropTypes.func,
    onChanged: PropTypes.func,
    onSubmitSuccess: PropTypes.func
}


export default memo(ESignDocumentTemplateFieldRuleForm)
