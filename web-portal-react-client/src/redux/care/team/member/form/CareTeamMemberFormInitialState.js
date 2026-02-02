import { each, isEmpty, last, mapObject } from "underscore";

import { State } from "redux/utils/Form";

import NotificationPreference from "./NotificationPreferenceInitialState";

const { Record, List } = require("immutable");

export default class extends State({
  tab: 0,
  error: null,
  isFetching: false,
  validation: Record({
    isSuccess: true,
    errors: Record({
      roleId: false,
      employeeId: false,
      description: false,
      includeInFaceSheet: false,
      careTeamManager: false,
      employeeOrganizationId: false,
      notificationsPreferences: {},
    })(),
  })(),
  fields: Record({
    id: null,
    clientId: null,
    communityId: null,
    description: "",
    employeeOrganizationId: null,
    employeeOrganizationName: "",
    employeeId: null,
    employeeName: "",
    roleId: null,
    roleIdHasError: false,
    roleIdErrorText: "",
    roleName: "",
    canChangeRole: true,
    includeInFaceSheet: false,
    notificationsPreferences: List(),
  })(),
}) {
  constructor(state) {
    super(state);

    this.updateHashCode();
  }

  setFieldErrors(errors) {
    let NPErrorFields = {};

    if (!isEmpty(errors.notificationsPreferences)) {
      const NPErrors = errors.notificationsPreferences[0];

      each(NPErrors, (errorMessages, fieldName) => {
        NPErrorFields[fieldName] = mapObject(errorMessages, (value) => last(value));
      });
    }

    return super.setFieldErrors(errors).setIn(["validation", "errors", "notificationsPreferences"], NPErrorFields);
  }

  changeNotificationPreferences(data, shouldUpdateHashCode) {
    return this.changeField(
      "notificationsPreferences",
      List(data.map((np) => NotificationPreference(np))),
      shouldUpdateHashCode,
    );
  }
}
