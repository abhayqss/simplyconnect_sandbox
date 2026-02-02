import React from 'react'

import { Redirect } from 'react-router-dom'

import { useQueryParams } from 'hooks/common'

import { path } from 'lib/utils/ContextUtils'

function DocumentSigned() {
    const {
        clientId,
        documentId
    } = useQueryParams()

    return (clientId && documentId) ? (
        <Redirect
            to={path(`/clients/${clientId}/documents?signedDocumentId=${documentId}`)}
        />
    ) : (
        <Redirect to={path('/clients')} />
    )
}

export default DocumentSigned
