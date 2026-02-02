import { useCallback, useEffect, useMemo, useState } from "react";

import { compact } from "underscore";

import { useSelector } from "react-redux";

import useSideBarUpdate from "hooks/common/redux/useSideBarUpdate";

import {
  useAssessmentCountQuery,
  useCanAddAssessmentQuery,
  useCanViewServicePlanQuery,
  useEventNoteComposedCountQuery,
  useServicePlanCountQuery,
} from "hooks/business/client";

import { useClientQuery } from "hooks/business/client/queries";

import { useCanViewDocumentsQuery, useClientDocumentCountQuery } from "hooks/business/client/documents";

import { useCanViewEventsAndNotesQuery } from "hooks/business/client/events";

import { useCanViewClientAssessmentsQuery } from "hooks/business/client/assessments";

import { useCanViewClientExpensesQuery, useClientExpenseCountQuery } from "hooks/business/client/expences";

import { useCanViewClientCareTeamQuery, useClientCareTeamMemberCountQuery } from "hooks/business/client/care-team";

import useClientWorkflowCountQuery from "hooks/business/client/workflows/useClientWorkflowCountQuery";

import { CARE_TEAM_AFFILIATION_TYPES, CLIENT_SECTIONS } from "lib/Constants";

import { getSideBarItems } from "containers/Clients/SideBarItems";

const { CALL_HISTORY } = CLIENT_SECTIONS;
const { BOTH } = CARE_TEAM_AFFILIATION_TYPES;

export default function useClientSideBarUpdate({ clientId }) {
  if (!clientId) {
    return () => {};
  }

  const {
    canViewCallHistory,
    canViewServicePlans,

    assessmentCount,

    servicePlanCount,
    eventNoteComposedCount,
  } = useSelector((state) => {
    const { details, assessment, servicePlan } = state.client;
    return {
      canViewServicePlans: servicePlan.can.view.value,
      canViewCallHistory: details.data?.associatedContact?.canViewCallHistory,

      assessmentCount: assessment.count.value,
      servicePlanCount: servicePlan.count.value,
      eventNoteComposedCount: state.event.note.composed.count.value,
    };
  });

  const [changes, setChanges] = useState({});
  const [shouldUpdate, setShouldUpdate] = useState(false);

  const params = useMemo(() => ({ clientId }), [clientId]);
  const options = useMemo(() => ({ shouldRetry: shouldUpdate }), [shouldUpdate]);

  const { data: client } = useClientQuery({ clientId });

  const { data: canViewEventsAndNotes } = useCanViewEventsAndNotesQuery({ clientId });

  const { data: canViewDocuments } = useCanViewDocumentsQuery({ clientId });

  const { canRequestRide, canViewRideHistory } = client ?? {};

  useCanAddAssessmentQuery(params, options);
  useCanViewServicePlanQuery(params, options);

  const { data: canViewAssessments } = useCanViewClientAssessmentsQuery({ clientId }, { staleTime: 0 });

  const { data: canViewExpenses } = useCanViewClientExpensesQuery({ clientId }, { staleTime: 0 });

  const CTMCountParams = useMemo(() => ({ clientId, affiliation: BOTH }), [clientId]);

  const { data: workflowCount } = useClientWorkflowCountQuery({ clientId }, { staleTime: 0 });

  const { data: documentCount } = useClientDocumentCountQuery({
    clientId,
    includeDeleted: false,
  });

  const { data: canViewCareTeamMembers } = useCanViewClientCareTeamQuery({ clientId }, { staleTime: 0 });

  const { data: careTeamCount } = useClientCareTeamMemberCountQuery(CTMCountParams, { staleTime: 0 });

  useAssessmentCountQuery(params, options);
  useServicePlanCountQuery(params, options);
  useEventNoteComposedCountQuery(params, options);

  const { data: expenseCount = 0 } = useClientExpenseCountQuery({ clientId }, { staleTime: 0 });

  const counts = useMemo(
    () => ({
      expenseCount,
      workflowCount,
      documentCount,
      careTeamCount,
      assessmentCount,
      servicePlanCount,
      eventNoteComposedCount,
    }),
    [
      expenseCount,
      documentCount,
      careTeamCount,
      assessmentCount,
      servicePlanCount,
      eventNoteComposedCount,
      workflowCount,
    ],
  );

  const permissions = useMemo(
    () => ({
      canRequestRide,
      canViewExpenses,
      canViewDocuments,
      canViewCallHistory,
      canViewRideHistory,
      canViewAssessments,
      canViewServicePlans,
      canViewEventsAndNotes,
      canViewCareTeamMembers,
    }),
    [
      canRequestRide,
      canViewExpenses,
      canViewDocuments,
      canViewCallHistory,
      canViewRideHistory,
      canViewAssessments,
      canViewServicePlans,
      canViewEventsAndNotes,
      canViewCareTeamMembers,
    ],
  );

  const excluded = useMemo(() => compact([!canViewCallHistory ? CALL_HISTORY : null]), [canViewCallHistory]);

  const update = useSideBarUpdate();

  useEffect(() => {
    setShouldUpdate(false);
  }, [shouldUpdate]);

  useEffect(() => {
    update({
      isHidden: false,
      items: getSideBarItems({
        clientId,
        excluded,
        ...counts,
        permissions,
      }),
      ...changes,
    });
  }, [update, counts, changes, clientId, excluded, permissions]);

  return useCallback((changes = {}) => {
    setChanges(changes);
    setShouldUpdate(true);
  }, []);
}
