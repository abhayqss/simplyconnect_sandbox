import { useHistory } from "react-router-dom";
import { useSelector } from "react-redux";
import defaultImg from "images/marketplace/defaultImg.png";
import adminAssociationsService from "../../../../../services/AssociationsService";
import { useState } from "react";

const VendorNavDetailList = () => {
  const history = useHistory();
  const vendorListData = useSelector((state) => state.vendor.vendorList);
  const [associationV, setAssociationV] = useState();

  const targetLength = 9;

  while (vendorListData.length < targetLength) {
    vendorListData.push({ id: Math.random() });
  }

  const vendorListData13 = vendorListData.slice(0, 9);
  const vendorListDataLast3 = [{ id: "level1" }, { id: "level2" }, { id: "level3" }];

  return (
    <>
      <div className="vendorLeftBox">
        {vendorListData13.map((item) => {
          return (
            <div key={item.id} className="item">
              {item.name && (
                <>
                  <img
                    src={item?.logo ? `data:image/png;base64,${item?.logo}` : defaultImg}
                    alt=""
                    onClick={() => history.push(`/web-portal/marketplace/vendorDetail/${item.id}`)}
                  />
                  <div className={"item-name"} title={item.name}>
                    {item.name}
                  </div>
                </>
              )}
            </div>
          );
        })}
      </div>
      <div className="vendorRightBox">
        {vendorListDataLast3.map((item) => {
          return (
            <div key={item.id} className="item second-col">
              {item.name && (
                <div onClick={() => history.push(`/web-portal/marketplace/vendorDetail/${item.id}`)}>
                  <img src={item?.logo ? `data:image/png;base64,${item?.logo}` : defaultImg} alt="" />
                  <div className={"item-name"} title={item.name}>
                    {item.name}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </>
  );
};

export default VendorNavDetailList;
