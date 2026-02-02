import { useReducer, useCallback, useMemo } from "react";
import { bindActionCreators } from "redux";
import { each, last, isFunction, isEmpty } from "underscore";

import { setProperty } from "lib/utils/ObjectUtils";

import useValidation from "./useValidation";

const { Record, hash, fromJS, isKeyed, isIndexed } = require("immutable");

const getImmutableStruct = (o) => (isFunction(o) ? o() : Record(o)());

function ActionTypes(enity) {
  return {
    CLEAR: `CLEAR_${enity}_FORM`,
    CLEAR_ERROR: `CLEAR_${enity}_FORM_ERROR`,
    CLEAR_FIELD: `CLEAR_${enity}_FORM_FIELD`,
    CLEAR_FIELDS: `CLEAR_${enity}_FORM_FIELDS`,
    CLEAR_FIELD_ERROR: `CLEAR_${enity}_FORM_FIELD_ERROR`,
    CHANGE_FIELD: `CHANGE_${enity}_FORM_FIELD`,
    CHANGE_FIELDS: `CHANGE_${enity}_FORM_FIELDS`,
  };
}

function getActionCreators(actionTypes) {
  const { CLEAR, CLEAR_FIELD, CLEAR_ERROR, CLEAR_FIELDS, CLEAR_FIELD_ERROR, CHANGE_FIELD, CHANGE_FIELDS } = actionTypes;

  return {
    clear: () => ({ type: CLEAR }),
    clearError: () => ({ type: CLEAR_ERROR }),
    clearField: (name) => ({
      type: CLEAR_FIELD,
      payload: name,
    }),
    clearFieldError: (name) => ({
      type: CLEAR_FIELD_ERROR,
      payload: name,
    }),
    changeField: (name, value, shouldUpdateHashCode) => ({
      type: CHANGE_FIELD,
      payload: { name, value, shouldUpdateHashCode },
    }),
    changeFields: (changes, shouldUpdateHashCode) => ({
      type: CHANGE_FIELDS,
      payload: {
        changes,
        shouldUpdateHashCode,
      },
    }),
    clearFields: (...names) => ({
      type: CLEAR_FIELDS,
      payload: { names },
    }),
  };
}

function State(fields = {}) {
  let hashCode = hash();

  return Record({
    error: null,
    isFetching: false,
    fields: getImmutableStruct(fields),
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
    clearField(name) {
      return this.removeIn(["fields", ...name?.split(".")]);
    },
    setFetching(isFetching = false) {
      return this.set("isFetching", isFetching);
    },
    clearFields(...names) {
      let state = this;
      const cleared = this.clear();

      each(names, (name) => {
        const path = ["fields", ...name?.split(".")];
        state = state.setIn(path, cleared.getIn(path));
      });

      return state;
    },
    changeField(name, value, shouldUpdateHashCode = false) {
      return this.setIn(["fields", ...name?.split(".")], this.fromJS(value)).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeFields(changes, shouldUpdateHashCode = false) {
      return this.mergeDeep({
        fields: this.fromJS(changes),
      }).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeFieldDeep(path = [], value) {
      return this.setIn(["fields", ...path], value);
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
        if (isKeyed(value)) return value.toObject();
        if (isIndexed(value)) return value.toList();
        return value.toSet();
      });
    },
  })().updateHashCode();
}

function Reducer(actionTypes) {
  return function reducer(state, action) {
    const { CLEAR, CLEAR_ERROR, CLEAR_FIELD, CLEAR_FIELDS, CLEAR_FIELD_ERROR, CHANGE_FIELD, CHANGE_FIELDS } =
      actionTypes;

    switch (action.type) {
      case CLEAR:
        return state.clear();

      case CLEAR_ERROR:
        return state.clearError();

      case CLEAR_FIELD:
        return state.clearField(action.payload);

      case CLEAR_FIELD_ERROR:
        return state.clearFieldError(action.payload);

      case CHANGE_FIELD:
        return state.changeField(action.payload.name, action.payload.value, action.payload.shouldUpdateHashCode);

      case CHANGE_FIELDS:
        return state.changeFields(action.payload.changes, action.payload.shouldUpdateHashCode);

      case CLEAR_FIELDS:
        return state.clearFields(...action.payload.names);
    }

    return state;
  };
}

function useForm(name = "", entity, validator) {
  const actionTypes = useMemo(() => ActionTypes(name), [name]);
  let actions = useMemo(() => getActionCreators(actionTypes), [actionTypes]);

  const [validate, errors, setErrors] = useValidation(validator);
  const [state, dispatch] = useReducer(Reducer(actionTypes), State(entity));

  actions = useMemo(() => bindActionCreators(actions, dispatch), [actions]);

  const { fields, isFetching } = state;

  const isValid = isEmpty(errors);
  const isChanged = state.isChanged();

  function doValidate(options) {
    return validate(fields.toJS(), options);
  }

  //@deprecated
  const changeDateField = useCallback(
    (name, date) => {
      const value = date ? new Date(date).getTime() : null;
      actions.changeField(name, value);
    },
    [actions],
  );

  //@deprecated
  const changeSelectField = useCallback(
    (name, value) => {
      actions.changeField(name, value);
    },
    [actions],
  );

  return {
    fields,
    errors,
    isFetching,
    isChanged,
    isValid,
    setErrors,
    validate: useCallback(doValidate, [fields, validate]),

    ...actions,
    changeDateField,
    changeSelectField,
  };
}

export default useForm;
