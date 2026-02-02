import { useContext } from 'react'
import { useMutation } from '@tanstack/react-query'

import { SignatureRequestContext } from 'contexts'

import service from 'services/DocumentESignService'

import { E_SIGN_REQUEST_STEPS } from 'lib/Constants'

const {
    SIGNATURE_REQUEST,
    DOCUMENT_TEMPLATE,
    DOCUMENT_TEMPLATE_PREVIEW,
    DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE,
    MULTIPLE_SIGNATURE_REQUEST
} = E_SIGN_REQUEST_STEPS

const Fetchers = {
    [SIGNATURE_REQUEST]: data => Promise.resolve(data),
    [DOCUMENT_TEMPLATE]: data => Promise.resolve(data),
    [DOCUMENT_TEMPLATE_PREVIEW]: data => service.submitSignatureRequest(data),
    [DOCUMENT_TEMPLATE_PREVIEW_MULTIPLE]: data => service.submitSignatureRequest(data),
    [MULTIPLE_SIGNATURE_REQUEST]: data => Promise.resolve(data)
}

function useESignRequestSubmit(options) {
    const { step } = useContext(SignatureRequestContext)

    return useMutation(Fetchers[step], options)
}

export default useESignRequestSubmit
