import React from "react";

import memo from "memoize-one";
import { isEqual, isFunction } from "underscore";

import { connect } from "react-redux";

const mapState = (state) => ({
  directory: state.directory,
});

const defaultMapper = { value: "id", text: "label" };

function mapToOptions(list, mapper = defaultMapper) {
  return list.map(
    isFunction(mapper)
      ? mapper
      : (o) => ({
          text: o[mapper.text],
          value: o[mapper.value],
          isDisabled: mapper.disableCondition ? mapper.disableCondition.includes(o.name) : false, // 根据条件设置 isDisabled，默认不禁用
        }),
  );
}

export default function withSelectOptions(Component) {
  return connect(mapState)(
    class extends React.Component {
      getDirectoryData = (path) => {
        return this.props.directory.getIn(path).list.dataSource.data;
      };

      MemoizedSelectOptions = (path, mapper) => {
        const memoizedMapToOptions = memo(mapToOptions, isEqual);

        return () => {
          return memoizedMapToOptions(this.getDirectoryData(path), mapper);
        };
      };

      render() {
        return (
          <Component
            {...this.props}
            MemoizedSelectOptions={this.MemoizedSelectOptions}
            getDirectoryData={this.getDirectoryData}
          />
        );
      }
    },
  );
}
