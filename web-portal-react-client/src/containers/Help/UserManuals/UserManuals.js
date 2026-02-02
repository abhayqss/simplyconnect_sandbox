import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

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
    useCanUploadUserManualsQuery,
    useCanDeleteUserManualsQuery
} from 'hooks/business/help/user-manual'

import {
    ErrorViewer,
    Breadcrumbs
} from 'components'

import { ConfirmDialog } from 'components/dialogs'

import * as errorActions from 'redux/error/errorActions'
import listActions from 'redux/help/user/manual/list/userManualListActions'
import detailsActions from 'redux/help/user/manual/details/userManualDetailsActions'
import deletionActions from 'redux/help/user/manual/deletion/userManualDeletionActions'

import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import { getSideBarItems } from '../SideBarItems'
import DocumentList from '../DocumentList/DocumentList'

import UserManualEditor from './UserManualEditor/UserManualEditor'

import './UserManuals.scss'

function mapStateToProps(state) {
    const { manual } = state.help.user

    return {
        state: manual.list,
        details: manual.details,
        canUpload: manual.can.upload.value,
        canDelete: manual.can.deletion.value,
        savingError: state.error.error,
        deletionError: manual.deletion.error
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: {
            ...bindActionCreators(listActions, dispatch),
            details: bindActionCreators(detailsActions, dispatch),
            deletion: bindActionCreators(deletionActions, dispatch),
            saving: bindActionCreators({ clearError: errorActions.clear }, dispatch),
        }
    }
}

function UserManuals(
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

    useCanUploadUserManualsQuery()
    useCanDeleteUserManualsQuery()

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
        <DocumentTitle title="Simply Connect | User Manuals">
            <div className="UserManuals">
                <Breadcrumbs
                    items={[
                        { title: 'Help', href: '/help' },
                        { title: 'User Manuals', href: '/help/user-manuals', isActive: true },
                    ]}
                    className="margin-bottom-30"
                />
                <div className="UserManuals-Header">
                    <div className="UserManuals-HeaderItem">
                        <span className="UserManuals-Title">
                            User Manuals
                        </span>
                    </div>
                    <div className="UserManuals-HeaderItem">
                        <div className="UserManuals-Actions">
                            {canUpload && (
                                <Button
                                    color='success'
                                    onClick={() => setIsEditorOpen(true)}
                                    className="UserManuals-UploadBtn"
                                >
                                    Upload Manual
                                </Button>
                            )}
                        </div>
                    </div>
                </div>
                <DocumentList
                    data={ds.data}
                    isLoading={isFetching}
                    noDataText="No user manuals"
                    className="UserManualList"
                    getItemOptions={useCallback(() => ({
                        canDelete,
                        canView: false,
                        canEdit: canUpload,
                        isFormatIconClickable: true,

                        onEdit,
                        onDelete,
                        onDownload,

                        editHint: "Upload the new user manual document",
                        deleteHint: "Delete the user manual document",
                        downloadHint: "Download the user manual document"
                    }), [canDelete, canUpload, onEdit, onDelete])}
                />
                <UserManualEditor
                    isOpen={isEditorOpen}
                    manualId={selected?.id}
                    onClose={onCloseEditor}
                    onSaveSuccess={onSaveSuccess}
                />
                {isDeleteConfirmDialogOpen && (
                    <ConfirmDialog
                        isOpen
                        icon={Warning}
                        confirmBtnText="Confirm"
                        title="The user manual will be deleted"
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
)(UserManuals)