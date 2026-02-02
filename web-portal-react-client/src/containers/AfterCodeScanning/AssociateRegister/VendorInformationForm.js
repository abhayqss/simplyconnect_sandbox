import { Button, Col, Form, Row } from "reactstrap";
import { PhoneField, SelectField, TextField } from "../../../components/Form";
import "./AssociateRegister.scss";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS } from "../../../lib/Constants";
import { useScrollable, useScrollToFormError, useSelectOptions } from "../../../hooks/common";
import { useDispatch, useSelector } from "react-redux";
import { findVendorCategoryType, findVendorCompanyType } from "../../../redux/vendorAdmin/vendorListActions";
import DropzoneFieldNew from "../../../components/Form/DropzoneField/DropzoneFieldNew";
import FileFieldNew from "../../../components/Form/FileField/FileFieldNew";
import { saveVendorFormData } from "../../../redux/QrCode/QrcodeActions";
import { useStatesQuery } from "hooks/business/directory/query";
import { isNullOrUndefined, isNumber } from "../../../lib/utils/Utils";

const VendorInformationForm = (props) => {
  const { setActiveStep } = props;
  const dispatch = useDispatch();

  const { data: states = [] } = useStatesQuery();

  const stateOptions = useSelectOptions(states, { textProp: "label" });

  const { vendorFormData } = useSelector((state) => state.Qrcode);
  const requiredFields = [
    "name",
    "companyId",
    "website",
    "state",
    "phone",
    "vendorTypeIds",
    "street",
    "city",
    "zipCode",
    "introduction",
  ];
  const [isNextClick, setIsNextClick] = useState(false);

  const [whetherToVerify, setWhetherToVerify] = useState(false);
  const [isThePhoneNumberCorrect, setIsThePhoneNumberCorrect] = useState(false);
  const [isZipCodeMeetsRules, setIsZipCodeMeetsRules] = useState(false);

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".VendorFormData", scroll);

  const [formData, setFormData] = useState({
    name: "",
    companyId: "",
    website: "",
    phone: "",
    vendorTypeIds: [],
    expYear: "",
    credential: "",
    street: "",
    city: "",
    zipCode: "",
    logoPic: null,
    logoName: "",
    vendorPhotos: [],
    vendorPhotoName: "",
    introduction: "",
    state: "",
  });

  const allFieldsFilled = (data, required) => {
    return required.every((field) => !!data[field]);
  };

  const theZipCodeMeetsTheRules = (zipCode) => {
    if (zipCode) {
      const regex = /^\d{5}$/;
      const result = regex.test(zipCode);
      if (result) {
        setIsZipCodeMeetsRules(true);
      } else {
        setIsZipCodeMeetsRules(false);
      }
    } else {
      setIsZipCodeMeetsRules(false);
    }
  };

  const changeZipField = (name, value) => {
    if (value?.length <= 5) {
      changeField(name, value);
    }
  };

  useEffect(() => {
    theZipCodeMeetsTheRules(formData.zipCode);
  }, [formData.zipCode]);

  useEffect(() => {
    const reg = /\+?\d{10,16}/;
    if (reg.test(formData.phone)) {
      setIsThePhoneNumberCorrect(true);
    } else {
      setIsThePhoneNumberCorrect(false);
    }
  }, [formData.phone]);

  useEffect(() => {
    setFormData({ ...vendorFormData });
  }, [vendorFormData]);

  const onSubmit = (e) => {
    e.preventDefault();
    setIsNextClick(true);
    setWhetherToVerify(true);

    dispatch(saveVendorFormData(formData));
    if (allFieldsFilled(formData, requiredFields) && isThePhoneNumberCorrect && isZipCodeMeetsRules) {
      setActiveStep(1);
    } else {
      onScroll();
    }
  };

  const changeField = (name, value) => {
    isNextClick && setWhetherToVerify(true);
    if (name === "vendorTypeIds") {
      setFormData({
        ...formData,
        [name]: value,
      });
    } else {
      setFormData({
        ...formData,
        [name]: value, // 更新特定的字段
      });
    }
  };
  const clearFields = (fieldName) => {
    setFormData((prevData) => ({ ...prevData, [fieldName]: null }));
  };
  const onChangeLogoField = useCallback(
    (name, value) => {
      setFormData((prevData) => ({ ...prevData, [name]: value }));

      if (!value) {
        // 如果 value 不存在或为空，调用 clearFields 函数
        clearFields(name);
      }

      // 如果你还需要调用 changeField
      changeField(name, value);
    },
    [changeField, clearFields],
  );

  const onChangeFileField = (name, files) => {
    setFormData((prevData) => ({ ...prevData, [name]: files }));
  };

  const categoryType = useSelector((state) => state.adminVendor.categoryTypes);
  const categoryTypeOptions = useSelectOptions(categoryType, { textProp: "name" });

  useEffect(() => {
    dispatch(findVendorCompanyType());
    dispatch(findVendorCategoryType());
  }, [dispatch]);

  return (
    <>
      <Form className="VendorFormData" onSubmit={onSubmit}>
        <Scrollable style={{ flex: 1 }}>
          <div className="AccountInformationTitle">General Data</div>
          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="name"
                value={formData.name} //
                label="Vendor Name*"
                errorText={whetherToVerify && !formData.name && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="companyId"
                value={formData.companyId}
                label="Company ID*"
                errorText={whetherToVerify && !formData.companyId && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="website"
                value={formData.website}
                label="website*"
                errorText={whetherToVerify && !formData.website && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <PhoneField
                name="phone"
                value={formData.phone}
                label="Phone*"
                errorText={
                  whetherToVerify && !isThePhoneNumberCorrect && "Please fill in the correct mobile phone number."
                }
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <SelectField
                isMultiple
                hasTooltip
                name="vendorTypeIds"
                value={formData.vendorTypeIds}
                label="Category*"
                options={categoryTypeOptions}
                placeholder="Select"
                className="VendorsForm-SelectField"
                errorText={
                  whetherToVerify && formData?.vendorTypeIds?.length === 0 && "Please fill in the required field"
                }
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="expYear"
                value={formData.expYear}
                label="Exp-Year"
                // errorText={whetherToVerify && !formData.expYear && 'Please fill in the required field'}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="credential"
                value={formData.credential}
                label="Credential"
                onChange={changeField}
              />
            </Col>
          </Row>

          <div className="AccountInformationTitle">Address</div>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="street"
                value={formData.street}
                label="Street*"
                errorText={whetherToVerify && !formData.street && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="text"
                name="city"
                value={formData.city}
                label="City*"
                errorText={whetherToVerify && !formData.city && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>

            <Col lg={4} md={4}>
              <SelectField
                name="state"
                value={formData.state}
                label="State*"
                options={stateOptions}
                placeholder="Select"
                className="VendorsForm-SelectField"
                isMultiple={false}
                errorText={whetherToVerify && !formData.state && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={4} md={4}>
              <TextField
                type="number"
                name="zipCode"
                value={formData.zipCode}
                label="Zip Code*"
                errorText={whetherToVerify && !isZipCodeMeetsRules && "Please fill in the required field"}
                // onChange={changeField}
                onChange={changeZipField}
              />
            </Col>
          </Row>

          <div className="AccountInformationTitle">Photo & Introductions</div>

          <Row>
            <Col lg={8} md={8}>
              <FileFieldNew
                hasHint
                name="logoPic"
                value={formData?.logoPic || formData?.logoName}
                label="logoPic"
                errorText={""}
                hintText="Supported file types: JPG, PNG, GIF | Max 1 mb"
                onChange={onChangeLogoField}
              />
            </Col>
          </Row>

          <Row>
            <Col>
              <DropzoneFieldNew
                name="vendorPhotos"
                label="Vendor Photos"
                value={formData.vendorPhotos || formData.vendorPhotoName}
                maxCount={10}
                hintText="Supported file types: JPEG, GIF, PNG | Max 20 mb | Upload at least 3 pictures | Picture size must be at least 400*240(5:2)"
                className="CommunityForm-DropzoneField"
                // errors={errors.vendorPhotos}
                allowedTypes={ALLOWED_FILE_FORMAT_LIST}
                onChange={onChangeFileField}
              />
            </Col>
          </Row>

          <Row>
            <Col lg={12}>
              <TextField
                type="textarea"
                name="introduction"
                value={formData.introduction}
                label="Introduction*"
                errorText={whetherToVerify && !formData.introduction && "Please fill in the required field"}
                onChange={changeField}
              />
            </Col>
          </Row>

          <Button
            style={{ width: "100%" }}
            color="success"
            // disabled={isFetching}
          >
            {"Next"}
          </Button>
        </Scrollable>
      </Form>
    </>
  );
};

const { JPG, GIF, PNG } = ALLOWED_FILE_FORMATS;

const ALLOWED_FILE_FORMAT_LIST = [
  ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
  ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
];

export default VendorInformationForm;
