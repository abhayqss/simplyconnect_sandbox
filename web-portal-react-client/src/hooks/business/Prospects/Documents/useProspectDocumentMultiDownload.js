import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/ProspectDocumentService'

function download({ prospectId, ...params }) {
    return service
        .downloadMultiple({ prospectId, ...params })
        .then(({ name, data }) => {
            saveAs(data, name)
        })
}

function useProspectDocumentMultiDownload(options) {
    return useMutation(download, options)
}

export default useProspectDocumentMultiDownload
