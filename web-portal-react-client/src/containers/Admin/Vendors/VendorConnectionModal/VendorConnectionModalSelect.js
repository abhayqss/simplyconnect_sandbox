import "./VendorConnectionModal.scss";
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
import {
  findVendorAssociateCommunities,
  findVendorAssociateOrganizations,
  findVendorContactData,
} from "../../../../redux/vendorAdmin/vendorListActions";
import { useDispatch, useSelector } from "react-redux";

const VendorConnectionModalSelect = (props) => {
  const { setStepOneSelectedData, stepOneSelectedData, isFetching, tab } = props;
  const scrollableStyles = { flex: 1 };

  const [searchName, setSearchName] = useState("");
  const [communitiesData, setCommunitiesData] = useState([]);
  const [dataTotal, setDataTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [communitiesDataPagination, setCommunitiesDataPagination] = useState();
  const [selectedList, setSelectedList] = useState([]);
  const dispatch = useDispatch();

  useEffect(() => {
    setSelectedList(stepOneSelectedData);
  }, [stepOneSelectedData]);
  const defaultSelectList = {
    data: [
      {
        id: 1,
        name: "Aegis Living Bellevue",
        communityOID: 965465,
        bids: 23,
        orgName: "Aegis Senior Communities, LLC",
        state: "American Samoa(AS)",
      },
      {
        id: 2,
        name: "Aegis Living Dana Point",
        communityOID: 9635465,
        bids: 223,
        orgName: "Aegis Senior Communities, LLC",
        state: "American Samoa(AS)",
      },
      {
        id: 3,
        name: "Aegis of San Fr",
        communityOID: 965465,
        bids: 323,
        orgName: "Aegis Senior Communities, LLC",
        state: "American Samoa(AS)",
      },
    ],
    pagination: {
      total: 20,
      page: 1,
      size: 10,
    },
  };
  const allAssociateCommunities = useSelector((state) => state.adminVendor.allAssociateCommunities);
  const allAssociateOrganizations = useSelector((state) => state.adminVendor.allAssociateOrganizations);
  const allAssociateCommunitiesTotal = useSelector((state) => state.adminVendor.allAssociateCommunitiesTotal);
  const allAssociateOrganizationsTotal = useSelector((state) => state.adminVendor.allAssociateOrganizationsTotal);
  const [sort, setSort] = useState();
  const onSort = (field, order) => {
    setSort(`${field},${order}`);
  };

  useEffect(() => {
    const params = {
      page: page - 1,
      size: 10,
      sort,
      // communityOrgName: searchName,
    };
    if (tab === 0) {
      dispatch(findVendorAssociateCommunities({ ...params, communityOrgName: searchName }));
    } else if (tab === 1) {
      dispatch(findVendorAssociateOrganizations({ ...params, name: searchName }));
    }
  }, [page, tab, sort, searchName]);

  const CommunitiesTable = [
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
    {
      dataField: "stateName",
      text: "State",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "180px",
      },
    },
  ];

  const OrganizationsTable = [
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
    {
      dataField: "stateName",
      text: "State",
      headerAlign: "left",
      align: "left",
      headerStyle: {
        width: "180px",
      },
      formatter: (v, row) => {
        return (
          <div className="Association-connection-modal-substance">
            <span className={"MultiSelect-Template-Text"}>{v ? v : "-"}</span>
          </div>
        );
      },
    },
  ];
  const TABLE_LIST = [CommunitiesTable, OrganizationsTable];

  useEffect(() => {
    if (tab === 0) {
      if (allAssociateCommunitiesTotal > 0) {
        setDataTotal(allAssociateCommunitiesTotal);
        setCommunitiesData(allAssociateCommunities);
        setCommunitiesDataPagination({
          page: page,
          size: 10,
          totalCount: allAssociateCommunitiesTotal,
        });
      } else {
        setDataTotal(0);
        setCommunitiesData([]);
      }
    } else if (tab === 1) {
      if (allAssociateOrganizationsTotal > 0) {
        setDataTotal(allAssociateOrganizationsTotal);
        setCommunitiesData(allAssociateOrganizations);
        setCommunitiesDataPagination({
          page: page,
          size: 10,
          totalCount: allAssociateOrganizationsTotal,
        });
      } else {
        setDataTotal(0);
        setCommunitiesData([]);
      }
    }
  }, [
    allAssociateCommunities,
    allAssociateOrganizations,
    allAssociateOrganizationsTotal,
    allAssociateCommunitiesTotal,
    page,
  ]);

  const { data: states = [] } = useStatesQuery();
  const valueTextMapper = ({ id, name, value, label, title }) => {
    return { value: id || value || name, text: label || title || name };
  };
  const mappedStates = useMemo(() => map(states, valueTextMapper), [states]);

  const onClearSearchName = () => {
    setSearchName("");
  };
  const onChangeSearchName = (name, value) => {
    setPage(1);
    setSearchName(value);
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
                placeholder={tab === 0 ? "Search by community or org.name" : "Search by name"}
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
            data={communitiesData}
            noDataText="No Data"
            pagination={communitiesDataPagination}
            onRefresh={(num) => setPage(num)}
            columns={TABLE_LIST[tab]}
            columnsMobile={["name", "orgName"]}
            selectedRows={{
              mode: "checkbox",
              hideSelectColumn: false,
              selected: map(selectedList, (c) => c?.id),
              onSelect: onChooseItem,
              onSelectAll: onSelectAllTableItem,
              nonSelectable: chain(defaultSelectList.data)
                .where({ isActive: false, canView: false })
                .pluck("id")
                .value(),
              selectionRenderer: ({ checked, disabled }) => <CheckboxField value={checked} isDisabled={disabled} />,
              selectionHeaderRenderer: () => (
                <CheckboxField
                  value={
                    !isFetching &&
                    isNotEmpty(defaultSelectList.data) &&
                    every(defaultSelectList.data, (d) => !!find(selectedList, (c) => c.id === d.id))
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
export default VendorConnectionModalSelect;
