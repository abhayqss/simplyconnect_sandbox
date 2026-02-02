import useList from 'hooks/common/useList'

import service from 'services/ClientMedicalContacts'

const options = {
    isMinmal: true,
    doLoad: params => service.find(params),
}

function useMedicalContactList(params) {
    return useList('MEDICAL_CONTACT', params, options)
}

export default useMedicalContactList
