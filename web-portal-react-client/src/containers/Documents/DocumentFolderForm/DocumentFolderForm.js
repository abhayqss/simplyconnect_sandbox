import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    Col,
    Row,
    Form,
    Button,
    UncontrolledTooltip as Tooltip,
} from 'reactstrap'

import { useDebouncedCallback } from 'use-debounce'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    ConfirmDialog
} from 'components/dialogs'

import {
    TextField,
    SwitchField,
    SelectField,
} from 'components/Form'

import {
    ContactPermissionList,
    ContactPermissionPicker
} from 'containers/Documents/Contacts'

import {
    useForm,
    useToggle,
    useScrollable,
    useSelectOptions,
    useQueryInvalidation,
    useScrollToFormError
} from 'hooks/common'

import {
    useDocumentFolderSubmit,
    useDocumentFolderQuery,
    useDefaultDocumentFolderQuery,
    useUniqFolderNameValidation
} from 'hooks/business/documents'

import {
    useDocumentCategoriesQuery
} from 'hooks/business/directory/query'

import DocumentFolder from 'entities/DocumentFolder'
import DocumentFolderFormValidator from 'validators/DocumentFolderFormValidator'

import {
    defer,
    isNotEmpty
} from 'lib/utils/Utils'

import './DocumentFolderForm.scss'

const scrollableStyles = { flex: 1 }

function getData(fields) {
    const data = fields.toJS()

    return {
        ...data,
        name: data.name.trim(),
        permissions: data.isSecurityEnabled
            ? data.permissions.data
            : null
    }
}

function mapCategories(categories) {
    return categories?.map(o => o.id)
}

