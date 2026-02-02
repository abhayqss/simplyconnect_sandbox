import { saveAs } from 'file-saver'
import { useMutation } from 'hooks/common'

import service from 'services/ProspectDocumentService'

function download({ documentId, shouldSave = true, ...other }) {
    return service
        .downloadById(documentId, other)
        .then(({ name, data }) => {
            shouldSave && saveAs(data, name)
            return { name, data }
        })
}

function useProspectDocumentDownload(params, options) {
    return useMutation(params, download, options)
}

export default useProspectDocumentDownload
