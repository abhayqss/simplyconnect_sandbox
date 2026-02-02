import useList from 'hooks/common/useList'

import service from 'services/ClientAllergyService'

const options = {
    doLoad: params => service.find(params)
}

function useAllergyList(params) {
    return useList('CLIENT_ALLERGY', params, options)
}

export default useAllergyList
