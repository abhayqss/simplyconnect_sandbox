import processMessage from "./processMessage";

function buildAllChannelData(sdk_filed_list) {
  if (!Array.isArray(sdk_filed_list)) return {};
  return sdk_filed_list.reduce((acc, item) => {
    acc[item.channel_id] = {
      ...item,
      last_message: processMessage(item.last_message, { setShowTime: false, forChannelList: true }),
    };
    return acc;
  }, {});
}

export default buildAllChannelData;
