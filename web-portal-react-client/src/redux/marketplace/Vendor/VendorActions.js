import actionTypes from "./actionTypes";
import service from "services/Marketplace";

export function getVendorType() {
  return (dispatch) => {
    dispatch({ type: actionTypes.LOAD_REQUEST });

    return service.vendorType().then((res) => {
      dispatch({ type: actionTypes.LOAD_TYPE_SUCESS, payload: res });
      return res;
    });
  };
}

export function getVendorList(params) {
  return (dispatch) => {
    service.vendorList(params).then(async (res) => {
      const fetchLogos = res?.data?.map(async (item) => {
        try {
          if (item.logo) {
            await service.getVendorLogo(item.id).then((response) => {
              item.logo = response.data;
              return Promise.resolve();
            });
          }
          return Promise.resolve();
        } catch (e) {
          return Promise.resolve();
        }
      });
      await Promise.all(fetchLogos);

      dispatch({ type: actionTypes.LOAD_SUCCESS, payload: res });
    });
  };
}

export function getVendorDetail(id) {
  return (dispatch) => {
    service.vendorDetail(id).then(async (res) => {
      const fetchLogos = res?.data?.photos?.map((item) => {
        if (item.url) {
          return service.getVendorPhoto(item.id).then((response) => {
            item.url = response.data;
          });
        }
        return Promise.resolve();
      });
      await Promise.all(fetchLogos);

      await service.getVendorLogo(id).then((response) => {
        res.data.logo = response.data;
      });
      dispatch({ type: actionTypes.LOAD_VENDOR_DETAIL_SUCESS, payload: res });
    });
  };
}

export function clearVendorDetail() {
  return (dispatch) => {
    dispatch({ type: actionTypes.CLEAR_VENDOR_DETAIL, payload: {} });
  };
}

export function getStateOptions(params, options = { response: { extractDataOnly: true } }) {
  return (dispatch) => {
    service
      .getStateOptions(params, {
        response: { extractDataOnly: true },
      })
      .then((res) => {
        dispatch({ type: actionTypes.LOAD_STATE_SUCCESS, payload: res });
      });
  };
}

export function getCategory() {
  return (dispatch) => {
    service.findVendorCategoryType().then((res) => {
      const payload = res.data || [];
      const updatedArray = payload?.map((item) => {
        const { name, ...rest } = item;
        return {
          ...rest,
          label: name,
        };
      });

      dispatch({ type: actionTypes.LOAD_CATEGORY_SUCCESS, payload: updatedArray });
    });
  };
}

export function getAllVendor(params) {
  return (dispatch) => {
    service.findAllVendor(params).then((res) => {
      const result = {};
      res.data.forEach((item) => {
        const firstLetter = item.name[0].toUpperCase();

        if (!result[firstLetter]) {
          result[firstLetter] = [];
        }

        result[firstLetter].push(item);
      });

      dispatch({ type: actionTypes.LOAD_ALL_VENDOR_SUCCESS, payload: result });
    });
  };
}
