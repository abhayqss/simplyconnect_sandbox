import { lazy } from "react";

import { path } from "lib/utils/ContextUtils";

import {
  BEHAVIORAL_HEALTH,
  CLINICIAN,
  DOCTOR,
  CAREGIVER,
  PREMIUM,
  EXTERNAL_PROVIDER,
  NAVI_GUIDE,
  ORGANIZATION_ADMIN,
  PHARMACIST_VENDOR,
  QUALITY_ASSURANCE,
  SUPER_ADMINISTRATOR,
  VENDOR,
  ASSOCIATION,
  NON_CLINICAL_STAFF,
} from "../Roles";

const ExternalProvider = lazy(() => import("containers/External/ExternalProvider"));

const Referrals = lazy(() => import("containers/Referrals/Referrals/Referrals"));
const InquiryDetails = lazy(() => import("containers/Referrals/InquiryDetails/InquiryDetails"));
const ReferralDetails = lazy(() => import("containers/Referrals/ReferralDetails/ReferralDetails"));

export default {
  component: ExternalProvider,
  path: "/external-provider",
  permission: [
    EXTERNAL_PROVIDER,
    SUPER_ADMINISTRATOR,
    NAVI_GUIDE,
    BEHAVIORAL_HEALTH,
    ORGANIZATION_ADMIN,
    VENDOR,
    ASSOCIATION,
    NON_CLINICAL_STAFF,
    DOCTOR,
    CAREGIVER,
    PREMIUM,
    CLINICIAN,
    PHARMACIST_VENDOR,
    QUALITY_ASSURANCE,
  ],
  children: [
    {
      component: Referrals,
      path: "/inbound-referrals",
      permission: [
        EXTERNAL_PROVIDER,
        SUPER_ADMINISTRATOR,
        NAVI_GUIDE,
        BEHAVIORAL_HEALTH,
        ORGANIZATION_ADMIN,
        VENDOR,
        ASSOCIATION,
        NON_CLINICAL_STAFF,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        QUALITY_ASSURANCE,
      ],
      exact: true,
    },
    {
      component: ReferralDetails,
      path: "/inbound-referrals/:referralId/requests/:requestId",
      permission: [EXTERNAL_PROVIDER, ASSOCIATION, NON_CLINICAL_STAFF, SUPER_ADMINISTRATOR],
    },
    {
      component: InquiryDetails,
      path: "/inbound-referrals/inquiries/:inquiryId",
      permission: [EXTERNAL_PROVIDER, ASSOCIATION, NON_CLINICAL_STAFF, SUPER_ADMINISTRATOR],
    },
    {
      exact: true,
      redirect: {
        from: path("/external-provider"),
        to: path("/external-provider/inbound-referrals"),
      },
    },
  ],
};
