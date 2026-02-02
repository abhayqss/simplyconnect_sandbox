import groupMessagesByDate from "../lib/groupMessagesByDate";
import service from "../lib/server";

// fetchMessagesList.js
const fetchMessagesList = async ({ channel_id, params = {} }) => {
  const queryParams = {
    page: params.page || 1,
    size: params.size || 15,
    begin_time: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    end_time: new Date().toISOString(),
  };

  // 加载更多时额外传 last_message_id
  if (params.isLoadMore && params.last_message_id) {
    queryParams.last_message_id = params.last_message_id;
  }

  const response = await service.get(`/channel/v1/${channel_id}/messages`, {
    params: queryParams,
  });

  return {
    ...response.data,
    total: response.data?.total || 0,
    message_list: groupMessagesByDate(response.data?.message_list || []),
    size: queryParams.size,
    current_page: params.page || 1, // 返回当前页码
  };
};

export default fetchMessagesList;
