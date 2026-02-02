import { useMutation } from '@tanstack/react-query'
import service from 'services/DocumentFolderService'

function fetch({ folderId, ...params }) {
    return service.deleteById(folderId, params)
}

export default function useDocumentFolderDeletion(options) {
    return useMutation(fetch, options)
}