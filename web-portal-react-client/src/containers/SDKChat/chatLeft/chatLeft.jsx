import { OutsideClickListener, SearchField } from "../../../components";
import React, { useCallback, useState } from "react";
import "../sdkChat.scss";
import { useChatManagerContext } from "../context/ChatManagerContext";
import { any, compact, values } from "underscore";
import { useParticipatingAccessibilityQuery } from "../../../hooks/business/conversations";
import { UserPen } from "lucide-react";
import { ActionPicker, ModalActionPicker } from "../../../components/communication/messenger";
import ConversationParticipantPicker from "../../Communication/Messenger/ConversationParticipantPicker/ConversationParticipantPicker";
import { useAuthUser } from "../../../hooks/common";

const ChatLeft = ({ toggleMultipleParticipantPicker }) => {
  const authUser = useAuthUser();
  const { channels, currentChannelId, searchText, setSearchText, switchChannel, createChannelAndSwitch, chatUserId } =
    useChatManagerContext();
  const [isActionPickerOpen, toggleActionPicker] = useState(false);
  const [isParticipantPickerOpen, toggleParticipantPicker] = useState(false);
  const [isModalActionPickerOpen, toggleModalActionPickerOpen] = useState(false);

  const { data: oneToOneAccessibility = {}, isFetching: isFetchingOneToOneAccessibility } =
    useParticipatingAccessibilityQuery({
      excludeOneToOneParticipants: true,
    });

  const { data: groupAccessibility = {}, isFetching: isFetchingGroupAccessibility } =
    useParticipatingAccessibilityQuery();

  const canCreateOneToOne = any(values(oneToOneAccessibility), (v) => v);

  const canCreateGroup = any(values(groupAccessibility), (v) => v);

  const canCreateAny = canCreateOneToOne || canCreateGroup;

  const onClickNewChat = () => {
    closeActionPicker();
    closeModalActionPicker();
    toggleParticipantPicker(true);
  };

  const onClickNewGroupChat = () => {
    closeActionPicker();
    closeModalActionPicker();
    toggleMultipleParticipantPicker(true);
  };

  const actionOptions = compact([
    canCreateOneToOne && { title: "New chat", onClick: onClickNewChat },
    canCreateGroup && { title: "New group chat", onClick: onClickNewGroupChat },
  ]);

  const onActionBtnClick = () => {
    if (canCreateAny) {
      toggleActionPicker(true);
    }
  };

  const onChangeSearchText = (_, text) => {
    setSearchText(text);
  };

  const onClearSearchText = () => {
    setSearchText("");
  };

  const closeActionPicker = useCallback(() => {
    toggleActionPicker(false);
  }, []);

  const closeModalActionPicker = useCallback(() => {
    toggleModalActionPickerOpen(false);
  }, []);

  const onCloseParticipantPicker = useCallback(() => {
    toggleParticipantPicker(false);
  }, []);

  // 单聊创建
  const onCompleteParticipantPicker = (participant) => {
    const targetChatUserId = participant.chatUserId;

    if (!targetChatUserId) return;

    const singleChatChannel = channels.find(
      (channel) =>
        channel.channel_type === 0 &&
        Array.isArray(channel.channel_member) &&
        channel.channel_member.some((member) => member.user_id === targetChatUserId),
    );

    if (singleChatChannel) {
      switchChannel(singleChatChannel.channel_id);
    } else {
      const params = {
        channel_type: 0,
        creator: chatUserId,
        displayName: "",
        receivers: [chatUserId, participant.chatUserId].filter((id) => id !== ""),
      };

      createChannelAndSwitch(params);
    }
  };

  return (
    <div className="sdkChatLeft">
      <div className="chatLeftHeader">
        <div className="chatLeftHeaderTitle">Chats</div>
        <div className="chatLeftHeaderManager">
          <OutsideClickListener className="Conversations-Actions" onClick={closeActionPicker}>
            <button
              type="button"
              onClick={onActionBtnClick}
              disabled={!canCreateAny || isFetchingGroupAccessibility || isFetchingOneToOneAccessibility}
              name="conversations-action-btn"
              className="Conversations-ActionBtn"
              title={
                canCreateAny
                  ? "Start chatting"
                  : `You don't have access to clients or contacts to start a conversation with`
              }
              style={{
                // 你可以加点样式让禁用有反馈
                background: "none",
                border: "none",
                padding: 0,
                cursor:
                  !canCreateAny || isFetchingGroupAccessibility || isFetchingOneToOneAccessibility
                    ? "not-allowed"
                    : "pointer",
                outline: "none",
              }}
            >
              <UserPen
                size={24}
                color={
                  !canCreateAny || isFetchingGroupAccessibility || isFetchingOneToOneAccessibility
                    ? "#ccc" // 灰色
                    : "#0064ad"
                }
              />
            </button>
            {isActionPickerOpen && (
              <ActionPicker right={0} bottom={-50} options={actionOptions} className="Conversations-ActionPicker" />
            )}
          </OutsideClickListener>
        </div>
      </div>

      <div>
        <SearchField
          name="name"
          className="ConversationFilter-Field"
          value={searchText}
          onChange={onChangeSearchText}
          onClear={onClearSearchText}
        />
      </div>

      <div className="scrollBox">
        <div className="chatListBox">
          {channels.length > 0 ? (
            channels.map((channel) => (
              <div
                className={`chatInfo ${currentChannelId === channel.channel_id ? "active" : ""}`}
                key={channel.channel_id}
                onClick={() => {
                  switchChannel(channel.channel_id, true);
                  // 移动端点击后会自动导航到chatRight，这个逻辑在sdkChat.jsx中处理
                }}
              >
                <div className="chatInfoLeft">
                  <div className="avatar">{channel.channel_avatar}</div>
                  {channel.channel_type === 0 && (
                    <div className={`userOnlineStatus ${channel.online ? "online" : "offline"}`} />
                  )}
                </div>
                <div className="chatInfoRight">
                  <div className="chatInfoRightHeader">
                    <div
                      className="chatName"
                      dangerouslySetInnerHTML={{ __html: channel.highlightedName || channel.channel_display_name }}
                    />
                    <div className="chatTime">{channel.updated_at}</div>
                  </div>
                  <div className="chatMessage">
                    <div className="message">{channel?.last_message?.text}</div>
                    {channel.un_read_count > 0 && <div className="unread">{channel.un_read_count}</div>}
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="emptyState">
              <div className="emptyStateContent">
                <div className="emptyStateIcon">💬</div>
                <div className="emptyStateTitle">No conversations yet</div>
                <div className="emptyStateText">Start a new chat to get started</div>
              </div>
            </div>
          )}
        </div>
      </div>

      {isModalActionPickerOpen && (
        <ModalActionPicker
          options={actionOptions}
          onClose={() => toggleModalActionPickerOpen(false)}
          className="Conversations-MobileActionPicker"
        />
      )}

      <ConversationParticipantPicker
        isOpen={isParticipantPickerOpen}
        excludedContactIds={[authUser.id]}
        onClose={onCloseParticipantPicker}
        onComplete={onCompleteParticipantPicker}
      />
    </div>
  );
};

export default ChatLeft;
