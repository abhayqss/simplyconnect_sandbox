import { useMutation } from '@tanstack/react-query'
import service from 'services/DocumentService'

function fetch({ documentId, ...params }) {
    return service.deleteById(documentId, params)
}

export default function useDocumentDeletion(options) {
    return useMutation(fetch, options)
}