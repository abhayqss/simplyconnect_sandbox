import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    Form,
    Col,
    Row,
    Button,
} from 'reactstrap'

import { Loader, ErrorViewer } from 'components'

import { TextField, ColorSelectField } from 'components/Form'

import {
    useForm,
    useScrollable,
    useScrollToFormError
} from 'hooks/common'

import {
    useOrganizationCategoryMutation,
    useUniqCategoryWithinOrganization
} from 'hooks/business/admin/organization'

import OrganizationCategory from 'entities/OrganizationCategory'

import OrganizationCategoryFormValidator from 'validators/OrganizationCategoryFormValidator'

import { ORGANIZATION_CATEGORY_COLORS } from 'lib/Constants'
import { isEmpty } from 'lib/utils/Utils'

import './OrganizationCategoryForm.scss'

const scrollableStyles = { flex: 1 }

const COLOR_SELECT_OPTIONS = Object
    .entries(ORGANIZATION_CATEGORY_COLORS)
    .map(([text, value]) => ({ text, value }))

function getData(fields) {
    return fields.toJS()
}

function OrganizationCategoryForm({
    onClose,
    category,
    organizationId,
    onSubmitSuccess,
}) {
    const isEditMode = !!category

    const {
        fields,
        errors,
        validate,
        isChanged,
        changeField: onChangeField,
        changeFields: onChangeFields,
    } = useForm('OrganizationCategory', OrganizationCategory, OrganizationCategoryFormValidator)

    const { mutateAsync: save, error, isLoading: isSubmitting, reset } = useOrganizationCategoryMutation({
        onSuccess: onSubmitSuccess
    })

    const [validateNameWithinOrganization, nameError] = useUniqCategoryWithinOrganization({
        organizationId,
        name: fields.name,
        categoryId: fields.id,
    })

    const [isFetching, setIsFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const validationOptions = useMemo(() => ({
        included: {}
    }), [])

    const { Scrollable, scroll } = useScrollable()

    const onScroll = useScrollToFormError('.IncidentReportForm', scroll)

    function cancel() {
        onClose(isChanged)
    }

    function submit(e) {
        e.preventDefault()

        setIsFetching(true)

        validate(validationOptions)
            .then(validateAsync)
            .then(async () => {
                save(getData(fields))
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

    const validateAsync = useCallback(() => {
        let isSameField = fields.name === category?.name

        return isEmpty(fields.name) || isSameField
            ? Promise.resolve()
            : validateNameWithinOrganization()
    }, [category, fields.name, validateNameWithinOrganization])

    function validateIf() {
        if (needValidation) {
            validate(validationOptions)
                .then(validateAsync)
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function setDefaultFormData() {
        onChangeFields({ organizationId }, true)
    }

    function setDetails() {
        if (category) {
            onChangeFields(category, true)
        }
    }

    useEffect(setDefaultFormData, [organizationId, onChangeFields])
    useEffect(setDetails, [category, onChangeFields])
    useEffect(validateIf, [needValidation, onScroll, validate, validationOptions])

    return (
        <Form className="OrganizationCategoryForm" onSubmit={submit}>
            <Scrollable style={scrollableStyles}>
                {(isFetching || isSubmitting) && (
                    <Loader
                        hasBackdrop
                        style={{ position: 'fixed' }}
                    />
                )}

                <div className="OrganizationCategoryForm-Section">
                    <Row>
                        <Col md="8">
                            <TextField
                                type="text"
                                name="name"
                                label="Category name*"
                                value={fields.name}
                                maxLength={50}
                                className="OrganizationCategoryForm-TextField"
                                errorText={errors.name || nameError.message}
                                onChange={onChangeField}
                            />
                        </Col>

                        <Col md="4">
                            <ColorSelectField
                                name="color"
                                value={fields.color?.toLowerCase()}
                                label="Color*"
                                placeholder="Select color"
                                options={COLOR_SELECT_OPTIONS}
                                className="OrganizationCategoryForm-ColorSelectField"
                                errorText={errors.color}
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="OrganizationCategoryForm-Buttons">
                <Button
                    outline
                    color="success"
                    onClick={cancel}
                >
                    Cancel
                </Button>

                <Button
                    color="success"
                    disabled={isFetching}
                >
                    {isEditMode ? 'Save' : 'Create'}
                </Button>
            </div>

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={reset}
                />
            )}
        </Form>
    )
}

export default memo(OrganizationCategoryForm)
