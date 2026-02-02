import "./OrgDetail.scss";
import { useHistory } from "react-router-dom";
import { path } from "../../../lib/utils/ContextUtils";
import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { featCategories, featCommunities, featOrgDetail } from "../../../redux/QrCode/QrcodeActions";
import "../BuildingDetail/BuildingDetail.scss";
import { ReactComponent as Indicatior } from "images/dot.svg";
import "../../Admin/Organizations/OrganizationDetails/OrganizationDetails.scss";

const detailInfoList = [
  {
    id: "oid",
    label: "ORGANIZATION OID",
  },
  {
    id: "email",
    label: "EMAIL",
  },
  {
    id: "phone",
    label: "PHONE",
  },
  {
    id: "displayAddress",
    label: "ADDRESS",
  },
];

const OrgDetail = (props) => {
  const { id, type } = props;
  const dispatch = useDispatch();
  const { orgDtail, categories, communities } = useSelector((state) => state.Qrcode);

  const history = useHistory();

  const login = () => {
    history.push(path(`/associate/${id}/${type}/${orgDtail.name}/login`));
  };

  const register = () => {
    history.push(path(`/associate/${id}/${type}/${orgDtail.name}/register`));
  };

  useEffect(() => {
    dispatch(featOrgDetail(id));
    dispatch(featCategories(id));
    dispatch(featCommunities(id));
  }, [id]);

  return (
    <>
      <div className="orgDetailTitle">{orgDtail?.name}</div>

      {detailInfoList?.map((item) => {
        return (
          <div className="afterCodeDetailInfo" key={item.id}>
            <span>{item?.label}</span>
            {orgDtail && <span>{orgDtail[item.id]}</span>}
          </div>
        );
      })}

      <div className="associateRegistered" onClick={login}>
        Sign In
      </div>

      <div className="associateUnRegistered" onClick={register}>
        Sign Up
      </div>

      <div className="orgDetailTitle">Active Categories</div>

      {categories?.map((o) => {
        return (
          <div key={o.id} className="OrganizationDetails-Category" style={{ borderColor: o.color || "#000000" }}>
            <Indicatior style={{ fill: o.color || "#000000" }} className="OrganizationDetails-CategoryIndicator" />
            <div className="OrganizationDetails-CategoryName">{o.name}</div>
          </div>
        );
      })}

      <div className="orgDetailTitleend">
        <span>Communities</span>

        {communities?.length > 0 && <div className="orgDetailNum">{communities?.length}</div>}
      </div>

      {communities?.map((item) => {
        return (
          <div className="orgCommunities" key={item.id}>
            <div className="orgCommunitiesTitle">{item?.name}</div>

            <div className="orgCommunitiesInfo">Address: {item?.displayAddress}</div>
          </div>
        );
      })}
    </>
  );
};

export default OrgDetail;
