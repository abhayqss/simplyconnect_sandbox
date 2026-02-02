import React, { memo, useCallback, useEffect, useMemo, useState } from "react";

import { chain, isNumber, map } from "underscore";

import { connect } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { useDebouncedCallback } from "use-debounce";

import * as errorActions from "redux/error/errorActions";
import * as organizationFormActions from "redux/organization/form/organizationFormActions";

import { useForm, useQueryInvalidation, useResponse, useScrollable, useScrollToFormError } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import {
  useMarketplaceLanguagesQuery,
  useServiceCategoriesQuery,
  useServicesQuery,
  useStatesQuery,
} from "hooks/business/directory/query";

import { useESignRequestCount } from "hooks/business/documents/e-sign";

import { useAppointmentCountQuery } from "hooks/business/appointments";

import {
  useOrganizationPermissionsQuery,
  useOrganizationQuery,
  useUniqCompanyIdWithinOrganization,
  useUniqNameWithinOrganization,
  useUniqOidWithingOrganization,
} from "hooks/business/admin/organization";

import { Button, Col, Form, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import { Loader, Tabs } from "components";

import { CheckboxField, FeatureSwitchField, FileField, PhoneField, SelectField, TextField } from "components/Form";

import ConfirmDialog from "components/dialogs/ConfirmDialog/ConfirmDialog";

import AffiliateRelationshipSection from "./AffiliateRelationshipSection/AffiliateRelationshipSection";

import Organization from "entities/Organization";
import AffiliatedRelationship from "entities/AffiliatedRelationship";
import OrganizationFormValidator from "validators/OrganizationFormSchemeValidator";

import { ReactComponent as Info } from "images/info.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import { isEmpty, omitEmptyPropsDeep } from "lib/utils/Utils";

import { E_SIGN_STATUSES, SYSTEM_ROLES } from "lib/Constants";

import { STEP, TAB_TITLE } from "./Constants";

import { AddModeButtonBuilder, EditModeButtonBuilder } from "./builders";

import { ComplexValidationStrategy, SimpleValidationStrategy, ValidationContext } from "./strategies";
import FileFieldWithCrop from "components/Form/FileField/FileFieldWithCrop";

import "./OrganizationForm.scss";

function valueTextMapper({ id, name, value, label, title }) {
  return { value: id || value || name, text: label || title || name };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(organizationFormActions, dispatch),
      error: bindActionCreators(errorActions, dispatch),
    },
  };
}

const getData = (fields, isEditMode) => {
  let data = fields.toJS();

  let affiliatedRelationships = data.affiliatedRelationships.map((o) => {
    if (o.areAllPrimaryCommunitiesSelected) {
      o.primaryCommunities = [];
    }

    if (o.areAllAffiliatedCommunitiesSelected) {
      o.affiliatedCommunities = [];
    }

    return o;
  });

  return {
    ...data,
    affiliatedRelationships,
    shouldRemoveLogo: isEditMode && !(data.logo || data.logoName),
  };
};

const setAffiliatedRelationships = (affiliatedRelationships) =>
  affiliatedRelationships &&
  affiliatedRelationships.map((o) => {
    if (isEmpty(o.primaryCommunities)) {
      o.areAllPrimaryCommunitiesSelected = true;
    }

    if (isEmpty(o.affiliatedCommunities)) {
      o.areAllAffiliatedCommunitiesSelected = true;
    }

    return AffiliatedRelationship(o);
  });

const { REQUESTED } = E_SIGN_STATUSES;

