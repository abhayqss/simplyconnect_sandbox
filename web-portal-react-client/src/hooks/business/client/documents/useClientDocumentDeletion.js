import { useMutation } from 'hooks/common'

import service from 'services/ClientDocumentService'

function _delete({ documentId, ...params }) {
    return service.deleteById(documentId, params)
}

export default function useClientDocumentDeletion(params, options) {
    return useMutation(params, _delete, options)
}