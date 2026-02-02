import service from "../lib/server";
import ProcessChannelList from "../lib/processChannelList";

const fetchChannelList = async (chatUserId) => {
  const result = await service.get("/channel/v1/list", {});

  return ProcessChannelList(result.data, chatUserId);
};

export default fetchChannelList;
