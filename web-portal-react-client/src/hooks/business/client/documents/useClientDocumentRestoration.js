import { useMutation } from 'hooks/common'

import service from 'services/ClientDocumentService'

function restore({ clientId, documentId }) {
    return service.restoreById(documentId, { clientId })
}

export default function useClientDocumentRestoration(params, options) {
    return useMutation(params, restore, options)
}