import React from 'react'

import { Redirect } from 'react-router-dom'

import { useQueryParams } from 'hooks/common'

import { path } from 'lib/utils/ContextUtils'

function SignatureNotification() {
    const {
        clientId,
        requestId
    } = useQueryParams()

    return (clientId && requestId) ? (
        <Redirect
            to={path(`/clients/${clientId}/documents?signatureRequestId=${requestId}`)}
        />
    ) : (
        <Redirect to={path('/clients')} />
    )
}

export default SignatureNotification
