import React, { memo, useState } from 'react'

import {
    ErrorViewer
} from 'components'

import {
    DocumentForm as Form
} from 'containers/common/forms'

import {
    useClientQuery
} from 'hooks/business/client/queries'

import {
    useClientDocumentQuery,
    useClientDocumentSubmit
} from 'hooks/business/client/documents'

import DocumentEntity from 'entities/Document'
import DocumentFormValidator from 'validators/ClientDocumentFormValidator'

function DocumentForm(
    {
        children,
        clientId,
        documentId,
        submitButtonText,
        onSubmitSuccess,
        onCancel
    }
) {
    const [error, setError] = useState(null)

    const {
        data: client
    } = useClientQuery({ clientId })

    const {
        organization,
        organizationId
    } = client ?? {}

    const {
        data: document,
        refetch: refetchDocument
    } = useClientDocumentQuery({ documentId }, {
        onError: setError,
        enabled: Boolean(documentId)
    })

    const { mutateAsync: submit } = useClientDocumentSubmit({ clientId }, {
        throwOnError: true,
        onSuccess: ({ data }) => {
            if (documentId) refetchDocument()
            onSubmitSuccess(data)
        }
    })

    return (
        <>
            <Form
                initialData={document}
                documentId={documentId}
                hasSharingSection={!documentId}
                organizationId={organizationId}
                organizationName={organization}
                submitButtonText={submitButtonText}
                entity={DocumentEntity}
                validator={DocumentFormValidator}
                className="ClientDocumentForm"
                onCancel={onCancel}
                submit={submit}
            >
                {children}
            </Form>

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => setError(null)}
                />
            )}
        </>
    )
}

export default memo(DocumentForm)
