import { saveAs } from 'file-saver'
import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentTemplateService'

function download({ templateId, shouldSave = true, ...other }) {
    return service
        .downloadById(templateId, other)
        .then(({ name, data }) => {
            shouldSave && saveAs(data, name)
            return { name, data }
        })
}

function useDocumentTemplateDownload(options) {
    return useMutation(download, options)
}

export default useDocumentTemplateDownload
