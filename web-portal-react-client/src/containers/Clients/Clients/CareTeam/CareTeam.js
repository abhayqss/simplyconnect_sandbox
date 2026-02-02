import React, { memo, useEffect, useState } from "react";

import { compact } from "underscore";

import { useParams } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Breadcrumbs } from "components";

import { Dialog, ErrorDialog } from "components/dialogs";

import { useAuthUser, useLocationState } from "hooks/common";

import { useSideBarUpdate } from "hooks/business/client";

import { useClientQuery } from "hooks/business/client/queries";

import { useCanViewClientCareTeamQuery } from "hooks/business/client/care-team";

import { pushIf } from "lib/utils/ArrayUtils";

import { CARE_TEAM_AFFILIATION_TYPES, SYSTEM_ROLES } from "lib/Constants";

import CareTeamMemberList from "./CareTeamMemberList/CareTeamMemberList";

import "./CareTeam.scss";

const { REGULAR, AFFILIATED } = CARE_TEAM_AFFILIATION_TYPES;

const { HOME_CARE_ASSISTANT } = SYSTEM_ROLES;

const SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT = [HOME_CARE_ASSISTANT];

function CareTeam() {
  const { clientId } = useParams();

  const user = useAuthUser();

  const { data: client } = useClientQuery({ clientId }, { staleTime: 0 });

  const { data: canView, isFetching: isFetchingCanView } = useCanViewClientCareTeamQuery(
    { clientId },
    { staleTime: 0 },
  );

  const updateSideBar = useSideBarUpdate({ clientId });

  useEffect(() => {
    updateSideBar();
  }, [updateSideBar]);

  const [{ isCommunityStaffContactNeed, isAddingMembersInstructionNeed } = {}, clearLocationState] = useLocationState();
  const [isCommunityStaffContactDialogOpen, toggleCommunityStaffContactDialog] = useState(isCommunityStaffContactNeed);
  const [isAddingMembersInstructionDialogOpen, toggleAddingMembersInstructionDialog] =
    useState(isAddingMembersInstructionNeed);
  const [isNoPermissionDialogOpen, toggleNoPermissionDialog] = useState(false);

  useEffect(() => {
    if (!isFetchingCanView && !canView && !isNoPermissionDialogOpen) toggleNoPermissionDialog(true);
  }, [canView, isFetchingCanView]);

  const canViewClient = Boolean(user && !SYS_ROLES_WITH_NOT_VIEWABLE_CLIENT.includes(user.roleName));
  const changTopCardRefresh = (data) => {
    setListDeleteAndRefreshTopCard(data);
  };

  const [refreshComponents, setRefreshComponents] = useState(false);

  const handleRefresh = () => {
    setRefreshComponents((prevRefresh) => !prevRefresh);
  };
  return (
    <DocumentTitle title="Simply Connect | Clients | Client Care Team">
      <div className="CareTeam">
        <Breadcrumbs
          className="CareTeam-Breadcrumbs"
          items={compact([
            { title: "Clients", href: "/clients", isEnabled: true },
            client && {
              title: client?.fullName ?? "",
              href: `/clients/${clientId}`,
              isActive: !canViewClient,
            },
            {
              title: "Care Team",
              href: `/clients/${clientId}`,
              isActive: true,
            },
          ])}
        />
        <div className="CareTeamMemberList-Title">
          <span className="CareTeamMemberList-TitleText">
            {pushIf(
              ["Care Team"],
              <span className="CareTeamMemberList-ClientName">{` / ${client?.fullName}`}</span>,
              !!client,
            )}
          </span>
        </div>

        <CareTeamMemberList
          type={REGULAR}
          showTopCard={true}
          showGroupChat={true}
          showTitle={false}
          title={pushIf(
            ["Care Team"],
            <span className="CareTeamMemberList-ClientName">{` / ${client?.fullName}`}</span>,
            !!client,
          )}
          isFetching={isFetchingCanView}
          client={client}
          clientFullName={client?.fullName}
          clientTwilioConversationSid={client?.twilioConversationSid}
          clientNonTwilioConversationSid={client?.nonclinicalConversationSid}
          clientId={clientId}
          careManagerId={client?.careTeamManager}
          hasActions={client?.isActive}
          refreshComponents={refreshComponents}
          onComponentsRefresh={handleRefresh}
        />
        {isCommunityStaffContactDialogOpen && (
          <Dialog
            isOpen
            buttons={[
              {
                text: "Close",
                color: "success",
                onClick: () => {
                  clearLocationState();
                  toggleCommunityStaffContactDialog(false);
                },
              },
            ]}
          >
            Please reach out to Community Staff for help in Care Team set up.
          </Dialog>
        )}

        {isAddingMembersInstructionDialogOpen && (
          <Dialog
            isOpen
            title="Let's set up your care team!"
            buttons={[
              {
                text: "Close",
                color: "success",
                onClick: () => {
                  clearLocationState();
                  toggleAddingMembersInstructionDialog(false);
                },
              },
            ]}
          >
            <p>You can share your health information and communicate with your care team securely.</p>
            <p>
              Once a new member is added to your care team, s(he) will receive a notification that s(he) can access your
              record.
            </p>
            <p>Click Add Member button located on the top right corner and select a person you want to add.</p>
            <p>
              You can change default responsibility and notification method(s) your care team member will be notified
              about events related to changes in your health.
            </p>
          </Dialog>
        )}

        {isNoPermissionDialogOpen && (
          <ErrorDialog
            isOpen
            title="You don't have permissions to see the client care team"
            buttons={[
              {
                text: "Close",
                onClick: () => toggleNoPermissionDialog(false),
              },
            ]}
          />
        )}
      </div>
    </DocumentTitle>
  );
}

export default memo(CareTeam);
