import React from "react";

import { UncontrolledTooltip as Tooltip } from "reactstrap";

import cn from "classnames";

import { Avatar } from "components/communication";

import { useAuthUser } from "hooks/common";

import { getInitials } from "lib/utils/Utils";

import { ReactComponent as Leave } from "images/leave.svg";
import { ReactComponent as Cross } from "images/cross-2.svg";
// 群聊时群员状态展示
import { useSelector } from "react-redux";
const selectOnlineUserIdentities = (state) => state.conversations.onlineUserIdentities;
function ManagementPopup({
  participants,
  onLeave,
  canLeave = true,
  isDisconnected,
  onDeleteParticipant,
  canDeleteParticipants,
}) {
  const authUser = useAuthUser();
  const onlineUserIdentities = useSelector(selectOnlineUserIdentities);

  return (
    <div className="ConversationManagementPopup">
      <div className="ConversationManagement">
        <div className="ConversationManagement-Participants">
          {participants.map((o, i) => (
            <div
              key={o.identity}
              className={cn("ConversationManagement-Participant", {
                "ConversationManagement-Participant_disabled": !o.isActive,
              })}
            >
              <Avatar
                id={o.avatarId}
                withStatus
                isOnline={onlineUserIdentities.includes(o?.identity)}
                className="ConversationManagement-ParticipantAvatar margin-right-10"
              >
                {getInitials(o)}
              </Avatar>
              <div className="flex-1 position-relative">
                <div className="padding-right-30 text-nowrap">
                  {o.firstName} {o.lastName}
                </div>
                {canDeleteParticipants && authUser.id !== o?.employeeId && (
                  <>
                    <Cross
                      id={`remove-participant-${o.identity}`}
                      onClick={() => !isDisconnected && onDeleteParticipant(o)}
                      disabled={isDisconnected}
                      className={cn("ConversationManagement-DeleteParticipantAction", {
                        "ConversationManagement-DeleteParticipantAction__disconnected": isDisconnected,
                      })}
                    />
                    {isDisconnected && (
                      <Tooltip
                        target={`remove-participant-${o.identity}`}
                        modifiers={[
                          {
                            name: "offset",
                            options: { offset: [0, 6] },
                          },
                          {
                            name: "preventOverflow",
                            options: { boundary: document.body },
                          },
                        ]}
                      >
                        Access to client data is not available per client request
                      </Tooltip>
                    )}
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
        {canLeave && (
          <div
            id="leave-chat"
            onClick={() => !isDisconnected && onLeave()}
            className={cn("ConversationManagement-LeaveAction", {
              "ConversationManagement-LeaveAction__disconnected": isDisconnected,
            })}
          >
            <Leave className="margin-left-15 margin-right-10" /> Leave chat
            {isDisconnected && (
              <Tooltip
                target="leave-chat"
                modifiers={[
                  {
                    name: "offset",
                    options: { offset: [0, 6] },
                  },
                  {
                    name: "preventOverflow",
                    options: { boundary: document.body },
                  },
                ]}
              >
                Access to client data is not available per client request
              </Tooltip>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default ManagementPopup;
