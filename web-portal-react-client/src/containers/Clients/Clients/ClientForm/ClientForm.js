import React, { memo, useCallback, useEffect, useMemo, useReducer, useState } from "react";

import { compact, find, findWhere, first, isNumber, omit, pick, reduce, reject, size, sortBy } from "underscore";

import { trim } from "lodash";

import moment from "moment";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { useDebouncedCallback } from "use-debounce";
import service from "services/DirectoryService";
import directoryService from "services/DirectoryService";
import { Button, Col, Form, Row } from "reactstrap";

import { AlertPanel, Loader, OutsideClickListener } from "components";

import { ConfirmDialog } from "components/dialogs";

import {
  CheckboxField,
  DateField,
  FileField,
  PhoneField,
  RadioGroupField,
  SelectField,
  TextField,
} from "components/Form";

import { EditButton } from "components/buttons";

import { withAutoSave, withTooltip } from "hocs";

import {
  useAuthUser,
  useCustomFormFieldChange,
  useDirectoryData,
  useForm,
  useResponse,
  useScrollable,
  useScrollToFormError,
  useSelectOptions,
  useToggle,
} from "hooks/common";

import {
  useGendersQuery,
  useInsuranceNetworksQuery,
  useMaritalStatusesQuery,
  useRacesQuery,
  useStatesQuery,
} from "hooks/business/directory";

import {
  useAttorneyTypesQuery,
  useCommunitiesQuery,
  useMarketplaceLanguagesQuery,
  useOrganizationsQuery,
} from "hooks/business/directory/query";

import {
  useClientDetails,
  usePrimaryContactsQuery,
  useUniqEmailValidation,
  useUniqInCommunityValidation,
  useUniqSSnInCommunityValidation,
} from "hooks/business/client";

import { useCareTeamIncomingInvitationsExistQuery } from "hooks/business/care-team";

import * as clientFormActions from "redux/client/form/clientFormActions";
import * as clientDetailsActions from "redux/client/details/clientDetailsActions";

import ClientEntity, { Attorney, ContactItem, HousingVouchers, Insurance } from "entities/Client";

import ClientInsuranceAuthorizationEntity from "entities/ClientInsuranceAuthorization";

import ClientValidator from "validators/ClientFormSchemeValidator";

import { HIE_CONSENT_POLICIES } from "lib/Constants";

import { ReactComponent as Warning } from "images/alert-yellow.svg";
import { ReactComponent as Close } from "images/close.svg";

import {
  allAreEmpty,
  allAreNotEmpty,
  DateUtils as DU,
  isEmpty,
  isInteger,
  isNotEmpty,
  omitEmptyPropsDeep,
} from "lib/utils/Utils";

import { addAsterix } from "lib/utils/StringUtils";

import { getTimeZoneAbbr, isSameDay, setTime } from "lib/utils/DateUtils";

import { getProperty, isBlank, isNotBlank, isNotEmptyOrBlank } from "lib/utils/ObjectUtils";

import { isUnary, map } from "lib/utils/ArrayUtils";

import { ReactComponent as Info } from "images/info.svg";

import "./ClientForm.scss";
import clientService from "services/ClientService";
import useEthnicityQuery from "../../../../hooks/business/directory/useEthnicityQuery";

const { List } = require("immutable");

const YES_NO_OPTIONS = [
  { value: true, label: "Yes" },
  { value: false, label: "No" },
];

const PRIMARY_CONTACT_NOTIFICATION_OPTIONS = [
  { value: "EMAIL", label: "Email" },
  { value: "PHONE", label: "Phone" },
  { value: "CHAT", label: "Chat" },
];

const { OPT_IN, OPT_OUT } = HIE_CONSENT_POLICIES;

const HIE_CONSENT_POLICY_OPTIONS = [
  {
    value: OPT_OUT,
    label: (
      <>
        <p className="mb-0">
          <strong>OPT-OUT for all health information: </strong>I DO NOT want any of my information visible within the
          HIEs in which Simply Connect participates.
        </p>
        <ul className="mb-0">
          <li>
            I understand that the applicable health information received by any Simply Connect provider WILL NOT BE
            VISIBLE in the HIEs in which Simply Connect participates. THIS INCLUDES EMERGENCY SITUATIONS.
          </li>
          <li>
            I understand that I am free to revoke this Opt-Out request at any time and can do so by completing a new
            Opt-In/Opt-Out form.
          </li>
          <li>
            I understand that this request only applies to sharing my health information with HIEs and that a health
            care provider may request and receive my medical information from other providers using other methods
            permitted by law. If you have previously opted out of participating in HIEs and want to reverse that
            decision, check the box below. Your health information from the period during which you had opted-out may be
            available through the HIEs after you decide to opt back in.
          </li>
        </ul>
      </>
    ),
  },
  {
    value: OPT_IN,
    label: (
      <>
        <strong>OPT-IN/Cancel OPT-OUT:</strong> I WANT my information visible in the HIEs in which Simply Connect
        participates.
      </>
    ),
  },
];
const HIE_CONSENT_POLICY_OBTAINED_BY_OPTIONS = [
  { value: "CLIENT", text: "Client" },
  { value: "RESPONSIBLE_PARTY", text: "Responsible Party" },
  { value: "REPRESENTATIVE", text: "Representative" },
];

const HIE_CONSENT_POLICY_OBTAINED_FROM_STATE = "State Policy";

const scrollableStyles = { flex: 1 };

const { format, formats } = DU;

const DATE_FORMAT = formats.americanMediumDate;

const NO_COMMUNITY_ERROR_TEXT = "There is no community created for current organization.";

const getData = (fields) => {
  let data = fields.toJS();

  const primaryContact = data.primaryContact;

  if (primaryContact) {
    data.primaryContact = {
      ...pick(primaryContact, "typeName", "notificationMethodName"),
      careTeamMemberId: primaryContact.typeName !== "SELF" ? primaryContact.careTeamMemberId : undefined,
    };
  }

  const insuranceAuthorizations = reject(data.insuranceAuthorizations, (o) => isBlank(omit(o, "index")));

  const insurances = reject(data.insurances, isBlank);

  return {
    ...data,
    insurances,
    insuranceAuthorizations,
    email: data.hasNoEmail ? null : data.email,
    shouldRemoveAvatar: data.avatar === null && data.avatarId === null,
  };
};

const formatSSN = (ssn) => (ssn ? `###-##-${ssn.substring(5, 9)}` : "");
const formatStringDate = (value) => (value ? moment(value, "MM/DD/YYYY").toDate().getTime() : null);

const avatarHint = {
  placement: "top",
  target: "avatar-hint",
  render: () => (
    <ul className="ClientForm-TooltipBody">
      <li>The maximum file size for uploads is 1 MB</li>
      <li>Only image files (JPG, GIF, PNG) are allowed</li>
      <li>Recommended aspect ratio is 3:1</li>
      <li>Recommended image resolution is 42x147</li>
    </ul>
  ),
};

const cellPhoneHint = {
  placement: "top",
  target: "cell-phone-hint",
  render: () => (
    <ul className="ClientForm-TooltipBody">
      <li>Use digits only and "+" before country code.</li>
      <li>Otherwise no spaces, dashes, or special symbols allowed.</li>
      {/* <li>Digits only allowed</li>
      <li>No spaces, dashes, or special symbols</li>
      <li>Country code is required</li>
      <li>‘+’ may be a leading symbol</li>*/}
    </ul>
  ),
};

const tCodeHint = {
  placement: "top",
  target: "tCode-hint",
  render: () => (
    <ul className="ClientForm-TooltipBody">
      <li>only 7 digits are allowed</li>
      <li>no special symbols</li>
    </ul>
  ),
};

const homePhoneHint = {
  ...cellPhoneHint,
  target: "home-phone-hint",
};

const mapDispatchToProps = (dispatch) => ({
  actions: {
    ...bindActionCreators(clientFormActions, dispatch),
    details: bindActionCreators(clientDetailsActions, dispatch),
  },
});

const extraErrorsInitialState = {
  community: "",
};

function extraErrorsReducer(state, action) {
  switch (action.type) {
    case "noCommunities":
      return {
        ...state,
        community: NO_COMMUNITY_ERROR_TEXT,
      };

    case "clear": {
      return {
        ...state,
        [action.payload]: "",
      };
    }
  }
}

function valueTextMapper({ name, title }) {
  return { value: name, text: title };
}
function languageTextMapper({ id, label }) {
  return { value: id, text: label };
}

const CareTeamMemberHint = withTooltip({
  text: "To enable this option, please add a client care team member with Family/Member or Person Receiving Services role",
})(Info);

const PrimaryContactSectionHint = withTooltip({
  text: "Primary contact and notification method will be used for sending e-sign requests when multiple recipients are selected",
})(Info);

const NotificationMethodChatHint = withTooltip({
  text: "The Chat feature is not enabled for the user(s) selected. Please contact the Simply Connect support team to enable the Chat feature",
})(Info);

