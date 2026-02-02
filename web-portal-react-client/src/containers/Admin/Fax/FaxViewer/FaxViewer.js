import React, { useEffect, useState } from "react";
import { useScrollable, useScrollToFormError } from "hooks/common";
import { Detail } from "../../../../components/business/common";

import { Image } from "react-bootstrap";
import { Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import { CheckboxField } from "components/Form";
import adminAssociationsService from "services/AssociationsService";

import Modal from "components/Modal/Modal";
import Loader from "components/Loader/Loader";

import "./FaxViewer.scss";

import { isEmpty } from "lib/utils/Utils";

const FaxViewer = (props) => {
  const { FaxId, isOpen } = props;
  const [faxDetailData, setFaxDetailData] = useState();
  const [isFetching, setIsFetching] = useState(false);
  const { Scrollable, scroll } = useScrollable();
  const scrollableStyles = { flex: 1 };
  const onScroll = useScrollToFormError(".FaxForm", scroll);
  const getDetail = () => {
    adminAssociationsService.FeatAssociationDetail(FaxId).then((res) => {
      if (res.success) {
        setFaxDetailData(res.data);
      }
      setIsFetching(false);
    });
  };
  useEffect(() => {
    setIsFetching(true);
    getDetail();
  }, [FaxId]);

  const onClose = () => {
    props.onClose();
  };

  const isLoading = () => {
    return isFetching;
  };
  return (
    <>
      <Modal
        isOpen={isOpen}
        hasCloseBtn={false}
        title="View Fax"
        className="FaxViewer"
        renderFooter={() => (
          <Button outline color="success" onClick={onClose}>
            Close
          </Button>
        )}
      >
        <div className="FaxForm">
          {isLoading() && <Loader />}
          {!isLoading() && !isEmpty(faxDetailData) && (
            <>
              <div className="FaxForm-Section">
                <Scrollable style={scrollableStyles}>
                  <div className="FaxForm-Section">
                    <div className="FaxForm-SectionTitle">Fax Information</div>
                    <Row>
                      <Col md={4}>
                        {/* <Detail title="From" className="FaxForm-TextField">
                                                {faxDetailData?.name}
                                            </Detail> */}
                        <Detail title="From" className="FaxDetail">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={8}>
                        <Detail title="Organizations" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={4}>
                        <Detail title="To" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={4}>
                        <Detail title="Role" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={4}>
                        <Detail title="Fax Number" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                    </Row>
                    <div className="FaxForm-SectionTitle">Fax Content</div>
                    <Row>
                      <Col md={12}>
                        <Detail title="Fax Content" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                    </Row>
                    <Row>
                      <Detail title="Notes" className="FaxForm-TextField">
                        {faxDetailData?.name}
                      </Detail>
                    </Row>
                    <Row>
                      <Col lg={8} md={6}>
                        {/* <FileField
                                                hasHint
                                                name="logoPic"
                                                value={fields?.logoPic ? fields?.logoPic?.name : AssociationDetail.logo}
                                                label="Attach Files*"

                                                className="CommunityForm-FileField"
                                                hintText="The accepted file types are DOC, DOCX, XLS, XlSX, PPT, PUB, PDF, TIF, JPG,BMP, PNG, GIF"
                                            /> */}
                      </Col>
                    </Row>
                    <Row>
                      <Col md={4}>
                        <Detail title="Header" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={4}>
                        <Detail title="Job Name" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                      <Col md={4}>
                        <Detail title="PO Number" className="FaxForm-TextField">
                          {faxDetailData?.name}
                        </Detail>
                      </Col>
                    </Row>
                    <Row>
                      <Col md={4}>
                        <CheckboxField
                          name="isCommunityAddressUsed"
                          value={faxDetailData.isCommunityAddressUsed}
                          label="Send Email"
                          className="FaxForm-CheckboxField"
                          isDisabled
                        />
                      </Col>
                    </Row>
                  </div>
                </Scrollable>
              </div>
            </>
          )}
          {faxDetailData && isEmpty(faxDetailData) && <h4>No Data</h4>}
        </div>
      </Modal>
    </>
  );
};

export default FaxViewer;
