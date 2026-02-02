import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import {
    ErrorViewer
} from 'components'

import {
    SuccessDialog
} from 'components/dialogs'

import {
    DocumentManager as Manager
} from 'containers/common/managers'

import {
    useClientQuery
} from 'hooks/business/client/queries'

import {
    useClientDocumentQuery,
    useClientDocumentDeletion,
    useClientDocumentRestoration
} from 'hooks/business/client/documents'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import {
    isInteger
} from 'lib/utils/Utils'

import DocumentForm from '../DocumentForm/DocumentForm'

import './DocumentManager.scss'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function DocumentManager(
    {
        isOpen,
        clientId,
        documentId,
        documentName,
        documentMimeType,
        hasEditBtn,
        hasDeleteBtn,
        hasRestoreBtn,
        onClose,
        onSaveSuccess,
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

    const {
        data: client
    } = useClientQuery(
        { clientId }, { enabled: isOpen }
    )

    const clientName = client?.fullName

    const {
        data,
        refetch,
        isFetching
    } = useClientDocumentQuery({
        clientId,
        documentId
    }, {
        staleTime: 0,
        onError: setError,
        enabled: isOpen && isInteger(documentId)
    })

    const preparedData = useMemo(
        () => data ? ({
            ...data,
            clientName,
            id: documentId,
            name: documentName ?? data.title,
            canEdit: data.canEdit && client?.isActive,
            canDelete: data.canDelete && client?.isActive,
            mimeType: documentMimeType ?? data.mimeType,
            path: `/clients/${clientId}/documents/${documentId}/download`
        }) : null,
        [
            data,
            client,
            clientId,
            clientName,
            documentId,
            documentName,
            documentMimeType,
        ]
    )

    if (data?.signature && !client?.isActive) {
        preparedData.signature.canRequest = false;
    }

    const {
        mutateAsync: remove,
        isLoading: isDeleting
    } = useClientDocumentDeletion({ clientId }, {
        onError: setError
    })

    const {
        mutateAsync: restore,
        isLoading: isRestoring
    } = useClientDocumentRestoration({ clientId })

    return (
        <>
            <Manager
                isOpen={isOpen}
                isFetching={isFetching}
                isDeleting={isDeleting}
                isRestoring={isRestoring}

                hasEditBtn={hasEditBtn}
                hasDeleteBtn={hasDeleteBtn}
                hasRestoreBtn={hasRestoreBtn}

                document={preparedData}

                remove={remove}
                restore={restore}
                refetch={refetch}

                className="ClientDocumentManager"

                renderForm={({ onCancel, onSubmitSuccess, children }) => (
                    <DocumentForm
                        clientId={clientId}
                        documentId={documentId}
                        submitButtonText="Save"
                        onCancel={onCancel}
                        onSubmitSuccess={onSubmitSuccess}
                    >
                        {children}
                    </DocumentForm>
                )}

                onClose={onClose}
                onSaveSuccess={onSaveSuccess}
                onDeleteSuccess={onDeleteSuccess}
                onRestoreSuccess={onRestoreSuccess}
                onRequestSignature={onRequestSignature}
                onRequestSignatureSuccess={onRequestSignatureSuccess}
                onRenewBulkRequestSuccess={onRenewBulkRequestSuccess}
                onCancelBulkRequestSuccess={onCancelBulkRequestSuccess}
                onCancelSignatureRequestSuccess={onCancelSignatureRequestSuccess}
            />
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

export default memo(DocumentManager)