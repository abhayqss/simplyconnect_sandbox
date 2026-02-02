import React, {
    memo,
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
    AlertPanel,
    ErrorViewer
} from 'components'


import {
    TextField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useQueryInvalidation,
    useScrollToFormError
} from 'hooks/common'

import { useAppointmentCancel } from 'hooks/business/appointments'

import AppointmentCancel from 'entities/AppointmentCancel'
import AppointmentCancelFormValidator from 'validators/AppointmentCancelFormValidator'

import './AppointmentCancelForm.scss'

const scrollableStyles = { flex: 1 }

function AppointmentCancelForm(
    {
        appointmentId,

        onClose,
        onCancelSuccess
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
    } = useForm(
        'AppointmentCancelForm',
        AppointmentCancel,
        AppointmentCancelFormValidator
    )
    const invalidate = useQueryInvalidation()


    const { Scrollable, scroll } = useScrollable()

    const scrollToError = useScrollToFormError('.AppointmentCancelForm', scroll)

    const { mutateAsync: cancel } = useAppointmentCancel({
        onError: setError,
        onSuccess: ({ data }) => {
            invalidate("Appointment", { id: appointmentId })
            onCancelSuccess(data)
        }
    });

    function close() {
        onClose(isChanged)
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
            .then(async () => {
                await cancel({
                    id: appointmentId,
                    cancellationReason: fields.cancelReason
                })

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
            <Form className="AppointmentCancelForm" onSubmit={tryToSubmit}>
                {(isFetching) && (
                    <Loader hasBackdrop />
                )}

                <Scrollable style={scrollableStyles} className="AppointmentCancelForm-Sections">
                    <div className="AppointmentCancelForm-Section">
                        <AlertPanel className="AppointmentCancelForm-AlertPanel">
                            The appointment will be cancelled. Client and client/community care team members will be notified according to their notifications preference.
                        </AlertPanel>
                        <Row>
                            <Col md={12}>
                                <TextField
                                    type="text"
                                    name="cancelReason"
                                    value={fields.cancelReason}
                                    label="Cancellation reason*"
                                    className="AppointmentCancelForm-TextField"
                                    errorText={errors.cancelReason}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    </div>
                </Scrollable>

                <div className="AppointmentCancelForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={close}
                    >
                        Close
                    </Button>

                    <Button
                        color="success"
                        disabled={isFetching || !isValid}
                    >
                        Cancel Appointment
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

export default memo(AppointmentCancelForm)
