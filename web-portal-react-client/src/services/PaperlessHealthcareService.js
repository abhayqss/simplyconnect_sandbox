import BaseService from './BaseService'

class PaperlessHealthcareService extends BaseService {
    canView() {
        return super.request({
            url: `/paperless-healthcare/can-view`,
            response: { extractDataOnly: true }
        })
    }

    requestDemo(tileName) {
        return super.request({
            method: "POST",
            url: "/paperless-healthcare/demo-request",
            body: { tileName },
            type: 'json'
        })
    }
}

const service = new PaperlessHealthcareService()
export default service