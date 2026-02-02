import React, { memo, useCallback, useEffect, useMemo, useState } from "react";
import { isNumber, map } from "underscore";
import { connect, useDispatch, useSelector } from "react-redux";
import { bindActionCreators, compose } from "redux";

import { useDebouncedCallback } from "use-debounce";

import { Button, Col, Form, Row } from "reactstrap";

import { Loader } from "components";

import { DropzoneField, FileField, PhoneField, RadioGroupField, SelectField, TextField } from "components/Form";

import { withAutoSave } from "hocs";

import {
  useAuthUser,
  useDirectoryData,
  useForm,
  useResponse,
  useScrollable,
  useScrollToFormError,
  useSelectOptions,
} from "hooks/common";

import { useStatesQuery } from "hooks/business/directory";

import { useUniqEmailValidation } from "hooks/business/client";

import { ALL_VENDORS_ROLES, ALLOWED_FILE_FORMAT_MIME_TYPES, ALLOWED_FILE_FORMATS } from "lib/Constants";

import { getDataUrl, isEmpty, omitEmptyPropsDeep } from "lib/utils/Utils";

import { ReactComponent as Info } from "images/info.svg";

import "./VendorForm.scss";
import VendorEntity from "entities/Vendor";
import VendorValidator from "validators/VendorFormSchemeValidator";

import * as vendorListActions from "redux/vendorAdmin/vendorListActions";
import { findVendorCategoryType, findVendorCompanyType } from "redux/vendorAdmin/vendorListActions";
import { useMarketplaceLanguagesQuery } from "hooks/business/directory/query";
import factory from "lib/converters/ConverterFactory";
import Converter from "lib/converters/Converter";
import adminVendorService from "services/AdminVendorService";

const NO_COMMUNITY_ERROR_TEXT = "There is no community created for current organization.";

// form data format

function valueTextMapper({ id, name, value, label, title }) {
  return { value: id || value || name, text: label || title || name };
}

const mapDispatchToProps = (dispatch) => ({
  actions: {
    ...bindActionCreators(vendorListActions, dispatch),
  },
});

