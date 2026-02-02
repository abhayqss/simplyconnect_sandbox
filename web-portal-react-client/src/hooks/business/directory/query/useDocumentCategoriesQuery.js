import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findDocumentCategories(params)

export default function useDocumentCategoriesQuery(params, options) {
    return useQuery(['Directory.DocumentCategories', params], () => fetch(params), options)
}