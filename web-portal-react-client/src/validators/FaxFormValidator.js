import BaseSchemeValidator from "./BaseSchemeValidator";
import FaxScheme from "schemes/FaxScheme";

class FaxFormValidator extends BaseSchemeValidator {
  constructor() {
    super(FaxScheme);
  }
}

export default FaxFormValidator;
