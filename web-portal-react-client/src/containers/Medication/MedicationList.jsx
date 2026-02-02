import "./index.scss";
import logo from "images/simplyconnect-logo.svg";
import MedicationTable from "./MedicationTable";
import { Link, useLocation } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import { useEffect, useState } from "react";
import SuccessDialog from "./SuccessDialog";
import { useQuery } from "@tanstack/react-query";
import service from "services/EmailMedicationService";
import { Loader } from "../../components";

const MedicationList = () => {
  const [showDialog, setShowDialog] = useState(false);
  const [unTakenData, setUnTakenData] = useState([]);
  const [takenData, setTakenData] = useState([]);

  const [isReply, setIsReply] = useState(false);

  const UserNameFn = (fullname) => {
    // 将全名按空格分割成数组
    const nameParts = fullname.trim().split(/\s+/);

    // 提取名字（第一个单词）
    const firstName = nameParts[0];

    // 提取姓氏的首字母（如果有姓氏）
    const lastInitial = nameParts.length > 1 ? nameParts[nameParts.length - 1].charAt(0).toUpperCase() : "";

    // 返回 "名字 姓氏首字母" 的格式
    return `${firstName} ${lastInitial}.`;
  };

  // 使用 useLocation 获取当前 location 对象
  const location = useLocation();

  // 使用 URLSearchParams 解析查询参数
  const searchParams = new URLSearchParams(location.search);

  // 获取 clientId 和 messageBatchIdentity
  const clientId = searchParams.get("clientId");
  const messageBatchIdentity = searchParams.get("messageBatchIdentity");
  const timeRange = searchParams.get("timeRange");

  // 使用 useQuery 获取数据
  const { data, isLoading } = useQuery({
    queryKey: ["emailMedicationList", clientId, messageBatchIdentity], // 将参数加入 queryKey
    queryFn: async () => {
      const result = await service.findEmailMedicationList(clientId, messageBatchIdentity);
      return result.data;
    },
    enabled: !!clientId && !!messageBatchIdentity, // 确保参数存在
  });

  useEffect(() => {
    if (!data || !data?.medications) {
      return;
    }

    if (data?.medications?.length === 0) {
      // 如果数据为空，清空状态
      setUnTakenData([]);
      setTakenData([]);
    } else {
      // 一次遍历，将数据分为两类
      const unTaken = [];
      const taken = [];

      const hasReplyTime = data.medications.some((item) => item?.replyTime);

      setIsReply(hasReplyTime);

      data.medications.forEach((medication) => {
        if (medication.replyTime) {
          taken.push(medication);
        } else {
          unTaken.push(medication);
        }
      });

      // 更新状态
      setUnTakenData(unTaken);
      setTakenData(taken);
    }
  }, [data]);

  return (
    <div className={"medicationWrap"}>
      <div className={"medicationHeader"}>
        <img src={logo} alt="" className={"logo"} />

        <Link to={path("/home")} className={"signInButton"}>
          Sign In
        </Link>
      </div>

      <div className="medicationContentWrap">
        {isLoading && <Loader isCentered hasBackdrop />}

        <div className={"medicationContent"}>
          <div className="medicationTime">{timeRange}</div>
          <div className="medicationTitle">Medication Intake Confirmation</div>

          <div className="medicationInfo">
            <div>Name: {UserNameFn(data?.fullName || "")}</div>
            <div>Gender: {data?.gender}</div>
            {/*<div>Date of Birth: {data?.birthDate}</div>*/}
          </div>

          <div className="medicationDescription">
            You can click the buttons below to reply with your medication intake status.
          </div>

          <div className="medicationTableWrap">
            <MedicationTable
              hasController={true}
              hasSelected={true}
              setShowDialog={setShowDialog}
              data={unTakenData}
              isReply={isReply}
              timeZone={data?.timeZone}
            />
          </div>

          <div className="medicationTakenList">
            <MedicationTable hasController={false} hasSelected={false} data={takenData} timeZone={data?.timeZone} />
          </div>
        </div>
      </div>

      <SuccessDialog shouldShow={showDialog} setShowDialog={setShowDialog} />
    </div>
  );
};

export default MedicationList;
