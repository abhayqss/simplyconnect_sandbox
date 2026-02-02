import React, { useEffect, useRef } from "react";
import DocumentTitle from "react-document-title";
import Footer from "../../../../../components/Footer/Footer";

import "./BuildingDetail.scss";
import { Breadcrumbs } from "../../../../../components";
import BuildingDetailLeft from "./BuildingDetailLeft";
import BuildingDetailRight from "./BuildingDetailRight";
import { useLocation, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { getBuildingDetail } from "../../../../../redux/marketplace/Building/BuildingActions";

const BuildingDetail = () => {
  const dispatch = useDispatch();
  const location = useLocation();

  const { communityId, organizationId } = useParams();

  const myRef = useRef(null);

  // const organizationId = JSON.parse(localStorage.getItem("AUTHENTICATED_USER") || "{}")?.organizationId || "";

  const isListToDetail = useSelector((state) => state.building.isListToDetail);

  const items = !isListToDetail
    ? [
        { title: "Marketplace", href: "/marketplace", isEnabled: true },
        { title: "BuildingDetail", href: `/marketplace/buildingDetail`, isActive: true },
      ]
    : [
        { title: "Marketplace", href: "/marketplace", isEnabled: true },
        { title: "Building List", href: "/marketplace/buildingList", isEnabled: true },
        { title: "BuildingDetail", href: `/marketplace/buildingDetail`, isActive: true },
      ];

  useEffect(() => {
    if (organizationId) {
      dispatch(getBuildingDetail(organizationId, communityId));
    }
  }, [dispatch, organizationId]);

  useEffect(() => {
    if (myRef.current) {
      myRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [location]);

  return (
    <DocumentTitle title="Simply Connect | Marketplace | BuildingDetail">
      <div className={"buildingDetailWrap"} ref={myRef}>
        <Breadcrumbs items={items} />

        <div className="buildingDetailBox">
          <div className="buildingDetailLeft">
            <BuildingDetailLeft organizationId={organizationId} communityId={communityId} />
          </div>
          <div className="buildingDetailRight">
            <BuildingDetailRight />
          </div>
        </div>

        <Footer theme="gray" className="markplaceFooter" />
      </div>
    </DocumentTitle>
  );
};

export default BuildingDetail;
