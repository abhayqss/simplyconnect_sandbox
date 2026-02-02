import { useMutation } from 'hooks/common'

import service from 'services/ProspectDocumentService'

function _delete({ documentId, ...params }) {
    return service.deleteById(documentId, params)
}

export default function useProspectDocumentDeletion(params, options) {
    return useMutation(params, _delete, options)
}