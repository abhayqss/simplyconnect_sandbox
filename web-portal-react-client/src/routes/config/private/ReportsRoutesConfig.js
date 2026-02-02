import { lazy } from "react";

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
  ORGANIZATION_ADMIN,
  PHARMACIST_VENDOR,
  SUPER_ADMINISTRATOR,
  VENDOR,
} from "../Roles";

const Reports = lazy(() => import("containers/Reports/Reports"));
const RegularReports = lazy(() => import("containers/Reports/Reports/Reports"));

const { PROFESSIONALS_CARE_MANAGEMENT } = GROUPS;

export default {
  component: Reports,
  path: "/reports",
  permission: [
    SUPER_ADMINISTRATOR,
    ORGANIZATION_ADMIN,
    BEHAVIORAL_HEALTH,
    VENDOR,
    DOCTOR,
    CAREGIVER,
    PREMIUM,
    CLINICIAN,
    PHARMACIST_VENDOR,
    NAVI_GUIDE,
    ADMINISTRATOR,
    COMMUNITY_ADMINISTRATOR,
    ...PROFESSIONALS_CARE_MANAGEMENT,
  ],
  children: [
    {
      component: RegularReports,
      path: "/",
      permission: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        BEHAVIORAL_HEALTH,
        VENDOR,
        DOCTOR,
        CAREGIVER,
        PREMIUM,
        CLINICIAN,
        PHARMACIST_VENDOR,
        NAVI_GUIDE,
        ADMINISTRATOR,
        COMMUNITY_ADMINISTRATOR,
        ...PROFESSIONALS_CARE_MANAGEMENT,
      ],
    },
  ],
};
