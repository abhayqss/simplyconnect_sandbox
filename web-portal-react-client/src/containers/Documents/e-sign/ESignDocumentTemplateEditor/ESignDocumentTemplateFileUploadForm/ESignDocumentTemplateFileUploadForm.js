import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useContext,
    useCallback
} from 'react'

import {
    Col,
    Row,
    Form
} from 'reactstrap'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    DropzoneField
} from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import Entity from "entities/e-sign/ESignDocumentTemplateFile"
import Validator from "validators/ESignDocumentTemplateFileUploadFormValidator"

import { ESignDocumentTemplateContext } from 'contexts'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES
} from 'lib/Constants'

import './ESignDocumentTemplateFileUploadForm.scss'

const {
    PDF
} = ALLOWED_FILE_FORMATS


const ALLOWED_FILE_MIME_TYPES = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
]

function ESignDocumentTemplateFileUploadForm(
    {
        children,
        documentId,

        onChanged,
        onSubmitSuccess,
        onCancel: onCancelCb
    }
) {
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const {
        templateData
    } = useContext(ESignDocumentTemplateContext)

    const isEditing = Boolean(documentId)

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField
    } = useForm('ESignDocumentTemplateFileUploadForm', Entity, Validator)

    const data = useMemo(() => fields.toJS(), [fields])

    const { Scrollable, scroll } = useScrollable()

    function init() {
        if (templateData?.file) {
            changeField('file', templateData?.file)
        }
    }

    function validateIf() {
        if (needValidation) {
            validate({ included: { isEditing } })
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault();

        setFetching(true)

        validate({ included: { isEditing } })
            .then(async () => {
                setFetching(false)
                setNeedValidation(false)

                try {
                    onSubmitSuccess({
                        ...data
                    })
                } catch (e) {
                    setError(e)
                }
            })
            .catch(() => {
                scrollToError()
                setFetching(false)
                setNeedValidation(true)
            })
    }

    const scrollToError = useScrollToFormError(
        '.ESignDocumentTemplateFileUploadForm', scroll
    )

    const onCancel = useCallback(
        () => onCancelCb(isChanged),
        [onCancelCb, isChanged]
    )

    const onSubmit = useCallback(
        tryToSubmit,
        [
            data,
            validate,
            scrollToError
        ]
    )

    const onChangeFileField = useCallback((name, value) => {
        changeField(name, value[0])
    }, [changeField])

    useEffect(init, [
        templateData,
        changeField
    ])

    useEffect(validateIf, [
        validate,
        scrollToError,
        needValidation
    ])

    useEffect(() => {
        onChanged(isChanged)
    }, [isChanged, onChanged])

    return (
        <>
            <Form className="ESignDocumentTemplateFileUploadForm" onSubmit={onSubmit}>
                {isFetching && (
                    <Loader hasBackdrop />
                )}
                <Scrollable className="ESignDocumentTemplateFileUploadForm-Sections">
                    <div className="ESignDocumentTemplateFileUploadForm-Section">
                        <Row>
                            <Col>
                                <DropzoneField
                                    name="file"
                                    label="Upload File*"
                                    value={data?.file ? [data.file] : []}
                                    maxCount={1}
                                    allowedTypes={ALLOWED_FILE_MIME_TYPES}
                                    hintText="Supported file types: PDF | Max 20 mb"
                                    className="ESignDocumentTemplateFileUploadForm-DropzoneField"
                                    errors={[errors.file]}
                                    onChange={onChangeFileField}
                                />
                            </Col>
                        </Row>
                    </div>
                </Scrollable>

                <div className="ESignDocumentTemplateFileUploadForm-Footer">
                    {children?.({
                        cancel: onCancel,
                        isValidToSubmit: isValid && fields.file
                    })}
                </div>
            </Form>
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}</>
    )
}


export default memo(ESignDocumentTemplateFileUploadForm)
