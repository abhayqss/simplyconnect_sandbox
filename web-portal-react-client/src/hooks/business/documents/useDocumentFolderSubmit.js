import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

function useDocumentFolderSubmit(options) {
    return useMutation(data => service.save(data), options)
}

export default useDocumentFolderSubmit
