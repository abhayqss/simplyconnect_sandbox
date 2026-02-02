import useList from 'hooks/common/useList'

import service from 'services/DirectoryService'

const options = {
    doLoad: ({ excludeCommunityId }) => {
        return service.findTreatmentServicesInUse({ excludeCommunityId })
    },
    isMinimal: true,
}

function useTreatmentServiceList(params) {
    return useList('TREATMENT_SERVICE', params, options)
}

export default useTreatmentServiceList