function ClientForm({
  clientId,
  isClientEmailRequired = false,
  isCanShowHousingVoucher = false,
  actions,

  communityId,
  organizationId,

  autoSaveAdapter,

  onCancel,
  onSubmitSuccess,
  canShowReminder,
  clientFullName,
  canEdit,
  changeWaringDialog,

  ...props
}) {
  const { data: marketplaceLanguages = [] } = useMarketplaceLanguagesQuery({}, { staleTime: 0 });

  const mappedMarketplaceLanguages = useMemo(
    () => map(marketplaceLanguages, languageTextMapper),
    [marketplaceLanguages],
  );
  let isEditing = isNumber(clientId);
  const user = useAuthUser();
  const [isEditableSSN, setEditableSSN] = useState(!isEditing);

  const [hasInsuranceAuthorizations, setHasInsuranceAuthorizations] = useState(false);

  const [isHieConsentPolicyChanged, setHieConsentPolicyChanged] = useState(false);

  const [systemRoleIds, setSystemRoleIds] = useState([]);
  const [systemRoleData, setSystemRoleData] = useState([]);

  const [contactSearchData, setContactSearchData] = useState("");
  const needRemoveRoleCode = [
    "ROLE_VENDOR_ADMIN_CODE",
    "ROLE_PERSON_RECEIVING_SERVICES",
    "ROLE_PARENT_GUARDIAN",
    "ROLE_QUALITY_ASSURANCE_CODE",
    "ROLE_VENDOR_CONCIERGE_CODE",
  ];

  const { state: details, fetchIf: fetchDetailsIf } = useClientDetails(clientId);
  const getSystemRole = () => {
    directoryService.findSystemRoles().then((res) => {
      if (res.success) {
        setSystemRoleData(res.data);
        let resultArray = res.data.filter((item) => {
          return !needRemoveRoleCode.includes(item.name);
        });
        const idArray = resultArray.map((item) => {
          return item.id;
        });
        setSystemRoleIds(idArray);
      }
    });
  };

  useEffect(() => {
    getSystemRole();
  }, []);

  const client = details?.data;
  const canEditSSN = client?.canEditSsn;
  const hasEmptySSN = trim(client?.ssn).length === 0;

  const { fields, errors, isValid, validate, isChanged, clearFields, changeField, changeFields } = useForm(
    "ClientForm",
    ClientEntity,
    ClientValidator,
  );

  const insuranceAuthorizationCount = fields.insuranceAuthorizations.size;
  const { changeDateField } = useCustomFormFieldChange(changeField);

  const { data: primaryContacts } = usePrimaryContactsQuery(
    {
      clientId,
    },
    {
      enabled: isInteger(clientId),
      cacheTime: 0,
      onSuccess: ({ data }) => {
        if (size(data) === 1 && !fields.primaryContact?.careTeamMemberId) {
          changeField("primaryContact.careTeamMemberId", data[0]?.careTeamMemberId);
        }
      },
    },
  );

  const isPrimaryContactSelfAndEmail =
    fields.primaryContact?.typeName === "SELF" && fields.primaryContact?.notificationMethodName === "EMAIL";

  const { data: organizations } = useOrganizationsQuery();

  const { data: communities = [], isFetching: isFetchingCommunities } = useCommunitiesQuery(
    { organizationId: fields.organizationId },
    {
      enabled: isInteger(fields.organizationId),
      onSuccess: (data) => {
        if (isEmpty(data)) {
          dispatchError({ type: "noCommunities" });
        }
      },
    },
  );

  let filteredCommunities = useMemo(() => communities.filter((c) => c.canAddClient), [communities]);

  const isHasNoSSNEnabled = useMemo(
    () => canEditSSN || !(isEditing && hasEmptySSN),
    [isEditing, hasEmptySSN, canEditSSN],
  );

  const ssn = useMemo(() => {
    if (isEditableSSN || hasEmptySSN || !isEditing) {
      return fields.ssn;
    }
    return formatSSN(fields.ssn);
  }, [isEditableSSN, isEditing, hasEmptySSN, fields.ssn]);

  const isHieConsentPolicyDisabled = useMemo(
    () => !fields.communityId || !fields.hieConsentPolicyName || !isHieConsentPolicyChanged,
    [fields, isHieConsentPolicyChanged],
  );

  let { race, states, genders, maritalStatuses, insuranceNetworks, ethnicity } = useDirectoryData({
    race: ["race"],
    states: ["state"],
    genders: ["gender"],
    ethnicity: ["ethnicity"],
    maritalStatuses: ["marital", "status"],
    insuranceNetworks: ["insurance", "network"],
  });

  const { data: attorneyTypes } = useAttorneyTypesQuery();

  const mappedAttorneyTypes = useMemo(() => map(attorneyTypes, valueTextMapper), [attorneyTypes]);

  const [validateEmailWithinOrganization, emailError] = useUniqEmailValidation({
    clientId,
    email: fields.email,
    organizationId: fields.organizationId,
  });

  const [validateSSNWithinCommunity, ssnError] = useUniqSSnInCommunityValidation({
    clientId,
    ssn: fields.ssn,
    communityId: fields.communityId,
  });

  const [validateMemberNumberWithinCommunity, memberNumberError] = useUniqInCommunityValidation({
    clientId,
    fieldName: "Member Number",
    memberNumber: fields.memberNumber,
    communityId: fields.communityId,
  });

  const [validateMedicareNumberWithinCommunity, medicareNumberError] = useUniqInCommunityValidation({
    clientId,
    fieldName: "Medicare Number",
    medicareNumber: fields.medicareNumber,
    communityId: fields.communityId,
  });

  const [validateMedicaidNumberWithinCommunity, medicaidNumberError] = useUniqInCommunityValidation({
    clientId,
    fieldName: "Medicaid Number",
    medicaidNumber: fields.medicaidNumber,
    communityId: fields.communityId,
  });
  const [TCodeErrors, setTCodeErrors] = useState([]);
  const onValidateUniqTCodes = (data) => {
    let flag = true;
    const error = [];
    if (data) {
      for (let key in data) {
        if (data[key].unique) {
          error.push("");
        } else {
          flag = false;
          error.push("T-code already exists");
        }
      }
    }
    setTCodeErrors(error);
    return flag;
  };

  const checkTCodes = useCallback(async () => {
    const params = {
      clientId,
      organizationId: fields.organizationId,
      tCodes: fields.housingVouchers
        ?.map((item) => {
          return item.tCode;
        })
        .join(","),
    };
    const data = await service.validateHousingVouchersTCode(params);
    if (data.success) {
      return onValidateUniqTCodes(data.data.tCodes);
    }
    return false;
  }, [clientId, fields]);

  const isValidForm = isValid && allAreEmpty(ssnError, memberNumberError, medicareNumberError, medicaidNumberError);

  const [isFetching, setIsFetching] = useState(false);
  const [isValidationNeed, setValidationNeed] = useState(props.isValidationNeed);

  const [customRaceOption, setCustomRaceOption] = useState();
  const [extraErrors, dispatchError] = useReducer(extraErrorsReducer, extraErrorsInitialState);

  const [isPrimaryContactDeletionConfirmDialogOpen, togglePrimaryContactDeletionConfirmDialog] = useState(false);

  const areRequiredPOAFieldsFilled = useMemo(() => {
    const o = fields.attorneys?.last();

    return !o || (isNotEmpty(o?.firstName) && isNotEmpty(o?.lastName));
  }, [fields.attorneys]);

  const areRequiredContactFieldsFilled = useMemo(() => {
    const o = fields.contact?.last();

    return !o || (isNotEmpty(o?.firstName) && isNotEmpty(o?.lastName));
  }, [fields.contact]);

  const shouldCheckCareTeamInvitations =
    fields.hieConsentPolicyName === OPT_OUT && fields.primaryContact?.typeName === "CARE_TEAM_MEMBER";

  const { data: careTeamInvitationsExist } = useCareTeamIncomingInvitationsExistQuery(
    { clientId },
    { enabled: isInteger(clientId) && shouldCheckCareTeamInvitations },
  );

  const races = useMemo(
    () => (customRaceOption ? sortBy([...race, customRaceOption], "text") : race),
    [race, customRaceOption],
  );
  const [careTeamManagerData, setCareTeamManagerData] = useState([]);
  const [isFetchingCareTeam, setIsFetchingCareTeam] = useState(false);
  const communitiesOptions = useSelectOptions(filteredCommunities, { textProp: "name" });
  const mappedOrganizations = useSelectOptions(organizations, { textProp: "label" });

  const raceOptions = useSelectOptions(races);
  const careTeamManagerOptions = useSelectOptions(careTeamManagerData, { textProp: "name", valueProp: "id" });
  const stateOptions = useSelectOptions(states, { textProp: "label" });
  const genderOptions = useSelectOptions(genders, { textProp: "label" });
  const ethnicityOptions = useSelectOptions(ethnicity);
  const maritalStatusOptions = useSelectOptions(maritalStatuses, { textProp: "label" });
  const insuranceNetworkOptions = useSelectOptions(insuranceNetworks);

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".ClientForm", scroll);

  const onResponse = useResponse({
    onFailure: actions.error?.change,
    onSuccess: useCallback(
      ({ data }) => {
        onSubmitSuccess(data);
      },
      [onSubmitSuccess],
    ),
    onUnknown: actions.error?.change,
  });

  const isInactiveCareTeamMember = useMemo(
    () =>
      fields.primaryContact?.careTeamMemberId === client?.primaryContact?.careTeamMemberId &&
      !client?.primaryContact?.active,
    [fields, client],
  );

  const selectedPrimaryContact = details?.data?.primaryContact;
  const hasInactivePrimaryContact =
    !selectedPrimaryContact?.active &&
    !!selectedPrimaryContact?.careTeamMemberId &&
    fields.primaryContact?.typeName === "CARE_TEAM_MEMBER";

  const areRequiredHousingVouchersFieldsFilled = useMemo(() => {
    const o = fields.housingVouchers?.last();

    return !o || isNotEmpty(o?.tCode);
  }, [fields.housingVouchers]);

  const primaryContactOptions = useSelectOptions(
    sortBy(
      compact([
        hasInactivePrimaryContact && {
          fullName: `${selectedPrimaryContact?.firstName} ${selectedPrimaryContact?.lastName}`,
          careTeamMemberId: selectedPrimaryContact.careTeamMemberId,
        },
        ...(primaryContacts?.data || []),
      ]),
      "fullName",
    ),
    { textProp: "fullName", valueProp: "careTeamMemberId" },
  );

  const validationOptions = useMemo(() => {
    const primaryContact = fields.primaryContact;
    const primaryContactDetails = find(
      primaryContacts?.data,
      (c) => c.careTeamMemberId === primaryContact?.careTeamMemberId,
    );

    return {
      included: {
        hasInsuranceAuthorizations,
        shouldValidateSSN: !fields.hasNoSSN,
        shouldValidateMedicaidNumber: !fields.hasNoMedicaidNumber,
        shouldValidateMedicareNumber: !fields.hasNoMedicareNumber,
        inactiveCareTeamMemberId: isInactiveCareTeamMember ? primaryContact?.careTeamMemberId : undefined,
        isChatInactive:
          primaryContact?.notificationMethodName === "CHAT" &&
          ((primaryContact?.typeName === "SELF" && !client?.associatedContact?.chatEnabled) ||
            (primaryContact?.typeName === "CARE_TEAM_MEMBER" && !primaryContactDetails?.chatEnabled)),
        isEmailInactive:
          primaryContact?.notificationMethodName === "EMAIL" &&
          primaryContact?.typeName === "CARE_TEAM_MEMBER" &&
          !primaryContactDetails?.hasEmail,
      },
    };
  }, [client, fields, primaryContacts, isInactiveCareTeamMember, hasInsuranceAuthorizations]);

  const onValidate = useCallback(
    (options) => {
      return validate(options)
        .then(() => (fields.hasNoEmail ? Promise.resolve() : validateEmailWithinOrganization()))
        .then(() => (fields.hasNoSSN ? Promise.resolve() : validateSSNWithinCommunity()))
        .then(validateMemberNumberWithinCommunity)
        .then(validateMedicareNumberWithinCommunity)
        .then(validateMedicaidNumberWithinCommunity);
    },
    [
      fields,
      validate,
      validateSSNWithinCommunity,
      validateEmailWithinOrganization,
      validateMemberNumberWithinCommunity,
      validateMedicareNumberWithinCommunity,
      validateMedicaidNumberWithinCommunity,
    ],
  );

  const onChangeSearchText = (_, value) => {
    setContactSearchData(value);
  };

  const submit = useCallback(
    (e = null) => {
      e && e.preventDefault();
      setIsFetching(true);
      let newFields = fields;
      if (isEmpty(fields.id)) {
        newFields = fields.set("canViewHousingVouchers", isCanShowHousingVoucher);
      }
      onValidate(validationOptions)
        .then(async () => {
          checkTCodes().then(async (result) => {
            if (result) {
              setIsFetching(true);
              onResponse(await actions.submit(getData(newFields)));
              setIsFetching(false);
              setValidationNeed(false);
              setTCodeErrors([]);
            }
          });
        })
        .catch(() => {
          onScroll();
          setValidationNeed(true);
          setIsFetching(false);
        });
    },
    [fields, actions, onScroll, onResponse, onValidate, validationOptions, isCanShowHousingVoucher, checkTCodes],
  );

  const onAutoSave = useCallback(() => {
    if (!isEditing) return;

    onValidate(validationOptions)
      .then(async () => {
        onResponse(
          await actions.submit({
            ...getData(fields),
            isAutoSave: true,
          }),
        );
        setValidationNeed(false);
      })
      .catch(async (error) => {
        const data = getData(fields);

        Object.keys(error).forEach((key) => {
          if (key === "address") {
            Object.keys(error.address).forEach((key) => (data.address[key] = client.address[key]));
          }

          if (key === "medicaidNumber" || key === "medicareNumber") {
            if (data.hasNoSSN && !!client.ssn) {
              data.ssn = client.ssn;
              data.hasNoSSN = false;
            }
          }

          data[key] = client[key];
        });

        onResponse(
          await actions.submit({
            ...data,
            isAutoSave: true,
          }),
        );
      })
      .finally(() => {
        setIsFetching(false);
      });
  }, [client, fields, actions, isEditing, onResponse, onValidate, validationOptions]);

  useEffect(() => {
    if (autoSaveAdapter && isEditing) {
      autoSaveAdapter.init({
        onSave: () => onAutoSave(),
      });
    }
  }, [fields, isEditing, onAutoSave, autoSaveAdapter]);

  function validateIf() {
    if (isValidationNeed) {
      validate(validationOptions)
        .then(() => setValidationNeed(false))
        .catch(() => setValidationNeed(true));
    }
  }

  function validateEmailIf() {
    if (!!fields.email && isNumber(fields.organizationId)) {
      onValidateEmail();
    }
  }

  function validateSSNIf() {
    if (!!fields.ssn && isNumber(fields.communityId)) {
      onValidateSSN();
    }
  }

  function validateMemberNumberIf() {
    if (!!fields.memberNumber && isNumber(fields.communityId)) {
      onValidateMemberNumber();
    }
  }

  function validateMedicareNumberIf() {
    if (!!fields.medicareNumber && isNumber(fields.communityId)) {
      onValidateMedicareNumber();
    }
  }

  function validateMedicaidNumberIf() {
    if (!!fields.medicaidNumber && isNumber(fields.communityId)) {
      onValidateMedicaidNumber();
    }
  }

  function fetchDetails() {
    fetchDetailsIf(isEditing);
  }

  function setDefaultData() {
    if (!isEditing) {
      changeFields(
        {
          communityId,
          organizationId,
        },
        true,
      );
    }
  }

  function init() {
    if (client) {
      let data = omitEmptyPropsDeep(client);

      changeFields(
        {
          ...data,
          isActive: client.isActive,
          retained: client.retained,
          hasNoEmail: !isClientEmailRequired && isEmpty(data.email),
          hasNoSSN: hasEmptySSN,
          manuallyCreated: client.manuallyCreated,
        },
        true,
      );
    }
  }

  function setDefaultInsurance() {
    if (!isEditing || (client && isEmpty(client.insurances))) {
      changeField("insurances", List([Insurance()]));
    }
  }

  function clearCommunityIf() {
    return () => {
      if (isNumber(fields.organizationId)) {
        changeField("communityId", null);
      }
    };
  }

  function setDefaultCommunity() {
    if (!isEditing && isUnary(filteredCommunities)) {
      const community = first(filteredCommunities);

      changeFields(
        {
          communityId: community.id,
          hieConsentPolicyName: community.hieConsentPolicyName,
          hieConsentPolicyObtainedFrom: HIE_CONSENT_POLICY_OBTAINED_FROM_STATE,
        },
        true,
      );
    } else if (!isEditing && isInteger(communityId) && isNotEmpty(communities) && communityId === fields.communityId) {
      const community = findWhere(communities, { id: communityId });

      if (community) {
        changeFields(
          {
            hieConsentPolicyName: community.hieConsentPolicyName,
            hieConsentPolicyObtainedFrom: HIE_CONSENT_POLICY_OBTAINED_FROM_STATE,
          },
          true,
        );
      }
    }
  }

  function setCustomRaceIfNeed() {
    let hasCustomRace = isNumber(fields.raceId) && !raceOptions.find((o) => o.value === fields.raceId);

    if (hasCustomRace) {
      setCustomRaceOption({ id: fields.raceId, text: fields.race });
    }
  }

  function scrollToErrorRightAway() {
    if (props.isValidationNeed && isNumber(fields.id)) {
      setTimeout(onScroll);
    }
  }

  const validateFieldIfNeed = useCallback(
    (field, cb) => {
      let detail = client && client[field];
      let shouldValidate = !(isEditing && fields[field] === detail);

      return shouldValidate ? cb().catch(console.log) : Promise.resolve();
    },
    [client, fields, isEditing],
  );

  const onValidateEmail = useDebouncedCallback(
    () => (isEditing || fields.hasNoEmail ? Promise.resolve() : validateEmailWithinOrganization()),
    300,
  );

  const validateSSN = useCallback(
    () => validateFieldIfNeed("ssn", validateSSNWithinCommunity),
    [validateFieldIfNeed, validateSSNWithinCommunity],
  );

  const onValidateSSN = useDebouncedCallback(validateSSN, 300);

  const validateMemberNumber = useCallback(
    () => validateFieldIfNeed("memberNumber", validateMemberNumberWithinCommunity),
    [validateFieldIfNeed, validateMemberNumberWithinCommunity],
  );

  const onValidateMemberNumber = useDebouncedCallback(validateMemberNumber, 300);

  const validateMedicareNumber = useCallback(
    () => validateFieldIfNeed("medicareNumber", validateMedicareNumberWithinCommunity),
    [validateFieldIfNeed, validateMedicareNumberWithinCommunity],
  );

  const onValidateMedicareNumber = useDebouncedCallback(validateMedicareNumber, 300);

  const validateMedicaidNumber = useCallback(
    () => validateFieldIfNeed("medicaidNumber", validateMedicaidNumberWithinCommunity),
    [validateFieldIfNeed, validateMedicaidNumberWithinCommunity],
  );

  const onValidateMedicaidNumber = useDebouncedCallback(validateMedicaidNumber, 300);

  const onChangeEmailField = useCallback(
    (name, value) => {
      changeFields({
        email: value,
        hasNoEmail: !isClientEmailRequired && isEmpty(value) && !isPrimaryContactSelfAndEmail,
      });
    },
    [changeFields, isClientEmailRequired, isPrimaryContactSelfAndEmail],
  );

  const onChangeNotificationMethod = useCallback(
    (_, value) => {
      if (fields.primaryContact?.typeName === "SELF" && value === "EMAIL")
        return changeFields({
          hasNoEmail: false,
          primaryContact: {
            ...fields.primaryContact,
            notificationMethodName: "EMAIL",
          },
        });

      changeField("primaryContact.notificationMethodName", value);
    },
    [fields, changeFields, changeField],
  );

  const onChangeContactType = useCallback(
    (_, value) => {
      if (fields.primaryContact?.notificationMethodName === "EMAIL" && value === "SELF")
        return changeFields({
          hasNoEmail: false,
          primaryContact: {
            ...fields.primaryContact,
            typeName: "SELF",
          },
        });

      changeField("primaryContact.typeName", value);
    },
    [fields, changeFields, changeField],
  );

  const onChangeCareTeamMember = useCallback(
    (_, value) => {
      changeFields({
        primaryContact: {
          ...fields.primaryContact,
          notificationMethodName: undefined,
          careTeamMemberId: value,
        },
      });
    },
    [fields, changeFields],
  );

  const onSubmit = useCallback(
    (e) => {
      if (shouldCheckCareTeamInvitations) {
        e && e.preventDefault();

        setIsFetching(true);

        onValidate(validationOptions)
          .then(() => {
            togglePrimaryContactDeletionConfirmDialog(true);
          })
          .catch(() => {
            onScroll();
            setValidationNeed(true);
          })
          .finally(() => {
            setIsFetching(false);
          });
      } else return submit(e);
    },
    [submit, onScroll, onValidate, validationOptions, shouldCheckCareTeamInvitations],
  );

  const onConfirmSubmit = useCallback(() => {
    submit();
    togglePrimaryContactDeletionConfirmDialog(false);
  }, [submit]);

  const onEditSSN = useCallback(() => {
    setEditableSSN(true);
  }, [setEditableSSN]);

  const hideSSN = useCallback(() => {
    if (isHasNoSSNEnabled && isEditing) {
      setEditableSSN(false);
    }
  }, [isEditing, isHasNoSSNEnabled, setEditableSSN]);

  const onChangeRaceField = useCallback(
    (field, value) => {
      changeFields({
        [field]: value,
        race: raceOptions.find((o) => o.value === value)?.text,
      });
    },
    [raceOptions, changeFields],
  );

  const onChangeBirthDate = useCallback(
    (field, value) => {
      changeField(field, value ? format(value, DATE_FORMAT) : null);
    },
    [changeField],
  );

  const onChangeAvatarField = useCallback(
    (field, value) => {
      changeFields({
        [field]: value,
        avatarName: value ? value.name : "",
      });

      if (!value) {
        changeField("avatarId", null);
      }
    },
    [changeField, changeFields],
  );

  const onChangeOrganization = useCallback(
    (name, value) => {
      clearFields(
        "hieConsentPolicyName",
        "hieConsentPolicyObtainedFrom",
        "hieConsentPolicyObtainedBy",
        "hieConsentPolicyObtainedDate",
      );

      changeFields({
        [name]: value,
        communityId: null,
      });
    },
    [clearFields, changeFields],
  );

  const onChangeCommunity = useCallback(
    (name, value) => {
      if (!isEditing) {
        const community = findWhere(filteredCommunities, { id: value });

        clearFields("hieConsentPolicyObtainedFrom", "hieConsentPolicyObtainedBy", "hieConsentPolicyObtainedDate");

        changeFields({
          [name]: value,
          hieConsentPolicyName: community?.hieConsentPolicyName,
          ...(!!value && { hieConsentPolicyObtainedFrom: HIE_CONSENT_POLICY_OBTAINED_FROM_STATE }),
        });

        if (value) {
          changeDateField("hieConsentPolicyObtainedDate", new Date());
        }
      } else changeField(name, value);
    },
    [isEditing, clearFields, changeField, changeFields, changeDateField, filteredCommunities],
  );
  const onChangeHieConsentPolicy = useCallback(
    (name, value) => {
      clearFields("hieConsentPolicyObtainedBy", "hieConsentPolicyObtainedFrom");
      if (value === "OPT_OUT") {
        clearFields("careTeamManager");
      }
      changeField(name, value);
      changeDateField("hieConsentPolicyObtainedDate", new Date());
      setHieConsentPolicyChanged(true);
    },
    [clearFields, changeField, changeDateField, setHieConsentPolicyChanged],
  );

  useRacesQuery();
  useStatesQuery();
  useGendersQuery();
  useEthnicityQuery();
  useMaritalStatusesQuery();
  useInsuranceNetworksQuery();

  const contactTypeOptions = [
    {
      value: 0,
      text: "Emergency",
    },
    {
      value: 1,
      text: "Family",
    },
    {
      value: 2,
      text: "Spouse",
    },
    {
      value: 3,
      text: "Children",
    },
    {
      value: 4,
      text: "Others",
    },
  ];

  const primaryContactTypeOptions = useMemo(() => {
    const isCareTeamMemberDisabled = isEmpty(primaryContacts?.data);

    return [
      { value: "SELF", label: "Self" },
      {
        value: "CARE_TEAM_MEMBER",
        label: (
          <div className="d-flex">
            Care team member
            {isCareTeamMemberDisabled && <CareTeamMemberHint className="ClientForm-InfoIcon" isTooltipEnabled />}
          </div>
        ),
        isDisabled: isCareTeamMemberDisabled,
      },
    ];
  }, [primaryContacts]);

  const primaryContactNotificationOptions = useMemo(
    () =>
      reduce(
        PRIMARY_CONTACT_NOTIFICATION_OPTIONS,
        (options, option) => {
          const isCareTeamMemberTypeSelected = fields.primaryContact?.typeName === "CARE_TEAM_MEMBER";
          const careTeamMember = find(
            primaryContacts?.data,
            (c) => c.careTeamMemberId === fields.primaryContact?.careTeamMemberId,
          );

          if (option.value === "CHAT") {
            if (!isEditing || (isEditing && !client?.associatedContact?.id)) return options;

            if (
              !client?.associatedContact?.chatEnabled ||
              (isCareTeamMemberTypeSelected && !careTeamMember?.chatEnabled)
            )
              return [
                ...options,
                {
                  ...option,
                  isDisabled: true,
                  label: (
                    <div className="d-flex">
                      {option.label}
                      <NotificationMethodChatHint className="ClientForm-InfoIcon" isTooltipEnabled />
                    </div>
                  ),
                },
              ];

            return [...options, option];
          }

          return [...options, option];
        },
        [],
      ),
    [client, fields, isEditing, primaryContacts],
  );

  const onAddAttorney = useCallback(
    (e) => {
      e.preventDefault();

      if (fields.attorneys.size < 4) {
        changeField("attorneys", fields.attorneys.push(Attorney()));
      }
    },
    [fields, changeField],
  );

  const onAddContact = useCallback(
    (e) => {
      e.preventDefault();

      if (fields.contact.size < 4) {
        changeField("contact", fields.contact.push(ContactItem()));
      }
    },
    [fields, changeField],
  );

  const onAddHousingVouchers = useCallback(
    (e) => {
      e.preventDefault();

      changeField("housingVouchers", fields.housingVouchers.push(HousingVouchers()));
    },
    [fields, changeField],
  );
  const [selectedTCode, setSelectedTCode] = useState(null);
  const [isDeleteTCodeConfirmDialogOpen, toggleDeleteTCodeConfirmDialog] = useToggle();
  const confirmDeleteTCodeTitle = `The T-code will be deleted.`;

  const onConfirmTCodeDelete = useCallback(() => {
    changeField("housingVouchers", fields.housingVouchers.splice(selectedTCode, 1));
    toggleDeleteTCodeConfirmDialog();
    setSelectedTCode(null);
  }, [fields, changeField, toggleDeleteTCodeConfirmDialog, selectedTCode]);

  const onCloseConfirmTCodeDeleteDialog = useCallback(
    (e) => {
      toggleDeleteTCodeConfirmDialog(false);
    },
    [toggleDeleteTCodeConfirmDialog],
  );

  const onRemoveHousingVouchers = useCallback(
    (e) => {
      setSelectedTCode(e);
      toggleDeleteTCodeConfirmDialog();
    },
    [toggleDeleteTCodeConfirmDialog],
  );

  const onAddInsuranceAuthorization = useCallback(
    (e) => {
      e.preventDefault();
      const o = fields.insuranceAuthorizations.last();

      if (isNotEmptyOrBlank(omit(o, "index"))) {
        changeField(
          "insuranceAuthorizations",
          fields.insuranceAuthorizations.push(
            ClientInsuranceAuthorizationEntity({
              index: insuranceAuthorizationCount,
            }).toJS(),
          ),
        );
      }
    },
    [fields, changeField, insuranceAuthorizationCount],
  );

  const onAddInsurance = useCallback(
    (e) => {
      e.preventDefault();

      if (fields.insurances.size < 3) {
        changeField("insurances", fields.insurances.push(Insurance().toJS()));
      }
    },
    [fields, changeField],
  );

  const onChangeInsuranceAuthorizationStartDateField = useCallback(
    (name, value) => {
      value = value ? value.getTime() : null;

      changeField(name, value);

      const endDateFieldName = name.replace("start", "end");
      const endDate = getProperty(fields.toJS(), endDateFieldName);

      if (allAreNotEmpty(value, endDate) && value > endDate) {
        changeField(endDateFieldName, setTime(endDate, value).valueOf());
      } else changeField(name, value);
    },
    [fields, changeField],
  );

  const onChangeInsuranceAuthorizationEndDateField = useCallback(
    (name, value) => {
      value = value ? value.getTime() : null;

      const startDateFieldName = name.replace("end", "start");
      const startDate = getProperty(fields.toJS(), startDateFieldName);

      if (allAreNotEmpty(value, startDate) && value < startDate) {
        changeField(name, setTime(startDate, value).valueOf());
      } else changeField(name, value);
    },
    [fields, changeField],
  );

  const onChangeAttorneyTypesField = useCallback(
    (field, value) => {
      changeField(field, value);
    },
    [changeField],
  );

  useEffect(validateIf, [isValidationNeed, onScroll, validate, validationOptions]);

  useEffect(validateSSNIf, [fields.ssn, fields.communityId, onValidateSSN]);
  useEffect(validateEmailIf, [fields.email, fields.organizationId, onValidateEmail]);
  useEffect(validateMemberNumberIf, [onValidateSSN, fields.communityId, fields.memberNumber, onValidateMemberNumber]);
  useEffect(validateMedicareNumberIf, [fields.medicareNumber, fields.communityId, onValidateMedicareNumber]);
  useEffect(validateMedicaidNumberIf, [fields.medicaidNumber, fields.communityId, onValidateMedicaidNumber]);

  useEffect(() => {
    setIsFetchingCareTeam(true);

    fields.organizationId &&
      fields.communityId &&
      systemRoleIds.length !== 0 &&
      clientService
        .findContactNoClient({
          organizationId: fields.organizationId,
          page: 0,
          size: 999,
          statuses: ["ACTIVE"],
          systemRoleIds: systemRoleIds,
        })
        .then((res) => {
          if (res.success) {
            const index = res?.data.some((item) => item.id === user.id);
            if (index) {
              setCareTeamManagerData(res.data);
            } else {
              setCareTeamManagerData([...new Set([user, ...res.data])]);
            }
            setIsFetchingCareTeam(false);
          }
        })
        .catch(() => {
          setIsFetchingCareTeam(false);
        });
  }, [fields.communityId, systemRoleIds]);

  useEffect(() => {
    if (isFetchingCommunities) {
      dispatchError({ type: "clear", payload: "community" });
    }
  }, [isFetchingCommunities]);

  useEffect(fetchDetails, [fetchDetailsIf, isEditing]);

  useEffect(setDefaultData, [isEditing, changeFields, communityId, organizationId]);

  useEffect(init, [client, hasEmptySSN, changeFields, isClientEmailRequired]);

  useEffect(setDefaultCommunity, [
    isEditing,
    communityId,
    communities,
    changeFields,
    fields.communityId,
    filteredCommunities,
  ]);

  useEffect(clearCommunityIf, [fields.organizationId, changeField]);

  useEffect(setCustomRaceIfNeed, [raceOptions, fields.race, fields.raceId]);

  useEffect(scrollToErrorRightAway, [onScroll, fields.id, props.isValidationNeed]);

  useEffect(() => {
    if (isEditing && insuranceAuthorizationCount > 0) {
      setHasInsuranceAuthorizations(true);
    }
  }, [isEditing, insuranceAuthorizationCount]);

  useEffect(() => {
    const authorizations = fields.insuranceAuthorizations;

    if (hasInsuranceAuthorizations && authorizations.size < 1) {
      changeField(
        "insuranceAuthorizations",
        authorizations.push(ClientInsuranceAuthorizationEntity({ index: 0 }).toJS()),
      );
    }
  }, [fields, changeField, hasInsuranceAuthorizations]);

  useEffect(() => {
    if (fields.hasNoSSN) {
      changeField("ssn", "");
    }
    if (fields.hasNoMedicaidNumber) {
      changeField("medicaidNumber", "");
    }
    if (fields.hasNoMedicareNumber) {
      changeField("medicareNumber", "");
    }
  }, [fields.hasNoSSN, fields.hasNoMedicaidNumber, fields.hasNoMedicareNumber, changeField]);

  useEffect(setDefaultInsurance, [client, isEditing, changeField]);
  const languageSpoken = useMemo(
    () => fields?.languageSpoken?.toJS() || details?.languageSpoken?.toJS(),
    [fields.languageSpoken, details],
  );
  const languageWritten = useMemo(
    () => fields?.languageWritten?.toJS() || details?.languageWritten?.toJS(),
    [fields.languageWritten, details],
  );
  return (
    <Form className="ClientForm" onSubmit={onSubmit}>
      {(isFetching || details.isFetching) && <Loader style={{ position: "fixed" }} hasBackdrop />}

      <Scrollable style={scrollableStyles}>
        <>
          {canShowReminder && (
            <div className="remindUsersToFillInInformationInEdit">
              <div className="remindUsersToFillInInformationInfoInEdit">
                Please update {clientFullName}'s record to include phone number or email to ensure medication intake
                tracking.
              </div>

              {canEdit && (
                <div className="remindUsersToFillInInformationDontShowInEdit" onClick={() => changeWaringDialog()}>
                  Don't show again
                </div>
              )}
            </div>
          )}
        </>

        <div className="ClientForm-Section ">
          <div className="ClientForm-SectionTitle">Community</div>
          <Row>
            <Col md={4}>
              <SelectField
                type="text"
                name="organizationId"
                value={fields.organizationId}
                options={mappedOrganizations}
                label="Organization*"
                className="ClientForm-SelectField"
                errorText={errors.organizationId}
                isDisabled={!fields.isActive || isEditing}
                onChange={onChangeOrganization}
              />
            </Col>

            {isEditing && (
              <Col md={4}>
                <TextField
                  isDisabled
                  type="text"
                  name="community"
                  value={fields.community}
                  label="Community*"
                  className="ClientForm-TextField"
                />
              </Col>
            )}

            {!isEditing && (
              <Col md={4}>
                <SelectField
                  type="text"
                  name="communityId"
                  value={fields.communityId}
                  options={communitiesOptions}
                  label="Community*"
                  className="ClientForm-SelectField"
                  errorText={errors.communityId || extraErrors.community}
                  isDisabled={!fields.isActive || isEditing || isFetchingCommunities || communitiesOptions.length === 1}
                  onChange={onChangeCommunity}
                />
              </Col>
            )}

            <Col md={4}>
              <TextField
                type="text"
                name="unit"
                value={fields.unit}
                label="Unit #"
                className="ClientForm-TextField"
                errorText={errors.unit}
                maxLength={12}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>
          </Row>
        </div>
        <div className="ClientForm-Section">
          <div className="ClientForm-SectionTitle ">Demographics</div>
          <Row>
            <Col md={4}>
              <TextField
                type="text"
                name="firstName"
                value={fields.firstName}
                label="First Name*"
                maxLength={256}
                className="ClientForm-TextField"
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
                className="ClientForm-TextField"
                errorText={errors.lastName}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <DateField
                name="birthDate"
                className="ClientForm-DateField"
                value={formatStringDate(fields.birthDate)}
                dateFormat="MM/dd/yyyy"
                label="Date Of Birth*"
                maxDate={Date.now()}
                errorText={errors.birthDate}
                isDisabled={!fields.isActive}
                onChange={onChangeBirthDate}
              />
            </Col>
          </Row>

          <Row>
            <Col md={4}>
              <SelectField
                name="genderId"
                value={fields.genderId}
                options={genderOptions}
                label="Gender*"
                className="ClientForm-SelectField"
                errorText={errors.genderId}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <SelectField
                name="ethnicityId"
                value={fields.ethnicityId}
                options={ethnicityOptions}
                label="Ethnicity"
                className="ClientForm-SelectField"
                // errorText={errors.ethnicityId}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <SelectField
                name="raceId"
                value={fields.raceId}
                options={raceOptions}
                label="Race"
                className="ClientForm-SelectField"
                errorText={errors.raceId}
                isDisabled={!fields.isActive}
                onChange={onChangeRaceField}
              />
            </Col>
          </Row>
          <Row>
            <Col md={4}>
              <SelectField
                name="maritalStatusId"
                value={fields.maritalStatusId}
                options={maritalStatusOptions}
                label="Marital Status"
                className="ClientForm-SelectField"
                errorText={errors.maritalStatusId}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>
            <Col lg={4} md={4}>
              <SelectField
                isMultiple
                hasNoneOption
                hasValueTooltip
                name="languageSpoken"
                value={languageSpoken}
                options={mappedMarketplaceLanguages}
                label="Language Spoken"
                placeholder="Select"
                className="OrganizationForm-SelectField"
                errorText={errors?.languageSpoken}
                onChange={changeField}
              />
            </Col>
            <Col lg={4} md={4}>
              <SelectField
                isMultiple
                hasNoneOption
                hasValueTooltip
                name="languageWritten"
                value={languageWritten}
                options={mappedMarketplaceLanguages}
                label="Language Written"
                placeholder="Select"
                className="OrganizationForm-SelectField"
                errorText={errors.languageWritten}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col md={4} className="position-relative">
              <OutsideClickListener onClick={hideSSN} containerSelector=".modal">
                <TextField
                  type="text"
                  name="ssn"
                  value={ssn}
                  maxLength={9}
                  placeholder="XXX XX XXXX"
                  className="ClientForm-TextField"
                  errorText={errors.ssn || ssnError.message}
                  isDisabled={!isEditableSSN || (!isEditing && fields.hasNoSSN)}
                  label={"Social Security Number" + (!fields.hasNoSSN ? "*" : "")}
                  onChange={changeField}
                />
                {canEditSSN && !isEditableSSN && !fields.hasNoSSN && (
                  <EditButton
                    size={24}
                    shouldHighLight={false}
                    className="ClientForm-EditSsnButton"
                    onClick={onEditSSN}
                  />
                )}
              </OutsideClickListener>
            </Col>

            <Col md={4}>
              <CheckboxField
                type="text"
                name="hasNoSSN"
                value={fields.hasNoSSN}
                label="Client doesn't have SSN"
                className="ClientForm-CheckboxField"
                isDisabled={!isHasNoSSNEnabled}
                onChange={changeField}
              />
            </Col>
            <Col md={4}>
              <SelectField
                name="careTeamManager"
                hasSearchBox
                isFetchingOptions={isFetchingCareTeam}
                value={fields.careTeamManager}
                options={careTeamManagerOptions}
                label="Care Team Manager"
                className="ClientForm-TextField"
                placeholder="Select"
                errorText={errors.careTeamManager}
                isDisabled={!fields.isActive || fields.hieConsentPolicyName === OPT_OUT}
                onChange={changeField}
                onChangeSearchText={onChangeSearchText}
              />
            </Col>
          </Row>

          <Row>
            <Col md={4}>
              <SelectField
                name="address.stateId"
                value={fields.address?.stateId}
                options={stateOptions}
                label="State*"
                className="ClientForm-TextField"
                placeholder="Select"
                errorText={errors.address?.stateId}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="text"
                name="address.city"
                value={fields.address?.city}
                label="City*"
                className="ClientForm-TextField"
                errorText={errors.address?.city}
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="number"
                name="address.zip"
                label="Zip Code*"
                value={fields.address?.zip}
                className="ClientForm-TextField"
                errorText={errors.address?.zip}
                isDisabled={!fields.isActive}
                maxLength={5}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={6}>
              <TextField
                type="text"
                name="address.street"
                value={fields.address?.street}
                label="Street*"
                className="ClientForm-TextField"
                errorText={errors.address?.street}
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col lg={8} md={6}>
              <FileField
                name="avatar"
                value={fields.avatar?.name || fields.avatarName}
                label="User Photo"
                className="ClientForm-TextField"
                errorText={errors.avatar?.size || errors.avatar?.type}
                isDisabled={!fields.isActive}
                onChange={onChangeAvatarField}
                renderLabelIcon={() => <Info id="avatar-hint" className="ClientForm-InfoIcon" />}
                tooltip={avatarHint}
              />
            </Col>
          </Row>
        </div>

        {(isCanShowHousingVoucher || fields.canViewHousingVouchers) && (
          <div className="ClientForm-Section">
            <div className="h-flexbox align-items-center justify-content-between">
              <div className="ClientForm-SectionTitle">Housing Vouchers</div>
              <Button
                color="success"
                onClick={onAddHousingVouchers}
                data-testid="add-housing-vouchers-btn"
                disabled={!areRequiredHousingVouchersFieldsFilled}
              >
                Add T-code
              </Button>
            </div>

            {fields.housingVouchers.map((o, i) => (
              <div className="ClientForm-HousingVouchers" key={i}>
                <Row>
                  <Col md={4}>
                    <TextField
                      type="number"
                      name={`housingVouchers.${i}.tCode`}
                      value={o.tCode}
                      label="T-code*"
                      maxLength={7}
                      className="ClientForm-TextField"
                      errorText={getProperty(errors, `housingVouchers.${i}.tCode`) || TCodeErrors[i]}
                      isDisabled={!fields.isActive}
                      onChange={changeField}
                      renderLabelIcon={() => <Info id="tCode-hint" className="ClientForm-InfoIcon" />}
                      tooltip={tCodeHint}
                      innerSpan={true}
                      innerSpanText="T"
                    />
                  </Col>

                  <Col md={4}>
                    <DateField
                      name={`housingVouchers.${i}.expiryDate`}
                      className="ClientForm-DateField"
                      value={formatStringDate(o.expiryDate)}
                      dateFormat="MM/dd/yyyy"
                      label="Expiry Date"
                      minDate={Date.now()}
                      errorText={getProperty(errors, `housingVouchers.${i}.expiryDate`)}
                      isDisabled={!fields.isActive}
                      onChange={onChangeBirthDate}
                    />
                  </Col>
                  <Col md={2}>
                    <label className="DateField-Label form-label">&nbsp;</label>
                    <div className="ClentForm-removeHousingVouchersDiv">
                      <Close
                        onClick={() => {
                          onRemoveHousingVouchers(i);
                        }}
                        className="ClentForm-removeHousingVouchersBtn"
                      />
                    </div>
                  </Col>
                </Row>
              </div>
            ))}
          </div>
        )}

        <div className="ClientForm-Section">
          <div className="h-flexbox align-items-center justify-content-between">
            <div className="ClientForm-SectionTitle">Power of Attorney (POA)</div>
            <Button
              color="success"
              onClick={onAddAttorney}
              data-testid="add-attorney-btn"
              disabled={fields.attorneys.size === 4 || !areRequiredPOAFieldsFilled}
            >
              Add POA
            </Button>
          </div>

          {fields.attorneys.map((o, i) => (
            <div className="ClientForm-PowerOfAttorney" key={i}>
              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    name={`attorneys.${i}.firstName`}
                    value={o.firstName}
                    label="First Name*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.firstName`)}
                    isDisabled={!fields.isActive || o.id}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`attorneys.${i}.lastName`}
                    value={o.lastName}
                    label="Last Name*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.lastName`)}
                    isDisabled={!fields.isActive || o.id}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <SelectField
                    type="text"
                    name={`attorneys.${i}.types`}
                    value={o.types?.toArray()}
                    options={mappedAttorneyTypes}
                    isMultiple
                    label="POA Type*"
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `attorneys.${i}.types`)}
                    isDisabled={!fields.isActive || o.id}
                    onChange={onChangeAttorneyTypesField}
                  />
                </Col>
              </Row>

              <Row>
                <Col md={4}>
                  <TextField
                    type="email"
                    name={`attorneys.${i}.email`}
                    value={o.email}
                    label="Email"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.email`)}
                    isDisabled={!fields.isActive || o.id}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <PhoneField
                    name={`attorneys.${i}.phone`}
                    value={o.phone}
                    label="Phone*"
                    className="ClientForm-PhoneField"
                    errorText={getProperty(errors, `attorneys.${i}.phone`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                    renderLabelIcon={() => <Info id="cell-phone-hint" className="ClientForm-InfoIcon" />}
                    tooltip={cellPhoneHint}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`attorneys.${i}.street`}
                    value={o.street}
                    label="Street*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.street`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    name={`attorneys.${i}.city`}
                    value={o.city}
                    label="City*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.city`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <SelectField
                    type="text"
                    name={`attorneys.${i}.state`}
                    value={o.state}
                    options={stateOptions}
                    label="State*"
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `attorneys.${i}.state`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="number"
                    name={`attorneys.${i}.zipCode`}
                    value={o.zipCode}
                    label="Zip Code*"
                    maxLength={5}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `attorneys.${i}.zipCode`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
          ))}
        </div>
        <div className="ClientForm-Section">
          <div className="h-flexbox align-items-center justify-content-between">
            <div className="ClientForm-SectionTitle">Contact</div>
            <Button
              color="success"
              onClick={onAddContact}
              data-testid="add-contact-btn"
              disabled={fields.contact.size === 4 || !areRequiredContactFieldsFilled}
            >
              Add Contact
            </Button>
          </div>

          {fields.contact.map((o, i) => (
            <div className="ClientForm-Contact" key={i}>
              <Row>
                <Col md={4}>
                  <SelectField
                    type="text"
                    name={`contact.${i}.type`}
                    value={o.type}
                    options={contactTypeOptions}
                    label="Type*"
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `contact.${i}.type`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
                <Col md={4}>
                  <TextField
                    type="text"
                    name={`contact.${i}.firstName`}
                    value={o.firstName}
                    label="First Name*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.firstName`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`contact.${i}.lastName`}
                    value={o.lastName}
                    label="Last Name*"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.lastName`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col md={4}>
                  <PhoneField
                    name={`contact.${i}.phone`}
                    value={o.phone}
                    label="Phone*"
                    className="ClientForm-PhoneField"
                    errorText={getProperty(errors, `contact.${i}.phone`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                    renderLabelIcon={() => <Info id="cell-phone-hint" className="ClientForm-InfoIcon" />}
                    tooltip={cellPhoneHint}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="email"
                    name={`contact.${i}.email`}
                    value={o.email}
                    label="Email"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.email`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`contact.${i}.street`}
                    value={o.street}
                    label="Street"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.street`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    name={`contact.${i}.city`}
                    value={o.city}
                    label="City"
                    maxLength={256}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.city`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <SelectField
                    type="text"
                    name={`contact.${i}.state`}
                    value={Number(o.state)}
                    options={stateOptions}
                    label="State"
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `contact.${i}.state`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="number"
                    name={`contact.${i}.zipCode`}
                    value={o.zipCode}
                    label="Zip Code"
                    maxLength={5}
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `contact.${i}.zipCode`)}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
          ))}
        </div>

        <div className="ClientForm-Section">
          <div className="ClientForm-SectionTitle">Telecom</div>

          <Row>
            <Col lg={4} md={6}>
              <PhoneField
                name="cellPhone"
                value={fields.cellPhone}
                label="Cell Phone*"
                className="ClientForm-PhoneField"
                errorText={errors.cellPhone}
                isDisabled={!fields.isActive}
                onChange={changeField}
                renderLabelIcon={() => <Info id="cell-phone-hint" className="ClientForm-InfoIcon" />}
                tooltip={cellPhoneHint}
              />
            </Col>

            <Col lg={4} md={6}>
              <PhoneField
                name="phone"
                value={fields.phone}
                label="Home Phone"
                className="ClientForm-PhoneField"
                errorText={errors.phone}
                isDisabled={!fields.isActive}
                onChange={changeField}
                renderLabelIcon={() => <Info id="home-phone-hint" className="ClientForm-InfoIcon" />}
                tooltip={homePhoneHint}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={6}>
              <TextField
                type="text"
                name="email"
                value={fields.email}
                label={"Email" + (isClientEmailRequired ? "*" : fields.hasNoEmail ? "" : "*")}
                className="ClientForm-TextField"
                errorText={errors.email || emailError.message}
                isDisabled={!fields.isActive || (!isClientEmailRequired && fields.hasNoEmail)}
                onChange={onChangeEmailField}
              />
            </Col>

            <Col lg={4} md={6}>
              <CheckboxField
                type="text"
                name="hasNoEmail"
                value={!isClientEmailRequired && fields.hasNoEmail}
                label="Client doesn't have email"
                className="ClientForm-CheckboxField"
                isDisabled={isClientEmailRequired || !fields.isActive || fields.email || isPrimaryContactSelfAndEmail}
                onChange={changeField}
              />
            </Col>
          </Row>
        </div>

        <div className="ClientForm-Section">
          <div className="h-flexbox align-items-center justify-content-between">
            <div className="ClientForm-SectionTitle">Insurance</div>
            <Button color="success" onClick={onAddInsurance} disabled={fields.insurances.size === 3}>
              Add Insurance
            </Button>
          </div>

          {fields.insurances.map((insurance, i) => (
            <div className="ClientForm-Insurance" key={i}>
              <Row>
                <Col md={12}>
                  <SelectField
                    hasSearchBox
                    name={`insurances.${i}.networkId`}
                    value={insurance.networkId}
                    options={insuranceNetworkOptions}
                    label="Network"
                    placeholder="Search by network name"
                    className="ClientForm-SelectField"
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col md={4}>
                  <TextField
                    type="text"
                    label="Plan"
                    name={`insurances.${i}.paymentPlan`}
                    value={insurance.paymentPlan}
                    className="ClientForm-TextField"
                    maxLength={256}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`insurances.${i}.groupNumber`}
                    value={insurance.groupNumber}
                    label="Group Number"
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `insurances.${i}.groupNumber`)}
                    maxLength={256}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>

                <Col md={4}>
                  <TextField
                    type="text"
                    name={`insurances.${i}.memberNumber`}
                    value={insurance.memberNumber}
                    label="Member Number"
                    className="ClientForm-TextField"
                    errorText={getProperty(errors, `insurances.${i}.memberNumber`)}
                    maxLength={256}
                    isDisabled={!fields.isActive}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
          ))}

          <Row>
            {/* 1011 前版本*/}
            {/*<Col md={4}>
                            <TextField
                                type="text"
                                name="medicareNumber"
                                value={fields.medicareNumber}
                                label={"Medicare Number" +
                                    (fields.manuallyCreated
                                        && fields.hasNoSSN
                                        && !fields.medicaidNumber
                                        ? "*"
                                        : ""
                                    )}
                                className="ClientForm-TextField"
                                errorText={medicareNumberError.message || errors.medicareNumber}
                                maxLength={25}
                                isDisabled={!fields.isActive}
                                onChange={changeField}
                            />
                        </Col>

                        <Col lg={4} md={6}>
                            <TextField
                                type="text"
                                name="medicaidNumber"
                                value={fields.medicaidNumber}
                                label={"Medicaid Number" +
                                    (fields.manuallyCreated
                                        && fields.hasNoSSN
                                        && !fields.medicareNumber
                                        ? "*"
                                        : ""
                                    )}
                                className="ClientForm-TextField"
                                errorText={medicaidNumberError.message || errors.medicaidNumber}
                                maxLength={25}
                                isDisabled={!fields.isActive}
                                onChange={changeField}
                            />
                        </Col>*/}
            <Col md={4}>
              <TextField
                type="text"
                name="medicareNumber"
                value={fields.medicareNumber}
                label={fields.hasNoMedicareNumber ? "Medicare Number" : "Medicare Number*"}
                className="ClientForm-TextField"
                errorText={medicareNumberError.message || errors.medicareNumber}
                maxLength={25}
                isDisabled={!fields.isActive || fields.hasNoMedicareNumber}
                onChange={changeField}
              />
            </Col>
            <Col md={6}>
              <CheckboxField
                type="text"
                name="hasNoMedicareNumber"
                value={fields.hasNoMedicareNumber}
                label="Client doesn't have medicareNumber"
                className="ClientForm-CheckboxField"
                // isDisabled={!isHasNoMedicareNumberEnabled}
                onChange={changeField}
              />
            </Col>

            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="medicaidNumber"
                value={fields.medicaidNumber}
                label={fields.hasNoMedicaidNumber ? "Medicaid Number" : "Medicaid Number*"}
                className="ClientForm-TextField"
                errorText={medicaidNumberError.message || errors.medicaidNumber}
                maxLength={25}
                isDisabled={!fields.isActive || fields.hasNoMedicaidNumber}
                onChange={changeField}
              />
            </Col>

            <Col lg={5} md={5}>
              <CheckboxField
                type="text"
                name="hasNoMedicaidNumber"
                value={fields.hasNoMedicaidNumber}
                label="Client doesn't have medicaidNumber"
                className="ClientForm-CheckboxField"
                // isDisabled={!isHasNoMedicaidNumberEnabled}
                onChange={changeField}
              />
            </Col>

            <Col lg={3} md={3}>
              <CheckboxField
                type="text"
                name="hasInsuranceAuthorizations"
                value={hasInsuranceAuthorizations}
                label="Add Authorization"
                className="ClientForm-CheckboxField"
                isDisabled={isEditing && !fields.insuranceAuthorizations.isEmpty()}
                onChange={(name, value) => setHasInsuranceAuthorizations(value)}
              />
            </Col>
          </Row>
        </div>

        {hasInsuranceAuthorizations && (
          <div className="ClientForm-Section">
            <div className="h-flexbox align-items-center justify-content-between">
              <div className="ClientForm-SectionTitle">Authorization</div>
              <Button color="success" data-testid="add-insurance-auth-btn" onClick={onAddInsuranceAuthorization}>
                Add Authorization
              </Button>
            </div>

            {fields.insuranceAuthorizations.map((o, i) => (
              <Row>
                <Col md={4}>
                  <DateField
                    name={`insuranceAuthorizations.${i}.startDate`}
                    value={o.startDate}
                    label={addAsterix("Start Date").if(i === 0 || isNotBlank(omit(o, "index")))}
                    hasTimeSelect
                    isDisabled={Boolean(o.id)}
                    dateFormat={`MM/dd/yyyy hh:mm a '${getTimeZoneAbbr()}'XXX`}
                    timeFormat="hh:mm aa"
                    maxDate={o.endDate ?? undefined}
                    maxTime={o.endDate && (!o.startDate || isSameDay(o.startDate, o.endDate)) ? o.endDate : undefined}
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `insuranceAuthorizations.${i}.startDate`)}
                    onChange={onChangeInsuranceAuthorizationStartDateField}
                  />
                </Col>
                <Col md={4}>
                  <DateField
                    name={`insuranceAuthorizations.${i}.endDate`}
                    value={o.endDate}
                    label={addAsterix("End Date").if(i === 0 || isNotBlank(omit(o, "index")))}
                    hasTimeSelect
                    isDisabled={Boolean(o.id)}
                    dateFormat={`MM/dd/yyyy hh:mm a '${getTimeZoneAbbr()}'XXX`}
                    timeFormat="hh:mm aa"
                    minDate={o.startDate ?? undefined}
                    minTime={o.startDate && (!o.endDate || isSameDay(o.startDate, o.endDate)) ? o.startDate : undefined}
                    className="ClientForm-SelectField"
                    errorText={getProperty(errors, `insuranceAuthorizations.${i}.endDate`)}
                    onChange={onChangeInsuranceAuthorizationEndDateField}
                  />
                </Col>
                <Col>
                  <TextField
                    type="text"
                    name={`insuranceAuthorizations.${i}.number`}
                    value={o.number}
                    isDisabled={Boolean(o.id)}
                    label={addAsterix("Authorization Number").if(i === 0 || isNotBlank(omit(o, "index")))}
                    className="ClientForm-TextField"
                    maxLength={128}
                    errorText={getProperty(errors, `insuranceAuthorizations.${i}.number`)}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            ))}
          </div>
        )}

        <div className="ClientForm-Section">
          <div className="ClientForm-SectionTitle">Ancillary Information</div>

          <Row>
            <Col lg={4} md={6}>
              <RadioGroupField
                view="row"
                name="retained"
                selected={fields.retained}
                title="Retained"
                isDisabled={!fields.isActive}
                options={YES_NO_OPTIONS}
                onChange={changeField}
              />
            </Col>
            <Col md={6}>
              <CheckboxField
                name="hasAdvancedDirectiveOnFile"
                value={fields.hasAdvancedDirectiveOnFile}
                label="Client has an advanced directive on file"
                className="ClientForm-CheckboxField"
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col md={4}>
              <TextField
                type="text"
                name="primaryCarePhysicianFirstName"
                value={fields.primaryCarePhysicianFirstName}
                label="PCP First Name"
                className="ClientForm-TextField"
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="text"
                name="primaryCarePhysicianLastName"
                value={fields.primaryCarePhysicianLastName}
                label="PCP Last Name"
                className="ClientForm-TextField"
                maxLength={15}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <PhoneField
                name="primaryCarePhysicianPhone"
                value={fields.primaryCarePhysicianPhone}
                label="PCP Phone"
                className="ClientForm-TextField"
                isDisabled={!fields.isActive}
                onChange={changeField}
                renderLabelIcon={() => <Info id="cell-phone-hint" className="ClientForm-InfoIcon" />}
                tooltip={cellPhoneHint}
                errorText={errors.primaryCarePhysicianPhone}
              />
            </Col>
          </Row>

          <Row>
            <Col md={4}>
              <DateField
                hasTimeSelect
                name="intakeDate"
                className="ClientForm-DateField"
                value={fields.intakeDate}
                label="Intake Date"
                dateFormat="MM/dd/yyyy hh:mm a 'GMT'XXX"
                isDisabled={!fields.isActive}
                onChange={changeDateField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="text"
                name="currentPharmacyName"
                value={fields.currentPharmacyName}
                label="Current Pharmacy Name"
                className="ClientForm-TextField"
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="text"
                name="referralSource"
                value={fields.referralSource}
                label="Referral Source"
                className="ClientForm-TextField"
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>

            <Col md={4}>
              <TextField
                type="text"
                name="riskScore"
                value={fields.riskScore}
                label="Risk score"
                className="ClientForm-TextField"
                maxLength={256}
                isDisabled={!fields.isActive}
                onChange={changeField}
              />
            </Col>
          </Row>
        </div>
        <div className="ClientForm-Section">
          <div className="ClientForm-SectionTitle d-flex align-items-center">
            Primary contact
            <PrimaryContactSectionHint className="ClientForm-InfoIcon ml-2" isTooltipEnabled />
          </div>
          {hasInactivePrimaryContact &&
            !selectedPrimaryContact?.isMarkedForDeletion &&
            selectedPrimaryContact?.careTeamMemberId === fields.primaryContact?.careTeamMemberId && (
              <AlertPanel>The selected care team member is currently inactive</AlertPanel>
            )}
          {selectedPrimaryContact?.isMarkedForDeletion && <AlertPanel>The user has deleted his/her account</AlertPanel>}

          <Row>
            <Col lg={4} md={6}>
              <RadioGroupField
                view="row"
                name="primaryContact.typeName"
                selected={fields.primaryContact.typeName}
                title="Primary contact*"
                options={primaryContactTypeOptions}
                onChange={onChangeContactType}
                errorText={errors.primaryContact?.typeName}
                className="ClientForm-PrimaryContactType"
              />
            </Col>
            {fields.primaryContact.typeName === "CARE_TEAM_MEMBER" && (
              <Col lg={4} md={6}>
                <SelectField
                  name="primaryContact.careTeamMemberId"
                  value={fields.primaryContact?.careTeamMemberId}
                  options={primaryContactOptions}
                  label="Care team member*"
                  placeholder="Select"
                  className="ClientForm-SelectField"
                  errorText={errors.primaryContact?.careTeamMemberId}
                  onChange={onChangeCareTeamMember}
                />
              </Col>
            )}
            <Col lg={4} md={6}>
              <RadioGroupField
                view="row"
                name="primaryContact.notificationMethodName"
                selected={fields.primaryContact?.notificationMethodName}
                title="Primary notification method*"
                options={primaryContactNotificationOptions}
                onChange={onChangeNotificationMethod}
                errorText={errors.primaryContact?.notificationMethodName}
                className="ClientForm-NotificationMethod"
              />
            </Col>
          </Row>
        </div>
        <div className="ClientForm-Section">
          <div className="ClientForm-SectionTitle d-flex align-items-center">HIE Opt In / Opt Out</div>

          <AlertPanel>
            <p className="mb-0">
              A Health Information Exchange (HIE) is a way for health care providers participating in the HIE to share
              health information with each other through a secure, electronic means so that health care providers have
              the benefit of the most current available information. Simply Connect participates in HIEs in order to aid
              in the facilitation and coordination of your healthcare.
            </p>
            <p className="mb-0">
              PRIVACY AND SECURITY. Federal and state laws govern how your health information can be exchanged, viewed,
              or used through an HIE. Simply Connect is committed to keeping your electronic health record private and
              secure, and only provides, views or uses your health information consistent with those laws.
            </p>
            <p className="mb-0">
              PARTICIPATION IN HIEs. Through its participation in HIEs, Simply Connect makes patient information
              available electronically to other HIE participants (e.g., participating hospitals, doctors, health plans
              and government agencies). We may also receive information about patients from other HIE participants. We
              expect that using HIEs will provide faster and more complete access to your health information to make
              more informed decisions about your care.
            </p>
          </AlertPanel>

          <Row>
            <Col>
              <RadioGroupField
                name="hieConsentPolicyName"
                selected={fields.hieConsentPolicyName}
                options={HIE_CONSENT_POLICY_OPTIONS}
                onChange={onChangeHieConsentPolicy}
                isDisabled={!fields.communityId}
                errorText={errors.hieConsentPolicyName}
              />
            </Col>
          </Row>
          <Row>
            <Col lg={4} md={6}>
              <TextField
                name="hieConsentPolicyObtainedFrom"
                label="Obtained from*"
                value={fields.hieConsentPolicyObtainedFrom}
                onChange={changeField}
                errorText={errors.hieConsentPolicyObtainedFrom}
                isDisabled={isHieConsentPolicyDisabled}
                maxLength={520}
              />
            </Col>
            <Col lg={4} md={6}>
              <SelectField
                name="hieConsentPolicyObtainedBy"
                label="Obtained by"
                value={fields.hieConsentPolicyObtainedBy}
                options={HIE_CONSENT_POLICY_OBTAINED_BY_OPTIONS}
                onChange={changeField}
                isDisabled={isHieConsentPolicyDisabled}
              />
            </Col>
            <Col lg={4} md={6}>
              <DateField
                name="hieConsentPolicyObtainedDate"
                label="Date Obtained"
                value={fields.hieConsentPolicyObtainedDate}
                onChange={changeDateField}
                isDisabled={isHieConsentPolicyDisabled}
                isFutureDisabled
              />
            </Col>
          </Row>
        </div>
      </Scrollable>

      <div className="ClientForm-Buttons">
        <Button outline color="success" disabled={isFetching} onClick={() => onCancel(isChanged)}>
          Cancel
        </Button>

        <Button color="success" disabled={!isValidForm || isFetching || !fields.isActive}>
          {isEditing ? "Save" : "Create"}
        </Button>
      </div>

      {isPrimaryContactDeletionConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="Confirm"
          title={`As a result of your Opt Out selection, ${fields?.primaryContact?.firstName} ${fields?.primaryContact?.lastName} will no 
					longer be a primary contact on Client care team and no longer have associated capabilities${careTeamInvitationsExist && ".Care team invitations will be canceled"}`}
          onConfirm={onConfirmSubmit}
          onCancel={() => togglePrimaryContactDeletionConfirmDialog(false)}
        />
      )}
      {isDeleteTCodeConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="Confirm"
          title={confirmDeleteTCodeTitle}
          onConfirm={onConfirmTCodeDelete}
          onCancel={onCloseConfirmTCodeDeleteDialog}
        />
      )}
    </Form>
  );
}

export default compose(memo, connect(null, mapDispatchToProps), withAutoSave())(ClientForm);
