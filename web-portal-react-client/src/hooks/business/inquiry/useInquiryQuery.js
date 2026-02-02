import { useQuery } from '@tanstack/react-query'

import service from 'services/InquiryService'

const fetch = ({ inquiryId, ...params }) => service.findInquiryById(inquiryId, params)

function useInquiryQuery(params, options) {
    return useQuery(['InquiryDetails', params], () => fetch(params), options)
}

export default useInquiryQuery
