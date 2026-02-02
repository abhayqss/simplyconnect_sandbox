import { first, isEmpty } from "underscore";

import BaseSchemeValidator from "./BaseSchemeValidator";
import IncidentReportScheme from "schemes/IncidentReportScheme";

import { setInObject } from "lib/utils/Utils";

const regex = /\[(?<index>\d+)\]/g;

class IncidentReportValidator extends BaseSchemeValidator {
  constructor() {
    super(IncidentReportScheme);
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

export default IncidentReportValidator;
