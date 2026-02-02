import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentTemplateService'

function download(params) {
    return service
        .downloadMultiple(params)
        .then(({ name, data }) => {
            saveAs(data, name)
        })
}

function useDocumentTemplateMultiDownload(options) {
    return useMutation(download, options)
}

export default useDocumentTemplateMultiDownload
