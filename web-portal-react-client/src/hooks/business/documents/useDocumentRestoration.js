import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentService'

function fetch({ documentId }) {
    return service.restoreById(documentId)
}

export default function useDocumentRestoration(options) {
    return useMutation(fetch, options)
}