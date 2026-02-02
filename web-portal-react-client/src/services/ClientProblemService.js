import BaseService from './BaseService'

export class ClientProblemService extends BaseService {
    find(clientId, params) {
        return super.request({
            url: `/clients/${clientId}/problems`,
            params,
        })
    }

    findById(clientId, { problemId }) {
        return super.request({
            url: `/clients/${clientId}/problems/${problemId}`
        })
    }

    count(clientId) {
        return super.request({
            url: `/clients/${clientId}/problems/count`
        })
    }

    findStatisticsById(clientId) {
        return super.request({
            response: { extractDataOnly: true },
            url: `/clients/${clientId}/problems/statistics`
        })
    }
}

const service = new ClientProblemService()

export default service