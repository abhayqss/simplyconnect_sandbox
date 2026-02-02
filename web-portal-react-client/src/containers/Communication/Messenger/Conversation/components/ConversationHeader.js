import React, { memo, useMemo, useState, useCallback } from "react";

import { useSelector } from "react-redux";

import { map, first, sortBy } from "underscore";

import cn from "classnames";

import { useBoundActions } from "hooks/common/redux";

import { useConversations, useCanStartCallQuery } from "hooks/business/conversations";

import { IconButton, ErrorViewer, OutsideClickListener } from "components";

import { Avatar } from "components/communication";
import { ConfirmDialog, ErrorDialog } from "components/dialogs";

import { CTMemberCommunicationParticipantPicker } from "containers/IncidentReports";

import ManagementPopup from "./ManagementPopup";
import ManagementModal from "./ManagementModal";

import { ReactComponent as Video } from "images/videocall-1.svg";
import { ReactComponent as Phone } from "images/phone-call-1.svg";
import { ReactComponent as AddUser } from "images/add-user-1.svg";
import { ReactComponent as BackArrow } from "images/backarrow.svg";

import { useAuthUser } from "hooks/common";

import { useParticipants, useCurrentParticipant } from "hooks/business/conversations";

import { ifElse, isEmpty, isInteger, getInitials } from "lib/utils/Utils";

import actions from "redux/conversations/conversationsActions";

import GroupConversationParticipantPicker from "../../GroupConversationParticipantPicker/GroupConversationParticipantPicker";

const selectCurrentCall = (state) => state.videoChat.currentCall;
const selectOnlineUserIdentities = (state) => state.conversations.onlineUserIdentities;

