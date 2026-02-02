import React, { memo, useState, useEffect, useCallback } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { Form, Col, Row, Button } from 'reactstrap'

import { useDebounce } from 'use-debounce'

import TextField from 'components/Form/TextField/TextField'

import { useForm, useScrollable, useResponse, useScrollToFormError } from 'hooks/common'

import ReferralCommunication from 'entities/ReferralCommunication'
import RequestInfoFormValidator from 'validators/RequestInfoFormValidator'

import * as errorActions from 'redux/error/errorActions'
import actions from 'redux/referral/info/request/form/referralInfoRequestFormActions'

import './RequestInfoForm.scss'

const scrollableStyles = { flex: 1 }

const mapDispatchToProps = dispatch => ({
    actions: {
        ...bindActionCreators(actions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
    }
})

function RequestInfoForm({ onClose, actions, requestId, onSubmitSuccess }) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
    } = useForm('RequestInfo', ReferralCommunication, RequestInfoFormValidator)

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    const [data] = useDebounce({
        ...fields.toJS(),
        response: null,
    }, 200)

    function cancel() {
        onClose(isChanged)
    }

    function submit() {
        setIsFetching(true)

        validate()
            .then(async () => {
                onResponse(
                    await actions.submit(data, { requestId })
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

    const onSubmit = useCallback(submit, [onResponse, actions.submit, validate, requestId, data])
    const onScroll = useScrollToFormError('.RequestInfoForm', scroll)
    const onCancel = useCallback(cancel, [onClose, isChanged])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="RequestInfoForm">
            <Scrollable style={scrollableStyles}>
                <div className="RequestInfoForm-Section">
                    <Row>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="subject"
                                value={fields.subject}
                                maxLength={256}
                                label="Subject*"
                                className="RequestInfoForm-TextField"
                                errorText={errors.subject}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="request.authorFullName"
                                value={fields.request.authorFullName}
                                maxLength={256}
                                label="Requester name*"
                                className="RequestInfoForm-TextField"
                                errorText={errors.request?.authorFullName}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="request.authorPhone"
                                value={fields.request.authorPhone}
                                maxLength={16}
                                label="Requester phone #*"
                                className="RequestInfoForm-TextField"
                                errorText={errors.request?.authorPhone}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <TextField
                                type="textarea"
                                name="request.text"
                                value={fields.request.text}
                                maxLength={20000}
                                label="Message*"
                                className="RequestInfoForm-TextArea"
                                errorText={errors.request?.text}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="RequestInfoForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                    className="RequestInfoForm-Button"
                >
                    Cancel
                </Button>
                <Button
                    color="success"
                    onClick={onSubmit}
                    disabled={isFetching}
                    className="RequestInfoForm-Button RequestInfoForm-Button_Submit"
                >
                    Send request
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(RequestInfoForm)
