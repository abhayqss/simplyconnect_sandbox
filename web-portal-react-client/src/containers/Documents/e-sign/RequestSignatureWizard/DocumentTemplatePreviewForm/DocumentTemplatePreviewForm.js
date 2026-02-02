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
    any,
    all,
    map,
    each,
    omit,
    findWhere
} from 'underscore'

import {
    fabric
} from 'fabric'

import {
    Form
} from 'reactstrap'

import {
    useForm,
    useScrollable
} from 'hooks/common'

import {
    useCanvasObjectMovingRestrictions
} from 'hooks/common/fabric'

import {
    useDocumentTemplatesQuery
} from 'hooks/business/documents'

import {
    useESignRequestSubmit
} from 'hooks/business/documents/e-sign'

import {
    useESignDocumentTemplateSchemesQuery,
    useESignDocumentTemplatePreviewFilesDownload
} from 'hooks/business/documents/e-sign/template'

import {
    Loader,
    AlertPanel,
    ErrorViewer
} from 'components'

import { SignatureRequestContext } from 'contexts'

import Entity from 'entities/e-sign/ESignDocumentTemplatePreview'
import Validator from 'validators/TemplatePreviewFormValidator'

import {
    count,
    isNotEmpty
} from 'lib/utils/ArrayUtils'

import RequestSignatureProgress from '../RequestSignatureProgress/RequestSignatureProgress'
import DocumentTemplateFilePreviewer from '../DocumentTemplateFilePreviewer/DocumentTemplateFilePreviewer'

import './DocumentTemplatePreviewForm.scss'

// function Point(x, y) {
//     return [+x.toFixed(10), +y.toFixed(10)]
// }

// function Signature({ width, height }, { tl, br }) {
//     return {
//         type: 'SIGNATURE',
//         topLeft: Point(tl.x / width, tl.y / height),
//         bottomRight: Point(br.x / width, br.y / height)
//     }
// }

const scrollableStyles = { flex: 1 }

function Canvas(id, { width, height }) {
    return new fabric.Canvas(id, {
        width,
        height,
        backgroundColor: 'transparent'
    })
}

function onCanvasObjectScaling(e) {
    const shape = e.target
    const maxWidth = shape.get('maxWidth')
    const maxHeight = shape.get('maxHeight')
    const actualWidth = shape.scaleX * shape.width
    const actualHeight = shape.scaleY * shape.height;

    if (!isNaN(maxWidth) && actualWidth >= maxWidth) {
        shape.set({ scaleX: maxWidth / shape.width })
    }

    if (!isNaN(maxHeight) && actualHeight >= maxHeight) {
        shape.set({ scaleY: maxHeight / shape.height })
    }
}

function Box({ x = 0, y = 0, width = 0, height = 0 }, payload) {
    const rect = new fabric.Rect({
        top: y,
        left: x,
        width,
        height,
        fill: '#e0e0e0',
        minScaleLimit: 1,
        borderColor: '#0064ad',
        hasControls: false,
        lockMovementX: true,
        lockMovementY: true,
        hoverCursor: 'pointer',
        stroke: '#0064ad',
        strokeDashArray: [4, 4],
        isPlaceholder: true,
        payload
    })

    rect.setCoords()
    return rect
}

function onOverBox(e) {
    if (e.target?.isPlaceholder) {
        e.target.set({ fill: '#fff1ca' })
        e.target.canvas.requestRenderAll()
    }
}

function onOutBox(e) {
    if (e.target?.isPlaceholder) {
        e.target.set({ fill: '#e0e0e0' })
        e.target.canvas.requestRenderAll()
    }
}

