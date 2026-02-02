import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map,
    noop,
    first,
} from 'underscore'

import {
    Col,
    Row,
    Form,
    Button
} from 'reactstrap'

import { Loader } from 'components'

import {
    TextField,
    SelectField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'
import { useAuthUser } from 'hooks/common/redux'
import {
    useBusinessUnitCodesQuery
} from 'hooks/business/docutrack'

import {
    useSendToDocuTrackMutation
} from 'hooks/business/conversations'

import { SendToDocuTrackFormValidator } from 'validators'

import DocuTrackDocumentEntity from 'entities/DocuTrackDocument'

import './SendToDocuTrackForm.scss'

const scrollableStyles = { flex: 1 }

function valueTextMapper({ pharmacyId, businessCode }) {
    return { value: `${pharmacyId}:${businessCode}`, text: businessCode }
}

function SendToDocuTrackForm(
    {
        media,
        onClose = noop,
        onSubmitSuccess = noop,
        onSubmitFailure = noop
    }
) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const user = useAuthUser()

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm(
        'SendDocToDocuTrack',
        DocuTrackDocumentEntity,
        SendToDocuTrackFormValidator
    )

    const { Scrollable, scroll } = useScrollable()

    const { mutateAsync: mutate } = useSendToDocuTrackMutation({
        onError: onSubmitFailure,
        onSuccess: onSubmitSuccess
    })

    const {
        data: businessUnitCodes,
        isFetching: isFetchingBusinessUnitCodes
    } = useBusinessUnitCodesQuery()

    function cancel() {
        onClose(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate()
            .then(async () => {
                await mutate({ mediaSid: media.sid, ...fields.toJS() })
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

    function setDefaultData() {
        const documentText = `${media.name} ${user.fullName} Simply Connect`

        changeField('documentText', documentText)
    }

    function setPredefinedCode() {
        if (mappedBusinessUnitCodes.length === 1) {
            const buc = first(mappedBusinessUnitCodes)

            onChangeBusinessUnitCode('businessUnitCode', buc.value)
        }
    }

    const mappedBusinessUnitCodes = useMemo(
        () => map(businessUnitCodes, valueTextMapper), [businessUnitCodes]
    )

    const onChangeBusinessUnitCode = useCallback((name, value) => {
        const [pharmacyId, code] = value?.split(':') || ':'
        changeFields({ [name]: code, pharmacyId })
    }, [changeFields])


    const onScroll = useScrollToFormError('.SendToDocuTrackForm', scroll)

    const onCancel = useCallback(cancel, [onClose, isChanged])

    const onSubmit = useCallback(submit, [media, mutate, fields, onScroll, validate])

    useEffect(validateIf, [needValidation, onScroll, validate])

    useEffect(setDefaultData, [user, media, changeField])

    useEffect(setPredefinedCode, [onChangeBusinessUnitCode, mappedBusinessUnitCodes])

    return (
        <Form className="SendToDocuTrackForm" onSubmit={onSubmit}>
            {isFetching && (<Loader hasBackdrop/>)}
            <Scrollable style={scrollableStyles}>
                <div className="SendToDocuTrackForm-Section">
                    <Row className="margin-bottom-20">
                        <Col>
                            <SelectField
                                type="text"
                                name="businessUnitCode"
                                value={`${fields.pharmacyId}:${fields.businessUnitCode}`}
                                options={mappedBusinessUnitCodes}
                                label="Business Unit Code"
                                isDisabled={isFetchingBusinessUnitCodes}
                                className="SendToDocuTrackForm-SelectField"
                                errorText={errors.businessUnitCode}
                                onChange={onChangeBusinessUnitCode}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <TextField
                                type="textarea"
                                name="documentText"
                                value={fields.documentText}
                                label="Document Text"
                                maxLength={5000}
                                numberOfRows={4}
                                className="SendToDocuTrackForm-TextAreaField"
                                errorText={errors.documentText}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="SendToDocuTrackForm-Buttons">
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
        </Form>
    )
}

export default memo(SendToDocuTrackForm)
