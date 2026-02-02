import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/ClientDocumentService'

function download({ clientId, ...params }) {
    return service
        .downloadMultiple({ clientId, ...params })
        .then(({ name, data }) => {
            saveAs(data, name)
        })
}

function useClientDocumentMultiDownload(options) {
    return useMutation(download, options)
}

export default useClientDocumentMultiDownload
