import React, { memo, useRef, useState, useEffect, useCallback } from "react";

import cn from "classnames";

import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { useLocation } from "react-router";

import OutsideClickListener from "components/OutsideClickListener/OutsideClickListener";

import User from "../../User/User";

import { ReactComponent as Cross } from "images/close.svg";
import { ReactComponent as Icon } from "images/hamburger.svg";
import { ReactComponent as MobileLogo } from "images/simplyconnect-mobile-logo.svg";

import { path } from "lib/utils/ContextUtils";

import "./MobileNavigation.scss";

const TIME_TO_CLOSE_NAVBAR = 300;

const getHref = (o) => path(o.href);
const getUser = (state) => state.auth.login.user.data;

const NavItems = memo(function NavItems({ items, onClickItem, isItemActive }) {
  return (
    <>
      {items.map((o) => (
        <div key={o.href} className={cn("NavigationMobile-Item", { "NavigationMobile-Item__Active": isItemActive(o) })}>
          <Link to={getHref(o)} onClick={onClickItem}>
            {o.title}
          </Link>

          {o.hasIndicator && typeof o.unreadChatsCount === "number" && o.unreadChatsCount > 0 && (
            <div className="Navigation-Indicator">{o.unreadChatsCount > 99 ? "99+" : o.unreadChatsCount}</div>
          )}
        </div>
      ))}
    </>
  );
});

NavItems.defaultProps = {
  items: [],
};

function MobileNavigation({ items }) {
  let location = useLocation();
  let user = useSelector(getUser);

  const listRef = useRef();

  let [isOpen, setIsOpen] = useState(false);

  let loggedIn = user !== null;

  const isNavItemActive = useCallback((o) => location.pathname.includes(getHref(o)), [location.pathname]);

  const open = useCallback(() => setIsOpen(true), []);

  const close = useCallback(() => setIsOpen(false), []);

  function resetScrollPosition() {
    let element = listRef.current;

    if (!isOpen && element) {
      setTimeout(() => element.scroll(0, 0), TIME_TO_CLOSE_NAVBAR);
    }
  }

  useEffect(resetScrollPosition, [isOpen]);

  return (
    <OutsideClickListener onClick={close} className="NavigationMobile hide-on-tablet-and-laptop">
      <div className="NavigationMobile-Bar">
        <div className="NavigationMobile-Icon">
          <Icon onClick={open} />
        </div>

        <MobileLogo className="NavigationMobile-Logo" />

        <div>{loggedIn && <User avatarSize={40} className="NavigationMobile-User" />}</div>
      </div>

      {loggedIn && (
        <div ref={listRef} className={cn("NavigationMobile-Navs", { "NavigationMobile-Navs_open": isOpen })}>
          <div className="NavigationMobile-Navs-Buttons">
            <Cross onClick={close} className="NavigationMobile-CrossIcon" />
            <MobileLogo height={44} className="NavigationMobile-Logo" />
          </div>
          <div className="NavigationMobile-Items">
            <NavItems items={items} onClickItem={close} isItemActive={isNavItemActive} />
          </div>
        </div>
      )}
    </OutsideClickListener>
  );
}

export default MobileNavigation;
