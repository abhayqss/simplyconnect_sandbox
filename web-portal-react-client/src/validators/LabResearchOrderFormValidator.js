import LabResearchOrderScheme from 'schemes/LabResearchOrderScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class LabResearchOrderFormValidator extends BaseSchemeValidator {
    constructor() {
        super(LabResearchOrderScheme)
    }
}

export default LabResearchOrderFormValidator