function DocumentFolderForm(
    {
        folderId,
        communityId,
        parentFolderId,
        organizationId,
        canEditSecurity,
        isSecurityEnabled,

        onCancel,
        onSubmitSuccess
    }
) {
    const isEditing = Boolean(folderId)

    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const [selectedContact, setSelectedContact] = useState(null)

    const [areDefaultPermissionsEnabled, setAreDefaultPermissionsEnabled] = useState(!isEditing)

    const [isContactPickerOpen, setIsContactPickerOpen] = useState(false)
    const [isDeleteContactConfirmDialogOpen, toggleDeleteContactConfirmDialogOpen] = useToggle()

    const [maxPermissionCount, setMaxPermissionCount] = useState(0)

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged,
        changeField,
        changeFields
    } = useForm(
        'DocumentFolder',
        DocumentFolder,
        DocumentFolderFormValidator
    )

    const permissionCount = (
        fields.permissions.data.size
    )

    const canAddContact = !(
        isSecurityEnabled
        && permissionCount >= maxPermissionCount
    )

    const {
        data: folder,
        isFetching: isFetchingFolder
    } = useDocumentFolderQuery({ folderId }, {
        staleTime: 0,
        enabled: isEditing && isNotEmpty(folderId)
    })

    const {
        data: defaultData,
        isFetching: isFetchingDefaultData
    } = useDefaultDocumentFolderQuery({
        folderId,
        communityId,
        parentFolderId,
        isSecurityEnabled: (
            isSecurityEnabled || fields.isSecurityEnabled
        )
    }, { staleTime: 0 })

    const {
        data: categories = []
    } = useDocumentCategoriesQuery(
        { organizationId },
        {
            enabled: Boolean(organizationId),
            staleTime: 0
        }
    )

    const invalidateQuery = useQueryInvalidation()

    const categoryIds = useMemo(() => {
        return fields.categoryIds.toJS()
    }, [fields.categoryIds])

    const permissions = useMemo(() => {
        return fields.permissions.getData()
    }, [fields.permissions])

    const identifiedColors = useMemo(() => {
        return categories.reduce((map, value) => {
            map[value.id] = value.color
            return map
        }, {})
    }, [categories])

    const identifiedColorNames = useMemo(() => {
        return categories.reduce((map, value) => {
            map[value.id] = value.name
            return map
        }, {})
    }, [categories])

    const categoryOptions = useSelectOptions(
        categories, { textProp: 'name' }
    )

    const validationOptions = useMemo(() => ({
        included: {

        }
    }), [])

    const [validateUniqName, nameError] = useUniqFolderNameValidation({
        communityId,
        id: fields.id,
        name: fields.name,
        parentId: parentFolderId,
    })

    const isValidForm = isValid && !nameError.message

    const { Scrollable, scroll } = useScrollable()

    const onScroll = useScrollToFormError('.DocumentFolderForm', scroll)

    function validateFolderName() {
        let detail = folder?.data?.name
        let shouldValidate = (!nameError.message || !(isEditing && fields.name === detail)) && fields.name.trim()

        return shouldValidate ? validateUniqName().catch(console.log) : Promise.resolve()
    }

    const onValidateFolderName = useDebouncedCallback(validateFolderName, 300)

    const { mutateAsync: submit } = useDocumentFolderSubmit({
        onError: setError,
        onSuccess: ({ data }) => {
            onSubmitSuccess(data)
            invalidateQuery('DocumentFolder', {
                folderId
            })
            invalidateQuery('DocumentFolders', {
                communityId,
                canUpload: true,
            })
        }
    })

    function setCommunity() {
        changeField('communityId', communityId, true)
    }

    function setParentFolder() {
        if (!isEditing) {
            changeField('parentId', parentFolderId, true)
        }
    }

    function init() {
        if (folder) {
            defer().then(() => setAreDefaultPermissionsEnabled(true))

            changeFields({
                ...folder,
                categoryIds: mapCategories(folder.categories),
                permissions: {
                    data: folder.permissions,
                    pagination: { totalCount: folder.permissions?.length }
                }
            }, true)
        }
    }

    function setDefaultPermissions() {
        if (
            areDefaultPermissionsEnabled
            && isNotEmpty(defaultData)
            && permissions.isEmpty()
        ) {
            changeField(
                'permissions.data',
                defaultData.permissions,
                true
            )

            changeField(
                'permissions.pagination.totalCount',
                defaultData.permissions?.length,
                true
            )
        }
    }

    function setInitialCategories() {
        if (
            !isEditing
            && isNotEmpty(defaultData)
            && fields.categoryIds.size === 0
        ) {
            changeField('categoryIds', mapCategories(defaultData.categories), true)
        }
    }

    function setInitialMaxPermissionsCount() {
        if (isSecurityEnabled && defaultData?.permissions?.length) {
            setMaxPermissionCount(defaultData.permissions.length)

            if (!isEditing) {
                changeField('isSecurityEnabled', isSecurityEnabled, true)
            }
        }
    }

    function cancel() {
        onCancel(isChanged)
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function validateNameIf() {
        if (fields.name) {
            onValidateFolderName()
        }
    }

    function tryToSubmit(e) {
        e.preventDefault()

        setFetching(true)

        validate(validationOptions)
            .then(validateUniqName)
            .then(async () => {
                await submit(getData(fields))

                setNeedValidation(false)
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    function onDeleteContact(o) {
        setSelectedContact(o)
        toggleDeleteContactConfirmDialogOpen(true)
    }

    function deleteContact(contact) {
        const data = fields.permissions.data.filter(
            o => o.contactId !== contact.contactId
        )

        changeField('permissions.data', data)
        changeField('permissions.pagination.totalCount', data.size)
    }

    function changeContactPermission(contactId, permissionLevelId) {
        const index = fields.permissions.data.findIndex(o => o.contactId === contactId)

        changeField(`permissions.data.${index}.permissionLevelId`, permissionLevelId)
    }

    function showContactPermissionPicker() {
        setIsContactPickerOpen(true)
    }

    function closeContactPicker() {
        setIsContactPickerOpen(false)
    }

    const addPermissions = useCallback((permissions) => {
        const data = fields.permissions.data.push(...permissions)
        changeField('permissions.data', data)
        changeField('permissions.pagination.totalCount', data.size)
    }, [changeField, fields.permissions.data])

    function refreshPermissions(page) {
        changeField('permissions.pagination.page', page)
    }

    useEffect(validateIf, [needValidation, onScroll, validate])
    useEffect(validateNameIf, [fields.name, onValidateFolderName])

    useEffect(init, [folder, parentFolderId, changeField, changeFields])

    useEffect(setDefaultPermissions, [
        changeField,
        changeFields,
        defaultData,
        permissions,
        parentFolderId,
        areDefaultPermissionsEnabled
    ])

    useEffect(setCommunity, [communityId, changeField])
    useEffect(setParentFolder, [parentFolderId, isEditing, changeField])
    useEffect(setInitialCategories, [isEditing, defaultData, changeField, fields.categoryIds.size])
    useEffect(setInitialMaxPermissionsCount, [defaultData, isEditing, changeField, isSecurityEnabled])

    return (
        <>
            <Form className="DocumentFolderForm" onSubmit={tryToSubmit}>
                {(isFetching || isFetchingFolder) && (
                    <Loader hasBackdrop />
                )}

                <Scrollable style={scrollableStyles} className="DocumentFolderForm-Sections">
                    <div className="DocumentFolderForm-Section">
                        <Row>
                            <Col>
                                <TextField
                                    name="name"
                                    value={fields.name}
                                    label="Folder Name*"
                                    className="DocumentFolderForm-TextField"
                                    errorText={errors.name || nameError.message}
                                    maxLength={256}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>

                        <Row>
                            <Col md={12}>
                                <div id="categoryIds">
                                    <SelectField
                                        name="categoryIds"
                                        value={categoryIds}
                                        isMultiple
                                        hasKeyboardSearch
                                        hasKeyboardSearchText
                                        options={categoryOptions}
                                        label="Folder Category"
                                        className="DocumentFolderForm-SelectField DocumentFolderForm-CategorySelectField"
                                        onChange={changeField}
                                        errorText={errors.categoryIds}
                                        isDisabled={!categoryOptions.length}
                                        renderSelectedText={() => {
                                            return (
                                                <>
                                                    {fields.categoryIds.map((id) => {
                                                        const color = identifiedColors[id]
                                                        const name = identifiedColorNames[id]
                                                        const isNotLast = fields.categoryIds.last() !== id

                                                        return (
                                                            <div key={id} className="DocumentFolderForm-SelectedCategory">
                                                                <div
                                                                    className="DocumentFolderForm-SelectedCategoryIndicator"
                                                                    style={{ backgroundColor: color }}
                                                                />

                                                                <div className="DocumentFolderForm-SelectedCategoryTitle">
                                                                    {`${name}${isNotLast ? ',' : ''}`}
                                                                </div>
                                                            </div>
                                                        )
                                                    })}
                                                </>
                                            )
                                        }}
                                        formatOptionText={({ text, value: id }) => {
                                            const color = identifiedColors[id]

                                            return (
                                                <div className="DocumentFolderForm-SelectFieldOption">
                                                    <span
                                                        style={{ backgroundColor: color }}
                                                        className="DocumentFolderForm-ColorMark"
                                                    />
                                                    <span>{text}</span>
                                                </div>
                                            )
                                        }}
                                    />
                                </div>


                                {!categoryOptions.length && (
                                    <Tooltip
                                        placement="top"
                                        target="categoryIds"
                                        modifiers={[
                                            {
                                                name: 'offset',
                                                options: { offset: [0, 6] }
                                            },
                                            {
                                                name: 'preventOverflow',
                                                options: { boundary: document.body }
                                            }
                                        ]}
                                    >
                                        No categories configured for your organization
                                    </Tooltip>
                                )}
                            </Col>
                        </Row>
                        {canEditSecurity && (
                            <>
                                <Row>
                                    <Col>
                                        <SwitchField
                                            name="isSecurityEnabled"
                                            isDisabled={isSecurityEnabled}
                                            label="Enable security for folder"
                                            isChecked={fields.isSecurityEnabled}
                                            className="DocumentFolderForm-CheckboxField"

                                            onChange={changeField}
                                        />
                                    </Col>
                                </Row>

                                {fields.isSecurityEnabled && (
                                    <>
                                        <Row className="margin-bottom-30">
                                            <Col className="DocumentFolderForm-ActionPanel">
                                                <div id="add-contact-btn">
                                                    <Button
                                                        color="success"
                                                        disabled={!canAddContact}
                                                        onClick={showContactPermissionPicker}
                                                    >
                                                        Add Contact
                                                    </Button>

                                                    {!canAddContact && (
                                                        <Tooltip
                                                            target="add-contact-btn"
                                                            modifiers={[
                                                                {
                                                                    name: 'offset',
                                                                    options: { offset: [0, 6] }
                                                                },
                                                                {
                                                                    name: 'preventOverflow',
                                                                    options: { boundary: document.body }
                                                                }
                                                            ]}
                                                        >
                                                            Child folders inherit security from the parent folder
                                                        </Tooltip>
                                                    )}
                                                </div>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col>
                                                <ContactPermissionList
                                                    hasRemoveButtons
                                                    data={permissions}
                                                    onDelete={onDeleteContact}
                                                    isLoading={isFetchingDefaultData}
                                                    pagination={fields.permissions.pagination}
                                                    onRefresh={refreshPermissions}
                                                    onChangePermission={changeContactPermission}
                                                />
                                            </Col>
                                        </Row>
                                    </>
                                )}
                            </>)}

                    </div>
                </Scrollable>

                <div className="DocumentFolderForm-Buttons">
                    <Button
                        outline
                        color="success"
                        onClick={cancel}
                    >
                        Close
                    </Button>

                    <Button
                        color="success"
                        disabled={isFetching || !isValidForm}
                    >
                        Save
                    </Button>
                </div>
            </Form>

            <ContactPermissionPicker
                isOpen={isContactPickerOpen}
                onClose={closeContactPicker}
                folderId={folderId}
                selectedPermissions={fields.permissions.data}
                communityId={communityId}
                parentFolderId={parentFolderId}
                onSaveSuccess={addPermissions}
            />

            {isDeleteContactConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    title="Access to folder will be removed after you save the changes on this form"
                    confirmBtnText="Remove"
                    onCancel={() => {
                        setSelectedContact(null)
                        toggleDeleteContactConfirmDialogOpen()
                    }}
                    onConfirm={() => {
                        setSelectedContact(null)
                        deleteContact(selectedContact)
                        toggleDeleteContactConfirmDialogOpen()
                    }}
                />
            )}

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

export default memo(DocumentFolderForm)
