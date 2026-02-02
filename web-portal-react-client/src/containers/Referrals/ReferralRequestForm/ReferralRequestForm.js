import React, { memo, useCallback, useEffect, useMemo, useState } from "react";

import { useSelector } from "react-redux";

import {
  any,
  every,
  filter,
  find,
  findWhere,
  first,
  flatten,
  groupBy,
  isEqual,
  last,
  map,
  omit,
  reduce,
  reject,
  sortBy,
  values,
} from "underscore";

import PTypes from "prop-types";

import { useParams } from "react-router-dom";

import { Button, Col, Form, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import { ErrorViewer, Loader } from "components";

import { CheckboxField, DropzoneField, PhoneField, SelectField, TextField } from "components/Form";

import { WarningDialog } from "components/dialogs";

import { useForm, useScrollable, useScrollToFormError } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import {
  useClientCommunitiesQuery,
  useClientOrganizationsQuery,
  useDefaultReferralRequestQuery,
  useReferralRequestSubmit,
} from "hooks/business/referrals";

import { useServicesQuery } from "hooks/business/community";
import service from "services/CommunityService";

import { useContactQuery } from "hooks/business/admin/contact";

import { useClientServicePlanCountQuery } from "hooks/business/client/service-plans";

import { useClientsQuery, useReferralPrioritiesQuery, useStatesQuery } from "hooks/business/directory/query";

import { DocumentDirectory } from "containers/Clients/Clients/Documents";

import ReferralRequestEntity from "entities/ReferralRequest";
import ReferralRequestFormValidator from "validators/ReferralRequestFormValidator";

import { DateUtils as DU, isInteger, isNotEmpty, toNumberExcept } from "lib/utils/Utils";

import { noop } from "lib/utils/FuncUtils";

import { isEmptyOrBlank } from "lib/utils/ObjectUtils";

import { addAsterix } from "lib/utils/StringUtils";

import { HIE_CONSENT_POLICIES, ONLY_VIEW_ROLES } from "lib/Constants";

import { ReactComponent as Info } from "images/info.svg";

import ShareThroughSection from "./ShareThroughSection/ShareThroughSection";
import NetworkSection, { PlainNetworkSection } from "./NetworkSection/NetworkSection";

import "./ReferralRequestForm.scss";
import useVendorReferralRequestSubmit from "../../../hooks/business/referrals/useVendorReferralRequestSubmit";
import ClientCareTeamSection from "./ClientCareTeamSection/ClientCareTeamSection";

const scrollableStyles = { flex: 1 };

const SHARED_WITH_CLIENT = "SHARED_WITH_CLIENT";

const { format, formats } = DU;

const DATE_TIME_FORMAT = formats.longDateMediumTime12;

const CLIENT_FIELDS = [
  "client.id",
  "client.location",
  "client.locationPhone",
  "client.address.street",
  "client.address.city",
  "client.address.stateId",
  "client.address.zip",
  "client.insuranceNetworkTitle",
  "attachmentFiles",
  "attachedClientDocumentIds",
  "attachedClientDocumentFiles",
  "isFacesheetShared",
  "isCcdShared",
  "isServicePlanShared",
];
function textValueMapper({ id, name, title, label, displayName, fullName }) {
  return {
    value: id ?? name,
    text: title ?? label ?? fullName ?? displayName ?? name,
  };
}

const SERVICE_FOR_VENDOR_SHOW_CLIENT = [
  "Healthcare - General",
  "Healthcare - Pharmacy",
  "Healthcare – Providers and Specialty Care",
];
function ReferralRequestForm({
  communityId,
  organizationId,
  marketplace = null,
  isFeaturedCommunity,
  isFromVendor = false,
  isOrganizationDisabled = false,
  onClose,
  onSubmitSuccess,
  vendorID,
  isFromSearch,
}) {
  const [error, setError] = useState(null);
  const [isFetching, setFetching] = useState(false);
  const [needValidation, setNeedValidation] = useState(false);

  const [networkIds, setNetworkIds] = useState([]);
  const [deferredServicesChange, setDeferredServiceChange] = useState(null);
  const [isCcdSharedFieldVisible, setCcdSharedFieldVisible] = useState(true);

  const [attachedClientDocuments, setAttachedClientDocuments] = useState([]);

  const [isClientDocumentDirectoryOpen, toggleClientDocumentDirectory] = useState(false);
  const [isConfirmSwitchServiceDialogOpen, setIsConfirmSwitchServiceDialogOpen] = useState(false);

  const [shouldShowAttachmentsWarningDialog, setShouldShowAttachmentsWarningDialog] = useState(false);
  const [isAttachmentsWarningDialogOpen, setAttachmentsWarningDialogOpen] = useState(false);

  const [prevSelectedServiceId, setPrevSelectedServiceId] = useState(null);
  const [isRemoveClientWarningDialogOpen, setRemoveClientWarningDialogOpen] = useState(false);
  const [isClientCanNotBeSelectedDialogOpen, setClientCanNotBeSelectedDialogOpen] = useState(false);

  const [serviceSections, setServiceSections] = useState();
  const [communityOrignService, setCommunityOrignService] = useState([]);
  const params = useParams();

  const vendorId = isFromSearch ? vendorID : params.id;

  const clientId = toNumberExcept(params.clientId, [null, undefined]);

  const isClient = isInteger(clientId);
  const isMarketplace = marketplace && !isClient;

  const user = useAuthUser();

  const isAssociationType = ONLY_VIEW_ROLES.includes(user.roleName);
  const [vendorCareTeams, setVendorCareTeams] = useState([]);
  const [servicesError, setServicesError] = useState(null);

  useEffect(() => {
    getServiceOptions();
  }, []);
  const getServiceOptions = () => {
    if (isFromVendor) {
      getVendorOptionSections(vendorId);
    } else {
      getCommunityOptionSections(organizationId, communityId);
    }
  };

  const getVendorOptionSections = (vendorId) => {
    service.findVendorServices({ vendorId }).then((res) => {
      const data = res?.map((item) => {
        return {
          id: item.id,
          title: item.title,
          options: map(item.options, (o) => ({
            value: o.id,
            text: o.name,
            PCode: item.title,
          })),
        };
      });
      setServiceSections(data || []);
    });
  };
  const getCommunityOptionSections = (organizationId, communityId) => {
    service.findServices({ organizationId, communityId }).then((res) => {
      const data = sortBy(
        map(groupBy(res, "serviceCategoryId"), (data, id) => ({
          id: +id,
          title: data[0].serviceCategoryTitle,
          options: map(data, (o) => ({
            value: o.id,
            text: o.title,
          })),
        })),
        "title",
      );
      setServiceSections(data);
      setCommunityOrignService(res);
    });
  };

  const getData = (fields, options) => {
    let data = fields.toJS();
    if (isFromVendor || (isFromSearch && vendorID)) {
      data.vendorId = vendorId;
      data.marketplace = { ...data.marketplace, communityId: "111111", organizationId: "" };
    }
    delete data.attachedClientDocumentFiles;
    data.sharedCommunityIds = flatten(values(data.sharedCommunityIds));

    if (!options.canHaveClinicalInfo) {
      data.isCcdShared = false;
      data.isFacesheetShared = false;
      data.isServicePlanShared = false;
    }

    if (!options.canHaveCcd) data.isCcdShared = false;

    if (isEmptyOrBlank(data.client.address)) {
      data.client.address = null;
    }

    if (!data.client.id) {
      delete data.client;
    }

    if (!options.isMarketplace) {
      data = omit(data, "marketplace");
    }
    return data;
  };

  const { fields, errors, isValid, validate, isChanged, changeField, clearFields, changeFields } = useForm(
    "ReferralRequest",
    ReferralRequestEntity,
    ReferralRequestFormValidator,
  );

  const { mutateAsync: submit } = useVendorReferralRequestSubmit({
    onError: setError,
    onSuccess: ({ data }) => {
      onSubmitSuccess(data);
    },
  });

  const { mutateAsync: submitBuilding } = useReferralRequestSubmit({
    onError: setError,
    onSuccess: ({ data }) => {
      onSubmitSuccess(data);
    },
  });

  const { data: organizations, isFetching: isFetchingOrganizations } = useClientOrganizationsQuery(
    { targetCommunityId: communityId },
    {
      staleTime: 0,
      onSuccess: (data) => {
        changeField("referringOrganizationId", data[0]?.id || "");
      },
    },
    isFromVendor,
  );

  const { data: communities, isFetching: isFetchingCommunities } = useClientCommunitiesQuery(
    {
      targetCommunityId: communityId,
      organizationId: fields.referringOrganizationId,
    },
    {
      staleTime: 0,
      enabled: isInteger(fields.referringOrganizationId),
      onSuccess: (data) => {
        changeField("referringCommunityId", data[0]?.id || "");
      },
    },
  );

  const isAttachedClientDocumentFile = useCallback(
    (doc) => any(fields.attachedClientDocumentFiles.toJS(), (o) => isEqual(doc, o)),
    [fields.attachedClientDocumentFiles],
  );

  const { data: states = [] } = useStatesQuery();
  const { data: priorities = [] } = useReferralPrioritiesQuery();

  const { data: servicePlanCount } = useClientServicePlanCountQuery(
    {
      status: SHARED_WITH_CLIENT,
      clientId: clientId ?? fields.client.id,
    },
    {
      enabled: isInteger(clientId ?? fields.client.id),
    },
  );

  const isServicePlanSharedFieldVisible = servicePlanCount !== 0;

  const { data: clients = [] } = useClientsQuery(
    {
      recordStatuses: ["ACTIVE"],
      communityIds: [fields.referringCommunityId],
    },
    {
      staleTime: 0,
      enabled: isInteger(fields.referringCommunityId),
    },
  );

  const serviceIds = useMemo(() => fields?.services?.toArray(), [fields.services]);

  const mappedPriorities = useMemo(
    () =>
      map(
        reject(priorities, (o) => o.name === "OTHER"),
        textValueMapper,
      ),
    [priorities],
  );

  const mappedStates = useMemo(() => map(states, textValueMapper), [states]);

  const mappedClients = useMemo(() => map(clients, textValueMapper), [clients]);

  const outboundOrganizations = useSelector((state) => state.directory.organization.list.dataSource.data);

  const outboundCommunities = useSelector((state) => state.referral.community.list.dataSource.data);

  const mappedOrganizations = useMemo(
    () => (!isMarketplace ? map(outboundOrganizations, textValueMapper) : map(organizations, textValueMapper)),
    [organizations, isMarketplace, outboundOrganizations, isFromVendor],
  );

  const mappedCommunities = useMemo(() => {
    const filteredCommunities = communities?.filter(({ id }) => id !== communityId);
    return map(filteredCommunities, textValueMapper);
  }, [communities, isMarketplace, outboundCommunities, isFromVendor]);

  const selectedService = useMemo(
    () => find(communityOrignService, (o) => o.id === last(fields.services.toJS())),
    [fields, communityOrignService],
  );

  const findParentCodeIfInclude = (data, childIds) => {
    let flagData = [];
    data?.forEach((item) => {
      item?.options?.forEach((o) => {
        if (childIds?.includes(o.value)) {
          flagData.push(o.PCode);
        }
      });
    });
    return isAnyItemInAInB(flagData, SERVICE_FOR_VENDOR_SHOW_CLIENT);
  };
  const isAnyItemInAInB = (A, B) => {
    return A?.some((item) => B?.includes(item));
  };
  const hasBusinessRelatedService = isFromVendor
    ? !findParentCodeIfInclude(serviceSections, fields.services.toJS())
    : any(communityOrignService, (o) => !o?.isClientRelated && o?.isBusinessRelated && fields.services.includes(o.id));

  const hasClientRelatedServicesOnly = isFromVendor
    ? findParentCodeIfInclude(serviceSections, fields.services.toJS())
    : every(
        communityOrignService,
        (o) => o?.isClientRelated && !o?.isBusinessRelated && fields.services.includes(o.id),
      );

  const isClientSectionVisible = isMarketplace
    ? fields.services.toJS().length > 0 && !hasBusinessRelatedService
    : selectedService?.isClientRelated;

  const isClientSectionRequired = isMarketplace
    ? fields.client.id || hasClientRelatedServicesOnly
    : fields.client.id || (selectedService?.isClientRelated && !selectedService?.isBusinessRelated);
  //
  const canHaveClinicalInfo = !selectedService || selectedService?.canAdditionalClinicalInfoBeShared;

  const validationOptions = useMemo(
    () => ({
      included: {
        isMarketplace,
        shouldValidateClientSection: isClientSectionRequired,
      },
    }),
    [isMarketplace, isClientSectionRequired],
  );

  const { Scrollable, scroll } = useScrollable();

  const { data: defaultData, isFetching: isFetchingDefaultData } = useDefaultReferralRequestQuery(
    { clientId: clientId ?? fields.client.id },
    { enabled: isInteger(clientId ?? fields.client.id) },
  );

  const { data: contact } = useContactQuery({ contactId: user?.id });

  function setDefaultData() {
    if (isNotEmpty(defaultData)) {
      changeFields(defaultData, isClient);
    } else if (!isFetchingDefaultData) {
      changeFields(
        {
          date: Date.now(),
          person: user?.fullName,
          marketplace: marketplace,
          organizationEmail: user?.email,
        },
        true,
      );
    }
  }

  function validateIf() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function cancel() {
    onClose(isChanged);
  }

  const onChangeNetworks = useCallback(
    (sharedCommunityIds) => {
      changeField("sharedCommunityIds", sharedCommunityIds);
    },
    [changeField],
  );

  const changeServices = useCallback(
    (field, value) => {
      let hasClientRelatedService = null;
      let hasBusinessRelatedService = null;

      if (isFromVendor) {
        if (value.length) {
          changeField(field, [last(value)]);
        } else {
          changeField(field, value);
        }
        hasClientRelatedService = findParentCodeIfInclude(serviceSections, fields.services.toJS());
      } else {
        let ids = [];
        if (value.length) {
          ids = isFeaturedCommunity ? value : value && [last(value)];
          changeField(field, ids);
        } else {
          changeField(field, []);
        }
        hasClientRelatedService = any(communityOrignService, (o) => ids.includes(o.id) && o?.isClientRelated);
        hasBusinessRelatedService = any(
          communityOrignService,
          (o) => ids.includes(o.id) && !o?.isClientRelated && o?.isBusinessRelated,
        );
      }

      if (isMarketplace && !hasClientRelatedService) {
        clearFields(...CLIENT_FIELDS);
      }

      if (
        !isMarketplace &&
        fields.client.id &&
        !communityOrignService[last(value)]?.isClientRelated &&
        communityOrignService[last(value)]?.isBusinessRelated
      ) {
        setPrevSelectedServiceId(first(value));
        setRemoveClientWarningDialogOpen(true);
      }

      if (isMarketplace && fields.client.id && hasClientRelatedService && hasBusinessRelatedService) {
        setClientCanNotBeSelectedDialogOpen(true);
      }
    },
    [fields, serviceSections, communityOrignService, clearFields, changeField, isMarketplace, isFeaturedCommunity],
  );

  const onChangeServicesField = useCallback(
    (field, value) => {
      if (value) {
        setServicesError(null);
      }
      let onChange = () => changeServices(field, value);

      if (networkIds.length > 0) {
        setIsConfirmSwitchServiceDialogOpen(true);
        setDeferredServiceChange(() => onChange);
      } else onChange();
    },
    [changeServices, networkIds.length],
  );

  const onConfirmSwitchService = useCallback(() => {
    deferredServicesChange();

    setNetworkIds([]);
    setDeferredServiceChange(null);
    setIsConfirmSwitchServiceDialogOpen(false);
  }, [deferredServicesChange]);

  const onCancelSwitchService = useCallback(() => {
    setDeferredServiceChange(null);
    setIsConfirmSwitchServiceDialogOpen(false);
  }, []);

  const onCancelClearClient = useCallback(() => {
    if (isMarketplace) {
      const businessService = communityOrignService.find((o) => o.isBusinessRelated && fields.services.includes(o.id));

      const filteredServices = fields.services.filter((v) => v !== businessService?.id);

      changeField("services", filteredServices);

      setClientCanNotBeSelectedDialogOpen(false);
    } else {
      changeField("services", [prevSelectedServiceId]);

      setPrevSelectedServiceId(null);
      setRemoveClientWarningDialogOpen(false);
    }
  }, [serviceSections, communityOrignService, changeField, isMarketplace, fields.services, prevSelectedServiceId]);

  const onConfirmClearClient = useCallback(() => {
    clearFields(...CLIENT_FIELDS);
    setAttachedClientDocuments([]);
    setRemoveClientWarningDialogOpen(false);
    setClientCanNotBeSelectedDialogOpen(false);
  }, [clearFields]);

  const onChangeOrganization = useCallback(
    (...args) => {
      changeField("client.id", null);
      changeFields({
        client: {
          id: null,
          communityId: null,
        },
        referringCommunityId: null,
      });
      changeField(...args);
    },
    [changeField, changeFields],
  );

  const onChangeCommunity = useCallback(
    (...args) => {
      changeField(...args);
      clearFields(...CLIENT_FIELDS);
    },
    [clearFields, changeField],
  );

  const onChangeClientField = useCallback(
    (name, value) => {
      const client = findWhere(clients, { id: value });

      if (client?.hieConsentPolicyName === HIE_CONSENT_POLICIES.OPT_OUT) {
        setShouldShowAttachmentsWarningDialog(true);
        setCcdSharedFieldVisible(false);
      } else setCcdSharedFieldVisible(true);

      setAttachedClientDocuments([]);

      changeField(name, value);

      if (!value) clearFields(...CLIENT_FIELDS);

      clearFields("attachmentFiles", "attachedClientDocumentIds", "attachedClientDocumentFiles");
    },
    [
      clients,
      clearFields,
      changeField,
      setCcdSharedFieldVisible,
      setAttachedClientDocuments,
      setShouldShowAttachmentsWarningDialog,
    ],
  );

  const onChangeAttachments = useCallback(
    (name, value) => {
      if (shouldShowAttachmentsWarningDialog) {
        setAttachmentsWarningDialogOpen(true);
      }

      changeField(name, reject(value, isAttachedClientDocumentFile));

      const files = filter(value, isAttachedClientDocumentFile);
      changeField("attachedClientDocumentFiles", files);

      const documents = map(files, (o) => findWhere(attachedClientDocuments, { id: o.id }));

      setAttachedClientDocuments(documents);
      changeField(
        "attachedClientDocumentIds",
        map(documents, (o) => o.id),
      );
    },
    [changeField, attachedClientDocuments, isAttachedClientDocumentFile, shouldShowAttachmentsWarningDialog],
  );

  const onAttachClientDocuments = useCallback(
    (documents) => {
      if (shouldShowAttachmentsWarningDialog) setAttachmentsWarningDialogOpen(true);

      setAttachedClientDocuments(documents);
      changeField(
        "attachedClientDocumentIds",
        map(documents, (o) => o.id),
      );
      changeField(
        "attachedClientDocumentFiles",
        map(documents, (o) => ({
          id: o.id,
          name: o.title,
          size: o.size,
          type: o.mimeType,
        })),
      );
    },
    [changeField, shouldShowAttachmentsWarningDialog],
  );

  const onCloseClientDocumentDirectory = useCallback(() => {
    toggleClientDocumentDirectory(false);
  }, []);

  const onCancel = useCallback(cancel, [onClose, isChanged]);

  const onScroll = useScrollToFormError(".ReferralRequestForm", scroll);

  const tryToSubmit = useCallback(
    (e) => {
      e.preventDefault();
      e.stopPropagation();
      // e.nativeEvent.stopImmediatePropagation();
      setFetching(true);

      if (isFromVendor) {
        validate(validationOptions)
          .then(async () => {
            await submit(
              getData(fields, {
                isMarketplace,
                canHaveClinicalInfo,
                canHaveCcd: isCcdSharedFieldVisible,
              }),
            );

            setNeedValidation(false);
          })
          .catch((error) => {
            onScroll();
            setNeedValidation(true);
          })
          .finally(() => {
            setFetching(false);
          });
      } else {
        validate(validationOptions)
          .then(async () => {
            await submitBuilding(
              getData(fields, {
                isMarketplace,
                canHaveClinicalInfo,
                canHaveCcd: isCcdSharedFieldVisible,
              }),
            );

            setNeedValidation(false);
          })
          .catch(() => {
            onScroll();
            setNeedValidation(true);
          })
          .finally(() => {
            setFetching(false);
          });
      }
    },
    [
      fields,
      submit,
      onScroll,
      validate,
      isMarketplace,
      validationOptions,
      canHaveClinicalInfo,
      isCcdSharedFieldVisible,
    ],
  );

  useEffect(setDefaultData, [user, isClient, defaultData, marketplace, isMarketplace, changeFields]);

  useEffect(validateIf, [validate, needValidation, validationOptions]);

  useEffect(() => {
    changeField("organizationPhone", contact?.mobilePhone);
  }, [changeField, contact]);

  const ClientField = (props) =>
    isClient ? (
      <TextField
        type="text"
        name="client.location"
        value={fields.client.fullName}
        isDisabled
        maxLength={256}
        label="Client name*"
        className="ReferralRequestForm-TextField"
      />
    ) : (
      <SelectField
        name="client.id"
        value={fields.client.id}
        hasKeyboardSearch
        hasKeyboardSearchText
        options={mappedClients}
        label={addAsterix("Client name").if(isClientSectionRequired)}
        className="ReferralRequestForm-SelectField"
        onChange={onChangeClientField}
        errorText={errors.client?.id}
        {...props}
      />
    );

  return (
    <>
      <Form className="ReferralRequestForm is-invalid" onSubmit={tryToSubmit}>
        {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}

        <Scrollable style={scrollableStyles}>
          <div className="ReferralRequestForm-Section is-invalid">
            <div className="ReferralRequestForm-SectionTitle">Request</div>
            <Row>
              <Col lg="4" md={4}>
                <SelectField
                  hasKeyboardSearch
                  hasKeyboardSearchText
                  name="referringOrganizationId"
                  value={fields.referringOrganizationId}
                  options={mappedOrganizations}
                  label="Organization*"
                  className="ReferralRequestForm-SelectField"
                  errorText={errors.referringOrganizationId}
                  isDisabled={isFetchingOrganizations || isOrganizationDisabled}
                  onChange={onChangeOrganization}
                />
              </Col>

              <Col lg="4" md={4}>
                <SelectField
                  name="referringCommunityId"
                  value={fields.referringCommunityId}
                  options={mappedCommunities}
                  label="Community*"
                  isDisabled={isFetchingCommunities}
                  className="ReferralRequestForm-SelectField"
                  errorText={errors.referringCommunityId}
                  onChange={onChangeCommunity}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="client.date"
                  value={format(fields.date, DATE_TIME_FORMAT)}
                  isDisabled
                  label="Request date*"
                  className="ReferralRequestForm-TextField"
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <SelectField
                  name="priorityId"
                  value={fields.priorityId}
                  options={mappedPriorities}
                  label="Priority*"
                  className="ReferralRequestForm-SelectField"
                  errorText={errors.priorityId}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="person"
                  value={fields.person}
                  maxLength={256}
                  label="Referring individual*"
                  className="ReferralRequestForm-TextField"
                  errorText={errors.person}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <PhoneField
                  name="organizationPhone"
                  value={fields.organizationPhone}
                  label="Phone #*"
                  className="ReferralRequestForm-PhoneField"
                  errorText={errors.organizationPhone}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="phone-hint" className="ReferralRequestForm-LabelIcon" />}
                  tooltip={{
                    target: "phone-hint",
                    render: () => (
                      <ul className="ReferralRequestForm-PhoneTooltipBody">
                        <li>Digits only allowed</li>
                        <li>No spaces, dashes, or special symbols</li>
                        <li>Country code is required</li>
                        <li>‘+’ may be a leading symbol</li>
                      </ul>
                    ),
                  }}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="organizationEmail"
                  value={fields.organizationEmail}
                  maxLength={256}
                  label="Email*"
                  className="ReferralRequestForm-TextField"
                  errorText={errors.organizationEmail}
                  onChange={changeField}
                />
              </Col>
              <Col md={8}>
                <SelectField
                  isMultiple
                  isSectioned
                  hasValueTooltip
                  hasSearchBox
                  name="services"
                  label="Service *"
                  placeholder="Select"
                  value={serviceIds}
                  hasSectionTitle
                  hasSectionSeparator
                  hasAllOption={false}
                  sections={serviceSections}
                  // hasError={!!servicesError}
                  errorText={errors.services}
                  onChange={onChangeServicesField}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <TextField
                  type="textarea"
                  name="instructions"
                  value={fields.instructions}
                  maxLength={5000}
                  numberOfRows={5}
                  label="Referral instructions*"
                  className="ReferralRequestForm-TextField"
                  placeholder="Use this space to include relevant information for the referral such as reason for referral or instructions"
                  errorText={errors.instructions}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>
          {/*   Client Section */}
          {isClientSectionVisible && !isAssociationType && (
            <div className="ReferralRequestForm-Section">
              <div className="ReferralRequestForm-SectionTitle">Client</div>

              <Row>
                <Col lg="4" md={4}>
                  <ClientField />
                </Col>

                <Col lg="4" md={isMarketplace ? 8 : 4}>
                  <TextField
                    type="text"
                    name="client.location"
                    value={fields.client.location}
                    maxLength={256}
                    label={addAsterix("Client location").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    errorText={errors.client?.location}
                    className="ReferralRequestForm-TextField"
                    onChange={changeField}
                  />
                </Col>
                <Col lg="4" md={4}>
                  <TextField
                    type="text"
                    name="client.locationPhone"
                    value={fields.client.locationPhone}
                    maxLength={16}
                    label={addAsterix("Location phone #").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    className="ReferralRequestForm-TextField"
                    errorText={errors.client?.locationPhone}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col lg="8" md={6}>
                  <TextField
                    type="text"
                    name="client.address.street"
                    value={fields.client.address.street}
                    maxLength={256}
                    label={addAsterix("Address").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    className="ReferralRequestForm-TextField"
                    errorText={errors.client?.address?.street}
                    onChange={changeField}
                  />
                </Col>
                <Col lg="4" md={6}>
                  <TextField
                    type="text"
                    name="client.address.city"
                    value={fields.client.address.city}
                    maxLength={256}
                    label={addAsterix("City").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    className="ReferralRequestForm-TextField"
                    errorText={errors.client?.address?.city}
                    onChange={changeField}
                  />
                </Col>
              </Row>
              <Row>
                <Col lg="4" md={4}>
                  <SelectField
                    name="client.address.stateId"
                    value={fields.client.address.stateId}
                    options={mappedStates}
                    label={addAsterix("State").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    className="ReferralRequestForm-SelectField"
                    errorText={errors.client?.address?.stateId}
                    onChange={changeField}
                  />
                </Col>
                <Col lg="4" md={4}>
                  <TextField
                    type="text"
                    name="client.address.zip"
                    value={fields.client.address.zip}
                    maxLength={5}
                    label={addAsterix("Zip code").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    className="ReferralRequestForm-TextField"
                    errorText={errors.client?.address?.zip}
                    onChange={changeField}
                  />
                </Col>
                <Col lg="4" md={4}>
                  <TextField
                    type="text"
                    name="client.insuranceNetworkTitle"
                    value={fields.client.insuranceNetworkTitle}
                    maxLength={256}
                    label={addAsterix("Insurer network").if(isClientSectionRequired)}
                    isDisabled={!isInteger(fields.client.id)}
                    errorText={errors.client?.insuranceNetworkTitle}
                    className="ReferralRequestForm-TextField"
                    onChange={changeField}
                  />
                </Col>
              </Row>

              <div
                id="additional-clinical-info"
                className="ReferralRequestForm-AdditionalClinicalInfo TextField form-group"
              >
                <label className="TextField-Label">Additional clinical information</label>

                <div style={{ display: "flex" }}>
                  <CheckboxField
                    name="isFacesheetShared"
                    label="Facesheet"
                    value={fields.isFacesheetShared}
                    isDisabled={!canHaveClinicalInfo}
                    className="ReferralRequestForm-CheckboxField"
                    onChange={changeField}
                  />
                  {isCcdSharedFieldVisible && (
                    <CheckboxField
                      name="isCcdShared"
                      label="CCD"
                      value={fields.isCcdShared}
                      isDisabled={!canHaveClinicalInfo}
                      className="ReferralRequestForm-CheckboxField"
                      onChange={changeField}
                    />
                  )}
                  {isServicePlanSharedFieldVisible && (
                    <CheckboxField
                      name="isServicePlanShared"
                      label="Service plan"
                      isDisabled={!canHaveClinicalInfo}
                      value={fields.isServicePlanShared}
                      className="ReferralRequestForm-CheckboxField"
                      onChange={changeField}
                    />
                  )}
                </div>
              </div>

              {!canHaveClinicalInfo && (
                <Tooltip
                  placement="top"
                  target="additional-clinical-info"
                  trigger="hover"
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
                  You can't share PHI as {selectedService?.label} selected
                </Tooltip>
              )}
            </div>
          )}

          {marketplace && (
            <ShareThroughSection
              fields={fields}
              isAssociation={isAssociationType}
              errors={errors}
              onChangeField={changeField}
            />
          )}
          {fields.client.id && isClientSectionVisible && isFromVendor && !isAssociationType && (
            <ClientCareTeamSection
              vendorId={vendorId}
              fields={fields}
              onChangeField={changeField}
              errors={errors}
              vendorCareTeams={vendorCareTeams}
              setVendorCareTeams={setVendorCareTeams}
            />
          )}
          <div className="ReferralRequestForm-Section">
            <div className="ReferralRequestForm-SectionTitle">Attachments</div>
            <Row>
              <Col>
                <DropzoneField
                  name="attachmentFiles"
                  label="Additional documents"
                  value={[...fields.attachmentFiles, ...fields.attachedClientDocumentFiles]}
                  maxCount={15}
                  browsers={
                    isInteger(fields.client.id) && [
                      {
                        type: "custom",
                        isDisabled: !isInteger(fields.client.id),
                        title: "Upload from Client Profile",
                        onSelect: () => {
                          toggleClientDocumentDirectory(true);
                        },
                      },
                      {
                        type: "default",
                        title: "Upload from your Computer",
                      },
                    ]
                  }
                  hintText="Supported file types: PDF, PNG, JPG, JPEG, GIF, TIFF, WORD | Max 20 mb"
                  className="ReferralRequestForm-DropzoneField"
                  errors={{
                    ...errors.attachmentFiles,
                    ...reduce(
                      errors.attachedClientDocumentFiles,
                      (m, v, i) => {
                        m[+i + fields.attachmentFiles.size] = v;
                        return m;
                      },
                      {},
                    ),
                  }}
                  onChange={onChangeAttachments}
                />
              </Col>
            </Row>
          </div>

          {!isFromVendor && (
            <div className="ReferralRequestForm-Section">
              <div className="ReferralRequestForm-SectionTitle">Share with</div>

              {marketplace ? (
                <Row>
                  <Col>
                    <PlainNetworkSection
                      serviceIds={serviceIds}
                      communityId={communityId}
                      communityName={marketplace.communityName}
                      organizationName={marketplace.organizationName}
                    />
                  </Col>
                </Row>
              ) : (
                <Row>
                  <Col>
                    <NetworkSection
                      errors={errors.sharedCommunityIds}
                      communityId={communityId}
                      serviceIds={serviceIds}
                      onChange={onChangeNetworks}
                    />
                  </Col>
                </Row>
              )}
            </div>
          )}
        </Scrollable>

        <div className="ReferralRequestForm-Buttons">
          <Button outline color="success" onClick={onCancel}>
            Cancel
          </Button>
          <Button color="success" disabled={!isValid || isFetching}>
            Submit
          </Button>
        </div>
      </Form>

      {isClientDocumentDirectoryOpen && (
        <DocumentDirectory
          isOpen
          clientId={fields.client.id}
          selectedDocumentMaxCount={15 - fields.attachmentFiles.size}
          selectedDocuments={attachedClientDocuments}
          onClose={onCloseClientDocumentDirectory}
          onComplete={onAttachClientDocuments}
        />
      )}

      {isConfirmSwitchServiceDialogOpen && (
        <WarningDialog
          isOpen
          title="The chosen networks will be reset if you select a new service."
          buttons={[
            {
              color: "success",
              outline: true,
              text: "Cancel",
              onClick: onCancelSwitchService,
            },
            {
              color: "success",
              text: "Confirm",
              onClick: onConfirmSwitchService,
            },
          ]}
        />
      )}

      {isAttachmentsWarningDialogOpen && (
        <WarningDialog
          isOpen
          title="The client status is currently opted out. Only upload clinically necessary information or discuss other options with Client."
          buttons={[
            {
              text: "Close",
              color: "success",
              onClick: () => {
                setShouldShowAttachmentsWarningDialog(false);
                setAttachmentsWarningDialogOpen(false);
              },
            },
          ]}
        />
      )}

      {isClientCanNotBeSelectedDialogOpen && (
        <WarningDialog
          isOpen
          title="Client can't be selected. Do you want to proceed?"
          buttons={[
            {
              color: "success",
              outline: true,
              text: "Cancel",
              onClick: onCancelClearClient,
            },
            {
              color: "success",
              text: "Proceed",
              onClick: onConfirmClearClient,
            },
          ]}
        />
      )}

      {isRemoveClientWarningDialogOpen && (
        <WarningDialog
          isOpen
          title="Client data will be removed since you have selected a business service. Please confirm."
          buttons={[
            {
              color: "success",
              outline: true,
              text: "Cancel",
              onClick: onCancelClearClient,
            },
            {
              color: "success",
              text: "Proceed",
              onClick: onConfirmClearClient,
            },
          ]}
        />
      )}

      {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
    </>
  );
}

ReferralRequestForm.propTypes = {
  communityId: PTypes.number,
  organizationId: PTypes.number,
  marketplace: PTypes.shape(),
  isFeaturedCommunity: PTypes.bool,

  onClose: PTypes.func,
  onSubmitSuccess: PTypes.func,
};

ReferralRequestForm.defaultProps = {
  marketplace: null,
  isFeaturedCommunity: false,

  onClose: noop,
  onSubmitSuccess: noop,
};

export default memo(ReferralRequestForm);
