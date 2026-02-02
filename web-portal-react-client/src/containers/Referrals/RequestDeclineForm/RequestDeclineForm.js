import React, { memo, useState, useMemo, useEffect, useCallback } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { Form, Col, Row, Button } from 'reactstrap'

import TextField from 'components/Form/TextField/TextField'
import SelectField from 'components/Form/SelectField/SelectField'

import { useForm, useDirectoryData, useResponse, useScrollable, useScrollToFormError } from 'hooks/common'

import { useReferralDeclineReasonsQuery } from 'hooks/business/directory'

import RequestDeclineEntity from 'entities/RequestDecline'
import RequestDeclineFormValidator from 'validators/RequestDeclineFormValidator'

import * as errorActions from 'redux/error/errorActions'
import actions from 'redux/referral/request/decline/referralRequestDeclineActions'

import './RequestDeclineForm.scss'

const scrollableStyles = { flex: 1 }

const mapDispatchToProps = dispatch => ({
    actions: {
        ...bindActionCreators(actions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
    }
})

function RequestDeclineForm({ actions, onClose, requestId, onSubmitSuccess }) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
    } = useForm('RequestDecline', RequestDeclineEntity, RequestDeclineFormValidator)

    const { reasons } = useDirectoryData({
        reasons: ['referral', 'decline', 'reason']
    })

    const reasonOptions = useMemo(() => reasons.map(reason => ({
        value: reason.id,
        text: reason.title,
    })), [reasons])

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
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
                    await actions.submit(fields.toJS(), requestId)
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

    const onScroll = useScrollToFormError('.RequestDeclineForm', scroll)
    const onSubmit = useCallback(submit, [validate, actions.submit, requestId, fields, onResponse])
    const onCancel = useCallback(cancel, [onClose, isChanged])

    useReferralDeclineReasonsQuery()

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="RequestDeclineForm">
            <Scrollable style={scrollableStyles}>
                <div className="RequestDeclineForm-Section">
                    <Row>
                        <Col md="4">
                            <SelectField
                                name="referralDeclineReasonId"
                                value={fields.referralDeclineReasonId}
                                options={reasonOptions}
                                label="Reason*"
                                className="RequestDeclineForm-SelectField"
                                errorText={errors.referralDeclineReasonId}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <TextField
                                type="textarea"
                                name="comment"
                                value={fields.comment}
                                maxLength={20000}
                                label="Comment"
                                className="RequestDeclineForm-TextArea"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="RequestDeclineForm-Buttons">
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
                    Decline
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(RequestDeclineForm)
