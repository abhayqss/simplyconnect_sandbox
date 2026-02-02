import React, {
    memo,
    useRef,
    useMemo,
    useState,
    useEffect,
    useContext,
    useCallback
} from 'react'

import $ from 'jquery'
import cn from 'classnames'

import {
    any,
    noop,
    pick,
    each,
    find,
    chain,
    pluck,
    filter,
    reject,
    groupBy,
    findWhere,
    findIndex
} from 'underscore'

import {
    Form
} from 'reactstrap'

import {
    useSensor,
    useSensors,
    DndContext,
    DragOverlay,
    MouseSensor,
    TouchSensor,
    PointerSensor
} from '@dnd-kit/core'

import {
    useForm,
    useScrollable,
    useMutationWatch,
    useScrollToFormError,
    useQueryInvalidation,
    useCustomFormFieldChange
} from 'hooks/common'

import {
    useESignDocumentTemplateQuery,
    useESignDocumentTemplateSubmit,
    useESignDocumentTemplateDefaultAssignedFoldersQuery
} from 'hooks/business/documents/e-sign/template'

import {
    useCommunitiesQuery,
    useESignDocumentTemplateSignerFieldTypesQuery,
    useESignDocumentTemplateAutofillFieldTypesQuery,
    useESignDocumentTemplateRequesterFieldTypesQuery,
    useESignDocumentTemplateOrgAutofillFieldTypesQuery
} from 'hooks/business/directory/query'

import {
    Loader,
    ErrorViewer
} from 'components'

import {
    Draggable,
    Droppable
} from 'components/dnd'

import {
    TextField,
    SelectField
} from 'components/Form'

import { ConfirmDialog } from 'components/dialogs'

import {
    withAutoSave
 } from 'hocs'

import { ESignDocumentTemplateContext } from 'contexts'

import Entity from 'entities/e-sign/ESignDocumentTemplate'
import Validator from 'validators/TemplateBuilderPreviewFormValidator'

import {
    measure
} from 'lib/utils/DomUtils'

import {
    download
} from 'lib/utils/AjaxUtils'

import {
    map,
    last
} from 'lib/utils/ArrayUtils'

import {
    convertBlobToFile
} from 'lib/utils/FileUtils'

import {
    isEmpty,
    isNumber,
    isInteger,
    isNotEmpty,
    getRandomInt
} from 'lib/utils/Utils'

import { E_SIGN_DOCUMENT_TEMPLATE_STATUSES, FOLDER_TYPES } from 'lib/Constants'

import { ReactComponent as CheckboxIcon } from 'images/templateBuilder/checkbox.svg'
import { ReactComponent as DateIcon } from 'images/templateBuilder/date.svg'
import { ReactComponent as InputIcon } from 'images/templateBuilder/input.svg'
import { ReactComponent as RadioButtonIcon } from 'images/templateBuilder/radio.svg'
import { ReactComponent as DropdownIcon } from 'images/templateBuilder/droplist.svg'
import { ReactComponent as SignatureIcon } from 'images/templateBuilder/signature.svg'
import { ReactComponent as RuleIcon } from 'images/templateBuilder/rules.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'
import { ReactComponent as CheckboxIcon2 } from 'images/checkbox-3.svg'

import { ESignDocTemplatePreview as TemplatePreview } from '../../components'

import {
    ESignDocumentTemplateFolderAssigner,
    ESignDocumentTemplateFieldRuleEditor,
    ESignDocumentTemplateFieldPropertyEditor
} from '..'

import {
    Rule,
    Field,
    Section,
    ESignDocumentTemplateFieldBox
} from './components'

import './ESignDocumentTemplateFieldEditor.scss'

const CSS = {
    fieldSelector: '.ESignDocumentTemplateField',
    fieldBoxSelector: '.ESignDocumentTemplateFieldBox',
    fieldLayoutAreaSelector: '.ESignDocumentTemplateFieldEditor-FieldLayoutArea'
}

const FIELD_BOX_MIN_WIDTH = 100
const FIELD_BOX_MIN_HEIGHT = 20

const FIELD_BOX_MAX_WIDTH = 800
const FIELD_BOX_MAX_HEIGHT = 200

const FIELD_BOX_DEFAULT_WIDTH = 180
const FIELD_BOX_DEFAULT_HEIGHT = 25

const TOP_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT = 21
const BOTTOM_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT = 25

const FILE_PREVIEW_PAGINATOR_HEIGHT = 60

const SECTIONS = {
    TEMPLATE: 'TEMPLATE',
    AUTOFILL_ORGANIZATION: 'AUTOFILL_ORGANIZATION',
    AUTOFILL: 'AUTOFILL',
    TOOLBOX_SIGNER: 'TOOLBOX_SIGNER',
    TOOLBOX_REQUESTER: 'TOOLBOX_REQUESTER',
    RULES: 'RULES'
}

const TEMPLATE_TYPE = {
    COMMUNITY: "COMMUNITY",
    ORGANIZATION: "ORGANIZATION",
}

const {
    COMMUNITY,
    ORGANIZATION
} = TEMPLATE_TYPE

const {
    RULES,
    TEMPLATE,
    AUTOFILL,
    TOOLBOX_SIGNER,
    TOOLBOX_REQUESTER,
    AUTOFILL_ORGANIZATION
} = SECTIONS

const SECTION_INDICATOR_COLORS = {
    [AUTOFILL_ORGANIZATION]: 'rgba(36, 147, 229, 0.5)',
    [AUTOFILL]: '#fdd835',
    [TOOLBOX_REQUESTER]: '#89d6a9',
    [TOOLBOX_SIGNER]: 'rgba(243, 50, 50, 0.5)'
}

const SECTION_INDICATOR_COLORS_NAMES = {
    [AUTOFILL_ORGANIZATION]: 'blue',
    [AUTOFILL]: 'yellow',
    [TOOLBOX_REQUESTER]: 'green',
    [TOOLBOX_SIGNER]: 'red'
}

const FIELD_CONFIG = {
    [TOOLBOX_REQUESTER]: {
        SIGNATURE: {
            hasValue: false,
            icon: SignatureIcon
        },
        INPUT_BOX: {
            hasValue: false,
            icon: InputIcon
        },
        CHECKBOX: {
            hasValue: true,
            icon: CheckboxIcon
        },
        DATE_BOX: {
            icon: DateIcon,
            hasValue: false
        },
        DROPDOWN: {
            hasValue: true,
            icon: DropdownIcon
        },
        RADIOBUTTON: {
            hasValue: true,
            icon: RadioButtonIcon
        }

    },
    [TOOLBOX_SIGNER]: {
        TEXT: {
            icon: InputIcon
        },
        CHECKBOX: {
            icon: CheckboxIcon
        },
        SIGNATURE_DATE: {
            icon: DateIcon,
            hasValue: false
        },
        SIGNATURE: {
            icon: SignatureIcon
        }
    }
}

const {
    DRAFT,
    COMPLETED
} = E_SIGN_DOCUMENT_TEMPLATE_STATUSES

const typeOptions = [
    { value: COMMUNITY, text: 'Community' },
    { value: ORGANIZATION, text: 'Organization' }
]

const FIELD_PROPERTY_ERROR = {
    CODE: 'no.field.property',
    MESSAGE: 'Please specify field property'
}

function valueTextMapper({ id, name, label }) {
    return { value: id, text: label || name }
}

