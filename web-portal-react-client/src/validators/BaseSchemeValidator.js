/* eslint-disable no-template-curly-in-string */
import { array, setLocale, addMethod } from "yup";
import { first, isEmpty, size, intersection } from "underscore";

import { setInObject } from "lib/utils/Utils";
import { VALIDATION_ERROR_TEXTS } from "lib/Constants";

const { FILE_SIZE, EMPTY_FIELD, LENGTH_EQUAL, EMAIL_FORMAT, LENGTH_MAXIMUM, LENGTH_MINIMUM } = VALIDATION_ERROR_TEXTS;

export default class BaseSchemeValidator {
  constructor(scheme) {
    this.scheme = scheme;
  }

  static formatMessage(message, keys, replacers) {
    return keys.reduce((currentMessage, key, index) => {
      return currentMessage.replace(key, replacers[index]);
    }, message);
  }

  static setup() {
    setLocale({
      mixed: {
        required: EMPTY_FIELD,
      },
      string: {
        email: EMAIL_FORMAT,
        max: this.formatMessage(LENGTH_MAXIMUM, ["$0"], ["${max}"]),
        min: this.formatMessage(LENGTH_MINIMUM, ["$0"], ["${min}"]),
        length: this.formatMessage(LENGTH_EQUAL, ["$0"], ["${length}"]),
      },
      array: {
        required: EMPTY_FIELD,
      },
      number: {
        max: this.formatMessage(FILE_SIZE, ["$0"], ["${max}"]),
      },
    });

    addMethod(array, "uniq", function (message, mapper = (a) => a) {
      return this.test("uniq", message, function (list) {
        return list.length === new Set(list.map(mapper)).size;
      });
    });

    addMethod(array, "noIntersectionWith", function (array, message) {
      return this.test("noIntersectionWith", message, function (list) {
        return size(intersection(array, list)) === 0;
      });
    });
  }

  formatErrors({ inner }) {
    return inner?.reduce((errors, validationError) => {
      const error = isEmpty(validationError.inner)
        ? first(validationError.errors)
        : this.formatErrors(validationError.inner);

      setInObject(errors, validationError.path, error);

      return errors;
    }, {});
  }

  validate(data, options) {
    return new Promise((resolve, reject) => {
      this.scheme
        .validate(data, {
          abortEarly: false,
          context: options,
        })
        .then(() => resolve(true))
        .catch((errors) => reject(this.formatErrors(errors)));
    });
  }
}
