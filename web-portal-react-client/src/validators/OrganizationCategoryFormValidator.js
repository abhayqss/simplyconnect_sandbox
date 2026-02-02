import OrganizationCategoryScheme from 'schemes/OrganizationCategoryScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class OrganizationCategoryFormValidator extends BaseSchemeValidator {
    constructor() {
        super(OrganizationCategoryScheme)
    }
}

export default OrganizationCategoryFormValidator
