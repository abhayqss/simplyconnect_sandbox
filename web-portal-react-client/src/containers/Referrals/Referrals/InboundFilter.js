import cn from "classnames";
import { SelectField } from "../../../components/Form";
import { compact } from "underscore";
import React, { useEffect } from "react";
import { Row, Col, Button } from "reactstrap";

const InboundFilter = () => {
  useEffect(() => {}, []);

  // 获取所有数据 service priority status referred by

  const onChangeServiceField = () => {};

  return (
    <div className={cn("ReferralFilter", className)}>
      <Row>
        <Col lg={4} md={6} sm={6}>
          <SelectField
            label="Service"
            name="serviceIds"
            value={""}
            isMultiple
            isSectioned
            hasValueTooltip
            sections={[]}
            hasSectionTitle
            hasSectionIndicator
            hasSectionSeparator
            hasKeyboardSearch
            hasKeyboardSearchText
            placeholder="Select Service"
            onChange={onChangeServiceField}
          />
        </Col>
      </Row>
    </div>
  );
};

export default InboundFilter;
