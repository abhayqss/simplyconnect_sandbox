import { Loader, Table } from "../../../../components";
import { chain, every, filter, find, map } from "underscore";
import { CheckboxField } from "../../../../components/Form";
import { isNotEmpty } from "../../../../lib/utils/Utils";
import React, { useEffect, useState } from "react";

const AssociationConnectionModalConfirm = (props) => {
  const { tab, stepOneSelectedData, setStepOneSelectedData, isSubmitting, selectedData, setSelectedData } = props;
  useEffect(() => {
    setSelectedData(stepOneSelectedData);
  }, []);

  const [selectedDataPagination, setSelectedDataPagination] = useState({
    size: stepOneSelectedData.length,
    page: 1,
    totalCount: stepOneSelectedData.length,
  });

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
  const onSort = (field, order) => {};

  const COMMUNITYCOLUMNS = [
    {
      dataField: "name",
      text: "Name",
      headerStyle: {
        width: "240px",
      },
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance Association-no-long-240">
            <span className={"MultiSelect-Template-Text"}>{v}</span>
          </div>
        );
      },
    },
    {
      dataField: "oid",
      text: "Community OID",
      formatter: (v, row) => {
        return <div className="Association-no-long-180">{v}</div>;
      },
    },
    {
      dataField: "orgname",
      text: "Org.Name",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "270px",
      },
      formatter: (v, row) => {
        return <div className="Association-no-long-270">{v}</div>;
      },
    },
    {
      dataField: "stateTitle",
      text: "State",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "180px",
      },
      formatter: (v, row) => {
        return <div className="Association-no-long-180">{v}</div>;
      },
    },
  ];

  const COMMUNITYCOLUMNSMOBILE = ["name", "stateTitle"];
  const VENDORCOLUMNS = [
    {
      dataField: "name",
      text: "Name",
      headerStyle: {
        width: "240px",
      },
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance">
            <span className={"MultiSelect-Template-Text"}>{v}</span>
          </div>
        );
      },
    },
    /*{
            dataField: 'vendorTypeId',
            text: 'Category',
            // sort:true,
            // onSort: onSort,
            formatter: (v, row) => {
                return (
                    <div  className='Association-connection-modal-substance' >
                         <span className={'MultiSelect-Template-Text'}>
                               {row.vendorType?.name}
                          </span>
                    </div>
                )
            },
        },*/
    {
      dataField: "premium",
      text: "Premium",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "270px",
      },
      formatter: (v, row) => {
        return <>{v ? "Yes" : "No"}</>;
      },
    },
  ];
  const VENDORCOLUMNSMOBILE = ["name", "premium"];

  const ORGANIZATIONCOLUMNS = [
    {
      dataField: "name",
      text: "Name",
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance">
            <span className={"MultiSelect-Template-Text"}>{v}</span>
          </div>
        );
      },
    },
    {
      dataField: "stateName",
      text: "State",
    },
  ];
  const ORGANIZATIONCOLUMNSMOBILE = ["name", "state"];

  const AllColumns = [COMMUNITYCOLUMNS, VENDORCOLUMNS, ORGANIZATIONCOLUMNS];
  const columnsMobile = [COMMUNITYCOLUMNSMOBILE, VENDORCOLUMNSMOBILE, ORGANIZATIONCOLUMNSMOBILE];

  return (
    <>
      <Table
        hasPagination
        keyField="id"
        className="AllergyList"
        containerClass="AssociationCommunitiesListContainer"
        data={selectedData}
        noDataText="No Data"
        pagination={selectedDataPagination}
        columns={AllColumns[tab]}
        columnsMobile={columnsMobile[tab]}
        selectedRows={{
          mode: "checkbox",
          hideSelectColumn: false,
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
export default AssociationConnectionModalConfirm;
