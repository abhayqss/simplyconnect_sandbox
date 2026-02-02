import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

function fetch({ folderId }) {
    return service.restoreById(folderId)
}

export default function useDocumentFolderRestoration(options) {
    return useMutation(fetch, options)
}