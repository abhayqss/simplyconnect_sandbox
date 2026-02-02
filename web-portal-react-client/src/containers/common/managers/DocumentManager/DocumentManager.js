import React, {
    memo,
    useMemo,
    useEffect,
    useState,
    useCallback
} from 'react'

import {
    useESignRequestCancel,
    useESignRequestRenewal,
    useESignBulkRequestCancel,
    useESignBulkRequestRenewal
} from 'hooks/business/documents/e-sign'

import {
    ErrorViewer
} from 'components'

import {
    SuccessDialog
} from 'components/dialogs'

import {
    DocumentManager as Manager
} from 'components/business/Documents'

import {
    interpolate,
    DateUtils as DU
} from 'lib/utils/Utils'

import { noop } from 'lib/utils/FuncUtils'

import { SERVER_ERROR_CODES } from 'lib/Constants'

const DELETE_DOC_CONFIRM_TEXT = 'The document is $0 deleted'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function getTimeAfter7DaysFromNow() {
    return DU.add(Date.now(), 7, 'day').getTime()
}

function DocumentManager(
    {
        isOpen,

        isFetching,
        isDeleting,
        isRestoring,

        hasEditBtn,
        hasDeleteBtn,
        hasRestoreBtn,

        document,
        remove,
        restore,
        refetch,
        className,
        renderForm,

        onClose,
        onSaveSuccess,
        onEditTemplate,
        onDeleteSuccess,
        onRestoreSuccess,
        onRequestSignature,
        onRequestSignatureSuccess,
        onRenewBulkRequestSuccess,
        onCancelBulkRequestSuccess,
        onCancelSignatureRequestSuccess
    }
) {
    const [error, setError] = useState(null)

    const [isSaveSuccessDialogOpen, toggleSaveSuccessDialog] = useState(false)
    const [isDeleteSuccessDialogOpen, toggleDeleteSuccessDialog] = useState(false)
    const [isRestoreSuccessDialogOpen, toggleRestoreSuccessDialog] = useState(false)
    const [isTemplate, setIsTemplate] = useState(false);

    const {
        mutateAsync: cancelSignatureRequest,
        isLoading: isCancelingSignatureRequest
    } = useESignRequestCancel({
        onError: setError
    })

    const {
        mutateAsync: renewSignatureRequest,
        isLoading: isRenewingSignatureRequest
    } = useESignRequestRenewal({
        onError: setError
    })

    const {
        mutateAsync: renewBulkRequest,
        isLoading: isRenewingBulkRequest
    } = useESignBulkRequestRenewal({
        onError: setError
    })

    const {
        mutateAsync: cancelBulkRequest,
        isLoading: isCancelingBulkRequest
    } = useESignBulkRequestCancel({
        onError: setError
    })

    const deletionSuccessText = useMemo(() => {
        return interpolate(
            DELETE_DOC_CONFIRM_TEXT,
            document?.isTemporarilyDeleted ? 'permanently' : 'temporarily'
        )
    }, [document])

    const _onSaveSuccess = useCallback(() => {
        refetch()
        onSaveSuccess()
        toggleSaveSuccessDialog(true)
    }, [refetch, onSaveSuccess])

    const onDelete = useCallback(() => {
        const isTemporaryDeletion = !document.isTemporarilyDeleted

        remove({
            isTemporaryDeletion,
            documentId: document.id
        }).then(() => {
            if (!isTemplate) {
                toggleDeleteSuccessDialog(true)
            }
            onDeleteSuccess(!isTemplate ? isTemporaryDeletion : false);
        }).catch(setError)
    }, [
        remove,
        document,
        isTemplate,
        onDeleteSuccess
    ])

    const onCloseDeleteSuccessDialog = useCallback(() => {
        if (isOpen) {
            refetch()
        }
        toggleDeleteSuccessDialog(false)
    }, [isOpen, refetch])

    const onRestore = useCallback(() => {
        restore({ documentId: document.id }).then(() => {
            refetch()
            onRestoreSuccess()
            toggleRestoreSuccessDialog(true)
        }).catch(setError)
    }, [
        restore,
        refetch,
        document,
        onRestoreSuccess
    ])

    const onCancelSignatureRequest = useCallback(() => {
        cancelSignatureRequest({
            requestId: document?.signature?.requestId
        }).then(() => {
            refetch()
            onCancelSignatureRequestSuccess()
        })
    }, [
        refetch,
        document,
        cancelSignatureRequest,
        onCancelSignatureRequestSuccess
    ])

    const onCancelBulkRequest = useCallback(() => {
        cancelBulkRequest({
            bulkRequestId: document?.bulkRequestId,
            templateId: document?.templateId
        }).then(() => {
            refetch()
            onCancelBulkRequestSuccess()
        })
    }, [
        refetch,
        document,
        cancelBulkRequest,
        onCancelBulkRequestSuccess
    ])

    const onRenewBulkRequest = useCallback(() => {
        renewBulkRequest({
            bulkRequestId: document?.bulkRequestId,
            expirationDate: getTimeAfter7DaysFromNow(),
            templateId: document?.templateId
        }).then(() => {
            refetch()
            onRenewBulkRequestSuccess()
        })
    }, [
        refetch,
        document,
        renewBulkRequest,
        onRenewBulkRequestSuccess
    ])

    const _onRequestSignature = useCallback(() => {
        onRequestSignature(document)
    }, [document, onRequestSignature])

    const _onEditTemplate = useCallback(() => {
        onEditTemplate(document)
    }, [document, onEditTemplate])

    const onRenewSignatureRequest = useCallback(() => {
        renewSignatureRequest({
            requestId: document?.signature?.requestId,
            expirationDate: getTimeAfter7DaysFromNow()
        }).then(() => {
            refetch()
            onRequestSignatureSuccess()
        })
    }, [
        refetch,
        document,
        renewSignatureRequest,
        onRequestSignatureSuccess
    ])

    const onSign = useCallback(() => {
        window.open(document.signature.pdcFlowLink)
    }, [document])

    useEffect(() => {
        setIsTemplate(document?.type === "TEMPLATE");
    }, [document])

    return (
        <>
            <Manager
                isOpen={isOpen}
                isFetching={isFetching}
                isDeleting={isDeleting || isCancelingSignatureRequest || isCancelingBulkRequest}
                isRestoring={isRestoring || isRenewingSignatureRequest || isRenewingBulkRequest}
                document={document}
                className={className}
                renderForm={renderForm}
                hasEditBtn={hasEditBtn}
                hasDeleteBtn={hasDeleteBtn}
                hasRestoreBtn={hasRestoreBtn}
                onClose={onClose}
                onDelete={onDelete}
                onRestore={onRestore}
                onSaveSuccess={_onSaveSuccess}
                onSign={onSign}
                onEditTemplate={_onEditTemplate}
                onRequestSignature={_onRequestSignature}
                onRenewBulkRequest={onRenewBulkRequest}
                onCancelBulkRequest={onCancelBulkRequest}
                onRenewSignatureRequest={onRenewSignatureRequest}
                onCancelSignatureRequest={onCancelSignatureRequest}
            />
            {isSaveSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={`The document has been updated.`}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => toggleSaveSuccessDialog(false)
                        }
                    ]}
                />
            )}
            {isDeleteSuccessDialogOpen && (
                <SuccessDialog
                    isOpen

                    title={deletionSuccessText}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: onCloseDeleteSuccessDialog
                        }
                    ]}
                />
            )}
            {isRestoreSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title={`The document has been restored.`}
                    buttons={[
                        {
                            text: 'Close',
                            onClick: () => toggleRestoreSuccessDialog(false)
                        }
                    ]}
                />
            )}
            {error && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

DocumentManager.defaultProps = {
    remove: noop,
    restore: noop,
    refetch: noop,
    onClose: noop,
    onDeleteSuccess: noop,
    onRestoreSuccess: noop,
    onSaveSuccess: noop,
    onRequestSignature: noop,
    onRenewBulkRequestSuccess: noop,
    onCancelBulkRequestSuccess: noop,
    onCancelSignatureRequestSuccess: noop
}

export default memo(DocumentManager)