import { each, last, initial, isFunction } from "underscore";

import { setProperty } from "lib/utils/ObjectUtils";

import { ACTION_TYPES } from "lib/Constants";

const { List, Record, hash, fromJS, isKeyed } = require("immutable");

export function getActionTypes(entity) {
  return ActionTypes(entity);
}

export function ActionTypes(entity) {
  return {
    CLEAR: `CLEAR_${entity}_FORM`,
    CLEAR_ERROR: `CLEAR_${entity}_FORM_ERROR`,
    SUBMIT_REQUEST: `SUBMIT_${entity}_FORM_REQUEST`,
    SUBMIT_SUCCESS: `SUBMIT_${entity}_FORM_SUCCESS`,
    SUBMIT_FAILURE: `SUBMIT_${entity}_FORM_FAILURE`,
  };
}

const { LOGOUT_SUCCESS, CLEAR_ALL_AUTH_DATA } = ACTION_TYPES;

export function clearFieldErrors(fields) {
  const it = fields.entries();

  let entry = it.next();

  while (!entry.done) {
    const [k, v] = entry.value;

    if (k.includes("HasError") || k.includes("ErrorCode") || k.includes("ErrorText")) {
      fields = fields.remove(k);
    }

    if (Record.isRecord(v)) {
      fields = fields.set(k, clearFieldErrors(v));
    }

    if (List.isList(v)) {
      for (let i = 0; i < v.size; i++) {
        fields = fields.setIn([k, i], clearFieldErrors(v.get(i)));
      }
    }

    entry = it.next();
  }

  return fields;
}

export function updateFieldErrors(fields, errors) {
  fields = clearFieldErrors(fields);

  each(errors, (errors, fieldName) => {
    const parts = fieldName.split(".");

    const postfix = last(parts);
    const path = parts.length > 1 ? initial(parts) : [];

    fields = fields.setIn([...path, `${postfix}HasError`], true).setIn([...path, `${postfix}ErrorText`], errors[0]);
  });

  return fields;
}

export function Actions({ actionTypes = {}, doSubmit, doValidate, shouldThrowError = false }) {
  const {
    CLEAR,
    CLEAR_ERROR,
    CLEAR_FIELD,
    CLEAR_FIELD_ERROR,
    CHANGE_FIELD,
    CHANGE_FIELDS,
    VALIDATE,
    SUBMIT_REQUEST,
    SUBMIT_SUCCESS,
    SUBMIT_FAILURE,
  } = actionTypes;

  return {
    clear: () => ({ type: CLEAR }),
    clearError: () => ({ type: CLEAR_ERROR }),
    clearFieldError: (name) => ({
      type: CLEAR_FIELD_ERROR,
      payload: name,
    }),
    clearField: (name) => ({
      type: CLEAR_FIELD,
      payload: name,
    }),
    changeField: (name, value) => {
      return (dispatch) => {
        dispatch({
          type: CHANGE_FIELD,
          payload: { name, value },
        });
      };
    },
    changeFields: (changes, shouldUpdateHashCode) => {
      return (dispatch) => {
        dispatch({
          type: CHANGE_FIELDS,
          payload: {
            changes,
            shouldUpdateHashCode,
          },
        });
      };
    },
    ...(isFunction(doValidate) && {
      validate: (data, options) => {
        return (dispatch) => {
          return doValidate(data, options)
            .then(() => {
              dispatch({ type: VALIDATE, payload: { success: true } });
              return true;
            })
            .catch((errors) => {
              dispatch({ type: VALIDATE, payload: { success: false, errors } });
              return false;
            });
        };
      },
    }),
    ...(isFunction(doSubmit) && {
      submit: (...args) => {
        return (dispatch) => {
          dispatch({ type: SUBMIT_REQUEST });
          return doSubmit(...args)
            .then((response) => {
              dispatch({ type: SUBMIT_SUCCESS, payload: response });
              return response;
            })
            .catch((error) => {
              dispatch({ type: SUBMIT_FAILURE, payload: error });

              if (shouldThrowError) throw error;
              else return error;
            });
        };
      },
    }),
  };
}

