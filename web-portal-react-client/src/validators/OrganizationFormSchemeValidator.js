import { first, isEmpty } from "underscore";

import OrganizationLegalInfoScheme from "schemes/OrganizationLegalInfoScheme";
import OrganizationMarketplaceScheme from "schemes/OrganizationMarketplaceScheme";
import OrganizationFeaturesScheme from "schemes/OrganizationFeaturesScheme";
import OrganizationAffiliateRelationshipScheme from "schemes/OrganizationAffiliateRelationshipScheme";

import { setInObject } from "lib/utils/Utils";

import BaseSchemeValidator from "./BaseSchemeValidator";

const STEPS = {
  LEGAL_INFO: 0,
  MARKETPLACE: 1,
  FEATURES: 2,
  AFFILIATE_RELATIONSHIP: 3,
};

const STEPS_SCHEME = {
  [STEPS.LEGAL_INFO]: OrganizationLegalInfoScheme,
  [STEPS.MARKETPLACE]: OrganizationMarketplaceScheme,
  [STEPS.FEATURES]: OrganizationFeaturesScheme,
  [STEPS.AFFILIATE_RELATIONSHIP]: OrganizationAffiliateRelationshipScheme,
};

const regex = /\[(?<index>\d+)\]/g;

class OrganizationFormSchemeValidator extends BaseSchemeValidator {
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
    let { step } = options;

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

export default OrganizationFormSchemeValidator;
