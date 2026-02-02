import React, { useMemo, useCallback, useEffect } from "react";

import { Link } from "react-router-dom";

import { map, sortBy, groupBy, isNumber, findWhere } from "underscore";

import { Row, Col, Button, UncontrolledTooltip as Tooltip } from "reactstrap";

import { ErrorViewer, Loader } from "components";

import { ConfirmDialog, WarningDialog } from "components/dialogs";

import {
  FileField,
  TextField,
  PhoneField,
  SelectField,
  SwitchField,
  DropzoneField,
  RadioGroupField,
  CheckboxField,
} from "components/Form";

import { useToggle, useSelectOptions } from "hooks/common";

import { useStatesQuery, useClientsQuery } from "hooks/business/directory/query";

import { useOrganizationQuery } from "hooks/business/admin/organization";

import { useCanConfigureQuery, useServerSignedCertificateMutation } from "hooks/business/community";

import { useESignRequestCount } from "hooks/business/documents/e-sign";

import { useCareTeamIncomingInvitationsQuery } from "hooks/business/care-team";

import {
  E_SIGN_STATUSES,
  ALLOWED_FILE_FORMATS,
  HIE_CONSENT_POLICIES,
  ALLOWED_FILE_FORMAT_MIME_TYPES,
} from "lib/Constants";

import { isEmpty } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import { CertificateSection } from "../";

import { ReactComponent as Info } from "images/info.svg";
import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./SettingsFormFields.scss";

const { PNG, JPG, JPEG, GIF } = ALLOWED_FILE_FORMATS;

const ALLOWED_PHOTOS_MIME_TYPES = [PNG, JPG, JPEG, GIF].map((type) => ALLOWED_FILE_FORMAT_MIME_TYPES[type]);

const { SENT, REQUESTED } = E_SIGN_STATUSES;

const HIE_CONSENT_POLICY_OPTIONS = [
  { value: HIE_CONSENT_POLICIES.OPT_IN, label: "Opt In" },
  { value: HIE_CONSENT_POLICIES.OPT_OUT, label: "Opt Out" },
];

