import React, { memo, useState, useEffect, useCallback } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { Form, Col, Row, Button } from 'reactstrap'

import { useDebounce } from 'use-debounce'

import TextField from 'components/Form/TextField/TextField'

import { useForm, useScrollable, useResponse, useScrollToFormError } from 'hooks/common'

import ReferralCommunication from 'entities/ReferralCommunication'
import ResponseInfoFormValidator from 'validators/ResponseInfoFormValidator'

import * as errorActions from 'redux/error/errorActions'
import actions from 'redux/referral/info/response/form/referralInfoResponseFormActions'

import './ResponseInfoForm.scss'

const scrollableStyles = { flex: 1 }

const mapDispatchToProps = dispatch => ({
    actions: {
        ...bindActionCreators(actions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
    }
})

function ResponseInfoForm({ onClose, actions, referralId, infoRequestId, onSubmitSuccess }) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
    } = useForm('ResponseInfo', ReferralCommunication, ResponseInfoFormValidator)

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    const [data] = useDebounce({
        ...fields.toJS(),
        request: null,
    }, 200)

    function cancel() {
        onClose(isChanged)
    }

    function submit() {
        setIsFetching(true)

        validate()
            .then(async () => {
                onResponse(
                    await actions.submit(data, { referralId, infoRequestId })
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

    const onSubmit = useCallback(submit, [onResponse, actions.submit, validate, referralId, infoRequestId, data])
    const onScroll = useScrollToFormError('.ResponseInfoForm', scroll)
    const onCancel = useCallback(cancel, [onClose, isChanged])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="ResponseInfoForm">
            <Scrollable style={scrollableStyles}>
                <div className="ResponseInfoForm-Section">
                    <Row>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="subject"
                                value={fields.subject}
                                maxLength={256}
                                label="Subject*"
                                className="ResponseInfoForm-TextField"
                                errorText={errors.subject}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="response.authorFullName"
                                value={fields.response.authorFullName}
                                maxLength={256}
                                label="Requester name*"
                                className="ResponseInfoForm-TextField"
                                errorText={errors.response?.authorFullName}
                                onChange={onChangeField}
                            />
                        </Col>
                        <Col md="4">
                            <TextField
                                type="text"
                                name="response.authorPhone"
                                value={fields.response.authorPhone}
                                maxLength={16}
                                label="Requester phone #*"
                                className="ResponseInfoForm-TextField"
                                errorText={errors.response?.authorPhone}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <TextField
                                type="textarea"
                                name="response.text"
                                value={fields.response.text}
                                maxLength={20000}
                                label="Message*"
                                className="ResponseInfoForm-TextArea"
                                errorText={errors.response?.text}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="ResponseInfoForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                    className="ResponseInfoForm-Button"
                >
                    Cancel
                </Button>
                <Button
                    color="success"
                    onClick={onSubmit}
                    disabled={isFetching}
                    className="ResponseInfoForm-Button ResponseInfoForm-Button_Submit"
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
)(ResponseInfoForm)