function getTemplateData(
    {
        formFields,
        fieldRules,
        assignedFolders,
        positionedFields,
        status = COMPLETED
    },
    isAutoSave = false
) {
    const {
        type,
        name: title,
        communityIds
    } = formFields.toJS()

    const configuration = {}

    const groupedFields = groupBy(
        positionedFields, o => o.section
    )

    each(groupedFields, (fields, name) => {
        const mappedFields = map(fields, o => {

            const field = {
                typeId: o.id,
                isEditable: o.isEditable,
                title: o.title ?? '',
                location: {
                    pageNo: o.position.pageNo,
                    ...o.position.relativeCoords
                }
            }

            if (isRequesterField(o) && o?.property?.label) {
                field.properties = {
                    label: o.property.label

                }
                if (o.hasValue) {
                    field.properties.values = o.property.values
                }
            }

            return field
        })

        if (name === SECTIONS.AUTOFILL) {
            configuration.autoFillFields = mappedFields
        }

        if (name === SECTIONS.AUTOFILL_ORGANIZATION) {
            configuration.organizationAutoFillFields = mappedFields
        }

        if (name === SECTIONS.TOOLBOX_REQUESTER) {
            configuration.toolboxRequesterFields = mappedFields
        }

        if (name === SECTIONS.TOOLBOX_SIGNER) {
            configuration.toolboxSignerFields = mappedFields
        }
    })

    configuration.rules = map(fieldRules,
        ({
            field: { title: dependentFieldTitle },
            signature: { title: fieldTitle }
        }) => ({ fieldTitle, dependentFieldTitle })
    )

    configuration.folders = assignedFolders

    const data = {
        title,
        configuration,
        statusName: status,
        isAutoSave
    }

    if (type === COMMUNITY) {
        data.communityIds = communityIds
    }

    return data
}

function isRequesterField(field) {
    return field.section === SECTIONS.TOOLBOX_REQUESTER
}

function isSignerField(field) {
    return field.section === SECTIONS.TOOLBOX_SIGNER
}

function areFieldEquals(f1, f2) {
    return f1.section === f2.section && (
        (
            f1.name && f2.name && f1.name === f2.name
            && ((f1.uuid || f2.uuid) ? f1.uuid === f2.uuid : true)
        )
    )
}

function checkAABBCollision(rect1, rect2) {
    return (
        rect1.x < rect2.x + rect2.width
        && rect1.x + rect1.width > rect2.x
        && rect1.y < rect2.y + rect2.height
        && rect1.height + rect1.y > rect2.y
    )
}

function doesFieldRuleContainField(rule, field) {
    return [rule.field.uuid, rule.signature.uuid].includes(field.uuid)
}

function fieldMapper(
    {
        field,
        config,
        section,
        layoutAreaMeasure: { width: layoutAreaWidth, height: layoutAreaHeight }
    }
) {
    const {
        id,
        typeId,
        isEditable,
        title,
        typeCode,
        typeTitle,
        isResizable,
        location: {
            pageNo,
            topLeftY,
            topLeftX,
            bottomRightY,
            bottomRightX
        },
        properties
    } = field

    return {
        uuid: id,
        id: typeId,
        isEditable,
        isResizable,
        title: title ?? typeTitle,
        section,
        name: typeCode,
        ...config ? config[typeCode] : null,
        ...properties && { property: properties },
        position: {
            pageNo,
            coords: {
                topLeftX: topLeftX * layoutAreaWidth,
                topLeftY: topLeftY * layoutAreaHeight,
                bottomRightX: bottomRightX * layoutAreaWidth,
                bottomRightY: bottomRightY * layoutAreaHeight
            },
            relativeCoords: {
                topLeftY,
                topLeftX,
                bottomRightY,
                bottomRightX
            }
        },
        size: {
            width: (bottomRightX - topLeftX) * layoutAreaWidth,
            height: (bottomRightY - topLeftY) * layoutAreaHeight
        }
    }
}

