import service from "../lib/server";

const exitGroupChatMember = async (channel_id) => {
  return await service.get(`/channel/v1/${channel_id}/exit`);
};

export default exitGroupChatMember;
