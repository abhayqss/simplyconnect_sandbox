import BaseService from './BaseService'

export class SupportTicketsService extends BaseService {
    save(data) {
        return super.request({
            method: data.id ? 'PUT' : 'POST',
            url: `/support-tickets`,
            body: data,
            type: 'multipart/form-data',
            responseTimeout: 1200000
        })
    }
}

export default new SupportTicketsService()
