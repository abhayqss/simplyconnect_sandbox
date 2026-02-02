import React, { memo } from 'react'

import { useClientQuery } from 'hooks/business/client/queries'

import { DocumentEditor as Editor } from 'containers/common/editors'

import DocumentForm from '../DocumentForm/DocumentForm'

function DocumentEditor(
    {
        isOpen,
        onClose,
        clientId,
        onUploadSuccess
    }
) {
    const {
        data: client
    } = useClientQuery({ clientId })

    return (
        <Editor
            isOpen={isOpen}
            onClose={onClose}
            onUploadSuccess={onUploadSuccess}
        >
            {({ onCancel, onSubmitSuccess }) => (
                <DocumentForm
                    clientId={clientId}
                    organization={client?.organization}
                    organizationId={client?.organizationId}
                    onCancel={onCancel}
                    onSubmitSuccess={onSubmitSuccess}
                />
            )}
        </Editor>
    )
}

export default memo(DocumentEditor)