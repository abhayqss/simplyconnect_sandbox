import { BaseService } from "services";

const baseService = new BaseService();

const getSdkAccessTokenApi = async () => {
  const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
  return baseService.request({
    method: "POST",
    url: "/conversations/access-token-sdk",
    headers: {
      // Authorization: AuthorizationData,
      "X-Auth-With-Cookies": "no-update",
    },
  });
};

export default getSdkAccessTokenApi;
