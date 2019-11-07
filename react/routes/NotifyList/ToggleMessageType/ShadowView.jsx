import React from 'react';
import ReactDOM from 'react-dom';

// 利用 ReactDOM.createPortal 的实现
export function ShadowContent({ root, children }) {
  return ReactDOM.createPortal(children, root);
}

export default class ShadowView extends React.Component {
  constructor(props) {
    super(props);
    this.state = { root: null };
  }

  setRoot = eleemnt => {
    if (eleemnt) {
      const root = eleemnt.attachShadow({ mode: 'open' });
      this.setState({ root });
    }
  };

  render() {
    const { children } = this.props;
    const { root } = this.state;
    return (
      <div ref={this.setRoot}>
        {root && (
        <ShadowContent root={root}>
          {children}
        </ShadowContent>
        )}
      </div>
    );
  }
}
