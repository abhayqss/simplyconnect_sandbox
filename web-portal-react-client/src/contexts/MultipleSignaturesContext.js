import { Button, Col, Form, Row } from "reactstrap";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { CheckboxField, RadioGroupField, SelectField, TextField } from "../components/Form";
import { useForm, useScrollable } from "hooks/common";
import Entity from "../entities/e-sign/MultipleESignRequest";
import Validator from "../validators/MultopleSignatureRequestFormValidator";
import "./ESign.scss";
import service from "services/DocumentESignService";
import ClientService from "services/ClientService";
import { ErrorDialog } from "components/dialogs";
import { chain, findWhere } from "underscore";
import { useSystemRolesQuery } from "../hooks/business/directory/query";
import { useQueryClient } from "@tanstack/react-query";
import EsignMultipleSignaturesPreview from "../containers/Documents/e-sign/ESignDocumentTemplateEditor/ESignMultipleSignatures/EsignMultipleSignaturesPreview";
import SelectFieldTree from "../components/Form/SelectFieldTree/SelectField";
import { useSelector } from "react-redux";
import { Loader } from "../components";
import SelectFieldLevel from "../components/Form/SelectFieldLevel/SelectFieldLevel";

const MultipleSignaturesContext = (props) => {
  const { fields, errors, changeField } = useForm("MultipleSignatureRequest", Entity, Validator);
  const {
    onClose,
    onSubmitSuccess,
    defaultOrganizationId,
    isSingleSignature = false,
    singleClientId,
    singleClientEmail,
    setIsClientSignatureRequestSuccessDialogOpen,
  } = props;

  const data = useMemo(() => fields.toJS(), [fields]);
  const [orgOptions, setOrgOptions] = useState([]);
  const [communityOptions, setCommunityOptions] = useState([]);
  const [templateOptions, setTemplateOptions] = useState([]);
  const [templateList, setTemplateList] = useState([]);
  const [selectedCommunityIds, setSelectedCommunityIds] = useState([]);
  const [selectTemplateId, setSelectTemplateId] = useState();
  const [clientsNum, setClientsNum] = useState(0);
  const [clientOptions, setClientOptions] = useState([]);
  const [contactOptions, setContactOptions] = useState([]);
  const [clientContactOptions, setClientContactOptions] = useState([]);

  const [hasClientError, setHasClientError] = useState(false);
  const [hasClientNumberError, setHasClientNumberError] = useState(false);
  const [selectedTemplate, setSelectedTemplate] = useState(); // 所选定模板
  const [organizationId, setOrganizationId] = useState();
  const [selectClient, setSelectClient] = useState([]);
  const clientsNumError = hasClientError ? `This template selects at most ${clientsNum} signatures` : "";
  const clientsSelectNumError = hasClientNumberError ? `Please select at least one client` : "";

  const [isShowTemplatePreview, setIsShowTemplatePreview] = useState(false);
  const [showSendErrorDialog, setShowSendErrorDialog] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [templateSignList, setTemplateSignList] = useState([]);

  const [allSignOptions, setAllSignOptions] = useState([]);
  const [allSignOptions2, setAllSignOptions2] = useState([]);

  let [signOrderNotPOAEmailList, setSignOrderNotPOAEmailList] = useState([]);
  let [signOrderEmailList, setSignOrderEmailList] = useState([]);
  const [onlyClientIds, setOnlyClientIds] = useState();
  const [onlyContactIds, setOnlyContactIds] = useState();
  let [clientUsePoaList, setClientUsePoaList] = useState([]);
  let [signOrderIdList, setSignOrderIdList] = useState([]);
  let [clientIdAndEmails, setClientIdAndEmails] = useState([]);
  let [allSelectOptionHasCareTeam, setAllSelectOptionHasCareTeam] = useState([]);
  const [showClientError, setShowClientError] = useState(false);

  //  Signature
  const [showRecipient, setShowRecipient] = useState(false);
  const [showLoading, setShowLoading] = useState(false);

  const recipientOptions = [
    { value: "CLIENT", label: "Client" },
    { value: "SELF", label: "Self" },
    { value: "STAFF", label: "Staff/Family Member" },
  ];

  const signNowOptions = [
    { value: true, label: "YES" },
    { value: false, label: "NO" },
  ];

  const [clickRecipient, setClickRecipient] = useState("CLIENT");
  const [singleEmail, setSingleEmail] = useState(singleClientEmail);
  const [staffOptions, setStaffOptions] = useState([]);
  const [selectedSingleReceiver, setSelectedSingleReceiver] = useState();
  const [signNowType, setSignNowType] = useState(false);

  const [subject, setSubject] = useState("Your Document is Ready for Review");
  const [message, setMessage] = useState("");
  const [levelOptions, setLevelOptions] = useState({});

  const changeSingleEmail = (name, value) => {
    setSingleEmail(value);
  };

  useEffect(() => {
    // Get all organizations and get the currently selected organization.
    service.findAllOrganizations().then((res) => {
      const list = res.map((item) => {
        return {
          value: item.id,
          text: item.label,
        };
      });

      setOrgOptions(list);

      changeField("organizationId", defaultOrganizationId);
      setOrganizationId(defaultOrganizationId);
      service.findCommunities({ organizationId: defaultOrganizationId }).then((res) => {
        const list = res.map((item) => {
          return {
            value: item.id,
            text: item.title,
          };
        });
        setCommunityOptions(list);
      });
    });
  }, [defaultOrganizationId]);

  useEffect(() => {
    //  合并数据
    if (clientsNum === 1) {
      setClientContactOptions([...clientOptions]);
    } else {
      setClientContactOptions([...clientOptions, ...contactOptions]);
    }
  }, [clientOptions, contactOptions]);

  useEffect(() => {
    //   设置 默认的message
    const currentDate = new Date();

    // 格式化日期为 MM/DD/YYYY 格式
    const formattedDate =
      (currentDate.getMonth() + 1).toString().padStart(2, "0") +
      "/" +
      currentDate.getDate().toString().padStart(2, "0") +
      "/" +
      currentDate.getFullYear();
    setMessage(`Please sign/review the document(s) by ${formattedDate}.
Please reach out to me if you have any questions.
${user.fullName}. ${user.email}`);
  }, []);

  const getDocumentMultipleTemplate = (ids) => {
    service
      .findMultipleESignRequestedDocuments({
        communityIds: ids,
        isManuallyCreated: true,
        single: isSingleSignature,
      })
      .then((res) => {
        setTemplateList(res);
      });
  };
  const getDocumentMultipleTemplateDetail = (id) => {
    service.findMultipleESignRequestedDocumentsDetail(id).then((res) => {
      setSelectedTemplate(res);
      setClientsNum(res?.submittersJson?.length || 0);
      setTemplateSignList(res?.submittersJson || []);
      changeField("submitters", res?.submittersJson || []);
    });
  };

  const getClientsOptions = (ids) => {
    ClientService.find({
      name: "",
      size: 272485555,
      filter: {
        communityIds: ids,
        organizationId,
        recordStatuses: "ACTIVE",
        sort: "fullName,asc",
      },
    }).then((res) => {
      const filterEmail = res.data
        ?.filter((item) => item.email && item.email.trim() !== "" && item.canView)
        .map((value) => {
          return {
            value: value.id,
            text: value.fullName,
            email: value.email,
            type: "Client",
            typeId: 0,
            hasPOA: value.hasPOA,
            usePOA: value.hasPOA || false,
            powerOfAttorneys: value.hasPOA ? value.powerOfAttorneys : [],
          };
        });
      setClientOptions(filterEmail);
    });
  };

  const queryClient = useQueryClient();

  const organizations = queryClient.getQueryData(["Directory.Organizations", null]);

  const organization = findWhere(organizations, { id: organizationId });

  const { data: systemRoles } = useSystemRolesQuery({
    organizationId,
    includeExternal: organization?.label?.includes("External"),
  });

  const getContactOptions = () => {
    const idArray = systemRoles.map((item) => item.id.toString());

    ClientService.findSignatureContactNoClient({
      AssociateClient: false,
      organizationId: organizationId,
      communityIds: selectedCommunityIds,
      includeWithoutCommunity: true,
      statuses: "ACTIVE",
      includeWithoutSystemRole: true,
      systemRoles: idArray,
      sort: "fullName,asc",
    }).then((res) => {
      const filterContactEmail = res.data
        ?.filter((item) => item.email && item.email.trim() !== "")
        .map((value) => {
          return {
            value: value.id,
            text: value.fullName,
            email: value.email,
            type: "Contact",
            typeId: 1,
          };
        });
      setContactOptions(filterContactEmail);
    });
  };

  useEffect(() => {
    if (selectedCommunityIds?.length) {
      const data = selectedCommunityIds.join();
      getDocumentMultipleTemplate(data);
    }
  }, [selectedCommunityIds]);

  useEffect(() => {
    if (selectedCommunityIds?.length) {
      const data = selectedCommunityIds.join();
      getClientsOptions(data);
      getContactOptions();
    }
  }, [selectTemplateId]);

  const onChangeCommunity = useCallback(
    (name, value) => {
      changeField(name, value);
      changeField("templateId", []);
      changeField("clientIds", []);
      setSelectTemplateId(null);
      setTemplateSignList([]);
      setAllSignOptions([]);
      setClientsNum(0);
      setSelectedCommunityIds(value);
    },
    [data, changeField, communityOptions],
  );

  /**
   * 更改template
   * @param name
   * @param value
   */
  const changeTemplateId = (name, value) => {
    setSelectTemplateId(value[0]);
    // 展示单人签名逻辑选项
    setShowRecipient(true);
    changeField(name, value);
    changeField("clientIds", []);
    setSignOrderEmailList([]);
    setSelectClient([]);
    setAllSignOptions([]);
    setAllSignOptions2([]);
    getDocumentMultipleTemplateDetail(value[0]);
  };

  const mergeArrays = (A, B) => {
    // 过滤出B数组中A中不存在的项，然后将其推入A数组
    B.filter((itemB) => !A.some((itemA) => itemA.value === itemB.value)).forEach((item) => A.push(item));
  };
  const changeClientId = (name, value) => {
    changeField(name, value);
    setAllSignOptions([]);
    setSignOrderEmailList([]);
    setSelectClient(value);
    fields.submitters.map((o, index) => {
      changeField(`submitters.${index}`, {
        name: o.name,
        uuid: o.uuid,
      });
    });
    let hasCareTeamOptions = clientContactOptions;
    mergeArrays(hasCareTeamOptions, allSelectOptionHasCareTeam);
    const filteredSelectData = hasCareTeamOptions.filter((item) => value.includes(item.value));

    const filteredClientData = clientContactOptions.filter((item) => value.includes(item.value) && item.typeId === 0);
    const filteredContactData = clientContactOptions.filter((item) => value.includes(item.value) && item.typeId === 1);

    setOnlyClientIds(
      filteredClientData.map((item) => {
        return item.value;
      }),
    );

    setOnlyContactIds(
      filteredContactData.map((item) => {
        return item.value;
      }),
    );

    setAllSignOptions(filteredSelectData);

    if (value.length > clientsNum) {
      setHasClientError(true);
    } else {
      if (filteredClientData.length < 1) {
        setHasClientNumberError(true);
        setHasClientError(false);
      } else {
        setHasClientError(false);
        setHasClientNumberError(false);
      }
    }
  };
  useEffect(() => {
    setAllSignOptions2(allSignOptions);
  }, [allSignOptions]);

  function expirationData() {
    const currentTimeStamp = Date.now();

    // 2 day * 1000
    const twoDaysInMilliseconds = 2 * 24 * 60 * 60 * 1000;

    // now + 2day*1000
    return currentTimeStamp + twoDaysInMilliseconds;
  }

  const onChangeSelectOptions = (data) => {
    allSelectOptionHasCareTeam = data;
  };

  const submitSingleMulTemplate = () => {
    setShowLoading(true);
    const newTimeStamp = expirationData();

    const params = {
      clientIds: [singleClientId],
      data: [{ templateId: selectedTemplate.id }],
      expirationDate: newTimeStamp,
      organizationId: data.organizationId,
      signNow: signNowType,
      documentUrl: selectedTemplate.documentUrl,
      applicationKey: selectedTemplate.applicaitonKey,
      clientIdAndEmails: [
        {
          role: "Client",
          id: singleClientId,
          email: singleEmail,
        },
      ],
      docuData: {
        template_id: selectedTemplate.templateId, // templateId
        message: {
          subject,
          body: `{{submitter.link}}\n` + "\n" + message,
        },
        submission: [
          {
            submitters: [
              {
                email: singleEmail,
                fields: templateSignList[0]?.fields,
              },
            ],
          },
        ],
      },
      sendType: clickRecipient,
    };

    try {
      service.sendMultiple(params).then((res) => {
        setShowLoading(false);
        setIsClientSignatureRequestSuccessDialogOpen(true);
        onSubmitSuccess();
        if (res) {
          //   有值
          window.open(`https://docseal.simplyconnect.me/s/${res}`, "_blank");
        }
      });
    } catch (e) {
      setShowSendErrorDialog(true);
      setShowLoading(false);
    }
  };

  const submitMulTemplate = () => {
    setShowLoading(true);
    if (signOrderEmailList.length > clientsNum || signOrderEmailList.length === 0) return;
    let hasClientInReceiver = signOrderEmailList.some((item) => item.typeId === 0);

    if (!hasClientInReceiver) {
      setShowClientError(true);
      return;
    } else {
      setShowClientError(false);
    }

    const data = signOrderEmailList.filter((item) => item !== null && item !== undefined && item !== "");
    const emailData = data.map((item) => {
      if (item.role !== "") {
        return {
          email: item.email,
          role: item.role,
          fields: item.fields,
        };
      }
    });
    const currentTimeStamp = Date.now();

    // 2day * 1000
    const twoDaysInMilliseconds = 2 * 24 * 60 * 60 * 1000;

    // now + 2day*1000
    const newTimeStamp = currentTimeStamp + twoDaysInMilliseconds;

    const clientIdAndEmailsData = clientIdAndEmails.filter(
      (item) => item !== null && item !== undefined && item !== "",
    );

    const params = {
      clientIds: onlyClientIds,
      data: [{ templateId: selectedTemplate.id }],
      expirationDate: newTimeStamp,
      clientIdAndEmails: clientIdAndEmailsData,
      organizationId: data.organizationId,
      documentUrl: selectedTemplate.documentUrl,
      applicationKey: selectedTemplate.applicaitonKey,
      docuData: {
        template_id: selectedTemplate.templateId, // templateId
        message: {
          subject,
          body: `{{submitter.link}}\n` + "\n" + message,
        },
        submission: [
          {
            submitters: emailData,
          },
        ],
      },
    };
    try {
      service.sendMultiple(params).then((res) => {
        onSubmitSuccess();
        setShowLoading(false);
      });
    } catch (e) {
      setShowSendErrorDialog(true);
      setShowLoading(false);
    }
  };

  function valueTextMapper({ id, name, value, label, title, text }) {
    return { value: id || value || name, text: label || title || name || text };
  }

  const preview = () => {
    setIsShowTemplatePreview(true);
  };

  const optionsCategories = [
    {
      typeId: 0,
      name: "CLIENT",
      title: "Client",
    },
    {
      typeId: 1,
      name: "CONTACT",
      title: "Contact",
    },
  ];
  const optionsCategoryIds = [0, 1];

  const mappedServices = useMemo(
    () =>
      chain(optionsCategories)
        .filter((o) => optionsCategoryIds.includes(o.typeId))
        .map((o) => ({
          id: o.id,
          title: o.text ?? o.title,
          options: chain(clientContactOptions).where({ typeId: o.typeId }).map(valueTextMapper).value(),
        }))
        .value(),
    [clientContactOptions, optionsCategoryIds, optionsCategories],
  );

  useEffect(() => {
    let data = {
      parentNode: [
        {
          title: "Client",
          parentId: "CLIENT",
          childNode: [...clientOptions],
        },
        {
          title: "Contact",
          parentId: "CONTACT",
          childNode: [...contactOptions],
        },
      ],
    };
    setLevelOptions(data);
  }, [contactOptions, clientOptions]);

  const changeSearchReceiverText = (_, text) => {
    setSearchText(text);
  };

  const changeSignItem = (name, value) => {
    changeField(name, value);
    let order = null;

    const match = name.match(/submitters\.(\d+)\.text/);
    if (match) {
      order = parseInt(match[1], 10);
    }

    const selected = allSignOptions.find((item) => item.value === value);
    const email = selected ? selected.email : null;

    signOrderNotPOAEmailList[order] = email;
    signOrderIdList[order] = value;
    clientIdAndEmails[order] = {
      id: value,
      email: email,
      role: selected?.type,
    };

    let poaOptions = [];
    if (selected?.powerOfAttorneys) {
      poaOptions = selected.powerOfAttorneys?.map((item) => {
        return {
          value: item.email,
          text: item.fullName,
          id: item.id,
        };
      });
    }

    // client
    if (selected?.typeId === 0) {
      signOrderEmailList[order] = {
        role: templateSignList[order]?.name,
        hasPOA: selected?.hasPOA,
        poaList: poaOptions,
        usePOA: selected?.hasPOA || false,
        email: email,
        typeId: 0,
        fields: templateSignList[order]?.fields,
      };
      changeField(`data.submitters${[order]}.usePOA`, selected?.hasPOA);
      clientUsePoaList[order] = selected?.hasPOA;
    } else if (selected?.typeId === 1) {
      // contact
      signOrderEmailList[order] = {
        role: templateSignList[order]?.name,
        email: email,
        typeId: 1,
        fields: templateSignList[order]?.fields,
      };
    }
    const dataOption = allSignOptions2.map((item) => ({ ...item }));

    dataOption.forEach((item) => {
      if (item.value === value) {
        item.isDisabled = true;
      } else {
        item.isDisabled = !!signOrderIdList?.includes(item.value);
      }
    });
    setAllSignOptions2(dataOption);
  };

  const clearSelectSign = () => {
    setAllSignOptions2([...allSignOptions]);
    setSignOrderEmailList([]);
    setSignOrderIdList([]);
    setClientIdAndEmails([]);
    fields.submitters.map((o, index) => {
      changeField(`submitters.${index}`, {
        name: o.name,
        uuid: o.uuid,
      });
    });
  };

  const clearSearchReceiverText = () => {
    setSearchText("");
    fields.submitters.map((o, index) => {
      changeField(`submitters.${index}`, {
        name: o.name,
        uuid: o.uuid,
      });
    });
  };

  const changeUsePOA = (name, value) => {
    changeField(name, value);
    let order = null;
    const match = name.match(/submitters\.(\d+)\.usePOA/);
    if (match && match[1]) {
      order = match[1];
    }
    clientUsePoaList[order] = value;
    if (!value) {
      signOrderEmailList[order].email = signOrderNotPOAEmailList[order];
      clientIdAndEmails[order].email = signOrderNotPOAEmailList[order];
    }
  };

  const changeClientSenderEmail = (name, value) => {
    changeField(name, value);
    let order = null;
    const match = name.match(/submitters\.(\d+)\.email/);
    if (match && match[1]) {
      order = match[1];
    }
    signOrderEmailList[order].email = value;
    clientIdAndEmails[order].email = value;
  };

  //  改变recipient 方法
  const user = useSelector((state) => state.auth.login.user.data);
  const onChangeRecipientTypeField = (name, value) => {
    setClickRecipient(value);
    setSelectedSingleReceiver(null);
    setSignNowType(false);

    if (value === "CLIENT") {
      setSingleEmail(singleClientEmail);
    }

    if (value === "SELF") {
      setSingleEmail(user.email);
    }

    if (value === "STAFF") {
      setSingleEmail(null);

      service.findContacts({ clientId: singleClientId }).then((res) => {
        const result = res.map((item) => {
          return {
            value: item.id,
            text: item.name,
          };
        });

        setStaffOptions(result);
      });
    }
  };

  const onChangeStaffReceiver = (name, value) => {
    setSelectedSingleReceiver(value);
    setSingleEmail(null);
    if (value) {
      service.findContactDetail(value).then((res) => {
        setSingleEmail(res.login);
      });
    }
  };

  const onChangeSignType = (name, value) => {
    setSignNowType(value);
  };

  const { Scrollable, scroll } = useScrollable();
  const scrollableStyles = { height: "calc(100% - 100px)" };

  const changeSubject = (name, value) => {
    setSubject(value);
  };

  const messageChange = (name, value) => {
    setMessage(value);
  };

  return (
    <>
      {showLoading && <Loader hasBackdrop={true} />}
      <div style={{ padding: 20, height: "100%" }}>
        <Form className="ESign-Form" onCancel={onClose} onSubmitSuccess={submitMulTemplate}>
          <Scrollable style={scrollableStyles}>
            <Row>
              <Col md={6}>
                <SelectField
                  name="organizationId"
                  label="Organization*"
                  value={data.organizationId}
                  options={orgOptions}
                  isDisabled={true}
                />
              </Col>

              <Col md={6}>
                <SelectField
                  name="communities"
                  label="Community*"
                  value={data.communities}
                  options={communityOptions}
                  onChange={onChangeCommunity}
                  // errorText={errors?.communities}
                  isMultiple
                />
              </Col>
              <Col md={isSingleSignature ? 6 : 12} lg={isSingleSignature ? 6 : 12}>
                <SelectFieldTree
                  name="templateId"
                  label="Template*"
                  isShowFolderIcon={true}
                  value={data.templateId}
                  options={templateList}
                  onChange={changeTemplateId}
                  isMultiple={false}
                />
              </Col>

              {isSingleSignature && Number(clientsNum) === 1 && (
                <>
                  {showRecipient && (
                    <Col md={6}>
                      <RadioGroupField
                        view="row"
                        name="recipientType"
                        title="Recipient*"
                        className="SignatureRequestForm-RadioGroupField"
                        options={recipientOptions}
                        onChange={onChangeRecipientTypeField}
                        selected={clickRecipient}
                      />
                    </Col>
                  )}
                </>
              )}

              {!isSingleSignature && (
                <Col lg={12} md={12}>
                  <SelectFieldLevel
                    hasSearchBox
                    hasAutoScroll={true}
                    isMultiple
                    hasTags
                    isShowRightChevron
                    hasValueTooltip
                    name="clientIds"
                    value={data.clientIds}
                    options={levelOptions}
                    hasAllOption={false}
                    onChangeSelectOptions={onChangeSelectOptions}
                    label="Receiver*"
                    onChangeSearchText={changeSearchReceiverText}
                    onClearSearchText={clearSearchReceiverText}
                    isDisabled={!clientsNum}
                    errorText={clientsNumError || clientsSelectNumError || errors?.clientIds}
                    onChange={changeClientId}
                  />
                </Col>
              )}
            </Row>

            {/*  单人模版逻辑处理 */}

            {isSingleSignature && Number(clientsNum) === 1 && (
              <>
                {Number(clientsNum) === 1 &&
                  clickRecipient === "CLIENT" &&
                  allSignOptions.length !== 0 &&
                  templateSignList &&
                  !hasClientError &&
                  !hasClientNumberError &&
                  templateSignList?.map((o, index) => {
                    return (
                      <Row>
                        {/*需要加上role*/}
                        <Col lg={4} md={4} key={index}>
                          <SelectField
                            key={new Date()}
                            type="text"
                            name={`submitters.${index}.text`}
                            label={o.name + "*"}
                            value={data.submitters[index].text}
                            options={allSignOptions2}
                            onChange={changeSignItem}
                            isMultiple={false}
                          />
                        </Col>

                        {signOrderEmailList[index] && (
                          <Col lg={8} md={8} key={new Date()}>
                            {/*contact*/}
                            {signOrderEmailList[index].typeId === 1 && (
                              <div className="emailBox"> {signOrderEmailList[index].email} </div>
                            )}
                            {/*client*/}

                            {/* 区分单人逻辑和多人逻辑*/}

                            {signOrderEmailList[index].typeId === 0 && !signOrderEmailList[index].hasPOA && (
                              <div className="emailBox"> {signOrderEmailList[index].email} </div>
                            )}

                            {signOrderEmailList[index].typeId === 0 && signOrderEmailList[index].hasPOA && (
                              <Row>
                                <Col lg={3} md={3}>
                                  <div className="poaEmailBox">
                                    <CheckboxField
                                      type="text"
                                      name={`submitters.${index}.usePOA`}
                                      value={data.submitters[index].usePOA}
                                      label="POA"
                                      isDisabled={!signOrderEmailList[index].hasPOA}
                                      onChange={changeUsePOA}
                                    />
                                  </div>
                                </Col>
                                {data.submitters[index]?.usePOA ? (
                                  <>
                                    <Col>
                                      <SelectField
                                        key={`email${index}`}
                                        type="text"
                                        name={`submitters.${index}.email`}
                                        label={"POA*"}
                                        value={data.submitters[index].email}
                                        options={signOrderEmailList[index].poaList}
                                        onChange={changeClientSenderEmail}
                                        isMultiple={false}
                                      />
                                    </Col>
                                    <Col>
                                      {" "}
                                      {data.submitters[index].email && (
                                        <div className="emailBox"> {data.submitters[index].email} </div>
                                      )}
                                    </Col>
                                  </>
                                ) : (
                                  <Col>
                                    <div className="emailBox"> {signOrderEmailList[index].email} </div>
                                  </Col>
                                )}
                              </Row>
                            )}
                          </Col>
                        )}
                      </Row>
                    );
                  })}
                <Row>
                  {/* 单人 client 选择*/}
                  {isSingleSignature && Number(clientsNum) === 1 && clickRecipient === "CLIENT" && (
                    <Col lg={6} md={6}>
                      <SelectField
                        hasSearchBox
                        isDisabled
                        isSectioned
                        hasValueTooltip
                        hasSectionIndicator
                        hasSectionSeparator
                        name="clientIds"
                        value={singleClientId}
                        sections={mappedServices}
                        hasAllOption={false}
                        label="Receiver*"
                        onChangeSearchText={changeSearchReceiverText}
                        onClearSearchText={clearSearchReceiverText}
                        errorText={clientsNumError || clientsSelectNumError || errors?.clientIds}
                        onChange={changeClientId}
                      />
                    </Col>
                  )}

                  {/*staff receiver*/}
                  {clickRecipient === "STAFF" && (
                    <Col lg={6} md={6}>
                      <SelectField
                        name="receiver"
                        label="Receiver*"
                        value={selectedSingleReceiver}
                        options={staffOptions}
                        onChange={onChangeStaffReceiver}
                      />
                    </Col>
                  )}

                  {(clickRecipient === "CLIENT" || clickRecipient === "STAFF") && (
                    <Col md={6} lg={6}>
                      <TextField
                        type="text"
                        name="Email"
                        value={singleEmail}
                        label="Email*"
                        onChange={changeSingleEmail}
                      />
                    </Col>
                  )}
                </Row>

                <Row>
                  <Col md={6} lg={6}>
                    {isSingleSignature &&
                      Number(clientsNum) === 1 &&
                      (clickRecipient === "CLIENT" || clickRecipient === "STAFF") && (
                        <>
                          <RadioGroupField
                            view="row"
                            name="signnow"
                            title="Sign Now*"
                            className="SignatureRequestForm-RadioGroupField"
                            options={signNowOptions}
                            onChange={onChangeSignType}
                            selected={signNowType}
                          />
                        </>
                      )}
                  </Col>

                  {isSingleSignature && (
                    <Col md={12} lg={12}>
                      <TextField
                        type="text"
                        name="subject"
                        value={subject}
                        label="Subject*"
                        className="RequestInfoForm-TextArea"
                        onChange={changeSubject}
                      />
                    </Col>
                  )}

                  {isSingleSignature && (
                    <Col md={12} lg={12}>
                      <TextField
                        type="textarea"
                        name="request.text"
                        value={message}
                        maxLength={20000}
                        label="Message*"
                        className="RequestInfoForm-TextArea"
                        onChange={messageChange}
                        numberOfRows={5}
                      />
                    </Col>
                  )}

                  {/* message */}
                </Row>
              </>
            )}

            {/* 选择 poa 还是 本人的email*/}
            {!isSingleSignature &&
              allSignOptions.length !== 0 &&
              templateSignList &&
              !hasClientError &&
              !hasClientNumberError &&
              templateSignList?.map((o, index) => {
                return (
                  <Row>
                    {/*需要加上role*/}
                    <Col lg={4} md={4} key={index}>
                      <SelectField
                        key={new Date()}
                        type="text"
                        name={`submitters.${index}.text`}
                        label={o.name + "*"}
                        value={data.submitters[index].text}
                        options={allSignOptions2}
                        onChange={changeSignItem}
                        isMultiple={false}
                      />
                    </Col>

                    {signOrderEmailList[index] && (
                      <Col lg={8} md={8} key={new Date()}>
                        {/*contact */}
                        {signOrderEmailList[index].typeId === 1 && (
                          <div className="emailBox"> {signOrderEmailList[index].email} </div>
                        )}
                        {/*client */}

                        {signOrderEmailList[index].typeId === 0 && !signOrderEmailList[index].hasPOA && (
                          <div className="emailBox"> {signOrderEmailList[index].email} </div>
                        )}

                        {signOrderEmailList[index].typeId === 0 && signOrderEmailList[index].hasPOA && (
                          <Row>
                            {/*  hasPOA 默认先使用POA  根据POA人员选择 并展示对应的email */}
                            <Col lg={3} md={3}>
                              <div className="poaEmailBox">
                                <CheckboxField
                                  type="text"
                                  name={`submitters.${index}.usePOA`}
                                  value={data.submitters[index].usePOA}
                                  label="POA"
                                  isDisabled={!signOrderEmailList[index].hasPOA}
                                  onChange={changeUsePOA}
                                />
                              </div>
                            </Col>
                            {data.submitters[index]?.usePOA ? (
                              <>
                                <Col>
                                  {/*选择poA的人员*/}
                                  <SelectField
                                    key={`email${index}`}
                                    type="text"
                                    name={`submitters.${index}.email`}
                                    label={"POA*"}
                                    value={data.submitters[index].email}
                                    options={signOrderEmailList[index].poaList}
                                    onChange={changeClientSenderEmail}
                                    isMultiple={false}
                                  />
                                </Col>
                                <Col>
                                  {" "}
                                  {data.submitters[index].email && (
                                    <div className="emailBox"> {data.submitters[index].email} </div>
                                  )}
                                </Col>
                              </>
                            ) : (
                              <Col>
                                <div className="emailBox"> {signOrderEmailList[index].email} </div>
                              </Col>
                            )}
                          </Row>
                        )}
                      </Col>
                    )}
                  </Row>
                );
              })}
            <Row>
              {showClientError && (
                <Col lg={12} xm={12} xl={12}>
                  <div className={"error-no-client"}>* Please select at least one client as the receiver</div>
                </Col>
              )}
            </Row>
            <Row>
              {allSignOptions?.length !== 0 && templateSignList && !hasClientNumberError && !hasClientError && (
                <>
                  <div className="clearBtn" onClick={clearSelectSign}>
                    Clear select order
                  </div>
                </>
              )}

              {!isSingleSignature && (
                <Col md={12} lg={12}>
                  <TextField
                    type="text"
                    name="subject"
                    value={subject}
                    label="Subject*"
                    className="RequestInfoForm-TextArea"
                    onChange={changeSubject}
                  />
                </Col>
              )}

              {!isSingleSignature && (
                <Col md={12} lg={12}>
                  <TextField
                    type="textarea"
                    name="request.text"
                    value={message}
                    maxLength={20000}
                    label="Message*"
                    className="RequestInfoForm-TextArea"
                    onChange={messageChange}
                    numberOfRows={5}
                  />
                </Col>
              )}
            </Row>
          </Scrollable>
          {/*isDisabled*/}
          <div className="Form-Buttons">
            {selectedTemplate && (
              <Button color="success" className="margin-right-25 width-170" onClick={preview}>
                Preview Template
              </Button>
            )}
            <Button outline id="cancel-action" className="margin-left-20 btn btn-outline-success" onClick={onClose}>
              Close
            </Button>

            {isSingleSignature && (
              <Button
                outline
                id="cancel-action"
                className="margin-right-25 width-100 btn-success"
                onClick={submitSingleMulTemplate}
                disabled={!selectTemplateId || !singleEmail}
              >
                Send
              </Button>
            )}

            {!isSingleSignature && (
              <Button
                outline
                id="cancel-action"
                className="margin-right-25 width-100 btn-success"
                onClick={submitMulTemplate}
                disabled={
                  hasClientError ||
                  hasClientNumberError ||
                  selectClient?.length === 0 ||
                  signOrderEmailList?.length === 0 ||
                  !subject ||
                  !message
                }
              >
                Send
              </Button>
            )}
          </div>
        </Form>

        {isShowTemplatePreview && (
          <EsignMultipleSignaturesPreview
            isOpen={isShowTemplatePreview}
            onClose={() => {
              setIsShowTemplatePreview(false);
            }}
            editData={{
              title: selectedTemplate.title,
              documentUrl: selectedTemplate.documentUrl,
              applicationKey: selectedTemplate.applicaitonKey,
            }}
          />
        )}

        {showSendErrorDialog && (
          <ErrorDialog
            isOpen
            title="Email sending failed."
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  setShowSendErrorDialog(false);
                },
              },
            ]}
          />
        )}
      </div>
    </>
  );
};

export default MultipleSignaturesContext;