function DocumentTemplatePreviewForm(
    {
        clientId,
        documentId,
        communityIds,
        templateId: defaultTemplateId,
        defaultData,

        children,

        onBack,
        onCancel,
        onSubmitSuccess
    }
) {
    const [error, setError] = useState(false)

    const [canvases, setCanvases] = useState([])
    const [areaWidth, setAreaWidth] = useState(0)

    const [documents, setDocuments] = useState([])

    const signAreaRef = useRef()

    const {
        fields,
        isChanged,
        changeField,
        changeFields,
    } = useForm('TemplatePreview', Entity, Validator)

    const {
        templateIds,
        requestData,
        templateData
    } = useContext(SignatureRequestContext)

    const selectedSignatureAreaCounts = useMemo(() => (
        fields.map(signatures => signatures.size).toJS()
    ), [fields])

    const { Scrollable } = useScrollable()

    const {
        add: addCanvasObjectMovingRestrictions,
        remove: removeCanvasObjectMovingRestrictions
    } = useCanvasObjectMovingRestrictions()

    const {
        data: templateTypes = []
    } = useDocumentTemplatesQuery({ communityIds }, {
        enabled: isNotEmpty(communityIds),
        staleTime: 0
    })

    const templates = useMemo(() => map(templateIds, templateId => ({
        clientId,
        templateId,
        documentId,
        ...templateData && templateData[templateId]
    })), [
        clientId,
        documentId,
        templateIds,
        templateData
    ])

    const files = useESignDocumentTemplatePreviewFilesDownload(templates, {
        enabled: isNotEmpty(templateIds),
        onError: setError
    })

    const isFetchingFiles = any(files, o => o.isFetching)

    const availableSignatureAreaCounts = useMemo(() => (
        files.reduce((memo, o) => {
            memo[o.templateId] = o.signatureAreas?.length ?? 0
            return memo
        }, {})
    ), [files])

    const {
        data: templateSchemes
    } = useESignDocumentTemplateSchemesQuery(
        { clientId, templateIds },
        { enabled: isNotEmpty(templateIds), }
    )

    const {
        mutateAsync: submit,
        isLoading: isFetching
    } = useESignRequestSubmit({
        throwOnError: true,
        onError: setError,
        onSuccess: onSubmitSuccess
    })

    function init() {
        changeFields(defaultData)
    }

    function setPlaceholders() {
        if (
            isNotEmpty(canvases)
            && all(files, o => o.url)
            && any(files, o => o.signatureAreas)
        ) {
            each(files, (o, i) => {
                const { templateId } = o
                const canvas = canvases[i]

                const {
                    width, height
                } = findWhere(documents, { templateId })

                each(o.signatureAreas, area => {
                    canvas.add(Box({
                        x: area.topLeftX * width,
                        y: area.topLeftY * height,
                        width: (area.bottomRightX - area.topLeftX) * width,
                        height: (area.bottomRightY - area.topLeftY) * height
                    }, { ...area, templateId }))
                    canvas.requestRenderAll()
                })
            })
        }
    }
    async function tryToSubmit(e) {
        e.preventDefault()

        try {
            await submit({
                clientId,
                ...omit(requestData, 'templateIds', 'originalData'),
                data: templateIds.map(templateId => ({
                    templateId,
                    documentId,
                    templateFieldValues: templateData && templateData[templateId],
                    signatureAreaIds: fields.get(String(templateId))?.map(o => o.id).toJS() ?? []
                }))
            })
        } catch (e) {
            setError(e)
        }
    }

    function back() {
        if (documentId || defaultTemplateId) onBack(2)
        else {
            const fillableCount = count(
                templateTypes, o => (
                    templateIds.includes(o.id) && o.isFillable
                )
            )

            onBack(fillableCount > 0 ? 1 : 2)
        }
    }

    function cancel() {
        onCancel(isChanged)
    }

    const addSignatureArea = useCallback(({ templateId, ...signature }) => {
        const k = String(templateId)
        const signatures = fields.get(k)

        changeField(k, signatures ? (
            signatures.push(signature)
        ) : [signature])
    }, [fields, changeField])

    const removeSignatureArea = useCallback(({ templateId, ...signature }) => {
        const k = String(templateId)
        const signatures = fields.get(k)
        changeField(k, signatures.filter(o => o.id !== signature.id))
    }, [fields, changeField])

    const turnBoxIntoPlaceholder = useCallback((target, canvas) => {
        target.set({
            fill: '#e0e0e0',
            borderColor: '#0064ad',
            stroke: '#0064ad',
            strokeDashArray: [4, 4],
            isPlaceholder: true
        })

        removeSignatureArea(target.payload)
        canvas.requestRenderAll()
    }, [removeSignatureArea])

    const turnBoxIntoSignatureArea = useCallback((target, canvas) => {
        target.set({
            fill: '#fff1ca',
            borderColor: '#0064ad',
            stroke: undefined,
            strokeDashArray: undefined,
            isPlaceholder: false
        })

        addSignatureArea(target.payload)
        canvas.requestRenderAll()
    }, [addSignatureArea])

    const onClickBox = useCallback(e => {
        if (e.target) {
            const { canvas } = e.target

            const render = e.target.isPlaceholder
                ? turnBoxIntoSignatureArea
                : turnBoxIntoPlaceholder

            render(e.target, canvas)
        }
    }, [
        turnBoxIntoPlaceholder,
        turnBoxIntoSignatureArea
    ])

    const onDocumentRenderSuccess = useCallback((o, { templateId }) => {
        setDocuments(prev => [...prev, { ...o, templateId }])
    }, [])

    useEffect(init, [defaultData, changeFields])

    useEffect(() => {
        // All files should be loaded successfully
        if (files.length === documents.length) {
            each(files, (o, i) => {
                const { templateId } = o
                const document = findWhere(documents, { templateId })

                setCanvases(prev => ([
                    ...prev,
                    Canvas(`doc-${i}-canvas`, {
                        width: document.width,
                        height: document.height
                    })
                ]))
            })
        }
    }, [files, documents])

    useEffect(() => {
        if (isNotEmpty(canvases)) {
            each(canvases, canvas => {
                canvas.on('mouse:over', onOverBox)
                canvas.on('mouse:out', onOutBox)
                canvas.on('mouse:up', onClickBox)
                addCanvasObjectMovingRestrictions(canvas)
                canvas.on('object:scaling', onCanvasObjectScaling)
            })
        }

        return () => {
            each(canvases, canvas => {
                canvas.off('mouse:over', onOverBox)
                canvas.off('mouse:out', onOutBox)
                canvas.off('mouse:up', onClickBox)
                removeCanvasObjectMovingRestrictions(canvas)
                canvas.off('object:scaling', onCanvasObjectScaling)
            })
        }
    }, [
        canvases,
        onClickBox,
        addCanvasObjectMovingRestrictions,
        removeCanvasObjectMovingRestrictions
    ])

    useEffect(() => {
        setAreaWidth(signAreaRef.current.clientWidth)
    }, [])

    useEffect(setPlaceholders, [files, canvases, documents])

    return (
        <>
            <Form className="DocumentTemplatePreviewForm" onSubmit={tryToSubmit}>
                {(isFetching || isFetchingFiles) && (
                    <Loader hasBackdrop />
                )}

                <Scrollable style={scrollableStyles} className="DocumentTemplatePreviewForm-Sections">
                    <div className="DocumentTemplatePreviewForm-Section">
                        <RequestSignatureProgress
                            className="DocumentTemplatePreviewForm-Progress"
                            hasSecondStep={!(documentId || defaultTemplateId)}
                        />

                        <AlertPanel className="DocumentTemplatePreviewForm-AlertPanel">
                            Please, review the document and define a place for signature by clicking on an area of the document you want to place a signature box
                        </AlertPanel>

                        <div className="DocumentTemplatePreviewForm-SignArea" ref={signAreaRef}>
                            {map(files, (o, i) => (
                                <div
                                    key={templateIds[i]}
                                    className="position-relative"
                                >
                                    <DocumentTemplateFilePreviewer
                                        data={o}
                                        dataUrl={o.url}
                                        width={areaWidth}
                                        onRenderSuccess={onDocumentRenderSuccess}
                                    />
                                    <canvas id={`doc-${i}-canvas`}/>
                                </div>
                            ))}
                        </div>
                    </div>
                </Scrollable>

                <div className="DocumentTemplatePreviewForm-Footer">
                    {children?.({
                        back,
                        cancel,
                        isValidToSubmit: (
                            templateIds.length === documents.length
                            && all(templateSchemes, o => (
                                o.hasSignatureAreas && availableSignatureAreaCounts[o.id] > 0 ? (
                                    selectedSignatureAreaCounts[o.id] > 0
                                ) : true
                            ))
                        )
                    })}
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

export default memo(DocumentTemplatePreviewForm)
