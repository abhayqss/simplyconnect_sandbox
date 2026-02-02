import { Loader, Table } from "../../components";
import { chain, every, filter, find, map } from "underscore";
import { CheckboxField } from "../../components/Form";
import { isNotEmpty } from "../../lib/utils/Utils";
import React, { useEffect, useState } from "react";
import "./index.scss";
import { UncontrolledTooltip as Tooltip } from "reactstrap";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useLocation } from "react-router-dom";
import service from "services/EmailMedicationService";
import { ErrorDialog, SuccessDialog } from "../../components/dialogs";
import moment from "moment-timezone";

const MedicationTable = ({ hasController = false, hasSelected = false, data, isReply: isReply, timeZone }) => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const clientId = searchParams.get("clientId");
  const messageBatchIdentity = searchParams.get("messageBatchIdentity");

  const [selectedList, setSelectedList] = useState([]);

  const [missedTooltipOpen, setMissedTooltipOpen] = useState(false);
  const [showErrorDialog, setShowErrorDialog] = useState(false);
  const [successDialog, setSuccessDialog] = useState(false);

  const [showAllButton, setShowAllButton] = useState(true);

  const missedToggle = () => setMissedTooltipOpen(!missedTooltipOpen);

  const queryClient = useQueryClient();

  useEffect(() => {
    if (isReply) {
      setShowAllButton(false); // isReply 为 true，直接设置为 false
    } else {
      // isReply 为 false
      if (selectedList.length === 0 || selectedList.length === data.length) {
        setShowAllButton(true); // 选中数量为 0 或等于 data 数量，设置为 true
      } else {
        setShowAllButton(false); // 其他情况设置为 false
      }
    }
  }, [selectedList, isReply, data.length]);

  const mutation = useMutation({
    mutationFn: async (body) => {
      return service.changeMedicationStatus(clientId, messageBatchIdentity, body);
    },
    onSuccess: () => {
      setSelectedList([]);
      setSuccessDialog(true);
      queryClient.invalidateQueries(["emailMedicationList", clientId, messageBatchIdentity]);
    },
    onError: () => {
      setShowErrorDialog(true);
    },
  });

  const onChooseItem = (tableItem, isSelected) => {
    setSelectedList((prevSelectIdList) => {
      if (isSelected) {
        return [...prevSelectIdList, tableItem];
      } else {
        return prevSelectIdList.filter((item) => item.id !== tableItem.id);
      }
    });
  };

  const onSelectAllTableItem = (isSelected, medications) => {
    setSelectedList(
      isSelected
        ? [...selectedList, ...filter(medications, (medication) => !find(selectedList, (c) => c.id === medication.id))]
        : filter(selectedList, (c) => !find(medications, (medication) => medication.id === c.id)),
    );
  };

  const columns = [
    {
      dataField: "name",
      text: "Medication Name",
      sort: false,
      formatter: (v, row) => {
        return (
          <div className="">
            <span className={"nameStyle"}>{v}</span>
          </div>
        );
      },
    },
    {
      dataField: "scheduleDateTime",
      text: "Scheduled Time",
      sort: false,
      align: "center",
      headerStyle: {
        width: "30%",
        textAlign: "center",
      },
      formatter: (v, row) => {
        // 获取用户时区，默认 UTC
        let userTimeZone = timeZone || "UTC";

        // 检查时区是否有效，无效则使用 UTC
        if (!moment.tz.zone(userTimeZone)) {
          userTimeZone = "UTC";
        }

        // 将时间戳（毫秒）转换为 moment 对象，假设时间戳是 UTC
        const utcDate = moment.utc(parseInt(v));

        // 如果时间戳无效，返回错误提示
        if (!utcDate.isValid()) {
          return (
            <div className="time-container">
              <span className="timeStyle error">' '</span>
            </div>
          );
        }

        // 转换为用户时区
        const convertedDate = utcDate.tz(userTimeZone);
        const formattedDate = convertedDate.format("MM/DD/YYYY hh:mm A");
        // const timeZoneAbbr = convertedDate.format("z");

        return (
          <div className="">
            <span className={"timeStyle"}>{formattedDate}</span>
          </div>
        );
      },
    },

    {
      dataField: "status",
      text: "Status",
      sort: false,
      align: "center",
      headerStyle: {
        width: "20%",
        textAlign: "center",
      },
      formatter: (v, row) => {
        const statusString = v === 1 ? "Taken" : v === 2 ? "Missed" : "Unknown";

        return (
          <div className="">
            <span className={`${statusString}StatusStyle`}>{statusString}</span>
          </div>
        );
      },
    },
  ];

  return (
    <>
      {mutation.isLoading && <Loader hasBackdrop isCentered />}
      <Table
        keyField="id"
        hasCaption={false}
        className="medicationTable"
        containerClass="AssociationCommunitiesListContainer"
        data={data}
        noDataText="No Data"
        columns={columns}
        columnsMobile={["name"]}
        onRefresh={(num) => {
          setPage(num);
        }}
        selectedRows={{
          mode: "checkbox",
          hideSelectColumn: !hasSelected,
          selected: map(selectedList, (c) => c.id),
          onSelect: onChooseItem,
          onSelectAll: onSelectAllTableItem,
          nonSelectable: chain(data).where({ isActive: false, canView: false }).pluck("id").value(),
          selectionRenderer: ({ checked, disabled }) => <CheckboxField value={checked} isDisabled={disabled} />,
          selectionHeaderRenderer: () => (
            <CheckboxField
              className={"medicationTableAllCheckbox"}
              value={isNotEmpty(data) && every(data, (d) => !!find(selectedList, (c) => c.id === d.id))}
            />
          ),
        }}
      />

      {hasController && data.length > 0 && (
        <div className="controller">
          <div
            className={`outlineButton ${showAllButton && "outlineButtonDisabled"}`}
            id="SomeMissed"
            onClick={() => {
              if (selectedList.length === 0) {
                return;
              }

              const body = {
                intake: "SOME",
                medDeliveryIds: selectedList.map((item) => item.id),
              };
              mutation.mutate(body);
            }}
          >
            Took Selected
          </div>

          <div
            className={`outlineButton ${!showAllButton && "outlineButtonDisabled"}`}
            onClick={() => {
              if (!showAllButton) {
                return;
              }

              setSelectedList([...data]);
              const body = {
                intake: "NO",
                medDeliveryIds: [],
              };

              mutation.mutate(body);
            }}
          >
            Took None
          </div>

          <div
            className={`button ${!showAllButton && "buttonDisabled"}`}
            onClick={() => {
              if (!showAllButton) {
                return;
              }

              setSelectedList([...data]);
              const body = {
                intake: "YES",
                medDeliveryIds: [],
              };

              mutation.mutate(body);
            }}
          >
            Took All
          </div>

          <Tooltip
            isOpen={missedTooltipOpen}
            target="SomeMissed"
            toggle={missedToggle}
            delay={300}
            transition={{ timeout: 300 }}
            modifiers={[
              {
                name: "offset",
                options: { offset: [0, 12] },
              },
            ]}
            className="medicationTooltip"
          >
            Select the medications and click to confirm.
          </Tooltip>
        </div>
      )}

      {showErrorDialog && (
        <ErrorDialog
          isOpen
          title={mutation.error?.message || "error"}
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setShowErrorDialog(false);
              },
            },
          ]}
        />
      )}

      {successDialog && (
        <SuccessDialog
          isOpen
          title={"Thank you for your reply"}
          text={"Your response has been logged in the Simply Connect platform."}
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setSuccessDialog(false);
              },
            },
          ]}
        />
      )}
    </>
  );
};

export default MedicationTable;
