import ShortenClientScheme from 'schemes/ShortenClientScheme'
import BaseSchemeValidator from './BaseSchemeValidator'

class ClientEssentialsFormValidator extends BaseSchemeValidator {
    constructor() {
        super(ShortenClientScheme)
    }
}

export default ClientEssentialsFormValidator