const VendorForm = ({
  vendorId,
  isClientEmailRequired = false,
  actions,
  autoSaveAdapter,
  onCancel,
  onSubmitSuccess,
  vendorStatus,
  onActionConfirm,
  onActionRemove,
  isEditVendor = false,
  ...props
}) => {
  const { JPG, GIF, PNG } = ALLOWED_FILE_FORMATS;

  const ALLOWED_FILE_FORMAT_LIST = [
    ALLOWED_FILE_FORMAT_MIME_TYPES[JPG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[PNG],
    ALLOWED_FILE_FORMAT_MIME_TYPES[GIF],
  ];
  const dispatch = useDispatch();
  let isEditing = isNumber(vendorId) || isEditVendor;

  const [vendorStatusIsPending, setVendorStatusIsPending] = useState(0);
  const [serviceTypeOptions, setServiceTypeOptions] = useState([]);
  const [vendorTypeIdsHasError, setVendorTypeIdsHasError] = useState(false);
  const [serviceTypeIdHasError, setServiceTypeIdHasError] = useState(false);

  const getDataFormat = (fields) => {
    let data = fields.toJS();
    return {
      ...data,
      status: vendorStatusIsPending,
    };
  };

  const { fields, errors, validate, isChanged, clearFields, changeField, changeFields } = useForm(
    "VendorForm",
    VendorEntity,
    VendorValidator,
  );

  const changeLicensedField = (name, value) => {
    changeField(name, value);
    changeField("otherLicense", "");
  };

  const changeIsClinicalVendorField = (name, value) => {
    changeField(name, value);
    changeField("hieAgreement", false);
    changeField("premium", null);
  };

  const user = useAuthUser();
  const disabledFlag = ALL_VENDORS_ROLES.includes(user.roleName);

  const onChangeFileField = useCallback(
    (name, value) => {
      changeField(name, value);
    },
    [changeField],
  );

  let { states } = useDirectoryData({
    states: ["state"],
  });
  const converter = factory.getConverter(Converter.types.DATA_URL_TO_FILE);

  const [validateEmailWithinOrganization, emailError] = useUniqEmailValidation({
    clientId: vendorId,
    email: fields.email,
  });
  const companyType = useSelector((state) => state.adminVendor.companyTypes);
  const categoryType = useSelector((state) => state.adminVendor.categoryTypes);
  const [vendor, setVendor] = useState();
  const [isFetching, setIsFetching] = useState(false);
  const [isValidationNeed, setValidationNeed] = useState(props.isValidationNeed);
  const [pictures, setPictures] = useState([]);

  const stateOptions = useSelectOptions(states, { textProp: "label" });

  const companyTypeOptions = useSelectOptions(companyType, { textProp: "name" });
  const categoryTypeOptions = useSelectOptions(categoryType, { textProp: "name" });

  const { Scrollable, scroll } = useScrollable();

  const onScroll = useScrollToFormError(".VendorForm", scroll);

  const onResponse = useResponse({
    onFailure: actions.error?.change,
    onSuccess: useCallback(({ data }) => onSubmitSuccess(data), [onSubmitSuccess]),
    onUnknown: actions.error?.change,
  });

  const isInactiveCareTeamMember = useMemo(
    () =>
      fields.primaryContact?.careTeamMemberId === vendor?.primaryContact?.careTeamMemberId &&
      !vendor?.primaryContact?.active,
    [fields, vendor],
  );

  const VENDOR_FORM__HIE_AGREEMENT_FALSE_PREMIUM_OPTIONS = [
    { value: "premium", text: "Premium" },
    { value: "static", text: "Static" },
  ];

  const VENDOR_FORM_HIE_AGREEMENT_TRUE_PREMIUM_OPTIONS = [
    { value: "preferred", text: "Preferred" },
    { value: "static", text: "Static" },
  ];

  const VENDOR_FORM_CMS_OPTIONS = [
    { value: true, text: "Yes" },
    { value: false, text: "No" },
  ];
  const HIE_AGREEMENT = [
    { value: true, label: "Yes" },
    { value: false, label: "No" },
  ];

  const VENDOR_FORM_LICENSED = [
    {
      value: "0",
      text: "Yes, copy of license on file",
    },
    {
      value: "1",
      text: "Pending receipt of copy of license",
    },
    {
      value: "2",
      text: "Not Licensed",
    },
    {
      value: "3",
      text: "Other",
    },
  ];

  const IS_CLINICAL_VENDOR = [
    { value: true, text: "Yes" },
    { value: false, text: "No" },
  ];

  const validationOptions = useMemo(() => {
    return {};
  }, [vendor, fields, isInactiveCareTeamMember]);

  const onValidate = useCallback(
    (options) => {
      return validate(options).then(() => Promise.resolve());
    },
    [fields, validate],
  );

  async function fetchVendorPictures(pictures) {
    setIsFetching(true);
    const promises = pictures.map((picture) => adminVendorService.viewVendorPhotos(picture.id));
    Promise.all(promises).then((result) => {
      let fileNameRegexp = /(.+?)\.[^.]+$/;

      setPictures(
        result.map((picture, index) => {
          let name = pictures[index].name.match(fileNameRegexp)[1];
          return {
            ...picture,
            name,
          };
        }),
      );
      setIsFetching(false);
    });
  }

  function setFilesToFormData() {
    if (pictures.length > 0) {
      setPictures([]);
      try {
        let files = pictures.map((picture) =>
          converter.convert(getDataUrl(picture.data, picture.mediaType), picture.name),
        );
        changeFields({ vendorPhotos: files }, true);
      } catch {
        console.error("Something went wrong during the pictures loading");
      }
    }
  }

  function setFormVendorPhotosData() {
    if (vendor?.photos) {
      fetchVendorPictures(vendor.photos);
    }
  }

  const changeCategoryTypeIds = (name, value) => {
    changeField(name, value);
    if (value.length) {
      getVendorServiceData(value);
      setVendorTypeIdsHasError(false);
    }
  };

  const changeServicesIds = (name, value) => {
    changeField(name, value);
    if (value.length) {
      setServiceTypeIdHasError(false);
    }
  };
  const getVendorServiceData = (categoryIds) => {
    adminVendorService.getVendorServicesList({ categoryIds: categoryIds }).then((res) => {
      res?.data?.forEach((item) => {
        item.options = item.options.map((option) => {
          return {
            value: option.id,
            text: option.name,
          };
        });
      });
      setServiceTypeOptions(res.data);
    });
  };
  const submit = async (e = null) => {
    e && e.preventDefault();
    if (vendorTypeIds.length === 0) {
      setVendorTypeIdsHasError(true);
      setIsFetching(false);
      return;
    }
    if (serviceIds.length === 0) {
      setServiceTypeIdHasError(true);
      setIsFetching(false);

      return;
    }
    setIsFetching(true);
    let newFields = fields;
    onValidate(newFields)
      .then(async () => {
        onResponse(await actions.submit(getDataFormat(newFields)));
        setValidationNeed(false);
        setIsFetching(false);
      })
      .catch(() => {
        onScroll();
        setValidationNeed(true);
        setIsFetching(false);
      })
      .finally(() => {
        setIsFetching(false);
      });
  };
  const onAutoSave = useCallback(() => {
    if (!isEditing) return;

    onValidate(validationOptions)
      .then(async () => {
        onResponse(
          await actions.submit({
            ...getDataFormat(fields),
            isAutoSave: true,
          }),
        );
        setValidationNeed(false);
      })
      .catch(async (error) => {
        const data = getDataFormat(fields);

        Object.keys(error).forEach((key) => {
          if (key === "address") {
            Object.keys(error.address).forEach((key) => (data.address[key] = vendor.address[key]));
          }

          data[key] = vendor[key];
        });

        onResponse(
          await actions.submit({
            ...data,
            isAutoSave: true,
          }),
        );
      })
      .finally(() => {
        setIsFetching(false);
      });
  }, [vendor, fields, actions, isEditing, onResponse, onValidate, validationOptions]);

  useEffect(() => {
    setFilesToFormData();
  }, [pictures, changeField]);

  useEffect(() => {
    if (autoSaveAdapter && isEditing) {
      autoSaveAdapter.init({
        onSave: () => onAutoSave(),
      });
    }
  }, [fields, isEditing, onAutoSave, autoSaveAdapter]);

  function validateIf() {
    if (isValidationNeed) {
      validate(validationOptions)
        .then(() => setValidationNeed(false))
        .catch(() => setValidationNeed(true));
    }
  }

  function validateEmailIf() {
    if (!!fields.email && isNumber(fields.organizationId)) {
      onValidateEmail();
    }
  }

  function setDefaultData() {
    if (!isEditing) {
      changeFields({}, true);
    }
  }

  function init() {
    if (vendor) {
      let data = omitEmptyPropsDeep(vendor);

      changeFields(
        {
          ...data,
          isActive: vendor.isActive,
          retained: vendor.retained,
          hasNoEmail: !isClientEmailRequired && isEmpty(data.email),
          manuallyCreated: vendor.manuallyCreated,
        },
        true,
      );
    }
  }

  function clearCommunityIf() {
    return () => {
      if (isNumber(fields.organizationId)) {
        changeField("communityId", null);
      }
    };
  }

  function scrollToErrorRightAway() {
    if (props.isValidationNeed && isNumber(fields.id)) {
      setTimeout(onScroll);
    }
  }

  const validateFieldIfNeed = useCallback(
    (field, cb) => {
      let detail = vendor && vendor[field];
      let shouldValidate = !(isEditing && fields[field] === detail);

      return shouldValidate ? cb().catch(console.log) : Promise.resolve();
    },
    [vendor, fields, isEditing],
  );

  const onValidateEmail = useDebouncedCallback(
    () => (isEditing || fields.hasNoEmail ? Promise.resolve() : validateEmailWithinOrganization()),
    300,
  );

  const onSubmit = useCallback(
    (e) => {
      setIsFetching(true);

      submit(e);
    },
    [onScroll, onValidate, validationOptions],
  );

  const onChangeLogoField = useCallback(
    (name, value) => {
      changeField(name, value);
      if (!value) clearFields("logoPic");
    },
    [clearFields, changeField, fields],
  );

  // get form item options
  useStatesQuery();

  useEffect(() => {
    validateIf();
  }, [isValidationNeed, onScroll, validate, validationOptions]);
  useEffect(() => {
    validateEmailIf();
  }, [fields.email, fields.organizationId, onValidateEmail]);
  useEffect(() => {
    setFormVendorPhotosData();
  }, [vendor, changeField]);

  useEffect(() => {
    setDefaultData();
  }, [isEditing, changeFields]);

  useEffect(() => {
    init();
  }, [vendor, changeFields, isClientEmailRequired]);

  useEffect(() => {
    clearCommunityIf();
  }, [fields.organizationId, changeField]);

  useEffect(() => {
    scrollToErrorRightAway();
  }, [onScroll, fields.id, props.isValidationNeed]);

  useEffect(() => {
    dispatch(findVendorCompanyType());
    dispatch(findVendorCategoryType());
  }, []);

  useEffect(() => {
    if (vendorId) {
      setIsFetching(true);
      adminVendorService
        .findById(vendorId)
        .then((res) => {
          if (res.success) {
            setVendorStatusIsPending(res.data.status);
            setVendor(res.data);
            getVendorServiceData(res?.data?.vendorTypeIds);
            setIsFetching(false);
          }
        })
        .catch(() => {
          setVendor([]);
          setIsFetching(false);
        });
    }
  }, [vendorId]);

  const languageIds = useMemo(
    () => fields?.languageIds?.toJS() || vendor?.languageIds?.toJS(),
    [fields.languageIds, vendorId],
  );

  const vendorTypeIds = useMemo(
    () => fields?.vendorTypeIds?.toJS() || vendor?.vendorTypeIds?.toJS(),
    [fields.vendorTypeIds, vendorId],
  );

  const serviceIds = useMemo(
    () => fields?.serviceIds?.toJS() || vendor?.serviceIds?.toJS(),
    [fields.serviceIds, vendorId],
  );

  const { data: marketplaceLanguages = [] } = useMarketplaceLanguagesQuery({}, { staleTime: 0 });

  const mappedMarketplaceLanguages = useMemo(() => map(marketplaceLanguages, valueTextMapper), [marketplaceLanguages]);

  return (
    <Form className="VendorForm is-invalid" onSubmit={onSubmit}>
      {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}
      <Scrollable style={{ flex: 1 }}>
        <div className="LegalInfo is-invalid">
          <div className="VendorsForm-Section LegalInfo-Section is-invalid">
            <div className="VendorsForm-SectionTitle is-invalid">General Data</div>

            <Row>
              <Col lg={8} md={6}>
                <TextField
                  type="text"
                  name="name"
                  value={fields.name}
                  label="Company Name*"
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.name}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={6}>
                <TextField
                  type="text"
                  name="website"
                  value={fields.website}
                  label="Company Website"
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.website}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="Website-Hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "Website-Hint",
                    boundariesElement: "document.body",
                    text: `Website is a part of the credentials required
                                     to log in to Simply Connect portal.`,
                  }}
                />
              </Col>
            </Row>

            <Row>
              <Col lg={4} md={6}>
                <TextField
                  type="text"
                  name="companyId"
                  value={fields.companyId}
                  label="Company ID*"
                  isDisabled={isEditing}
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.companyId}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="Company-Hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "Company-Hint",
                    boundariesElement: "document.body",
                    text: `Company ID is a part of the credentials required
                                     to log in to Simply Connect portal.`,
                  }}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="email"
                  value={fields.email}
                  label="Company Email*"
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.email}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <PhoneField
                  name="phone"
                  value={fields.phone}
                  label="Phone"
                  className="OrganizationForm-TextField"
                  errorText={errors.phone}
                  onChange={changeField}
                  renderLabelIcon={() => <Info id="phone-hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "phone-hint",
                    boundariesElement: "document.body",
                    render: () => (
                      <ul className="OrganizationForm-PhoneTooltipBody">
                        <li>Digits only allowed</li>
                        <li>No spaces, dashes, or special symbols</li>
                        <li>Country code is required</li>
                        <li>‘+’ may be a leading symbol</li>
                      </ul>
                    ),
                  }}
                />
              </Col>
            </Row>

            <Row>
              <Col lg={6} md={6}>
                <SelectField
                  name="companyTypeId"
                  value={fields.companyTypeId}
                  label="Company Type"
                  options={companyTypeOptions}
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  isMultiple={false}
                  errorText={errors.companyTypeId}
                  onChange={changeField}
                />
              </Col>

              <Col lg={6} md={6}>
                <TextField
                  type="text"
                  name="expYear"
                  value={fields.expYear}
                  label="Years of Experience"
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.expYear}
                  onChange={changeField}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={6} md={6}>
                <SelectField
                  isMultiple
                  hasValueTooltip
                  name="vendorTypeIds"
                  value={vendorTypeIds}
                  options={categoryTypeOptions}
                  label="Category*"
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  hasError={vendorTypeIdsHasError}
                  errorText={vendorTypeIdsHasError ? "Please fill in the required field" : ""}
                  onChange={changeCategoryTypeIds}
                />
              </Col>
              {/*add vendor service has section title*/}

              <Col lg={6} md={6}>
                <SelectField
                  isMultiple
                  isSectioned
                  hasAllOption
                  hasSectionIndicator
                  hasSectionSeparator
                  name="serviceIds"
                  value={serviceIds}
                  sections={serviceTypeOptions}
                  label="Services*"
                  className="VendorsForm-SelectField"
                  hasError={serviceTypeIdHasError}
                  errorText={serviceTypeIdHasError ? "Please fill in the required field" : ""}
                  onChange={changeServicesIds}
                />
              </Col>
            </Row>
            <Row>
              <Col lg={8} md={8}>
                <TextField
                  type="text"
                  name="credential"
                  value={fields.credential}
                  label="Credential"
                  maxLength={256}
                  className="VendorsForm-TextField"
                  errorText={errors.credential}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <SelectField
                  name="clinicalVendor"
                  value={fields.clinicalVendor}
                  label="Clinical Vendor*"
                  options={IS_CLINICAL_VENDOR}
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  isMultiple={false}
                  isDisabled={disabledFlag}
                  errorText={errors.clinicalVendor}
                  onChange={changeIsClinicalVendorField}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="oid"
                  value={fields.oid}
                  label="VendorOID*"
                  maxLength={120}
                  isDisabled={isEditing}
                  className="VendorsForm-TextField"
                  errorText={errors.oid}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <SelectField
                  type="text"
                  name="license"
                  options={VENDOR_FORM_LICENSED}
                  value={fields.license}
                  label="Licensed"
                  isMultiple={false}
                  className="VendorsForm-SelectField"
                  errorText={errors.license}
                  onChange={changeLicensedField}
                />
              </Col>

              {fields.license === "3" && (
                <Col lg={4} md={4}>
                  <TextField
                    type="text"
                    name="otherLicense"
                    value={fields.otherLicense}
                    label="License Note"
                    maxLength={100}
                    className="VendorsForm-TextField"
                    errorText={errors.otherLicense}
                    onChange={changeField}
                  />
                </Col>
              )}
              <Col lg={4} md={4}>
                <SelectField
                  name="cms"
                  value={fields.cms || false}
                  label="CME Provider"
                  options={VENDOR_FORM_CMS_OPTIONS}
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  isMultiple={false}
                  errorText={errors.cms}
                  onChange={changeField}
                  hasTooltip={true}
                  renderLabelIcon={() => <Info id="cms-hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "cms-hint",
                    boundariesElement: "document.body",
                    text: `Is the vendor a Continuing Medical Education provider accredited by ACCME.org.?`,
                  }}
                />
              </Col>

              <Col lg={8} md={8}>
                <SelectField
                  isMultiple
                  hasNoneOption
                  hasValueTooltip
                  name="languageIds"
                  value={languageIds}
                  options={mappedMarketplaceLanguages}
                  label="Languages"
                  placeholder="Select"
                  className="OrganizationForm-SelectField"
                  errorText={errors.languageIds}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <SelectField
                  name="premium"
                  value={fields.premium}
                  label="Premium Subscriber*"
                  options={
                    fields.hieAgreement
                      ? VENDOR_FORM_HIE_AGREEMENT_TRUE_PREMIUM_OPTIONS
                      : VENDOR_FORM__HIE_AGREEMENT_FALSE_PREMIUM_OPTIONS
                  }
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  isMultiple={false}
                  isDisabled={disabledFlag}
                  errorText={errors.premium}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>
        </div>
        <div className="LegalInfo">
          <div className="VendorsForm-Section LegalInfo-Section">
            <div className="VendorsForm-SectionTitle">Address</div>

            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="street"
                  value={fields.street}
                  label="Street*"
                  className="VendorsForm-TextField"
                  errorText={errors.street}
                  onChange={changeField}
                />
              </Col>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="city"
                  value={fields.city}
                  label="City*"
                  className="VendorsForm-TextField"
                  errorText={errors.city}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <SelectField
                  name="state"
                  value={fields.state}
                  label="State*"
                  options={stateOptions}
                  placeholder="Select"
                  className="VendorsForm-SelectField"
                  isMultiple={false}
                  errorText={errors.state}
                  onChange={changeField}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="number"
                  name="zipCode"
                  value={fields.zipCode}
                  label="Zip Code*"
                  maxLength={5}
                  className="VendorsForm-TextField"
                  errorText={errors.zipCode}
                  onChange={changeField}
                />
              </Col>
            </Row>
          </div>
        </div>
        <div>
          <div className="VendorsForm-Section LegalInfo-Section">
            <div className="VendorsForm-SectionTitle">HIE Agreement</div>

            <Row>
              <Col lg={6} md={6}>
                <RadioGroupField
                  view="row"
                  name="hieAgreement"
                  selected={fields.hieAgreement}
                  title="HIE agreement*"
                  options={HIE_AGREEMENT}
                  isDisabled={fields.clinicalVendor === false || disabledFlag}
                  onChange={onChangeFileField}
                  errorText={errors.hieAgreement}
                  className="ClientForm-PrimaryContactType"
                />
              </Col>
            </Row>
          </div>
        </div>
        {/* Business hours */}

        <div className="LegalInfo">
          <div className="VendorsForm-Section LegalInfo-Section">
            <div className="VendorsForm-SectionTitle">Operating Hours</div>
            <Row>
              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="operatingWorkDay"
                  value={fields.operatingWorkDay}
                  label="Moday-Friday"
                  className="VendorsForm-TextField"
                  errorText={errors.operatingWorkDay}
                  onChange={changeField}
                  placeholder={"6:00AM - 7:00 PM"}
                  renderLabelIcon={() => <Info id="operatingWorkDay-Hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "operatingWorkDay-Hint",
                    boundariesElement: "document.body",
                    text: `Please enter for example 9:00AM-7:00PM
or closed`,
                  }}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="operatingSaturday"
                  value={fields.operatingSaturday}
                  label="Saturday"
                  className="VendorsForm-TextField"
                  errorText={errors.operatingSaturday}
                  onChange={changeField}
                  placeholder={"6:00AM - 7:00 PM"}
                  renderLabelIcon={() => <Info id="operatingSaturday-Hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "operatingSaturday-Hint",
                    boundariesElement: "document.body",
                    text: `Please enter for example 9:00AM-7:00PM
or closed`,
                  }}
                />
              </Col>

              <Col lg={4} md={4}>
                <TextField
                  type="text"
                  name="operatingSunday"
                  value={fields.operatingSunday}
                  label="Sunday"
                  className="VendorsForm-TextField"
                  errorText={errors.operatingSunday}
                  onChange={changeField}
                  placeholder={"6:00AM - 7:00 PM"}
                  renderLabelIcon={() => <Info id="operatingSunday-Hint" className="VendorsForm-LabelIcon" />}
                  tooltip={{
                    target: "operatingSunday-Hint",
                    boundariesElement: "document.body",
                    text: `Please enter for example 9:00AM-7:00PM
or closed`,
                  }}
                />
              </Col>
            </Row>
          </div>
        </div>

        <div className="LegalInfo">
          <div className="VendorsForm-Section LegalInfo-Section">
            <div className="VendorsForm-SectionTitle">Photo & Introductions</div>
            <Row>
              <Col lg={8} md={8}>
                <FileField
                  hasHint
                  name="logoPic"
                  value={fields.logoPic ? fields?.logoPic?.name : vendor?.logo}
                  label="Upload Logo"
                  renderLabelIcon={() => <Info id="Logo-Hint" className="OrganizationForm-LabelIcon" />}
                  tooltip={{
                    target: "Logo-Hint",
                    boundariesElement: "document.body",
                    text: () => {
                      return (
                        <>
                          <div>The maximum file size for uploads is 1 MB.</div>
                          <div>Only image files (JPG, JPEG, GIF, PNG) are allowed.</div>
                          <div>Recommended aspect ratio is 16:9.</div>
                        </>
                      );
                    },
                  }}
                  className="VendorsForm-TextField"
                  errorText={errors.logoPic?.size || errors.logoPic?.type}
                  hintText="Supported file types: JPG, JPEG, PNG, GIF | Max 1 mb | Recommended aspect ratio is 16:9"
                  onChange={onChangeLogoField}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <DropzoneField
                  name="vendorPhotos"
                  label="Upload Vendor Photos"
                  value={fields.vendorPhotos ? fields.vendorPhotos : vendor.photos}
                  maxCount={10}
                  hintText="Supported file types: JPG, JPEG, GIF, PNG | Max 1 mb |  Recommended aspect ratio is 3:2 | Maximum of 4 photos"
                  className="CommunityForm-DropzoneField"
                  errors={errors.vendorPhotos}
                  allowedTypes={ALLOWED_FILE_FORMAT_LIST}
                  onChange={onChangeFileField}
                />
              </Col>
            </Row>

            <Row>
              <Col lg={12}>
                <TextField
                  maxLength={1000}
                  type="textarea"
                  name="introduction"
                  value={fields.introduction}
                  label="Introduction"
                  className="VendorsForm-TextAreaIntroductionField"
                  errorText={errors.introduction}
                  onChange={changeField}
                  placeholder={"Maximum 1000 characters allowed."}
                />
              </Col>
            </Row>
          </div>
        </div>
      </Scrollable>

      <div className="VendorForm-Buttons">
        {vendorStatus && (
          <Button outline color="success" disabled={isFetching} onClick={() => onActionConfirm(vendorId)}>
            Confirm
          </Button>
        )}

        {vendorStatus && (
          <Button outline color="success" disabled={isFetching} onClick={() => onActionRemove(vendorId)}>
            Reject
          </Button>
        )}

        <Button outline color="success" disabled={isFetching} onClick={() => onCancel(isChanged)}>
          Cancel
        </Button>

        <Button color="success" disabled={isFetching}>
          Save
        </Button>
      </div>
    </Form>
  );
};

export default compose(memo, connect(null, mapDispatchToProps), withAutoSave())(VendorForm);
