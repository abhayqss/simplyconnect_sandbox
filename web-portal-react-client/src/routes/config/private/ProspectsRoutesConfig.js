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
  HOME_CARE_ASSISTANT,
  NAVI_GUIDE,
  ORGANIZATION_ADMIN,
  PERSON_RECEIVING_SERVICES,
  PHARMACIST_VENDOR,
  QUALITY_ASSURANCE,
  SUPER_ADMINISTRATOR,
  VENDOR,
} from "../Roles";

// const Events = lazy(() => import('containers/Events/Events'))
const Prospects = lazy(() => import("containers/Prospects/Prospects"));
const ProspectList = lazy(() => import("containers/Prospects/Prospects/Prospects"));
const ProspectDashboard = lazy(() => import("containers/Prospects/Prospects/ProspectDashboard/ProspectDashboard"));
// const ProspectCallHistory = lazy(() => import('containers/Prospects/Prospects/CallHistory/ProspectCallHistory'))

// const Rides = lazy(() => import('containers/Prospects/Prospects/Rides/Rides'))
// const CareTeam = lazy(() => import('containers/Prospects/Prospects/CareTeam/CareTeam'))
// const Documents = lazy(() => import('containers/Clients/Clients/Documents/Documents'))
// const Assessments = lazy(() => import('containers/Clients/Clients/Assessments/Assessments'))
// const ServicePlans = lazy(() => import('containers/Clients/Clients/ServicePlans/ServicePlans'))
// const Referrals = lazy(() => import('containers/Referrals/Referrals/Referrals'))
// const ReferralDetails = lazy(() => import('containers/Referrals/ReferralDetails/ReferralDetails'))

const {
  PROFESSIONALS_CARE_MANAGEMENT,
  PROFESSIONALS_OTHER,
  PHARMACY,
  NON_PROFESSIONALS,
  PROFESSIONALS_PRIMARY_PHYSICIAN,
} = GROUPS;

export default {
  component: Prospects,
  path: "/prospects",
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
    HOME_CARE_ASSISTANT,
    ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    PERSON_RECEIVING_SERVICES,
    ...PROFESSIONALS_CARE_MANAGEMENT,
    ...PROFESSIONALS_OTHER,
    ...PHARMACY,
    ...NON_PROFESSIONALS,
  ],
  children: [
    {
      component: ProspectList,
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
        HOME_CARE_ASSISTANT,
        ...PROFESSIONALS_PRIMARY_PHYSICIAN,
        PERSON_RECEIVING_SERVICES,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PROFESSIONALS_OTHER,
        ...PHARMACY,
        ...NON_PROFESSIONALS,
      ],
      exact: true,
    },
    {
      component: ProspectDashboard,
      path: "/:prospectId",
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
        PERSON_RECEIVING_SERVICES,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PHARMACY,
        ...PROFESSIONALS_OTHER,
        ...NON_PROFESSIONALS,
      ],
      exact: true,
    },
    {
      component: ProspectDashboard,
      path: "/:prospectId/dashboard",
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
        PERSON_RECEIVING_SERVICES,
        ...PROFESSIONALS_CARE_MANAGEMENT,
        ...PHARMACY,
        ...PROFESSIONALS_OTHER,
        ...NON_PROFESSIONALS,
      ],
      exact: true,
    },
    // {
    //     component: ProspectCallHistory,
    //     path: '/:prospectId/call-history',
    //     permission: [
    //         SUPER_ADMINISTRATOR,
    //         ADMINISTRATOR,
    //         COMMUNITY_ADMINISTRATOR,
    //         ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    //         PERSON_RECEIVING_SERVICES,
    //         ...PROFESSIONALS_CARE_MANAGEMENT,
    //         ...PHARMACY,
    //         ...PROFESSIONALS_OTHER,
    //         ...NON_PROFESSIONALS
    //     ]
    // },
    // {
    //     component: Events,
    //     path: '/:prospectId/events',
    //     permission: [
    //         SUPER_ADMINISTRATOR,
    //         ADMINISTRATOR,
    //         COMMUNITY_ADMINISTRATOR,
    //         ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    //         PERSON_RECEIVING_SERVICES,
    //         ...PROFESSIONALS_CARE_MANAGEMENT,
    //         ...PHARMACY,
    //         ...PROFESSIONALS_OTHER,
    //         ...NON_PROFESSIONALS,
    //     ]
    // },
    // {
    //     component: Documents,
    //     path: '/:prospectId/documents',
    //     permission: [
    //         SUPER_ADMINISTRATOR,
    //         ADMINISTRATOR,
    //         COMMUNITY_ADMINISTRATOR,
    //         ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    //         PERSON_RECEIVING_SERVICES,
    //         ...PROFESSIONALS_CARE_MANAGEMENT,
    //         ...PHARMACY,
    //         ...PROFESSIONALS_OTHER,
    //         ...NON_PROFESSIONALS,
    //     ],
    //     exact: true,
    // },
    // {
    //     component: CareTeam,
    //     path: '/:prospectId/care-team',
    //     permission: [
    //         SUPER_ADMINISTRATOR,
    //         ADMINISTRATOR,
    //         COMMUNITY_ADMINISTRATOR,
    //         HOME_CARE_ASSISTANT,
    //         ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    //         PERSON_RECEIVING_SERVICES,
    //         ...PROFESSIONALS_CARE_MANAGEMENT,
    //         ...PHARMACY,
    //         ...PROFESSIONALS_OTHER,
    //         ...NON_PROFESSIONALS,
    //     ]
    // },
    // {
    //     component: Rides,
    //     path: '/:prospectId/rides',
    //     permission: [
    //         SUPER_ADMINISTRATOR,
    //         ADMINISTRATOR,
    //         COMMUNITY_ADMINISTRATOR,
    //         PERSON_RECEIVING_SERVICES,
    //         ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    //         ...PROFESSIONALS_CARE_MANAGEMENT,
    //         ...PROFESSIONALS_OTHER,
    //         ...NON_PROFESSIONALS
    //     ]
    // }
  ],
};
