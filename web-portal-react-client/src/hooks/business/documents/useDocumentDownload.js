import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentService'

function download({ documentId, shouldSave = true, ...other }) {
    return service
        .downloadById(documentId, other)
        .then(({ name, data }) => {
            shouldSave && saveAs(data, name)
            return { name, data }
        })
}

function useDocumentDownload(options) {
    return useMutation(download, options)
}

export default useDocumentDownload
