import React, { Component } from "react";

import PropTypes from "prop-types";

const MOUNTING_PHASE = "mounting";
const UNMOUNTING_PHASE = "unmounting";

export default class Action extends Component {
  static propTypes = {
    action: PropTypes.func,
    params: PropTypes.object,
    onPerform: PropTypes.func,
    onPerformed: PropTypes.func,
    shouldPerform: PropTypes.func,
    isMultiple: PropTypes.bool,
    performingPhase: PropTypes.oneOf([MOUNTING_PHASE, UNMOUNTING_PHASE]),
  };

  static defaultProps = {
    action: () => {},
    isMultiple: false,
    onPerform: () => {},
    onPerformed: () => {},
    shouldPerform: () => true,
    performingPhase: MOUNTING_PHASE,
  };

  state = {
    isPerformed: true,
  };

  componentDidMount() {
    const { performingPhase } = this.props;

    if (performingPhase === MOUNTING_PHASE && this.shouldPerform({})) {
      this.perform({}).then(this.onPerformed);
    }
  }

  componentDidUpdate(prevProps) {
    const { isPerformed } = this.state;
    const { isMultiple, performingPhase } = this.props;

    if (performingPhase === MOUNTING_PHASE && (!isPerformed || isMultiple) && this.shouldPerform(prevProps.params)) {
      this.perform(prevProps.params).then(this.onPerformed);
    }
  }

  componentWillUnmount() {
    const { performingPhase } = this.props;

    if (performingPhase === UNMOUNTING_PHASE && this.shouldPerform({})) {
      this.perform({}).then(this.onPerformed);
    }
  }

  onPerformed = (result) => {
    this.props.onPerformed(result);
  };

  shouldPerform(prevParams) {
    return this.props.shouldPerform(prevParams);
  }

  perform(prevParams) {
    this.incrementCount();
    this.props.onPerform();
    return new Promise((resolve) => {
      resolve(this.props.action(prevParams));
    });
  }

  incrementCount() {
    const { isPerformed } = this.state;

    if (!isPerformed) this.setState((s) => ({ count: s.count + 1 }));
  }

  render() {
    return null;
  }
}
