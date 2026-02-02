import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    pluck,
    compact,
} from 'underscore'

import {
    useParams
} from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import {
    Badge,
    Collapse,
} from 'reactstrap'

import {
    useToggle,
    useQueryInvalidation,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useProspectQuery,
    useSideBarUpdate
} from 'hooks/business/Prospects'

import {
    useProspectDocumentsQuery,
    useProspectDocumentDeletion,
    useProspectDocumentDownload,
    useProspectDocumentListState,
    useProspectDocumentRestoration,
    useCanAddProspectDocumentsQuery,
    useProspectDocumentMultiDownload,
    usePreparedProspectDocumentFilterData
} from 'hooks/business/Prospects/Documents'

import {
    Breadcrumbs,
    ErrorViewer
} from 'components'

import {
    Button
} from 'components/buttons'

import {
    ConfirmDialog,
} from 'components/dialogs'

import {
    DocumentList
} from 'components/business/Documents'

import {
    toNumberExcept
} from 'lib/utils/Utils'

import {
    path
} from 'lib/utils/ContextUtils'

import {
    SERVER_ERROR_CODES,
    ALLOWED_FILE_FORMATS,
} from 'lib/Constants'

import { ReactComponent as Filter } from 'images/filters.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import DocumentFilter from './DocumentFilter/DocumentFilter'
import DocumentEditor from './DocumentEditor/DocumentEditor'

import './Documents.scss'