function ESignDocumentTemplateFieldEditor(
    {
        isCopying,
        documentId,
        templateId,
        communityId,

        autoSaveAdapter,

        children,

        onBack,
        onCancel,
        onChanged,
        onSubmitSuccess,

        ...props
    }
) {
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)
    const [isAutoSaving, setAutoSaving] = useState(false)
    const [needValidation, setNeedValidation] = useState(false)

    const [file, setFile] = useState(null)
    const [filePageNo, setFilePageNo] = useState(0)

    const [filePreviewScale, setFilePreviewScale] = useState(1)
    const [isFilePreviewRenderSuccess, setFilePreviewRenderSuccess] = useState(false)

    const [selectedField, setSelectedField] = useState(null)
    const [isDeleteFieldConfirmDialogOpen, toggleDeleteFieldConfirmDialog] = useState(false)

    const [areFieldsChanged, setAreFieldsChanged] = useState(false)

    const [draggableField, setDraggableField] = useState(null)
    const [isFieldResizing, setFieldResizing] = useState(false)
    const [positionedFields, setPositionedFields] = useState([])

    const [fieldLayoutAreaWidth, setFieldLayoutAreaWidth] = useState(0)
    const [fieldLayoutAreaHeight, setFieldLayoutAreaHeight] = useState(0)

    const [isFieldPropertyEditorOpen, setFieldPropertyEditorOpen] = useState(false)

    const [fieldRules, setFieldRules] = useState([])
    const [selectedFieldRule, setSelectedFieldRule] = useState(null)
    const [isFieldRuleEditorOpen, toggleFieldRuleEditor] = useState(false)
    const [isDeleteFieldRuleConfirmDialogOpen, toggleDeleteFieldRuleConfirmDialog] = useState(false)

    const [assignedFolders, setAssignedFolders] = useState([])
    const [isFolderAssignerOpen, toggleFolderAssigner] = useState(false)

    const fieldLayoutSectionRef = useRef()
    const filePreviewRef = useRef()

    const isEditing = !isEmpty(templateId)

    const mouseSensor = useSensor(MouseSensor, {
        activationConstraint: {
            distance: 3
        }
    })

    const touchSensor = useSensor(TouchSensor, {
        activationConstraint: {
            delay: 250, tolerance: 5
        }
    })

    const pointerSensor = useSensor(PointerSensor, {
        activationConstraint: {
            distance: 3
        }
    })

    const sensors = useSensors(
        mouseSensor,
        touchSensor,
        pointerSensor
    )

    const {
        fields,
        errors,
        isValid,
        validate,
        isChanged: isFormChanged,
        changeField,
        changeFields
    } = useForm('ESignDocumentTemplateFieldEditor', Entity, Validator)

    const data = useMemo(() => fields.toJS(), [fields])

    const { communityIds } = data

    const {
        changeSelectField
    } = useCustomFormFieldChange(changeField)

    const {
        templateData
    } = useContext(ESignDocumentTemplateContext)

    const organizationId = isCopying ? templateData?.organizationId : props.organizationId

    const { Scrollable, scroll } = useScrollable()

    // const {
    //     isFetching: isDocumentFetching,
    //     data: document,
    // } = useDocumentQuery({ documentId }, {
    //     cacheTime: 0,
    //     staleTime: 0,
    //     onError: setError,
    //     enabled: Boolean(documentId)
    // })

    const invalidate = useQueryInvalidation()

    const {
        data: template,
        isFetching: isTemplateFetching
    } = useESignDocumentTemplateQuery(
        { templateId },
        {
            onError: setError,
            enabled: Boolean(templateId)
        }
    )

    const {
        mutateAsync: submit,
    } = useESignDocumentTemplateSubmit({
        organizationId,
        ...isCopying ? { id: null, template: file } : {
            id: templateId, template: templateData?.file,
        },
    }, {
        throwOnError: true,
        onError: setError,
        onSuccess: (...args) => {
            if (!isAutoSaving) {
                invalidate('ESignDocumentTemplate', {
                    templateId
                })
                onSubmitSuccess(args)
            }
        }
    })

    const hasFieldErrors = any(positionedFields, o => !!o.error)

    const isChanged = useMemo(() => isFormChanged || areFieldsChanged,
        [isFormChanged, areFieldsChanged])

    const requesterPositionedFields = useMemo(() => filter(
        positionedFields,
        o => o.section === SECTIONS.TOOLBOX_REQUESTER
    ), [positionedFields])

    const requesterPositionedFieldCount = requesterPositionedFields.length

    const signerPositionedFields = useMemo(() => filter(
        positionedFields,
        o => o.section === SECTIONS.TOOLBOX_SIGNER
    ), [positionedFields])

    const signerPositionedFieldCount = signerPositionedFields.length

    const visiblePositionedFields = useMemo(() => filter(
        positionedFields,
        o => o.position.pageNo === filePageNo
    ), [filePageNo, positionedFields])

    const filePreviewData = useMemo(() => {
        if (file) return URL.createObjectURL(file)
        return templateId ? '' : URL.createObjectURL(templateData?.file)
    }, [file, templateId, templateData])

    const {
        data: communities
    } = useCommunitiesQuery(
        { organizationId },
        { enabled: isNumber(organizationId) }
    )

    const {
        data: autofillFieldTypes = [],
        isFetching: isFetchingAutofillFieldTypes
    } = useESignDocumentTemplateAutofillFieldTypesQuery()

    const {
        data: autofillOrgFieldTypes = [],
        isFetching: isFetchingAutofillOrganizationFieldTypes
    } = useESignDocumentTemplateOrgAutofillFieldTypesQuery()

    const {
        data: requesterFieldTypes = [],
        isFetching: isFetchingRequesterFieldTypes
    } = useESignDocumentTemplateRequesterFieldTypesQuery()

    const mappedRequesterFieldTypes = useMemo(() => {
        return requesterFieldTypes.map(o => (
            {
                ...o,
                ...(FIELD_CONFIG[TOOLBOX_REQUESTER][o.name])
            }
        ))
    }, [requesterFieldTypes])

    const {
        data: signerFieldTypes = [],
        isFetching: isFetchingSignerFieldTypes
    } = useESignDocumentTemplateSignerFieldTypesQuery()


    const {
        data: defaultAssignedFolders = [],
    } = useESignDocumentTemplateDefaultAssignedFoldersQuery({
        organizationId,
        types: [FOLDER_TYPES.TEMPLATE]
    }, { enabled: isInteger(organizationId) })

    const mappedDefaultAssignedFolders = useMemo(() => map(defaultAssignedFolders,
        ({ id, communityId }) => ({ folderId: id, communityId })),
        [defaultAssignedFolders])

    const assignedFoldersOfCommunities = useMemo(
        () => filter(
            mappedDefaultAssignedFolders,
            o => communityIds.includes(o.communityId)
        ), [communityIds, mappedDefaultAssignedFolders]
    )

    const assignedFoldersOfAllCommunities = useMemo(
        () => filter(
            mappedDefaultAssignedFolders,
            o => pluck(communities, 'id').includes(o.communityId)
        ), [communities, mappedDefaultAssignedFolders]
    )

    const mappedSignerFieldTypes = useMemo(() => {
        return signerFieldTypes.map(o => (
            {
                ...o,
                icon: FIELD_CONFIG[TOOLBOX_SIGNER][o.name]?.icon
            }
        ))
    }, [signerFieldTypes])

    const mappedFieldRules = useMemo(() => {
        return map(fieldRules, ({
            id,
            field: { title: fieldTitle },
            signature: { title: signatureTitle }
        }) => ({
            id,
            field: fieldTitle,
            signature: signatureTitle
        }))
    }, [fieldRules])

    const isDataFetching = useMemo(() => (
        isFetching
        || isTemplateFetching
        || isFetchingAutofillFieldTypes
        || isFetchingSignerFieldTypes
        || isFetchingRequesterFieldTypes
        || isFetchingAutofillOrganizationFieldTypes
    ),
        [
            isFetching,
            isTemplateFetching,
            isFetchingAutofillFieldTypes,
            isFetchingSignerFieldTypes,
            isFetchingRequesterFieldTypes,
            isFetchingAutofillOrganizationFieldTypes
        ]
    )

    const mappedCommunities = useMemo(
        () => map(communities, valueTextMapper),
        [communities]
    )

    const positionedSignerFieldsWithoutSignature = useMemo(() => {
        return pluck(
            filter(signerPositionedFields, ({ name }) => (
                name !== 'SIGNATURE'
            )), 'title')

    }, [signerPositionedFields])

    const positionedSignatures = useMemo(() => {
        return pluck(
            filter(signerPositionedFields, ({ name }) => (
                name === 'SIGNATURE'
            )), 'title')
    }, [signerPositionedFields])

    const selectedCommunityOptions = useMemo(() => {
        return fields.type === ORGANIZATION
            ? mappedCommunities
            : filter(mappedCommunities, ({ value }) => communityIds.includes(value))
    }, [communityIds, fields.type, mappedCommunities])

    const canAddFieldRule = useMemo(() => positionedSignatures.length < 2,
        [positionedSignatures.length]
    )

    const checkPositionedFieldCollision = useCallback((rect, options = {}) => {
        const fields = options?.excludedFields ? (
            reject(visiblePositionedFields, o => any(
                options?.excludedFields ?? [],
                f => areFieldEquals(f, o)
            ))
        ) : visiblePositionedFields

        return any(fields, o => checkAABBCollision(
            rect, {
            x: o.position.coords.topLeftX,
            y: o.position.coords.topLeftY,
            width: o.size.width,
            height: o.size.height
        }
        ))
    }, [visiblePositionedFields])

    const indexPositionedFieldTitles = useCallback((criteria = noop) => {
        setPositionedFields(prev => map(prev, o => {
            if (criteria(o)) {
                const fields = filter(
                    filter(prev, criteria), f => (
                        f.name === o.name
                    )
                )

                const index = findIndex(
                    fields, f => areFieldEquals(f, o)
                )

                return {
                    ...o, title: (
                        o.title.replace(/\d+/, '').trim() + (
                            fields.length === 1 ? '' : ` ${index + 1}`
                        )
                    )
                }
            }

            return o
        }))
    }, [])

    const onRenderFilePreviewSuccess = useCallback(o => {
        setFetching(false)
        setFilePreviewRenderSuccess(true)
        setFilePageNo(o.pages[0].pageNumber - 1)
        setFieldLayoutAreaWidth(o.width)
        setFieldLayoutAreaHeight(o.height)
        setFilePreviewScale(o.height / o.originalHeight)
    }, [])

    const validationOptions = {
        included: {}
    }

    const scrollToError = useScrollToFormError(
        '.ESignDocumentTemplateFieldEditor-FieldSelectSection', scroll
    )

    const selectedFieldType = useMemo(
        () => selectedField?.title?.replace(/\d+/, '').trim(),
        [selectedField]
    )

    function setDefaultData() {
        if (!isEditing) {
            changeField('type', ORGANIZATION, true)
        }
    }

    function init() {
        if (isEditing && template) {
            const {
                title,
                communityIds,
                configuration,
                organizationId
            } = template

            const preparedFieldRules = map(configuration?.rules,
                ({ dependentFieldTitle, fieldTitle, dependentFieldId, fieldId }) => (
                    {
                        id: Math.random(),
                        field: { title: dependentFieldTitle, uuid: dependentFieldId },
                        signature: { title: fieldTitle, uuid: fieldId }
                    })
            )

            setFieldRules(preparedFieldRules)
            setAssignedFolders(configuration?.folders ?? [])

            const data = {
                name: title,
                organizationId,
                communityIds: communityIds ?? [],
                type: isEmpty(communityIds) ? ORGANIZATION : COMMUNITY,
            }

            if (isCopying) {
                data.type = ''
                data.name = `COPY ${title}`
            }

            changeFields(data, true)
        }
    }

    function initPositionedFieldsIfReady() {
        if (template && isFilePreviewRenderSuccess) {
            const {
                autoFillFields,
                toolboxSignerFields,
                toolboxRequesterFields,
                organizationAutoFillFields
            } = template.configuration

            const sectionedFields = {
                AUTOFILL: autoFillFields,
                TOOLBOX_REQUESTER: toolboxRequesterFields,
                TOOLBOX_SIGNER: toolboxSignerFields,
                AUTOFILL_ORGANIZATION: organizationAutoFillFields
            }

            const preparedFields = chain(sectionedFields)
                .mapObject((rawFields, section) => map(rawFields,
                    (o) => fieldMapper({
                        section,
                        field: o,
                        config: FIELD_CONFIG[section],
                        layoutAreaMeasure: {
                            width: fieldLayoutAreaWidth,
                            height: fieldLayoutAreaHeight
                        }
                    })))
                .reduce((acc, o) => acc.concat(o))
                .value()

            setPositionedFields(preparedFields)
        }
    }

    function validateIf() {
        if (needValidation) {
            validate()
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function tryToSubmit(e, status) {
        e.preventDefault()
        setFetching(true)
        validate(validationOptions)
            .then(async () => {
                setNeedValidation(false)

                try {
                    await submit(getTemplateData({
                        status,
                        assignedFolders,
                        positionedFields,
                        formFields: fields,
                        fieldRules: fieldRules
                    }))
                } catch (e) {
                    setError(e)
                }
            })
            .catch(() => {
                scrollToError()
                setNeedValidation(true)
            })
            .finally(() => {
                setFetching(false)
            })
    }

    function back() {
        onBack(isChanged)
    }

    function cancel() {
        onCancel()
    }

    // const changeTypeField = useCallback((name, value) => {
    //     const newValues = {
    //         type: value
    //     }
    //     if(value === ORGANIZATION) {
    //         newValues.communityIds = [];
    //     }

    //     changeFields(newValues)
    // })

    const onChangeTemplateType = useCallback((name, value) => {
        changeSelectField(name, value)

        setAssignedFolders(
            value === COMMUNITY
                ? assignedFoldersOfCommunities
                : assignedFoldersOfAllCommunities
        )
    }, [
        changeSelectField,
        assignedFoldersOfCommunities,
        assignedFoldersOfAllCommunities
    ])

    const onChangeCommunityField = useCallback((name, value) => {
        changeSelectField(name, value)

        setAssignedFolders(filter(
            mappedDefaultAssignedFolders,
            o => value.includes(o.communityId)
        ))
    }, [
        changeSelectField,
        mappedDefaultAssignedFolders
    ])

    const onFieldDragStart = useCallback(e => {
        if (!isFieldResizing) {
            setDraggableField(e.active.data.current)
        }
    }, [isFieldResizing])

    const onFieldDragEnd = useCallback(e => {
        setDraggableField(null)

        if (e.over && !isFieldResizing) {
            const field = e.active.data.current

            const dragNode = (
                $(e.activatorEvent.target).closest(
                    field.position ? CSS.fieldBoxSelector : CSS.fieldSelector
                ).get(0)
            )

            const { x: clientX, y: clientY } = measure(dragNode)

            const dropArea = findWhere(
                e.collisions, { id: e.over.id }
            )

            const dropAreaNode = (
                dropArea?.data.droppableContainer.node.current
            )

            const dropAreaLeft = e.over.rect.left
            const dropAreaTop = e.over.rect.top
            const dropAreaScrollTop = dropAreaNode?.scrollTop ?? 0
            const dropAreaScrollLeft = dropAreaNode?.scrollLeft ?? 0
            const dropAreaMarginTop = TOP_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT * filePreviewScale
            const dropAreaMarginBottom = BOTTOM_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT * filePreviewScale

            if (field.position) {
                let topLeftX = clientX - dropAreaLeft + dropAreaScrollLeft
                let topLeftY = clientY - dropAreaTop + dropAreaScrollTop

                const canDrop = !(
                    checkPositionedFieldCollision({
                        x: topLeftX, y: topLeftY, ...field.size
                    }, { excludedFields: [field] })
                )

                if (canDrop) {
                    //correction if negative overflow
                    topLeftX = Math.max(0, topLeftX)
                    topLeftY = Math.max(dropAreaMarginTop, topLeftY)

                    let bottomRightX = topLeftX + field.size.width
                    let bottomRightY = topLeftY + field.size.height

                    //correction if positive overflow by x
                    if (bottomRightX > fieldLayoutAreaWidth) {
                        topLeftX -= bottomRightX - fieldLayoutAreaWidth
                        bottomRightX = topLeftX + field.size.width
                    }

                    //correction if positive overflow by y
                    if (bottomRightY > (fieldLayoutAreaHeight - dropAreaMarginBottom)) {
                        topLeftY -= bottomRightY - fieldLayoutAreaHeight + dropAreaMarginBottom
                        bottomRightY = topLeftY + field.size.height
                    }

                    setAreFieldsChanged(true)

                    setPositionedFields(
                        map(positionedFields, o => (
                            o.uuid !== field.uuid ? o : {
                                ...field, position: {
                                    ...field.position,
                                    coords: {
                                        topLeftX,
                                        topLeftY,
                                        bottomRightX,
                                        bottomRightY
                                    },
                                    relativeCoords: {
                                        topLeftX: topLeftX / fieldLayoutAreaWidth,
                                        topLeftY: topLeftY / fieldLayoutAreaHeight,
                                        bottomRightX: bottomRightX / fieldLayoutAreaWidth,
                                        bottomRightY: bottomRightY / fieldLayoutAreaHeight
                                    }
                                }
                            }
                        ))
                    )
                }
            } else {
                const scrollableParentNode = (
                    $(dragNode).closest('.Scrollable').get(0)
                )

                const parentScrollTop = scrollableParentNode?.scrollTop ?? 0
                const parentScrollLeft = scrollableParentNode?.scrollLeft ?? 0

                let topLeftX = (
                    (clientX + parentScrollLeft) + e.delta.x
                    - dropAreaLeft + dropAreaScrollLeft
                )

                let topLeftY = (
                    (clientY + parentScrollTop) + e.delta.y
                    - dropAreaTop + dropAreaScrollTop
                )

                const width = field?.size?.width || FIELD_BOX_DEFAULT_WIDTH
                const height = field?.size?.height || FIELD_BOX_DEFAULT_HEIGHT

                const canDrop = !(
                    checkPositionedFieldCollision({
                        x: topLeftX, y: topLeftY, width, height
                    })
                )

                if (canDrop) {
                    //correction if negative overflow
                    topLeftX = Math.max(0, topLeftX)
                    topLeftY = Math.max(dropAreaMarginTop, topLeftY)

                    let bottomRightX = topLeftX + width
                    let bottomRightY = topLeftY + height

                    //correction if positive overflow by x
                    if (bottomRightX > fieldLayoutAreaWidth) {
                        topLeftX -= bottomRightX - fieldLayoutAreaWidth
                        bottomRightX = topLeftX + width
                    }

                    //correction if positive overflow by y
                    if (bottomRightY > (fieldLayoutAreaHeight - dropAreaMarginBottom)) {
                        topLeftY -= bottomRightY - fieldLayoutAreaHeight + dropAreaMarginBottom
                        bottomRightY = topLeftY + height
                    }

                    setAreFieldsChanged(true)

                    setPositionedFields([...positionedFields, {
                        ...field,
                        uuid: getRandomInt(0, 999999),
                        ...field.isResizable !== false && {
                            size: {
                                width: FIELD_BOX_DEFAULT_WIDTH,
                                height: FIELD_BOX_DEFAULT_HEIGHT
                            }
                        },
                        position: {
                            pageNo: filePageNo,
                            coords: {
                                topLeftX,
                                topLeftY,
                                bottomRightX,
                                bottomRightY
                            },
                            relativeCoords: {
                                topLeftX: topLeftX / fieldLayoutAreaWidth,
                                topLeftY: topLeftY / fieldLayoutAreaHeight,
                                bottomRightX: bottomRightX / fieldLayoutAreaWidth,
                                bottomRightY: bottomRightY / fieldLayoutAreaHeight
                            }
                        }
                    }])
                }
            }
        }
    }, [
        filePageNo,
        isFieldResizing,
        filePreviewScale,
        positionedFields,
        fieldLayoutAreaWidth,
        fieldLayoutAreaHeight,
        checkPositionedFieldCollision
    ])

    const onFieldResizeStart = useCallback(() => {
        setFieldResizing(true)
    }, [])

    const onFieldResize = useCallback((e, field, params) => {
        const { size, handle: direction } = params

        const coords = { ...field.position.coords }

        const layoutAreaNode = $(
            CSS.fieldLayoutAreaSelector
        ).get(0)

        const {
            x: layoutAreaLeft,
            y: layoutAreaTop
        } = measure(layoutAreaNode)

        if (['n', 'w', 'nw', 'ne'].includes(direction)) {
            const layoutAreaScrollTop = layoutAreaNode?.scrollTop ?? 0
            const layoutAreaScrollLeft = layoutAreaNode?.scrollLeft ?? 0

            if (!['n', 'ne'].includes(direction)) {
                coords.topLeftX = e.x - layoutAreaLeft + layoutAreaScrollLeft
            }

            if (direction !== 'w') {
                coords.topLeftY = e.y - layoutAreaTop + layoutAreaScrollTop
            }
        }

        const layoutAreaMarginTop = TOP_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT * filePreviewScale
        const layoutAreaMarginBottom = BOTTOM_PDC_FLOW_REWRITABLE_FILE_SEGMENT_HEIGHT_PT * filePreviewScale

        //correction if negative overflow
        coords.topLeftX = Math.max(0, coords.topLeftX)
        coords.topLeftY = Math.max(layoutAreaMarginTop, coords.topLeftY)

        const canResize = !(
            checkPositionedFieldCollision({
                x: coords.topLeftX, y: coords.topLeftY, ...size
            }, { excludedFields: [field] })
        )

        if (canResize) {
            let bottomRightX = coords.topLeftX + size.width
            let bottomRightY = coords.topLeftY + size.height

            //correction if positive overflow by x
            if (bottomRightX > fieldLayoutAreaWidth) {
                coords.topLeftX -= bottomRightX - fieldLayoutAreaWidth
            }

            //correction if positive overflow by y
            if (bottomRightY > (fieldLayoutAreaHeight - layoutAreaMarginBottom)) {
                coords.topLeftY -= bottomRightY - fieldLayoutAreaHeight + layoutAreaMarginBottom
            }

            coords.bottomRightX = coords.topLeftX + size.width
            coords.bottomRightY = coords.topLeftY + size.height

            setPositionedFields(map(positionedFields, o => (
                !areFieldEquals(field, o) ? o : {
                    ...o, size, position: {
                        ...o.position,
                        coords,
                        relativeCoords: {
                            topLeftX: coords.topLeftX / fieldLayoutAreaWidth,
                            topLeftY: coords.topLeftY / fieldLayoutAreaHeight,
                            bottomRightX: coords.bottomRightX / fieldLayoutAreaWidth,
                            bottomRightY: coords.bottomRightY / fieldLayoutAreaHeight
                        }
                    }
                }
            )))
        }
    }, [
        positionedFields,
        filePreviewScale,
        fieldLayoutAreaWidth,
        fieldLayoutAreaHeight,
        checkPositionedFieldCollision
    ])

    const onFieldResizeStop = useCallback(() => {
        setFieldResizing(false)
    }, [])

    const toggleFieldEditability = useCallback(field => {
        if (field.section === SECTIONS.AUTOFILL) {
            setPositionedFields(map(positionedFields, o => (
                areFieldEquals(o, field) ? { ...o, isEditable: !o.isEditable } : o
            )))
        }
    }, [positionedFields])

    const onDeleteField = useCallback(field => {
        const rule = find(fieldRules, o => doesFieldRuleContainField(o, field))

        if (rule) {
            setSelectedField(field)
            toggleDeleteFieldConfirmDialog(true)
        } else {
            setPositionedFields(prev => reject(
                prev, o => areFieldEquals(o, field)
            ))
        }
    }, [fieldRules])

    const onConfirmDeleteField = useCallback(() => {
        setPositionedFields(prev => reject(
            prev, o => areFieldEquals(o, selectedField)
        ))

        setFieldRules(prev => reject(prev,
            o => doesFieldRuleContainField(o, selectedField)
        ))

        toggleDeleteFieldConfirmDialog(false)
        setSelectedField(null)
    }, [selectedField])

    const onCancelConfirmDeleteField = useCallback(() => {
        toggleDeleteFieldConfirmDialog(false)
        setSelectedField(null)
    }, [setSelectedField])

    const onSetFieldProperty = useCallback((field) => {
        setSelectedField(field)
        setFieldPropertyEditorOpen(true)
    }, [])

    const onSaveFieldProperty = useCallback((property) => {
        setPositionedFields(fields => map(fields, o => (
            o.uuid === selectedField.uuid ? {
                ...o, error: null, property
            } : o
        )))

        setFieldPropertyEditorOpen(false)
        setSelectedField(null)
    }, [selectedField])

    const onCloseFieldPropertyEditor = useCallback(() => {
        setFieldPropertyEditorOpen(false)
        setSelectedField(null)
    }, [])

    const onEditRules = useCallback(() => {
        toggleFieldRuleEditor(true)
    }, [])

    const onSaveRules = useCallback((updatedRules) => {
        const preparedRules = map(updatedRules, ({ id, field, signature }) => {
            const fieldUuid = findWhere(signerPositionedFields,
                { title: field }).uuid

            const signatureUuid = findWhere(signerPositionedFields,
                { title: signature }).uuid

            return {
                id,
                field: { title: field, uuid: fieldUuid },
                signature: { title: signature, uuid: signatureUuid }
            }
        })

        setFieldRules(preparedRules)
        toggleFieldRuleEditor(false)
    }, [signerPositionedFields])

    const onAssignFolders = useCallback((value) => {
        toggleFolderAssigner(false)
        setAssignedFolders(value)
    }, [])

    const onDeleteRule = useCallback((rule) => () => {
        setSelectedFieldRule(rule)
        toggleDeleteFieldRuleConfirmDialog(true)
    }, [])

    const onConfirmDeleteRule = useCallback(() => {
        setFieldRules(prev => prev.filter(({ id }) => id !== selectedFieldRule.id))
        toggleDeleteFieldRuleConfirmDialog(false)
        setSelectedFieldRule(null)
    }, [selectedFieldRule])

    useEffect(setDefaultData, [isEditing, changeField])
    useEffect(init, [template, isEditing, isCopying, changeFields])

    useEffect(initPositionedFieldsIfReady, [
        template,
        fieldLayoutAreaWidth,
        fieldLayoutAreaHeight,
        isFilePreviewRenderSuccess
    ])

    useEffect(() => {
        return () => URL.revokeObjectURL(filePreviewData)
    }, [filePreviewData])

    useEffect(() => {
        onChanged(isChanged)
    }, [isChanged, onChanged])

    useMutationWatch(requesterPositionedFieldCount, () => {
        indexPositionedFieldTitles(isRequesterField)
    })

    useMutationWatch(isFieldPropertyEditorOpen, prev => {
        if (prev) indexPositionedFieldTitles(isRequesterField)
    })

    useMutationWatch(signerPositionedFieldCount, () => {
        indexPositionedFieldTitles(isSignerField)
    })

    useMutationWatch(requesterPositionedFieldCount, prev => {
        if (requesterPositionedFieldCount - prev === 1 && areFieldsChanged) {
            const field = last(requesterPositionedFields)

            setSelectedField(field)
            setFieldPropertyEditorOpen(true)

            setPositionedFields(prev => map(prev, o => (
                o.uuid === field.uuid ? {
                    ...o, error: FIELD_PROPERTY_ERROR
                } : o
            )))
        }
    })

    useMutationWatch(mappedCommunities.length, () => {
        changeField('communityIds', [mappedCommunities[0].value], true)
    }, () => mappedCommunities.length === 1)

    useEffect(() => {
        const props = ['uuid', 'title']

        setFieldRules(prev => map(prev, o => {
            const currentSignature = pick(findWhere(
                signerPositionedFields, { uuid: o.signature.uuid }
            ), props)

            const currentField = pick(findWhere(
                signerPositionedFields, { uuid: o.field.uuid }
            ), props)

            return {
                ...o,
                field: currentField.uuid ? currentField : o.field,
                signature: currentSignature.uuid ? currentSignature : o.signature
            }
        }))

    }, [signerPositionedFields])

    function setDefaultAssignedFolders() {
        if (!isEditing && isNotEmpty(assignedFoldersOfAllCommunities)) {
            setAssignedFolders(assignedFoldersOfAllCommunities)
        }
    }

    useEffect(setDefaultAssignedFolders, [
        isEditing,
        communities,
        assignedFoldersOfAllCommunities
    ])

    useEffect(() => {
        if (templateId) {
            setFetching(true)
            download({
                path: `/document-templates/${templateId}/download`,
                mimeType: 'application/pdf'
            })
                .then(response => {
                    setFile(convertBlobToFile(response.body, 'template'))
                })
                .catch(e => {
                    setError(e)
                })
                .finally(() => {
                    setFetching(false)
                })
        }
    }, [templateId])

    useEffect(validateIf, [
        validate,
        scrollToError,
        needValidation
    ])

    function onAutoSave() {
        setFetching(true)
        setAutoSaving(true)

        validate(validationOptions)
            .then(async () => {
                setNeedValidation(false)

                await submit(
                    getTemplateData({
                        status: template?.statusName,
                        assignedFolders,
                        positionedFields,
                        formFields: fields,
                        fieldRules: fieldRules
                    }, true)
                )
            })
            .catch(async error => {
                const data = {
                    status: template?.statusName,
                    assignedFolders,
                    positionedFields,
                    formFields: fields,
                    fieldRules: fieldRules
                }

                if (error.communityIds) {
                    data.assignedFolders = template?.configuration?.folders

                    if (isEmpty(template.communityIds)) {
                        data.formFields = data.formFields.setIn(['type'], ORGANIZATION)
                    }
                    else data.formFields = data.formFields.setIn(['communityIds'], template.communityIds)
                }

                if (error.name) data.formFields = data.formFields.setIn(['name'], template.title)

                await submit(getTemplateData(data, true))
            })
            .finally(() => {
                setFetching(false)
                setAutoSaving(false)
            })
    }

    useEffect(() => {
        if (template && !isCopying && autoSaveAdapter)
            autoSaveAdapter.init({
                onSave: () => onAutoSave()
            })
    }, [
        template,
        isCopying,
        onAutoSave,
        assignedFolders,
        autoSaveAdapter
    ])

    return (
        <>
            <Form className="ESignDocumentTemplateFieldEditor" onSubmit={tryToSubmit}>
                {(isDataFetching) && (
                    <Loader hasBackdrop />
                )}

                <div className="ESignDocumentTemplateFieldEditor-Body">
                    <DndContext
                        sensors={sensors}
                        onDragStart={onFieldDragStart}
                        onDragEnd={onFieldDragEnd}
                    >
                        <Scrollable
                            hasScrollTopBtn={false}
                            className="ESignDocumentTemplateFieldEditor-FieldSelectSection"
                        >
                            <Section
                                key={TEMPLATE}
                                name={TEMPLATE}
                                title="Template"
                                hasIcon={false}
                                className="margin-bottom-40 padding-right-20"
                            >
                                <TextField
                                    type="text"
                                    name="name"
                                    maxLength={36}
                                    value={fields.name}
                                    hasError={errors.name}
                                    label="Template Name*"
                                    errorText={errors.name}

                                    onChange={changeField}
                                />
                                <SelectField
                                    type="text"
                                    name="type"
                                    value={fields.type}
                                    hasError={errors.type}
                                    errorText={errors.type}
                                    placeholder="Select"
                                    hasEmptyValue={false}
                                    options={typeOptions}
                                    label="Template Type*"

                                    onChange={onChangeTemplateType}
                                />
                                {fields.type === COMMUNITY && (
                                    <SelectField
                                        isMultiple
                                        hasValueTooltip
                                        name="communityIds"
                                        hasAllOption={false}
                                        value={communityIds}
                                        placeholder='Select'
                                        label="Community Name*"
                                        options={mappedCommunities}
                                        hasError={errors.communityIds}
                                        errorText={errors.communityIds}
                                        isDisabled={mappedCommunities.length === 1}
                                        className="ESignDocumentTemplateFieldEditor-SelectField"

                                        onChange={onChangeCommunityField}
                                    />
                                )}

                                <div
                                    className={cn({
                                        "link font-size-15": true,
                                        "ESignDocumentTemplateFieldEditor-AssignFolder_disabled": (fields.type === COMMUNITY && isEmpty(communityIds)) || isEmpty(fields.type)
                                    })}
                                    onClick={() => toggleFolderAssigner(true)}
                                >
                                    Assign Template to Folder
                                </div>
                            </Section>
                            {fields.type === ORGANIZATION && (
                                <Section
                                    hasBorder
                                    key={AUTOFILL_ORGANIZATION}
                                    name={AUTOFILL_ORGANIZATION}
                                    renderTitle={() => (
                                        <>
                                            Auto-Fill: Organization
                                            <br />
                                            Template
                                        </>
                                    )}
                                    indicatorColor={SECTION_INDICATOR_COLORS[AUTOFILL_ORGANIZATION]}
                                    className="margin-bottom-40"
                                    hint='Use the selections in this section to auto-populate a template with community-specific data.'
                                >
                                    <div className='d-flex flex-wrap ESignDocumentTemplateFieldEditorSection-Body'>
                                        {autofillOrgFieldTypes.map(({ id, name, title }) => (
                                            <Draggable
                                                key={id}
                                                id={`${SECTIONS.AUTOFILL_ORGANIZATION}_field_${id}`}
                                                data={{ id, name, title, section: SECTIONS.AUTOFILL_ORGANIZATION }}
                                            >
                                                <Field
                                                    title={title}
                                                    className="margin-right-8 margin-bottom-12"
                                                />
                                            </Draggable>
                                        ))}
                                    </div>
                                </Section>
                            )}
                            <Section
                                hasBorder
                                key={AUTOFILL}
                                name={AUTOFILL}
                                title="Auto-Fill"
                                className='ESignDocumentTemplateFieldEditor-AutofillSection'
                                indicatorColor={SECTION_INDICATOR_COLORS[AUTOFILL]}
                                hint='Use the selections in this section to auto-populate a document with client/community data. The fields will be displayed on step 2 of Request Signature screen.'
                            >
                                <div className='d-flex flex-wrap ESignDocumentTemplateFieldEditorSection-Body'>
                                    {autofillFieldTypes.map(({ id, name, title }) => (
                                        <Draggable
                                            key={id}
                                            id={`${SECTIONS.AUTOFILL}_field_${id}`}
                                            data={{ id, name, title, isEditable: true, section: SECTIONS.AUTOFILL }}
                                        >
                                            <Field
                                                title={title}
                                                className="margin-right-8 margin-bottom-12"
                                            />
                                        </Draggable>
                                    ))}
                                </div>
                            </Section>
                            {/*task 7 */}
                            <Section
                                hasBorder
                                key={TOOLBOX_REQUESTER}
                                name={TOOLBOX_REQUESTER}
                                title="Toolbox: Requester"
                                indicatorColor={SECTION_INDICATOR_COLORS[TOOLBOX_REQUESTER]}
                                hint='Use the selections in this section to define fields that should be populated by Staff on step 2 of Request Signature screen.'
                                className='ESignDocumentTemplateFieldEditor-RequesterSection'
                            >
                                <div
                                    className='d-flex flex-column align-items-start pl-3 ESignDocumentTemplateFieldEditorSection-Body'>
                                    {mappedRequesterFieldTypes.map(({
                                        id,
                                        name,
                                        icon,
                                        title,
                                        hasValue
                                    }) => (
                                        <Draggable
                                            key={name}
                                            data={{
                                                id,
                                                name,
                                                title,
                                                hasValue,
                                                Icon: icon,
                                                iconBgColor: SECTION_INDICATOR_COLORS[TOOLBOX_REQUESTER],
                                                section: SECTIONS.TOOLBOX_REQUESTER
                                            }}
                                            id={`${SECTIONS.TOOLBOX_REQUESTER}_field_${name}`}
                                        >
                                            <Field
                                                Icon={icon}
                                                title={title}
                                                className="margin-right-8 margin-bottom-12"
                                                iconBgColor={SECTION_INDICATOR_COLORS[TOOLBOX_REQUESTER]}
                                            />
                                        </Draggable>
                                    ))}
                                </div>
                            </Section>

                            <Section
                                hasBorder
                                key={TOOLBOX_SIGNER}
                                name={TOOLBOX_SIGNER}
                                title="Toolbox: Signer"
                                indicatorColor={SECTION_INDICATOR_COLORS[TOOLBOX_SIGNER]}
                                hint='Use the selections in this section to define fields that should be populated by Signer (Staff or Client) while signing the document.'
                                className='ESignDocumentTemplateFieldEditor-SignerSection'
                            >
                                <div
                                    className='d-flex flex-column align-items-start pl-3 ESignDocumentTemplateFieldEditorSection-Body'>
                                    {mappedSignerFieldTypes.map(({ id, name, title, icon, width, height }) => (
                                        <Draggable
                                            key={id}
                                            data={{
                                                id,
                                                name,
                                                title,
                                                Icon: icon,
                                                ...Boolean(width && height) && {
                                                    isResizable: false,
                                                    size: {
                                                        width: width * filePreviewScale,
                                                        height: height * filePreviewScale
                                                    }
                                                },
                                                iconBgColor: SECTION_INDICATOR_COLORS[TOOLBOX_SIGNER],
                                                section: SECTIONS.TOOLBOX_SIGNER
                                            }}
                                            id={`${SECTIONS.TOOLBOX_SIGNER}_field_${name}`}
                                        >
                                            <Field
                                                Icon={icon}
                                                title={title}
                                                className="margin-right-8 margin-bottom-12"
                                                iconBgColor={SECTION_INDICATOR_COLORS[TOOLBOX_SIGNER]}
                                            />
                                        </Draggable>
                                    ))}
                                </div>
                            </Section>

                            <Section
                                key={RULES}
                                name={RULES}
                                title="Rules"
                                icon={RuleIcon}
                                isIconBtnDisabled={canAddFieldRule}
                                hint={canAddFieldRule
                                    ? 'The feature is disabled until two and more Signature boxes are added to the document viewer.'
                                    : null
                                }
                                className='ESignDocumentTemplateFieldEditor-RulesSection'
                                onClick={onEditRules}
                            >
                                <div className='d-flex flex-column flex-no-wrap margin-right-30'>
                                    {mappedFieldRules.map((rule) => {
                                        const {
                                            id,
                                            field,
                                            signature
                                        } = rule

                                        return (
                                            <Rule
                                                key={id}
                                                dependentField={field}
                                                className="margin-bottom-12"
                                                independentField={signature}
                                                onDelete={onDeleteRule(rule)}
                                            />
                                        )
                                    }
                                    )}
                                </div>
                            </Section>
                        </Scrollable>
                        <Droppable id="field-droppable-area"
                            className="ESignDocumentTemplateFieldEditor-FieldLayoutSection">
                            <div
                                ref={fieldLayoutSectionRef}
                                className="ESignDocumentTemplateFieldEditor-FieldLayoutArea"
                            >
                                {!isFieldResizing && (
                                    <DragOverlay>
                                        {draggableField && !draggableField.position && (
                                            <Field {...draggableField} />
                                        )}
                                    </DragOverlay>
                                )}
                                <TemplatePreview
                                    isSinglePageMode
                                    isLoading={false}
                                    innerRef={filePreviewRef}
                                    dataUrl={filePreviewData}
                                    width={fieldLayoutSectionRef.current?.clientWidth}
                                    onRenderSuccess={onRenderFilePreviewSuccess}
                                />
                                {map(visiblePositionedFields, o => (
                                    <Draggable
                                        data={o}
                                        canTransform
                                        isDisabled={isFieldResizing}
                                        style={{
                                            position: 'absolute',
                                            top: o.position.coords.topLeftY + 'px',
                                            left: o.position.coords.topLeftX + 'px'
                                        }}
                                        id={`${o.section}_positioned-field_${o.uuid}`}
                                        key={`${o.section}_positioned-field_${o.uuid}`}
                                    >
                                        {isSignerField(o) && o.name === 'CHECKBOX' ? (
                                            <ESignDocumentTemplateFieldBox
                                                data={o}
                                                isResizable={false}
                                                width='auto'
                                                height={Math.floor(o.size.height) + 1}
                                                minSizeConstraints={[5, 5]}
                                                id={`${o.section}_positioned-field_${o.uuid}`}
                                                color={SECTION_INDICATOR_COLORS_NAMES[o.section]}
                                                className="border-radius-2"
                                                onRemove={onDeleteField}
                                            >
                                                <div
                                                    className={cn(
                                                        'font-size-10',
                                                        'h-flexbox justify-content-between align-items-center'
                                                    )}
                                                    style={{ backgroundColor: '#f3323280' }}
                                                >
                                                    <CheckboxIcon2
                                                        className="border-radius-2"
                                                        style={{ height: Math.ceil(o.size.height) }}
                                                    />
                                                    {/\d+/.exec(o.title) && (
                                                        <div
                                                            style={{ color: '#ffffff' }}
                                                            className={cn(
                                                                "h-flexbox",
                                                                "align-items-center",
                                                                "justify-content-center",
                                                                "padding-left-4 padding-right-3"
                                                            )}
                                                        >
                                                            {/\d+/.exec(o.title)[0]}
                                                        </div>
                                                    )}
                                                </div>
                                            </ESignDocumentTemplateFieldBox>
                                        ) : (
                                            <ESignDocumentTemplateFieldBox
                                                data={o}
                                                canEditProps={isRequesterField(o)}

                                                width={o.size.width}
                                                height={o.size.height}
                                                isResizable={o.isResizable}
                                                maxSizeConstraints={[
                                                    FIELD_BOX_MAX_WIDTH,
                                                    FIELD_BOX_MAX_HEIGHT
                                                ]}
                                                minSizeConstraints={[
                                                    FIELD_BOX_MIN_WIDTH,
                                                    FIELD_BOX_MIN_HEIGHT
                                                ]}

                                                id={`${o.section}_positioned-field_${o.uuid}`}
                                                color={o.error ? 'deep-red' : SECTION_INDICATOR_COLORS_NAMES[o.section]}

                                                onRemove={onDeleteField}
                                                /*onDoubleClick={toggleFieldEditability}*/
                                                onSetProperties={onSetFieldProperty}

                                                onResize={onFieldResize}
                                                onResizeStop={onFieldResizeStop}
                                                onResizeStart={onFieldResizeStart}
                                            >
                                                <div
                                                    className={cn(
                                                        'flex-1',
                                                        'h-flexbox',
                                                        'padding-left-12',
                                                        'padding-right-12',
                                                        'align-items-center',
                                                        'justify-content-between'
                                                    )}
                                                >
                                                    <div
                                                        className="flex-1 line-clamp-1 user-select-none"
                                                        style={{ fontSize: Math.max(Math.min(o.size.height / 3.5, 14), 10) }}
                                                    >
                                                        {(() => {
                                                            if (isRequesterField(o)) {
                                                                const fields = filter(
                                                                    requesterPositionedFields, f => (
                                                                        !f.property && f.name === o.name
                                                                    )
                                                                )

                                                                const index = findIndex(
                                                                    fields, f => areFieldEquals(f, o)
                                                                )

                                                                return o.property?.label ?? o.title.replace(/\d+/, '').trim() + (
                                                                    fields.length === 1 ? '' : ` ${index + 1}`
                                                                )
                                                            }

                                                            return o.title
                                                        })()}
                                                    </div>
                                                    {/*{o.section === SECTIONS.AUTOFILL && (
                                                        <div id={`edit-${o.uuid}`} >
                                                            {o.isEditable
                                                                ? (
                                                                    <Pencil
                                                                        width={16}
                                                                        height={16}
                                                                        className="margin-left-10"
                                                                    />
                                                                ) : (
                                                                    <PencilCrossed
                                                                        width={16}
                                                                        height={16}
                                                                        className="margin-left-10"
                                                                    />
                                                                )
                                                            }
                                                            <Tooltip
                                                                placement="top"
                                                                trigger="hover"
                                                                target={`edit-${o.uuid}`}
                                                                boundariesElement={document.body}
                                                                className="ESignDocumentTemplateFieldEditor-FIeldEditTooltip"
                                                            >
                                                                Double clicking on placeholder changes it from editable to not-editable state. Editable means that Community Staff can modify auto-populated value in step 2 of Request Signature screen.
                                                            </Tooltip>
                                                        </div>
                                                    )}*/}
                                                </div>
                                            </ESignDocumentTemplateFieldBox>
                                        )}
                                    </Draggable>
                                ))}
                            </div>
                        </Droppable>
                    </DndContext>
                </div>

                <div className="ESignDocumentTemplateFieldEditor-Footer">
                    {children?.({
                        back,
                        cancel,
                        save: (e) => tryToSubmit(e, DRAFT),
                        isValidToComplete: isValid && !hasFieldErrors,
                        isDraftEnabled: template?.statusName !== COMPLETED || isCopying,
                        isValidToSave: !((template?.statusName === COMPLETED && !isCopying) || hasFieldErrors) && isValid
                    })}
                </div>
            </Form>

            <ESignDocumentTemplateFieldPropertyEditor
                isOpen={isFieldPropertyEditorOpen}
                hasValue={selectedField?.hasValue}
                fieldType={selectedFieldType}
                fieldLabel={selectedField?.property?.label}
                fieldValue={selectedField?.property?.values}

                onClose={onCloseFieldPropertyEditor}
                onSubmitSuccess={onSaveFieldProperty}
            />

            <ESignDocumentTemplateFieldRuleEditor
                rules={mappedFieldRules}
                isOpen={isFieldRuleEditorOpen}
                signatures={positionedSignatures}
                fields={positionedSignerFieldsWithoutSignature}

                onSubmitSuccess={onSaveRules}
                onClose={() => toggleFieldRuleEditor(false)}
            />


            <ESignDocumentTemplateFolderAssigner
                isOpen={isFolderAssignerOpen}
                assignedFolders={assignedFolders}
                communityOptions={selectedCommunityOptions}

                onSubmitSuccess={onAssignFolders}
                onClose={() => toggleFolderAssigner(false)}
            />

            <ConfirmDialog
                isOpen={isDeleteFieldConfirmDialogOpen}
                icon={Warning}
                confirmBtnText='Confirm'
                title="The field and related rule(s) will be deleted."
                onConfirm={onConfirmDeleteField}
                onCancel={onCancelConfirmDeleteField}
            />

            <ConfirmDialog
                isOpen={isDeleteFieldRuleConfirmDialogOpen}
                icon={Warning}
                confirmBtnText='Confirm'
                title="The rule and all dependencies will be deleted. Previously created signature requests will not be affected."
                onConfirm={onConfirmDeleteRule}
                onCancel={() => toggleDeleteFieldRuleConfirmDialog(false)}
            />

            {
                error && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={() => setError(null)}
                    />
                )
            }
        </>
    )
}

export default memo(
    withAutoSave()(ESignDocumentTemplateFieldEditor)
)
