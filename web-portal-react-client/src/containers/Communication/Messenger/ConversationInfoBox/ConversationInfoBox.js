import React, { useMemo, useState, useEffect } from "react";

import cn from "classnames";

import { first, sortBy } from "underscore";

import { Button } from "reactstrap";

import Highlighter from "react-highlight-words";

import { useSelector } from "react-redux";

import { useAuthUser } from "hooks/common";
import { useBoundActions } from "hooks/common/redux";
import { useParticipants, useConversations } from "hooks/business/conversations";

import { Loader } from "components";

import { Avatar } from "components/communication";

import { MessageTypeView } from "components/communication/messenger";

import { conversationsActions as actions } from "redux/index";

import { isNotEmpty, getInitials, DateUtils as DU } from "lib/utils/Utils";

import "./ConversationInfoBox.scss";

const { format, formats } = DU;

const TIME_FORMAT = formats.time;
const DATE_FORMAT = formats.americanMediumDate;

const formatDate = (date) => format(date, DU.isToday(date) ? TIME_FORMAT : DATE_FORMAT);

const selectUsers = (state) => state.conversations.users.data;
const selectLastMessages = (state) => state.conversations.lastMessages;
const selectIsOnCallCall = (state) => state.videoChat.isOnCall;
const selectIsOutgoingCallCall = (state) => state.videoChat.isOutgoingCall;
const selectOnlineUserIdentities = (state) => state.conversations.onlineUserIdentities;
const selectLiveConversationSids = (state) => state.conversations.liveConversationSids;

export default function ConversationInfoBox({
  onClick,
  isLoading,
  isSelected,
  className,
  conversation,
  isHighlighted,
  highlightedText,
}) {
  const [unreadMessageCount, setUnreadMessageCount] = useState();

  const user = useAuthUser();

  const users = useSelector(selectUsers);

  const lastMessages = useSelector(selectLastMessages);

  const onlineUserIdentities = useSelector(selectOnlineUserIdentities);

  const isOnCall = useSelector(selectIsOnCallCall);
  const isOutgoingCall = useSelector(selectIsOutgoingCallCall);

  const liveConversationSids = useSelector(selectLiveConversationSids);

  const setLastMessage = useBoundActions(actions.setLastMessage);

  const { emit } = useConversations();

  const { getMessages, getUnreadMessageCount } = useConversations();

  const lastMessage = useMemo(() => lastMessages.get(conversation.sid), [conversation, lastMessages]);

  const participants = sortBy(useParticipants(conversation), "firstName");

  const currentParticipant = useMemo(() => users.find((o) => o.employeeId === user.id), [user.id, users]);

  const otherParticipants = participants.filter((o) => o !== currentParticipant);

  const isGroup = otherParticipants.length > 1;
  const isFetching = isLoading || !participants.length;

  const participant = !isGroup ? first(otherParticipants) : null;
  const isOnline = onlineUserIdentities.includes(participant?.identity);

  const friendlyName =
    conversation.friendlyName || otherParticipants.map((o) => `${o.firstName} ${o.lastName}`).join(", ");

  const lastMessageDate = lastMessage?.dateCreated || lastMessage?.dateUpdated;

  const isHavingPendingCall =
    isGroup && !isOnCall && !isOutgoingCall && liveConversationSids.includes(conversation.sid);

  const joinCall = () => {
    emit("joinCall", { conversationSid: conversation.sid });
  };

  useEffect(() => {
    getMessages(conversation, { pageSize: 1 }).then((messages) => {
      if (isNotEmpty(messages)) {
        setLastMessage(first(messages));
      }
    });
  }, [getMessages, conversation, setLastMessage]);

  useEffect(() => {
    if (lastMessage && currentParticipant && lastMessage.author !== currentParticipant.identity) {
      getUnreadMessageCount(conversation).then(setUnreadMessageCount);
    }
  }, [lastMessage, conversation, currentParticipant, getUnreadMessageCount]);

  return (
    <div
      onClick={onClick}
      className={cn(
        "ConversationInfoBox",
        isSelected && "ConversationInfoBox_selected",
        isHighlighted && "ConversationInfoBox_highlighted",
        className,
      )}
      data-testid={conversation.sid}
    >
      {isFetching && <Loader />}

      {!isFetching && (
        <>
          <div className="position-relative margin-right-10">
            <Avatar
              withStatus={!isGroup}
              isOnline={isOnline}
              name={friendlyName}
              id={participant?.avatarId}
              className="ConversationInfoBox-Avatar"
            >
              {getInitials({ fullName: friendlyName })}
            </Avatar>
          </div>

          <div className="ConversationInfoBox-Content">
            <div className="ConversationInfoBox-Header">
              <div className="ConversationInfoBox-ParticipantNames">
                {highlightedText ? (
                  <Highlighter
                    textToHighlight={friendlyName}
                    searchWords={[highlightedText]}
                    highlightClassName="ConversationInfoBox-HighlightedText"
                  />
                ) : (
                  friendlyName
                )}
              </div>

              <div className="ConversationInfoBox-LastMessageDate">{formatDate(lastMessageDate)}</div>
            </div>

            <div className="ConversationInfoBox-Body">
              <MessageTypeView message={lastMessage} highlightedText={highlightedText} />

              <div className="ConversationInfoBox-UnreadMessageCount margin-left-12">
                {!isHavingPendingCall && unreadMessageCount > 0 && (
                  <div className="ConversationInfoBox-UnreadMessageCountBadge">{unreadMessageCount}</div>
                )}

                {isHavingPendingCall && (
                  <Button color="success" className="ConversationInfoBox-Action JoinCallBtn" onClick={joinCall}>
                    Join call
                  </Button>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
