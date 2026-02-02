import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import { map } from 'underscore'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import DocumentTitle from 'react-document-title'

import { Button } from 'reactstrap'

import {
    useResponse,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import {
    useListDataFetch,
    useSideBarUpdate
} from 'hooks/common/redux'

import {
    useCanUploadReleaseNotesQuery,
    useCanDeleteReleaseNotesQuery
} from 'hooks/business/help/release'

import {
    ErrorViewer,
    Breadcrumbs
} from 'components'

import { ConfirmDialog } from 'components/dialogs'

import * as errorActions from 'redux/error/errorActions'
import listActions from 'redux/help/release/note/list/releaseNoteListActions'
import detailsActions from 'redux/help/release/note/details/releaseNoteDetailsActions'
import deletionActions from 'redux/help/release/note/deletion/releaseNoteDeletionActions'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import { getSideBarItems } from '../SideBarItems'
import DocumentList from '../DocumentList/DocumentList'

import ReleaseNoteEditor from './ReleaseNoteEditor/ReleaseNoteEditor'

import './ReleaseNotes.scss'

function mapStateToProps(state) {
    const { note } = state.help.release

    return {
        state: note.list,
        details: note.details,
        canUpload: note.can.upload.value,
        canDelete: note.can.deletion.value,
        savingError: state.error.error,
        deletionError: note.deletion.error
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(listActions, dispatch),
            details: bindActionCreators(detailsActions, dispatch),
            deletion: bindActionCreators(deletionActions, dispatch),
            saving: bindActionCreators({ clearError: errorActions.clear }, dispatch)
        }
    }
}

function ReleaseNotes(
    {
        state,
        actions,
        details,
        canUpload,
        canDelete,
        savingError,
        deletionError
    }
) {
    const {
        isFetching,
        shouldReload,
        dataSource: ds
    } = state

    const error = (
        state.error || details.error || savingError || deletionError
    )

    const [selected, setSelected] = useState(null)
    const [isEditorOpen, setIsEditorOpen] = useState(false)
    const [shouldRefresh, setShouldRefresh] = useState(false)

    const [isDeleteConfirmDialogOpen, setIsDeleteConfirmDialogOpen] = useState(false)

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const { fetch, fetchIf } = useListDataFetch(state, actions)

    const updateSideBar = useSideBarUpdate()

    const data = useMemo(() => map(ds.data, o => ({
        id: o.id,
        title: o.description,
        mimeType: o.fileMimeType,
        createdDate: o.createdDate
    })), [ds.data])

    function closeEditor() {
        setSelected(false)
        setIsEditorOpen(false)
    }

    function closeDeleteConfirmDialog() {
        setSelected(false)
        setIsDeleteConfirmDialogOpen(false)
    }

    function refreshIfNeed() {
        setShouldRefresh(false)
        fetchIf(shouldRefresh || shouldReload)
    }

    useCanUploadReleaseNotesQuery()
    useCanDeleteReleaseNotesQuery()

    const onEdit = useCallback(o => {
        setSelected(o)
        setIsEditorOpen(true)
    }, [selected])

    const onCloseEditor = useCallback(() => {
        closeEditor()
    }, [selected])

    const onSaveSuccess = useCallback(() => {
        closeEditor()
        setShouldRefresh(true)
    }, [selected])

    const onDelete = useCallback(o => {
        setSelected(o)
        setIsDeleteConfirmDialogOpen(true)
    }, [selected])

    const onDownload = useCallback(({ id, mimeType }) => {
        withDownloadingStatusInfoToast(() => actions.details.download(id, { mimeType }))
    }, [actions, withDownloadingStatusInfoToast])

    const onCloseDeleteConfirmDialog = useCallback(() => {
        closeDeleteConfirmDialog()
    }, [selected])

    const onDeleteResponse = useResponse({
        onSuccess: () => setShouldRefresh(true)
    })

    const onConfirmDelete = useCallback(() => {
        closeDeleteConfirmDialog()
        actions.deletion.delete(selected.id).then(onDeleteResponse)
    }, [selected, actions])

    const onClearError = useCallback(() => {
        actions.clearError()
        actions.saving.clearError()
        actions.deletion.clearError()
    }, [actions])

    useEffect(() => {
        updateSideBar({
            isHidden: false,
            items: getSideBarItems()
        })
    }, [updateSideBar])

    useEffect(() => { fetch() }, [fetch])

    useEffect(
        refreshIfNeed,
        [fetchIf, shouldRefresh, shouldReload]
    )

    useEffect(() => actions.clear, [actions.clear])

    return (
        <DocumentTitle title="Simply Connect | Release Notes">
            <div className="ReleaseNotes">
                <Breadcrumbs
                    items={[
                        { title: 'Help', href: '/help' },
                        { title: 'Release Notes', href: '/help/release-notes', isActive: true },
                    ]}
                    className="margin-bottom-30"
                />
                <div className="ReleaseNotes-Header">
                    <div className="ReleaseNotes-HeaderItem">
                        <span className="ReleaseNotes-Title">
                            Release Notes
                        </span>
                    </div>
                    <div className="ReleaseNotes-HeaderItem">
                        <div className="ReleaseNotes-Actions">
                            {canUpload && (
                                <Button
                                    color='success'
                                    onClick={() => setIsEditorOpen(true)}
                                    className="ReleaseNotes-UploadBtn"
                                >
                                    Upload Notes
                                </Button>
                            )}
                        </div>
                    </div>
                </div>
                <DocumentList
                    data={data}
                    isLoading={isFetching}
                    noDataText="No release notes"
                    className="ReleaseNoteList"
                    getItemOptions={useCallback(() => ({
                        canDelete,
                        canView: false,
                        canEdit: canUpload,
                        isFormatIconClickable: true,

                        onEdit,
                        onDelete,
                        onDownload,

                        editHint: "Upload the new release notes document",
                        deleteHint: "Delete the release notes document",
                        downloadHint: "Download the release notes document"
                    }), [canDelete, canUpload, onEdit, onDelete])}
                />
                <ReleaseNoteEditor
                    isOpen={isEditorOpen}
                    noteId={selected?.id}
                    onClose={onCloseEditor}
                    onSaveSuccess={onSaveSuccess}
                />
                {isDeleteConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText="Confirm"
                        title="The release notes document will be deleted"
                        onConfirm={onConfirmDelete}
                        onCancel={onCloseDeleteConfirmDialog}
                    />
                )}
                {error && (
                    <ErrorViewer
                        isOpen
                        error={error}
                        onClose={onClearError}
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(ReleaseNotes)