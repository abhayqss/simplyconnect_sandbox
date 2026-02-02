import service from "../lib/server";

const removeGroupChatMember = async (channel_id, params) => {
  return await service.post(`/channel/v1/${channel_id}/remove`, {
    members: [...params],
  });
};

export default removeGroupChatMember;
