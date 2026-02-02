import Response from '../Response'
import Controller from './Controller'

import dao from '../dao/LabResearchOrderDao'

class LabResearchOrderController extends Controller {
    getPath() {
        return '*/lab-research/orders'
    }

    getHandlers() {
        return [
            {
                path: '/validate-uniq-in-organization',
                handler: () => {
                    return Response.success(dao.validateUniqInOrganization())
                }
            }
        ]
    }
}

export default new LabResearchOrderController()