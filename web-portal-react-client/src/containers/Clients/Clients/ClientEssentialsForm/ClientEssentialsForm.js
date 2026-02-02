import React, { memo, useState, useEffect, useCallback } from 'react'

import moment from 'moment'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import { Form, Col, Row, Button } from 'reactstrap'

import Loader from 'components/Loader/Loader'
import TextField from 'components/Form/TextField/TextField'
import DateField from 'components/Form/DateField/DateField'

import { useForm, useResponse, useScrollable, useScrollToFormError } from 'hooks/common'

import Entity from 'entities/ClientEssentials'
import Validator from 'validators/ClientEssentialsFormValidator'

import * as errorActions from 'redux/error/errorActions'
import formActions from 'redux/client/essentials/form/clientEssentialsFormActions'

import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientEssentialsForm.scss'

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(formActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
        }
    }
}

const scrollableStyles = { flex: 1 }

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const formatStringDate = value => value ? moment(value, 'MM/DD/YYYY').toDate().getTime() : null

const getDate = fields => {
    let data = fields.toJS()

    return {
        ...data,
    }
}

const AlertPanel = ({ children }) => (
    <div className="ClientEssentialsForm-Alert">
        <span className="ClientEssentialsForm-AlertText">
            {children}
        </span>
    </div>
)

const MAX_DATE = new Date()

function ClientEssentialsForm({ onClose, initialData, actions, onSubmitSuccess }) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
        changeFields: onChangeFields,
    } = useForm('CLIENT', Entity, Validator)

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(() => onSubmitSuccess(getDate(fields)), [fields, onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function cancel() {
        onClose(isChanged)
    }

    function submit() {
        setIsFetching(true)

        validate()
            .then(async () => {
                onResponse(
                    await actions.submit(getDate(fields))
                )
                setNeedValidation(false)
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
            .finally(() => {
                setIsFetching(false)
            })
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function setInitialData() {
        onChangeFields(initialData, true)
    }

    const onScroll = useScrollToFormError('.ClientEssentialsForm', scroll)

    const onCancel = useCallback(cancel, [onClose, isChanged])
    const onSubmit = useCallback(submit, [validate, actions.submit, onResponse])

    const onChangeBirthDate = useCallback((field, value) => {
        onChangeField(field, value ? format(value, DATE_FORMAT) : null)
    }, [onChangeField])

    useEffect(setInitialData, [initialData, onChangeFields])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="ClientEssentialsForm">
            {isFetching && (<Loader hasBackdrop />)}

            <Scrollable style={scrollableStyles}>
                <div className="ClientEssentialsForm-Section">
                    <Row className="margin-bottom-20">
                        <Col md={4}>
                            <TextField
                                type="text"
                                name="firstName"
                                value={fields.firstName}
                                label="First Name*"
                                className="ClientEssentialsForm-TextField"
                                errorText={errors.firstName}
                                maxLength={256}
                                onChange={onChangeField}
                            />
                        </Col>

                        <Col md={4}>
                            <TextField
                                type="text"
                                name="lastName"
                                value={fields.lastName}
                                label="Last Name*"
                                className="ClientEssentialsForm-TextField"
                                errorText={errors.lastName}
                                maxLength={256}
                                onChange={onChangeField}
                            />
                        </Col>

                        <Col md={4}>
                            <DateField
                                name="birthDate"
                                className="ClientEssentialsForm-DateField"
                                value={formatStringDate(fields.birthDate)}
                                dateFormat="MM/dd/yyyy"
                                label="Date Of Birth*"
                                maxDate={MAX_DATE}
                                errorText={errors.birthDate}
                                onChange={onChangeBirthDate}
                            />
                        </Col>
                    </Row>

                    <AlertPanel>
                        By clicking on the "Save" button, the lab order and the client record will be updated.
                    </AlertPanel>
                </div>
            </Scrollable>

            <div className="ClientEssentialsForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                >
                    Cancel
                </Button>

                <Button
                    color="success"
                    onClick={onSubmit}
                    disabled={isFetching}
                >
                    Save
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(ClientEssentialsForm)
