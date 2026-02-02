import Slider from "react-slick";

import "./BuildingDetail.scss";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { APIProvider, Map, Marker } from "@vis.gl/react-google-maps";
import config from "../../../config";
import { useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import { useDispatch, useSelector } from "react-redux";
import React, { useCallback, useEffect, useState } from "react";
import { featBuildingQrDetail } from "../../../redux/QrCode/QrcodeActions";
import CheckedImg from "../../../images/marketplace/checkedImg.svg";
import { MarkerWithInfoWindow } from "../../../components/Map/MarkerWithInfoWindow";

const BuildingDetail = (props) => {
  const { id, type } = props;
  const dispatch = useDispatch();
  const history = useHistory();

  const { buildingQrDetail } = useSelector((state) => state.Qrcode);

  const [cameraProps, setCameraProps] = useState();
  const handleCameraChange = useCallback((ev) => setCameraProps(ev.detail));

  const login = () => {
    history.push(path(`/associate/${id}/${type}/${buildingQrDetail.name}/login`));
  };

  const register = () => {
    history.push(path(`/associate/${id}/${type}/${buildingQrDetail.name}/register`));
  };

  useEffect(() => {
    dispatch(featBuildingQrDetail(id));
  }, []);

  useEffect(() => {
    if (buildingQrDetail?.location?.latitude !== null && buildingQrDetail?.location !== undefined) {
      setCameraProps({
        center: { lat: buildingQrDetail?.location?.latitude, lng: buildingQrDetail?.location?.longitude },
      });

      return;
    }
    setCameraProps({
      center: { lat: 38.9072, lng: -77.0369 },
    });
  }, [buildingQrDetail]);

  return (
    <>
      <div className="buildingDetailTitle">{buildingQrDetail?.name}</div>

      {detailInfoList?.map((item) => {
        return (
          <div className="afterCodeDetailInfo" key={item.id}>
            <span>{item.label}</span>
            {buildingQrDetail && <span>{buildingQrDetail[item.id]}</span>}
          </div>
        );
      })}

      {buildingQrDetail?.pictures?.length > 0 && (
        <div className="slick-slider-container">
          <Slider {...settings}>
            {buildingQrDetail?.pictures.map((item) => {
              return (
                <div key={item.id}>
                  <img src={`data:image/png;base64,${item.logo}`} alt="" style={{ width: "100%", height: "100%" }} />
                </div>
              );
            })}
          </Slider>
        </div>
      )}

      <div className="associateRegistered" onClick={login}>
        Sign In
      </div>

      <div className="associateUnRegistered" onClick={register}>
        Sign Up
      </div>

      <div className="associateService">
        <div className="associateTitle">SERVICE</div>

        {buildingQrDetail?.marketplace?.serviceNames?.map((item, index) => {
          return (
            <div className="associateServiceList" key={index}>
              {item}
            </div>
          );
        })}
      </div>

      <div className="associateLanguages">
        <div className="associateTitle">LANGUAGES</div>

        {buildingQrDetail?.languageServices?.length > 0 &&
          buildingQrDetail.languageServices.map((item) => {
            return (
              <div className="associateLanguagesList" key={item.id}>
                <img src={CheckedImg} alt="" />
                <span>{item.displayName}</span>
              </div>
            );
          })}
      </div>

      <div className="associateIntruduction">{buildingQrDetail.introduction}</div>

      <div className="associateMap">
        <APIProvider apiKey={config.google.maps.apiKey}>
          <Map
            zoom={15}
            {...cameraProps}
            gestureHandling={"greedy"}
            disableDefaultUI={false}
            onCameraChanged={handleCameraChange}
            mapId={"8b15fd729aa51fc"}
          >
            {buildingQrDetail?.location !== undefined && buildingQrDetail?.location?.latitude !== null && (
              <MarkerWithInfoWindow
                position={{ lat: buildingQrDetail?.location?.latitude, lng: buildingQrDetail?.location?.longitude }}
                addressName={buildingQrDetail?.name}
                address={`${buildingQrDetail?.street} ${buildingQrDetail?.city} ${buildingQrDetail?.zipCode}`}
              />
            )}
          </Map>
        </APIProvider>
      </div>
    </>
  );
};

const styles = {
  dot: {
    width: "20px",
    height: "10px",
    borderRadius: "5px",
  },
};
const detailInfoList = [
  {
    id: "oid",
    label: "COMMUNITY OID",
  },
  {
    id: "licenseNumber",
    label: "LICENSE #",
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
  {
    id: "numberOfBeds",
    label: "# OF UNITS",
  },
  {
    id: "numberOfVacantBeds",
    label: "# OF OPEN UNIts",
  },
  {
    id: "organizationName",
    label: "ORGANIZATION",
  },
];
const settings = {
  dots: true,
  infinite: true,
  speed: 500,
  slidesToShow: 1,
  slidesToScroll: 1,
  autoplay: true,
  arrows: false,
  appendDots: (dots) => <div style={{ bottom: "0" }}>{dots}</div>,
  customPaging: (i) => <div style={styles.dot}></div>,
};

export default BuildingDetail;
