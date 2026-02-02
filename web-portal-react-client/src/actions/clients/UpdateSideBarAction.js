import React, { useEffect } from "react";

import { isEqual, compact } from "underscore";

import { CLIENT_SECTIONS, CARE_TEAM_AFFILIATION_TYPES } from "lib/Constants";

import { getSideBarItems } from "containers/Clients/SideBarItems";

import { useClientQuery } from "hooks/business/client/queries";

import { useCanViewDocumentsQuery, useClientDocumentCountQuery } from "hooks/business/client/documents";

import { useCanViewEventsAndNotesQuery, useClientEventNoteComposedCountQuery } from "hooks/business/client/events";

import { useClientAssessmentCountQuery, useCanViewClientAssessmentsQuery } from "hooks/business/client/assessments";

import { useClientServicePlanCountQuery, useCanViewClientServicePlansQuery } from "hooks/business/client/service-plans";

import { useClientExpenseCountQuery, useCanViewClientExpensesQuery } from "hooks/business/client/expences";

import { useCanViewClientCareTeamQuery, useClientCareTeamMemberCountQuery } from "hooks/business/client/care-team";

import { isInteger } from "lib/utils/Utils";

import UpdateSideBarAction from "../sidebar/UpdateSideBarAction";
import LoadCTMemberCountAction from "./LoadCTMemberCountAction";
import LoadCanViewCTMemberAction from "./LoadCanViewCTMemberAction";
import LoadCanAddAssessmentAction from "./LoadCanAddAssessmentAction";
import useClientWorkflowCountQuery from "hooks/business/client/workflows/useClientWorkflowCountQuery";

const { CALL_HISTORY } = CLIENT_SECTIONS;
const { BOTH } = CARE_TEAM_AFFILIATION_TYPES;

function ClientSideBarUpdateAction({ params: { changes, clientId, shouldRefresh } = {} }) {
  const { data: client } = useClientQuery({ clientId }, { enabled: isInteger(clientId) });

  const { canRequestRide, canViewRideHistory } = client ?? {};

  const { canViewCallHistory } = client?.associatedContact ?? {};

  const { data: canViewEventsAndNotes } = useCanViewEventsAndNotesQuery({ clientId });

  const { data: canViewDocuments } = useCanViewDocumentsQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: canViewCareTeamMembers } = useCanViewClientCareTeamQuery({ clientId }, { staleTime: 0 });

  const { data: careTeamCount } = useClientCareTeamMemberCountQuery({ clientId, affiliation: BOTH }, { staleTime: 0 });

  const { data: workflowCount } = useClientWorkflowCountQuery({ clientId }, { staleTime: 0 });

  const { data: documentCount, remove: removeDocumentCount } = useClientDocumentCountQuery(
    {
      clientId,
      includeDeleted: false,
    },
    { enabled: isInteger(clientId) },
  );

  const { data: eventNoteComposedCount, remove: removeEventNoteComposedCount } = useClientEventNoteComposedCountQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId) && canViewEventsAndNotes,
    },
  );

  const { data: canViewAssessments, remove: removeCanViewAssessments } = useCanViewClientAssessmentsQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: assessmentCount, remove: removeAssessmentCount } = useClientAssessmentCountQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: canViewServicePlans, remove: removeCanViewServicePlans } = useCanViewClientServicePlansQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: servicePlanCount, remove: removeServicePlanCount } = useClientServicePlanCountQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: canViewExpenses } = useCanViewClientExpensesQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const { data: expenseCount } = useClientExpenseCountQuery(
    { clientId },
    {
      staleTime: 0,
      enabled: isInteger(clientId),
    },
  );

  const counts = {
    expenseCount,
    careTeamCount,
    documentCount,
    assessmentCount,
    servicePlanCount,
    eventNoteComposedCount,
    workflowCount,
  };

  const permissions = {
    canRequestRide,
    canViewExpenses,
    canViewDocuments,
    canViewRideHistory,
    canViewAssessments,
    canViewCallHistory,
    canViewServicePlans,
    canViewEventsAndNotes,
    canViewCareTeamMembers,
  };

  useEffect(() => {
    if (shouldRefresh) {
      removeDocumentCount();
      removeAssessmentCount();
      removeServicePlanCount();
      removeCanViewAssessments();
      removeCanViewServicePlans();
      removeEventNoteComposedCount();
    }
  }, [
    shouldRefresh,
    removeDocumentCount,
    removeAssessmentCount,
    removeServicePlanCount,
    removeCanViewAssessments,
    removeCanViewServicePlans,
    removeEventNoteComposedCount,
  ]);

  return (
    <>
      <LoadCTMemberCountAction
        isMultiple
        params={{ clientId, shouldRefresh }}
        shouldPerform={(prevParams) =>
          canViewCareTeamMembers &&
          !careTeamCount?.isFetching &&
          (careTeamCount?.fetchCount === 0 || (shouldRefresh && !prevParams.shouldRefresh))
        }
      />
      <LoadCanViewCTMemberAction params={{ clientId }} />
      <LoadCanAddAssessmentAction params={{ clientId }} />
      <UpdateSideBarAction
        isMultiple
        shouldPerform={(prevParams) =>
          !(isEqual(prevParams.counts, counts) && isEqual(prevParams.permissions, permissions))
        }
        params={{
          counts,
          permissions,
          changes: {
            isHidden: false,
            items: getSideBarItems({
              clientId,
              ...counts,
              excluded: compact([!canViewCallHistory ? CALL_HISTORY : null]),
              permissions,
            }),
            ...changes,
          },
        }}
      />
    </>
  );
}

export default ClientSideBarUpdateAction;