const { PDF, XML } = ALLOWED_FILE_FORMATS

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function Documents() {
    const params = useParams()

    const prospectId = toNumberExcept(
        params.prospectId, [null, undefined]
    )

    const [selected, setSelected] = useState(null)

    const [isFilterOpen, toggleFilter] = useToggle(true)
    const [isEditorOpen, toggleEditor] = useToggle()
    const [isManagerOpen, toggleManager] = useToggle()
    const [isConfirmDeleteDialogOpen, toggleConfirmDeleteDialog] = useToggle()
    const [isConfirmRestoreDialogOpen, toggleConfirmRestoreDialog] = useToggle()

    const {
        data: prospect
    } = useProspectQuery({ prospectId })

    const updateSideBar = useSideBarUpdate({ prospectId })

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const {
        state,
        setError,
        clearError,
        changeFilter
    } = useProspectDocumentListState()

    const { error } = state

    const filter = usePreparedProspectDocumentFilterData(
        { prospectId, ...state.filter.toJS() }
    )

    const {
        sort,
        fetch,
        refresh,
        pagination,
        isFetching,
        data: { data = [] } = {}
    } = useProspectDocumentsQuery(
        { prospectId, ...filter },
        { onError: setError }
    )

    const {
        data: canAdd = true
    } = useCanAddProspectDocumentsQuery({ prospectId })

    const {
        mutateAsync: remove,
        isLoading: isDeleting
    } = useProspectDocumentDeletion(
        { prospectId }, { onError: setError }
    )

    const {
        mutateAsync: restore,
        isLoading: isRestoring
    } = useProspectDocumentRestoration(
        { prospectId }, { onError: setError }
    )

    const {
        mutateAsync: download,
        isLoading: isDownloading
    } = useProspectDocumentDownload({
        onError: setError
    })

    const {
        mutateAsync: downloadMultiple,
        isLoading: isDownloadingMultiple
    } = useProspectDocumentMultiDownload({
        onError: setError
    })

    const prospectName = prospect?.fullName
    const isProspectActive = prospect?.isActive

    const preparedData = useMemo(() => data.map(({ canDelete, canEdit, ...o }) => ({
        ...o,
        canDelete: canDelete && isProspectActive,
        canEdit: canEdit && isProspectActive
    })), [data, isProspectActive])

    useEffect(() => {
        updateSideBar()
    }, [updateSideBar])

    useEffect(() => {
        fetch()
    }, [fetch])

    const invalidate = useQueryInvalidation()

    const invalidateCount = useCallback(() => {
        invalidate(
            'Prospect.Document.Count',
            { prospectId, includeDeleted: false }
        )
    }, [prospectId, invalidate])

    const onRefresh = useCallback(() => {
        fetch()
        updateSideBar()
        invalidateCount()
    }, [fetch, updateSideBar, invalidateCount])

    const onOpenConfirmDeleteDialog = useCallback((document) => {
        setSelected(document)

        toggleConfirmDeleteDialog()
    }, [])

    const onCloseConfirmDeleteDialog = useCallback(() => {
        setSelected(null)

        toggleConfirmDeleteDialog()
    }, [])

    const onCloseEditor = useCallback(() => {
        toggleEditor(false)
    }, [])

    const onView = useCallback(o => {
        setSelected(o)
        toggleManager(true)
    }, [])

    const onCloseViewer = useCallback(() => {
        toggleManager(false)
    }, [])

    const onDelete = useCallback(() => {
        setSelected(null)
        toggleConfirmDeleteDialog()

        remove({
            documentId: selected.id,
            isTemporaryDeletion: !selected.isTemporarilyDeleted
        }).then(onRefresh)
    }, [
        remove,
        selected,
        onRefresh,
        toggleConfirmDeleteDialog
    ])

    const onDeleteSuccess = useCallback(isTemporarily => {
        onRefresh()

        if (!isTemporarily) toggleManager(false)
    })

    const onUploadSuccess = useCallback(() => {
        fetch()
        updateSideBar()
        invalidateCount()
    }, [fetch, updateSideBar, invalidateCount])

    const onOpenConfirmRestoreDialog = useCallback((document) => {
        setSelected(document)

        toggleConfirmRestoreDialog()
    }, [])

    const onCloseConfirmRestoreDialog = useCallback(() => {
        setSelected(null)

        toggleConfirmRestoreDialog()
    })

    const onRestore = useCallback(() => {
        setSelected(null)
        toggleConfirmRestoreDialog()

        restore({
            prospectId,
            documentId: selected.id
        }).then(onRefresh)
    }, [selected, prospectId, onRefresh])

    const onDownloadSingle = useCallback(o => {
        withDownloadingStatusInfoToast(() => download({
            prospectId,
            documentId: o.id,
            mimeType: o.mimeType
        }))
    }, [download, prospectId, withDownloadingStatusInfoToast])

    const onDownloadMultiple = useCallback(items => {
        if (items.length === 1) {
            onDownloadSingle(items[0])
        } else {
            withDownloadingStatusInfoToast(() => downloadMultiple({
                prospectId,
                documentIds: pluck(items, 'id')
            }))
        }
    }, [
        prospectId,
        onDownloadSingle,
        downloadMultiple,
        withDownloadingStatusInfoToast
    ])

    const onDownloadAll = useCallback(() => {        
        withDownloadingStatusInfoToast(() => downloadMultiple({ prospectId, ...filter }))
    }, [filter, prospectId, downloadMultiple, withDownloadingStatusInfoToast])

    return (
        <DocumentTitle title="Simply Connect | Prospects | Prospect documents">
            <div className="ProspectDocuments">
                <Breadcrumbs
                    items={compact([
                        { title: 'Prospects', href: '/prospects', isEnabled: true },
                        prospectName && { title: prospectName || '', href: `/prospects/${prospectId}` },
                        { title: 'Documents', href: `/prospects/${prospectId}/documents`, isActive: true }
                    ])}
                    className="margin-bottom-32"
                />
                <div className="ProspectDocuments-Header">
                    <div className="ProspectDocuments-HeaderItem">
                        <div className="ProspectDocuments-Title">
                            <span className='ProspectDocuments-TitleText'>
                                Documents
                            </span>
                            <div className="d-inline-block text-nowrap">
                                {prospectName && (
                                    <span className="ProspectDocuments-ProspectName">
                                        &nbsp;/&nbsp;{prospectName}
                                    </span>
                                )}
                                {pagination.totalCount ? (
                                    <Badge color='info' className='Badge Badge_place_top-right'>
                                        {pagination.totalCount}
                                    </Badge>
                                ) : null}
                            </div>
                        </div>
                    </div>
                </div>
                <div className="ProspectDocuments-HeaderItem">
                    <div className="ProspectDocuments-Actions">
                        {canAdd && isProspectActive && (
                            <Button
                                color='success'
                                className="ProspectDocuments-UploadAction margin-left-20"
                                onClick={() => toggleEditor(true)}
                            >
                                Upload Document
                            </Button>
                        )}
                        <Filter
                            className={cn(
                                'ProspectDocumentFilter-Icon margin-left-20',
                                isFilterOpen
                                    ? 'ProspectDocumentFilter-Icon_rotated_90'
                                    : 'ProspectDocumentFilter-Icon_rotated_0'
                            )}
                            onClick={() => toggleFilter(!isFilterOpen)}
                        />
                    </div>
                </div>
                <Collapse isOpen={isFilterOpen}>
                    <DocumentFilter
                        prospectId={prospectId}
                        onChange={changeFilter}
                        onReset={isSaved => isSaved && fetch()}
                        onApply={fetch}
                    />
                </Collapse>
                <DocumentList
                    data={preparedData}
                    hasAuthorCol
                    hasCreatedDateCol
                    hasLastModifiedDateCol
                    pagination={pagination}
                    noDataText="No results found."
                    isFetching={
                        isFetching
                        || isDeleting
                        || isRestoring
                        // || isFetchingSignedDocument
                        // || isFetchingSignatureRequest
                    }
                    isDownloading={isDownloading || isDownloadingMultiple}
                    getPath={data => path(`/prospects/${prospectId}/documents/${data.id}-${data.title}`)}
                    className="ProspectDocumentList"

                    onSort={sort}
                    onView={onView}
                    onRefresh={refresh}
                    onDownloadSingle={onDownloadSingle}
                    onDownloadMultiple={onDownloadMultiple}
                    onDownloadAll={onDownloadAll}
                    onDelete={onOpenConfirmDeleteDialog}
                    onRestore={onOpenConfirmRestoreDialog}
                />
                <DocumentEditor
                    prospectId={prospectId}
                    isOpen={isEditorOpen}
                    onClose={onCloseEditor}
                    onUploadSuccess={onUploadSuccess}
                />
                {isConfirmDeleteDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText='Delete'
                        title={`The document will be ${selected.isTemporarilyDeleted ? 'permanently' : 'temporarily'} deleted`}
                        onConfirm={onDelete}
                        onCancel={onCloseConfirmDeleteDialog}
                    />
                )}
                {isConfirmRestoreDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText='Restore'
                        title={`The document will be restored`}
                        onConfirm={onRestore}
                        onCancel={onCloseConfirmRestoreDialog}
                    />
                )}
                {error && !isIgnoredError(error) && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={clearError}
                    />
                )}
            </div>            
        </DocumentTitle>
    )
}

export default memo(Documents)