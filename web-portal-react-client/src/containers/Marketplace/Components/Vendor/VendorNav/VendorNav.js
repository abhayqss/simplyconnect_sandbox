import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getVendorList, getVendorType } from "../../../../../redux/marketplace/Vendor/VendorActions";

const VendorNav = (props) => {
  const { setShowLeftNav } = props;
  const dispatch = useDispatch();

  const VendorTypeData = useSelector((state) => state.vendor.vendorTypeData);
  const [clickId, setClickId] = useState();

  const changeNav = (id) => {
    setClickId(id);
  };

  useEffect(() => {
    // console.log(setShowLeftNav);
    dispatch(getVendorType());
  }, []);

  useEffect(() => {
    if (VendorTypeData.length > 0) {
      setClickId(VendorTypeData[0].id);
    }
  }, [VendorTypeData.length]);

  useEffect(() => {
    const params = {
      typeId: clickId,
    };
    if (clickId) {
      dispatch(getVendorList(params));
    }
  }, [clickId]);

  return (
    <>
      {VendorTypeData.map((item) => {
        return (
          <div
            className={clickId === item.id ? "navBoxClick" : "navBox"}
            key={item.id}
            onClick={() => {
              changeNav(item.id);
              if (setShowLeftNav) {
                setShowLeftNav(false);
              }
            }}
          >
            {item.name}
          </div>
        );
      })}

      {/*<div className="navBoxClick"> Prescription Filling and Dispensing</div>*/}
    </>
  );
};

export default VendorNav;
