import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentTemplateService'

const fetch = ({ templateId, ...params }) => service.findById(templateId, params)

export default function useDocumentTemplateQuery(params, options) {
    return useQuery(['DocumentTemplate', params], () => fetch(params), options)
}