import React, { Component } from "react";
import cn from "classnames";
import { map } from "underscore";
import PropTypes from "prop-types";
import memoize from "memoize-one";
import { connect } from "react-redux";
import { bindActionCreators } from "redux";
import { withRouter, matchPath } from "react-router";
import Nav from "./Nav/Nav";
import PrivateRoutesConfig from "routes/config/private";
import { camel, last } from "lib/utils/Utils";
import { path } from "lib/utils/ContextUtils";
import { getAllAllowedRoutes } from "lib/utils/UrlUtils";
import "./SideBar.scss";
import { ReactComponent as ToggleIcon } from "images/close-btn.svg";
import { ReactComponent as ToggleSmallIcon } from "images/open-sidebar-btn.svg";
import * as sideBarActions from "redux/sidebar/sideBarActions";

function Section({ title, isOpen, children, className }) {
  return (
    <div className={cn("SideBar-Section", className)}>
      <div className="SideBar-SectionTitle">{title}</div>
      <div className={isOpen ? "SideBar-SectionBody" : "SideBar-SectionBody-close"}>{children}</div>
    </div>
  );
}

function mapStateToProps(state) {
  return {
    isHidden: state.sidebar.isHidden,
    isNo: state.sidebar.isNo,
    isOpen: state.sidebar.isOpen,
    items: state.sidebar.items,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(sideBarActions, dispatch),
  };
}

function getAllowedItems(items = []) {
  const isSectioned = !!last(items)?.section;
  const allowedRoutes = getAllAllowedRoutes(PrivateRoutesConfig);
  const isMatchAllowedRoute = (item) =>
    allowedRoutes.find((route) =>
      matchPath(item.href, {
        path: route.path,
        exact: true,
      }),
    );

  if (isSectioned) {
    return items.map((item) => ({
      ...item,
      section: {
        ...item.section,
        items: item.section.items.filter(isMatchAllowedRoute),
      },
    }));
  } else {
    return items.filter(isMatchAllowedRoute);
  }
}

class SideBar extends Component {
  state = {
    isTransition: false,
    isOpen: this.props.isOpen,
  };

  static propTypes = {
    isOpen: PropTypes.bool,
    isHidden: PropTypes.bool,
    onToggle: PropTypes.func,
  };

  static defaultProps = {
    isOpen: true,
    isHidden: false,
    onToggle: () => {},
  };

  getAllowedItems = memoize(getAllowedItems);

  get items() {
    return this.getAllowedItems(this.props.items);
  }

  shouldComponentUpdate(nextProps, nextState) {
    return (
      nextProps.isOpen !== this.props.isOpen ||
      nextProps.isHidden !== this.props.isHidden ||
      nextProps.items !== this.props.items ||
      nextState.isOpen !== this.state.isOpen ||
      nextState.isTransition !== this.state.isTransition ||
      nextProps.location.pathname !== this.props.location.pathname
    );
  }

  onToggle = (e) => {
    e.preventDefault();
    this.startTransition();
    const { isOpen } = this.state;
    this.setState({ isOpen: !isOpen });
    this.props.actions.update({ isOpen: !isOpen });
    setTimeout(() => this.finishTransition(), 150);
  };

  startTransition() {
    this.setState({ isTransition: true });
  }

  finishTransition() {
    this.setState({ isTransition: false });
  }

  render() {
    const { items } = this;
    const { isOpen, isTransition } = this.state;
    const {
      isNo,
      isHidden,
      location: { pathname },
    } = this.props;

    return (
      <div className={cn("SideBar-Container")}>
        {!(isNo || isHidden) && (
          <div className={cn("SideBar", isOpen && "SideBar_opened")}>
            <div className="SideBar-Items">
              {map(items, (o) =>
                o.section ? (
                  <Section key={camel(o.section.title)} title={isOpen ? o.section.title : ""} isOpen={isOpen}>
                    {map(o.section.items, (nav) => (
                      <Nav
                        key={camel(nav.title)}
                        to={nav.href}
                        hasIcon={!isOpen}
                        renderIcon={nav.renderIcon}
                        name={nav.name}
                        title={nav.title}
                        hintText={nav.hintText}
                        extraText={nav.extraText}
                        hasTitle={isOpen && !isTransition}
                        isActive={pathname.includes(path(nav.href))}
                      />
                    ))}
                  </Section>
                ) : (
                  <Nav
                    key={camel(o.title)}
                    to={o.href}
                    hasIcon={!isOpen}
                    renderIcon={o.renderIcon}
                    name={o.name}
                    title={o.title}
                    hintText={o.hintText}
                    extraText={o.extraText}
                    hasTitle={isOpen && !isTransition}
                    isActive={o.isExact ? pathname === path(o.href) : pathname.includes(path(o.href))}
                    isDisabled={o.isDisabled}
                  />
                ),
              )}
            </div>
            <div onClick={this.onToggle} className="SideBar-Toggle">
              <ToggleIcon className="SideBar-ToggleIcon" />
              <ToggleSmallIcon className="SideBar-ToggleSmallIcon" />
            </div>
          </div>
        )}
        <div className="SideBar-Content" style={isNo || isHidden ? { padding: 0 } : {}}>
          {this.props.children}
        </div>
      </div>
    );
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SideBar));
