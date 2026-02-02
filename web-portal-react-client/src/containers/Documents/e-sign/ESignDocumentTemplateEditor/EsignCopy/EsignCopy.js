import { Modal } from "../../../../../components";
import { Button, Col, Row } from "reactstrap";
import { SelectField, TextField } from "../../../../../components/Form";
import React, { useEffect, useMemo, useState } from "react";
import "./EsignCopy.css";
import { useForm } from "../../../../../hooks/common";
import Entity from "../../../../../entities/e-sign/ESignDocumentTemplateOrganization";
import Validator from "../../../../../validators/ESignDocumentTemplateOrganizationFormValidator";
import { useOrganizationsQuery } from "../../../../../hooks/business/directory/query";
import { useSelector } from "react-redux";
import { map } from "../../../../../lib/utils/ArrayUtils";
import { reject } from "underscore";
import server from "../../../../../services/DirectoryService";
import documentService from "../../../../../services/DocumentFolderService";
import esignService from "../../../../../services/EsignService";
import { ErrorDialog, SuccessDialog } from "../../../../../components/dialogs";
import Loader from "../../../../../components/Loader/Loader";

const EsignCopy = (props) => {
  const { isOpen, onClose, organizationId, editMultipleSignaturesRowData } = props;
  const [templateName, setTemplateName] = useState("");
  const [orgSelect, setOrgSelect] = useState();
  const [templateTypeSelect, setTemplateTypeSelect] = useState();
  const [communityList, setCommunityList] = useState([]);
  const [communitySelect, setCommunitySelect] = useState([]);
  const [assignData, setAssignData] = useState([]);
  const [assignAllData, setAssignAllData] = useState([]);
  const [folderNameSelect, setFolderNameSelect] = useState([]);
  const [canSubmit, setCanSubmit] = useState(false);
  const [successAdd, setSuccessAdd] = useState(false);
  const [errorAdd, setErrorAdd] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const string = editMultipleSignaturesRowData?.title.split(".")[0];
    setTemplateName(string);
  }, [editMultipleSignaturesRowData]);

  useEffect(() => {
    if (communitySelect.length > 0) {
      setFolderNameSelect(Array(communitySelect.length).fill(null));
    }
  }, [communitySelect.length]);

  useEffect(() => {
    if (assignData.length <= 0) {
      return;
    }

    const params = {
      communityIds: communitySelect,
      types: "TEMPLATE",
    };

    documentService.find(params).then((res) => {
      const formattedArrayB = res.map((item) => ({
        text: item.name,
        value: item.id,
        communityId: item.communityId,
      }));

      const mergedArray = assignData.map((itemA) => {
        // 找出所有 communityId 与 itemA.value 相同的 arrayB 的元素
        const relatedBItems = formattedArrayB.filter((itemB) => itemB.communityId === itemA.value);

        // 返回合并后的对象
        return {
          ...itemA,
          folderItems: relatedBItems,
        };
      });

      const values = mergedArray.reduce((acc, entry) => {
        entry.folderItems.forEach((item) => {
          if (item.text === "E-sign Documents and Forms") {
            acc.push(item.value);
          }
        });
        return acc;
      }, []);

      setFolderNameSelect(values);

      setAssignAllData(mergedArray);
    });
  }, [assignData]);

  useEffect(() => {
    if (orgSelect) {
      const params = {
        organizationId: orgSelect,
      };
      server.findCommunities(params).then((res) => {
        const result = res.data.map((item) => {
          return {
            value: item.id,
            text: item.name,
          };
        });

        setCommunityList(result);
      });
    }
  }, [orgSelect]);

  const { data: organizations, isFetching: isFetchingOrganizations } = useOrganizationsQuery(
    { isESignEnabled: true },
    { staleTime: 0 },
  );

  const mappedOrganizations = useMemo(
    () =>
      map(
        reject(organizations, (o) => o.id === organizationId),
        (o) => ({ text: o.label, value: o.id }),
      ),
    [organizations, organizationId],
  );

  const templateTypeList = [
    {
      value: 1,
      text: "Community",
    },
    {
      value: 2,
      text: "Organization",
    },
  ];

  const changeName = (name, value) => {
    setTemplateName(value);
  };
  const orgChange = (name, value) => {
    setOrgSelect(value);
    setCommunitySelect([]);
    setTemplateTypeSelect(null);
    setAssignAllData([]);
    setCanSubmit(false);
  };

  const templateTypeChange = (name, value) => {
    setCommunitySelect([]);
    setAssignAllData([]);
    setCanSubmit(false);
    setTemplateTypeSelect(value);

    if (value === 2) {
      const deepCopyCommunityList = _.cloneDeep(communityList); // 使用 cloneDeep 方法进行深拷贝

      // 然后将深拷贝后的对象传递给 setAssignData 方法
      setAssignData(deepCopyCommunityList);

      // setAssignData(communityList);
      // 设置成选中所有community
      const result = communityList.map((item) => {
        return item.value;
      });

      setCommunitySelect(result);
    }
  };

  function findCommunities(communityList, communitySelectValues) {
    // Use the filter method to filter communityList based on the communitySelectValues array.
    return communityList.filter((community) => communitySelectValues.includes(community.value));
  }

  const communityNameChange = (name, value) => {
    setCanSubmit(false);
    setAssignAllData([]);
    setCommunitySelect(value);

    const filter = findCommunities(communityList, value);
    setAssignData(filter);
  };

  function containsEmptyValue(array) {
    // 检查数组中是否存在 null, undefined 或空字符串
    return array.some((item) => item === null || item === undefined || item === "");
  }

  const folderNameChange = (name, value, index) => {
    const arr = [...folderNameSelect];

    arr[index] = value;

    setFolderNameSelect(arr);
  };

  useEffect(() => {
    if (folderNameSelect.length === 0) {
      setCanSubmit(false);
      return;
    }
    const can = containsEmptyValue(folderNameSelect);
    setCanSubmit(!can);
  }, [folderNameSelect]);

  const submit = () => {
    setLoading(true);

    const matchedData = folderNameSelect.map((selectedValue) => {
      for (const community of assignAllData) {
        const folderItem = community.folderItems.find((item) => item.value === selectedValue);
        if (folderItem) {
          return {
            folderId: folderItem.value,
            communityId: folderItem.communityId,
          };
        }
      }
      return null; // 如果没有找到匹配项
    });

    const params = {
      applicationKey: editMultipleSignaturesRowData.applicationKey,
      documentUrls: [editMultipleSignaturesRowData.documentUrl],
      title: templateName,
      organizationId: organizationId,
      communityIds: communitySelect,
      configuration: {
        folders: matchedData,
      },
    };

    try {
      esignService.copyMultiPersonSignatureTemplate(params).then((res) => {
        if (res.success) {
          setSuccessAdd(true);
          setLoading(false);
        } else {
          setErrorAdd(true);
          setLoading(false);
        }
      });
    } catch (e) {
      setErrorAdd(true);
      setLoading(false);
    }
  };

  return (
    <>
      <Modal
        hasCloseBtn
        isOpen={isOpen}
        onClose={onClose}
        className="ESignDocumentTemplateEditor"
        title={"Copy template"}
        hasFooter={true}
        renderFooter={() => {
          return (
            <>
              <Button color="success" disabled={!canSubmit || loading} onClick={submit}>
                Save
              </Button>
            </>
          );
        }}
      >
        <div className={"ESignCopy"}>
          {loading && <Loader hasBackdrop={true} className={"eSingCopyLoad"} />}
          <Row>
            <Col>
              <TextField
                type="text"
                name="name"
                label="name*"
                value={templateName}
                // label={`${fieldType} Label*`}
                // errorText={errors.label}
                maxLength={200}
                onChange={changeName}
                isDisabled={loading}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <SelectField
                label="Organization*"
                name="organizationId"
                value={orgSelect}
                options={mappedOrganizations}
                hasTooltip
                hasKeyboardSearch
                hasKeyboardSearchText
                // isDisabled={isFetchingOrganizations}
                placeholder="Select"
                onChange={orgChange}
                isDisabled={loading}
              />
            </Col>
          </Row>

          {orgSelect && (
            <>
              <Row>
                <Col>
                  <SelectField
                    label="Template Type*"
                    name="templateType"
                    value={templateTypeSelect}
                    options={templateTypeList}
                    placeholder="Select"
                    onChange={templateTypeChange}
                    isDisabled={loading}
                  />
                </Col>
              </Row>

              {templateTypeSelect === 1 && (
                <Row>
                  <Col>
                    <SelectField
                      label="Community Name*"
                      name="communityName"
                      value={communitySelect}
                      options={communityList}
                      isMultiple
                      placeholder="Select"
                      onChange={communityNameChange}
                      isDisabled={loading}
                    />
                  </Col>
                </Row>
              )}

              {communitySelect && assignAllData.length > 0 && (
                <>
                  <Row>
                    <Col>Assign Template to Folder</Col>
                  </Row>

                  <Row>
                    {/* 根据选中的community 来 指定 目录 */}

                    {assignAllData.map((item, index) => {
                      return (
                        <React.Fragment key={item.value}>
                          <Row key={item.value}>
                            <Col md={6} lg={6}>
                              <SelectField
                                label="Community Name*"
                                name="community"
                                value={item.value}
                                options={assignAllData}
                                isDisabled={true}
                              />
                            </Col>

                            <Col md={6} lg={6}>
                              <SelectField
                                label="Folder Name*"
                                name="folderName"
                                value={folderNameSelect[index]}
                                options={item.folderItems}
                                placeholder="Select"
                                onChange={(e, v) => folderNameChange(e, v, index)}
                                isDisabled={loading}
                              />
                            </Col>
                          </Row>
                        </React.Fragment>
                      );
                    })}
                  </Row>
                </>
              )}
            </>
          )}
        </div>
      </Modal>

      <SuccessDialog
        isOpen={successAdd}
        title={"Template copied successfully."}
        buttons={[{ text: "Ok", onClick: () => onClose() }]}
      />

      <ErrorDialog
        isOpen={errorAdd}
        title={"Template copy failed."}
        buttons={[
          {
            text: "Ok",
            onClick: () => {
              setErrorAdd(false);
            },
          },
        ]}
      />
    </>
  );
};

export default EsignCopy;
