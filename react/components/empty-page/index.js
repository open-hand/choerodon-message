import React from 'react';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router-dom';
import { Button } from 'choerodon-ui/pro';

import './index.less';

const EmptyPage = withRouter(((props) => {
  const {
    history,
    location: { search },
    pathname,
    access,
    title,
    describe,
    btnText,
    onClick,
  } = props;

  function handleClick() {
    history.push({
      pathname,
      search,
    });
  }

  return (
    <div className="c7n-notify-empty-page-wrap">
      <div className="c7n-notify-empty-page">
        <div className={`c7n-notify-empty-page-image c7n-notify-empty-page-image-${access ? 'owner' : 'member'}`} />
        <div className="c7n-notify-empty-page-text">
          <div className="c7n-notify-empty-page-title">
            {title}
          </div>
          <div className="c7n-notify-empty-page-des">
            {describe}
          </div>
          {(access && btnText) && (
            <Button
              color="primary"
              onClick={onClick || handleClick}
              funcType="raised"
            >
              {btnText}
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}));

EmptyPage.propTypes = {
  pathname: PropTypes.string,
  access: PropTypes.bool,
  title: PropTypes.string,
  btnText: PropTypes.string,
  describe: PropTypes.string,
  onClick: PropTypes.func,
};

EmptyPage.defaultProps = {
  pathname: '',
  access: false,
  btnText: '',
};

export default EmptyPage;
