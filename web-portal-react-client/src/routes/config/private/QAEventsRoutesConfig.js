import { lazy } from "react";

import { GROUPS, QUALITY_ASSURANCE } from "../Roles";

const QAFeedback = lazy(() => import("containers/Events/workflow"));
const QAWorkflowEvents = lazy(() => import("containers/Events/QAWorkflow/QAWorkflow"));
const WorkflowEvents = lazy(() => import("containers/Events/WorkflowEvent/WorkflowEvent"));
const WorkflowFeedback = lazy(() => import("containers/Events/WorkflowEvent/WorkflowFeedback/WorkflowFeedback"));
const {
  PROFESSIONALS_CARE_MANAGEMENT,
  PROFESSIONALS_OTHER,
  PHARMACY,
  NON_PROFESSIONALS,
  PROFESSIONALS_PRIMARY_PHYSICIAN,
} = GROUPS;
/*  qa role event*/
export default {
  component: QAFeedback,
  path: "/qa",
  permission: [QUALITY_ASSURANCE],
  children: [
    {
      component: QAWorkflowEvents,
      path: "/events/:from",
      permission: [QUALITY_ASSURANCE],
      exact: true,
    },
    {
      component: WorkflowEvents,
      path: "/workflow-events",
      permission: [QUALITY_ASSURANCE],
      exact: true,
    },
    {
      component: WorkflowFeedback,
      path: "/feedback/:eventId",
      permission: [QUALITY_ASSURANCE],
      exact: true,
    },
  ],
};
