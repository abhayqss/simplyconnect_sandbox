import React, { memo, useCallback, useMemo, useState } from "react";

import $ from "jquery";

import PTypes from "prop-types";

import cn from "classnames";

import { contains, filter, noop } from "underscore";

import { useHistory } from "react-router-dom";

import { UncontrolledTooltip as Tooltip } from "reactstrap";

import { useAuthUser, useToggle } from "hooks/common";

import { useConversations } from "hooks/business/conversations";

import { Picture, Table } from "components";

import { IconButton } from "components/buttons";

import ContactViewer from "containers/Admin/Contacts/ContactViewer/ContactViewer";

import Avatar from "containers/Avatar/Avatar";

import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Delete } from "images/delete.svg";
import { ReactComponent as Pencil } from "images/pencil.svg";
import { ReactComponent as Chat } from "images/chat-bubble.svg";
import { ReactComponent as Video } from "images/videocall.svg";

import "./CareTeamMemberList.scss";

function CareTeamMemberList({
  pagination,
  careManagerId,
  isFetching,
  hasActions,
  hasOptedOutIndication,
  data,
  columns,
  columnsMobile,

  onSort,
  onEdit,
  onDelete,
  onRefresh,
  setPage,
}) {
  const [selected, setSelected] = useState(null);

  const [isViewerOpen, toggleViewer] = useToggle();

  const user = useAuthUser();
  const history = useHistory();

  const { emit } = useConversations();

  const onCloseViewer = useCallback(() => {
    setSelected(null);
    toggleViewer();
  }, [toggleViewer]);

  const onView = (member) => {
    setSelected(member);
    toggleViewer();
  };

  const onVideo = useCallback(
    (member) => {
      emit("attemptCall", {
        companionAvatarId: member.avatarId,
        conversationSid: member?.conversationSid,
        employeeIds: [user.id, member.employeeId],
      });
    },
    [user, emit],
  );

  const onChat = (member) => {
    history.push(path("/chats"), {
      employeeIds: [user.id, member.employeeId],
      conversationSid: member.conversationSid,
    });
  };

  function canCall(row) {
    return user?.areVideoCallsEnabled && row.isActive && row.employeeId !== user.id && row.roleName !== "HCA";
  }

  function canChat(row) {
    return user?.areConversationsEnabled && row.isActive && row.employeeId !== user.id && row.roleName !== "HCA";
  }

  const columnList = useMemo(
    () => [
      {
        dataField: "contactName",
        text: "Member",
        sort: true,
        onSort,
        formatter: (v, row, index, formatExtraData, isMobile) => {
          return (
            <div className="d-flex align-items-center">
              <Avatar name={v} id={row.avatarId} className="CareTeamMemberList-MemberAvatar" />

              <div className="CareTeamMemberList-Member margin-left-10">
                <div
                  id={`${isMobile ? "m-" : ""}contact-${row.id}`}
                  className={cn("CareTeamMemberList-MemberName", {
                    "CareTeamMemberList-MemberName_disabled": !row.canViewContact,
                  })}
                  onClick={() => onView(row)}
                >
                  {row.contactName}
                </div>
                {row.canViewContact && (
                  <Tooltip
                    placement="top"
                    modifiers={[
                      {
                        name: "offset",
                        options: { offset: [0, 6] },
                      },
                    ]}
                    target={`${isMobile ? "m-" : ""}contact-${row.id}`}
                  >
                    View contact details
                  </Tooltip>
                )}
                {hasOptedOutIndication && row.isOnHold && !row.canViewClient && (
                  <Tooltip
                    modifiers={[
                      {
                        name: "offset",
                        options: { offset: [0, 6] },
                      },
                    ]}
                    target={() => $(`#${isMobile ? "m-" : ""}contact-${row.id}`).closest("tr")[0]}
                    placement="top"
                  >
                    Client opted out, this member has no access to client record
                  </Tooltip>
                )}
                {hasOptedOutIndication && !row.canViewClient && (
                  <Tooltip
                    target={() => $(`#${isMobile ? "m-" : ""}contact-${row.id}`).closest("tr")[0]}
                    placement="top"
                    className="CareTeamMember-HintOnHold"
                    modifiers={[
                      {
                        name: "offset",
                        options: { offset: [0, 6] },
                      },
                    ]}
                  >
                    <p>Client opted out, member has no access to the Client record through Client care team.</p>
                    <p>
                      Note: member can still have access to the Client record through community care team association or
                      Admin permissions.
                    </p>
                  </Tooltip>
                )}
                {row.isContactMarkedForDeletion && !row.isActive && (
                  <Tooltip
                    placement="top"
                    target={`${isMobile ? "m-" : ""}contact-${row.id}`}
                    modifiers={[
                      {
                        name: "offset",
                        options: { offset: [0, 6] },
                      },
                    ]}
                  >
                    The user has deleted his/her account.
                  </Tooltip>
                )}
                <div className="CareTeamMemberList-MemberRelation">{row.roleName}</div>
              </div>
            </div>
          );
        },
      },
      {
        dataField: "careTeamManager",
        text: "Team Role",
        headerStyle: {
          width: "240px",
        },
        formatter: (v, row) => (
          <div className="d-flex flex-row align-items-center">
            <div>{v ? "Group Manager" : "Member"}</div>
          </div>
        ),
      },
      {
        dataField: "organizationName",
        text: "Organization",
        sort: true,
        onSort,
        formatter: (v, row) => (
          <div className="d-flex flex-row align-items-center">
            <Picture
              path={`/organizations/${row.organizationId}/logo`}
              className="CareTeamMemberList-OrganizationLogo margin-right-10"
            />
            <div>{v}</div>
          </div>
        ),
      },
      {
        dataField: "communityName",
        text: "Community",
        sort: true,
        onSort,
        formatter: (v, row) => (
          <div className="d-flex flex-row align-items-center">
            <Picture
              path={`/organizations/${row.organizationId}/communities/${row.employeeCommunityId}/logo`}
              className="CareTeamMemberList-CommunityLogo margin-right-10"
            />
            <div>{v}</div>
          </div>
        ),
      },
      {
        dataField: "description",
        text: "Description",
      },
      {
        dataField: "contacts",
        text: "Contacts",
        formatter: (v, row) => {
          return (
            <>
              <div className="CareTeamMemberList-Label">{row.phone}</div>
              <div className="CareTeamMemberList-Label">{row.email}</div>
            </>
          );
        },
      },
      {
        dataField: "@actions",
        text: "",
        headerStyle: {
          width: "200px",
        },
        formatter: (v, row) => {
          return (
            <div className="h-flexbox justify-content-end">
              {hasActions && canCall(row) && !(hasOptedOutIndication && row.isOnHold) && (
                <div
                  className={cn("margin-right-10", "CareTeamMember-Action", {
                    "CareTeamMember-Action_disabled": !row.isVideoCallAllowed,
                  })}
                  id={`member_${row.id}__video_action_btn`}
                >
                  <IconButton
                    size={36}
                    Icon={Video}
                    name={`member_${row.id}__video_action`}
                    tooltip={
                      row.isVideoCallAllowed
                        ? "Call with team member"
                        : "You don't have access to clients or contacts to start a call with"
                    }
                    onClick={() => row.isVideoCallAllowed && onVideo(row)}
                    className="VideoActionBtn"
                  />
                </div>
              )}
              {hasActions && canChat(row) && !(hasOptedOutIndication && row.isOnHold) && (
                <div
                  className={cn("margin-right-10", "CareTeamMember-Action", {
                    "CareTeamMember-Action_disabled": !row.isConversationAllowed,
                  })}
                  id={`member_${row.id}__chat_action_btn`}
                >
                  <IconButton
                    size={36}
                    Icon={Chat}
                    name={`member_${row.id}__chat_action`}
                    tooltip={
                      row.isConversationAllowed
                        ? "Chat with team member"
                        : `You don't have access to clients or contacts to start a conversation with`
                    }
                    onClick={() => row.isConversationAllowed && onChat(row)}
                    className="ChatActionBtn"
                  />
                </div>
              )}
              {hasActions && row.canEdit && !(hasOptedOutIndication && row.isOnHold) && (
                <IconButton
                  size={36}
                  Icon={Pencil}
                  name={`member_${row.id}__edit_action`}
                  tooltip="Change notification preferences"
                  onClick={() => onEdit(row)}
                  className="CareTeamMember-Action EditActionBtn"
                />
              )}

              {hasActions && row.canDelete && !row.careTeamManager && (
                <IconButton
                  size={36}
                  Icon={Delete}
                  name={`member_${row.id}__delete_action`}
                  tooltip="Delete community care team member"
                  onClick={() => onDelete(row)}
                  className="CareTeamMember-Action DeleteActionBtn"
                />
              )}
            </div>
          );
        },
      },
    ],
    [onSort, onView, onChat, onEdit, canCall, canChat, onVideo, onDelete, hasActions, hasOptedOutIndication],
  );

  const visibleColumns = useMemo(() => {
    return filter(columnList, ({ dataField }) => contains(columns, dataField));
  }, [columns, columnList]);

  return (
    <>
      <Table
        hasHover
        hasOptions
        hasPagination
        keyField="id"
        isLoading={isFetching}
        className="CareTeamMemberList"
        containerClass="CareTeamMemberListContainer"
        data={data}
        noDataText="No care team members"
        pagination={pagination}
        getRowStyle={(row) => ({
          opacity: row.isActive ? 1 : 0.5,
        })}
        columns={visibleColumns}
        columnsMobile={columnsMobile}
        onRefresh={(num) => setPage(num)}
        renderCaption={() => null}
      />

      {isViewerOpen && <ContactViewer isOpen contactId={selected && selected.employeeId} onClose={onCloseViewer} />}
    </>
  );
}

CareTeamMemberList.propTypes = {
  pagination: PTypes.shape({
    page: PTypes.number,
    size: PTypes.number,
    totalCount: PTypes.number,
  }),

  isFetching: PTypes.bool,
  hasActions: PTypes.bool,
  hasOptedOutIndication: PTypes.bool,

  data: PTypes.array,
  columns: PTypes.arrayOf(PTypes.string),
  columnsMobile: PTypes.arrayOf(PTypes.string),

  onSort: PTypes.func,
  onEdit: PTypes.func,
  onDelete: PTypes.func,
  onRefresh: PTypes.func,
};

CareTeamMemberList.defaultProps = {
  hasActions: true,
  hasOptedOutIndication: false,

  onSort: noop,
  onEdit: noop,
  onDelete: noop,
  onRefresh: noop,
};

export default memo(CareTeamMemberList);
