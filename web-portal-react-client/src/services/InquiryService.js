import BaseService from './BaseService'

export class InquiryService extends BaseService {
    findInquiryById(inquiryId, params) {
        return super.request({
            url: `/inquiries/${inquiryId}`,
            response: { extractDataOnly: true },
            params
        })
    }

    markAsDone(data, { inquiryId}) {
        return super.request({
            method: 'POST',
            url: `/inquiries/${inquiryId}/mark-as-done`,
            body: data,
            type: 'application/json',
            responseTimeout: 1200000,
            response: { extractDataOnly: true }
        })
    }
}

export default new InquiryService()