import React, {
    memo,
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'
import { saveAs } from 'file-saver'

import {
    Modal,
    Loader,
    FilePreviewer
} from 'components'

import {
    ConfirmDialog
} from 'components/dialogs'

import {
    Button
} from 'components/buttons'

import {
    DocumentDetails
} from 'components/business/Documents'

import {
    useToggle,
    useDownloadingStatusInfoToast
} from 'hooks/common'

import { download } from 'lib/utils/AjaxUtils'

import { ReactComponent as Cross } from 'images/cross.svg'
import { ReactComponent as Pencil } from 'images/pencil.svg'
import { ReactComponent as Download } from 'images/download-2.svg'
import { ReactComponent as Warning } from 'images/alert-yellow.svg'

import DocumentSignatureManager from '../DocumentSignatureManager/DocumentSignatureManager'

import './DocumentManager.scss'

function DocumentManager(
    {
        isOpen,
        isFetching,
        isDeleting,
        isRestoring,

        document,
        className,
        renderForm,

        hasEditBtn,
        hasDeleteBtn,
        hasRestoreBtn,

        onClose,

        onDelete,
        onRestore,
        onSaveSuccess,

        onSign,
        onEditTemplate,
        onRenewBulkRequest,
        onRequestSignature,
        onCancelBulkRequest,
        onRenewSignatureRequest,
        onCancelSignatureRequest
    }
) {
    const [isEditing, setEditing] = useState(false)
    const [isSignatureManagerOpen, toggleSignatureManager] = useToggle()
    const [isDeleteConfirmDialogOpen, toggleDeleteConfirmDialog] = useToggle()
    const [isRestoreConfirmDialogOpen, toggleRestoreConfirmDialog] = useToggle()
    const [isCancelEditConfirmDialogOpen, toggleCancelEditConfirmDialog] = useToggle()

    const isTemplate = useMemo(() => document?.type === "TEMPLATE", [document]);

    const deleteDialogTitle = useMemo(() => {
        const deletionType = document?.isTemporarilyDeleted ? 'permanently' : 'temporarily';

        return document?.type === "TEMPLATE"
            ? `${document?.title?.slice?.(0, -4)} template will be deleted.`
            : `The document will be ${deletionType} deleted.`
    }, [document]);

    const withDownloadingStatusInfoToast = useDownloadingStatusInfoToast()

    const onSaveFile = useCallback(() => {
        if (document) {
            withDownloadingStatusInfoToast(
                () => download({
                        path: document.path,
                        mimeType: document.mimeType
                    }).then(response => {
                        saveAs(response.body, document?.name)
                    })
            )
        }
    }, [document, withDownloadingStatusInfoToast]
    )

    const openSignatureManager = useCallback(
        () => toggleSignatureManager(true),
        [toggleSignatureManager]
    )

    let details = null

    if (isFetching) {
        details = (
            <Loader isCentered />
        )
    } else if (document) {
        details = (
            <DocumentDetails
                data={document}
                onViewSignature={openSignatureManager}
                className="DocumentManager-DocumentDetails"
            />
        )
    }

    const openDeleteConfirmDialog = useCallback(
        () => toggleDeleteConfirmDialog(true),
        [toggleDeleteConfirmDialog]
    )

    const _onDelete = useCallback(() => {
        toggleDeleteConfirmDialog(false)
        onDelete(document)
    }, [document, onDelete, toggleDeleteConfirmDialog])

    const openRestoreConfirmDialog = useCallback(
        () => toggleRestoreConfirmDialog(true),
        [toggleRestoreConfirmDialog]
    )

    const _onRestore = useCallback(() => {
        toggleRestoreConfirmDialog(false)
        onRestore(document)
    }, [document, onRestore, toggleRestoreConfirmDialog])

    const onEdit = useCallback(
        () => {
            if (isTemplate) {
                onEditTemplate();
            } else {
                setEditing(true);
            }
        }, [isTemplate, onEditTemplate]
    )

    const onCompleteEditing = useCallback(() => {
        setEditing(false)
        toggleCancelEditConfirmDialog(false)
    }, [toggleCancelEditConfirmDialog])

    const onCancelEditing = useCallback(isChanged => {
        if (isChanged) {
            toggleCancelEditConfirmDialog(true)
        } else onCompleteEditing()
    }, [onCompleteEditing, toggleCancelEditConfirmDialog])

    function _onCancelSignatureRequest() {
        toggleSignatureManager()
        onCancelSignatureRequest()
    }

    function _onCancelBulkRequest() {
        toggleSignatureManager()
        onCancelBulkRequest()
    }

    function _onRequestSignature() {
        toggleSignatureManager()
        onRequestSignature()
    }

    function _onRenewSignatureRequest() {
        toggleSignatureManager()
        onRenewSignatureRequest()
    }

    function _onRenewBulkRequest() {
        toggleSignatureManager()
        onRenewBulkRequest()
    }

    const viewer = (
        <div className="DocumentManager-DocumentViewer">
            <div className="DocumentManager-Document">
                {details}
                {document && !isEditing && (
                    <div className="DocumentManager-DocumentActions">
                        {document.isTemporarilyDeleted ? (
                            <>
                                {hasDeleteBtn && document.canDelete && (
                                    <Button
                                        outline
                                        color="success"
                                        className="DocumentManager-DocumentAction DocumentManager-PermanentlyDeleteAction"
                                        onClick={openDeleteConfirmDialog}
                                    >
                                        Permanently Delete
                                    </Button>
                                )}
                                {hasRestoreBtn && document.canDelete && (
                                    <Button
                                        color="success"
                                        className="DocumentManager-DocumentAction DocumentManager-RestoreAction"
                                        onClick={openRestoreConfirmDialog}
                                    >
                                        Restore File
                                    </Button>
                                )}
                            </>
                        ) : (
                            <>
                                {hasDeleteBtn && document.canDelete && (
                                    <Button
                                        outline
                                        color="success"
                                        className="DocumentManager-DocumentAction"
                                        onClick={openDeleteConfirmDialog}
                                    >
                                        Delete File
                                    </Button>
                                )}
                                {hasEditBtn && document.canEdit && (
                                    <Button
                                        color="success"
                                        className="DocumentManager-DocumentAction"
                                        onClick={onEdit}
                                    >
                                        Edit File
                                    </Button>
                                )}
                            </>
                        )}
                    </div>
                )}
            </div>
            <div className="DocumentManager-DocumentPreview">
                <FilePreviewer
                    src={{
                        name: document?.name,
                        path: document?.path,
                        mimeType: document?.mimeType
                    }}
                />
            </div>
        </div>
    )

    useEffect(() => {
        if (!isOpen) setEditing(false)
    }, [isOpen])

    return (
        <>
            {isOpen && (
                <Modal
                    isOpen
                    title="File Details"
                    className={cn(
                        'DocumentManager',
                        { 'DocumentManager_editing': isEditing },
                        className
                    )}
                    headerClassName="DocumentManager-Header"
                    bodyClassName="DocumentManager-Body"
                    footerClassName="DocumentManager-Footer"
                    hasCloseBtn={false}
                    hasFooter={false}
                    renderHeader={title => (
                        <div className="h-flexbox justify-content-between align-items-center">
                            <div className="DocumentManager-Title">
                                {isEditing && (
                                    <Pencil
                                        className={cn(
                                            'DocumentManager-HeaderIcon',
                                            'DocumentManager-HeaderEditIcon'
                                        )}
                                    />
                                )}
                                <div>{title}</div>
                            </div>
                            <div className="DocumentManager-HeaderButtons">
                                {!isEditing && (
                                    <Download
                                        className='DocumentManager-HeaderButton margin-right-20'
                                        onClick={onSaveFile}
                                    />
                                )}
                                <Cross
                                    className="DocumentManager-HeaderButton DocumentManager-CrossIcon"
                                    onClick={onClose}
                                />
                            </div>
                        </div>
                    )}
                >
                    {(isDeleting || isRestoring) && (
                        <Loader hasBackdrop />
                    )}
                    {isEditing ? (
                        <div className="DocumentManager-DocumentEditor">
                            {renderForm && renderForm({
                                children: viewer,
                                onCancel: onCancelEditing,
                                onSubmitSuccess: onSaveSuccess
                            })}
                        </div>
                    ) : viewer}
                </Modal>
            )}
            {isSignatureManagerOpen && (
                <DocumentSignatureManager
                    isOpen
                    document={document}
                    onSignDocument={onSign}
                    onRequestSignature={_onRequestSignature}
                    onRenewBulkRequest={_onRenewBulkRequest}
                    onCancelBulkRequest={_onCancelBulkRequest}
                    onRenewSignatureRequest={_onRenewSignatureRequest}
                    onCancelSignatureRequest={_onCancelSignatureRequest}
                    onClose={() => toggleSignatureManager()}
                />
            )}
            {isCancelEditConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="OK"
                    title="The updates will not be saved."
                    onConfirm={onCompleteEditing}
                    onCancel={() => toggleCancelEditConfirmDialog(false)}
                />
            )}
            {isDeleteConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="Delete"
                    title={deleteDialogTitle}
                    onConfirm={_onDelete}
                    onCancel={() => toggleDeleteConfirmDialog(false)}
                />
            )}
            {isRestoreConfirmDialogOpen && (
                <ConfirmDialog
                    isOpen
                    icon={Warning}
                    confirmBtnText="Restore"
                    title={`The document will be restored.`}
                    onConfirm={_onRestore}
                    onCancel={() => toggleRestoreConfirmDialog(false)}
                />
            )}
        </>
    )
}

DocumentManager.propTypes = {
    isOpen: PTypes.bool,
    isFetching: PTypes.bool,
    isDeleting: PTypes.bool,
    isRestoring: PTypes.bool,

    document: PTypes.object,
    className: PTypes.string,

    renderForm: PTypes.func,

    hasEditBtn: PTypes.bool,
    hasDeleteBtn: PTypes.bool,
    hasRestoreBtn: PTypes.bool,

    onClose: PTypes.func,
    onDelete: PTypes.func,
    onRestore: PTypes.func
}

DocumentManager.defaultProps = {
    hasEditBtn: true,
    hasDeleteBtn: true,
    hasRestoreBtn: true,
}

export default memo(DocumentManager)