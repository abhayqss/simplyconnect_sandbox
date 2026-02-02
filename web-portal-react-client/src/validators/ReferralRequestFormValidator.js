import { first, isEmpty } from "underscore";

import ReferralRequestScheme from "schemes/ReferralRequestScheme";

import { setInObject } from "lib/utils/Utils";

import BaseSchemeValidator from "./BaseSchemeValidator";

const regex = /\[(?<index>\d+)\]/g;

class ReferralRequestFormValidator extends BaseSchemeValidator {
  constructor() {
    super(ReferralRequestScheme);
  }

  formatErrors({ inner }) {
    return inner?.reduce((errors, validationError) => {
      const error = isEmpty(validationError.inner)
        ? first(validationError.errors)
        : this.formatErrors(validationError.inner);

      let path = validationError.path.replace(regex, ".$<index>");

      setInObject(errors, path, error);

      return errors;
    }, {});
  }
}

export default ReferralRequestFormValidator;
