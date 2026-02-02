import "./Vendor.scss";
import VendorNav from "./VendorNav/VendorNav";
import VendorNavDetailList from "./VendorNavDetailList/VendorNavDetailList";
import foldImg from "images/marketplace/fold.svg";
import { useState } from "react";

const Vendor = () => {
  // const [showFold,setShowFold] =
  const [showLeftNav, setShowLeftNav] = useState(false);

  return (
    <div className="VendorWrap">
      <img src={foldImg} alt="" className={"vendorFoldImg"} onClick={() => setShowLeftNav(!showLeftNav)} />

      <div className={`VendorWrapLeft ${showLeftNav ? "VendorWrapLeftShow" : "VendorWrapLeftNoShow"}`}>
        <VendorNav setShowLeftNav={setShowLeftNav} />
      </div>

      <div className="VendorWrapRight">
        <VendorNavDetailList />
      </div>
    </div>
  );
};

export default Vendor;
