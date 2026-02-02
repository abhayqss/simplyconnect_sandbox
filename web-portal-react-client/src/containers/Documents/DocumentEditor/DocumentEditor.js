import React, { memo } from 'react'

import { findWhere } from 'underscore'

import { useQueryClient } from '@tanstack/react-query'

import { DocumentEditor as Editor } from 'containers/common/editors'

import DocumentForm from '../DocumentForm/DocumentForm'

function DocumentEditor(
    {
        isOpen,
        onClose,
        folderId,
        documentId,
        communityId,
        organizationId,
        folderCategoryIds,
        onUploadSuccess
    }
) {
    const queryClient = useQueryClient()

    const organizations = queryClient.getQueryData(
        ['Directory.Organizations', null]
    )

    const organization = findWhere(
        organizations, { id: organizationId }
    )

    return (
        <Editor
            isOpen={isOpen}
            onClose={onClose}
            onUploadSuccess={onUploadSuccess}
        >
            {({ onCancel, onSubmitSuccess }) => (
                <DocumentForm
                    documentId={documentId}
                    communityId={communityId}
                    organizationId={organizationId}
                    organizationName={organization?.label}
                    folderId={folderId}
                    folderCategoryIds={folderCategoryIds}
                    onCancel={onCancel}
                    onSubmitSuccess={onSubmitSuccess}
                />
            )}
        </Editor>
    )
}

export default memo(DocumentEditor)