import React, { memo, useMemo, useState, useEffect, useCallback } from "react";

import { omit, noop, first, chain } from "underscore";

import { useQueryClient } from "@tanstack/react-query";

import { Form, Button } from "reactstrap";

import { Tabs, Loader, ErrorViewer } from "components";

import { WarningDialog } from "components/dialogs";

import { useForm, useScrollable, useScrollToFormError } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import {
  useCommunityQuery,
  useCommunityMutation,
  useCanConfigureQuery,
  useUniqBUCWithDomain,
  useUniqNameOidValidation,
  useCommunityPictureMutation,
  useCommunityPermissionsQuery,
} from "hooks/business/community";

import { useOrganizationQuery } from "hooks/business/admin/organization";

import { useCommunityTypesQuery, useTreatmentServicesQuery } from "hooks/business/directory/query";

import { useCareTeamIncomingInvitationsQuery } from "hooks/business/care-team";

import Community from "entities/Community";
import ReferralEmail from "entities/ReferralEmail";
import CommunityValidator from "validators/CommunityFormValidator";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import { SettingsFormFields, MarketplaceFormFields } from "./components";

import { ValidationContext, RegularValidationStrategy, ExtendedValidationStrategy } from "./strategies";

import { SYSTEM_ROLES, HIE_CONSENT_POLICIES } from "lib/Constants";

import Converter from "lib/converters/Converter";
import factory from "lib/converters/ConverterFactory";

import {
  ifElse,
  isEmpty,
  compact,
  isInteger,
  isNotEmpty,
  getDataUrl,
  allAreTrue,
  allAreNotFalse,
  omitEmptyProps,
  omitEmptyPropsDeep,
} from "lib/utils/Utils";

import "./CommunityForm.scss";

const { ADMINISTRATOR, SUPER_ADMINISTRATOR } = SYSTEM_ROLES;

const converter = factory.getConverter(Converter.types.DATA_URL_TO_FILE);

const TAB = {
  SETTINGS: 0,
  MARKETPLACE: 1,
};

const TabFormFields = {
  [TAB.SETTINGS]: SettingsFormFields,
  [TAB.MARKETPLACE]: MarketplaceFormFields,
};

const TAB_TITLE = {
  [TAB.SETTINGS]: "Settings",
  [TAB.MARKETPLACE]: "Marketplace",
};

const UNIQ_OID_ERROR_TEXT = "Entered OID already exists in the system.";
const UNIQ_NAME_ERROR_TEXT = "The community with name entered already exists.";

const scrollableStyles = { flex: 1 };

function createNonUniqBUCError(codes) {
  const TEXT = {
    CODE: `code${codes.length > 1 ? "s" : ""}`,
    EXIST: `exist${codes.length > 1 ? "" : "s"}`,
  };

  return Error(`Business unit ${TEXT.CODE} ${codes.join(", ")} already ${TEXT.EXIST}. Please enter a unique code.`);
}

const getDocuTrackData = (data, certificateLoaded) => {
  return {
    ...data,
    businessUnitCodes: data.businessUnitCodes.filter((o) => o !== ""),
    publicKeyCertificate: data.publicKeyCertificates.length ? first(data.publicKeyCertificates) : null,
    acceptedCertificateSha1Fingerprint:
      data.useSuggestedCertificate && data.serverCertificate ? data.serverCertificate.sha1Fingerprint : null,
    shouldRemoveCertificate:
      certificateLoaded && data.serverCertificate === null && data.configuredCertificate !== null,
  };
};

const getFeaturedCommunitiesData = (data, canEdit) => {
  if (!canEdit) return;

  return chain(data)
    .map((o) => {
      const result = { id: o.id, displayOrder: o.displayOrder, communityId: o.communityId };

      if (!o.confirmVisibility || o.deselected) result.displayOrder = undefined;

      return result;
    })
    .sortBy((o) => o.displayOrder)
    .value();
};

const getMarketplaceData = (data) => {
  return omit(
    {
      ...data,
      referralEmails: data.referralEmails.map((o) => o.value).filter((email) => !!email.trim()),
    },
    "allowExternalInboundReferrals",
    "featuredCommunities",
  );
};

function getData(fields, { canEditFeaturedCommunities }) {
  let data = fields.toJS();
  const isEditMode = !!data.id;

  data = {
    ...data,
    logo: fields.logo,
    shouldRemoveLogo: isEditMode && !(fields.logo || fields.logoName),
    allowExternalInboundReferrals: data.marketplace.allowExternalInboundReferrals,
    docutrackPharmacyConfig: getDocuTrackData(data.docutrackPharmacyConfig, data.certificateLoaded),
    marketplace: getMarketplaceData(data.marketplace),
    featuredServiceProviders: getFeaturedCommunitiesData(
      data.marketplace.featuredCommunities,
      canEditFeaturedCommunities && data.marketplace.confirmVisibility,
    ),
  };

  if (data.signatureConfig) {
    data.signatureConfig.isPinEnabled = Boolean(data.signatureConfig.isPinEnabled);
  }

  return data;
}

