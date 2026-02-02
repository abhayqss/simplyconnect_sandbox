import { useDispatch, useSelector } from "react-redux";
import React, { useCallback, useEffect, useMemo, useReducer, useState } from "react";
import { Button, Col, Form, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import { Loader } from "components";
import { useForm, useScrollable, useScrollToFormError, useSelectOptions } from "hooks/common";
import { CheckboxField, FileField, SelectField, TextField } from "components/Form";
import "./FaxForm.scss";
import FaxEntity from "entities/Fax";
import FaxFormValidator from "validators/FaxFormValidator";

import { isEmpty, isInteger, omitEmptyPropsDeep } from "lib/utils/Utils";
import adminFaxService from "services/FaxService";
import { useCommunitiesQuery, useOrganizationsQuery, useSystemRolesQuery } from "hooks/business/directory/query";

import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS } from "lib/Constants";
import { ReactComponent as Info } from "images/info.svg";
import SelectFieldLevel from "../../../../components/Form/SelectFieldLevel/SelectFieldLevel";

const { DOC, DOCX, PDF, XLS, XlSX, TIFF, JPG, PNG, GIF } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_MIME_TYPES = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOC],
  ALLOWED_FILE_FORMAT_MIME_TYPES[DOCX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XLS],
  ALLOWED_FILE_FORMAT_MIME_TYPES[XlSX],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PDF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[TIFF],
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];
function extraErrorsReducer(state, action) {
  switch (action.type) {
    case "noCommunities":
      return {
        ...state,
        community: NO_COMMUNITY_ERROR_TEXT,
      };

    case "clear": {
      return {
        ...state,
        [action.payload]: "",
      };
    }
  }
}
const extraErrorsInitialState = {
  community: "",
};

