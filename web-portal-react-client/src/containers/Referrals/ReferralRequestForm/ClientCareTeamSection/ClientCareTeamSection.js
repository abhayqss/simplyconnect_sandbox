import "./ClientCareTeamSection.scss";
import { Col, Row } from "reactstrap";
import { CheckboxField, RadioGroupField, SelectField } from "../../../../components/Form";
import React, { useEffect, useState } from "react";
import { map } from "underscore";
import { Table } from "components";
import adminVendorService from "../../../../services/AdminVendorService";
import Avatar from "../../../Avatar/Avatar";
import cn from "classnames";
import { getProperty } from "../../../../lib/utils/ObjectUtils";

const _ = require("lodash");
const CREATE_CARE_TEAM_OPTIONS = [
  { label: "Yes", value: true },
  { label: "No", value: false },
];
const ClientCareTeamSection = ({ vendorId, errors, onChangeField, isClinicalVendor }) => {
  const [createCreateTeam, setCreateCreateTeam] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  let [vendorTeamData, setVendorTeamData] = useState([]);
  let [selectedMembers, setSelectedMembers] = useState([]);

  useEffect(() => {
    getVendorData({
      page: 0,
      size: 999,
      vendorId,
      active: true,
    });
  }, [vendorId, createCreateTeam]);
  const getVendorData = (params) => {
    setIsFetching(true);
    adminVendorService.viewVendorContactData(params).then((res) => {
      if (res.success) {
        const hasSelectData = res?.data?.map((item) => {
          return { ...item, checked: false };
        });
        setVendorTeamData(hasSelectData);
        setIsFetching(false);
      }
    });
  };
  const getRoleOptions = async (params) => {
    try {
      const res = await adminVendorService.findCareTeamRoles(params);
      if (res.success) {
        return res?.data?.map((item) => ({
          value: item.name,
          text: item.title,
        }));
      } else {
        return [];
      }
    } catch (error) {
      return [];
    }
  };

  const replaceItemsInArray = (A, B) => {
    for (let i = 0; i < A.length; i++) {
      for (let j = 0; j < B.length; j++) {
        if (A[i].id === B[j].id) {
          A[i] = B[j];
          break;
        }
      }
    }
  };

  useEffect(() => {
    if (errors.vendorCareTeams) {
      let data = vendorTeamData.map((item) => {
        return { ...item, someOneHasError: true };
      });
      setVendorTeamData(data);
    }
  }, [errors]);
  const changeTeamType = async (name, value) => {
    let itemSelect = selectedMembers.find((item) => item.id === name);
    itemSelect.roleOptions = await getRoleOptions({
      contactId: name,
      nonClinicalTeam: value === "nonClinical",
    });
    itemSelect.teamType = value;
    itemSelect.roleCode = itemSelect.roleCode ? null : undefined;
    itemSelect.canChangeSelected = true;
    itemSelect.checked = true;

    const flagData = vendorTeamData;
    replaceItemsInArray(flagData, selectedMembers);
    const data = _.cloneDeep(flagData);

    setVendorTeamData(data);

    onChangeField(
      "vendorCareTeams",
      selectedMembers.map((item) => {
        return {
          contactId: item.id,
          teamType: item.teamType,
          roleCode: item.roleCode,
        };
      }),
    );
  };

  const changeCareTeamRoleCode = (name, value) => {
    let itemSelect = selectedMembers.find((item) => item.id === name);
    itemSelect.roleCode = value;

    onChangeField(
      "vendorCareTeams",
      selectedMembers.map((item) => {
        return {
          contactId: item.id,
          teamType: item.teamType,
          roleCode: item.roleCode,
        };
      }),
    );
    const flagData = vendorTeamData;
    replaceItemsInArray(flagData, selectedMembers);
    setVendorTeamData(flagData);
  };

  const getVendorItemRole = (roleCode) => {
    let role;
    switch (roleCode) {
      case "ROLE_DOCTOR_CODE":
        role = "Doctor";
        break;
      case "ROLE_VENDOR_CODE":
        role = "Vendor";
        break;
      case "ROLE_PHARMACIST_VENDOR_CODE":
        role = "Pharmacist Vendor";
        break;
      default:
        role = "Vendor";
    }
    return role;
  };

  const changeCreateCareTeam = (name, value) => {
    setCreateCreateTeam(value);
    if (!value) {
      onChangeField("vendorCareTeams", []);
      setSelectedMembers([]);
      setVendorTeamData([]);
    }
  };
  const CLINICAL_ROLE_CODE = ["ROLE_PHARMACIST_VENDOR_CODE", "ROLE_DOCTOR_CODE"];
  const NON_CLINICAL_ROLE_CODE = ["ROLE_PREMIUM_CODE"];
  const onSelectTeamMember = (data) => {
    const index = selectedMembers?.findIndex((item) => {
      return item.id === data.id;
    });
    if (index === -1) {
      selectedMembers?.push({ ...data, checked: data.careTeamRoleCode === "ROLE_VENDOR_CODE" });
      // judge if is isClinicalVendor
      if (isClinicalVendor) {
        if (CLINICAL_ROLE_CODE.includes(data?.careTeamRoleCode)) {
          changeTeamType(data.id, "clinical").then(() => {
            changeCareTeamRoleCode(data.id, data.careTeamRoleCode);
            let newArray = vendorTeamData.map((item) => {
              if (item.id === data.id) {
                return {
                  ...item,
                  checked: true,
                  teamType: "clinical",
                  roleCode: item.careTeamRoleCode,
                  canChangeSelected: false,
                };
              } else return item;
            });
            setVendorTeamData(newArray);
          });
        } else {
          onChangeField(
            "vendorCareTeams",
            selectedMembers?.map((item) => {
              return {
                contactId: item.id,
                teamType: item.teamType,
                roleCode: item.roleCode,
                roleOptions: [
                  { value: "ROLE_DOCTOR_CODE", text: "Doctor" },
                  { value: "ROLE_PHARMACIST_VENDOR_CODE", text: "Pharmacist Vendor" },
                ],
              };
            }),
          );
          let newArray = vendorTeamData.map((item) => {
            if (item.id === data.id) {
              return {
                ...item,
                checked: true,
                canChangeSelected: true,
                roleOptions: [
                  { value: "ROLE_DOCTOR_CODE", text: "Doctor" },
                  { value: "ROLE_PHARMACIST_VENDOR_CODE", text: "Pharmacist Vendor" },
                ],
              };
            } else return item;
          });
          setVendorTeamData(newArray);
        }
      } else {
        // not isClinicalVendor
        changeTeamType(data.id, "nonClinical").then(() => {
          changeCareTeamRoleCode(data.id, data.careTeamRoleCode);
          let newArray = vendorTeamData.map((item) => {
            if (item.id === data.id) {
              return {
                ...item,
                checked: true,
                teamType: "clinical",
                roleCode: item.careTeamRoleCode,
                canChangeSelected: false,
              };
            } else return item;
          });
          setVendorTeamData(newArray);
        });
      }
    } else {
      const newArray = vendorTeamData.map((item) => {
        if (item.id === data.id) {
          return { ...item, checked: false, roleCode: null, teamType: null };
        } else {
          return item;
        }
      });

      setVendorTeamData(newArray);
      selectedMembers?.splice(index, 1);

      onChangeField(
        "vendorCareTeams",
        selectedMembers?.map((item) => {
          return {
            contactId: item.id,
            teamType: item.teamType,
            roleCode: item.roleCode,
          };
        }),
      );
    }
  };

  const VENDOR_TEAM_COLUMNS = [
    {
      dataField: "fullName",
      text: "Member",
      headerClasses: "VendorsTeamMemberList-Header-MemberName",
      formatter: (v, row, index, formatExtraData, isMobile) => {
        return (
          <div className="d-flex align-items-center">
            <Avatar name={v} id={row.avatarId} className="VendorsTeamMemberList-MemberAvatar" />

            <div className="VendorsTeamMemberList-Member margin-left-10">
              <div id={`${isMobile ? "m-" : ""}contact-${row.id}`} className={cn("VendorsTeamMemberList-MemberName")}>
                {row.fullName}
              </div>
              <div className="VendorsTeamMemberList-MemberRelation">{getVendorItemRole(row.careTeamRoleCode)}</div>
            </div>
          </div>
        );
      },
    },
    {
      dataField: "login",
      text: "Email",
      align: "left",
      headerAlign: "left",
      formatter: (v, row) => {
        return (
          <div title={v} className="VendorTeamMemberList-Email">
            {v}
          </div>
        );
      },
    },
    {
      dataField: "teamType",
      text: "Team Type",
      align: "left",
      headerAlign: "left",
      formatter: (v, row) => {
        const index = selectedMembers.findIndex((item) => item.id === row.id);
        return (
          <>
            <SelectField
              type={"text"}
              name={row.id}
              value={v}
              placeholder={"Select"}
              isDisabled={!row.checked}
              options={
                isClinicalVendor
                  ? [
                      {
                        text: "Clinical",
                        value: "clinical",
                      },
                      {
                        text: "Non-Clinical ",
                        value: "nonClinical",
                      },
                    ]
                  : [
                      {
                        text: "Non-Clinical ",
                        value: "nonClinical",
                      },
                    ]
              }
              onChange={changeTeamType}
              errorText={getProperty(errors, `vendorCareTeams.${index}.teamType`)}
            />
          </>
        );
      },
    },
    {
      dataField: "roleCode",
      text: "Team Role",
      align: "left",
      headerAlign: "left",
      formatter: (v, row) => {
        const index = selectedMembers.findIndex((item) => item.id === row.id);
        return (
          <>
            <SelectField
              value={v}
              name={row.id}
              isDisabled={!row.checked}
              placeholder={"Select"}
              onChange={changeCareTeamRoleCode}
              options={row?.roleOptions}
              errorText={getProperty(errors, `vendorCareTeams.${index}.roleCode`)}
            />
          </>
        );
      },
    },
  ];
  return (
    <>
      <div className="ReferralRequestForm-little-Section">
        <Row>
          <Col md={12}>
            <RadioGroupField
              title="Whether to join the care team*"
              name="vendor.createCareTeam"
              value={createCreateTeam}
              options={CREATE_CARE_TEAM_OPTIONS}
              onChange={changeCreateCareTeam}
              className="ReferralRequestForm-RadioGroupField"
            />
          </Col>
        </Row>

        {createCreateTeam && (
          <Table
            hasHover
            hasPagination={false}
            keyField="id"
            isLoading={isFetching}
            data={vendorTeamData}
            noDataText={"No Team Member"}
            className={"VendorTeamList"}
            containerClass="VendorTeamListContainer"
            columns={VENDOR_TEAM_COLUMNS}
            columnsMobile={["fullName", "login"]}
            title={"Team"}
            selectedRows={{
              mode: "checkbox",
              selected: map(selectedMembers, (c) => c.id),
              onSelect: onSelectTeamMember,
              selectionRenderer: ({ checked, disabled }) => {
                return <CheckboxField value={checked} isDisabled={disabled} className={"little-checkBox"} />;
              },
              selectionHeaderRenderer: () => <div style={{ display: "none" }}></div>,
            }}
          />
        )}
      </div>
    </>
  );
};

export default ClientCareTeamSection;
