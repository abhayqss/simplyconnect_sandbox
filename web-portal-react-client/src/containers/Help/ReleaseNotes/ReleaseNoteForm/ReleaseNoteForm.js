import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import { Form, Col, Row, Button } from 'reactstrap'

import { Loader } from 'components'

import {
    TextField,
    FileField,
    CheckboxField
} from 'components/Form'

import {
    useForm,
    useResponse,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import ReleaseNoteEntity from 'entities/ReleaseNote'
import ReleaseNoteFormValidator from 'validators/ReleaseNoteFormValidator'

import * as errorActions from 'redux/error/errorActions'
import formActions from 'redux/help/release/note/form/releaseNoteFormActions'
import detailsActions from 'redux/help/release/note/details/releaseNoteDetailsActions'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import './ReleaseNoteForm.scss'
import { isInteger, isNotEmpty } from "../../../../lib/utils/Utils";

const { DOC, DOCX, PDF, } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
]

function preventDefault(e) {
    e.preventDefault()
}

function mapStateToProps(state) {
    return { details: state.help.release.note.details }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(formActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
            details: bindActionCreators(detailsActions, dispatch)
        }
    }
}

const scrollableStyles = { flex: 1 }

function ReleaseNoteForm(
    {
        actions,
        details,

        noteId,

        onSubmitSuccess,

        ...props
    }
) {

    const [isFetching, setIsFetching] = useState(false)
    const [isValidationRequired, setIsValidationRequired] = useState(false)

    const isEditMode = isInteger(noteId)

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm(
        'RELEASE_NOTE',
        ReleaseNoteEntity,
        ReleaseNoteFormValidator
    )

    const { Scrollable, scroll } = useScrollable()

    const onSubmitResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    const onFetchDataResponse = useResponse({
        onSuccess: useCallback(({ data }) => {
            changeFields(data, true)
        }, [changeFields]),
    })
    
    function fetchData() {
        return actions.details.load(noteId)
    }

    function cancel() {
        props.onCancel(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate()
            .then(async () => {
                onSubmitResponse(
                    await actions.submit({
                        id: noteId,
                        ...fields.toJS()
                    })
                )
                setIsValidationRequired(false)
            })
            .catch(() => {
                onScroll()
                setIsValidationRequired(true)
            })
            .finally(() => {
                setIsFetching(false)
            })
    }

    function validateIf() {
        if (isValidationRequired) {
            validate()
                .then(() => setIsValidationRequired(false))
                .catch(() => setIsValidationRequired(true))
        }
    }

    const onChangeFileField = useCallback((name, value) => {
        changeField(name, value ?? undefined)
    }, [changeFields])

    const onScroll = useScrollToFormError('.ReleaseNoteForm', scroll)

    const onCancel = useCallback(cancel, [props.onCancel, isChanged])
    const onSubmit = useCallback(submit, [noteId, validate, actions.submit, onSubmitResponse])

    useEffect(validateIf, [isValidationRequired, onScroll, validate])

    useEffect(() => {
        if (isEditMode) {
            fetchData().then(onFetchDataResponse)
        }
    }, [actions, noteId])

    return (
        <Form
            className="ReleaseNoteForm"
            onClear={preventDefault}
            onSubmit={preventDefault}
        >
            {isFetching && (<Loader hasBackdrop/>)}
            <Scrollable style={scrollableStyles}>
                <div className="ReleaseNoteForm-Section">
                    <Row>
                        <Col md={8}>
                            <FileField
                                hasHint
                                name="file"
                                value={fields.file?.name}
                                label="Choose document*"
                                className="ReleaseNoteForm-FileField"
                                placeholder="Document is not chosen"
                                hasError={!!errors.file}
                                errorText={errors.file?.name || errors.file?.size || errors.file?.type}
                                hintText="Supported file types: Word, PDF | Max 20 mb"
                                onChange={onChangeFileField}
                                allowedTypes={ALLOWED_FILE_MIME_TYPES}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col md={8}>
                            <TextField
                                type="text"
                                name="description"
                                value={fields.description}
                                label="Description*"
                                className="ReleaseNoteForm-TextField"
                                errorText={errors.description}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                    {/*<Row>
                        <Col lg={12}>
                            <CheckboxField
                                label="Notify end users via email"
                                name="isEmailNotificationEnabled"

                                isDisabled={isEditMode}
                                value={fields.isEmailNotificationEnabled}
                                errorText={errors.isEmailNotificationEnabled}

                                className="ReleaseNoteForm-CheckboxField"

                                onChange={changeField}
                            />
                        </Col>
                    </Row>*/}
                    <Row>
                        <Col lg={12}>
                            <CheckboxField
                                label="Enable In-app notification"
                                name="isInAppNotificationEnabled"

                                isDisabled={isEditMode}
                                value={fields.isInAppNotificationEnabled}
                                errorText={errors.isInAppNotificationEnabled}

                                className="ReleaseNoteForm-CheckboxField"

                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col lg={12}>
                            <TextField
                                type="textarea"
                                name="features"
                                value={fields.features}
                                label="What's New*"
                                className="ReleaseNoteForm-TextAreaField"
                                maxLength={5000}
                                numberOfRows={4}
                                errorText={errors.features}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                    <Row>
                        <Col lg={12}>
                            <TextField
                                type="textarea"
                                name="fixes"
                                value={fields.fixes}
                                label="Bug Fixes"
                                className="ReleaseNoteForm-TextAreaField"
                                maxLength={5000}
                                numberOfRows={4}
                                errorText={errors.fixes}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>
            <div className="ReleaseNoteForm-Buttons">
                <Button
                    outline
                    color="success"
                    disabled={isFetching}
                    onClick={onCancel}
                >
                    Cancel
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

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(ReleaseNoteForm)
