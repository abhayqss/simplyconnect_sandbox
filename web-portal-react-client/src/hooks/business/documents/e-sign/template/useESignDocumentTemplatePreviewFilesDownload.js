import {
    useState,
    useEffect
} from 'react'

import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

import { noop } from 'lib/utils/FuncUtils'
import { getDataUrl } from 'lib/utils/Utils'

const fetch = params => service.downloadTemplatePreviewFile(params)

export default function useESignDocumentTemplatePreviewFilesDownload(templates = [], {
    enabled,
    onSuccess = noop,
    onError = noop
}
) {
    const [files, setFiles] = useState(templates.map(o => ({
        isFetching: enabled, templateId: o.templateId
    })))

    const {
        mutateAsync: download
    } = useMutation(fetch, {
        throwOnError: true
    })

    useEffect(() => {
        if (enabled) {
            templates.forEach(({ templateId, ...params }) => {
                download({ templateId, ...params })
                    .then(response => {
                        setFiles(prev => prev.map(o => (
                            o.templateId === templateId ? (
                                {
                                    ...o,
                                    isFetching: false,
                                    signatureAreas: response.signatureAreas,
                                    url: getDataUrl(response.data, 'application/pdf'),
                                }
                            ) : o
                        )))
                        onSuccess(response)
                    })
                    .catch(onError)
            })
        }
    }, [
        enabled,
        download,
        templates,
        onSuccess,
        onError
    ])

    return files
}