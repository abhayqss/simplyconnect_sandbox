import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getAllVendor } from "../../../../redux/marketplace/Vendor/VendorActions";
import { useHistory } from "react-router-dom";

const NexusListDetail = (props) => {
  const { state, category, vendor, isSearch } = props;
  const history = useHistory();

  const dispatch = useDispatch();
  const { allVendor } = useSelector((state) => state.vendor);

  const Arrar1 = ["A", "B", "C", "D", "E", "F", "G"];
  const Arrar2 = ["H", "I", "J", "K", "L", "M", "N"];
  const Arrar3 = ["O", "P", "Q", "R", "S", "T"];
  const Arrar4 = ["U", "V", "W", "X", "Y", "Z"];

  useEffect(() => {
    let params = {};
    if (state) {
      params.state = state;
    }
    if (category) {
      params.typeIds = category;
    }

    if (vendor) {
      params.name = vendor;
    }

    dispatch(getAllVendor(params));
  }, [isSearch]);

  function renderNexusList(arr) {
    return arr.map((item, index) => (
      <div className="nexusList-module" key={index}>
        <div className="title">{item}</div>
        {allVendor[item]?.map((itm) => (
          <div
            className="nexusList-box"
            key={itm.id}
            onClick={() => history.push(`/web-portal/marketplace/vendorDetail/${itm.id}`)}
          >
            <div className="nexusList-title">{itm.name}</div>
            <div className="nexusList-state">State: {itm?.stateInfo?.name}</div>
          </div>
        ))}
      </div>
    ));
  }

  return (
    <div className="NexusList-Container">
      <div className="nexusList-column">{renderNexusList(Arrar1)}</div>

      <div className="nexusList-column">{renderNexusList(Arrar2)}</div>
      <div className="nexusList-column">{renderNexusList(Arrar3)}</div>
      <div className="nexusList-column">{renderNexusList(Arrar4)}</div>
    </div>
  );
};

export default NexusListDetail;
