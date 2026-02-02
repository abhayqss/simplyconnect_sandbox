import { useQuery } from '@tanstack/react-query'

import service from 'services/DirectoryService'

const fetch = params => service.findAssessmentTypes(
    params, { response: { extractDataOnly: true } }
)

function useAssessmentTypesQuery(params, options) {
    return useQuery(['Directory.AssessmentTypes', params], () => fetch(params), options)
}

export default useAssessmentTypesQuery
