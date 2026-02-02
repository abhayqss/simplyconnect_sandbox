import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentFolderService'

function download({ folderId, shouldSave = true, ...other }) {
    return service
        .downloadById(folderId, other)
        .then(({ name, data }) => {
            shouldSave && saveAs(data, name)
            return { name, data }
        })
}

function useDocumentFolderDownload(options) {
    return useMutation(download, options)
}

export default useDocumentFolderDownload
