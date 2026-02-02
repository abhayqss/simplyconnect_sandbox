import service from "../lib/server";

const createChannel = async (params) => {
  const result = await service.post("/channel/v1/create", {
    ...params,
  });

  return result.data;
};

export default createChannel;
