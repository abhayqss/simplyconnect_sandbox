import React, { memo, useState } from 'react'

import {
    ErrorViewer
} from 'components'

import {
    DocumentForm as Form
} from 'containers/common/forms'

import {
    useProspectQuery
} from 'hooks/business/Prospects'

import {
    useProspectDocumentQuery,
    useProspectDocumentSubmit
} from 'hooks/business/Prospects/Documents'

import DocumentEntity from 'entities/Document'
import DocumentFormValidator from 'validators/ProspectDocumentFormValidator'

function DocumentForm(
    {
        children,
        prospectId,
        documentId,
        onSubmitSuccess,
        onCancel
    }
) {
    const [error, setError] = useState(null)

    const {
        data: prospect
    } = useProspectQuery({ prospectId })

    const {
        organization,
        organizationId
    } = prospect ?? {}

    const {
        data: document,
        refetch: refetchDocument
    } = useProspectDocumentQuery({ documentId }, {
        onError: setError,
        enabled: Boolean(documentId)
    })

    const [submit] = useProspectDocumentSubmit(prospectId, {
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
                entity={DocumentEntity}
                validator={DocumentFormValidator}
                className="ProspectDocumentForm"
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
