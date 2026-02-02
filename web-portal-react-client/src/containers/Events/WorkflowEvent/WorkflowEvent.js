import { Col, Row } from "reactstrap";
import EventPrimaryFilter from "../EventNotePrimaryFilter/EventNotePrimaryFilter";
import React, { useEffect, useState } from "react";
import { Footer } from "../../../components";
import { connect } from "react-redux";
import WorkFlowRightEvents from "../WorkflowHomePage/WorkflowRight/WorkflowRight";
import WorkflowLeft from "../WorkflowHomePage/WorkflowLeft/WorkflowLeft";
import { compose } from "underscore";
import { withRouter } from "react-router-dom";
import "./WorkflowEvent.scss";

function mapStateToProps(state) {
  const { event } = state;
  const { list } = event.note.composed;

  return {
    dataSource: list.dataSource,
  };
}

const WorkflowEvent = (props) => {
  const [isRefresh, setIsRefresh] = useState(false);
  const { organizationId, communityIds } = props.dataSource.filter;

  localStorage.setItem("triggerCurrentOrgId", organizationId);
  return (
    <div className="WorkflowEvent">
      <div style={{ paddingLeft: 20 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <EventPrimaryFilter />
        </div>
        <Row>
          <Col xs={12} sm={12} md={12} lg={12} xl={8} xxl={9}>
            <WorkflowLeft
              communityIds={communityIds}
              organizationId={organizationId}
              isRefresh={isRefresh}
              setIsRefresh={setIsRefresh}
            />
          </Col>
          <Col xs={12} sm={12} md={12} lg={12} xl={4} xxl={3}>
            <WorkFlowRightEvents setIsRefresh={setIsRefresh} isWorkflowAdd={isRefresh} />
          </Col>
        </Row>
      </div>
      <Footer theme="gray" />
    </div>
  );
};

export default compose(withRouter, connect(mapStateToProps))(WorkflowEvent);
