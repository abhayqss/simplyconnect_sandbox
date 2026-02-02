import React, { useCallback } from "react";
import Avatar from "../../../../Avatar/Avatar";
import "./card.scss";
import { IconButton } from "../../../../../components/buttons";
import cn from "classnames";
import { ReactComponent as Chat } from "images/chat-bubble.svg";
import { ReactComponent as Video } from "images/videocall.svg";
import { ReactComponent as ReselectPerson } from "images/Clients/reselectPerson.svg";
import { ReactComponent as DeletePerson } from "images/delete.svg";
// import { ReactComponent as ReselectPerson } from "images/Clients/img.png";
import { useAuthUser } from "../../../../../hooks/common";
import { useConversations } from "../../../../../hooks/business/conversations";
import { path } from "../../../../../lib/utils/ContextUtils";
import { useHistory } from "react-router-dom";
import { Button } from "reactstrap";

const HasMemberCard = (props) => {
  const { changeMember, itemDetail, className, canAdd, deleteMember } = props;
  const user = useAuthUser();
  const history = useHistory();
  const { emit } = useConversations();
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

  return (
    <div className={className}>
      <div className={"card-member-avatar"}>
        <Avatar name={itemDetail?.contactName} id={itemDetail?.avatarId} className="MemberAvatar" />
      </div>
      <div className={"card-name-have-show"}>{itemDetail.contactName}</div>
      <div className={"card-role-btn-show"}>{itemDetail.roleName}</div>
      <div className={"card-has-member-option  margin-top-15"}>
        <div
          className={cn("CareTeamMember-Action", "margin-left-15", {
            "CareTeamMember-Action_disabled": !itemDetail?.isVideoCallAllowed,
          })}
          id={`member_${itemDetail.id}__chat_action_btn`}
        >
          <IconButton
            size={30}
            Icon={Video}
            name={`member_${itemDetail.id}__video_action`}
            tooltip={
              itemDetail?.isVideoCallAllowed
                ? "Call with team member"
                : "You don't have access to clients or contacts to start a call with"
            }
            onClick={() => itemDetail.isVideoCallAllowed && onVideo(itemDetail)}
            className="VideoActionBtn"
          />
        </div>
        <div
          className={cn("CareTeamMember-Action", "margin-left-15", "margin-right-15", {
            "CareTeamMember-Action_disabled": !itemDetail.isConversationAllowed,
          })}
          id={`member_${itemDetail.id}__chat_action_btn`}
        >
          <IconButton
            size={30}
            Icon={Chat}
            name={`member_${itemDetail.id}__chat_action`}
            tooltip={
              itemDetail?.isConversationAllowed
                ? "Chat with team member"
                : `You don't have access to clients or contacts to start a conversation with`
            }
            onClick={() => itemDetail?.isConversationAllowed && onChat(itemDetail)}
            className="ChatActionBtn"
          />
        </div>
        <Button
          disabled={!canAdd}
          style={{ background: "transparent", border: "none" }}
          onClick={() => changeMember(itemDetail.roleName, itemDetail.id)}
        >
          <IconButton
            size={30}
            disabled={!canAdd}
            Icon={ReselectPerson}
            name={`card_${itemDetail.id}__reselect_action`}
            tooltip={"Change member"}
            className="TopCardActionBtn"
          />
        </Button>

        {/*   <Button
          disabled={!canAdd}
          style={{ background: "transparent", border: "none" }}
          onClick={() => deleteMember(itemDetail.roleName, itemDetail.id)}
        >
          <IconButton
            size={30}
            disabled={!canAdd}
            Icon={DeletePerson}
            name={`card_${itemDetail.id}__reselect_action`}
            tooltip={"Delete member"}
            className="TopCardActionBtn"
          />
        </Button>*/}
      </div>
    </div>
  );
};
export default HasMemberCard;
