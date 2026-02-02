import { first, isEmpty } from "underscore";

import CommunitySettingsScheme from "schemes/CommunitySettingsScheme";
import CommunityMarketplaceScheme from "schemes/CommunityMarketplaceScheme";

import { setInObject } from "lib/utils/Utils";

import BaseSchemeValidator from "./BaseSchemeValidator";

const STEPS = {
  SETTINGS: 0,
  MARKETPLACE: 1,
};

const STEPS_SCHEME = {
  [STEPS.SETTINGS]: CommunitySettingsScheme,
  [STEPS.MARKETPLACE]: CommunityMarketplaceScheme,
};

const regex = /\[(?<index>\d+)\]/g;

class CommunityFormValidator extends BaseSchemeValidator {
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

  validate(data, options) {
    let { step } = options.included;

    let scheme = STEPS_SCHEME[step];

    return new Promise((resolve, reject) => {
      scheme
        .validate(data, {
          abortEarly: false,
          context: options,
        })
        .then(() => resolve(true))
        .catch((errors) => reject(this.formatErrors(errors)));
    });
  }
}

export default CommunityFormValidator;
