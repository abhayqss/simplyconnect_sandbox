
import adminVendorService from 'services/AdminVendorService'

const findCompanyType = (params) => (
    adminVendorService.findVendorCompanyType(params,
        {
        response: { extractDataOnly: true }
    }
    )
)

function useCompanyTypeQuery(params, options) {
    return findCompanyType(params, options)
}

export default useCompanyTypeQuery
