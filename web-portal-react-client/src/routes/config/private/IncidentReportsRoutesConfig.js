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
  QUALITY_ASSURANCE,
  SUPER_ADMINISTRATOR,
  VENDOR,
} from "../Roles";

const IncidentReports = lazy(() => import("containers/IncidentReports/IncidentReports"));
const IncidentReportList = lazy(() => import("containers/IncidentReports/IncidentReports/IncidentReports"));
const IncidentReportDetails = lazy(
  () => import("containers/IncidentReports/IncidentReportDetails/IncidentReportDetails"),
);

const { PROFESSIONALS_CARE_MANAGEMENT, PROFESSIONALS_OTHER, PROFESSIONALS_PRIMARY_PHYSICIAN } = GROUPS;

export default {
  component: IncidentReports,
  path: "/incident-reports",
  permission: [
    SUPER_ADMINISTRATOR,
    ORGANIZATION_ADMIN,
    NAVI_GUIDE,
    BEHAVIORAL_HEALTH,
    VENDOR,
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
      component: IncidentReportList,
      path: "/",
      permission: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        NAVI_GUIDE,
        BEHAVIORAL_HEALTH,
        VENDOR,
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
      component: IncidentReportDetails,
      path: "/:reportId",
      permission: [
        SUPER_ADMINISTRATOR,
        ORGANIZATION_ADMIN,
        NAVI_GUIDE,
        BEHAVIORAL_HEALTH,
        VENDOR,
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
