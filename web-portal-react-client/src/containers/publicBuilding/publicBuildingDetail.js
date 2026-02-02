import React, { useEffect, useRef } from "react";
import DocumentTitle from "react-document-title";

import './publicBuilding';
import { Breadcrumbs } from "../../components";
import BuildingDetailLeft from "./publicBuildingDetailLeft";
import { useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";
import './publicBuildingDetail.scss'

const BuildingDetail = () => {

  const dispatch = useDispatch();
  const location = useLocation();

  const myRef = useRef(null);

  const items = [
    { title: 'simplyplace', href: '/simplyplace', isEnabled: true },
    { title: 'publicBuildingDetail', href: `/simplyplace/buildingDetail`, isActive: true },
  ]


  useEffect(() => {

  }, [dispatch]);


  useEffect(() => {
    if (myRef.current) {
      myRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [location]);


  return (
    <DocumentTitle title="Simply Connect | Marketplace | BuildingDetail">
      <div className={'buildingDetailWrap'} ref={myRef}>

        <Breadcrumbs items={items}/>

        <div className="buildingDetailBox">

          <div className="buildingDetailLeft">
            <BuildingDetailLeft/>
          </div>
        </div>
      </div>

    </DocumentTitle>
  )
}

export default BuildingDetail;
