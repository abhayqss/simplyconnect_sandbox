import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import {
    useQueryClient
} from '@tanstack/react-query'

import {
    findWhere
} from 'underscore'

import {
    ErrorViewer
} from 'components'

import {
    DocumentManager as Manager
} from 'containers/common/managers'

import {
    useDocumentQuery,
    useDocumentDeletion,
    useDocumentRestoration,
    useDocumentTemplateQuery
} from 'hooks/business/documents'

import {
    useESignDocumentTemplateDeletion
} from 'hooks/business/documents/e-sign/template'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import { isInteger } from 'lib/utils/Utils'

import DocumentForm from '../DocumentForm/DocumentForm'

import './DocumentManager.scss'

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function DocumentManager(
    {
        isOpen,
        folderId,
        templateId,
        documentId,
        documentName,
        documentMimeType,
        organizationId,
        communityId,
        onClose,
        onSaveSuccess,
        onEditTemplate,
        onDeleteSuccess,
        onRestoreSuccess,
        onRequestSignature
    }
) {

    const [error, setError] = useState(null)

    const {
        data,
        refetch,
        isFetching
    } = useDocumentQuery({ documentId }, {
        staleTime: 0,
        onError: setError,
        enabled: isOpen && isInteger(+documentId)
    })

    const {
        data: template
    } = useDocumentTemplateQuery(
        { templateId, communityId },
        {
            enabled: (
                isOpen
                && Boolean(templateId)
                && Boolean(communityId)
            ),
            staleTime: 0,
            onError: setError
        }
    )

    const preparedData = useMemo(
        () => {
            return (data || template) ? ({
                ...data,
                ...template,
                folderId,
                templateId,
                id: documentId,
                name: documentName ?? data.title,
                mimeType: documentMimeType ?? data.mimeType,
                path: templateId ? (
                    `/document-templates/${templateId}/download?communityId=${communityId}`
                ) : `/documents/${documentId}/download`
            }) : null;
        },
        [
            data,
            template,
            folderId,
            templateId,
            documentId,
            communityId,
            documentName,
            documentMimeType
        ]
    )

    const queryClient = useQueryClient()

    const organizations = queryClient.getQueryData(
        ['Directory.Organizations', null]
    )

    const organization = findWhere(
        organizations, { id: organizationId }
    )

    const {
        mutateAsync: removeDocument, 
        isLoading: isDocumentDeleting
    } = useDocumentDeletion({ throwOnError: true })

    const {
        mutateAsync: removeTemplate, 
        isLoading: isTemplateDeleting
    } = useESignDocumentTemplateDeletion({ throwOnError: true })

    const remove = useCallback((params) => {
        return preparedData.type === "TEMPLATE"
            ? removeTemplate({ templateId })
            : removeDocument({ documentId, ...params })
    }, [
        preparedData,
        documentId,
        templateId,
        removeTemplate,
        removeDocument
    ])

    const {
        mutateAsync: restore, 
        isLoading: isRestoring
    } = useDocumentRestoration({ throwOnError: true })

    return (
        <>
            <Manager
                isOpen={isOpen}
                isFetching={isFetching}
                isDeleting={isDocumentDeleting || isTemplateDeleting}
                isRestoring={isRestoring}

                document={preparedData}

                remove={remove}
                restore={restore}
                refetch={refetch}

                renderForm={({ onCancel, onSubmitSuccess, children }) => (
                    <DocumentForm
                        folderId={folderId}
                        documentId={documentId}
                        communityId={communityId}
                        organizationId={organizationId}
                        organizationName={organization?.label}
                        submitButtonText="Save"
                        onCancel={onCancel}
                        onSubmitSuccess={onSubmitSuccess}
                    >
                        {children}
                    </DocumentForm>
                )}

                onClose={onClose}
                onSaveSuccess={onSaveSuccess}
                onEditTemplate={onEditTemplate}
                onDeleteSuccess={onDeleteSuccess}
                onRestoreSuccess={onRestoreSuccess}
                onRequestSignature={onRequestSignature}
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