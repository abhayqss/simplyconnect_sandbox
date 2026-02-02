import service from "../lib/server";

const sendMessageFn = async (channel_id, params) => {
  const result = await service.post(`/channel/v1/${channel_id}/message`, params);
  return result.data;
};

export default sendMessageFn;
