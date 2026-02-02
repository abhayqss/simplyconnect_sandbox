import React, {
    useMemo,
    useState,
    useEffect,
} from 'react'

import {
    Col,
    Row,
    Form,
    Button,
} from 'reactstrap'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    TextField,
    PhoneField,
    SelectField,
    DropzoneField,
} from 'components/Form'

import {
    useForm,
    useAuthUser,
    useScrollable,
    useSelectOptions,
    useScrollToFormError
} from 'hooks/common'

import {
    useSupportTicketTypes
} from 'hooks/business/directory/query'

import {
    useSupportTicketSubmit
} from 'hooks/business/help/contact-us'

import {
    useContactQuery
} from 'hooks/business/admin/contact'

import ContactUs from 'entities/ContactUs'
import ContactUsFormValidator from 'validators/ContactUsFormValidator'

import {
    ALLOWED_FILE_FORMATS,
    ALLOWED_FILE_FORMAT_MIME_TYPES,
} from 'lib/Constants'

import './ContactUsForm.scss'

const {
    PDF,
    PNG,
    JPG,
    JPEG,
    GIF,
    TIFF,
    DOC,
    DOCX,
} = ALLOWED_FILE_FORMATS

const ALLOWED_MIME_TYPES = [PDF, PNG, JPG, JPEG, GIF, TIFF, DOC, DOCX].map(
    type => ALLOWED_FILE_FORMAT_MIME_TYPES[type]
)

const scrollableStyles = { flex: 1 }

function getData(fields) {
    const data = fields.toJS()

    return data
}

const messageFieldPlaceholder = "Please do not include client identifying information such as name,  birth date, or personal medical information in this support request."

function ContactUsForm({
    onCancel,
    onSubmitSuccess
}) {
    const [error, setError] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const user = useAuthUser()

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
    } = useForm(
        'ContactUsForm',
        ContactUs,
        ContactUsFormValidator
    )

    const {
        data: contact
    } = useContactQuery({ contactId: user?.id }, {
        enabled: Boolean(user?.id)
    })

    const {
        data: types,
        isFetching: isFetchingTicketTypes
    } = useSupportTicketTypes()

    const supportTicketTypeSelectOptions = useSelectOptions(types, { textProp: 'label' })

    const validationOptions = useMemo(() => ({
        included: {

        }
    }), [])

    const { Scrollable, scroll } = useScrollable()

    const onScroll = useScrollToFormError('.ContactUsForm', scroll)

    const { mutateAsync: submit, isLoading: isSubmitting }= useSupportTicketSubmit({
        onError: setError,
        onSuccess: ({ data }) => {
            onSubmitSuccess(data)
        }
    })

    function cancel() {
        onCancel(isChanged)
    }

    function init() {
        if (contact) {
            changeField('phone', contact.mobilePhone, true)
        }
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e) {
        e.preventDefault()

        validate(validationOptions)
            .then(async () => {
                await submit(getData(fields))

                setNeedValidation(false)
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
    }

    useEffect(validateIf, [needValidation, onScroll, validate])

    useEffect(init, [contact, changeField])

    return (
        <>
            <Form className="ContactUsForm" onSubmit={tryToSubmit}>
                {(isSubmitting || isFetchingTicketTypes) && (
                    <Loader hasBackdrop />
                )}

                <Scrollable style={scrollableStyles} className="ContactUsForm-Sections">
                    <div className="ContactUsForm-Section">
                        <Row>
                            <Col md={4}>
                                <PhoneField
                                    name="phone"
                                    value={fields.phone}
                                    label="Phone Number*"
                                    className="ContactUsForm-PhoneField"
                                    errorText={errors.phone}
                                    onChange={changeField}
                                />
                            </Col>

                            <Col md={8}>
                                <SelectField
                                    name="typeId"
                                    value={fields.typeId}
                                    label="How can we help you?*"
                                    options={supportTicketTypeSelectOptions}
                                    placeholder="Select"
                                    className="ContactUsForm-SelectField"
                                    errorText={errors.typeId}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <TextField
                                    type="textarea"
                                    name="messageText"
                                    value={fields.messageText}
                                    label="Message*"
                                    placeholder={messageFieldPlaceholder}
                                    className="ContactUsForm-TextField"
                                    errorText={errors.messageText}
                                    maxLength={5000}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col>
                                <DropzoneField
                                    name="attachmentFiles"
                                    label="Attachment"
                                    value={fields.attachmentFiles}
                                    maxCount={10}
                                    hintText="Supported file types: Word, TIFF, PDF, JPEG, GIF, PNG. | Max 20 mb"
                                    allowedTypes={ALLOWED_MIME_TYPES}
                                    className="ContactUsForm-DropzoneField"
                                    errors={errors.attachmentFiles}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    </div>
                </Scrollable>

                <div className="ContactUsForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={cancel}
                    >
                        Cancel
                    </Button>

                    <Button
                        color="success"
                        disabled={isSubmitting || !isValid}
                    >
                        Submit
                    </Button>
                </div>
            </Form>

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

export default ContactUsForm
