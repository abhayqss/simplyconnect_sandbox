import BaseService from './BaseService'
import { PAGINATION } from 'lib/Constants'

const { FIRST_PAGE } = PAGINATION

export class ClientDashboardService extends BaseService {
    findInDevelopmentServicePlan({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/service-plans/in-development`
        })
    }

    findEvents({ clientId, limit = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/recent-events`,
            response: { extractDataOnly: true },
            params: { clientId, limit, ...other }
        })
    }

    findNotes({ clientId, limit = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/recent-notes`,
            response: { extractDataOnly: true },
            params: { clientId, limit, ...other }
        })
    }

    findAssessments({ clientId, page = FIRST_PAGE, size = 10, ...other }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/assessments`,
            params: { clientId, page: page - 1, size, ...other }
        })
    }

    findAssessmentStatistics({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/assessment-statistics`
        })
    }

    findDashboartPermissions({ clientId }) {
        return super.request({
            url: `/clients/${clientId}/dashboard/permissions`,
            mockParams: { id: clientId },
            response: { extractDataOnly: true },
        })
    }
}

export default new ClientDashboardService()