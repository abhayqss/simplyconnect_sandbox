import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentFolderAggregatedService'

function download(params) {
    return service
        .downloadMultiple(params)
        .then(({ name, data }) => {
            saveAs(data, name)
        })
}

function useDocumentMultiDownload(options) {
    return useMutation(download, options)
}

export default useDocumentMultiDownload
