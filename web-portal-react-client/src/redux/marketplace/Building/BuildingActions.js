import actionTypes from "./actionTypes";
import service from "services/Marketplace";

export function getBuildingList(page = 1, size = 12, name = "", organizationId) {
  const params = {
    page,
    size,
    name,
    organizationId,
  };

  return (dispatch) => {
    dispatch({ type: actionTypes.LOAD_REQUEST });

    return service
      .buildingList(params)
      .then(async (res) => {
        const fetchLogos = res.data.map(async (item) => {
          try {
            if (item.mainLogoPath) {
              await service.getBuildingLogo(item.id).then((response) => {
                item.logo = response.data;
              });
              return Promise.resolve();
            }
            return Promise.resolve();
          } catch (e) {
            return Promise.resolve();
          }
        });

        await Promise.all(fetchLogos);

        dispatch({ type: actionTypes.LOAD_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.LOAD_ERROR, payload: e });
      });
  };
}

export function getBuildingDetail(organizationId, communityId) {
  return (dispatch) => {
    dispatch({ type: actionTypes.LOAD_DETAIL_REQUEST });

    return service
      .buildingDetail(organizationId, communityId)
      .then(async (res) => {
        const fetchLogos = res.data.pictures?.map(async (item) => {
          try {
            if (item.name) {
              await service.getBuildingPhoto(organizationId, communityId, item.id).then((response) => {
                item.logo = response.data;
              });
              return Promise.resolve();
            }
            return Promise.resolve();
          } catch (e) {
            return Promise.resolve();
          }
        });

        await Promise.all(fetchLogos);

        dispatch({ type: actionTypes.LOAD_DETAIL_SUCESS, payload: res });
      })
      .catch((e) => {
        dispatch({ type: actionTypes.LOAD_DETAIL_ERROR, payload: e });
      });
  };
}

export function clearBuildingDetail() {
  return (dispatch) => {
    dispatch({ type: actionTypes.CLEAR_BUILDING_DETAIL, payload: {} });
  };
}

export function IsListToDetail(isList) {
  return (dispatch) => {
    dispatch({ type: actionTypes.IS_LIST_TO_DETAIL, payload: isList });
  };
}
