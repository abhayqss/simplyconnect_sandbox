import React from "react";
import { Col, Row } from "reactstrap";
import WorkflowClientLeft from "./WorkflowClientLeft/WorkflowClientLeft"
import WorkFlowRightEvents from '../WorkflowHomePage/WorkflowRight/WorkflowRight'
import "./WorkflowClient.scss"
import { Footer } from "../../../components";
import EventPrimaryFilter from "../EventNotePrimaryFilter/EventNotePrimaryFilter";

const WorkflowClient = (props) => {

  return (<>
      <div className="WorkflowEvent">
        <div style={{paddingLeft: 20}}>
          <EventPrimaryFilter/>
          <Row>
            <Col xs={12} sm={12} md={12} lg={12} xl={8} xxl={9}>
              <WorkflowClientLeft/>
            </Col>
            <Col xs={12} sm={12} md={12} lg={12} xl={4} xxl={3}>
              <WorkFlowRightEvents/>
            </Col>
          </Row>
          <Footer theme='gray'/>
        </div>
      </div>
    </>)
}

export default WorkflowClient;