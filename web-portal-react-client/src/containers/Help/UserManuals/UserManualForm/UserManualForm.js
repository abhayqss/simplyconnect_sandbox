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
import {TextField, FileField} from 'components/Form'

import {
    useForm,
    useResponse,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import UserManualEntity from 'entities/UserManual'
import UserManualFormValidator from 'validators/UserManualFormValidator'

import * as errorActions from 'redux/error/errorActions'
import formActions from 'redux/help/user/manual/form/userManualFormActions'

import FU from 'lib/utils/FileUtils'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import './UserManualForm.scss'

const { DOC, DOCX, PDF } = ALLOWED_FILE_FORMATS

const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
    ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF]
]

function preventDefault(e) {
    e.preventDefault()
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(formActions, dispatch),
            error: bindActionCreators(errorActions, dispatch),
        }
    }
}

const scrollableStyles = { flex: 1 }

function UserManualForm({ actions, onSubmitSuccess, ...props }) {
    const { manualId } = props

    const [isFetching, setIsFetching] = useState(false)
    const [isValidationRequired, setIsValidationRequired] = useState(false)

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm(
        'USER_MANUAL',
        UserManualEntity,
        UserManualFormValidator
    )

    const { Scrollable, scroll } = useScrollable()

    const onResponse = useResponse({
        onFailure: actions.error.change,
        onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
        onUnknown: actions.error.change
    })

    function cancel() {
        props.onCancel(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate()
            .then(async () => {
                onResponse(
                    await actions.submit({
                        id: manualId,
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
        changeFields({
            [name]: value ?? undefined,
            title: value?.name ?? ''
        })
    }, [changeFields])

    const onChangeTitleField = useCallback((name, value) => {
        const extension = FU.getFileExtension(
            fields.file.name
        )

        const title = value ? `${value}.${extension}` : ''

        changeField(name, title)
    }, [fields.file, changeField])

    const onScroll = useScrollToFormError('.UserManualForm', scroll)

    const onCancel = useCallback(cancel, [props.onCancel, isChanged])
    const onSubmit = useCallback(submit, [manualId, validate, actions.submit, onResponse])

    useEffect(validateIf, [isValidationRequired, onScroll, validate])

    return (
        <Form
            className="UserManualForm"
            onClear={preventDefault}
            onSubmit={preventDefault}
        >
            {isFetching && (<Loader hasBackdrop/>)}
            <Scrollable style={scrollableStyles}>
                <div className="UserManualForm-Section">
                    <Row className="margin-bottom-20">
                        <Col md={8}>
                            <FileField
                                hasHint
                                name="file"
                                value={fields.file?.name}
                                label="Choose document*"
                                className="UserManualForm-FileField"
                                placeholder="Document is not chosen"
                                hasError={!!errors.file}
                                errorText={errors.file?.name || errors.file?.size || errors.file?.type}
                                hintText="Supported file types: Word, PDF | Max 20 mb"
                                onChange={onChangeFileField}
                                allowedTypes={ALLOWED_FILE_MIME_TYPES}
                            />
                        </Col>
                        <Col md={8}>
                            <TextField
                                type="text"
                                name="title"
                                value={FU.getFileBaseName(fields.title)}
                                label="Title"
                                isDisabled={!fields.file?.size}
                                className="UserManualForm-TextField"
                                errorText={errors.title}
                                hintText="Supported file types: Word, PDF | Max 20 mb"
                                onChange={onChangeTitleField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="UserManualForm-Buttons">
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
                    disabled={isFetching || !isValid}
                >
                    Upload
                </Button>
            </div>
        </Form>
    )
}

export default compose(
    memo,
    connect(null, mapDispatchToProps)
)(UserManualForm)
