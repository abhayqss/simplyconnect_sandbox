import React from "react";

import cn from "classnames";
import PropTypes from "prop-types";

import Logo from "../Logo/Logo";

import "./Footer.scss";

function Footer({ theme, hasLogo, children, className, width }) {
  const year = new Date().getFullYear();
  return (
    <div className={cn("Footer", `Footer_theme_${theme}`, className)} style={{ width: width }}>
      <div className="Footer-Body">
        <div className="h-flexbox">
          {hasLogo ? (
            <Logo iconSize={17} className="Footer-Logo margin-right-8" />
          ) : (
            <div className="Footer-LogoFallback" />
          )}
          <div className="Footer-Copyright">© {year} Simply Connect</div>
        </div>
        <div className="h-flexbox">{children}</div>
        <div className="h-flexbox">
          <a
            target="_blank"
            rel="noopener noreferer"
            href="https://simplyconnect.me/privacy-policy/"
            className="Footer-PrivacyPolicyLink margin-right-24"
          >
            Privacy Policy
          </a>
          <a
            target="_blank"
            rel="noopener noreferer"
            href="https://simplyconnect.me/terms-of-use/"
            className="Footer-TermsOfUse"
          >
            Terms of Use
          </a>
        </div>
      </div>
    </div>
  );
}

Footer.propTypes = {
  hasLogo: PropTypes.bool,
  className: PropTypes.string,
  theme: PropTypes.oneOf(["black", "gray"]),
};

Footer.defaultProps = {
  hasLogo: true,
};

export default Footer;
