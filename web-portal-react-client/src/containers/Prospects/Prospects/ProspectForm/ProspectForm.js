import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { pick, omit, chain, reduce, compose, findWhere } from "underscore";

import { Col, Row, Form, Button } from "reactstrap";

import { Loader, ErrorViewer, OutsideClickListener, AlertPanel } from "components";

import { EditButton } from "components/buttons";

import {
  TextField,
  DateField,
  FileField,
  PhoneField,
  SelectField,
  CheckboxField,
  RadioGroupField,
} from "components/Form";

import { ConfirmDialog } from "components/dialogs";

import { withTooltip, withAutoSave } from "hocs";

import { useForm, useScrollable, useScrollToFormError, useCustomFormFieldChange } from "hooks/common";

import { useCommunityQuery } from "hooks/business/community";

import {
  useProspectQuery,
  useProspectSubmit,
  usePrimaryContactsQuery,
  useProspectFormDirectory,
  useUniqSsnInCommunityValidation,
  useUniqEmailInOrganizationValidation,
} from "hooks/business/Prospects";

import { ProspectFormValidator } from "validators";

import Prospect, { RelatedParty, SecondOccupant } from "entities/Prospect";

import { ReactComponent as Info } from "images/info-2.svg";

import { isEmpty, isInteger, formatSSN, getFullName, allAreInteger } from "lib/utils/Utils";

import { addAsterix } from "lib/utils/StringUtils";

import { format, formats, setTime, parseDate, parseTime } from "lib/utils/DateUtils";

import { isBlank, isNotBlank, getProperty, isEmptyOrBlank } from "lib/utils/ObjectUtils";

import { map, first, pushIf, isUnary } from "lib/utils/ArrayUtils";

import "./ProspectForm.scss";

import { useDebouncedCallback } from "use-debounce";

const IS_VETERAN_CASES = [
  { name: "YES", title: "Yes" },
  { name: "NO", title: "No" },
  { name: "UNKNOWN", title: "Prospect doesn't know" },
  { name: "UNDISCLOSED", title: "Chooses not to disclose " },
];

const PRIMARY_CONTACT_NOTIFICATION_METHODS = [
  { name: "EMAIL", title: "Email" },
  { name: "PHONE", title: "Phone" },
  { name: "CHAT", title: "Chat" },
];

export function convertDateToTime(date, time) {
  return setTime(date, parseTime(time)).valueOf();
}

function valueTextMapper({ id, name, value, label, title }) {
  return { value: id || value || name, text: label || title || name };
}

const scrollableStyles = { flex: 1 };

const avatarHint = {
  placement: "top",
  target: "avatar-hint",
  render: () => (
    <ul className="ProspectForm-TooltipBody">
      <li>The maximum file size for uploads is 1 MB</li>
      <li>Only image files (JPG, GIF, PNG) are allowed</li>
      <li>Recommended aspect ratio is 3:1</li>
      <li>Recommended image resolution is 42x147</li>
    </ul>
  ),
};

const cellPhoneTooltipHint = {
  placement: "top",
  target: "cell-phone-hint",
  render: () => (
    <ul className="ProspectForm-TooltipBody">
      <li>Digits only allowed</li>
      <li>No spaces, dashes, or special symbols</li>
      <li>Country code is required</li>
      <li>‘+’ may be a leading symbol</li>
    </ul>
  ),
};

const CareTeamMemberHint = withTooltip({
  text: "To enable this option, please add a prospect care team member with Family/Member or Person Receiving Services role",
})(Info);

const PrimaryContactSectionHint = withTooltip({
  text: "Primary contact and notification method will be used for sending e-sign requests when multiple recipients are selected",
})(Info);

const NotificationMethodChatHint = withTooltip({
  text: "The Chat feature is not enabled for the user(s) selected. Please contact the Simply Connect support team to enable the Chat feature",
})(Info);

function prepareDataToSubmit(fields) {
  const data = fields.toJS();

  if (data.avatar === null && data.avatarId === null) {
    data.shouldRemoveAvatar = true;
  }

  if (isEmptyOrBlank(omit(data.relatedParty, "address")) && isEmptyOrBlank(data.relatedParty?.address)) {
    delete data["relatedParty"];
  }

  if (data.secondOccupant && data.secondOccupant.avatar === null && data.secondOccupant.avatarId === null) {
    data.secondOccupant.shouldRemoveAvatar = true;
  }

  return data;
}

