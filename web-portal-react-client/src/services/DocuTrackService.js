import BaseService from './BaseService'

export class DocuTrackService extends BaseService {
    findBusinessUnitCodes(params) {
        return super.request({
            url: '/docutrack/business-unit-codes',
            response: { extractDataOnly: true },
            params
        })
    }

    findSupportedFileTypes(params) {
        return super.request({
            url: '/docutrack/suppotred-file-types',
            response: { extractDataOnly: true },
            params
        })
    }
}

const service = new DocuTrackService()
export default service