import { useMemo, useEffect, useReducer, useCallback } from "react";

import { noop, isNull } from "underscore";

import { bindActionCreators } from "redux";

import { useFilter } from "./";

import { useRefCurrent, useMutationWatch } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import { deferred, isNotEqual } from "lib/utils/Utils";

const { Record } = require("immutable");

const CLEAR = "CLEAR_FIELDS";
const RESET = "RESET_FIELDS";
const CHANGE_FIELD = "CHANGE_FIELD";
const CHANGE_FIELDS = "CHANGE_FIELDS";
const UPDATE_HASH_CODE = "UPDATE_HASH_CODE";

function getActionCreators() {
  return {
    clear: () => ({ type: CLEAR }),
    reset: (data) => ({
      type: RESET,
      payload: { data },
    }),
    changeField: (name, value) => ({
      type: CHANGE_FIELD,
      payload: { name, value },
    }),
    changeFields: (changes) => ({
      type: CHANGE_FIELDS,
      payload: { changes },
    }),
  };
}

function Entity() {}

Entity.OrganizationField = function () {
  return { organizationId: null };
};

Entity.CommunityField = function (isMultiple) {
  return isMultiple ? { communityIds: [] } : { communityId: null };
};

function EntityBuilder() {
  const result = {};

  return {
    addOrganizationField() {
      result.organizationId = null;
      return this;
    },
    addCommunityField(hasMultiSelection) {
      if (hasMultiSelection) result.communityIds = [];
      else result.communityId = null;
      return this;
    },
    complete() {
      return result;
    },
  };
}

EntityBuilder().addOrganizationField().addCommunityField(true).complete();

function State(fields = {}) {
  return Record({
    fields: Record(fields)(),
    reset(data) {
      return this.clear().mergeIn(["fields"], data);
    },
    changeField(name, value) {
      return this.setIn(["fields", ...name.split(".")], value);
    },
    changeFields(changes) {
      return this.mergeIn(["fields"], changes);
    },
  })();
}

function Reducer() {
  return function reducer(state, action) {
    switch (action.type) {
      case CLEAR:
        return state.clear();

      case RESET:
        return state.reset(action.payload.data);

      case CHANGE_FIELD:
        return state.changeField(action.payload.name, action.payload.value);

      case CHANGE_FIELDS:
        return state.changeFields(action.payload.changes);

      case UPDATE_HASH_CODE:
        return state.updateHashCode();

      default:
        return state;
    }
  };
}

export default function usePrimaryFilter(
  name,
  {
    customFields,
    getInitialData = (data) => data,
    isCommunityMultiSelection = true,
    onClear = noop,
    onApply = noop,
    onChange = noop,
    onRestore = noop,
  } = {},
) {
  const user = useAuthUser();

  const options = useRefCurrent({
    onChange,
    onClear: deferred(onClear),
    onApply: deferred(onApply),
    onRestore: deferred(onRestore),
    getInitialData,
  });

  let actions = useMemo(() => getActionCreators(), []);

  const [state, dispatch] = useReducer(
    Reducer(),
    State({
      ...EntityBuilder().addOrganizationField().addCommunityField(isCommunityMultiSelection).complete(),
      ...customFields,
    }),
  );

  actions = useMemo(() => bindActionCreators(actions, dispatch), [actions]);

  const { fields } = state;
  const data = useMemo(() => fields.toJS(), [fields]);

  const { organizationId } = data;

  const { save, apply, remove, restore, isSaved } = useFilter(name, data, {
    onApply: () => {
      options.onApply();
    },
    onClear: () => {
      actions.clear({}, false);
      options.onClear();
    },
    onRestore: (data) => {
      data = getInitialData(data);

      if (isSaved()) {
        actions.changeFields(data, true);
        options.onRestore();
      }
    },
  });

  const changeField = useCallback(
    (name, value, shouldSave = false, shouldSaveAll = true) => {
      shouldSave && save({ [name]: value }, shouldSaveAll);
      return actions.changeField(name, value);
    },
    [save, actions],
  );

  const changeFields = useCallback(
    (changes, shouldSave = false, shouldSaveAll = true) => {
      shouldSave && save(changes, shouldSaveAll);
      return actions.changeFields(changes);
    },
    [save, actions],
  );

  const changeOrganizationField = useCallback(
    (value, shouldSave = true, shouldSaveAll = false) => {
      shouldSave && save({ organizationId: value }, shouldSaveAll);
      //Remove all fields except organizationId (optimization)
      return actions.reset({ organizationId: value });
    },
    [save, actions],
  );

  const changeCommunityField = useCallback(
    (value, shouldSave = true, shouldSaveAll = true) => {
      const name = isCommunityMultiSelection ? "communityIds" : "communityId";
      shouldSave && save({ organizationId, [name]: value }, shouldSaveAll);
      return actions.changeField(name, value);
    },
    [save, actions, organizationId, isCommunityMultiSelection],
  );

  useMutationWatch(
    data,
    () => {
      options.onChange(data);
    },
    (prev) => isNotEqual(data, prev),
  );

  useEffect(() => {
    if (user && !isSaved() && isNull(organizationId)) {
      actions.changeFields(
        options.getInitialData({
          organizationId: user.organizationId,
        }),
      );
    }
  }, [user, isSaved, actions, options, organizationId]);

  return {
    data,
    save,
    apply,
    remove,
    restore,
    isSaved,
    ...actions,
    changeField,
    changeFields,
    changeCommunityField,
    changeOrganizationField,
  };
}