function ProspectForm({
  prospectId,
  communityId: defaultCommunityId,
  organizationId: defaultOrganizationId,
  autoSaveAdapter,
  onClose,
  onSubmitSuccess,
}) {
  const [error, setError] = useState(null);
  const [isFetching, setFetching] = useState(false);
  const [needValidation, setNeedValidation] = useState(false);
  const [needRemoteValidation, setNeedRemoteValidation] = useState(false);

  const isEditing = isInteger(prospectId);

  const [isEditableSSN, setEditableSSN] = useState(!isEditing);

  const [has2ndOccupant, setHas2ndOccupant] = useState(false);
  const [isEditable2ndOccupantSSN, setEditable2ndOccupantSSN] = useState(!isEditing);
  const [isDelete2ndOccupantConfirmDialogOpen, toggleDelete2ndOccupantConfirmDialog] = useState(false);
  const [isOverwrite2ndOccupantConfirmDialogOpen, toggleOverwrite2ndOccupantConfirmDialog] = useState(false);

  const { fields, errors, isValid, isChanged, validate, clearField, changeField, changeFields } = useForm(
    "Prospect",
    Prospect,
    ProspectFormValidator,
  );

  const data = useMemo(() => fields.toJS(), [fields]);

  const { changeDateField, changeSelectField } = useCustomFormFieldChange(changeField);

  const { Scrollable, scroll } = useScrollable();

  const scrollToError = useScrollToFormError(".ProspectForm", scroll);

  const {
    data: prospect,
    remove: removeProspect,
    isFetching: isFetchingProspect,
  } = useProspectQuery(
    { prospectId },
    {
      enabled: isInteger(prospectId),
    },
  );

  const prevPrimContact = prospect?.primaryContact;
  const nextPrimContact = fields?.primaryContact;

  const { data: community } = useCommunityQuery(
    {
      communityId: fields.communityId,
      organizationId: fields.organizationId,
      isMarketplaceDataIncluded: false,
    },
    {
      staleTime: 0,
      enabled: allAreInteger(fields.organizationId, fields.communityId),
    },
  );

  const { data: primaryContacts = [] } = usePrimaryContactsQuery(
    { prospectId },
    {
      enabled: !!prospectId,
      cacheTime: 0,
      onSuccess: (data) => {
        if (isUnary(data) && !nextPrimContact?.careTeamMemberId) {
          changeField("primaryContact.careTeamMemberId", first(data).careTeamMemberId);
        }
      },
    },
  );

  const isNoRelatedParty =
    isEmptyOrBlank(omit(data.relatedParty, "address")) && isEmptyOrBlank(data.relatedParty?.address);

  const isInactiveCareTeamMember = useMemo(
    () => !prevPrimContact?.active && nextPrimContact?.careTeamMemberId === prevPrimContact?.careTeamMemberId,
    [prevPrimContact, nextPrimContact],
  );

  const hasInactivePrimaryContact =
    !prevPrimContact?.active &&
    isInteger(prevPrimContact?.careTeamMemberId) &&
    nextPrimContact?.typeName === "CARE_TEAM_MEMBER";

  const canEditSSN = prospect?.canEditSsn;
  const hasEmptySSN = isEmpty(prospect?.ssn?.trim());

  const isHasNoSSNEnabled = useMemo(
    () => canEditSSN || !(isEditing && hasEmptySSN),
    [isEditing, hasEmptySSN, canEditSSN],
  );

  const ssn = useMemo(
    () => (!isEditing || hasEmptySSN || isEditableSSN ? fields.ssn : formatSSN(fields.ssn)),
    [isEditing, fields.ssn, hasEmptySSN, isEditableSSN],
  );

  const hasEmpty2ndOccupantSSN = isEmpty(prospect?.secondOccupant?.ssn?.trim());

  const isHasNo2ndOccupantSSNEnabled = useMemo(
    () => !(isEditing && hasEmpty2ndOccupantSSN),
    [isEditing, hasEmpty2ndOccupantSSN],
  );

  const _2ndOccupantSSN = useMemo(
    () =>
      !isEditing || hasEmpty2ndOccupantSSN || isEditable2ndOccupantSSN
        ? fields.secondOccupant?.ssn
        : formatSSN(fields.secondOccupant?.ssn),
    [isEditing, fields.secondOccupant, hasEmpty2ndOccupantSSN, isEditable2ndOccupantSSN],
  );

  const {
    races,
    states,
    genders,
    networks,
    communities,
    organizations,
    maritalStatuses,
    relationshipTypes,
    isFetchingOrganizations,
  } = useProspectFormDirectory({
    organizationId: fields.organizationId,
  });

  const mappedGenders = useMemo(() => map(genders, valueTextMapper), [genders]);

  const mappedMaritalStatuses = useMemo(() => map(maritalStatuses, valueTextMapper), [maritalStatuses]);

  const mappedRaces = useMemo(() => map(races, valueTextMapper), [races]);

  const mappedIsVeteranCases = useMemo(() => map(IS_VETERAN_CASES, valueTextMapper), []);

  const mappedNetworks = useMemo(() => map(networks, valueTextMapper), [networks]);

  const mappedOrganizations = useMemo(() => map(organizations, valueTextMapper), [organizations]);

  const mappedStates = useMemo(() => map(states, valueTextMapper), [states]);

  const mappedRelationshipTypes = useMemo(() => map(relationshipTypes, valueTextMapper), [relationshipTypes]);

  const validateFieldIfAllowed = useCallback(
    (name, cb) => {
      const value = getProperty(data, name);
      const originValue = getProperty(prospect, name);
      return value !== originValue ? cb() : Promise.resolve();
    },
    [data, prospect],
  );

  const [validateEmail, emailError] = useUniqEmailInOrganizationValidation({
    prospectId,
    email: fields.email,
    organizationId: fields.organizationId,
  });

  const validateEmailIfAllowed = useCallback(() => {
    return validateFieldIfAllowed("email", validateEmail);
  }, [validateEmail, validateFieldIfAllowed]);

  const validateDebouncedEmailIfAllowed = useDebouncedCallback(validateEmailIfAllowed, 300);

  const [validate2ndOccupantEmail, _2ndOccupantEmailError] = useUniqEmailInOrganizationValidation({
    prospectId,
    email: fields.secondOccupant?.email,
    organizationId: fields.organizationId,
  });

  const validate2ndOccupantEmailIfAllowed = useCallback(() => {
    return validateFieldIfAllowed("secondOccupant.email", validate2ndOccupantEmail);
  }, [validate2ndOccupantEmail, validateFieldIfAllowed]);

  const validateDebounced2ndOccupantEmailIfAllowed = useDebouncedCallback(validate2ndOccupantEmailIfAllowed, 300);

  const [validateSsn, ssnError] = useUniqSsnInCommunityValidation({
    prospectId,
    ssn: fields.ssn,
    communityId: fields.communityId,
  });

  const validateSsnIfAllowed = useCallback(() => {
    return validateFieldIfAllowed("ssn", validateSsn);
  }, [validateSsn, validateFieldIfAllowed]);

  const validateDebouncedSsnIfAllowed = useDebouncedCallback(validateSsnIfAllowed, 300);

  const [validate2ndOccupantSsnAllowed, _2ndOccupantSsnError] = useUniqSsnInCommunityValidation({
    prospectId,
    ssn: fields.secondOccupant?.ssn,
    communityId: fields.communityId,
  });

  const validate2ndOccupantSsnIfAllowed = useCallback(() => {
    return validateFieldIfAllowed("secondOccupant.ssn", validate2ndOccupantSsnAllowed);
  }, [validate2ndOccupantSsnAllowed, validateFieldIfAllowed]);

  const validateDebounced2ndOccupantSsnIfAllowed = useDebouncedCallback(validate2ndOccupantSsnIfAllowed, 300);

  const mappedCommunities = useMemo(() => map(communities, valueTextMapper), [communities]);

  const mappedPrimaryContactTypes = useMemo(
    () => [
      { value: "SELF", label: "Self" },
      {
        isDisabled: isEmpty(primaryContacts),
        value: "CARE_TEAM_MEMBER",
        label: (
          <div className="d-flex">
            Care team member
            {isEmpty(primaryContacts) && <CareTeamMemberHint isTooltipEnabled className="ProspectForm-InfoIcon" />}
          </div>
        ),
      },
    ],
    [primaryContacts],
  );

  const mappedPrimaryContacts = useMemo(
    () =>
      map(
        chain([
          hasInactivePrimaryContact && {
            fullName: getFullName(prevPrimContact),
            careTeamMemberId: prevPrimContact.careTeamMemberId,
          },
          ...(primaryContacts?.data || []),
        ])
          .compact()
          .sortBy("fullName")
          .value(),
        (o) => ({ text: o.fullName, value: o.careTeamMemberId }),
      ),
    [prevPrimContact, primaryContacts, hasInactivePrimaryContact],
  );

  const mappedPrimaryContactNotificationMethods = useMemo(
    () =>
      reduce(
        PRIMARY_CONTACT_NOTIFICATION_METHODS,
        (methods, method) => {
          const mappedMethod = {
            label: method.title,
            value: method.name,
          };

          const isCTMemberType = nextPrimContact?.typeName === "CARE_TEAM_MEMBER";

          const contact = findWhere(primaryContacts?.data, {
            careTeamMemberId: nextPrimContact?.careTeamMemberId,
          });

          if (method.name === "CHAT") {
            if (!isEditing) return methods;

            if (isCTMemberType && !contact?.chatEnabled) {
              mappedMethod.isDisabled = true;
              mappedMethod.label = (
                <div className="d-flex">
                  {method.title}
                  <NotificationMethodChatHint isTooltipEnabled className="ProspectForm-InfoIcon" />
                </div>
              );
            }
          }

          return [...methods, mappedMethod];
        },
        [],
      ),
    [isEditing, primaryContacts, nextPrimContact],
  );

  const { mutateAsync: submit } = useProspectSubmit(
    {},
    {
      onError: setError,
      onSuccess: onSubmitSuccess,
    },
  );

  const validationOptions = useMemo(
    () => ({
      included: {
        isEditing,
        has2ndOccupant,
        shouldValidateSSN: !fields.hasNoSsn,
        shouldValidate2ndOccupantSSN: !fields.secondOccupant?.hasNoSsn,
      },
    }),
    [fields, isEditing, has2ndOccupant],
  );

  const getRemoteValidations = useCallback(() => {
    const validations = [validateEmailIfAllowed()];
    pushIf(validations, validateSsnIfAllowed(), !fields.hasNoSsn);

    if (has2ndOccupant) {
      pushIf(validations, validate2ndOccupantEmailIfAllowed());
      pushIf(validations, validate2ndOccupantSsnIfAllowed(), !fields.secondOccupant?.hasNoSsn);
    }

    return validations;
  }, [
    fields,
    has2ndOccupant,
    validateSsnIfAllowed,
    validateEmailIfAllowed,
    validate2ndOccupantSsnIfAllowed,
    validate2ndOccupantEmailIfAllowed,
  ]);

  function cancel() {
    onClose(isChanged);
  }

  async function tryToSubmit(e) {
    e.preventDefault();
    setFetching(true);

    try {
      await validate(validationOptions);
      setNeedValidation(false);

      await Promise.all(getRemoteValidations())
        .then(() => {
          setNeedRemoteValidation(false);
        })
        .catch((e) => {
          setNeedRemoteValidation(true);
          throw e;
        });

      await submit(prepareDataToSubmit(fields)).then(removeProspect);
    } catch (e) {
      scrollToError();
      setNeedValidation(true);
    } finally {
      setFetching(false);
    }
  }

  async function autoSave() {
    if (!isEditing) return;

    setFetching(true);

    try {
      await validate(validationOptions);
      setNeedValidation(false);

      await Promise.all(getRemoteValidations())
        .then(() => {
          setNeedRemoteValidation(false);
        })
        .catch((e) => {
          setNeedRemoteValidation(true);
          throw e;
        });

      await submit({ ...data, isAutoSave: true });
    } catch (error) {
      const mergedData = data;

      Object.keys(error).forEach((key) => {
        mergedData[key] = (prospect || {})[key];
      });

      await submit({ ...data, isAutoSave: true });
    } finally {
      setFetching(false);
    }
  }

  const onAutoSave = useCallback(autoSave, [
    data,
    submit,
    prospect,
    validate,
    isEditing,
    validationOptions,
    getRemoteValidations,
  ]);

  useEffect(() => {
    if (autoSaveAdapter && isEditing) {
      autoSaveAdapter.init({
        onSave: () => onAutoSave(),
      });
    }
  }, [data, isEditing, autoSaveAdapter]);

  function validateIfNeed() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function validateSsnIfNeed() {
    if (fields.ssn && needRemoteValidation) {
      validateDebouncedSsnIfAllowed();
    }
  }

  function validateEmailIfNeed() {
    if (fields.email && needRemoteValidation) {
      validateDebouncedEmailIfAllowed();
    }
  }

  function validate2ndOccupantSsnIfNeed() {
    if (fields.secondOccupant?.ssn && needRemoteValidation) {
      validateDebounced2ndOccupantSsnIfAllowed();
    }
  }

  function validate2ndOccupantEmailIfNeed() {
    if (fields.secondOccupant?.email && needRemoteValidation) {
      validateDebounced2ndOccupantEmailIfAllowed();
    }
  }

  function init() {
    if (prospect) {
      const data = { ...prospect };

      if (!prospect.relatedParty) {
        data.relatedParty = RelatedParty().toJS();
      }

      if (prospect.secondOccupant) {
        setHas2ndOccupant(true);
      }

      changeFields(data, true);
    }
  }

  function setDefaultData() {
    if (!isEditing) {
      const changes = {
        communityId: defaultCommunityId,
        organizationId: defaultOrganizationId,
      };

      changeFields(changes, true);
    }
  }

  function setDefaultCommunity() {
    if (!isEditing && isUnary(communities)) {
      changeField("communityId", communities[0].id, true);
    }
  }

  const onEditSSN = useCallback(() => {
    setEditableSSN(true);
  }, []);

  const onChangeHasNoSsnField = useCallback(
    (name, value) => {
      clearField("ssn");
      changeField(name, value);
    },
    [clearField, changeField],
  );

  const onStopEditingSSN = useCallback(() => {
    if (isEditing && isHasNoSSNEnabled) {
      setEditableSSN(false);
    }
  }, [isEditing, isHasNoSSNEnabled]);

  const onEdit2ndOccupantSSN = useCallback(() => {
    setEditable2ndOccupantSSN(true);
  }, []);

  const onChange2ndOccupantDoesNotHaveSsnField = useCallback(
    (name, value) => {
      changeField(name, value);
      clearField("secondOccupant.ssn");
    },
    [clearField, changeField],
  );

  const onStopEditing2ndOccupantSSN = useCallback(() => {
    if (isEditing && isHasNo2ndOccupantSSNEnabled) {
      setEditable2ndOccupantSSN(false);
    }
  }, [isEditing, isHasNo2ndOccupantSSNEnabled]);

  const onSubmit = useCallback(tryToSubmit, [data, validate, validationOptions]);

  const onCancel = useCallback(cancel, [onClose, isChanged]);

  const onChangeBirthdayField = useCallback(
    (name, value) => {
      changeField(name, value && format(value, formats.americanMediumDate));
    },
    [changeField],
  );

  const onChangeAvatarField = useCallback(
    (field, value) => {
      changeFields({
        [field]: value,
        avatarName: value ? value.name : "",
      });

      if (!value) changeField("avatarId", null);
    },
    [changeField, changeFields],
  );

  const onChangePrimaryContactTypeField = useCallback(
    (name, value) => {
      if (value === "SELF" && nextPrimContact?.notificationMethodName === "EMAIL") {
        changeField(name, "SELF");
        changeField("hasNoEmail", false);
      } else changeField(name, value);
    },
    [changeField, nextPrimContact],
  );

  const onChangePrimaryContactCTMemberField = useCallback(
    (name, value) => {
      changeField(name, value);
      clearField("primaryContact.notificationMethodName");
    },
    [clearField, changeField],
  );

  const onChangePrimaryContactNotificationMethodField = useCallback(
    (name, value) => {
      if (value === "EMAIL" && nextPrimContact?.typeName === "SELF") {
        changeField(name, "EMAIL");
        changeField("hasNoEmail", false);
      } else changeField(name, value);
    },
    [changeField, nextPrimContact],
  );

  const onChangeHas2ndOccupantField = useCallback(
    (name, value) => {
      if (!(value || isEmpty(data.secondOccupant))) {
        toggleDelete2ndOccupantConfirmDialog(true);
      } else {
        setHas2ndOccupant(value);
        changeField("secondOccupant", SecondOccupant(prospect?.secondOccupant ?? {}));
      }
    },
    [data, prospect, changeField],
  );

  const onChangeRelatedPartyIs2ndOccupantField = useCallback(
    (name, value) => {
      const fields = ["firstName", "lastName", "cellPhone", "email"];

      const isNotBlank2ndOccupant =
        isNotBlank(pick(data.secondOccupant, fields)) && isNotBlank(data.secondOccupant.address);

      if (value && isNotBlank2ndOccupant) {
        toggleOverwrite2ndOccupantConfirmDialog(true);
      } else
        changeFields({
          [name]: value,
          secondOccupant: data.relatedParty,
        });
    },
    [data, changeFields],
  );

  const onChange2ndOccupantHasProspectAddressField = useCallback(
    (name, value) => {
      changeField(name, value);
      if (value) changeFields({ secondOccupant: { address: data.address } });
    },
    [data, changeField, changeFields],
  );

  const onConfirm2ndOccupantDeletion = useCallback(() => {
    clearField("secondOccupant");
    setHas2ndOccupant(false);
    toggleDelete2ndOccupantConfirmDialog(false);
  }, [clearField]);

  const onConfirm2ndOccupantOverwriting = useCallback(() => {
    changeFields({
      relatedPartyIs2ndOccupant: true,
      secondOccupant: data.relatedParty,
    });
    toggleOverwrite2ndOccupantConfirmDialog(false);
  }, [data, changeFields]);

  const onChangeSecondOccupantAvatarField = useCallback(
    (field, value) => {
      changeField(field, value);
      changeField("secondOccupant.avatarName", value ? value.name : "");
      if (!value) changeField("secondOccupant.avatarId", null);
    },
    [changeField],
  );

  useEffect(validateIfNeed, [validate, scrollToError, needValidation, validationOptions]);

  useEffect(validateSsnIfNeed, [fields.ssn, needRemoteValidation, validateDebouncedSsnIfAllowed]);

  useEffect(validateEmailIfNeed, [fields.email, needRemoteValidation, validateDebouncedEmailIfAllowed]);

  useEffect(validate2ndOccupantSsnIfNeed, [
    needRemoteValidation,
    fields.secondOccupant,
    validateDebounced2ndOccupantSsnIfAllowed,
  ]);

  useEffect(validate2ndOccupantEmailIfNeed, [
    needRemoteValidation,
    fields.secondOccupant,
    validateDebounced2ndOccupantEmailIfAllowed,
  ]);

  useEffect(init, [prospect, changeFields]);

  useEffect(setDefaultData, [isEditing, changeFields, defaultCommunityId, defaultOrganizationId]);

  useEffect(setDefaultCommunity, [isEditing, communities, changeField]);

  return (
    <>
      <Form onSubmit={onSubmit} className="ProspectForm" data-testid="prospectForm">
        {(isFetching || isFetchingProspect) && <Loader style={{ position: "fixed" }} hasBackdrop />}

        <Scrollable style={scrollableStyles}>
          <div className="form-section">
            <div className="form-section-title">Demographics & Insurance</div>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="firstName"
                  value={fields.firstName}
                  label="First Name*"
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.firstName}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>

              <Col md={4}>
                <TextField
                  type="text"
                  name="lastName"
                  value={fields.lastName}
                  label="Last Name*"
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.lastName}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>

              <Col md={4}>
                <TextField
                  type="text"
                  name="middleName"
                  value={fields.middleName}
                  label="Middle Name"
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.middleName}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <DateField
                  name="birthDate"
                  className="ProspectForm-DateField"
                  value={parseDate(fields.birthDate)?.getTime()}
                  dateFormat="MM/dd/yyyy"
                  label="Date Of Birth*"
                  maxDate={Date.now()}
                  errorText={errors.birthDate}
                  isDisabled={!fields.isActive}
                  onChange={onChangeBirthdayField}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="genderId"
                  value={fields.genderId}
                  options={mappedGenders}
                  label="Gender*"
                  className="ProspectForm-SelectField"
                  errorText={errors.genderId}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="maritalStatusId"
                  value={fields.maritalStatusId}
                  options={mappedMaritalStatuses}
                  label="Marital Status"
                  className="ProspectForm-SelectField"
                  errorText={errors.maritalStatusId}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <SelectField
                  name="raceId"
                  value={fields.raceId}
                  options={mappedRaces}
                  label="Race"
                  className="ProspectForm-SelectField"
                  errorText={errors.raceId}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
              <Col md={4} className="position-relative">
                <OutsideClickListener onClick={onStopEditingSSN} containerSelector=".modal">
                  <TextField
                    type="text"
                    name="ssn"
                    value={ssn}
                    maxLength={9}
                    placeholder="XXX XX XXXX"
                    className="ProspectForm-TextField"
                    errorText={errors.ssn || ssnError?.message}
                    isDisabled={!isEditableSSN || (!isEditing && fields.hasNoSsn)}
                    label={"Social Security Number" + (!fields.hasNoSsn ? "*" : "")}
                    onChange={changeField}
                  />
                  {canEditSSN && !isEditableSSN && !fields.hasNoSsn && (
                    <EditButton
                      size={24}
                      shouldHighLight={false}
                      className="ProspectForm-EditSsnButton"
                      onClick={onEditSSN}
                    />
                  )}
                </OutsideClickListener>
              </Col>
              <Col md={4}>
                <CheckboxField
                  type="text"
                  name="hasNoSsn"
                  value={fields.hasNoSsn}
                  label="Prospect doesn't have SSN"
                  className="ProspectForm-CheckboxField padding-top-34"
                  isDisabled={!isHasNoSSNEnabled}
                  onChange={onChangeHasNoSsnField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <SelectField
                  name="veteranStatusName"
                  value={fields.veteranStatusName}
                  options={mappedIsVeteranCases}
                  label="Veteran?"
                  className="ProspectForm-SelectField"
                  errorText={errors.veteranStatusName}
                  isDisabled={!fields.isActive}
                  onChange={changeSelectField}
                />
              </Col>

              <Col md={4}>
                <SelectField
                  hasSearchBox
                  name="insuranceNetworkId"
                  value={fields.insuranceNetworkId}
                  options={mappedNetworks}
                  label="Network"
                  placeholder="Search by network name"
                  className="ProspectForm-SelectField"
                  isDisabled={!fields.isActive}
                  onChange={changeSelectField}
                />
              </Col>

              <Col md={4}>
                <TextField
                  type="text"
                  label="Plan"
                  name="insurancePaymentPlan"
                  value={fields.insurancePaymentPlan}
                  className="ProspectForm-TextField"
                  maxLength={256}
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>

              <Col md={8}>
                <FileField
                  name="avatar"
                  value={fields.avatar?.name || fields.avatarName}
                  label="Photo"
                  className="ProspectForm-TextField"
                  errorText={errors.avatar?.size || errors.avatar?.type}
                  isDisabled={!fields.isActive}
                  onChange={onChangeAvatarField}
                  renderLabelIcon={() => <Info id="avatar-hint" className="ProspectForm-InfoIcon" />}
                  tooltip={avatarHint}
                />
              </Col>
            </Row>
          </div>

          <div className="form-section">
            <div className="form-section-title">Community</div>
            <Row>
              <Col md="6">
                <SelectField
                  name="organizationId"
                  value={fields.organizationId}
                  options={mappedOrganizations}
                  hasValueTooltip
                  hasAllOption={false}
                  hasNoneOption={false}
                  hasKeyboardSearch
                  hasKeyboardSearchText
                  label="Organization Name*"
                  isDisabled={isEditing || isFetchingOrganizations || mappedOrganizations.length === 1}
                  className="ProspectForm-SelectField"
                  errorText={errors.organizationId}
                  onChange={changeSelectField}
                />
              </Col>

              <Col md="6">
                <SelectField
                  name="communityId"
                  value={fields.communityId}
                  options={mappedCommunities}
                  hasKeyboardSearch
                  label="Community Name*"
                  isDisabled={isEditing || mappedCommunities.length === 1 || fields.organizationId === null}
                  className="ProspectForm-SelectField"
                  errorText={errors.communityId}
                  onChange={changeSelectField}
                />
              </Col>
            </Row>
          </div>

          <div className="form-section">
            <div className="form-section-title">Contact Information</div>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="address.street"
                  value={fields.address.street}
                  label="Street*"
                  className="ProspectForm-TextField"
                  errorText={errors.address?.street}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="address.city"
                  value={fields.address.city}
                  label="City*"
                  className="ProspectForm-TextField"
                  errorText={errors.address?.city}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="address.stateId"
                  value={fields.address?.stateId}
                  label="State*"
                  hasValueTooltip
                  options={mappedStates}
                  className="ProspectForm-TextField"
                  placeholder="Select State"
                  errorText={errors.address?.stateId}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="number"
                  name="address.zip"
                  value={fields.address.zip}
                  label="Zip Code*"
                  className="ProspectForm-TextField"
                  maxLength={5}
                  errorText={errors.address?.zip}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <PhoneField
                  name="cellPhone"
                  value={fields.cellPhone}
                  label="Cell Phone #*"
                  className="ProspectForm-TextField"
                  errorText={errors.cellPhone}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="cell-phone-hint" className="ProspectForm-InfoIcon" />}
                  tooltip={cellPhoneTooltipHint}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="email"
                  value={fields.email}
                  label="Email*"
                  className="ProspectForm-TextField"
                  errorText={errors.email || emailError?.message}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>

          <div className="ProspectForm-Section">
            <div className="ProspectForm-SectionTitle d-flex align-items-center">
              Primary Contact
              <PrimaryContactSectionHint isTooltipEnabled className="ProspectForm-InfoIcon ml-2" />
            </div>

            {hasInactivePrimaryContact && nextPrimContact?.careTeamMemberId === prevPrimContact?.careTeamMemberId && (
              <AlertPanel>The selected care team member is currently inactive</AlertPanel>
            )}

            <Row>
              <Col lg={4} md={6}>
                <RadioGroupField
                  view="row"
                  name="primaryContact.typeName"
                  selected={nextPrimContact?.typeName}
                  title="Primary contact*"
                  options={mappedPrimaryContactTypes}
                  onChange={onChangePrimaryContactTypeField}
                  errorText={errors.primaryContact?.typeName}
                  className="ProspectForm-PrimaryContactType"
                />
              </Col>
              {nextPrimContact?.typeName === "CARE_TEAM_MEMBER" && (
                <Col lg={4} md={6}>
                  <SelectField
                    name="primaryContact.careTeamMemberId"
                    value={nextPrimContact?.careTeamMemberId}
                    options={mappedPrimaryContacts}
                    label="Care team member*"
                    placeholder="Select"
                    className="ProspectForm-SelectField"
                    errorText={errors.primaryContact?.careTeamMemberId}
                    onChange={onChangePrimaryContactCTMemberField}
                  />
                </Col>
              )}
              <Col lg={4} md={6}>
                <RadioGroupField
                  view="row"
                  name="primaryContact.notificationMethodName"
                  selected={nextPrimContact?.notificationMethodName}
                  title="Primary notification method*"
                  options={mappedPrimaryContactNotificationMethods}
                  onChange={onChangePrimaryContactNotificationMethodField}
                  errorText={errors.primaryContact?.notificationMethodName}
                  className="ProspectForm-NotificationMethod"
                />
              </Col>
            </Row>
          </div>

          <div className="form-section">
            <div className="form-section-title">Move-In Information</div>
            <Row>
              <Col md={4}>
                <DateField
                  name="moveInDate"
                  className="ProspectForm-DateField"
                  value={fields.moveInDate}
                  dateFormat="MM/dd/yyyy"
                  label="Move-In Date"
                  errorText={errors.moveInDate}
                  onChange={changeDateField}
                />
              </Col>
              <Col md={4}>
                <DateField
                  name="rentalAgreementSignedDate"
                  className="ProspectForm-DateField"
                  value={fields.rentalAgreementSignedDate}
                  dateFormat="MM/dd/yyyy"
                  label="Rental Agreement Signed Date"
                  errorText={errors.rentalAgreementSignedDate}
                  onChange={changeDateField}
                />
              </Col>
              <Col md={4}>
                <DateField
                  name="assessmentDate"
                  className="ProspectForm-DateField"
                  value={fields.assessmentDate}
                  dateFormat="MM/dd/yyyy"
                  label="Assessment Date"
                  errorText={errors.assessmentDate}
                  onChange={changeDateField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="referralSource"
                  value={fields.referralSource}
                  label="Referral Source"
                  className="ProspectForm-TextField"
                  errorText={errors.referralSource}
                  onChange={changeField}
                />
              </Col>
              <Col md={8}>
                <TextField
                  type="text"
                  name="notes"
                  value={fields.notes}
                  label="Notes"
                  className="ProspectForm-TextField"
                  errorText={errors.notes}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>

          <div className="form-section">
            <div className="form-section-title">Related Party</div>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="relatedParty.firstName"
                  value={fields.relatedParty?.firstName}
                  label={addAsterix("First Name").if(!isNoRelatedParty)}
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.firstName}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="relatedParty.lastName"
                  value={fields.relatedParty?.lastName}
                  label={addAsterix("Last Name").if(!isNoRelatedParty)}
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.lastName}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="relatedParty.relationshipTypeName"
                  value={fields.relatedParty?.relationshipTypeName}
                  label={addAsterix("Relationship").if(!isNoRelatedParty)}
                  hasValueTooltip
                  options={mappedRelationshipTypes}
                  className="ProspectForm-TextField"
                  placeholder="Select"
                  errorText={errors.relatedParty?.relationshipTypeName}
                  onChange={changeSelectField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <TextField
                  type="text"
                  name="relatedParty.address.street"
                  value={fields.relatedParty?.address?.street}
                  label={addAsterix("Street").if(!isNoRelatedParty)}
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.address?.street}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="relatedParty.address.city"
                  value={fields.relatedParty?.address?.city}
                  label={addAsterix("City").if(!isNoRelatedParty)}
                  maxLength={256}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.address?.city}
                  onChange={changeField}
                />
              </Col>
              <Col md={4}>
                <SelectField
                  name="relatedParty.address.stateId"
                  value={fields.relatedParty?.address?.stateId}
                  label={addAsterix("State").if(!isNoRelatedParty)}
                  hasValueTooltip
                  options={mappedStates}
                  className="ProspectForm-TextField"
                  placeholder="Select State"
                  errorText={errors.relatedParty?.address?.stateId}
                  onChange={changeSelectField}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="relatedParty.address.zip"
                  value={fields.relatedParty?.address?.zip}
                  label={addAsterix("Zip Code").if(!isNoRelatedParty)}
                  className="ProspectForm-TextField"
                  maxLength={5}
                  errorText={errors.relatedParty?.address?.zip}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <PhoneField
                  name="relatedParty.cellPhone"
                  value={fields.relatedParty?.cellPhone}
                  label={addAsterix("Cell Phone #").if(!isNoRelatedParty)}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.cellPhone}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="cell-phone-hint" className="ProspectForm-InfoIcon" />}
                  tooltip={cellPhoneTooltipHint}
                />
              </Col>
              <Col md={4}>
                <TextField
                  type="text"
                  name="relatedParty.email"
                  value={fields.relatedParty?.email}
                  label={addAsterix("Email").if(!isNoRelatedParty)}
                  className="ProspectForm-TextField"
                  errorText={errors.relatedParty?.secureMail}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>

          <div className="form-section">
            <Row>
              <Col md={4}>
                <CheckboxField
                  name="hasSecondOccupant"
                  value={has2ndOccupant}
                  label="Add 2nd Occupant"
                  className="ProspectForm-CheckboxField"
                  onChange={onChangeHas2ndOccupantField}
                />
              </Col>

              {has2ndOccupant && (
                <Col md={4}>
                  <CheckboxField
                    name="relatedPartyIs2ndOccupant"
                    value={fields.relatedPartyIs2ndOccupant}
                    label="Related Party is 2nd Occupant"
                    className="ProspectForm-CheckboxField"
                    isDisabled={isNoRelatedParty}
                    tooltip={
                      isNoRelatedParty
                        ? {
                            text: "Please enter Related party fields to enable this checkbox",
                          }
                        : null
                    }
                    onChange={onChangeRelatedPartyIs2ndOccupantField}
                  />
                </Col>
              )}
            </Row>
          </div>

          {has2ndOccupant && (
            <div className="form-section">
              <div className="form-section-title">Demographics & Insurance</div>
              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.firstName"
                    value={fields.secondOccupant?.firstName}
                    label="First Name*"
                    maxLength={256}
                    isDisabled={fields.relatedPartyIs2ndOccupant}
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.firstName}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.lastName"
                    value={fields.secondOccupant?.lastName}
                    label="Last Name*"
                    maxLength={256}
                    isDisabled={fields.relatedPartyIs2ndOccupant}
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.lastName}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.middleName"
                    value={fields.secondOccupant?.middleName}
                    label="Middle Name*"
                    maxLength={256}
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.middleName}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  <DateField
                    name="secondOccupant.birthDate"
                    className="ProspectForm-DateField"
                    value={parseDate(fields.secondOccupant?.birthDate)?.getTime()}
                    dateFormat="MM/dd/yyyy"
                    label="Date Of Birth*"
                    maxDate={Date.now()}
                    errorText={errors.secondOccupant?.birthDate}
                    onChange={onChangeBirthdayField}
                  />
                </Col>
                <Col md={4}>
                  <SelectField
                    name="secondOccupant.genderId"
                    value={fields.secondOccupant?.genderId}
                    options={mappedGenders}
                    label="Gender*"
                    className="ProspectForm-SelectField"
                    errorText={errors.secondOccupant?.genderId}
                    onChange={changeField}
                  />
                </Col>
                <Col md={4}>
                  <SelectField
                    name="secondOccupant.maritalStatusId"
                    value={fields.secondOccupant?.maritalStatusId}
                    options={mappedMaritalStatuses}
                    label="Marital Status"
                    className="ProspectForm-SelectField"
                    errorText={errors.secondOccupant?.maritalStatusId}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  <SelectField
                    name="secondOccupant.raceId"
                    value={fields.secondOccupant?.raceId}
                    options={mappedRaces}
                    label="Race"
                    className="ProspectForm-SelectField"
                    errorText={errors.secondOccupant?.raceId}
                    onChange={changeField}
                  />
                </Col>
                <Col md={4}>
                  <OutsideClickListener onClick={onStopEditing2ndOccupantSSN} containerSelector=".modal">
                    <TextField
                      type="text"
                      name="secondOccupant.ssn"
                      value={_2ndOccupantSSN}
                      maxLength={9}
                      placeholder="XXX XX XXXX"
                      className="ProspectForm-TextField"
                      errorText={errors.secondOccupant?.ssn || _2ndOccupantSsnError?.message}
                      isDisabled={!isEditable2ndOccupantSSN || (!isEditing && fields.secondOccupant?.hasNoSsn)}
                      label={"Social Security Number" + (!fields.secondOccupant?.hasNoSsn ? "*" : "")}
                      onChange={changeField}
                    />
                    {!isEditable2ndOccupantSSN && !fields.secondOccupant?.hasNoSsn && (
                      <EditButton
                        size={24}
                        shouldHighLight={false}
                        className="ProspectForm-EditSsnButton"
                        onClick={onEdit2ndOccupantSSN}
                      />
                    )}
                  </OutsideClickListener>
                </Col>
                <Col md={4}>
                  <CheckboxField
                    type="text"
                    name="secondOccupant.hasNoSsn"
                    value={fields.secondOccupant?.hasNoSsn}
                    label="2nd occupant doesn't have SSN"
                    className="ProspectForm-CheckboxField padding-top-34"
                    isDisabled={!isHasNoSSNEnabled}
                    onChange={onChange2ndOccupantDoesNotHaveSsnField}
                  />
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  <SelectField
                    name="secondOccupant.veteranStatusName"
                    value={fields.secondOccupant?.veteranStatusName}
                    options={mappedIsVeteranCases}
                    label="Veteran?"
                    className="ProspectForm-SelectField"
                    errorText={errors.secondOccupant?.veteranStatusName}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <SelectField
                    hasSearchBox
                    name="secondOccupant.insuranceNetworkId"
                    value={fields.secondOccupant?.insuranceNetworkId}
                    options={mappedNetworks}
                    label="Network"
                    placeholder="Search by network name"
                    className="ProspectForm-SelectField"
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    label="Plan"
                    name="secondOccupant.insurancePaymentPlan"
                    value={fields.secondOccupant?.insurancePaymentPlan}
                    className="ProspectForm-TextField"
                    maxLength={256}
                    onChange={changeField}
                  />
                </Col>

                <Col md={8}>
                  <FileField
                    name="secondOccupant.avatar"
                    value={fields.secondOccupant?.avatar?.name || fields.secondOccupant?.avatarName}
                    label="Photo"
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.avatar?.size || errors.secondOccupant?.avatar?.type}
                    onChange={onChangeSecondOccupantAvatarField}
                    renderLabelIcon={() => <Info id="second-occupant-avatar-hint" className="ProspectForm-InfoIcon" />}
                    tooltip={{
                      ...avatarHint,
                      target: "second-occupant-avatar-hint",
                    }}
                  />
                </Col>
              </Row>
            </div>
          )}

          {has2ndOccupant && (
            <div className="form-section">
              <div className="form-section-title">Contact Information</div>
              <Row>
                <Col md={4}>
                  <CheckboxField
                    name="secondOccupant.hasProspectAddress"
                    value={fields.secondOccupant?.hasProspectAddress}
                    label="Use prospect address"
                    isDisabled={isBlank(data.address) || fields.relatedPartyIs2ndOccupant}
                    tooltip={
                      isBlank(data.address)
                        ? {
                            text: "Please enter Prospect address to enable this checkbox.",
                          }
                        : null
                    }
                    className="ProspectForm-CheckboxField"
                    onChange={onChange2ndOccupantHasProspectAddressField}
                  />
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.address.street"
                    value={fields.secondOccupant?.address?.street}
                    label="Street*"
                    className="ProspectForm-TextField"
                    isDisabled={fields.relatedPartyIs2ndOccupant || fields.secondOccupant?.hasProspectAddress}
                    errorText={errors.secondOccupant?.address?.street}
                    onChange={changeField}
                  />
                </Col>
                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.address.city"
                    value={fields.secondOccupant?.address?.city}
                    label="City*"
                    className="ProspectForm-TextField"
                    isDisabled={fields.relatedPartyIs2ndOccupant || fields.secondOccupant?.hasProspectAddress}
                    errorText={errors.secondOccupant?.address?.city}
                    onChange={changeField}
                  />
                </Col>
                <Col md={4}>
                  <SelectField
                    name="secondOccupant.address.stateId"
                    value={fields.secondOccupant?.address?.stateId}
                    label="State*"
                    hasValueTooltip
                    options={mappedStates}
                    className="ProspectForm-TextField"
                    placeholder="Select State"
                    isDisabled={fields.relatedPartyIs2ndOccupant || fields.secondOccupant?.hasProspectAddress}
                    errorText={errors.secondOccupant?.address?.stateId}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col lg={4} md={4}>
                  <TextField
                    type="number"
                    name="secondOccupant.address.zip"
                    value={fields.secondOccupant?.address?.zip}
                    label="Zip Code*"
                    className="ProspectForm-TextField"
                    maxLength={5}
                    isDisabled={fields.relatedPartyIs2ndOccupant || fields.secondOccupant?.hasProspectAddress}
                    errorText={errors.secondOccupant?.address?.zip}
                    onChange={changeField}
                  />
                </Col>
                <Col lg={4} md={4}>
                  <PhoneField
                    name="secondOccupant.cellPhone"
                    value={fields.secondOccupant?.cellPhone}
                    label="Cell Phone #*"
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.cellPhone}
                    isDisabled={fields.relatedPartyIs2ndOccupant}
                    onChange={changeField}
                    renderLabelIcon={() => (
                      <Info id="second-occupant-cell-phone-hint" className="ProspectForm-InfoIcon" />
                    )}
                    tooltip={{
                      ...cellPhoneTooltipHint,
                      target: "second-occupant-cell-phone-hint",
                    }}
                  />
                </Col>
                <Col md={4}>
                  <TextField
                    type="text"
                    name="secondOccupant.email"
                    value={fields.secondOccupant?.email}
                    label="Email*"
                    isDisabled={fields.relatedPartyIs2ndOccupant}
                    className="ProspectForm-TextField"
                    errorText={errors.secondOccupant?.email || _2ndOccupantEmailError?.message}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
          )}
        </Scrollable>

        <div className="ProspectForm-Footer">
          <Button outline color="success" onClick={onCancel}>
            Cancel
          </Button>

          <Button color="success" disabled={isFetching || !isValid}>
            Save
          </Button>
        </div>
      </Form>

      {isDelete2ndOccupantConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          title="2nd occupant data will be deleted"
          onCancel={() => toggleDelete2ndOccupantConfirmDialog(false)}
          onConfirm={onConfirm2ndOccupantDeletion}
        />
      )}

      {isOverwrite2ndOccupantConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          title="The entered data will be overwritten with Related Party data. Please confirm."
          onCancel={() => toggleOverwrite2ndOccupantConfirmDialog(false)}
          onConfirm={onConfirm2ndOccupantOverwriting}
        />
      )}

      {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
    </>
  );
}

export default compose(memo, withAutoSave())(ProspectForm);
