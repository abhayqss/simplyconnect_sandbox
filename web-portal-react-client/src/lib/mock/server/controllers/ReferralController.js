import Response from '../Response'
import Controller from './Controller'

import dao from '../dao/ReferralDao'

class ReferralController extends Controller {
    getPath() {
        return '/*/referrals'
    }

    getHandlers() {
        return [
            {
                path: '',
                handler: (vars, params) => {
                    return Response.success(
                        dao.find(params), { totalCount: dao.count() }
                    )
                }
            },
            {
                path: '/:referralId',
                handler: () => {
                    return Response.success(dao.findById())
                }
            },
            {
                path: '/:referralId/responses',
                handler: (vars, params) => {
                    return Response.success(
                        dao.findResponses(params),
                        { totalCount: dao.responseCount() }
                    )
                }
            },
            {
                path: '/count',
                handler: () => {
                    return Response.success(dao.count())
                }
            }
        ]
    }
}

export default new ReferralController()