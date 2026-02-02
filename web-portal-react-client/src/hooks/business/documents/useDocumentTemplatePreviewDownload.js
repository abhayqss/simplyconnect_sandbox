import { useState, useEffect } from 'react'

import { useMutation } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

import { getDataUrl } from 'lib/utils/Utils'
import { noop } from 'lib/utils/FuncUtils'

const fetch = params => service.downloadTemplatePreviewFile(params)

export default function useDocumentTemplatePreviewDownload(
    {
        clientId,
        templateId,
        documentId,
        templateData
    }, {
        enabled,
        onSuccess = noop,
        onError = noop
    }
) {
    const [data, setData] = useState()

    const {
        mutateAsync: download,
        isLoading: isFetching
    } = useMutation(fetch, {
        throwOnError: true,
    })

    useEffect(() => {
        if (enabled) {
            download({ ...templateData, clientId, templateId, documentId })
                .then(response => {
                    setData({
                        url: getDataUrl(response.data, 'application/pdf'),
                        signatureAreas: response.signatureAreas
                    })
                    onSuccess(response)
                })
                .catch(onError)
        }
    }, [
        enabled,
        download,
        clientId,
        documentId,
        templateId,
        templateData,
        onSuccess,
        onError
    ])

    return {
        data,
        isFetching
    }
}