function FaxForm(props) {
  const [isInternalType, setIsInternalType] = useState(true);

  const { onSaveSuccess, onCancel, loginFax } = props;
  const [organizationId, setOrganizationId] = useState();
  const { fields, validate, isChanged, clearFields, changeField, changeFields, errors } = useForm(
    "FaxForm",
    FaxEntity,
    FaxFormValidator,
  );

  useEffect(() => {
    changeField("from", loginFax);
  }, [loginFax]);

  const { data: organizations = [] } = useOrganizationsQuery();

  const { Scrollable, scroll } = useScrollable();
  const onScroll = useScrollToFormError(".FaxForm", scroll);
  const [needValidation, setNeedValidation] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const scrollableStyles = { flex: 1 };
  const [extraErrors, dispatchError] = useReducer(extraErrorsReducer, extraErrorsInitialState);

  const [contactOptions, setContactOptions] = useState([]);
  const [vendorMemberOptions, setVendorMemberOptions] = useState([]);
  const [receiverOptions, setReceiverOptions] = useState({});
  const [contactId, setContactId] = useState(null);
  let [receiveSearchText] = useState("");

  const organizationsOptions = useSelectOptions(organizations, {
    textProp: "label",
  });
  useEffect(() => {
    let data = {
      parentNode: [
        {
          title: "Contact",
          parentId: "CONTACT",
          childNode: [...contactOptions],
        },
        {
          title: "Vendor Member",
          parentId: "VENDOR-MEMBER",
          childNode: [...vendorMemberOptions],
        },
      ],
    };
    setReceiverOptions(data);
  }, [vendorMemberOptions, contactOptions]);

  const { data: communities = [] } = useCommunitiesQuery(
    { organizationId: fields.organizationId },
    {
      enabled: isInteger(fields.organizationId),
      staleTime: 0, // 数据在获取后立即变为过时
      cacheTime: 0, // 缓存数据的时间
      onSuccess: (data) => {
        if (isEmpty(data)) {
          dispatchError({ type: "noCommunities" });
        }
      },
    },
  );
  let filteredCommunities = useMemo(() => communities.filter((c) => c.canAddClient), [communities]);

  const communitiesOptions = useSelectOptions(filteredCommunities, { textProp: "name" });

  const onChangeFieldWithOrganizationIdCheck = (name, value) => {
    changeField("communityId", "");
    changeField("roleName", "");
    changeField("receiveFaxNumber", "");
    setContactId(null);
    setReceiverOptions({});
    if (value) {
      changeField("organizationId", value);
      setOrganizationId(value);
    } else {
      changeField("organizationId", "");
      setOrganizationId(null);
    }
  };

  const onChangeCommunityId = (name, value) => {
    changeField("contactId", "");
    setContactId(null);
    changeField("roleName", "");
    changeField("receiveFaxNumber", "");
    if (value) {
      changeField("communityId", value);
      const params = {
        organizationId: organizationId,
        communityIds: [value],
        statuses: "ACTIVE",
        page: 1,
        size: 9999,
      };
      adminFaxService.findContact(params).then((res) => {
        if (res.success) {
          let dataArr = [];
          res?.data?.forEach((element) => {
            const dataList = {
              text: element.fullName,
              value: element.id,
              typeId: 6,
            };
            dataArr.push(dataList);
          });
          setContactOptions(dataArr);
        }
      });
      adminFaxService.findVendorMember(params).then((res) => {
        if (res.success) {
          let dataArr = [];
          res?.data?.forEach((element) => {
            const dataList = {
              text: element.fullName,
              value: element.id,
              typeId: 8,
            };
            dataArr.push(dataList);
          });
          setVendorMemberOptions(dataArr);
        }
      });
    } else {
      changeField("communityId", "");
    }
  };

  const onChangeMember = (name, value) => {
    if (value) {
      changeField(name, value);
    } else {
      changeField(name, "");
    }
  };

  const onChangeFieldRecipientCategory = (name, value) => {
    setIsInternalType(value === "internal");
    changeField(name, value);
    if (value !== "internal") {
      changeField("organizationId", "");
      changeField("communityId", "");
      changeField("roleName", "");
      changeField("receiveFaxNumber", "");
      setReceiverOptions({});
    }
  };

  const validationOptions = useMemo(
    () => ({
      included: {
        isInternalType,
        contactId: fields.contactId || contactId,
      },
    }),
    [fields, isInternalType],
  );

  const onChangeFileField = useCallback(
    (name, value) => {
      changeFields({
        [name]: value ?? undefined,
        title: value?.name ?? "",
      });
    },
    [changeFields],
  );

  function validateIf() {
    if (needValidation) {
      validate(validationOptions)
        .then(() => setNeedValidation(false))
        .catch(() => setNeedValidation(true));
    }
  }

  useEffect(() => {
    validateIf();
  }, [needValidation, onScroll, validate, validationOptions]);
  const onFormSubmit = (e) => {
    e.preventDefault();
    setIsFetching(true);
    validate(validationOptions)
      .then(() => {
        setIsFetching(true);
        const params = fields.toJS();
        adminFaxService
          .AddSentFax(params)
          .then((res) => {
            if (res.success) {
              onSaveSuccess();
              setNeedValidation(false);
              setIsFetching(false);
            }
          })
          .catch((error) => {
            setIsFetching(false);
          });
      })
      .catch((validationError) => {
        console.error({
          error: validationError,
          message: validationError?.message,
          stack: validationError?.stack,
        });
        onScroll();
        setNeedValidation(true);
      })
      .finally(() => {
        setIsFetching(false);
      });
  };

  const changeReceiverId = (name, value) => {
    if (value?.length) {
      if (contactId !== value[0]) {
        setContactId(value[0]);
      }
      setContactId(value[0]);
      changeField("contactId", value[0]);
    } else {
      if (contactId) {
        changeField("contactId", contactId);
      } else {
        changeField("contactId", "");
      }
    }
  };
  useEffect(() => {
    if (contactId) {
      getMemberFaxAndRole(contactId);
    }
  }, [contactId]);

  const getMemberFaxAndRole = (value) => {
    adminFaxService.findOneId(value).then((res) => {
      if (res.success) {
        changeField("receiveFaxNumber", res.data.fax);
        changeField("roleName", res.data.systemRoleTitle);
      }
    });
  };
  return (
    <Form className={"FaxForm is-invalid"} onSubmit={onFormSubmit}>
      {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}
      <Scrollable style={scrollableStyles}>
        <div className="FaxForm-Section is-invalid">
          <div className="FaxForm-SectionTitle">Fax Information</div>
          <Row>
            <Col md={4}>
              <TextField
                type="text"
                name="from"
                value={loginFax || ""}
                label="From"
                maxLength={256}
                className="FaxForm-TextField"
                isDisabled={true}
                onChange={changeField}
              />
            </Col>
          </Row>
          <Row>
            <Col md={4}>
              <SelectField
                name="recipientCategory"
                value={fields.recipientCategory || "internal"}
                hasTooltip={true}
                hasAllOption={false}
                hasNoneOption={false}
                renderLabelIcon={() => <Info id="recipientCategory-hint" className="ContactForm-InfoIcon" />}
                options={[
                  { value: "internal", text: "Internal" },
                  {
                    value: "external",
                    text: "External",
                  },
                ]}
                hasEmptyValue={false}
                label="Recipient Category *"
                className="FaxForm-TextField"
                isDisabled={!fields.isActive}
                placeholder="Select"
                isMultiple={false}
                tooltip={{
                  text: "Internal means community contact or linked vendor member.",
                  target: "recipientCategory-hint",
                }}
                errorText={errors?.recipientCategory}
                onChange={onChangeFieldRecipientCategory}
              />
            </Col>
            {isInternalType && (
              <>
                <Col md={4}>
                  <SelectField
                    name="organizationId"
                    value={fields.organizationId}
                    hasValueTooltip
                    hasAllOption={false}
                    hasNoneOption={false}
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    // hasSearchBox
                    options={organizationsOptions}
                    label={"Organization " + (isInternalType ? " *" : "")}
                    className="FaxForm-TextField"
                    isDisabled={!fields.isActive || !isInternalType}
                    placeholder="Select"
                    isMultiple={false}
                    errorText={errors?.organizationId}
                    onChange={onChangeFieldWithOrganizationIdCheck}
                  />
                </Col>
                <Col md={4}>
                  <SelectField
                    name="communityId"
                    value={fields.communityId}
                    options={communitiesOptions}
                    label={"Community " + (isInternalType ? " *" : "")}
                    className="FaxForm-TextField"
                    errorText={errors?.communityId}
                    isDisabled={!fields.isActive}
                    onChange={onChangeCommunityId}
                  />
                </Col>
                <Col md={4}>
                  <SelectFieldLevel
                    hasChildSearch
                    hasAutoScroll={true}
                    isShowRightChevron
                    name="contactId"
                    placeholder={"Select"}
                    value={fields.contactId || contactId}
                    errorText={errors?.contactId}
                    options={receiverOptions}
                    hasAllOption={false}
                    className="FaxForm-TextField"
                    label="Recipient Name *"
                    onChange={changeReceiverId}
                  />
                </Col>
                <Col md={4}>
                  <TextField
                    type="roleName"
                    name="roleName"
                    value={fields.roleName}
                    label="Role"
                    maxLength={256}
                    className="FaxForm-TextField"
                    errorText={errors?.roleName}
                    onChange={changeField}
                    isDisabled={true}
                  />
                </Col>
              </>
            )}
            {!isInternalType && (
              <Col md={4}>
                <TextField
                  name="recipientName"
                  value={fields.recipientName}
                  label="Recipient Name *"
                  className="FaxForm-TextField"
                  errorText={errors?.recipientName}
                  onChange={onChangeMember}
                />
              </Col>
            )}

            <Col md={4}>
              <TextField
                name="receiveFaxNumber"
                placeholder={"XXX-XXX-XXXX"}
                value={fields?.receiveFaxNumber}
                label="Fax Number *"
                renderLabelIcon={() => <Info id="fax-number-hint" className="fax-number-icon" />}
                className="FaxForm-TextField"
                errorText={errors?.receiveFaxNumber}
                onChange={changeField}
              />
              <Tooltip
                autohide={false}
                boundariesElement={document.body}
                className="RoleInfoHint"
                target="fax-number-hint"
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
                Please enter only ten digits.
              </Tooltip>
            </Col>
          </Row>
          <div className="FaxForm-SectionTitle">Fax Content</div>
          <Row>
            <Col md={12}>
              <TextField
                type="text"
                name="content"
                value={fields.content}
                label="Fax Content *"
                maxLength={256}
                className="FaxForm-TextField"
                isDisabled={!fields.isActive}
                onChange={changeField}
                errorText={errors?.content}
              />
            </Col>
          </Row>
          <Row>
            <Col lg={12} md={12}>
              <FileField
                hasHint
                name="file"
                value={fields.file?.name}
                label="Choose document*"
                className="UserManualForm-FileField"
                placeholder=""
                hasError={!!errors?.file}
                errorText={errors?.file ? "Please fill in the required field" : ""}
                hintText="The accepted file types are DOC, DOCX, XLS, XlSX,  PDF, TIF, JPG, PNG, GIF"
                onChange={onChangeFileField}
                allowedTypes={ALLOWED_FILE_MIME_TYPES}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <TextField
                type="text"
                name="header"
                value={fields.header}
                errorText={errors?.header}
                label="Header*"
                maxLength={120}
                className="FaxForm-TextField"
                onChange={changeField}
              />
            </Col>
          </Row>
        </div>
      </Scrollable>
      <div className="FaxForm-Buttons">
        <Button
          outline
          color="success"
          disabled={isFetching}
          onClick={() => {
            onCancel(isChanged);
          }}
        >
          Cancel
        </Button>

        <Button color="success" disabled={isFetching}>
          Send
        </Button>
      </div>
      {/* <Row>
            <Col md={4}>
              <CheckboxField
                name="syncEmail"
                value={fields.syncEmail}
                label="Send Email"
                className="FaxForm-CheckboxField"
                isDisabled={!fields.isActive}
                onChange={changeFieldSendEmail}
              />
            </Col>
          </Row>*/}
      {/*  {isEmail ? (
            <Row>
              <Col md={4}>
                <TextField
                  name="email"
                  value={fields.email}
                  label="Email"
                  className="FaxForm-CheckboxField"
                  isDisabled={!fields.isActive}
                  onChange={changeField}
                />
              </Col>
            </Row>
          ) : null}*/}
    </Form>
  );
}

export default FaxForm;
