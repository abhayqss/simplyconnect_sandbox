import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

function fetch(params) {
    return service.findTemplateById(params)
}

function useESignDocumentTemplateQuery(params, options) {
    return useQuery(['ESignDocumentTemplate', params], () => fetch(params), options)
}

export default useESignDocumentTemplateQuery
