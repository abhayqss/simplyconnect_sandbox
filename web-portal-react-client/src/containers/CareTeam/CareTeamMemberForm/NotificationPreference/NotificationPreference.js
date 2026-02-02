import React, { memo, useEffect, useMemo, useRef, useState } from "react";

import { Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import { Popover } from "reactstrap";

import { ResponsibilityInfo } from "components/info";

import SelectField from "components/Form/SelectField/SelectField";

import { NOTIFICATION_RESPONSIBILITY_TYPES } from "lib/Constants";

import { ReactComponent as Info } from "images/info.svg";

import "./NotificationPreference.scss";

import playImg from "images/Admin/play.svg";
import Slider from "@mui/material/Slider";
import { debounce } from "lodash";
import moment from "moment/moment";
import { Checkbox } from "@mui/material";
import { Tooltip as MuiTooltip, tooltipClasses } from "@mui/material";
import { styled } from "@mui/material/styles";

const { VIEWABLE, NOT_VIEWABLE } = NOTIFICATION_RESPONSIBILITY_TYPES;

const COVID19 = "COVID19";

const LightTooltip = styled(({ className, ...props }) => <MuiTooltip {...props} classes={{ popper: className }} />)(
  ({ theme }) => ({
    [`& .${tooltipClasses.tooltip}`]: {
      backgroundColor: theme.palette.common.white,
      color: "#7D7E7F",
      boxShadow: theme.shadows[1],
      fontSize: 14,
      width: "200px",
      textAlign: "center",
    },
    [`& .MuiTooltip-arrow`]: {
      color: theme.palette.common.white, // 设置箭头颜色为白色
      "&::before": {
        backgroundColor: theme.palette.background.paper,
        boxShadow: "0px 0px 4px rgba(0, 0, 0, 0.2)", // 这里是三角形的阴影
      },
    },
  }),
);

const ThresholdText =
  "If the patient's medication response is marked as 'some,' the pharmacist will receive an alert. The default setting is 'checked' (alert enabled). If unchecked, the pharmacist will not be alerted.";

function NotificationPreference({
  data,
  name,
  title,
  errors,
  channels,
  showLabel,
  isDisabledResp,
  isDisabledChannel,
  placeholder,
  onChangeChannel,
  responsibilities,
  onChangeResponsibility,

  onChangeTimeRange,
  onChangeThreshold,
  missedMedicationReminderId,
  thresholdToPass,
  shouldHidePmmaEvent,
}) {
  const target = useRef(null);

  const popoverRef = useRef(null); // 用来获取 Popover 的引用

  const [isPopoverOpen, setPopoverOpen] = useState(false);

  // 默认百分比 70
  const [sliderValue, setSliderValue] = useState(70);

  const medicationOmissionReminder = ["BLUE_STONE", "SECURITY_MESSAGE", "SMS"];

  useEffect(() => {
    if (data.eventTypeId === Number(missedMedicationReminderId)) {
      setSliderValue(data?.ratio || 70);
    }
    return () => {
      setSliderValue(70);
    };
  }, [data]);

  function checkResponsibility(data) {
    const includeResponsibilityName = ["A", "C", "I"];
    return includeResponsibilityName.includes(data);
  }

  // 切换 Popover 的可见状态
  const togglePopover = () => {
    setPopoverOpen(!isPopoverOpen);
  };

  const handleClickOutside = (event) => {
    // 如果点击目标是 Popover 或 Popover 内部内容，不执行任何操作
    if (popoverRef.current && popoverRef.current.contains(event.target)) {
      return; // 防止在点击目标区域时关闭 Popover
    }

    // 关闭 Popover
    setPopoverOpen(false);
  };

  useEffect(() => {
    if (isPopoverOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    // 清除effect
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isPopoverOpen]);

  if (name === COVID19) {
    responsibilities = responsibilities.map((o) => ({
      ...o,
      isDisabled: [VIEWABLE, NOT_VIEWABLE].includes(o.name) ? true : o.isDisabled,
    }));
  }

  let methodsOptions = channels;
  if (data.eventTypeId === Number(missedMedicationReminderId)) {
    methodsOptions = channels.map((o) => ({
      ...o,
      isDisabled: medicationOmissionReminder.includes(o.value),
    }));
  }

  // 使用 useMemo 缓存防抖函数
  const debouncedOnChangeTimeRange = useMemo(
    () =>
      debounce((eventTypeId, newValue) => {
        onChangeTimeRange(eventTypeId, newValue);
      }, 300),
    [],
  ); // 依赖项为空数组，确保防抖函数只创建一次

  const handleSliderChange = (event, newValue) => {
    setSliderValue(newValue);
  };

  const handleSliderChangeState = (event, newValue) => {
    debouncedOnChangeTimeRange(data.eventTypeId, newValue);
  };

  return (
    <Row className="NotificationPreference">
      <Col md={6} className="NotificationPreference-Title">
        {title}
        {data.eventTypeId === Number(missedMedicationReminderId) && checkResponsibility(data.responsibilityName) && (
          <>
            <div ref={target} id="missedAlerts" className={"missedAlerts"} onClick={togglePopover}>
              <img src={playImg} alt="" />

              <span>Threshold Setting</span>
            </div>

            <Popover
              isOpen={isPopoverOpen}
              target={target.current}
              toggle={togglePopover}
              className="missedAlertsPopover"
              placement="top"
            >
              <div ref={popoverRef} className={"popoverBox"}>
                <div className={"missedAlertsTitle"}>Percentage of times taken per week</div>
                <div className={"missedAlertsRatioTitle"}>
                  Ratio Settings <span>*</span>
                </div>

                <div className="missedAlertsRatioBox">
                  <Slider
                    defaultValue={70}
                    value={sliderValue}
                    aria-label="Default"
                    valueLabelDisplay="auto"
                    onChange={handleSliderChange}
                    onChangeCommitted={handleSliderChangeState}
                  />

                  <div className="missedAlertsRatioBoxText">{sliderValue}%</div>
                </div>

                {!shouldHidePmmaEvent && (
                  <div className="thresholdWrap">
                    <div className="thresholdWrapLeft">
                      <div className="thresholdTitle">Threshold</div>
                      <LightTooltip title={ThresholdText} arrow placement={"top"}>
                        <Info />
                      </LightTooltip>
                    </div>

                    <div>
                      <Checkbox
                        checked={thresholdToPass}
                        onChange={() => {
                          onChangeThreshold(!thresholdToPass);
                        }}
                      />
                    </div>
                  </div>
                )}
              </div>
            </Popover>
          </>
        )}
      </Col>

      <Col md={3}>
        <SelectField
          label={showLabel ? "Responsibility" : ""}
          className="NotificationPreference-Select"
          placeholder={placeholder ?? "Nothing selected"}
          name={`${data.eventTypeId}-responsibility`}
          value={data.responsibilityName}
          options={responsibilities}
          onChange={(name, value) => {
            onChangeResponsibility(data.eventTypeId, value);
          }}
          renderLabelIcon={() => (
            <Info id={`responsibility-info-hint-${name}`} className="NotificationPreference-SelectLabelIcon" />
          )}
          isDisabled={isDisabledResp}
          hasError={!!errors?.responsibilityName}
          errorText={errors?.responsibilityName}
        />
        {showLabel && (
          <Tooltip
            trigger="hover click"
            boundariesElement={document.body}
            className="ResponsibilityInfoHint"
            target={`responsibility-info-hint-${name}`}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 6] },
              },
              {
                name: "preventOverflow",
                options: { boundary: document.body },
              },
            ]}
          >
            <ResponsibilityInfo />
          </Tooltip>
        )}
      </Col>

      <Col md={3}>
        <SelectField
          isMultiple
          label={showLabel ? "Channel" : ""}
          className="NotificationPreference-Select"
          placeholder={placeholder ?? "Nothing selected"}
          name={`${data.eventTypeId}-channel`}
          value={data.channels}
          options={methodsOptions}
          onChange={(name, value) => {
            onChangeChannel(data.eventTypeId, value);
          }}
          hasAllOption={data.eventTypeId !== Number(missedMedicationReminderId)}
          errorText={errors?.channels}
          hasError={!!errors?.channels}
          isDisabled={isDisabledChannel || [VIEWABLE, NOT_VIEWABLE].includes(data.responsibilityName)}
        />
      </Col>
    </Row>
  );
}

export default memo(NotificationPreference);
