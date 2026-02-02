import request from "superagent";

import BaseService from "./BaseService";

export class TransportationService extends BaseService {
  canView(params) {
    return super.request({
      url: `/transportation/rides/can-view`,
      response: { extractDataOnly: true },
      params,
    });
  }

  rideRequest(params, options) {
    return super.request({
      url: `/transportation/rides/request`,
      ...options,
      params,
    });
  }

  rideHistory(params, options) {
    return super.request({
      url: `/transportation/rides/history`,
      ...options,
      params,
    });
  }

  submit({ token }, { url }) {
    const AuthorizationData = JSON.parse(localStorage.getItem("AUTHENTICATED_USER"))?.token || "";
    return (
      request
        .post(url)
        .type("form")
        .accept("text/plain")
        .withCredentials()
        .set("X-Auth-With-Cookies", "no-update")
        // .set("Authorization", AuthorizationData)
        .send({ payload: token })
        .then((response) => {
          return response;
        })
        .catch((e) => {
          throw e;
        })
    );
  }
}

export default new TransportationService();
