import service from "../lib/server";

const changeGroupName = async (channel_Id, display_name) => {
  const result = await service.put(`/channel/v1/${channel_Id}/displayName`, {
    display_name,
  });

  return result.data;
};

export default changeGroupName;
