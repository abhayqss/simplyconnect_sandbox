import "./AssociationConnectionModal.scss";
import { Button, Col, Row } from "reactstrap";
import React, { useEffect, useMemo, useState } from "react";
import { chain, every, filter, find, map } from "underscore";
import Scrollable from "../../../../components/Calendar/Scrollable/Scrollable";
import { Loader } from "../../../../components";
import { useStatesQuery } from "../../../../hooks/business/directory/query";

import { Table, SearchField } from "components";
import { CheckboxField, SelectField } from "components/Form";
import { isNotEmpty } from "../../../../lib/utils/Utils";
import adminVendorService from "../../../../services/AdminVendorService";
import adminAssociationsService from "../../../../services/AssociationsService";

const AssociationConnectionModalSelect = (props) => {
  const { setStepOneSelectedData, stepOneSelectedData, tab } = props;
  const scrollableStyles = { flex: 1 };

  const [page, setPage] = useState(1);
  const [searchName, setSearchName] = useState("");
  const [searchState, setSearchState] = useState("");
  const [allData, setAllData] = useState([]);
  const [allDataPagination, setAllDataPagination] = useState({
    page: 1,
    size: 10,
    totalCount: 0,
  });
  const [isFetching, setIsFetching] = useState(false);
  const [selectedList, setSelectedList] = useState(stepOneSelectedData || []);
  const [sort, setSort] = useState();

  useEffect(() => {
    setIsFetching(true);
    if (tab === 0) {
      const params = {
        page: page - 1,
        size: 10,
        sort,
        communityOrgName: searchName,
      };
      adminVendorService.viewVendorAssociateCommunities(params).then((res) => {
        if (res.success) {
          setAllData(res.data);
          setAllDataPagination({
            page: page,
            size: 10,
            totalCount: res.totalCount,
          });
        }
        setIsFetching(false);
      });
    } else if (tab === 1) {
      const params = {
        page: page - 1,
        size: 10,
        sort,
        name: searchName,
      };
      adminAssociationsService.viewAssociationsVendorList(params).then((res) => {
        if (res.success) {
          setAllData(res.data);
          setAllDataPagination({
            page: page,
            size: 10,
            totalCount: res.totalCount,
          });
        }
        setIsFetching(false);
      });
    } else if (tab === 2) {
      const params = {
        page: page - 1,
        size: 10,
        sort,
        name: searchName,
      };
      adminVendorService.viewVendorAssociateOrganizations(params).then((res) => {
        if (res.success) {
          setAllData(res.data);
          setAllDataPagination({
            page: page,
            size: 10,
            totalCount: res.totalCount,
          });
        }
        setIsFetching(false);
      });
    }
  }, [tab, sort, page, searchName]);

  const changeState = (name, value) => {
    setSearchState(value);
  };

  const { data: states = [] } = useStatesQuery();
  const valueTextMapper = ({ id, name, value, label, title }) => {
    return { value: id || value || name, text: label || title || name };
  };
  const mappedStates = useMemo(() => map(states, valueTextMapper), [states]);

  const onClearSearchName = () => {
    setSearchName("");
  };
  const onChangeSearchName = (name, value) => {
    setSearchName(value);
    setPage(1);
  };

  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };

  const onChooseItem = (tableItem, isSelected) => {
    /**
     * 判断 已选内容是否含有该id 含有则删除 不包含则添加
     * 然后给 selectIdList重新赋值
     */
    // 使用回调函数形式的 setSelectIdList 来避免闭包问题
    setSelectedList((prevSelectIdList) => {
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
    setSelectedList(
      isSelected
        ? [...selectedList, ...filter(clients, (client) => !find(selectedList, (c) => c.id === client.id))]
        : filter(selectedList, (c) => !find(clients, (client) => client.id === c.id)),
    );
    setStepOneSelectedData(
      isSelected
        ? [...selectedList, ...filter(clients, (client) => !find(selectedList, (c) => c.id === client.id))]
        : filter(selectedList, (c) => !find(clients, (client) => client.id === c.id)),
    );
  };

  const COMMUNITYCOLUMNS = [
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
          <div className="Association-connection-modal-substance Association-no-long-240">
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
      formatter: (v, row) => {
        return <div className="Association-no-long-240">{v}</div>;
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
      // sort: true,
      // onSort: onSort,
      headerStyle: {
        width: "180px",
      },
      formatter: (v, row) => {
        return <div className="Association-no-long-180">{v}</div>;
      },
    },
    /*{
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
  ];

  const COMMUNITYCOLUMNSMOBILE = ["name", "stateTitle"];
  const VENDORCOLUMNS = [
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
    /* {
      dataField: "vendorTypeId",
      text: "Category",
      // sort:true,
      // onSort: onSort,
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance">
            <span className={"MultiSelect-Template-Text"}>{row.vendorType?.name}</span>
          </div>
        );
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
      sort: true,
      onSort: onSort,
      formatter: (v) => {
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
    {
      dataField: "stateName",
      text: "State",
      /*    sort:true,
               onSort: onSort,*/
    },
  ];
  const ORGANIZATIONCOLUMNSMOBILE = ["name", "state"];
  const AllColumns = [COMMUNITYCOLUMNS, VENDORCOLUMNS, ORGANIZATIONCOLUMNS];
  const columnsMobile = [COMMUNITYCOLUMNSMOBILE, VENDORCOLUMNSMOBILE, ORGANIZATIONCOLUMNSMOBILE];
  const placeHolderList = ["Search by community or org.name", "Search by name", "Search by name"];
  return (
    <>
      <Scrollable style={scrollableStyles}>
        {isFetching && <Loader hasBackdrop style={{ position: "fixed" }} />}
        <div className="Association-connection-modal-select-filter">
          <Row>
            <Col md={6} lg={6}>
              <SearchField
                name="name"
                value={searchName}
                placeholder={placeHolderList[tab]}
                onClear={onClearSearchName}
                onChange={onChangeSearchName}
              />
            </Col>
          </Row>
          <Table
            hasPagination
            keyField="id"
            className="AllergyList"
            containerClass="AssociationCommunitiesListContainer"
            data={allData}
            noDataText="No Data"
            pagination={allDataPagination}
            columns={AllColumns[tab]}
            columnsMobile={columnsMobile[tab]}
            onRefresh={(num) => {
              setPage(num);
            }}
            selectedRows={{
              mode: "checkbox",
              hideSelectColumn: false,
              selected: map(selectedList, (c) => c.id),
              onSelect: onChooseItem,
              onSelectAll: onSelectAllTableItem,
              nonSelectable: chain(allData).where({ isActive: false, canView: false }).pluck("id").value(),
              selectionRenderer: ({ checked, disabled }) => <CheckboxField value={checked} isDisabled={disabled} />,
              selectionHeaderRenderer: () => (
                <CheckboxField
                  value={
                    !isFetching &&
                    isNotEmpty(allData) &&
                    every(allData, (d) => !!find(selectedList, (c) => c.id === d.id))
                  }
                />
              ),
            }}
          />
        </div>
      </Scrollable>
    </>
  );
};
export default AssociationConnectionModalSelect;
