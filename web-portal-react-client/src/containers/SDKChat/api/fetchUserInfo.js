import service from "../lib/server";

const fetchUserInfo = async () => {
  return await service.get(`/user/v1/info`);
};

export default fetchUserInfo;
