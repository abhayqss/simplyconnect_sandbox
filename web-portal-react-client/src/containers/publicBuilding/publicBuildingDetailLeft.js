import CheckedImg from "../../images/marketplace/checkedImg.svg";
import config from "../../config";
import React, { useCallback, useEffect, useState } from "react";
import defaultImg from "../../images/marketplace/defaultImg.png";
import { useParams } from "react-router-dom";
import service from "../../services/PublicBuilding";
import { APIProvider, Map } from "@vis.gl/react-google-maps";
import { MarkerWithInfoWindow } from "../../components/Map/MarkerWithInfoWindow";

const BuildingDetailLeft = () => {
  const params = useParams();
  const [detailData, setDetail] = useState({});

  useEffect(() => {
    // 获取数据
    if (params.id) {
      service.featQrBuildingDetail(params.id).then(async (res) => {
        const fetchLogos = res.data.pictures.map(async (item) => {
          try {
            if (item.id) {
              const response = await service.getQrCodePic(params.id, item.id);
              item.logo = response.data;
            }
          } catch (e) {
            // 错误处理: 如果有需要，可以在这里添加错误处理逻辑
          }
        });

        // 等待所有的 logo 加载完成
        await Promise.all(fetchLogos);

        // 此时所有的 item.logo 都已被赋值，可以安全地更新 state
        setDetail(res.data);
      });
    }
  }, [params.id]);

  const [cameraProps, setCameraProps] = useState();
  const handleCameraChange = useCallback((ev) => setCameraProps(ev.detail));

  useEffect(() => {
    if (detailData?.location?.latitude !== null && detailData?.location !== undefined) {
      setCameraProps({
        center: { lat: detailData?.location?.latitude, lng: detailData?.location?.longitude },
      });

      return;
    }
    setCameraProps({
      center: { lat: 38.9072, lng: -77.0369 },
    });
  }, [detailData]);

  return (
    <>
      <div className="detailHeader">
        <div className="detailHeaderLeft">{detailData.name}</div>
      </div>

      <div className="detailInfo">
        <div className="detailInfoLeft">
          <div>
            <p>COMMUNITY OID</p>
            <p>LICENSE #</p>
            <p>EMAIL</p>
            <p>PHONE</p>
            <p>ADDRESS</p>
            <p> # OF UNITS</p>
            <p> # OF OPEN UNITS</p>
            <p>ORGANIZATION</p>
          </div>
          <div>
            <p>{detailData?.oid || "-"}</p>
            <p>{detailData?.licenseNumber || "-"}</p>
            <p>{detailData?.email || "-"}</p>
            <p>{detailData?.phone || "-"}</p>
            <p>{detailData?.displayAddress || "-"}</p>
            <p>{detailData?.numberOfBeds || "-"}</p>
            <p>{detailData?.numberOfVacantBeds || "-"}</p>
            <p>{detailData?.organizationName || "-"}</p>
          </div>
        </div>
        <div className="detailInfoRight">
          <div className="detailInfoRightImgLeft">
            <img
              src={
                detailData?.pictures?.[0]?.logo
                  ? `data:image/png;base64,${detailData?.pictures?.[0]?.logo}`
                  : defaultImg
              }
              alt=""
            />
          </div>

          <div className="detailInfoRightImgRight">
            <div>
              <img
                src={
                  detailData?.pictures?.[1]?.logo
                    ? `data:image/png;base64,${detailData?.pictures?.[1]?.logo}`
                    : defaultImg
                }
                alt=""
              />
            </div>
            <div>
              <img
                src={
                  detailData?.pictures?.[2]?.logo
                    ? `data:image/png;base64,${detailData?.pictures?.[2]?.logo}`
                    : defaultImg
                }
                alt=""
              />
            </div>
          </div>
        </div>
      </div>

      <div className="detailService">
        <div className="detailServiceTitle">SERVICE</div>

        <div className="detailSeriveWrap">
          {detailData.marketplace?.serviceNames.map((item, index) => {
            return (
              <div className="detailServiceDetail" key={index}>
                {item}
              </div>
            );
          })}

          <div className="detailServiceDetail">Bariatric Services</div>
        </div>
      </div>

      <div className="detailLanguages">
        <div className="detailLanguagesTitle">LANGUAGES</div>

        <div className="detailLanguagesDetails">
          {detailData?.languageServices?.length > 0 &&
            detailData.languageServices.map((item) => {
              return (
                <div className="detailLanguagesDetail" key={item.id}>
                  <img src={CheckedImg} alt="" />
                  <span>{item.displayName}</span>
                </div>
              );
            })}
        </div>
      </div>

      <div className="detailIntroduction">{detailData.introduction}</div>

      <div className="detailMap">
        <APIProvider apiKey={config.google.maps.apiKey}>
          <Map
            zoom={15}
            {...cameraProps}
            gestureHandling={"greedy"}
            disableDefaultUI={false}
            onCameraChanged={handleCameraChange}
            mapId={"8b15fd729aa51fc"}
          >
            {detailData?.location !== undefined && detailData?.location?.latitude !== null && (
              <MarkerWithInfoWindow
                position={{ lat: detailData?.location?.latitude, lng: detailData?.location?.longitude }}
                addressName={detailData?.name}
                address={`${detailData?.street} ${detailData?.city} ${detailData?.zipCode}`}
              />
            )}
          </Map>
        </APIProvider>
      </div>
    </>
  );
};

export default BuildingDetailLeft;
