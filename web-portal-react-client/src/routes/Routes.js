import React, { useMemo } from "react";

import { useAuthUser, useIsRelevantHost, useQueryParams } from "hooks/common";

import {
  useExternalProviderRoleCheck,
  useIsAssociation,
  useIsClientCheck,
  useIsQACheck,
  userIsVendorConcierge,
} from "hooks/business/external";

import config from "config";

import { primaryOrganizationStore } from "lib/stores";

import { path } from "lib/utils/ContextUtils";
import { getAllowedRoutes } from "lib/utils/UrlUtils";

import MapAllowedRoutes from "./MapAllowedRoutes";

import PublicRoutesConfig from "./config/public";
import PrivateRoutesConfig from "./config/private";
import PublicMarketplaceRoutesConfig from "./config/public/PublicMarketplaceRoutesConfig";
import useIsVendorAdmin from "../hooks/business/external/useIsVendorAdmin";

function PublicRoutes() {
  const isLoggedIn = !!useAuthUser();
  const isExtProviderRole = useExternalProviderRoleCheck();
  const isQa = useIsQACheck();
  const isClient = useIsClientCheck();
  const isAssociation = useIsAssociation();
  const isVendorAdmin = useIsVendorAdmin();
  const isVendorConcierge = userIsVendorConcierge();

  const isRelevantHost = useIsRelevantHost();

  const params = useQueryParams();
  const { organizationCode } = params ?? {};

  const primaryOrganization = primaryOrganizationStore.get();

  let basePath = "";
  if (isRelevantHost) basePath = config.context;

  let redirectPath = "";

  if (isRelevantHost) {
    if (isClient) {
      redirectPath = path("/cl/workflow");
    } else if (isAssociation) {
      redirectPath = path("/marketplace");
    } else if (isQa) {
      redirectPath = path("/qa/events/qa");
    } else if (isExtProviderRole) {
      redirectPath = path(isLoggedIn ? "/admin-events" : "/admin-events");
    } else if (isVendorAdmin) {
      redirectPath = path("/admin/vendors");
    } else if (isVendorConcierge) {
      redirectPath = path("/admin/organizations");
    } else if (isLoggedIn) {
      redirectPath = path("/admin-events");
    } else if (!(organizationCode ?? primaryOrganization)) {
      redirectPath = path("/admin-events");
    }
  }

  const configuration = useMemo(
    () =>
      isRelevantHost
        ? [...PublicRoutesConfig, ...PrivateRoutesConfig, { redirect: { to: redirectPath } }]
        : [PublicMarketplaceRoutesConfig, { redirect: { to: redirectPath } }],
    [redirectPath, isRelevantHost],
  );
  return <MapAllowedRoutes routes={getAllowedRoutes(configuration)} basePath={basePath} />;
}

export default PublicRoutes;
