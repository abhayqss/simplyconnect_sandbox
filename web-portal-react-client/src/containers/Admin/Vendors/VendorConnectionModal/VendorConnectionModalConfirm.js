import { Loader, Table } from "../../../../components";
import { chain, every, filter, find, map } from "underscore";
import { CheckboxField } from "../../../../components/Form";
import { isNotEmpty } from "../../../../lib/utils/Utils";
import React, { useEffect, useState } from "react";

const VendorConnectionModalConfirm = (props) => {
  const { stepOneSelectedData, setStepOneSelectedData, isSubmitting, setStepTwoSelectedId, stepTwoSelectedId } = props;
  const [selectedData, setSelectedData] = useState();
  const [selectedDataPagination, setSelectedDataPagination] = useState({
    size: stepOneSelectedData.length,
    page: 1,
    totalCount: stepOneSelectedData.length,
  });
  useEffect(() => {
    setSelectedData(stepOneSelectedData);
  }, [stepOneSelectedData]);

  const onChooseItem = (tableItem, isSelected) => {
    /**
     * 判断 已选内容是否含有该id 含有则删除 不包含则添加
     * 然后给 selectIdList重新赋值
     */
    // 使用回调函数形式的 setSelectIdList 来避免闭包问题
    setSelectedData((prevSelectIdList) => {
      if (isSelected) {
        return [...prevSelectIdList, tableItem];
      } else {
        return prevSelectIdList.filter((item) => item.id !== tableItem.id);
      }
    });
    setStepOneSelectedData((prevSelectIdList) => {
      if (isSelected) {
        return [...prevSelectIdList, tableItem];
      } else {
        return prevSelectIdList.filter((item) => item.id !== tableItem.id);
      }
    });
  };

  const onSelectAllTableItem = (isSelected, clients) => {
    setSelectedData(
      isSelected
        ? [...selectedData, ...filter(clients, (client) => !find(selectedData, (c) => c.id === client.id))]
        : filter(selectedData, (c) => !find(clients, (client) => client.id === c.id)),
    );
    setStepOneSelectedData(
      isSelected
        ? [...selectedData, ...filter(clients, (client) => !find(selectedData, (c) => c.id === client.id))]
        : filter(selectedData, (c) => !find(clients, (client) => client.id === c.id)),
    );
  };
  const onSort = (field, order) => {
    // data.sort(field, order)
    console.log("sort");
  };

  return (
    <>
      {isSubmitting && <Loader hasBackdrop style={{ position: "fixed" }} />}
      <Table
        hasPagination
        keyField="id"
        className="AllergyList"
        containerClass="AssociationCommunitiesListContainer"
        // data={communitiesData}
        data={selectedData}
        noDataText="No Data"
        pagination={selectedDataPagination}
        onRefresh={fetch}
        columns={[
          {
            dataField: "name",
            text: "Name",
            sort: true,
            headerStyle: {
              width: "240px",
            },
            onSort: onSort,
            formatter: (v, row) => {
              return (
                <div className="Association-connection-modal-substance">
                  <span className={"MultiSelect-Template-Text"}>{v}</span>
                </div>
              );
            },
          },
          {
            dataField: "oid",
            text: "Community OID",
            sort: true,
            onSort: onSort,
          },
          /*  {
                    dataField: 'orgname',
                    text: 'Org.Name',
                    headerAlign: 'left',
                    align: 'left',
                    headerStyle: {
                        width: '270px',
                    },
                },*/
          {
            dataField: "stateName",
            text: "State",
            headerAlign: "left",
            align: "left",
            sort: true,
            onSort: onSort,
            headerStyle: {
              width: "180px",
            },
          },
          /* {
                    dataField: 'bids',
                    text: 'Bids',
                    headerAlign: 'left' ,
                    align: 'left',
                    sort: true,
                    onSort: onSort,
                    headerStyle: {
                        width: '70px',
                    },
                }*/
        ]}
        columnsMobile={["name", "orgName"]}
        selectedRows={{
          mode: "checkbox",
          hideSelectColumn: false, //
          selected: map(selectedData, (c) => c.id),
          onSelect: onChooseItem,
          onSelectAll: onSelectAllTableItem,
          selectionRenderer: ({ checked, disabled }) => <CheckboxField value={checked} isDisabled={disabled} />,
          selectionHeaderRenderer: () => (
            <CheckboxField
              value={
                !isSubmitting &&
                isNotEmpty(stepOneSelectedData) &&
                every(stepOneSelectedData, (d) => !!find(selectedData, (c) => c.id === d.id))
              }
            />
          ),
        }}
      />
    </>
  );
};
export default VendorConnectionModalConfirm;