function OrganizationForm({ actions, onClose, setIsFormChanged, testConfig, organizationId, onSaveSuccess }) {
  const [isFetching, setIsFetching] = useState(false);
  const [needValidation, setNeedValidation] = useState(false);

  const [tab, setTab] = useState(testConfig?.tab ?? STEP.LEGAL_INFO);

  const [selectedARIndex, setSelectedARIndex] = useState(null);

  const [isRemoveARConfirmDialogOpen, setIsRemoveARConfirmDialogOpen] = useState(false);
  const [isTurnOffESignFeatureWarningDialogOpen, toggleTurnOffESignFeatureWarningDialog] = useState(false);
  const [isTurnOffAppointmentsFeatureWarningDialogOpen, toggleTurnOffAppointmentsFeatureWarningDialog] =
    useState(false);

  const user = useAuthUser();

  const isSuperAdmin = user?.roleName === SYSTEM_ROLES.SUPER_ADMINISTRATOR;

  const isEditing = isNumber(organizationId);

  const invalidate = useQueryInvalidation();

  const { data: defaultPermissions } = useOrganizationPermissionsQuery();

  const { fields, errors, isValid, validate, isChanged, changeField, changeFields } = useForm(
    "OrganizationForm",
    Organization,
    OrganizationFormValidator,
  );

  const { data: organization = {}, isFetching: isFetchingData } = useOrganizationQuery(
    {
      organizationId,
      isMarketplaceDataIncluded: true,
    },
    {
      staleTime: 0,
      enabled: isEditing && Boolean(organizationId),
    },
  );

  useEffect(() => {
    isChanged && setIsFormChanged(isChanged);
  }, [isChanged]);

  const permissions = useMemo(() => {
    if (isEditing) {
      return {
        canEdit: organization?.canEdit,
        canEditFeatures: organization?.features?.canEdit,
        canEditAffiliateRelationships: organization?.canEditAffiliateRelationships,
        canEditConfirmMarketplaceVisibility: organization?.canEditConfirmMarketplaceVisibility,
        canEditAllowExternalInboundReferrals: organization?.canEditAllowExternalInboundReferrals,
      };
    }

    return defaultPermissions ?? {};
  }, [isEditing, organization, defaultPermissions]);

  const {
    data: signatureRequestCount,
    refetch: refetchSignatureRequestCount,
    isFetching: isFetchingESignRequestCount,
  } = useESignRequestCount(
    {
      organizationId,
      statuses: [REQUESTED],
    },
    {
      enabled: false,
    },
  );

  const {
    data: appointmentCount,
    refetch: refetchAppointmentCount,
    isFetching: isFetchingAppointmentCount,
  } = useAppointmentCountQuery({ organizationId }, { enabled: false });

  const onChangeLogoField = useCallback(
    (name, value) => {
      if (value) {
        changeField(name, value);
      } else {
        changeFields({ logo: null, logoName: "" });
      }
    },
    [changeField, changeFields],
  );

  const serviceIds = useMemo(() => fields.marketplace.serviceIds.toJS(), [fields.marketplace.serviceIds]);
  const languageIds = useMemo(() => fields.marketplace.languageIds.toJS(), [fields.marketplace.languageIds]);
  const serviceCategoryIds = useMemo(
    () => fields.marketplace.serviceCategoryIds.toJS(),
    [fields.marketplace.serviceCategoryIds],
  );

  const turnOffESignFeatureWarningDialogTitle = useMemo(() => {
    return `The are ${signatureRequestCount} signatures requests. All requests will be cancelled automatically. Please confirm that you want turn off E-sign feature.`;
  }, [signatureRequestCount]);
  const turnOffAppointmentsFeatureWarningDialogTitle = useMemo(() => {
    return `The are ${appointmentCount} planned and triaged appointments. Please confirm that you want turn off Appointments feature.`;
  }, [appointmentCount]);

  const { data: states = [] } = useStatesQuery();

  const mappedStates = useMemo(() => map(states, valueTextMapper), [states]);

  const { data: serviceCategories = [] } = useServiceCategoriesQuery({}, { staleTime: 0 });

  const mappedServiceCategories = useMemo(() => map(serviceCategories, valueTextMapper), [serviceCategories]);

  const { data: services = [] } = useServicesQuery({ serviceCategoryIds }, { staleTime: 0 });

  const mappedServices = useMemo(
    () =>
      chain(serviceCategories)
        .filter((o) => serviceCategoryIds.includes(o.id))
        .map((o) => ({
          id: o.id,
          title: o.label ?? o.title,
          options: chain(services).where({ serviceCategoryId: o.id }).map(valueTextMapper).value(),
        }))
        .value(),
    [services, serviceCategories, serviceCategoryIds],
  );

  const { data: marketplaceLanguages = [] } = useMarketplaceLanguagesQuery({}, { staleTime: 0 });

  const mappedMarketplaceLanguages = useMemo(() => map(marketplaceLanguages, valueTextMapper), [marketplaceLanguages]);

  const validationOptions = useMemo(
    () => ({
      step: tab,
      included: {},
    }),
    [tab],
  );

  const [validateNameWithinOrganization, nameError] = useUniqNameWithinOrganization(fields.name, organizationId);
  const [validateOidWithinOrganization, oidError] = useUniqOidWithingOrganization(fields.oid);
  const [validateCompanyIdWithinOrganization, companyIdError] = useUniqCompanyIdWithinOrganization(fields.companyId);

  const { Scrollable, scroll } = useScrollable();

  const onResponse = useResponse({
    onFailure: actions.error.change,
    onSuccess: useCallback(
      ({ data }) => {
        if (isEditing) {
          invalidate("Organization", {
            organizationId,
            isMarketplaceDataIncluded: true,
          });
        }

        onSaveSuccess(data, !isEditing);
      },
      [invalidate, isEditing, onSaveSuccess, organizationId],
    ),
    onUnknown: actions.error.change,
  });

  const onScroll = useScrollToFormError(".OrganizationForm", scroll);

  function cancel() {
    onClose(isChanged);
  }

  const asyncFieldValidations = useMemo(
    () => ({
      name: validateNameWithinOrganization,
      oid: validateOidWithinOrganization,
      companyId: validateCompanyIdWithinOrganization,
    }),
    [validateNameWithinOrganization, validateOidWithinOrganization, validateCompanyIdWithinOrganization],
  );

  const validateAsync = () => {
    let promises = map(asyncFieldValidations, (validate, field) => {
      let isSameField = fields[field] === organization?.[field];

      return isEmpty(fields[field]) || isSameField ? Promise.resolve() : validate();
    });

    return Promise.all(promises);
  };

  const validationContext = new ValidationContext({
    step: tab,
    validate,
    validateAsync,
  });

  async function doValidate(strategy) {
    let { step, isValid } = await validationContext.executeValidation(strategy);

    if (!isValid) {
      onScroll();
      setTab(step);
      setNeedValidation(true);
    } else {
      setNeedValidation(false);
    }
    setIsFetching(false);
    return isValid;
  }

  console.log(mappedServices, "mappedServices");
  async function onNext() {
    setIsFetching(true);
    let isValid = await doValidate(new SimpleValidationStrategy());

    isValid && setTab((tab) => ++tab);
  }

  async function onSubmit(e) {
    e.preventDefault();

    let isValid = await doValidate(new SimpleValidationStrategy());

    if (isValid) {
      setIsFetching(true);

      onResponse(await actions.submit(getData(fields, isEditing)));

      setIsFetching(false);
    }
  }

  function validateIfNeed() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function validateName() {
    if (fields.name) {
      validateDebouncedNameIfAllowed();
    }
  }

  function validateOid() {
    if (fields.oid) {
      validateDebouncedOidIfAllowed();
    }
  }

  function validateCompanyId() {
    if (fields.companyId) {
      validateDebouncedCompanyIdIfAllowed();
    }
  }

  function init() {
    if (isNumber(organization?.id)) {
      let data = omitEmptyPropsDeep(organization);
      data.features = organization.features;
      data.affiliatedRelationships = setAffiliatedRelationships(data.affiliatedRelationships);
      changeFields(data, true);
    }
  }

  const onBack = () => setTab((tab) => --tab);
  const onCancel = useCallback(cancel, [onClose, isChanged]);

  const onChangeCategoryField = useCallback(
    (name, value) => {
      changeField(name, value);
      changeField("marketplace.serviceIds", []);
    },
    [changeField],
  );

  const onChangeFeatureField = useCallback(
    (name, value) => {
      changeField(name, value);

      if (name === "features.isChatEnabled" && !value) {
        changeField("features.isVideoEnabled", false);
      }

      if (name === "features.isVideoEnabled" && value) {
        changeField("features.isChatEnabled", true);
      }
    },
    [changeField],
  );

  const onChangeESignFeatureField = useCallback(
    async (name, value) => {
      if (value) changeField(name, value);
      else {
        const count = await refetchSignatureRequestCount();

        if (count) toggleTurnOffESignFeatureWarningDialog(true);
        else changeField(name, false);
      }
    },
    [changeField, refetchSignatureRequestCount],
  );

  const onConfirmTurnOffESignFeature = useCallback(() => {
    changeField("features.isSignatureEnabled", false);
    toggleTurnOffESignFeatureWarningDialog(false);
  }, [changeField]);

  const onChangeAppointmentsFeatureField = useCallback(
    async (name, value) => {
      if (value) changeField(name, value);
      else {
        const count = await refetchAppointmentCount();

        if (count) toggleTurnOffAppointmentsFeatureWarningDialog(true);
        else changeField(name, false);
      }
    },
    [changeField, refetchAppointmentCount],
  );

  const onConfirmTurnOffAppointmentsFeature = useCallback(() => {
    changeField("features.areAppointmentsEnabled", false);
    toggleTurnOffAppointmentsFeatureWarningDialog(false);
  }, [changeField]);

  const onChangeTab = async (value) => {
    if (value > tab) {
      let stepOffset = value - tab;
      let Strategy = SimpleValidationStrategy;

      if (stepOffset > 1) {
        Strategy = ComplexValidationStrategy;
        validationContext.setStep(value - 1);
      }

      let isValid = await doValidate(new Strategy());

      isValid && setTab(value);
    } else {
      setTab(value);
    }
  };
  const onAddRelationship = () => {
    changeField("affiliatedRelationships", fields.affiliatedRelationships.push(AffiliatedRelationship()));
  };

  const onRemoveRelationship = (index, isChanged) => {
    if (isChanged) {
      setSelectedARIndex(index);
      setIsRemoveARConfirmDialogOpen(true);
    } else {
      removeRelationship(index);
    }
  };

  const removeRelationship = (index) => {
    changeField("affiliatedRelationships", fields.affiliatedRelationships.remove(index));
  };

  const onConfirmRemovingAR = () => {
    setSelectedARIndex(null);
    removeRelationship(selectedARIndex);
    setIsRemoveARConfirmDialogOpen(false);
  };

  const validateFieldIfAllowed = useCallback(
    (name, cb) => {
      let isSame = fields[name] === organization?.[name];
      return !isSame ? cb().catch(console.log) : Promise.resolve();
    },
    [organization, fields],
  );

  const validateNameIfAllowed = useCallback(
    () => validateFieldIfAllowed("name", asyncFieldValidations["name"]),
    [validateFieldIfAllowed, asyncFieldValidations],
  );

  const validateOidIfAllowed = useCallback(
    () => validateFieldIfAllowed("oid", asyncFieldValidations["oid"]),
    [validateFieldIfAllowed, asyncFieldValidations],
  );

  const validateCompanyIdIfAllowed = useCallback(
    () => validateFieldIfAllowed("companyId", asyncFieldValidations["companyId"]),
    [validateFieldIfAllowed, asyncFieldValidations],
  );

  const validateDebouncedNameIfAllowed = useDebouncedCallback(validateNameIfAllowed, 300);
  const validateDebouncedOidIfAllowed = useDebouncedCallback(validateOidIfAllowed, 300);
  const validateDebouncedCompanyIdIfAllowed = useDebouncedCallback(validateCompanyIdIfAllowed, 300);

  useEffect(validateIfNeed, [needValidation, onScroll, validate, validationOptions]);
  useEffect(validateName, [fields.name, validateDebouncedNameIfAllowed]);
  useEffect(validateOid, [fields.oid, validateDebouncedOidIfAllowed]);
  useEffect(validateCompanyId, [fields.companyId, validateDebouncedCompanyIdIfAllowed]);

  useEffect(init, [organization, changeFields]);

  const tabs = useMemo(
    () =>
      map(STEP, (value) => ({
        title: TAB_TITLE[value],
        isActive: tab === value,
        hasError: tab === value && !isValid,
        isDisabled:
          (value === STEP.FEATURES && !permissions.canEditFeatures) ||
          (value === STEP.AFFILIATE_RELATIONSHIP &&
            (!isEditing || !fields.hasCommunities || !permissions.canEditAffiliateRelationships)),
      })),
    [tab, isValid, isEditing, permissions, fields.hasCommunities],
  );

  const CloseButton = (
    <Button outline color="success" disabled={isFetching} onClick={onCancel}>
      Close
    </Button>
  );

  const BackButton = (
    <Button outline color="success" disabled={isFetching} onClick={onBack}>
      Back
    </Button>
  );

  const NextButton = (
    <Button color="success" disabled={isFetching} onClick={onNext}>
      Next
    </Button>
  );

  const SaveButton = (
    <Button color="success" disabled={isFetching}>
      {isEditing ? "Save" : "Create"}
    </Button>
  );

  const context = {
    tab,
    permissions,
    buttons: {
      CloseButton,
      BackButton,
      NextButton,
      SaveButton,
    },
  };

  const buttonsBuilder =
    isEditing && fields.hasCommunities ? new EditModeButtonBuilder(context) : new AddModeButtonBuilder(context);

  const FormActionButtons = buttonsBuilder.build();

  return (
    <Form className="OrganizationForm" onSubmit={onSubmit}>
      {(isFetching || isFetchingData || isFetchingESignRequestCount || isFetchingAppointmentCount) && (
        <Loader style={{ position: "fixed" }} hasBackdrop />
      )}

      <Tabs items={tabs} onChange={onChangeTab} containerClassName="OrganizationForm-TabsContainer" />

      {!isEditing && (
        <Tooltip
          target=".OrganizationForm .Tabs-TabDisabled"
          modifiers={[
            {
              name: "offset",
              options: { offset: [0, 6] },
            },
            {
              name: "preventOverflow",
              options: { boundary: document.body },
            },
          ]}
        >
          The feature will be available when you create the organization
        </Tooltip>
      )}

      {isEditing && !fields.hasCommunities && (
        <Tooltip
          target=".OrganizationForm .Tabs-TabDisabled"
          modifiers={[
            {
              name: "offset",
              options: { offset: [0, 6] },
            },
            {
              name: "preventOverflow",
              options: { boundary: document.body },
            },
          ]}
        >
          Affiliate relationship could be set up if there is at least one community associated with the current
          organization. Please create the community and get back to the screen if needed
        </Tooltip>
      )}

      <Scrollable style={{ flex: 1 }}>
        {tab === STEP.LEGAL_INFO && (
          <div className="LegalInfo">
            <div className="OrganizationForm-Section LegalInfo-Section">
              <div className="OrganizationForm-SectionTitle">General Data</div>

              <Row>
                <Col lg={8} md={6}>
                  <TextField
                    type="text"
                    name="name"
                    value={fields.name}
                    label="Organization Name*"
                    maxLength={256}
                    className="OrganizationForm-TextField"
                    errorText={errors.name || nameError.message}
                    onChange={changeField}
                  />
                </Col>

                <Col lg={4} md={6}>
                  <TextField
                    type="text"
                    name="oid"
                    value={fields.oid}
                    tabIndex={1}
                    label={isEditing ? "Organization OID" : "Organization OID*"}
                    renderLabelIcon={() => <Info id="Oid-Hint" className="OrganizationForm-LabelIcon" />}
                    tooltip={{
                      target: "Oid-Hint",
                      boundariesElement: "document.body",
                      text: `Organization OID is a unique ID that is used for
                                        identifying the organization across Simply Connect system.
                                        'Provider NPI' or 'Federal TAX ID' can be used for this purpose.`,
                    }}
                    className="OrganizationForm-TextField"
                    errorText={errors.oid || oidError.message}
                    isDisabled={isEditing}
                    maxLength={256}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col lg={4} md={4}>
                  <TextField
                    type="text"
                    name="companyId"
                    value={fields.companyId}
                    label="Company ID*"
                    renderLabelIcon={() => <Info id="Company-ID-Hint" className="OrganizationForm-LabelIcon" />}
                    tooltip={{
                      target: "Company-ID-Hint",
                      boundariesElement: "document.body",
                      text: `Company ID is a part of the credentials required
                                                     to log in to Simply Connect portal.`,
                    }}
                    className="OrganizationForm-TextField"
                    errorText={errors.companyId || companyIdError.message}
                    isDisabled={isEditing}
                    maxLength={25}
                    onChange={changeField}
                  />
                </Col>

                <Col lg={4} md={4}>
                  <TextField
                    type="email"
                    name="email"
                    value={fields.email}
                    label="Email*"
                    className="OrganizationForm-TextField"
                    errorText={errors.email}
                    maxLength={256}
                    onChange={changeField}
                  />
                </Col>

                <Col lg={4} md={4}>
                  <PhoneField
                    name="phone"
                    value={fields.phone}
                    label="Phone*"
                    className="OrganizationForm-TextField"
                    errorText={errors.phone}
                    onChange={changeField}
                    renderLabelIcon={() => <Info id="phone-hint" className="OrganizationForm-LabelIcon" />}
                    tooltip={{
                      target: "phone-hint",
                      boundariesElement: "document.body",
                      render: () => (
                        <ul className="OrganizationForm-PhoneTooltipBody">
                          <li>Use digits only and "+" before country code.</li>
                          <li>Otherwise no spaces, dashes, or special symbols allowed.</li>
                          {/*<li>Digits only allowed</li>
                          <li>No spaces, dashes, or special symbols</li>
                          <li>Country code is required</li>
                          <li>‘+’ may be a leading symbol</li>*/}
                        </ul>
                      ),
                    }}
                  />
                </Col>
              </Row>
            </div>

            <div className="OrganizationForm-Section LegalInfo-Section">
              <div className="OrganizationForm-SectionTitle">Address</div>

              <Row>
                <Col lg={8} md={6}>
                  <TextField
                    type="text"
                    name="street"
                    value={fields.street}
                    label="Street*"
                    maxLength={256}
                    className="OrganizationForm-TextField"
                    errorText={errors.street}
                    onChange={changeField}
                  />
                </Col>

                <Col lg={4} md={6}>
                  <TextField
                    type="text"
                    name="city"
                    value={fields.city}
                    label="City*"
                    maxLength={256}
                    className="OrganizationForm-TextField"
                    errorText={errors.city}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col lg={4} md={6}>
                  <SelectField
                    name="stateId"
                    value={fields.stateId}
                    label="State*"
                    options={mappedStates}
                    placeholder="Select State"
                    className="OrganizationForm-SelectField"
                    isMultiple={false}
                    errorText={errors.stateId}
                    onChange={changeField}
                  />
                </Col>

                <Col lg={4} md={6}>
                  <TextField
                    type="number"
                    name="zipCode"
                    value={fields.zipCode}
                    label="Zip Code*"
                    className="OrganizationForm-TextField"
                    maxLength={5}
                    errorText={errors.zipCode}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
            <div className="OrganizationForm-Section LegalInfo-Section">
              <div className="OrganizationForm-SectionTitle">Organization Logo</div>

              <Row>
                <Col lg={8} md={8}>
                  <FileFieldWithCrop
                    hasHint
                    name="logo"
                    value={fields.logo ? fields.logo.name : fields.logoName}
                    label="Select file"
                    renderLabelIcon={() => <Info id="Logo-Hint" className="OrganizationForm-LabelIcon" />}
                    tooltip={{
                      target: "Logo-Hint",
                      boundariesElement: "document.body",
                      text: `The maximum file size for uploads is 1 MB
                                           Only image files (JPG, JPEG, GIF, PNG) are allowed
                                           Recommended aspect ratio is 3:1
                                           Recommended image resolution is 42x14`,
                    }}
                    className="OrganizationForm-TextField"
                    errorText={errors.logo?.size || errors.logo?.type}
                    hintText="Supported file types: JPG, JPEG, PNG, GIF | Max 1 mb"
                    onChange={onChangeLogoField}
                    aspect={3}
                  />
                </Col>
              </Row>
            </div>
          </div>
        )}

        {tab === STEP.MARKETPLACE && (
          <div className="Marketplace">
            <div className="OrganizationForm-Section Marketplace-Section">
              <Row>
                <Col lg={6}>
                  <CheckboxField
                    name="marketplace.confirmVisibility"
                    value={fields.marketplace.confirmVisibility}
                    className="OrganizationForm-ConfirmVisibilityField"
                    label="Confirm that organization will be visible in Marketplace"
                    renderLabelIcon={() => (
                      <Info id="ConfirmVisibilityHint" className="OrganizationForm-InfoHint ConfirmVisibilityHint" />
                    )}
                    isDisabled={!permissions.canEditConfirmMarketplaceVisibility}
                    tooltip={{
                      placement: "right",
                      target: "ConfirmVisibilityHint",
                      boundariesElement: document.body,
                      text: 'The organization will be available in the search results in mobile and web apps ("Marketplace" feature).',
                    }}
                    onChange={changeField}
                  />
                </Col>
                <Col lg={6}>
                  <CheckboxField
                    name="allowExternalInboundReferrals"
                    value={fields.allowExternalInboundReferrals}
                    className="OrganizationForm-AllowInboundRRField"
                    label="Allow referral requests from outside of network"
                    renderLabelIcon={() => (
                      <Info id="AllowInboundRRHint" className="OrganizationForm-InfoHint AllowInboundRRHint" />
                    )}
                    isDisabled={!permissions.canEditAllowExternalInboundReferrals}
                    tooltip={{
                      placement: "right",
                      target: "AllowInboundRRHint",
                      boundariesElement: document.body,
                      text: "By enabling this checkbox you allow referral requests from outside of network.",
                    }}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col lg={12}>
                  <TextField
                    type="textarea"
                    name="marketplace.servicesSummaryDescription"
                    value={fields.marketplace.servicesSummaryDescription}
                    label="Services Summary Description*"
                    className="OrganizationForm-TextAreaField"
                    maxLength={5000}
                    placeholder={"Maximum 5000 characters allowed."}
                    errorText={errors.marketplace?.servicesSummaryDescription}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col lg={6} md={6}>
                  <SelectField
                    isMultiple
                    hasValueTooltip
                    name="marketplace.serviceCategoryIds"
                    value={serviceCategoryIds}
                    options={mappedServiceCategories}
                    label="Category*"
                    className="OrganizationForm-SelectField"
                    errorText={errors.marketplace?.serviceCategoryIds}
                    onChange={onChangeCategoryField}
                  />
                </Col>
                <Col lg={6} md={6}>
                  <SelectField
                    isMultiple
                    isSectioned
                    hasValueTooltip
                    hasSectionIndicator
                    hasSectionSeparator
                    name="marketplace.serviceIds"
                    value={serviceIds}
                    sections={mappedServices}
                    label="Services*"
                    className="OrganizationForm-SelectField"
                    isDisabled={isEmpty(serviceCategoryIds)}
                    errorText={errors.marketplace?.serviceIds}
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <Row>
                <Col lg={6} md={6}>
                  <SelectField
                    isMultiple
                    hasNoneOption
                    hasValueTooltip
                    name="marketplace.languageIds"
                    value={languageIds}
                    options={mappedMarketplaceLanguages}
                    label="Languages"
                    placeholder="Select"
                    className="OrganizationForm-SelectField"
                    errorText={errors.marketplace?.languageIds}
                    onChange={changeField}
                  />
                </Col>
              </Row>
            </div>
          </div>
        )}

        {tab === STEP.FEATURES && (
          <div className="OrganizationForm-Features">
            <div className="OrganizationForm-Section OrganizationForm-FeaturesSection">
              <FeatureSwitchField
                name="features.isChatEnabled"
                label="Chat"
                description="You can send messages and documents"
                isChecked={fields.features.isChatEnabled}
                onChange={onChangeFeatureField}
                isDisabled={!permissions.canEditFeatures}
                className="OrganizationForm-FeatureSwitchField"
              />
              <FeatureSwitchField
                name="features.isVideoEnabled"
                label="Video"
                description="You can make audio and video calls"
                isChecked={fields.features.isVideoEnabled}
                onChange={onChangeFeatureField}
                isDisabled={!permissions.canEditFeatures}
                className="OrganizationForm-FeatureSwitchField"
              />
              <FeatureSwitchField
                name="features.areComprehensiveAssessmentsEnabled"
                label="Comprehensive assessments"
                description="You can assess clients’ health status and define their needs"
                isChecked={fields.features.areComprehensiveAssessmentsEnabled}
                onChange={onChangeFeatureField}
                isDisabled={!permissions.canEditFeatures}
                className="OrganizationForm-FeatureSwitchField"
              />
              {/*<FeatureSwitchField*/}
              {/*	name="features.isPaperlessHealthcareEnabled"*/}
              {/*	label="Paperless Healthcare"*/}
              {/*	description="You can access Paperless Healthcare screen"*/}
              {/*	isChecked={fields.features.isPaperlessHealthcareEnabled}*/}
              {/*	onChange={onChangeFeatureField}*/}
              {/*	isDisabled={!permissions.canEditFeatures}*/}
              {/*	className="OrganizationForm-FeatureSwitchField"*/}
              {/*/>*/}
              <FeatureSwitchField
                name="features.areAppointmentsEnabled"
                label="Appointments"
                description="Access to shared appointment scheduling tool"
                isChecked={fields.features.areAppointmentsEnabled}
                onChange={onChangeAppointmentsFeatureField}
                isDisabled={!permissions.canEditFeatures}
                className="OrganizationForm-FeatureSwitchField"
              />
              <FeatureSwitchField
                name="features.isSignatureEnabled"
                label="E-sign"
                description="Access to Electronic Signature feature"
                isChecked={fields.features.isSignatureEnabled}
                onChange={onChangeESignFeatureField}
                isDisabled={!permissions.canEditFeatures}
                className="OrganizationForm-FeatureSwitchField"
              />
            </div>
          </div>
        )}

        {tab === STEP.AFFILIATE_RELATIONSHIP && (
          <div className="OrganizationForm-Section">
            <div className="OrganizationForm-SectionTitle">
              <div>
                {`Affiliated Organizations & Communities`}
                <Info
                  id="AffiliatedRelationshipHint"
                  className="OrganizationForm-InfoHint AffiliatedRelationshipHint"
                />
                <Tooltip
                  placement="right"
                  target="AffiliatedRelationshipHint"
                  modifiers={[
                    {
                      name: "offset",
                      options: { offset: [0, 6] },
                    },
                    {
                      name: "preventOverflow",
                      options: { boundary: document.body },
                    },
                  ]}
                >
                  This option allows the users associated with the affiliated organizations to receive notifications
                  about events coming to the primary organization.
                </Tooltip>
              </div>

              {isSuperAdmin && (
                <Button color="success" onClick={onAddRelationship}>
                  Add relationship
                </Button>
              )}
            </div>

            {fields.affiliatedRelationships.map((relationship, i) => (
              <AffiliateRelationshipSection
                key={i}
                name={`affiliatedRelationships.${i}`}
                title={`Relationship ${i + 1}`}
                value={relationship}
                organizationId={organizationId}
                errors={errors.affiliatedRelationships?.[i]}
                onChange={changeField}
                onRemove={() => onRemoveRelationship(i, relationship.isChanged())}
              />
            ))}
          </div>
        )}
      </Scrollable>

      <div className="OrganizationForm-Buttons">
        <FormActionButtons />
      </div>

      {isRemoveARConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="OK"
          title="Relationship, including affiliated care team members, will be removed"
          onConfirm={onConfirmRemovingAR}
          onCancel={() => setIsRemoveARConfirmDialogOpen(false)}
        />
      )}

      {isTurnOffESignFeatureWarningDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          title={turnOffESignFeatureWarningDialogTitle}
          onConfirm={onConfirmTurnOffESignFeature}
          onCancel={() => toggleTurnOffESignFeatureWarningDialog(false)}
        />
      )}

      {isTurnOffAppointmentsFeatureWarningDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          title={turnOffAppointmentsFeatureWarningDialogTitle}
          onConfirm={onConfirmTurnOffAppointmentsFeature}
          onCancel={() => toggleTurnOffAppointmentsFeatureWarningDialog(false)}
        />
      )}
    </Form>
  );
}

export default compose(connect(null, mapDispatchToProps), memo)(OrganizationForm);
