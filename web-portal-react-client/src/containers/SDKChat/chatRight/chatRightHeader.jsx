import { PhoneCall, UserRoundCog, Video, X, ArrowLeft, Edit } from "lucide-react";
import "./chatRight.scss";
import { useChatManagerContext } from "../context/ChatManagerContext";
import React, { useEffect, useRef, useState } from "react";

const ChatRightHeader = ({
  toggleMultipleParticipantPicker,
  isMultipleParticipantPickerOpen,
  onBackToList,
  isMobile,
}) => {
  const { currentChannel, currentChannelId, removeGroupMember, exitGroupChat, callStart, updateGroupName, chatUserId } =
    useChatManagerContext();
  const isGroupCreator = currentChannel?.channel_created_id === chatUserId;

  const [showParticipants, setShowParticipants] = useState(false);
  const [showOfflineTooltip, setShowOfflineTooltip] = useState(false);
  const participantsBoxRef = useRef(null);
  const tooltipTimeoutRef = useRef(null);

  const [isEditingName, setIsEditingName] = useState(false);
  const [groupNameInput, setGroupNameInput] = useState("");

  useEffect(() => {
    if (!showParticipants) return;

    function handleClickOutside(event) {
      if (
        participantsBoxRef.current &&
        !participantsBoxRef.current.contains(event.target) &&
        !event.target.classList.contains("groupStatus")
      ) {
        setShowParticipants(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showParticipants]);

  const handleRemoveMember = (member) => {
    const userIds = [member.user_id];
    removeGroupMember(currentChannelId, userIds);
  };

  const handleSaveGroupName = () => {
    if (groupNameInput.trim() && groupNameInput.trim() !== currentChannel.channel_display_name) {
      if (typeof updateGroupName === "function") {
        updateGroupName(currentChannelId, groupNameInput.trim());
      }
    }
    setIsEditingName(false);
  };

  const handleExitGroup = () => {
    exitGroupChat(currentChannelId);
    setShowParticipants(false);
  };

  const handleVideo = (type) => {
    // 检查对方是否在线（仅对单聊）
    if (currentChannel.channel_type === 0 && !currentChannel.online) {
      handleOfflineClick();
      return; // 如果对方不在线，显示提示并返回
    }

    const { user_id } = currentChannel.channel_member.find((member) => member.user_id !== chatUserId);
    const params = {
      channel_id: currentChannelId,
      receiver: user_id,
      type,
    };
    callStart(params);
  };

  // 检查是否可以发起通话（对方是否在线）
  const canMakeCall = currentChannel?.channel_type === 0 ? currentChannel.online : true;

  // 处理点击离线用户的通话按钮
  const handleOfflineClick = () => {
    if (!canMakeCall) {
      setShowOfflineTooltip(true);
      // 清除之前的定时器
      if (tooltipTimeoutRef.current) {
        clearTimeout(tooltipTimeoutRef.current);
      }
      // 4秒后自动隐藏提示
      tooltipTimeoutRef.current = setTimeout(() => {
        setShowOfflineTooltip(false);
      }, 4000);
    }
  };

  // 隐藏提示气泡
  const hideTooltip = () => {
    setShowOfflineTooltip(false);
    if (tooltipTimeoutRef.current) {
      clearTimeout(tooltipTimeoutRef.current);
    }
  };

  // 清理定时器
  useEffect(() => {
    return () => {
      if (tooltipTimeoutRef.current) {
        clearTimeout(tooltipTimeoutRef.current);
      }
    };
  }, []);

  return (
    <div className="chatRightHeader">
      {currentChannel && (
        <>
          {isMobile && (
            <div className="mobileBackButton" onClick={onBackToList} title="Back to chat list">
              <ArrowLeft size={20} color="#0064ad" />
            </div>
          )}
          <div className="chatRightHeaderLeft">
            <div className="avatar">{currentChannel.channel_avatar}</div>
            <div className="chatDetailInfo">
              <div className="detailName" style={{ display: "flex", alignItems: "center", gap: 8 }}>
                {isEditingName ? (
                  <>
                    <input
                      type="text"
                      className="groupName-edit-input"
                      value={groupNameInput}
                      autoFocus
                      onChange={(e) => setGroupNameInput(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter") handleSaveGroupName();
                        if (e.key === "Escape") setIsEditingName(false);
                      }}
                      style={{
                        width: 260,
                        padding: "4px 8px",
                        marginRight: 8,
                        borderRadius: 4,
                        border: "1px solid #cfd8dc",
                        fontSize: 15,
                        outline: "none",
                      }}
                      placeholder="Enter group name"
                    />
                    <button
                      className="saveGroupNameBtn"
                      onClick={handleSaveGroupName}
                      style={{
                        padding: "4px 14px",
                        background: "#0064ad",
                        color: "#fff",
                        border: "none",
                        borderRadius: 4,
                        marginRight: 6,
                        fontSize: 14,
                        cursor: "pointer",
                        fontWeight: 500,
                        transition: "background 0.2s",
                      }}
                    >
                      Save
                    </button>
                    <button
                      className="cancelGroupNameBtn"
                      onClick={() => setIsEditingName(false)}
                      style={{
                        padding: "4px 14px",
                        background: "#e0e3e7",
                        color: "#34495e",
                        border: "none",
                        borderRadius: 4,
                        fontSize: 14,
                        cursor: "pointer",
                        fontWeight: 500,
                        transition: "background 0.2s",
                      }}
                    >
                      Cancel
                    </button>
                  </>
                ) : (
                  <>
                    {currentChannel.channel_display_name}
                    {currentChannel.channel_type === 1 && isGroupCreator && (
                      <Edit
                        size={16}
                        style={{ marginLeft: 6, cursor: "pointer", color: "#0064ad" }}
                        onClick={() => {
                          setIsEditingName(true);
                          setGroupNameInput(currentChannel.channel_display_name);
                        }}
                        title="Modify group chat name"
                      />
                    )}
                  </>
                )}
              </div>
              {currentChannel.channel_type === 0 ? (
                <div className={`detailStatus ${currentChannel.online ? "online" : "offline"}`}>
                  <span className="status-indicator"></span>
                  {currentChannel.online ? "Online" : "Offline"}
                </div>
              ) : (
                <>
                  <div
                    className="groupStatus"
                    onClick={() => setShowParticipants((v) => !v)}
                    style={{ userSelect: "none" }}
                  >
                    {currentChannel?.channel_member?.length + " " + "participants"}
                  </div>
                  {showParticipants && (
                    <div className="participantsBox" ref={participantsBoxRef} onClick={(e) => e.stopPropagation()}>
                      {currentChannel.channel_member?.map((member) => {
                        const isCreator = currentChannel.channel_created_id === chatUserId;
                        const isSelf = member.user_id === chatUserId;
                        return (
                          <div className="participantInfo" key={member.user_id}>
                            <div className="participantInfoLeft">
                              <div className="avatarBox">
                                <div className="avatarParticipant">{member.user_avatar}</div>
                                <div
                                  className={`userOnlineStatus ${member.online || member.user_id === chatUserId ? "online" : "offline"}`}
                                />
                              </div>
                              <div className="name">{member.user_name}</div>
                            </div>
                            {isCreator && !isSelf && (
                              <X
                                className="deleteMemberBtn"
                                onClick={() => handleRemoveMember(member)}
                                title="Delete this member"
                                style={{ cursor: "pointer" }}
                              />
                            )}
                            {!isCreator && isSelf && (
                              <X
                                className="exitGroupBtn"
                                onClick={() => handleExitGroup()}
                                title="Leave group chat"
                                style={{ cursor: "pointer" }}
                              />
                            )}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </>
              )}
            </div>
          </div>

          <div className="chatRightHeaderRight">
            {currentChannel.channel_type === 0 && (
              <>
                <div className="call-buttons-container">
                  <div
                    className={`imageBox ${!canMakeCall ? "disabled" : ""}`}
                    title={!canMakeCall ? "User is offline" : "Start video call"}
                    onClick={canMakeCall ? () => handleVideo(2) : handleOfflineClick}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        if (canMakeCall) {
                          handleVideo(2);
                        } else {
                          handleOfflineClick();
                        }
                      }
                    }}
                    tabIndex={0}
                    role="button"
                    aria-label={!canMakeCall ? "Cannot start video call - user is offline" : "Start video call"}
                  >
                    <Video
                      color={canMakeCall ? "#0064ad" : "#ccc"}
                      style={{ cursor: canMakeCall ? "pointer" : "not-allowed" }}
                    />
                  </div>
                  <div
                    className={`imageBox ${!canMakeCall ? "disabled" : ""}`}
                    title={!canMakeCall ? "User is offline" : "Start voice call"}
                    onClick={canMakeCall ? () => handleVideo(1) : handleOfflineClick}
                    onKeyDown={(e) => {
                      if (e.key === "Enter" || e.key === " ") {
                        e.preventDefault();
                        if (canMakeCall) {
                          handleVideo(1);
                        } else {
                          handleOfflineClick();
                        }
                      }
                    }}
                    tabIndex={0}
                    role="button"
                    aria-label={!canMakeCall ? "Cannot start voice call - user is offline" : "Start voice call"}
                  >
                    <PhoneCall
                      color={canMakeCall ? "#0064ad" : "#ccc"}
                      style={{ cursor: canMakeCall ? "pointer" : "not-allowed" }}
                    />
                  </div>

                  {/* 离线提示气泡 */}
                  {showOfflineTooltip && (
                    <div
                      className="offline-tooltip"
                      onClick={hideTooltip}
                      onKeyDown={(e) => {
                        if (e.key === "Escape") {
                          hideTooltip();
                        }
                      }}
                      role="alert"
                      aria-live="polite"
                    >
                      <div className="tooltip-content">
                        <span className="tooltip-icon">📵</span>
                        <div className="tooltip-text">
                          <div className="tooltip-title">User is offline</div>
                          <div className="tooltip-subtitle">Cannot start call right now</div>
                        </div>
                      </div>
                      <div className="tooltip-arrow"></div>
                    </div>
                  )}
                </div>
              </>
            )}

            {currentChannel.channel_type === 1 && isGroupCreator && (
              <div className="imageBox">
                <UserRoundCog
                  color="#0064ad"
                  onClick={() => {
                    toggleMultipleParticipantPicker(!isMultipleParticipantPickerOpen);
                    sessionStorage.setItem("isAddMember", "true");
                  }}
                />
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default ChatRightHeader;
