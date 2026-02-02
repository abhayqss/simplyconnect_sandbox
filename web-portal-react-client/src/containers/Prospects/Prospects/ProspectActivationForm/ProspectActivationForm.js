import React, {
    memo,
    useMemo,
    useState,
    useEffect
} from 'react'

import {
    Col,
    Row,
    Form,
    Button,
} from 'reactstrap'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    TextField,
    DateField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import {
    useProspectActivation
} from 'hooks/business/Prospects'

import ProspectActivation from 'entities/ProspectActivation'
import ProspectActivationFormValidator from 'validators/ProspectActivationFormValidator'

import './ProspectActivationForm.scss'

const TODAY = Date.now();

const scrollableStyles = { flex: 1 }

function ProspectActivationForm(
    {
        prospectId,
        onCancel,
        onSubmitSuccess
    }
) {
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [isValidationNeeded, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        changeDateField
    } = useForm(
        'ProspectActivationForm',
        ProspectActivation,
        ProspectActivationFormValidator
    )

    const data = useMemo(() => fields.toJS(), [fields])

    const { Scrollable, scroll } = useScrollable()

    const scrollToError = useScrollToFormError(
        '.ProspectActivationForm', scroll
    )

    const { mutateAsync: activate } = useProspectActivation({
        ...data,
        prospectId
    }, {
        onError: setError,
        onSuccess: data => {
            onSubmitSuccess(data)
        }
    })

    function cancel() {
        onCancel(isChanged)
    }

    function validateIf() {
        if (isValidationNeeded) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault()

        setFetching(true)

        validate()
            .then()
            .then(async () => {
                await activate()
                setNeedValidation(false)
            })
            .catch(() => {
                scrollToError()
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    useEffect(validateIf, [isValidationNeeded, scrollToError, validate])

    return (
        <>
            <Form className="ProspectActivationForm" onSubmit={tryToSubmit}>
                {isFetching && (
                    <Loader style={{ position: 'fixed' }} hasBackdrop/>
                )}

                <Scrollable style={scrollableStyles} className="ProspectActivationForm-Sections">
                    <div className="ProspectActivationForm-Section">
                        <Row>
                            <Col md={4}>
                                <DateField
                                    name="activationDate"
                                    value={fields.activationDate}
                                    dateFormat="MM/dd/yyyy"
                                    maxDate={TODAY}
                                    label="Activate date*"
                                    placeholder="Select date"
                                    onChange={changeDateField}
                                    errorText={errors.activationDate}
                                    className="ProspectActivationForm-DateField"
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md={12}>
                                <TextField
                                    type="textarea"
                                    name="comment"
                                    value={fields.comment}
                                    label="Comment"
                                    numberOfRows={10}
                                    className="ProspectActivationForm-TextField"
                                    errorText={errors.comment}
                                    maxLength={5000}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    </div>
                </Scrollable>

                <div className="ProspectActivationForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={cancel}
                    >
                        Close
                    </Button>

                    <Button
                        color="success"
                        disabled={isFetching || !isValid}
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

export default memo(ProspectActivationForm)