function mapPictureFiles(pictures) {
  return (files) => {
    let fileNameRegexp = /(.+?)\.[^.]+$/;

    return files.map((picture, index) => {
      let name = pictures[index].name.match(fileNameRegexp)[1];

      return {
        ...picture,
        name,
      };
    });
  };
}

const getInitialReferralEmails = ifElse(
  (emails) => !!emails?.length,
  (emails) => emails.map((email) => ReferralEmail({ value: email, canEdit: false })),
  () => [ReferralEmail()],
);

function CommunityForm({
  communityId,
  organizationId,
  defaultActiveTab = TAB.SETTINGS,

  onClose = noop,
  onSubmitSuccess = noop,
}) {
  const [tab, setTab] = useState(defaultActiveTab);
  const [isFetching, setIsFetching] = useState(false);

  const [error, setError] = useState();
  const [docutrackError, setDocutrackError] = useState(null);
  const [needValidation, setNeedValidation] = useState(false);
  const [hasUniqOidError, setHasUniqOidError] = useState(false);
  const [hasUniqNameError, setHasUniqNameError] = useState(false);

  const user = useAuthUser();
  const queryClient = useQueryClient();

  const isEditing = isInteger(communityId);

  const { data: defaultPermissions } = useCommunityPermissionsQuery({ organizationId });

  const {
    fields,
    isValid,
    validate,
    isChanged,
    clearField,
    clearFields,
    changeField,
    changeFields,
    errors: localErrors,
  } = useForm("CommunityForm", Community, CommunityValidator);

  const errors = useMemo(
    () => ({
      ...localErrors,
      ...omitEmptyProps({
        oid: hasUniqOidError ? UNIQ_OID_ERROR_TEXT : null,
        name: hasUniqNameError ? UNIQ_NAME_ERROR_TEXT : null,
      }),
    }),
    [localErrors, hasUniqOidError, hasUniqNameError],
  );

  const docuPharmConfig = fields.docutrackPharmacyConfig;

  const { data: community, isFetching: isFetchingCommunity } = useCommunityQuery(
    {
      communityId,
      organizationId,
      isMarketplaceDataIncluded: true,
    },
    {
      staleTime: 0,
      enabled: Boolean(communityId),
    },
  );

  const { data: canConfigure } = useCanConfigureQuery({ organizationId });

  const { data: organization } = useOrganizationQuery(
    {
      organizationId,
      isMarketplaceDataIncluded: true,
    },
    {
      staleTime: 0,
      enabled: Boolean(organizationId),
    },
  );

  const permissions = useMemo(() => {
    if (isEditing) {
      return {
        canEdit: community?.canEdit,
        canEditDocutrack: community?.canEditDocutrack,
        canEditFeatures: community?.features?.canEdit,
        canEditSignatureSetup: community?.signatureConfig?.canEdit,
        canEditHieConsentPolicy: community?.canEditHieConsentPolicy,
        canEditAffiliateRelationships: community?.canEditAffiliateRelationships,
        canEditFeaturedServiceProviders: community?.canEditFeaturedServiceProviders,
        canEditMarketplaceReferralEmails: community?.canEditMarketplaceReferralEmails,
        canEditConfirmMarketplaceVisibility: community?.canEditConfirmMarketplaceVisibility,
        canEditAllowExternalInboundReferrals: community?.canEditAllowExternalInboundReferrals,
      };
    }

    return defaultPermissions ?? {};
  }, [isEditing, community, defaultPermissions]);

  const { mutateAsync: validateUniq } = useUniqNameOidValidation({
    organizationId,
    ...omit(
      isEditing
        ? {
            name: fields.name,
          }
        : {
            oid: fields.oid,
            name: fields.name,
          },
      isEmpty,
    ),
  });

  const shouldValidateDocutrack = canConfigure && permissions.canEditDocutrack && docuPharmConfig.isIntegrationEnabled;

  const referralEmails = useMemo(() => fields.marketplace.referralEmails.map((o) => o.value).toJS(), [fields]);

  const shouldValidateReferralEmails = permissions.canEditMarketplaceReferralEmails;

  const validationOptions = useMemo(
    () => ({
      included: {
        step: tab,
        referralEmails,
        shouldValidateDocutrack,
        shouldValidateReferralEmails,
        useSuggestedCertificate: docuPharmConfig.useSuggestedCertificate,
      },
    }),
    [
      tab,
      referralEmails,
      shouldValidateDocutrack,
      shouldValidateReferralEmails,
      docuPharmConfig.useSuggestedCertificate,
    ],
  );

  const {
    error: bucError,
    reset: clearBUCError,
    isLoading: isValidatingBUCs,
    mutateAsync: validateBUCsWithinDomain,
  } = useUniqBUCWithDomain({
    onSuccess: ({ data: codes }) => {
      if (codes?.length) {
        throw createNonUniqBUCError(codes);
      }
    },
  });

  const validationContext = new ValidationContext({
    validate,
    validateAsync: ({ included }) => {
      const promises = [];

      if (!isEditing || community.name !== fields.name) {
        promises.push(
          validateUniq().then(({ data: { oid, name } }) => {
            setHasUniqOidError(oid === false);
            setHasUniqNameError(name === false);
            return allAreNotFalse(oid, name);
          }),
        );
      }

      let { serverDomain, businessUnitCodes } = docuPharmConfig ?? {};

      businessUnitCodes = compact(businessUnitCodes.toJS());

      if (businessUnitCodes.length) {
        promises.push(
          included.shouldValidateDocutrack
            ? validateBUCsWithinDomain({
                serverDomain,
                businessUnitCodes,
                excludeCommunityId: fields.id,
              }).then(({ data: codes }) => isEmpty(codes))
            : Promise.resolve(true),
        );
      }

      return Promise.all(promises).then((values) => {
        if (allAreTrue(...values)) return true;
        else return Promise.reject(false);
      });
    },
    included: {
      step: tab,
      referralEmails,
      shouldValidateDocutrack,
      shouldValidateReferralEmails,
      useSuggestedCertificate: docuPharmConfig.useSuggestedCertificate,
    },
  });

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".CommunityForm", scroll);
  const { mutateAsync: submit, reset: resetCommunityMutation } = useCommunityMutation({
    onError: setError,
    onSuccess: ({ data }) => {
      queryClient.invalidateQueries("Community", {
        organizationId,
        communityId: data,
        isMarketplaceDataIncluded: true,
      });
      onSubmitSuccess(data, !isEditing);
    },
  });

  const { mutateAsync: fetchPicture } = useCommunityPictureMutation();

  function cancel() {
    onClose(isChanged);
  }

  async function tryToSubmit(e) {
    e.preventDefault();

    let isValid = await doValidate(new RegularValidationStrategy());

    if (isValid) {
      setIsFetching(true);

      await submit({
        data: getData(fields, {
          canEditFeaturedCommunities: permissions.canEditFeaturedServiceProviders,
        }),
        organizationId,
      });

      setIsFetching(false);
    }
  }

  function validateIf() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  function init() {
    if (community) {
      const data = omitEmptyPropsDeep(community);

      data.marketplace = data.marketplace ?? {};
      data.marketplace.featuredCommunities = chain(data.featuredServiceProviders)
        .filter((o) => !!o.displayOrder)
        .map((o) => ({ ...o, selected: true }))
        .value();

      data.marketplace.allowExternalInboundReferrals = !!data.allowExternalInboundReferrals;

      data.marketplace.referralEmails = getInitialReferralEmails(data.marketplace.referralEmails);

      const docuConfig = data.docutrackPharmacyConfig;

      if (docuConfig.docutrackError) {
        setDocutrackError(docuConfig.docutrackError);
      }

      if (!docuConfig?.serverDomain) {
        docuConfig.serverCertificate = null;
        docuConfig.configuredCertificate = null;
      } else {
        data.certificateLoaded = true;
      }

      if (!docuConfig.businessUnitCodes?.length) {
        docuConfig.businessUnitCodes = [""];
      }

      data.docutrackPharmacyConfig = docuConfig;

      changeFields(data, true);
    }
  }

  function setDefaultData() {
    if (user && !isEditing && organization) {
      let { features, marketplace, allowExternalInboundReferrals } = organization;

      const data = {
        allowExternalInboundReferrals,
        marketplace: omitEmptyPropsDeep(omit(marketplace, "id")),
        docutrackPharmacyConfig: { businessUnitCodes: [""] },
      };

      data.marketplace.allowExternalInboundReferrals = allowExternalInboundReferrals;

      data.marketplace.referralEmails = getInitialReferralEmails(marketplace.referralEmails);

      if (!isEditing && features.isSignatureEnabled) {
        data.signatureConfig = {
          canEdit: [ADMINISTRATOR, SUPER_ADMINISTRATOR].includes(user.roleName),
          isPinEnabled: true,
        };
      }

      changeFields(data, true);
    }
  }

  function setFiles() {
    async function execute() {
      try {
        const pictures = await fetchPictures(fields.pictures);

        let files = pictures?.map((picture) =>
          converter.convert(getDataUrl(picture.data, picture.mediaType), picture.name),
        );

        if (isNotEmpty(files)) {
          changeFields({ pictureFiles: files }, true);
        }
      } catch (error) {
        console.error("Something went wrong during the pictures loading");
      }
    }

    if (fields.pictures) {
      execute();
    }
  }

  function onBack() {
    setTab((tab) => --tab);
  }

  async function doValidate(strategy) {
    let { step, isValid } = await validationContext.executeValidation(strategy);

    if (!isValid) {
      onScroll();
      setTab(step);
      setNeedValidation(true);
    } else {
      setNeedValidation(false);
    }

    return isValid;
  }

  async function onNext() {
    let isValid = await doValidate(new RegularValidationStrategy());

    isValid && setTab((tab) => ++tab);
  }

  const onChangeTab = async (value) => {
    if (value > tab) {
      let stepOffset = value - tab;
      let Strategy = RegularValidationStrategy;

      if (stepOffset > 1) {
        Strategy = ExtendedValidationStrategy;
        validationContext.setStep(value - 1);
      }

      let isValid = await doValidate(new Strategy());

      isValid && setTab(value);
    } else {
      setTab(value);
    }
  };

  const onCancel = useCallback(cancel, [onClose, isChanged]);

  const onCloseErrorViewer = useCallback(() => {
    setError(null);
    setIsFetching(false);
    resetCommunityMutation();
  }, [resetCommunityMutation]);

  const fetchPictures = useCallback(
    (pictures) => {
      return Promise.all(
        pictures.map((picture) =>
          fetchPicture({
            communityId,
            organizationId,
            pictureId: picture.id,
          }),
        ),
      ).then(mapPictureFiles(pictures));
    },
    [communityId, organizationId, fetchPicture],
  );

  useCommunityTypesQuery({ primaryFocusIds: fields.primaryFocusIds }, { enabled: !!fields.primaryFocusIds?.length });
  useTreatmentServicesQuery({ primaryFocusIds: fields.primaryFocusIds }, { enabled: !!fields.primaryFocusIds?.length });

  useEffect(init, [community, changeFields]);
  useEffect(setFiles, [fields.pictures, fetchPictures, changeFields]);
  useEffect(setDefaultData, [user, organization, changeFields, isEditing]);
  useEffect(validateIf, [needValidation, onScroll, validate, validationOptions]);

  const FormFields = TabFormFields[tab];

  return (
    <>
      <Form className="CommunityForm">
        {(isFetching || isFetchingCommunity || isValidatingBUCs) && <Loader hasBackdrop />}

        <Tabs
          containerClassName="CommunityForm-TabsContainer"
          items={[
            {
              title: TAB_TITLE[TAB.SETTINGS],
              isActive: tab === TAB.SETTINGS,
              hasError: !isValid && tab === TAB.SETTINGS,
            },
            {
              title: TAB_TITLE[TAB.MARKETPLACE],
              isActive: tab === TAB.MARKETPLACE,
              hasError: !isValid && tab === TAB.MARKETPLACE,
              isDisabled:
                fields.docutrackPharmacyConfig.isIntegrationEnabled &&
                fields.docutrackPharmacyConfig.serverDomain &&
                !fields.certificateLoaded,
            },
          ]}
          onChange={onChangeTab}
        />

        <Scrollable style={scrollableStyles}>
          <FormFields
            fields={fields}
            errors={errors}
            permissions={permissions}
            communityId={communityId}
            organizationId={organizationId}
            onError={setError}
            onClearField={clearField}
            onClearFields={clearFields}
            onChangeField={changeField}
            onChangeFields={changeFields}
          />
        </Scrollable>

        <div className="CommunityForm-Buttons">
          {tab === TAB.SETTINGS && (
            <Button outline color="success" disabled={isFetching} onClick={onCancel}>
              Close
            </Button>
          )}

          {tab === TAB.MARKETPLACE && (
            <Button outline color="success" disabled={isFetching} onClick={onBack}>
              Back
            </Button>
          )}

          {tab === TAB.SETTINGS && (
            <Button
              color="success"
              disabled={
                isFetching ||
                (fields.docutrackPharmacyConfig.isIntegrationEnabled &&
                  fields.docutrackPharmacyConfig.serverDomain &&
                  !fields.certificateLoaded)
              }
              onClick={onNext}
            >
              Select
            </Button>
          )}

          {tab === TAB.MARKETPLACE && (
            <Button color="success" disabled={isFetching} onClick={tryToSubmit}>
              {isEditing ? "Save" : "Create"}
            </Button>
          )}
        </div>
      </Form>

      {error && <ErrorViewer isOpen error={error} onClose={onCloseErrorViewer} />}

      {bucError && <ErrorViewer isOpen error={bucError} onClose={clearBUCError} />}

      {docutrackError && <ErrorViewer isOpen error={docutrackError} onClose={() => setDocutrackError(null)} />}
    </>
  );
}

export default memo(CommunityForm);
