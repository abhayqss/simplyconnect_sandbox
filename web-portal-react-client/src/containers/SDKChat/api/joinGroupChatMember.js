import service from "../lib/server";

const joinGroupChatMember = async (channel_id, params) => {
  return await service.post(`/channel/v1/${channel_id}/join`, {
    members: [...params],
  });
};

export default joinGroupChatMember;
