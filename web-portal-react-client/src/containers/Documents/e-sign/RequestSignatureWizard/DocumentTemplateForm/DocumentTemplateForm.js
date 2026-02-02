import React, {
    memo,
    useRef,
    useMemo,
    useState,
    useEffect,
    useContext,
    useCallback
} from 'react'

import {
    all,
    omit,
    filter,
    isObject,
    mapObject
} from 'underscore'

import {
    useESignDocumentTemplateSchemesQuery
} from 'hooks/business/documents/e-sign/template'

import {
    useESignRequestSubmit
} from 'hooks/business/documents/e-sign'

import { SignatureRequestContext } from 'contexts'

import {
    Loader,
    ErrorViewer,
    WarningPanel
} from 'components'

import {
    DynaForm
} from 'components/DynaForm'

import {
    useEventListening
} from 'hooks/common'

import {
    isEmpty,
    isNotEmpty
} from 'lib/utils/Utils'

import {
    map
} from 'lib/utils/ArrayUtils'

import RequestSignatureProgress from '../RequestSignatureProgress/RequestSignatureProgress'

import './DocumentTemplateForm.scss'

function omitDeep(o, ...keys) {
    function omitIfObject(v) {
        return isObject(v) ? omitDeep(v, ...keys) : v
    }

    return Array.isArray(o)
        ? o.map(omitIfObject)
        : mapObject(
            omit(o, ...keys),
            omitIfObject
        )
}

function omitValidation(scheme) {
    return omitDeep(scheme, 'required')
}

const DocumentTemplateSections = memo(({ forms }) => (
    <div className="DocumentTemplateSections">
        {map(forms, o => (
            <div key={o.templateId} className="DocumentTemplateSection">
                <div className="DocumentTemplateSection-DecoratedBorder" />
                <div className="DocumentTemplateSection-Title">
                    {o.templateTitle}
                </div>
                <DynaForm
                    {...o}
                    noValidate
                    key={o.templateId}
                    className="DocumentTemplateForm"
                />
            </div>
        ))}
    </div>
))

function DocumentTemplateForm(
    {
        clientId,
        communityId,
        organizationId,
        defaultData,

        children,

        onBack,
        onCancel,
        onSubmitSuccess
    }
) {
    const [data, setData] = useState({})
    const [error, setError] = useState(false)
    const [isFetching, setFetching] = useState(false)

    const buttonsRef = useRef()

    const changeData = useCallback((templateId, changes) => {
        setData({
            ...data,
            [templateId]: {
                ...data[templateId],
                ...changes
            }
        })
    }, [data])

    const {
        templateIds
    } = useContext(SignatureRequestContext)

    const isSingle = templateIds?.length === 1

    const {
        data: templates = [],
        isFetching: isFetchingSchemes
    } = useESignDocumentTemplateSchemesQuery({ templateIds, clientId }, {
        enabled: isNotEmpty(templateIds)
    })

    const schemes = useMemo(() => templates.reduce((memo, o) => {
        if (o?.schema) memo[o.id] = omitValidation(JSON.parse(o.schema))
        return memo
    }, {}), [templates])

    const uiSchemes = useMemo(() => templates.reduce((memo, o) => {
        if (o?.uiSchema) memo[o.id] = omitValidation(JSON.parse(o.uiSchema))
        return memo
    }, {}), [templates])

    const { mutateAsync: submit } = useESignRequestSubmit({
        onError: setError,
        onSuccess: onSubmitSuccess
    })

    const onSubmit = useCallback(async e => {
        if (isSingle || e.target.id === 'submit-action') {
            setFetching(true)
            await submit(data)
        }
    }, [data, submit, isSingle])

    useEventListening(buttonsRef?.current, 'click', onSubmit)

    function back() {
        onBack()
    }

    function cancel() {
        onCancel(true)
    }

    function submitRightAway() {
        if (!isFetchingSchemes && all(templates, o => !o.schema)) {
            submit()
        }
    }

    const onChange = useCallback(data => {
        changeData(templateIds[0], data)
    }, [templateIds, changeData])

    const forms = useMemo(() => map(
        filter(templates, o => o.schema),
        (o, i) => (
            {
                templateId: o.id,
                templateTitle: o.title,
                schema: schemes[o.id],
                uiSchema: uiSchemes[o.id] ?? {},
                className: 'DocumentTemplateForm',
                onChange: data => changeData(o.id, data),
                defaultData: defaultData && (
                    isNotEmpty(defaultData[o.id])
                ) ? defaultData[o.id] : o?.defaults

            }
        )), [
        schemes,
        uiSchemes,
        templates,
        changeData,
        defaultData
    ])

    useEffect(() => {
        if (isEmpty(data)) {
            setData(templates?.reduce((memo, o) => {
                memo[o.id] = (
                    defaultData && isNotEmpty(defaultData[o.id])
                ) ? defaultData[o.id] : o?.defaults

                return memo
            }, {}) || {})
        }
    }, [data, templates, defaultData])

    useEffect(submitRightAway, [submit, templates, isFetchingSchemes])

    return (
        <div className="DocumentTemplateForm-Container">
            {(isFetching || isFetchingSchemes) && (
                <Loader hasBackdrop className="DocumentTemplateForm-Loader" />
            )}
            <RequestSignatureProgress
                className="DocumentTemplateForm-Progress"
            />
            <WarningPanel className="DocumentTemplateForm-WarningPanel">
                All of the fields are required
            </WarningPanel>
            {isSingle && (() => {
                const template = templates[0]
                const templateId = templateIds[0]
                const schema = schemes[templateId]
                const uiSchema = uiSchemes[templateId] ?? {}

                return schema && (
                    <DynaForm
                        noValidate
                        className="DocumentTemplateForm"
                        schema={schema}
                        uiSchema={uiSchema}
                        defaultData={(
                            defaultData && isNotEmpty(defaultData[templateId])
                        ) ? defaultData[templateId] : template?.defaults}
                        onChange={onChange}
                        onSubmit={onSubmit}
                    >
                        {children?.({ back, cancel, isValidToSubmit: true })}
                    </DynaForm>
                )
            })()}
            {!isSingle && (
                <>
                    <DocumentTemplateSections forms={forms} />
                    <div ref={buttonsRef}>
                        {children?.({ back, cancel, isValidToSubmit: true })}
                    </div>
                </>
            )}
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </div>
    )
}

export default memo(DocumentTemplateForm)