function SettingsFormFields({
  errors,
  fields,
  permissions,
  organizationId,

  onError,
  onClearField,
  onChangeField,
  onChangeFields,
}) {
  const [isConfirmToggleSecurityPinDialogOpen, toggleConfirmToggleSecurityPinDialog] = useToggle(false);
  const [isConfirmTurnOffSecurityPinDialogOpen, toggleConfirmTurnOffSecurityPinDialog] = useToggle(false);
  const [isHieConsentPolicyWarningDialogOpen, setHieConsentPolicyWarningDialog] = useToggle(false);
  const [isPrimaryContactDeletionConfirmDialogOpen, togglePrimaryContactDeletionConfirmDialog] = useToggle(false);

  const isEditing = isNumber(fields.id);

  const {
    data: clients,
    refetch: fetchClients,
    isFetching: isFetchingClients,
  } = useClientsQuery(
    {
      communityIds: [fields.id],
      hieConsentPolicyName: HIE_CONSENT_POLICIES.OPT_IN,
    },
    {
      enabled: false,
      staleTime: 0,
    },
  );

  const {
    data: careTeamInvitations,
    refetch: fetchCareTeamInvitaions,
    isFetching: isFetchingCareTeamInvitations,
  } = useCareTeamIncomingInvitationsQuery(
    {
      communityId: fields.id,
    },
    {
      enabled: false,
      staleTime: 0,
    },
  );

  const careTeamInvitationSections = useMemo(() => {
    return sortBy(
      map(groupBy(careTeamInvitations, "clientId"), (data, id) => ({
        id: +id,
        fullName: `${data[0].clientFirstName} ${data[0].clientLastName}`,
        options: map(data, (o) => ({
          id: o.targetEmployeeId,
          fullName: `${o.targetFirstName} ${o.targetLastName}`,
        })),
      })),
    );
  }, [careTeamInvitations]);

  const { data: organization = {} } = useOrganizationQuery(
    {
      organizationId,
      isMarketplaceDataIncluded: !isEditing,
    },
    {
      enabled: Boolean(organizationId),
    },
  );

  const { data: canConfigure } = useCanConfigureQuery({ organizationId });

  const {
    data: signatureRequestNumber,
    refetch: refetchSignatureRequestCount,
    isFetching: isFetchingESignRequestCount,
  } = useESignRequestCount(
    {
      communityId: fields.id,
      statuses: [SENT, REQUESTED],
    },
    {
      onError,
      enabled: false,
    },
  );

  const {
    error: certError,
    reset: resetCertError,
    mutateAsync: fetchCert,
    isLoading: isFetchingCert,
  } = useServerSignedCertificateMutation({
    onSuccess: ({ data = null }) => {
      onChangeFields({
        certificateLoaded: true,
        docutrackPharmacyConfig: {
          serverCertificate: data,
        },
      });
    },
    onError: () => {
      onChangeField("certificateLoaded", false);
    },
  });

  const { data: states = [] } = useStatesQuery();

  const stateOptions = useSelectOptions(states, { textProp: "label" });

  function addBusinessUnitCode() {
    onChangeField(
      "docutrackPharmacyConfig.businessUnitCodes",
      fields.docutrackPharmacyConfig.businessUnitCodes.push(""),
    );
  }

  function changeServerDomain(_, value) {
    onChangeFields({
      certificateLoaded: false,
      docutrackPharmacyConfig: {
        serverDomain: value,
        serverCertificate: null,
        configuredCertificate: null,
        useSuggestedCertificate: true,
        publicKeyCertificates: [],
      },
    });
  }

  function fetchServerSelfSignedCert() {
    fetchCert({
      serverDomain: fields.docutrackPharmacyConfig.serverDomain,
    });
  }

  const onChangeLogoField = useCallback(
    (name, value) => {
      onChangeField(name, value);
      if (!value) onClearField("logoName");
    },
    [onClearField, onChangeField],
  );

  const onChangeCoverField = useCallback(
    (name, value) => {
      onChangeField(name, value);

      if (!value) onClearField("coverName");
    },
    [onClearField, onChangeField],
  );

  const onChangeSecurityPin = useCallback(
    async (name, value) => {
      if (value) {
        if (isEditing) {
          const count = await refetchSignatureRequestCount();

          if (count) {
            toggleConfirmToggleSecurityPinDialog(true);
          } else {
            onChangeField(name, value);
          }
        } else onChangeField(name, value);
      } else {
        toggleConfirmTurnOffSecurityPinDialog(true);
      }
    },
    [
      isEditing,
      onChangeField,
      refetchSignatureRequestCount,
      toggleConfirmToggleSecurityPinDialog,
      toggleConfirmTurnOffSecurityPinDialog,
    ],
  );

  const onChangeState = useCallback(
    (name, value) => {
      if (isEditing) return onChangeField(name, value);

      const state = findWhere(states, { id: value });

      onChangeFields({
        [name]: value,
        hieConsentPolicyName: state?.hieConsentPolicyName,
      });
    },
    [states, isEditing, onChangeField, onChangeFields],
  );

  const onChangeHieConsentPolicy = useCallback(
    async (name, value) => {
      onChangeField(name, value);

      if (isEditing && value === HIE_CONSENT_POLICIES.OPT_OUT) {
        const clients = await fetchClients();

        if (!isEmpty(clients)) setHieConsentPolicyWarningDialog(true);

        const careTeamInvitations = await fetchCareTeamInvitaions();

        if (!isEmpty(careTeamInvitations)) {
          togglePrimaryContactDeletionConfirmDialog(true);
        }
      }
    },
    [
      isEditing,
      fetchClients,
      onChangeField,
      fetchCareTeamInvitaions,
      setHieConsentPolicyWarningDialog,
      togglePrimaryContactDeletionConfirmDialog,
    ],
  );

  const onConfirmToggleSecurityPinDialog = useCallback(() => {
    onChangeField("signatureConfig.isPinEnabled", !fields.signatureConfig.isPinEnabled);
    toggleConfirmToggleSecurityPinDialog(false);
  }, [fields.signatureConfig, onChangeField, toggleConfirmToggleSecurityPinDialog]);

  const onConfirmTurnOffSecurityPin = useCallback(async () => {
    toggleConfirmTurnOffSecurityPinDialog(false);

    if (isEditing) {
      const data = await refetchSignatureRequestCount();

      if (data) {
        toggleConfirmToggleSecurityPinDialog(true);
      } else {
        onChangeField("signatureConfig.isPinEnabled", false);
      }
    } else onChangeField("signatureConfig.isPinEnabled", false);
  }, [
    isEditing,
    onChangeField,
    refetchSignatureRequestCount,
    toggleConfirmToggleSecurityPinDialog,
    toggleConfirmTurnOffSecurityPinDialog,
  ]);

  const changeFaxCheckbox = (name, value) => {
    onChangeField(name, value);
    if (!value) {
      onChangeField("fax", "");
      onChangeField("faxLogin", "");
      onChangeField("faxPassword", "");
    }
  };

  return (
    <div className="Settings">
      {(isFetchingESignRequestCount || isFetchingCert || isFetchingClients || isFetchingCareTeamInvitations) && (
        <Loader hasBackdrop />
      )}

      <div className="CommunityForm-Section Settings-Section">
        <div className="CommunityForm-SectionTitle">General Data</div>

        <Row>
          <Col lg={8} md={6}>
            <TextField
              type="text"
              name="name"
              value={fields.name}
              label="Community Name*"
              className="CommunityForm-TextField"
              errorText={errors.name}
              maxLength={256}
              onChange={onChangeField}
            />
          </Col>

          <Col lg={4} md={6}>
            <TextField
              type="text"
              name="oid"
              value={fields.oid}
              label={isEditing ? "Community OID" : "Community OID*"}
              className="CommunityForm-TextField"
              renderLabelIcon={() => <Info id="Oid-Hint" className="CommunityForm-LabelIcon" />}
              tooltip={{
                target: "Oid-Hint",
                boundariesElement: document.body,
                text: "Community OID is a unique ID that is used for identifying the community across Simply Connect system.",
              }}
              maxLength={256}
              errorText={errors.oid}
              isDisabled={isEditing}
              onChange={onChangeField}
            />
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="licenseNumber"
              value={fields.licenseNumber}
              label="License #"
              className="CommunityForm-TextField"
              maxLength={56}
              errorText={errors.licenseNumber}
              onChange={onChangeField}
            />
          </Col>
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="numberOfBeds"
              value={fields.numberOfBeds}
              label="# of units"
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.numberOfBeds}
              onChange={onChangeField}
            />
          </Col>
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="numberOfVacantBeds"
              value={fields.numberOfVacantBeds}
              label="# of open units"
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.numberOfVacantBeds}
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>
      <div className="CommunityForm-Section Settings-Section">
        <div className="CommunityForm-SectionTitle">Fax</div>
        <Row>
          <Col>
            <CheckboxField
              name="openFax"
              value={fields.openFax}
              label="Community can send fax"
              className="ClientForm-CheckboxField"
              onChange={changeFaxCheckbox}
              renderLabelIcon={() => <Info id="addFax-Hint" className="ClientForm-InfoIcon" />}
              tooltip={{
                target: "addFax-Hint",
                boundariesElement: document.body,
                text: "These fields require fax information from your WestFax account. Incorrect information will result in unsuccessful saving.",
              }}
            />
          </Col>
        </Row>
        <Row>
          {/*  <Col lg={4} md={4}>
            <TextField
              type="text"
              name="fax"
              value={fields.fax}
              label={fields.openFax ? "Fax Number *" : "Fax Number"}
              className="CommunityForm-TextField"
              isDisabled={!fields.openFax}
              maxLength={256}
              errorText={errors.fax}
              onChange={onChangeField}
            />
          </Col>*/}
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="faxLogin"
              value={fields.faxLogin}
              isDisabled={!fields.openFax}
              label={fields.openFax ? "Fax Username *" : "Fax Username"}
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.faxLogin}
              onChange={onChangeField}
            />
          </Col>
          <Col lg={4} md={4}>
            <TextField
              type="text"
              name="faxPassword"
              value={fields.faxPassword}
              isDisabled={!fields.openFax}
              label={fields.openFax ? "Fax Password *" : "Fax Password"}
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.faxPassword}
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>
      <div className="CommunityForm-Section Settings-Section">
        <div className="CommunityForm-SectionTitle">Address</div>

        <Row>
          <Col lg={4} md={4}>
            <TextField
              type="email"
              name="email"
              value={fields.email}
              label="Email*"
              className="CommunityForm-TextField"
              errorText={errors.email}
              maxLength={256}
              onChange={onChangeField}
            />
          </Col>

          <Col lg={4} md={4}>
            <PhoneField
              name="phone"
              value={fields.phone}
              label="Phone*"
              className="CommunityForm-PhoneField"
              errorText={errors.phone}
              onChange={onChangeField}
              renderLabelIcon={() => <Info id="phone-hint" className="CommunityForm-LabelIcon" />}
              tooltip={{
                target: "phone-hint",
                boundariesElement: document.body,
                render: () => (
                  <ul className="CommunityForm-PhoneTooltipBody">
                    <li>Digits only allowed</li>
                    <li>No spaces, dashes, or special symbols</li>
                    <li>Country code is required</li>
                    <li>‘+’ may be a leading symbol</li>
                  </ul>
                ),
              }}
            />
          </Col>

          <Col lg={4} md={4}>
            <SelectField
              name="stateId"
              value={fields.stateId}
              label="State*"
              options={stateOptions}
              placeholder="Select State"
              className="CommunityForm-SelectField"
              errorText={errors.stateId}
              onChange={onChangeState}
            />
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={6}>
            <TextField
              type="text"
              name="city"
              value={fields.city}
              label="City*"
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.city}
              onChange={onChangeField}
            />
          </Col>

          <Col lg={8} md={6}>
            <TextField
              type="text"
              name="street"
              value={fields.street}
              label="Street*"
              className="CommunityForm-TextField"
              maxLength={256}
              errorText={errors.street}
              onChange={onChangeField}
            />
          </Col>
        </Row>

        <Row>
          <Col lg={4} md={4}>
            <TextField
              type="number"
              name="zipCode"
              value={fields.zipCode}
              label="Zip Code*"
              className="CommunityForm-TextField"
              maxLength={5}
              errorText={errors.zipCode}
              onChange={onChangeField}
            />
          </Col>
          <Col lg={8} md={6}>
            <TextField
              type="text"
              name="websiteUrl"
              label="Website Url"
              value={fields.websiteUrl}
              errorText={errors.websiteUrl}
              className="CommunityForm-TextField"
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>

      <div className="CommunityForm-Section Settings-Section">
        <div className="CommunityForm-SectionTitle">Photos</div>

        <Row>
          <Col lg={6} md={6}>
            <FileField
              hasHint
              name="logo"
              value={fields.logo ? fields.logo.name : fields.logoName}
              label="Community Logo"
              renderLabelIcon={() => <Info id="Logo-Hint" className="CommunityForm-LabelIcon" />}
              tooltip={{
                target: "Logo-Hint",
                boundariesElement: document.body,
                text: `The maximum file size for uploads is 1 MB
                                                   Only image files (JPG, GIF, PNG) are allowed
                                                   Recommended aspect ratio is 3:1
                                                   Recommended image resolution is 42x147`,
              }}
              className="CommunityForm-FileField"
              errorText={errors.logo?.size || errors.logo?.type}
              hintText="Supported file types: JPG, PNG, GIF | Max 1 mb"
              onChange={onChangeLogoField}
            />
          </Col>

          {/* !Community Cover */}
          <Col lg={6} md={6}>
            <FileField
              hasHint
              name="cover"
              value={fields.cover ? fields.cover.name : fields.coverName}
              label="Community Cover"
              renderLabelIcon={() => <Info id="Logo-Hint-Cover" className="CommunityForm-LabelIcon" />}
              tooltip={{
                target: "Logo-Hint-Cover",
                boundariesElement: document.body,
                text: `The maximum file size for uploads is 1 MB
                                                   Only image files (JPG, GIF, PNG) are allowed
                                                   Recommended aspect ratio is 5:3
                                                   Recommended image resolution is 400*240`,
              }}
              className="CommunityForm-FileField"
              errorText={errors.cover?.size || errors.cover?.type}
              hintText="Supported file types: JPG, PNG, GIF | Max 1 mb"
              onChange={onChangeCoverField}
            />
          </Col>
        </Row>

        <Row>
          <Col>
            <DropzoneField
              name="pictureFiles"
              label="Community Photos"
              value={fields.pictureFiles}
              maxCount={10}
              hintText="Supported file types: JPEG, GIF, PNG | Max 20 mb"
              className="CommunityForm-DropzoneField"
              errors={errors.pictureFiles}
              allowedTypes={ALLOWED_PHOTOS_MIME_TYPES}
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>

      {canConfigure && (
        <div className="CommunityForm-Section Settings-Section">
          <div className="CommunityForm-SectionHeader">
            <div className="CommunityForm-SectionTitle">DocuTrack Setup</div>

            <Button
              data-testid="add-buc-btn"
              color="success"
              disabled={
                !permissions.canEditDocutrack ||
                !fields.docutrackPharmacyConfig.isIntegrationEnabled ||
                fields.docutrackPharmacyConfig.businessUnitCodes.last() === ""
              }
              onClick={addBusinessUnitCode}
            >
              Add BUC
            </Button>
          </div>

          <Row>
            <Col>
              <SwitchField
                name="docutrackPharmacyConfig.isIntegrationEnabled"
                isDisabled={!permissions.canEditDocutrack}
                isChecked={fields.docutrackPharmacyConfig.isIntegrationEnabled}
                onChange={onChangeField}
                className="CommunityForm-SwitchField"
                label="Enable Integration"
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={12}>
              <TextField
                type="text"
                name="docutrackPharmacyConfig.clientType"
                value={fields.docutrackPharmacyConfig.clientType}
                label="Client Type*"
                className="CommunityForm-TextField"
                maxLength={50}
                isDisabled={!permissions.canEditDocutrack || !fields.docutrackPharmacyConfig.isIntegrationEnabled}
                errorText={errors.docutrackPharmacyConfig?.clientType}
                onChange={onChangeField}
              />
            </Col>

            <Col lg={4} md={12}>
              <TextField
                type="text"
                name="docutrackPharmacyConfig.serverDomain"
                value={fields.docutrackPharmacyConfig.serverDomain}
                label="Server domain*"
                className="CommunityForm-TextField"
                maxLength={256}
                isDisabled={!permissions.canEditDocutrack || !fields.docutrackPharmacyConfig.isIntegrationEnabled}
                errorText={errors.docutrackPharmacyConfig?.serverDomain}
                onChange={changeServerDomain}
              />
            </Col>

            <Col lg={4} md={12}>
              <Button
                color="success"
                data-testid="get-certificate-btn"
                className="margin-top-28"
                disabled={
                  !fields.docutrackPharmacyConfig.isIntegrationEnabled ||
                  !fields.docutrackPharmacyConfig.serverDomain ||
                  !permissions.canEditDocutrack ||
                  isFetchingCert
                }
                onClick={fetchServerSelfSignedCert}
              >
                Get certificate
              </Button>
            </Col>
          </Row>

          <Row>
            {fields.docutrackPharmacyConfig.businessUnitCodes.map((code, i) => {
              return (
                <Col key={i} lg={4} md={6} sm={12}>
                  <TextField
                    type="text"
                    name={`docutrackPharmacyConfig.businessUnitCodes.${i}`}
                    value={code}
                    label="Business Unit Code"
                    className="CommunityForm-TextField"
                    maxLength={256}
                    isDisabled={!permissions.canEditDocutrack || !fields.docutrackPharmacyConfig.isIntegrationEnabled}
                    errorText={errors.docutrackPharmacyConfig?.businessUnitCodes?.[i]}
                    onChange={onChangeField}
                  />
                </Col>
              );
            })}
          </Row>

          {permissions.canEditDocutrack &&
            fields.certificateLoaded &&
            fields.docutrackPharmacyConfig.isIntegrationEnabled && (
              <CertificateSection config={fields.docutrackPharmacyConfig} onChange={onChangeField} />
            )}
        </div>
      )}

      <div className="CommunityForm-Section">
        <div className="CommunityForm-SectionHeader">
          <div className="CommunityForm-SectionTitle">
            HIE Opt In / Opt Out
            <Info id="hieConsentPolicy-Hint" className="CommunityForm-LabelIcon" />
            <Tooltip
              target="hieConsentPolicy-Hint"
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
              You have the right to decide to make your health information viewable to other organizations participating
              in your care by Opting In.
            </Tooltip>
          </div>
        </div>
        <Row>
          <Col>
            <RadioGroupField
              name="hieConsentPolicyName"
              title="Opt In / Opt Out Policy*"
              options={HIE_CONSENT_POLICY_OPTIONS}
              selected={fields.hieConsentPolicyName}
              className="d-flex"
              isDisabled={!(isEditing && permissions.canEditHieConsentPolicy)}
              errorText={errors.hieConsentPolicyName}
              onChange={onChangeHieConsentPolicy}
            />
          </Col>
        </Row>
      </div>

      {organization?.features?.isSignatureEnabled && (
        <div className="CommunityForm-Section Settings-Section">
          <div className="CommunityForm-SectionHeader">
            <div className="CommunityForm-SectionTitle">E-signature Setup</div>
          </div>
          <Row>
            <Col>
              <SwitchField
                label="Enable Security PIN"
                isDisabled={!permissions.canEditSignatureSetup}
                className="CommunityForm-SwitchField"
                name="signatureConfig.isPinEnabled"
                isChecked={fields.signatureConfig.isPinEnabled}
                onChange={onChangeSecurityPin}
              />
            </Col>
          </Row>
        </div>
      )}

      <div className="CommunityForm-Section Settings-Section">
        <div className="CommunityForm-SectionHeader">
          <div className="CommunityForm-SectionTitle">Events</div>
        </div>
        <Row>
          <Col>
            <SwitchField
              label="Medication Risk Alerts"
              isDisabled={isEditing ? !fields.eventsConfig.canEdit : !permissions.canEditEventsConfig}
              className="CommunityForm-SwitchField"
              name="eventsConfig.isMedicationRiskAlertingEnabled"
              isChecked={fields.eventsConfig.isMedicationRiskAlertingEnabled}
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>

      <div className="CommunityForm-Section LegalInfo-Section">
        <div className="CommunityForm-SectionHeader">
          <div className="CommunityForm-SectionTitle">
            Merging Client Records
            <Info id="matchedClientSearchAbilityHint" className="CommunityForm-LabelIcon" />
            <Tooltip
              target="matchedClientSearchAbilityHint"
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
              Simply Connect allows Clients to sign up through the mobile app and search for their records in Simply
              Connect. If the “Allow merging data” flag is on, then the following data from your community will be
              shared with the Client when they sign up: Care team, Chats, Facesheet, CCD, Medications, Documents,
              Assessments, Events, Notes, Service plans.
            </Tooltip>
          </div>
        </div>
        <Row>
          <Col>
            <SwitchField
              label="Allow Data Merging"
              isDisabled={fields.isFamily}
              className="CommunityForm-SwitchField"
              name="allowFamilyAppToGetMatchedClients"
              isChecked={fields.allowFamilyAppToGetMatchedClients}
              onChange={onChangeField}
            />
          </Col>
        </Row>
      </div>

      {certError && <ErrorViewer isOpen error={certError} onClose={resetCertError} />}

      {isConfirmToggleSecurityPinDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="Proceed"
          title={`${signatureRequestNumber} active signature request(s) found in your community. All request(s) will be canceled. Do you want to proceed?`}
          onConfirm={onConfirmToggleSecurityPinDialog}
          onCancel={() => toggleConfirmToggleSecurityPinDialog()}
        />
      )}
      {isConfirmTurnOffSecurityPinDialogOpen && (
        <ConfirmDialog
          isOpen
          icon={Warning}
          confirmBtnText="Proceed"
          title="By selecting to remove the PIN requirement for all Signature requests through Simply Connect you are eliminating one of the embedded security features, do you want to proceed?"
          onConfirm={onConfirmTurnOffSecurityPin}
          onCancel={() => toggleConfirmTurnOffSecurityPinDialog()}
        />
      )}
      {isHieConsentPolicyWarningDialogOpen && (
        <WarningDialog
          isOpen
          icon={null}
          title={
            <div className="d-flex flex-column align-items-start text-left font-size-18 font-weight-normal">
              <p>Please review Client(s) consent due to update:</p>
              <p className="d-flex flex-column">
                {map(clients, ({ id, fullName }) => (
                  <Link
                    key={`link-${id}`}
                    to={path(`/clients/${id}`)}
                    target="_blank"
                    className="HieConsentPolicyWarningDialog-ClientLink"
                  >
                    {fullName}
                  </Link>
                ))}
              </p>
            </div>
          }
          buttons={[
            {
              text: "Close",
              onClick: () => setHieConsentPolicyWarningDialog(false),
            },
          ]}
          className="HieConsentPolicyWarningDialog"
        />
      )}
      {!isHieConsentPolicyWarningDialogOpen && isPrimaryContactDeletionConfirmDialogOpen && (
        <WarningDialog
          isOpen
          icon={Warning}
          title="As a result of your Opt Out selection, care team invitations will be cancelled for:"
          text={
            <div className="d-flex flex-column align-items-start text-left font-size-18 font-weight-normal">
              {careTeamInvitationSections.map((o) => (
                <p className="d-flex flex-row ">
                  <Link
                    key={`link-${o.id}`}
                    to={path(`/clients/${o.id}`)}
                    target="_blank"
                    className="InvitedCareTeamMember-ClientLink"
                  >
                    {o.fullName}
                  </Link>
                  <span className="InvitedCareTeamMember-Separator">-</span>
                  {o.options.map((member) => (
                    <Link
                      key={`link-${member.id}`}
                      to={path(`/clients/${member.id}`)}
                      target="_blank"
                      className="InvitedCareTeamMember-TargerLink"
                    >
                      {member.fullName}
                    </Link>
                  ))}
                </p>
              ))}
            </div>
          }
          buttons={[
            {
              text: "Close",
              onClick: () => togglePrimaryContactDeletionConfirmDialog(false),
            },
          ]}
        />
      )}
    </div>
  );
}

export default SettingsFormFields;
