import { lazy } from "react";

import ReferralList from "containers/Referrals/Referrals/Referrals";

import {
  ADMINISTRATOR,
  BEHAVIORAL_HEALTH,
  CLINICIAN,
  COMMUNITY_ADMINISTRATOR,
  DOCTOR,
  CAREGIVER,
  PREMIUM,
  GROUPS,
  NAVI_GUIDE,
  ASSOCIATION,
  NON_CLINICAL_STAFF,
  ORGANIZATION_ADMIN,
  PHARMACIST_VENDOR,
  QUALITY_ASSURANCE,
  SUPER_ADMINISTRATOR,
  VENDOR,
} from "../Roles";

const Referrals = lazy(() => import("containers/Referrals/Referrals"));
const ReferralDetails = lazy(() => import("containers/Referrals/ReferralDetails/ReferralDetails"));

const { PROFESSIONALS_CARE_MANAGEMENT, PROFESSIONALS_OTHER, PROFESSIONALS_PRIMARY_PHYSICIAN } = GROUPS;

export default {
  component: Referrals,
  path: "/outbound-referrals",
  permission: [
    SUPER_ADMINISTRATOR,
    ORGANIZATION_ADMIN,
    NAVI_GUIDE,
    BEHAVIORAL_HEALTH,
    VENDOR,
    ASSOCIATION,
    NON_CLINICAL_STAFF,
    DOCTOR,
    CAREGIVER,
    PREMIUM,
    CLINICIAN,
    PHARMACIST_VENDOR,
    QUALITY_ASSURANCE,
    ADMINISTRATOR,
    COMMUNITY_ADMINISTRATOR,
    ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    ...PROFESSIONALS_CARE_MANAGEMENT,
    ...PROFESSIONALS_OTHER,
  ],
  children: [
    {
      component: ReferralList,
      path: "/",
      permission: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        NAVI_GUIDE,
        BEHAVIORAL_HEALTH,
        VENDOR,
        ASSOCIATION,
        NON_CLINICAL_STAFF,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        QUALITY_ASSURANCE,
        ADMINISTRATOR,
        COMMUNITY_ADMINISTRATOR,
        ...PROFESSIONALS_PRIMARY_PHYSICIAN,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PROFESSIONALS_OTHER,
      ],
      exact: true,
    },
    {
      component: ReferralDetails,
      path: "/:referralId",
      permission: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        NAVI_GUIDE,
        BEHAVIORAL_HEALTH,
        VENDOR,
        ASSOCIATION,
        NON_CLINICAL_STAFF,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        QUALITY_ASSURANCE,
        ADMINISTRATOR,
        COMMUNITY_ADMINISTRATOR,
        ...PROFESSIONALS_PRIMARY_PHYSICIAN,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PROFESSIONALS_OTHER,
      ],
    },
  ],
};
