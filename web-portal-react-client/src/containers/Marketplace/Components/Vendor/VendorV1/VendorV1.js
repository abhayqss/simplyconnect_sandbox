import "./VendorV1.scss";
import { CheckboxField } from "components/Form";
import PerxxImg from "../../../../../images/marketplace/perxx.svg";
import NotifyNurseCallImg from "../../../../../images/marketplace/notifyNurseCall.svg";
import NotifyImg from "../../../../../images/marketplace/notify.svg";
import { useDispatch, useSelector } from "react-redux";
import React, { useEffect, useState } from "react";
import { getVendorType } from "../../../../../redux/marketplace/Vendor/VendorActions";
import services from "../../../../../services/OrganizationService";
import service from "../../../../../services/Marketplace";
import defaultImg from "../../../../../images/public/vendorBigLogo.svg";
import { useHistory } from "react-router-dom";
import { Loader } from "../../../../../components";
import { useAuthUser } from "../../../../../hooks/common";
import { SYSTEM_ROLES } from "../../../../../lib/Constants";
import adminAssociationsService from "../../../../../services/AssociationsService";

const VendorV1 = () => {
  const dispatch = useDispatch();
  const history = useHistory();
  const [selectedIds, setSelectedIds] = useState([]);
  const [vendorList, setVendorList] = useState([]);
  const [isFetching, setIsFetching] = useState(false);

  const VendorTypeData = useSelector((state) => state.vendor.vendorTypeData);
  const user = useAuthUser();
  const { ASSOCIATION } = SYSTEM_ROLES;
  const [isAssociation, setIsAssociation] = useState(false);
  useEffect(() => {
    if (user.roleName === ASSOCIATION) {
      setIsAssociation(true);
    } else {
      setIsAssociation(false);
    }
  }, [user]);

  useEffect(() => {
    dispatch(getVendorType());
  }, []);
  // 取消vendorType全选
  /*  useEffect(() => {
    if (VendorTypeData?.length) {
      setSelectedIds(VendorTypeData.map((item) => item.id));
    }
  }, [VendorTypeData]);*/

  useEffect(() => {
    // 如果类型都不选就都不展示
    if (selectedIds.length === 0) {
      if (isAssociation) {
        const params = {
          vendorTypeIds: "",
          page: 0,
          size: 9,
        };
        getAssociationVendorData(params);
      } else {
        const params = {
          vendorTypeIds: "",
          primaryType: true,
        };
        getVendorList(params);
      }
    } else {
      // 将 selectedIds 用逗号分隔成一个字符串
      const result = _.join(selectedIds, ",");
      // 将该字符串作为参数传递
      if (isAssociation) {
        const params = {
          vendorTypeIds: result,
          page: 0,
          size: 9,
        };
        getAssociationVendorData(params);
      } else {
        const params = {
          vendorTypeIds: result,
        };

        getVendorList(params);
      }
    }
  }, [selectedIds, isAssociation]);

  const getAssociationVendorData = (params) => {
    adminAssociationsService.marketPlaceFindAssociationsVendor(params).then(async (res) => {
      if (res.success) {
        const fetchLogos = res?.data?.map(async (item) => {
          item.vendorId = item.id;
          try {
            if (item.logo) {
              // 请求 Logo 数据并赋值给 item.logo
              await service.getVendorLogo(item.vendorId).then((response) => {
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

        // 对获取到的数据进行处理
        let results = res.data || [];

        // 截取前9条数据，如果不足9条则补充到9条
        results = results.slice(0, 9);
        while (results.length < 9) {
          results.push({}); // 在此处可以根据需要选择其他默认值
        }

        // 更新供应商列表
        setVendorList(results);
        setIsFetching(false);
      }
    });
  };
  const getVendorList = (params) => {
    setIsFetching(true);

    // 调用服务获取供应商的信息
    services.featVendorOfAssociation(params).then(async (res) => {
      // 获取供应商的 Logo
      const fetchLogos = res?.data?.map(async (item) => {
        try {
          if (item.logo) {
            // 请求 Logo 数据并赋值给 item.logo
            await service.getVendorLogo(item.vendorId).then((response) => {
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

      // 对获取到的数据进行处理
      let results = res.data || [];

      // 截取前9条数据，如果不足9条则补充到9条
      results = results.slice(0, 9);
      while (results.length < 9) {
        results.push({}); // 在此处可以根据需要选择其他默认值
      }

      // 更新供应商列表
      setVendorList(results);
      setIsFetching(false);
    });
  };

  const handleCheckboxChange = (itemId) => {
    setSelectedIds((prevSelectedIds) => {
      if (prevSelectedIds.includes(itemId)) {
        // 如果已选中，则去除该id
        return prevSelectedIds.filter((id) => id !== itemId);
      } else {
        // 如果未选中，则增加该id
        return [...prevSelectedIds, itemId];
      }
    });
  };

  return (
    <div className="vendorWrap">
      {isFetching && <Loader hasBackdrop style={{ position: "fixed" }} />}
      <div className="vendorSidWrap">
        {VendorTypeData?.map((item) => (
          <CheckboxField
            key={item.id}
            label={item.name}
            name={item.name}
            value={selectedIds.includes(item.id)}
            tooltip={{ text: item.name }}
            onChange={() => handleCheckboxChange(item.id)}
          />
        ))}
      </div>

      <div className="vendorCenterWrap">
        <div className="vendorLeftWrap">
          {vendorList?.map((item, index) => (
            <div
              key={`${item.vendorId}${index}`}
              className={item.vendorId ? "vendorListBox" : "vendorListBox vendorListBoxNone"}
              onClick={() => {
                if (item.vendorId) {
                  history.push(`/web-portal/marketplace/vendorDetail/${item.vendorId}`);
                }
              }}
            >
              <img src={item?.logo ? `data:image/png;base64,${item?.logo}` : defaultImg} alt="" />

              <div className="vendorName">{item?.vendorName || item?.name || "-"}</div>
            </div>
          ))}
        </div>

        <div className="vendorRightWrap">
          <div>
            <img src={PerxxImg} alt="" />
          </div>
          <div>
            <img src={NotifyNurseCallImg} alt="" />
          </div>
          <div>
            <img src={NotifyImg} alt="" />
          </div>
        </div>
      </div>
    </div>
  );
};

export default VendorV1;
