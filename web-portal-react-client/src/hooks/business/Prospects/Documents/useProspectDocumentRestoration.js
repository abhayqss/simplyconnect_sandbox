import { useMutation } from 'hooks/common'
import service from 'services/ProspectDocumentService'

function restore({ prospectId, documentId }) {
    return service.restoreById(documentId, { prospectId })
}

export default function useProspectDocumentRestoration(params, options) {
    return useMutation(params, restore, options)
}