import React, { memo } from 'react'

import { DocumentEditor as Editor } from 'containers/common/editors'

import DocumentForm from '../DocumentForm/DocumentForm'

function DocumentEditor(
    {
        isOpen,
        onClose,
        prospectId,
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
                <DocumentForm
                    prospectId={prospectId}
                    onCancel={onCancel}
                    onSubmitSuccess={onSubmitSuccess}
                />
            )}
        </Editor>
    )
}

export default memo(DocumentEditor)