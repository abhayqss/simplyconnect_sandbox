import React, { memo } from 'react'

import { RequestSignatureEditor as Editor } from 'containers/common/editors'

import Form from '../RequestSignatureWizard/RequestSignatureWizard'

function RequestSignatureEditor(
    {
        isOpen,
        onClose,
        clients,
        clientId,
        clientIds,
        templateId,
        documentId,
        communityIds,
        organizationId,
        isMultipleRequest,

        onUploadSuccess
    }
) {
    return (
        <Editor
            isOpen={isOpen}
            onClose={onClose}
            onUploadSuccess={onUploadSuccess}
        >
            {({ onCancel, onSubmitSuccess }) => (
                <Form
                    clients={clients}
                    clientId={clientId}
                    clientIds={clientIds}
                    templateId={templateId}
                    documentId={documentId}
                    communityIds={communityIds}
                    organizationId={organizationId}
                    isMultipleRequest={isMultipleRequest}

                    onCancel={onCancel}
                    onSubmitSuccess={onSubmitSuccess}
                />
            )}
        </Editor>
    )
}

export default memo(RequestSignatureEditor)