function ConversationHeader({ conversation, onClose }) {
  const authUser = useAuthUser();

  const [error, setError] = useState(null);
  const [selectedParticipant, setSelectedParticipant] = useState(null);

  const [isManagementPopupOpen, toggleManagementPopup] = useState(false);
  const [isParticipantPickerOpen, setIsParticipantPickerOpen] = useState(false);

  const [isLeavingConfirmDialogOpen, toggleLeavingConfirmDialog] = useState(false);

  const [isDeleteParticipantConfirmDialogOpen, toggleDeleteParticipantConfirmDialog] = useState(false);

  const [doParticipantsExceedLimitDialogOpen, setDoParticipantsExceedLimitDialogOpen] = useState(false);

  const { irClientId, incidentReportId, irClientCommunityId, participatingClientId } = conversation?.attributes ?? {};

  const currentCall = useSelector(selectCurrentCall);
  const onlineUserIdentities = useSelector(selectOnlineUserIdentities);

  const { emit, leaveBySid, addParticipants, deleteParticipants } = useConversations();

  const { data: canStartCall } = useCanStartCallQuery(
    { conversationSid: conversation?.sid },
    { enabled: Boolean(conversation?.sid) },
  );

  const currentParticipant = useCurrentParticipant(conversation);
  const isCurrentParticipantOwner = currentParticipant?.isConversationOwner;

  const participants = useParticipants(conversation);

  const participantIds = useMemo(() => map(participants, (o) => o.employeeId), [participants]);

  const sortedParticipants = useMemo(() => sortBy(participants, "firstName"), [participants]);

  const otherParticipants = useMemo(
    () => sortedParticipants.filter((o) => o && o.employeeId !== authUser.id),
    [authUser, sortedParticipants],
  );

  const friendlyName =
    conversation?.friendlyName || otherParticipants.map((o) => `${o.firstName} ${o.lastName}`).join(", ");

  const isGroup = otherParticipants.length > 1;
  const isHavingCall = !!currentCall;

  const fetchUsers = useBoundActions(actions.loadUsers);

  const closeManagementPopup = () => toggleManagementPopup(false);

  const onAddParticipant = () => setIsParticipantPickerOpen(true);

  const onCloseParticipantPicker = useCallback(() => {
    setIsParticipantPickerOpen(false);
  }, []);

  const onCompleteParticipantPicker = useCallback(
    async (value) => {
      emit("conversationLoading", conversation.sid);

      const { clientId, contactIds, groupName } = value;

      try {
        let { data: sid } = await addParticipants({
          friendlyName: groupName,
          addedEmployeeIds: contactIds,
          participatingClientId: clientId,
          conversationSid: conversation.sid,
        });

        await fetchUsers({ conversationSids: [conversation.sid] });

        emit("conversationLoading", null);
        !isGroup && emit("conversationTurnedIntoGroup", sid);
      } catch (e) {
        setError(e);
        emit("conversationLoading", null);
      }
    },
    [emit, isGroup, conversation, fetchUsers, addParticipants],
  );

  const onDeleteParticipantIntent = useCallback((o) => {
    setSelectedParticipant(o);
    toggleManagementPopup(false);
    toggleDeleteParticipantConfirmDialog(true);
  }, []);

  const closeDeleteParticipantConfirmDialog = useCallback(() => {
    toggleDeleteParticipantConfirmDialog(false);
  }, []);

  const onDeleteParticipant = useCallback(async () => {
    const { employeeId } = selectedParticipant;

    emit("conversationLoading", conversation.sid);

    deleteParticipants(conversation.sid, [employeeId])
      .catch(setError)
      .finally(() => {
        setSelectedParticipant(null);
        closeDeleteParticipantConfirmDialog();

        emit("conversationLoading", null);
      });
  }, [emit, conversation, deleteParticipants, selectedParticipant, closeDeleteParticipantConfirmDialog]);

  const onLeaveIntent = useCallback(() => {
    toggleManagementPopup(false);
    toggleLeavingConfirmDialog(true);
  }, []);

  const closeLeavingConfirmDialog = useCallback(() => {
    toggleLeavingConfirmDialog(false);
  }, []);

  const closeParticipantsExceedLimitDialog = () => setDoParticipantsExceedLimitDialogOpen(false);

  const onLeave = useCallback(() => {
    emit("conversationLoading", conversation.sid);

    leaveBySid(conversation.sid)
      .catch(setError)
      .finally(() => {
        closeLeavingConfirmDialog();
        emit("conversationLoading", null);
      });
  }, [emit, leaveBySid, conversation, closeLeavingConfirmDialog]);

  const getCallData = ({ isVideoCall } = {}) => ({
    isVideoCall,
    conversationSid: conversation.sid,
    conversationFriendlyName: conversation.friendlyName || friendlyName,
    companionAvatarId: !isGroup ? first(otherParticipants)?.avatarId : null,
    incidentReport: incidentReportId
      ? {
          id: incidentReportId,
          clientId: irClientId,
          communityId: irClientCommunityId,
        }
      : null,
  });

  const onVideo = ifElse(
    () => participants.length > 20,
    () => setDoParticipantsExceedLimitDialogOpen(true),
    () => emit("attemptCall", getCallData({ isVideoCall: true })),
  );

  const onPhone = ifElse(
    () => participants.length > 20,
    () => setDoParticipantsExceedLimitDialogOpen(true),
    () => emit("attemptCall", getCallData({ isVideoCall: false })),
  );

  if (isEmpty(participants)) return null;

  let isMobileView = window.innerWidth <= 1024;

  const isDisconnected = conversation?.attributes?.disconnected;

  return (
    <>
      <div className="Conversation-Header">
        <div onClick={onClose} className="Conversation-BackArrow">
          <BackArrow />
        </div>

        <Avatar
          name={friendlyName}
          className="Conversation-Avatar"
          id={!isGroup ? first(otherParticipants)?.avatarId : null}
        >
          {getInitials({ fullName: friendlyName })}
        </Avatar>

        <div className="Conversation-Participants">
          <div className="Conversation-ParticipantNames">{friendlyName}</div>

          {participants.length === 2 && (
            <div className="Conversation-Status">
              {onlineUserIdentities.includes(first(otherParticipants)?.identity) ? "Online" : "Offline"}
            </div>
          )}

          {participants.length > 2 && (
            <OutsideClickListener className="d-inline-block" onClick={closeManagementPopup}>
              <div
                id="ConversationParticipantCount"
                className="Conversation-ParticipantCount"
                onClick={() => toggleManagementPopup(true)}
              >
                {participants.length} participant{String(participants.length).endsWith("1") ? "" : "s"}
              </div>

              {isManagementPopupOpen && (
                <>
                  {isMobileView && (
                    <ManagementModal
                      participants={sortedParticipants}
                      canLeave={!isCurrentParticipantOwner}
                      canDeleteParticipants={isCurrentParticipantOwner}
                      onLeave={onLeaveIntent}
                      onClose={() => toggleManagementPopup(false)}
                      className="Conversation-ManagementModal"
                      onDeleteParticipant={onDeleteParticipantIntent}
                      isDisconnected={isDisconnected}
                    />
                  )}

                  {!isMobileView && (
                    <ManagementPopup
                      participants={sortedParticipants}
                      canLeave={!isCurrentParticipantOwner}
                      canDeleteParticipants={isCurrentParticipantOwner}
                      onLeave={onLeaveIntent}
                      onDeleteParticipant={onDeleteParticipantIntent}
                      isDisconnected={isDisconnected}
                    />
                  )}
                </>
              )}
            </OutsideClickListener>
          )}
        </div>

        <div className="Conversation-Actions">
          {canStartCall && (
            <IconButton
              size={24}
              Icon={Video}
              shouldHighLight={false}
              name="conversation_video-call"
              tipText={isDisconnected ? "Access to client data is not available per client request" : "Video Call"}
              onClick={() => !isDisconnected && onVideo()}
              tipTrigger="hover"
              className={cn("Conversation-Action VideoActionBtn", { "Conversation-Action_disabled": isHavingCall })}
              disabled={isDisconnected}
            />
          )}

          {canStartCall && (
            <IconButton
              size={24}
              Icon={Phone}
              shouldHighLight={false}
              name="conversation_chat-call"
              tipText={isDisconnected ? "Access to client data is not available per client request" : "Audio Call"}
              onClick={() => !isDisconnected && onPhone()}
              tipTrigger="hover"
              className={cn("Conversation-Action PhoneActionBtn", { "Conversation-Action_disabled": isHavingCall })}
              disabled={isDisconnected}
            />
          )}

          {(currentParticipant?.isConversationOwner || !isGroup) && (
            <IconButton
              size={24}
              Icon={AddUser}
              shouldHighLight={false}
              tipTrigger="hover"
              name="conversation_add-participant"
              tipText={
                isDisconnected ? "Access to client data is not available per client request" : "Add a Participant"
              }
              onClick={() => !isDisconnected && onAddParticipant()}
              className={cn("Conversation-Action AddParticipantActionBtn", {
                "Conversation-Action_disabled": isHavingCall,
              })}
              disabled={isDisconnected}
            />
          )}
        </div>
      </div>

      {!incidentReportId && (
        <GroupConversationParticipantPicker
          isOpen={isParticipantPickerOpen}
          excludedContactIds={participantIds}
          areClientsExcluded={isInteger(participatingClientId)}
          groupName={conversation.friendlyName}
          onClose={onCloseParticipantPicker}
          onComplete={onCompleteParticipantPicker}
        />
      )}

      {isParticipantPickerOpen && isInteger(incidentReportId) && (
        <CTMemberCommunicationParticipantPicker
          clientId={irClientId}
          communicationType="conversation"
          communityId={irClientCommunityId}
          isOpen={isParticipantPickerOpen}
          excludedContactIds={participantIds}
          groupName={conversation.friendlyName}
          onClose={onCloseParticipantPicker}
          onComplete={onCompleteParticipantPicker}
        />
      )}

      {isDeleteParticipantConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          title="If you remove the user, the chat, including chat history, will be deleted for this user."
          confirmBtnText="Remove"
          onCancel={closeDeleteParticipantConfirmDialog}
          onConfirm={onDeleteParticipant}
        />
      )}

      {isLeavingConfirmDialogOpen && (
        <ConfirmDialog
          isOpen
          title="If you leave the chat, you will lose access to this group chat and history."
          confirmBtnText="Leave Chat"
          onCancel={closeLeavingConfirmDialog}
          onConfirm={onLeave}
        />
      )}

      {doParticipantsExceedLimitDialogOpen && (
        <ErrorDialog
          isOpen
          title="You can add up to 20 participants"
          buttons={[
            {
              text: "Close",
              onClick: closeParticipantsExceedLimitDialog,
            },
          ]}
        />
      )}

      {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
    </>
  );
}

export default memo(ConversationHeader);
