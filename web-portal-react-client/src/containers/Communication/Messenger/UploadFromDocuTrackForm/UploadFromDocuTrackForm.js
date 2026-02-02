import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    noop
} from 'underscore'

import {
    Col,
    Row,
    Form,
    Button
} from 'reactstrap'

import { Loader } from 'components'

import {
    TextField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import {
    useUploadFromDocuTrackMutation
} from 'hooks/business/conversations'

import { UploadFromDocuTrackFormValidator } from 'validators'

import DocuTrackDocumentEntity from 'entities/DocuTrackDocument'

import './UploadFromDocuTrackForm.scss'

const scrollableStyles = { flex: 1 }

function UploadFromDocuTrackForm(
    {
        conversationSid,
        onClose = noop,
        onSubmitSuccess = noop,
        onSubmitFailure = noop
    }
) {
    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField
    } = useForm(
        'UploadFromDocuTrack',
        DocuTrackDocumentEntity,
        UploadFromDocuTrackFormValidator
    )

    const { Scrollable, scroll } = useScrollable()

    const { mutateAsync: mutate } = useUploadFromDocuTrackMutation({
        onError: onSubmitFailure,
        onSuccess: onSubmitSuccess
    })

    function cancel() {
        onClose(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate()
            .then(async () => {
                await mutate({
                    conversationSid,
                    ...fields.toJS()
                })
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

    const onScroll = useScrollToFormError('.UploadFromDocuTrackForm', scroll)

    const onCancel = useCallback(cancel, [onClose, isChanged])
    const onSubmit = useCallback(submit, [mutate, fields, onScroll, validate, conversationSid])

    useEffect(validateIf, [needValidation, onScroll, validate])

    return (
        <Form className="UploadFromDocuTrackForm" onSubmit={onSubmit}>
            {isFetching && (<Loader hasBackdrop/>)}
            <Scrollable style={scrollableStyles}>
                <div className="UploadFromDocuTrackForm-Section">
                    <Row>
                        <Col>
                            <TextField
                                type="text"
                                name="documentId"
                                value={fields.documentId}
                                label="Document ID*"
                                maxLength={256}
                                className="UploadFromDocuTrackForm-TextField"
                                errorText={errors.documentId}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="UploadFromDocuTrackForm-Buttons">
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
                    Upload
                </Button>
            </div>
        </Form>
    )
}

export default memo(UploadFromDocuTrackForm)
