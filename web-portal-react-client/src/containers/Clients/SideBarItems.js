import React from "react";

import { filter } from "underscore";

import { interpolate } from "lib/utils/Utils";
import { CLIENT_SECTIONS } from "lib/Constants";

import { ReactComponent as Rides } from "images/rides.svg";
import { ReactComponent as Events } from "images/client-events.svg";
import { ReactComponent as Details } from "images/client-details.svg";
import { ReactComponent as CareTeam } from "images/care-team.svg";
import { ReactComponent as Assessments } from "images/assessments.svg";
import { ReactComponent as Documents } from "images/document-list.svg";
import { ReactComponent as ServicePlans } from "images/service-plans.svg";
import { ReactComponent as CallHistory } from "images/history.svg";
import { ReactComponent as Expenses } from "images/expenses.svg";

const {
  DASHBOARD,
  ASSESSMENTS,
  SERVICE_PLANS,
  CARE_TEAM_MEMBER,
  EVENTS_AND_NOTES,
  CALL_HISTORY,
  RIDES,
  EXPENSES,
  WORKFLOW,
} = CLIENT_SECTIONS;

const NO_PERMISSIONS_ERROR_TEXT = `You don't have permissions to see the $0`;

export function getSideBarItems(params) {
  const {
    clientId,

    expenseCount,
    documentCount,
    careTeamCount,
    assessmentCount,
    servicePlanCount,
    eventNoteComposedCount,
    workflowCount,

    excluded = [],
    permissions: {
      canRequestRide,
      canViewExpenses,
      canViewDocuments,
      canViewCallHistory,
      canViewRideHistory,
      canViewAssessments,
      canViewServicePlans,
      canViewEventsAndNotes,
      canViewWorkflow,
      canViewCareTeamMembers,
    },
  } = params;

  const canViewRides = canRequestRide || canViewRideHistory;

  const path = `/clients/${clientId}`;

  return filter(
    [
      {
        title: "Dashboard",
        href: path,
        isExact: true,
        name: DASHBOARD,
        hintText: "Dashboard",
        renderIcon: (className) => <Details className={className} />,
      },
      {
        title: "Care Team",
        name: CARE_TEAM_MEMBER,
        extraText: careTeamCount,
        href: `${path}/care-team`,
        isDisabled: !canViewCareTeamMembers,
        hintText: !canViewCareTeamMembers
          ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client care team")
          : "Care Team Listing",
        renderIcon: (className) => <CareTeam className={className} />,
      },
      {
        title: "Events & Notes",
        name: EVENTS_AND_NOTES,
        extraText: eventNoteComposedCount,
        href: `${path}/events`,
        isDisabled: !canViewEventsAndNotes,
        hintText: !canViewEventsAndNotes
          ? "You don't have permissions to Client's Events&Notes"
          : "Event and Note Listing",
        renderIcon: (className) => <Events className={className} />,
      },
      {
        title: "Workflows",
        name: WORKFLOW,
        extraText: workflowCount,
        href: `${path}/workflow`,
        isDisabled: !canViewEventsAndNotes,
        hintText: !canViewEventsAndNotes
          ? "You don't have permissions to Client's Client-Workflow"
          : "Client-Workflow Listing",
        renderIcon: (className) => <Assessments className={className} />,
      },
      /*{
        title: 'CCD',
        href: `${path}/ccd-details`,
        hintText: 'CCD',
        renderIcon: (className) => <CCDDetails className={className} />
    },*/
      {
        title: "Documents",
        extraText: documentCount,
        href: `${path}/documents`,
        isDisabled: !canViewDocuments,
        hintText: !canViewDocuments ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client documents") : "Document Listing",
        renderIcon: (className) => <Documents className={className} />,
      },
      {
        title: "Assessments",
        name: ASSESSMENTS,
        extraText: assessmentCount,
        isDisabled: !canViewAssessments,
        href: `${path}/assessments`,
        hintText: !canViewAssessments
          ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client assessments")
          : "Assessment Listing",
        renderIcon: (className) => <Assessments className={className} />,
      },
      {
        title: "Service Plans",
        name: SERVICE_PLANS,
        extraText: servicePlanCount,
        href: `${path}/service-plans`,
        isDisabled: !canViewServicePlans,
        hintText: !canViewServicePlans
          ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client service plans")
          : "Service Plan Listing",
        renderIcon: (className) => <ServicePlans className={className} />,
      },
      {
        title: "Expenses",
        name: EXPENSES,
        extraText: expenseCount,
        href: `${path}/expenses`,
        isDisabled: !canViewExpenses,
        hintText: !canViewExpenses ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client expenses") : "Expense Listing",
        renderIcon: (className) => <Expenses className={className} />,
      },
      {
        title: "Rides",
        name: RIDES,
        href: `${path}/rides`,
        isDisabled: !canViewRides,
        hintText: !canViewRides ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "client rides") : "Rides Listing",
        renderIcon: (className) => <Rides className={className} />,
      },
      /*{
        title: 'Outbound Referrals',
        extraText: 12,
        hintText: 'Outbound Referrals',
        href: `${path}/outbound-referrals`,
        renderIcon: (className) => <Outbound className={className} />
    }*/
      {
        title: "Call History",
        name: CALL_HISTORY,
        href: `${path}/call-history`,
        isDisabled: !canViewCallHistory,
        hintText: !canViewCallHistory ? interpolate(NO_PERMISSIONS_ERROR_TEXT, "call history") : "Call History",
        renderIcon: (className) => <CallHistory className={className} />,
      },
    ],
    (o) => !excluded.includes(o.name),
  );
}
