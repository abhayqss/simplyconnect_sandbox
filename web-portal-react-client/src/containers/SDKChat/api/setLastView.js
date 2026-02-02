import service from "../lib/server";

const setLastView = async (channel_id) => {
  return await service.post(`/channel/v1/${channel_id}/lastView`);
};

export default setLastView;
