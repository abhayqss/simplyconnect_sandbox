import { useMemo, useState, useEffect, useReducer, useCallback } from "react";

import { noop, isFunction } from "underscore";

import { bindActionCreators } from "redux";

import { useRefCurrent, useValidation } from "hooks/common";

import { useBaseCustomFilter } from "./";

const { hash, Record } = require("immutable");

const CLEAR = "CLEAR_FIELDS";
const RESET = "RESET_FIELDS";
const CHANGE_FIELD = "CHANGE_FIELD";
const CHANGE_FIELDS = "CHANGE_FIELDS";
const UPDATE_HASH_CODE = "UPDATE_HASH_CODE";

const Immutable = (o) => (isFunction(o) ? o() : Record(o)());

function getActions() {
  return {
    clear: (shouldUpdateHashCode) => ({
      type: CLEAR,
      payload: { shouldUpdateHashCode },
    }),
    reset: (data, shouldUpdateHashCode) => ({
      type: RESET,
      payload: { data, shouldUpdateHashCode },
    }),
    changeField: (name, value, shouldUpdateHashCode) => ({
      type: CHANGE_FIELD,
      payload: { name, value, shouldUpdateHashCode },
    }),
    changeFields: (changes, shouldUpdateHashCode) => ({
      type: CHANGE_FIELDS,
      payload: { changes, shouldUpdateHashCode },
    }),
    updateHashCode: () => ({ type: UPDATE_HASH_CODE }),
  };
}

function State(fields = {}) {
  let hashCode = hash();

  return Record({
    fields: Immutable(fields),
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
    reset(data, shouldUpdateHashCode = false) {
      return this.clear().mergeIn(["fields"], data).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeField(name, value, shouldUpdateHashCode = false) {
      return this.setIn(["fields", ...name.split(".")], value).updateHashCodeIf(shouldUpdateHashCode);
    },
    changeFields(changes, shouldUpdateHashCode = false) {
      return this.mergeIn(["fields"], changes).updateHashCodeIf(shouldUpdateHashCode);
    },
  })().updateHashCode();
}

function Reducer(extReducer) {
  return function reducer(state, action) {
    let nextState = state;

    switch (action.type) {
      case CLEAR: {
        nextState = state.clear().updateHashCodeIf(action.payload.shouldUpdateHashCode);
        break;
      }

      case RESET: {
        nextState = state.reset(action.payload.data, action.payload.shouldUpdateHashCode);
        break;
      }

      case CHANGE_FIELD: {
        nextState = state.changeField(action.payload.name, action.payload.value, action.payload.shouldUpdateHashCode);
        break;
      }

      case CHANGE_FIELDS: {
        nextState = state.changeFields(action.payload.changes, action.payload.shouldUpdateHashCode);
        break;
      }

      case UPDATE_HASH_CODE: {
        nextState = state.updateHashCode();
        break;
      }
    }

    if (extReducer) {
      return extReducer(nextState, action) ?? nextState;
    }

    return nextState;
  };
}

const EVENT_APPLY = "APPLY";
const EVENT_RESET = "RESET";
const EVENT_CLEAR = "CLEAR";
const EVENT_RESTORE = "RESTORE";

function Event(type, data = []) {
  return { type, data };
}

Event.Apply = function (data) {
  return Event(EVENT_APPLY, data);
};

Event.Reset = function (data) {
  return Event(EVENT_RESET, data);
};

Event.Clear = function (data) {
  return Event(EVENT_CLEAR, data);
};

Event.Restore = function (data) {
  return Event(EVENT_RESTORE, data);
};

export default function useCustomFilter(
  name,
  entity,
  {
    Validator = null,
    canReApply = false,
    canReReset = false,
    onApply = noop,
    onClear = noop,
    onRestore = noop,
    onReset = noop,
    onChange = noop,
    onPressEnterKey,
    getDefaultData = noop,
  },
) {
  const [events, setEvents] = useState([]);

  function addEvent(e) {
    setEvents([...events, e]);
  }

  const options = useRefCurrent({ onChange });

  let actions = useMemo(() => getActions(), []);

  const [state, dispatch] = useReducer(
    Reducer((state, action) => {
      if (action.type !== UPDATE_HASH_CODE) {
        options.onChange(state.fields.toJS(), state.isChanged());
      }
    }),
    State(entity),
  );

  actions = useMemo(() => bindActionCreators(actions, dispatch), [actions]);

  const [validate, errors] = useValidation(Validator);

  const { fields } = state;
  const isChanged = state.isChanged();
  const data = useMemo(() => fields.toJS(), [fields]);

  const { save, blur, focus, reset, apply, remove, isSaved } = useBaseCustomFilter(name, data, {
    onPressEnterKey,
    onClear: () => {
      actions.clear(true);
      addEvent(Event.Clear([isSaved()]));
      isSaved() && remove();
    },
    onRestore: (data) => {
      if (isSaved()) {
        actions.reset(data);
        addEvent(Event.Restore([data]));
      }
    },
    onApply: () => {
      if (isChanged || canReApply) {
        save();
        actions.updateHashCode();
        addEvent(Event.Apply());
      }
    },
    onReset: (data = {}, shouldReReset = false) => {
      if (isSaved() || isChanged || canReReset || shouldReReset) {
        actions.reset({ ...getDefaultData(), ...data }, true);
        addEvent(Event.Reset([isSaved()]));
        isSaved() && remove();
      }
    },
  });

  function doValidate(options) {
    return validate(data, options);
  }

  const changeDateField = useCallback(
    (name, date) => {
      const value = date ? date.getTime() : null;
      actions.changeField(name, value);
    },
    [actions],
  );

  const changeSelectField = useCallback(
    (name, value) => {
      actions.changeField(name, value);
    },
    [actions],
  );

  useEffect(() => {
    if (events.length) {
      const event = events.shift();

      setEvents([...events]);

      switch (event.type) {
        case EVENT_APPLY:
          onApply();
          break;
        case EVENT_RESET:
          onReset(...event.data);
          break;
        case EVENT_CLEAR:
          onClear(...event.data);
          break;
        case EVENT_RESTORE:
          onRestore();
          break;
      }
    }
  }, [events, onApply, onReset, onClear, onRestore]);

  return {
    data,
    save,
    ...actions,
    blur,
    focus,
    reset,
    apply,
    remove,
    errors,
    isSaved,
    isChanged,
    changeDateField,
    changeSelectField,
    validate: useCallback(doValidate, [data, validate]),
  };
}
