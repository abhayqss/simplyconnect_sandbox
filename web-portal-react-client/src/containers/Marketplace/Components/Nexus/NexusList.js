import "./nexus.scss";
import { ReactComponent as Filter } from "images/filters.svg";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getCategory, getStateOptions } from "../../../../redux/marketplace/Vendor/VendorActions";
import NexusListDetail from "./NexusListDetail";
import { Col, Collapse, Row } from "reactstrap";
import { SelectField } from "../../../../components/Form";
import TextField from "../../../../components/Form/TextField/TextField";
import { Button } from "../../../../components/buttons";
import cn from "classnames";

const NexusList = () => {
  const dispatch = useDispatch();
  const { stateOptions, category } = useSelector((state) => state.vendor);

  const getOptionsFormat = (data) => {
    return data.map((item) => {
      return { value: item.id, text: item.label };
    });
  };

  const StateOptions = getOptionsFormat(stateOptions);
  const categoryOptions = getOptionsFormat(category);
  const [showSearchBox, setShowSearchBox] = useState(true);

  const [state, setState] = useState(null);
  const [categoryIds, setCategoryIds] = useState(null);
  const [vendorSearch, setVendorSearch] = useState("");
  const [isSearch, setIsSearch] = useState(false);

  const changeState = (name, value) => {
    setState(value);
  };

  const changeCategory = (_, value) => {
    setCategoryIds(value);
  };

  const changeVendor = (_, value) => {
    setVendorSearch(value);
  };

  useEffect(() => {
    dispatch(getStateOptions({}));
    dispatch(getCategory());
  }, []);

  const clearInput = () => {
    setState(null);
    setCategoryIds(null);
    setVendorSearch("");
    setIsSearch(!isSearch);
  };

  const apply = () => {
    setIsSearch(!isSearch);
  };

  return (
    <>
      <div className="NexusList-Wrap">
        <div className="NexusList-Wrap-Header">
          <div>
            <div className="NexusList-Wrap-Title">Simply Nexus</div>
            {/*<div className="NexusList-Wrap-Title-Num">43</div>*/}
          </div>
          <Filter
            className={cn(
              "FilterIcon",
              "margin-right-24",
              showSearchBox ? "FilterIcon_rotated_90" : "FilterIcon_rotated_0",
            )}
            onClick={() => setShowSearchBox(!showSearchBox)}
          />
        </div>

        <Collapse isOpen={showSearchBox}>
          <div style={{ backgroundColor: "#f9f9f9", padding: "20px" }}>
            <Row>
              <Col lg={4} sm={4}>
                <SelectField
                  hasSearchBox={true}
                  name="state"
                  isMultiple={false}
                  hasAutoScroll={true}
                  value={state}
                  label="State"
                  options={StateOptions}
                  className="StateSelect"
                  onChange={changeState}
                />
              </Col>
              <Col lg={4} sm={4}>
                <SelectField
                  hasSearchBox={true}
                  name="categoryIds"
                  isMultiple={true}
                  hasAutoScroll={true}
                  value={categoryIds}
                  label="Category"
                  options={categoryOptions}
                  className="StateSelect"
                  onChange={changeCategory}
                />
              </Col>
              <Col lg={4} sm={4}>
                <TextField
                  name="vendorSearch"
                  value={vendorSearch}
                  label="Vendor"
                  placeholder="Search by name"
                  onChange={changeVendor}
                />
              </Col>
              <Col lg={12} sm={12}>
                <div className={"btn-list"}>
                  <Button className="margin-right-25" outline color={"success"} onClick={clearInput}>
                    Clear
                  </Button>
                  <Button color={"success"} onClick={apply}>
                    Apply
                  </Button>
                </div>
              </Col>
            </Row>
          </div>
        </Collapse>
      </div>

      <NexusListDetail state={state} category={categoryIds} vendor={vendorSearch} isSearch={isSearch} />
    </>
  );
};

export default NexusList;
