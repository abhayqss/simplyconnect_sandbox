import React, { memo, useState, useEffect, useCallback } from 'react'

import { compose, bindActionCreators } from 'redux'
import { connect } from 'react-redux'

import { Form, Col, Row, Button } from 'reactstrap'

import { AlertPanel } from 'components'
import DateField from 'components/Form/DateField/DateField'

import { useForm, useResponse, useScrollable, useScrollToFormError } from 'hooks/common'

import ReferralRequestAcceptEntity from 'entities/ReferralRequestAccept'
import ReferralRequestAcceptValidator from 'validators/ReferralRequestAcceptValidator'

import * as errorActions from 'redux/error/errorActions'
import actions from 'redux/referral/request/accept/referralRequestAcceptActions'

import './ReferralRequestAcceptForm.scss'

const scrollableStyles = { flex: 1 }

const mapDispatchToProps = dispatch => ({
    actions: {
        ...bindActionCreators(actions, dispatch),
        error: bindActionCreators(errorActions, dispatch),
    }
})

function ReferralRequestAcceptForm({ actions, onClose, requestId, onSubmitSuccess }) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeDateField: onChangeDateField,
    } = useForm('RequestAccept', ReferralRequestAcceptEntity, ReferralRequestAcceptValidator)

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function cancel() {
        onClose(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate({
            included: {
                serviceStartDate: fields.serviceStartDate
            }
        })
            .then(async () => {
                onResponse(
                    await actions.submit(requestId, fields.toJS())
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

    const onScroll = useScrollToFormError('.ReferralRequestAcceptForm', scroll)
    const onSubmit = useCallback(submit, [validate, actions.submit, requestId, fields, onResponse])
    const onCancel = useCallback(cancel, [onClose, isChanged])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="ReferralRequestAcceptForm" onSubmit={onSubmit}>
            <Scrollable style={scrollableStyles}>
                <div className="ReferralRequestAcceptForm-Section">
                    <Row>
                        <Col md="6">
                            <DateField
                                type="text"
                                value={fields.serviceStartDate}
                                name="serviceStartDate"
                                hasTimeSelect
                                label="Service Start Date"
                                className="ReferralRequestAcceptForm-TextField"
                                timeFormat="hh:mm aa"
                                dateFormat="MM/dd/yyyy hh:mm a"
                                placeholder="mm/dd/yyyy"
                                errorText={errors.serviceStartDate}
                                onChange={onChangeDateField}
                            />
                        </Col>

                        <Col md="6">
                            <DateField
                                type="text"
                                value={fields.serviceEndDate}
                                name="serviceEndDate"
                                hasTimeSelect
                                label="Service End Date"
                                className="ReferralRequestAcceptForm-TextField"
                                timeFormat="hh:mm aa"
                                dateFormat="MM/dd/yyyy hh:mm a"
                                placeholder="mm/dd/yyyy"
                                errorText={errors.serviceEndDate}
                                onChange={onChangeDateField}
                            />
                        </Col>
                    </Row>

                    <AlertPanel className="margin-top-40">
                        {`By clicking on the "Accept" button, you confirm that eligibility/assessment completed and the individual has been accepted into program/service provision.`}
                    </AlertPanel>
                </div>
            </Scrollable>

            <div className="ReferralRequestAcceptForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                >
                    Cancel
                </Button>

                <Button
                    color="success"
                    disabled={isFetching}
                >
                    Accept
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(ReferralRequestAcceptForm)
