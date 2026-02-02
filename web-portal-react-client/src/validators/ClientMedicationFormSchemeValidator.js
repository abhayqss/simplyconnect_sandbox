import BaseSchemeValidator from "./BaseSchemeValidator";
import ClientMedicationScheme from "../schemes/ClientMedicationScheme";

class ClientMedicationFormSchemeValidator extends BaseSchemeValidator {
  constructor() {
    super(ClientMedicationScheme);
  }
}

export default ClientMedicationFormSchemeValidator;
