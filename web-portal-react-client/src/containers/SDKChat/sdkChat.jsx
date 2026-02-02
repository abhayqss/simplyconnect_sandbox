import "./sdkChat.scss";
import { useEffect, useState } from "react";
import ChatLeft from "./chatLeft/chatLeft";
import ChatRight from "./chatRight/chatRight";
import GroupConversationParticipantPicker from "../Communication/Messenger/GroupConversationParticipantPicker/GroupConversationParticipantPicker";
import { useAuthUser } from "../../hooks/common"; // Import useAuthUser
import { useChatManagerContext } from "./context/ChatManagerContext";
import { useChatBadge } from "./context/ChatBadgeContext";

const SdkChat = () => {
  const {
    refreshChannelList,
    currentChannelId,
    currentChannel, // Get currentChannel
    createChannelAndSwitch, // Get createChannelAndSwitch
    joinGroupChat, // Get joinGroupChat
    chatToken, // Get chatToken from context
    chatUserId, // Get chatUserId from context
    getUserInfo,
  } = useChatManagerContext();
  const authUser = useAuthUser(); // Get authUser
  const { clearUnread } = useChatBadge();
  const [isMultipleParticipantPickerOpen, toggleMultipleParticipantPicker] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);
  const [showChatRight, setShowChatRight] = useState(false);

  // 检测屏幕尺寸
  useEffect(() => {
    const checkIsMobile = () => {
      const newIsMobile = window.innerWidth <= 768;
      setIsMobile(newIsMobile);

      // 如果从移动端切换到桌面端，重置显示状态
      if (!newIsMobile) {
        setShowChatRight(false);
      }
    };

    window.addEventListener("resize", checkIsMobile);
    return () => window.removeEventListener("resize", checkIsMobile);
  }, []);

  // 移动端：当选择频道时显示聊天右侧
  useEffect(() => {
    if (isMobile && currentChannelId) {
      setShowChatRight(true);
    } else if (isMobile && !currentChannelId) {
      // 如果没有选择频道，确保显示左侧列表
      setShowChatRight(false);
    }
  }, [isMobile, currentChannelId]);

  useEffect(() => {
    if (chatToken) {
      getUserInfo();
      refreshChannelList();
    }
  }, [chatToken]);

  useEffect(() => {
    clearUnread();
  }, [clearUnread]);

  // 如果已经退出登录（authUser 或 chatToken 不存在），直接不渲染聊天组件，防止副作用和冗余请求
  if (!chatUserId || !chatToken) {
    return null;
  }

  // 返回到列表页面
  const handleBackToList = () => {
    setShowChatRight(false);
  };

  // Handler for when the participant picker modal is closed
  const onCloseMultipleParticipantPicker = () => {
    toggleMultipleParticipantPicker(false);
    sessionStorage.removeItem("isAddMember");
  };

  // Handler for when participants are selected in the group picker
  const onCompleteParticipantGroupPicker = (value) => {
    const isAddMember = sessionStorage.getItem("isAddMember") === "true";

    if (isAddMember) {
      const existedUserIds = currentChannel?.channel_member?.map((mem) => mem.user_id) || [];
      // 新选择的 user_id（去除已在群聊内的）
      const newMembers = value.allChatUserIds.filter((userId) => !existedUserIds.includes(userId));
      if (newMembers.length > 0) {
        joinGroupChat(currentChannelId, newMembers);
      }

      sessionStorage.removeItem("isAddMember");
    } else {
      const params = {
        channel_type: 1,
        creator: chatUserId,
        display_name: value.groupName,
        receivers: [chatUserId, ...value.allChatUserIds].filter((id) => id !== "" && id !== null && id !== undefined),
      };

      createChannelAndSwitch(params);
    }

    // Close the modal after completion
    toggleMultipleParticipantPicker(false);
    // Ensure session storage is cleared after creating a new group
    if (!isAddMember) {
      sessionStorage.removeItem("isAddMember");
    }
  };

  // Determine if the modal should be in "new conversation" mode
  // This is true when "isAddMember" is NOT set in session storage
  const isNewConversationMode = sessionStorage.getItem("isAddMember") !== "true";

  return (
    <div className={`sdkChat ${isMobile ? "mobile" : "desktop"}`}>
      {/* Render the GroupConversationParticipantPicker here, outside the mobile/desktop conditional */}
      <GroupConversationParticipantPicker
        isNewConversation={isNewConversationMode} // Pass the derived mode
        excludedContactIds={[authUser.id]}
        isOpen={isMultipleParticipantPickerOpen}
        onClose={onCloseMultipleParticipantPicker}
        onComplete={onCompleteParticipantGroupPicker}
      />
      {isMobile ? (
        // 移动端：根据状态显示左侧或右侧
        <>
          {!showChatRight ? (
            <div className="chatLeftWrapper">
              <ChatLeft
                isMultipleParticipantPickerOpen={isMultipleParticipantPickerOpen}
                toggleMultipleParticipantPicker={toggleMultipleParticipantPicker}
              />
            </div>
          ) : (
            <div className="chatRightWrapper">
              <ChatRight
                isMultipleParticipantPickerOpen={isMultipleParticipantPickerOpen}
                toggleMultipleParticipantPicker={toggleMultipleParticipantPicker}
                onBackToList={handleBackToList}
                isMobile={isMobile}
              />
            </div>
          )}
        </>
      ) : (
        // 桌面端：同时显示左右两侧
        <>
          <div className="chatLeftWrapper">
            <ChatLeft
              isMultipleParticipantPickerOpen={isMultipleParticipantPickerOpen}
              toggleMultipleParticipantPicker={toggleMultipleParticipantPicker}
            />
          </div>
          <div className="chatRightWrapper">
            <ChatRight
              isMultipleParticipantPickerOpen={isMultipleParticipantPickerOpen}
              toggleMultipleParticipantPicker={toggleMultipleParticipantPicker}
              onBackToList={handleBackToList}
              isMobile={isMobile}
            />
          </div>
        </>
      )}
    </div>
  );
};

export default SdkChat;
