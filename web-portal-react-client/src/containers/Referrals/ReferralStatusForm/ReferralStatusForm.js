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
    ErrorViewer,
} from 'components'

import {
    TextField,
    SelectField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useDirectoryData,
    useScrollToFormError
} from 'hooks/common'

import { useReferralDeclineReasonsQuery } from 'hooks/business/directory'
import { useReferralStatusFormSubmit } from 'hooks/business/admin/referrals'

import ReferralStatusEntity from 'entities/ReferralStatus'
import ReferralStatusFormValidator from 'validators/ReferralStatusFormValidator'

import './ReferralStatusForm.scss'

const STATUSES = [
    { title: 'Accepted', name: 'ACCEPTED' },
    { title: 'Declined', name: 'DECLINED' }
]

const scrollableStyles = { flex: 1 }

export default function ReferralStatusForm(
    {
        onClose,
        requestId,
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
    } = useForm('ReferralStatus', ReferralStatusEntity, ReferralStatusFormValidator)

    const data = useMemo(() => fields.toJS(), [fields])
    const submit = useReferralStatusFormSubmit(data, { requestId })

    const mappedStatuses = useMemo(
        () => STATUSES.map(o => ({
            value: o.name,
            text: o.title
        })), []
    )

    const { reasons } = useDirectoryData({
        reasons: ['referral', 'decline', 'reason']
    })

    const mappedReasons = useMemo(() => reasons.map(reason => ({
        value: reason.id,
        text: reason.title
    })), [reasons])

    const { Scrollable, scroll } = useScrollable()

    useReferralDeclineReasonsQuery()

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
    const onScroll = useScrollToFormError('.ReferralStatusForm', scroll)

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
        <Form className="ReferralStatusForm">
            {isFetching && <Loader hasBackdrop/>}
            <Scrollable style={scrollableStyles}>
                <div className="ReferralStatusForm-Section">
                    <Row>
                        <Col md="4">
                            <SelectField
                                name="status"
                                value={fields.status}
                                options={mappedStatuses}
                                label="Status*"
                                className="ReferralStatusForm-SelectField"
                                errorText={errors.status}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                    {fields.status === 'DECLINED' && (
                        <Row>
                            <Col md="4">
                                <SelectField
                                    name="referralDeclineReasonId"
                                    value={fields.referralDeclineReasonId}
                                    options={mappedReasons}
                                    label="Reason*"
                                    className="RequestDeclineForm-SelectField"
                                    errorText={errors.referralDeclineReasonId}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    )}
                    <Row>
                        <Col>
                            <TextField
                                type="textarea"
                                name="comment"
                                value={fields.comment}
                                maxLength={5000}
                                label="Comment"
                                className="ReferralStatusForm-TextArea"
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="ReferralStatusForm-Buttons">
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
                    Submit
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