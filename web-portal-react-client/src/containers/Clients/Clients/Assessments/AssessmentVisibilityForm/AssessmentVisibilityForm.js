import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    Col,
    Row,
    Form,
    Button
} from 'reactstrap'

import {
    Loader,
    AlertPanel,
    ErrorViewer
} from 'components'

import { TextField } from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import { useAssessmentVisibilityFormSubmit } from 'hooks/business/client'

import AssessmentVisibility from 'entities/AssessmentVisibility'
import AssessmentVisibilityFormValidator from 'validators/AssessmentVisibilityFormValidator'

import { ASSESSMENT_STATUSES } from 'lib/Constants'

import './AssessmentVisibilityForm.scss'

const { HIDDEN } = ASSESSMENT_STATUSES

const HIDING_WARNING_TEXT = 'The assessment will be visible to Admin users only'
const RESTORING_WARNING_TEXT = 'The assessment will be visible to all users who have access to this assessment'

const scrollableStyles = { flex: 1 }

export default function AssessmentVisibilityForm(
    {
        clientId,
        assessmentId,
        assessmentStatus,

        onClose,
        onSubmitSuccess
    }
) {
    const [error, setError] = useState(null)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField
    } = useForm('AssessmentVisibility', AssessmentVisibility, AssessmentVisibilityFormValidator)

    const data = useMemo(() => fields.toJS(), [fields])

    const submit = useAssessmentVisibilityFormSubmit(data, {
        clientId,
        assessmentId,
        assessmentStatus,
    })

    const { Scrollable, scroll } = useScrollable()

    function cancel() {
        onClose(isChanged)
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    const onCancel = useCallback(cancel, [onClose, isChanged])
    const onScroll = useScrollToFormError('.AssessmentVisibilityForm', scroll)

    const onSubmit = useCallback(() => {
        validate()
            .then(() => {
                setFetching(true)
                setNeedValidation(false)

                submit()
                    .then(({ data }) => {
                        onSubmitSuccess(data)
                    })
                    .catch(setError)
                    .finally(() => setFetching(false))
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
    }, [
        submit,
        onScroll,
        validate,
        onSubmitSuccess
    ])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="AssessmentVisibilityForm">
            {isFetching && <Loader hasBackdrop/>}
            <Scrollable style={scrollableStyles}>
                <div className="AssessmentVisibilityForm-Section">
                    <Row>
                        <AlertPanel className="AssessmentVisibilityForm-Alert">
                            {assessmentStatus === HIDDEN ? RESTORING_WARNING_TEXT : HIDING_WARNING_TEXT}
                        </AlertPanel>
                    </Row>
                    <Row>
                        <Col md="12">
                            <TextField
                                type="textarea"
                                name="comment"
                                value={fields.comment}
                                maxLength={5000}
                                label="Comment*"
                                errorText={errors?.comment}
                                className="AssessmentVisibilityForm-TextArea"
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="AssessmentVisibilityForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                >
                    Close
                </Button>
                <Button
                    color="success"
                    onClick={onSubmit}
                    disabled={isFetching}
                >
                    {assessmentStatus === HIDDEN ? 'Restore' : 'Hide'}
                </Button>
            </div>

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </Form>
    )
}