import { lazy } from "react";

import { CLIENT_POA, GROUPS, PERSON_RECEIVING_SERVICES } from "../Roles";

// const WorkflowEvents = lazy(() => import('containers/Events/WorkflowEvent/WorkflowEvent'))
const ClientWorkflow = lazy(() => import("containers/Events/workflow"));
const WorkflowEvents = lazy(() => import("containers/Events/WorkflowClient/WorkflowClient"));
const WorkflowDetail = lazy(() => import("containers/Events/WorkflowClientDetail/WorkflowClientDetail"));

const {
  PROFESSIONALS_CARE_MANAGEMENT,
  PROFESSIONALS_OTHER,
  PHARMACY,
  NON_PROFESSIONALS,
  PROFESSIONALS_PRIMARY_PHYSICIAN,
} = GROUPS;

export default {
  component: ClientWorkflow,
  path: "/cl",
  permission: [
    // ...PROFESSIONALS_PRIMARY_PHYSICIAN,
    PERSON_RECEIVING_SERVICES,
    CLIENT_POA,
    // ...PROFESSIONALS_CARE_MANAGEMENT,
    // ...PROFESSIONALS_OTHER,
    // ...PHARMACY,
    // ...NON_PROFESSIONALS,
  ],
  children: [
    {
      component: WorkflowEvents,
      path: "/workflow",
      permission: [PERSON_RECEIVING_SERVICES, CLIENT_POA],
      exact: true,
    },
    {
      component: WorkflowDetail,
      path: "/workflow/:workflowId/:fillClientId/:canEdit",
      permission: [PERSON_RECEIVING_SERVICES, CLIENT_POA],
      exact: true,
    },
  ],
};
