import React, { memo, useEffect, useMemo, useState } from "react";

import cn from "classnames";

import { Badge } from "reactstrap";

import { MEDICATION_STATUSES } from "lib/Constants";

import { ErrorViewer, Loader } from "components";

import ClientMedications from "./ClientMedications/ClientMedications";
import ClientSummaryFallback from "../ClientSummaryFallback/ClientSummaryFallback";
import ClientMedicationBarChart from "./ClientMedicationBarChart/ClientMedicationBarChart";

import "./ClientMedicationsSummary.scss";
import service from "services/ClientMedicationService";
import Calendar from "react-calendar";
import moment from "moment";
import NoDataImg from "images/Clients/noData.svg";
import { MedicationViewer } from "../../Medications";
import { useLocation, useParams } from "react-router-dom";

const { ACTIVE } = MEDICATION_STATUSES;

function ClientMedicationsSummary({ clientId, className }) {
  let query = new URLSearchParams(useLocation().search);
  const medication = query.get("type");

  const [status, setStatus] = useState(ACTIVE);
  const [isErrorViewerOpen, toggleErrorViewer] = useState(false);
  const [error, setError] = useState([]);

  const [data, setData] = useState([]);
  const [isFetching, setIsFetching] = useState(false);

  const [medicationsTab, setMedicationsTab] = useState(0);

  const [dailyTracker, setDailyTracker] = useState([]);

  const [currentDailyTrackerData, setCurrentDailyTrackerData] = useState([]);

  const [allDailyTrackers, setAllDailyTrackers] = useState([]);

  const [haveStatusTime, setHaveStatusTime] = useState([]);

  const [activeStartDate, setActiveStartDate] = useState(new Date());

  const [currentMonth, setCurrentMonth] = useState([]);

  const [isViewerOpen, setIsViewerOpen] = useState(false);

  const [medicationId, setMedicationId] = useState("");

  const [calendarValue, setCalendarValue] = useState(moment());

  const [isDailyAllLoading, setIsDailyAllLoading] = useState(false);

  useEffect(() => {
    if (medication === "dailyTracker") {
      const timeoutId = setTimeout(() => {
        const element = document.querySelector(".ClientMedicationsSummary-Title");

        if (element) {
          element.scrollIntoView({ behavior: "smooth" });

          setMedicationsTab(1);
        }
      }, 1500); // 使用 0 毫秒的延时确保页面渲染完成后执行

      // 清除 timeout
      return () => clearTimeout(timeoutId);
    }
  }, [medication]);

  useEffect(() => {
    getMedicationStatisticsQuery();
  }, [clientId]);

  useEffect(() => {
    getMonthTimestamps(activeStartDate);
  }, [clientId, activeStartDate]);

  useEffect(() => {
    if (medicationsTab === 0) {
      return;
    }

    fetchMedicationsDailyAllData(clientId, currentMonth);
  }, [clientId, currentDailyTrackerData, currentMonth]);

  useEffect(() => {
    const result = processCalendarData(allDailyTrackers);

    setHaveStatusTime(result);
  }, [allDailyTrackers]);

  useEffect(() => {
    if (medicationsTab === 0) {
      setActiveStartDate(new Date());
      getStartAndEndTimestamps(moment());
      setCalendarValue(moment());
      setDailyTracker([]);
      setCurrentDailyTrackerData([]);
      setAllDailyTrackers([]);
      setHaveStatusTime([]);
      setCurrentMonth([]);
    }
    if (medicationsTab === 1) {
      getStartAndEndTimestamps(moment());
    }
  }, [medicationsTab]);

  useEffect(() => {
    const nowClickTime = moment(calendarValue).format("YYYY-MM-DD");
    const mathData = haveStatusTime.find((item) => item.scheduleDate === nowClickTime)?.info || [];
    setDailyTracker(mathData);
  }, [calendarValue, haveStatusTime]);

  const processCalendarData = (data) => {
    // 使用 Map 进行日期分组
    const acc = new Map();

    data.forEach(({ scheduleDateTime, status, name, medicationId, dosageQuantity, replyTime, id }) => {
      // 确保 scheduleDateTime 是有效时间戳
      if (!scheduleDateTime || isNaN(scheduleDateTime)) {
        return; // 跳过无效数据
      }

      // 使用 moment 转换时间戳为日期字符串
      const scheduleDate = moment(scheduleDateTime).format("YYYY-MM-DD");

      // 映射 status 值为字符串
      const statusString =
        status === 0
          ? "Null"
          : status === 1
            ? "Taken"
            : status === 2
              ? "Missed"
              : status === 3
                ? "No Response"
                : "Unknown";

      // 获取当前日期的记录或初始化新记录
      if (!acc.has(scheduleDate)) {
        acc.set(scheduleDate, {
          scheduleDate,
          status: new Set(), // 使用 Set 去重 status
          info: [], // 初始化 info 数组
        });
      }

      const record = acc.get(scheduleDate);

      // 添加 status 到 Set（去重）
      record.status.add(statusString);

      // 不再去重，直接添加 info
      record.info.push({ scheduleDateTime, status, name, medicationId, dosageQuantity, replyTime, id });
    });

    // 转换 Map 为数组，格式化 Set 为数组
    return Array.from(acc.values()).map((item) => ({
      ...item,
      status: Array.from(item.status), // 将 Set 转换为数组
    }));
  };

  function getMonthTimestamps(dateString) {
    // 创建 moment 对象
    const date = moment(dateString);

    // 获取当月的开始和结束日期
    const startOfMonth = date.clone().startOf("month");
    const endOfMonth = date.clone().endOf("month");

    // 获取时间戳
    const startTimestamp = startOfMonth.valueOf();
    const endTimestamp = endOfMonth.valueOf();
    setCurrentMonth([startTimestamp, endTimestamp]);
  }

  const handleActiveStartDateChange = ({ activeStartDate }) => {
    setActiveStartDate(activeStartDate);
  };

  const fetchMedicationsDailyAllData = (clientId, currentDailyTrackerData) => {
    if (currentDailyTrackerData.length === 0) {
      return;
    }

    setIsDailyAllLoading(true);
    const params = {
      deliveryStart: currentDailyTrackerData[0],
      deliveryEnd: currentDailyTrackerData[1],
    };
    service
      .fetchMedicationsDailyData(clientId, params)
      .then((res) => {
        setAllDailyTrackers(res);
        setIsDailyAllLoading(false);
      })
      .catch(() => {
        setIsDailyAllLoading(false);
      });
  };

  const getMedicationStatisticsQuery = () => {
    setIsFetching(true);
    service
      .statistics({ clientId })
      .then((res) => {
        setData(res);
        setIsFetching(false);
      })
      .catch((e) => {
        toggleErrorViewer(true);
        setError(e);
        setIsFetching(false);
      });
  };

  const count = useMemo(() => {
    return data?.reduce((accum, o) => {
      return (accum += o.value);
    }, 0);
  }, [data]);

  const onPickStatus = (status) => setStatus(MEDICATION_STATUSES[status]);

  function formattedTime(time) {
    const timeMoment = moment(time);

    // 格式化为指定的 12 小时制格式
    return timeMoment.format("hh:mm A");
  }

  function formattedTimeAll(time) {
    // 如果时间为 null，直接返回 null
    if (time === null) {
      return null;
    }
    const momentObj = moment(time);
    return momentObj.format("DD/MM/YYYY h:mm A");
  }

  function getMedicationsStatus(status) {
    if (status === 0) {
      return "Null";
    } else if (status === 1) {
      return "Taken";
    } else if (status === 2) {
      return "Missed";
    } else if (status === 3) {
      return "No Response";
    } else if (status === 4) {
      return "Unknown";
    }
  }

  function getStartAndEndTimestamps(dateString) {
    // 使用 moment 和 UTC 来解析输入的日期字符串

    const date = moment(dateString);

    // 获取当前日期的开始和结束时间
    const startOfDay = date.clone().startOf("day");
    const endOfDay = date.clone().endOf("day");

    // 获取时间戳
    const startTimestamp = startOfDay.valueOf();
    const endTimestamp = endOfDay.valueOf();

    // 设定数据，通过一个回调函数返回时间戳数组
    setCurrentDailyTrackerData([startTimestamp, endTimestamp]);
  }

  return (
    <div className={cn("ClientMedicationsSummary", className)}>
      <a name="medications-summary" href="#" />
      <div className="ClientMedicationsSummary-Title">
        <span className="ClientMedicationsSummary-TitleText">Medications</span>
        <Badge color="info" className="ClientMedicationsSummary-MedicationCount">
          {count}
        </Badge>
      </div>

      <ClientSummaryFallback isShown={!count} isLoading={isFetching}>
        <div className="ClientMedicationsSummary-Body">
          <div className={"Client-Medication-Tab"}>
            <div
              className={medicationsTab === 0 ? "Client-Medication-OverView-Tab-Click" : ""}
              onClick={() => setMedicationsTab(0)}
            >
              OVERVIEW
            </div>

            <div
              className={medicationsTab === 1 ? "Client-Medication-OverView-Tab-Click" : ""}
              onClick={() => setMedicationsTab(1)}
            >
              DAILY TRACKER
            </div>
          </div>

          {medicationsTab === 0 && (
            <div className="Client-Medication-OverView">
              <ClientMedicationBarChart
                data={data}
                status={status}
                onPickBar={onPickStatus}
                className="ClientMedicationsSummary-Chart flex-1"
              />
              <ClientMedications
                getMedicationStatisticsQuery={getMedicationStatisticsQuery}
                status={status}
                className="flex-1"
                clientId={clientId}
                onChangeStatus={onPickStatus}
              />
            </div>
          )}

          {medicationsTab === 1 && (
            <div className="Client-Medication-Daily">
              <div className="Client-Medication-Daily-CalendarBox">
                {isDailyAllLoading && <Loader className={"Client-Medication-Daily-CalendarLoading"} hasBackdrop />}
                <Calendar
                  locale={"en"}
                  view="month" // 默认视图为月视图
                  maxDetail="month"
                  prev2Label={null}
                  next2Label={null}
                  // defaultValue={moment()}
                  value={calendarValue}
                  onActiveStartDateChange={handleActiveStartDateChange}
                  tileClassName={({ date, view }) => {
                    // 为不属于当前显示月份的日期添加一个自定义class
                    if (view === "month" && date.getMonth() !== activeStartDate.getMonth()) {
                      return "not-current-month";
                    }
                  }}
                  onChange={(value, event) => {
                    setCalendarValue(value);
                    getStartAndEndTimestamps(value);
                    setDailyTracker([]);
                  }}
                  tileContent={({ date, view }) => {
                    if (view !== "month") {
                      return null;
                    }

                    const formattedDate = moment(date).format("YYYY-MM-DD");

                    const statusRecord = haveStatusTime.find((item) => item.scheduleDate === formattedDate);

                    if (statusRecord) {
                      return (
                        <div className="medicationsStatus">
                          {/* 渲染不同的状态圈 */}
                          {statusRecord.status.includes("Null") && <></>}
                          {statusRecord.status.includes("Taken") && <div className="Taken-circle"></div>}
                          {statusRecord.status.includes("Missed") && <div className="Missed-circle"></div>}
                          {statusRecord.status.includes("Unknown") && <div className="Unknown-circle"></div>}
                          {statusRecord.status.includes("No Response") && <div className="NoResponse-circle"></div>}
                        </div>
                      );
                    }

                    return null;
                  }}
                />
              </div>
              {/*  medications Details */}
              <div className="ClientMedicationDailyDetails">
                {dailyTracker.length === 0 ? (
                  <div className={"ClientMedicationDailyDetailsNoData"}>
                    <img src={NoDataImg} alt="" />
                    <div>No Data.</div>
                  </div>
                ) : (
                  dailyTracker.map((item, index) => {
                    return (
                      <div className="ClientMedicationDailyDetailsBox" key={item.id + index}>
                        <div
                          className="ClientMedicationDailyDetailsLeftTitle"
                          onClick={() => {
                            setMedicationId(item.medicationId);
                            setIsViewerOpen(true);
                          }}
                        >
                          {item.name}
                        </div>

                        <div className="ClientMedicationDailyDetailsInfo">
                          {getMedicationsStatus(item.status) !== "Null" && (
                            <div className="ClientMedicationDailyDetailsLeftStatus">
                              <span className={`${getMedicationsStatus(item.status).replace(/\s+/g, "") + "Status"}`}>
                                {getMedicationsStatus(item.status)}
                              </span>
                            </div>
                          )}

                          <div>
                            {item.dosageQuantity && <>Dosage: {item.dosageQuantity} tablet at </>}

                            {item.scheduleDateTime && <> {formattedTime(item.scheduleDateTime)}</>}
                          </div>

                          {formattedTimeAll(item.replyTime) && (
                            <div>Reply Time: {formattedTimeAll(item.replyTime)}</div>
                          )}
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
            </div>
          )}
        </div>
      </ClientSummaryFallback>

      <MedicationViewer
        isOpen={isViewerOpen}
        clientId={clientId}
        medicationId={medicationId}
        onClose={() => {
          setMedicationId("");
          setIsViewerOpen(false);
        }}
        isCanEdit={false}
      />

      {isErrorViewerOpen && <ErrorViewer isOpen error={error} onClose={() => toggleErrorViewer(false)} />}
    </div>
  );
}

export default memo(ClientMedicationsSummary);