export function State(state = {}) {
  let hashCode = hash();

  return Record({
    error: null,
    isFetching: false,
    validation: Record({
      isSuccess: true,
      errors: Record({}),
    }),
    fields: Record({}),
    ...state,
    getHashCode() {
      return hashCode;
    },
    updateHashCode() {
      hashCode = this.fields?.hashCode();

      return this;
    },
    updateHashCodeIf(condition = false) {
      return condition ? this.updateHashCode() : this;
    },
    isChanged() {
      return this.fields?.hashCode() !== hashCode;
    },
    setError(e) {
      return this.set("error", e);
    },
    clearError() {
      return this.remove("error");
    },
    setFetching(isFetching = false) {
      return this.set("isFetching", isFetching);
    },
    changeField(name, value, shouldUpdateHashCode = false) {
      return this.setIn(["fields", ...name.split(".")], value).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeFields(changes, shouldUpdateHashCode = false) {
      return this.mergeDeep({
        fields: changes,
      }).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeFieldDeep(path = [], value) {
      return this.setIn(["fields", ...path], value);
    },
    isValid() {
      return this.validation.isSuccess;
    },
    setValid(isValid = false) {
      return this.setIn(["validation", "isSuccess"], isValid);
    },
    clearValidation() {
      return this.set("validation", this.validation.clear());
    },
    clearField(name) {
      return this.removeIn(["fields", ...name.split(".")]);
    },
    clearFieldError(name) {
      return this.removeIn(["validation", "errors", name]);
    },
    setFieldErrors(errors) {
      let errorFields = {};

      each(errors, (errorMessages, fieldName) => {
        errorFields = setProperty(errorFields, fieldName, last(errorMessages));
      });

      return this.setIn(["validation", "errors"], this.validation.errors.clear()).mergeDeepIn(
        ["validation", "errors"],
        errorFields,
      );
    },
    fromJS(data, struct) {
      return fromJS(data, function reviver(key, value) {
        if (key === "") {
          return isKeyed(value) ? value.toObject() : value.toSet();
        }

        return isKeyed(value) ? value.toObject() : value.toSet();
      });
    },
  });
}

export function Reducer({ stateClass, actionTypes = {} }) {
  const {
    CLEAR,
    CLEAR_ERROR,
    CLEAR_FIELD,
    CLEAR_FIELD_ERROR,
    CHANGE_FIELD,
    CHANGE_FIELDS,
    VALIDATE,
    SUBMIT_REQUEST,
    SUBMIT_SUCCESS,
    SUBMIT_FAILURE,
  } = actionTypes;

  const initialState = new stateClass();

  return function reducer(state = initialState, action) {
    if (!(state instanceof stateClass)) {
      return initialState.mergeDeep(state);
    }

    switch (action.type) {
      case CLEAR:
      case LOGOUT_SUCCESS:
      case CLEAR_ALL_AUTH_DATA:
        return state.clear();

      case CLEAR_ERROR:
        return state.clearError();

      case CLEAR_FIELD_ERROR:
        return state.clearFieldError(action.payload);

      case CHANGE_FIELD:
        return state.changeField(action.payload.name, action.payload.value);

      case CLEAR_FIELD:
        return state.clearField(action.payload);

      case CHANGE_FIELDS:
        return state.changeFields(action.payload.changes, action.payload.shouldUpdateHashCode);

      case VALIDATE:
        return state.setValid(action.payload.success).setFieldErrors(action.payload.errors);

      case SUBMIT_REQUEST:
        return state.clearError().setFetching(true);

      case SUBMIT_SUCCESS:
        return state.setFetching(false);

      case SUBMIT_FAILURE:
        return state.setFetching(false).setError(action.payload);
    }

    return state;
  };
}
