import React, { Component } from 'react';
import ReactQuill, { Quill } from 'react-quill';
import { axios, Content, Choerodon } from '@choerodon/boot';
import 'react-quill/dist/quill.snow.css';
import './Editor.scss';
import { Modal, Input, Button, Form, Tabs, Upload, Icon } from 'choerodon-ui';
import { FormattedMessage, injectIntl } from 'react-intl';
import ChoerodonEditor from '../choerodonEditor';
import CustomToolbar from './CustomToolbar';

const { TabPane } = Tabs;
const { Dragger } = Upload;
const FormItem = Form.Item;
const limitSize = 5120;
const Align = Quill.import('attributors/style/align');
Align.whitelist = ['right', 'center', 'justify'];
Quill.register(Align, true);

const Size = Quill.import('attributors/style/size');
Size.whitelist = ['10px', '12px', '14px', '16px', '18px', '20px'];
Quill.register(Size, true);

const Font = Quill.import('attributors/style/font');
Font.whitelist = ['STSong', 'STKaiti', 'STHeiti', 'STFangsong', 'SimSun', 'KaiTi', 'SimHei', 'FangSong', 'Microsoft-YaHei'];
Quill.register(Font, true);


@Form.create()
@injectIntl
export default class Editor extends Component {
  constructor(props) {
    super(props);
    this.urlFocusInput = React.createRef();
    this.onQuillChange = this.onQuillChange.bind(this);
    this.state = {
      htmlString: null,
      isShowModal: false,
      previewUrl: null, // 网络上传预览图片url
      range: null,
      file: null,
      localSrc: null, // 本地图片上传前的blob
      submitting: false,
      type: 'online',
      isCode: false,
    };
  }

  // eslint-disable-next-line react/no-deprecated
  componentWillMount() {
    this.originValue = this.props.value;
    if (this.props.onRef) {
      this.props.onRef(this);
    }
  }

  // 点击code按钮
  changeToHtml = () => {
    Modal.confirm({
      title: 'html代码编辑器',
      content: '切换编辑器后将无法切换回现有编辑器，且现有编辑内容将会丢失，是否继续？',
      onOk: () => {
        this.props.onChange(this.originValue);
        this.setState({ isCode: true });
      },
    });
  }

  initEditor = () => {
    if (this.state.htmlString) {
      this.props.onChange(this.state.htmlString);
    }
  }

  handleChangedHTML = (e) => {
    this.setState({
      htmlString: e.target.value,
    }, () => {
      this.props.onChange(this.state.htmlString);
    });
  }

  // 开启上传图片模态框
  handleOpenModal = () => {
    const range = this.quillRef.getEditor().getSelection();
    const { resetFields } = this.props.form;
    resetFields();
    this.setState({
      isShowModal: true,
      previewUrl: null,
      file: null,
      localSrc: null,
      type: 'online',
      range,
    });
  }

  // 关闭图片模态框
  handleCloseModal = () => {
    this.setState({
      isShowModal: false,
    });
  }

  // 预览图片
  previewPic = () => {
    const { getFieldValue } = this.props.form;
    this.setState({
      previewUrl: getFieldValue('imgUrl'),
    });
  }

  loadImage(src) {
    this.setState({ localSrc: src });
  }

  getUploadProps() {
    const { intl } = this.props;
    return {
      multiple: false,
      name: 'file',
      accept: 'image/jpeg, image/png, image/jpg, image/gif',
      action: `${Choerodon.API_HOST}/file/v1/file?/bucket_name=file&file_name=file`,
      headers: {
        Authorization: `bearer ${Choerodon.getCookie('access_token')}`,
      },
      showUploadList: false,
      beforeUpload: (file) => {
        const { size } = file;
        if (size > limitSize * 1024) {
          Choerodon.prompt(intl.formatMessage({ id: 'editor.file.size.limit' }, { size: `${limitSize / 1024}M` }));
          return false;
        }
        this.setState({ file });
        const windowURL = window.URL || window.webkitURL;
        if (windowURL && windowURL.createObjectURL) {
          this.loadImage(windowURL.createObjectURL(file));
          return false;
        }
      },
      onChange: ({ file }) => {
        const { status, response } = file;
        if (status === 'done') {
          this.loadImage(response);
        } else if (status === 'error') {
          Choerodon.prompt(`${response.message}`);
        }
      },
    };
  }

  insertToEditor = (url = null) => {
    const { type, range } = this.state;
    if (type === 'online') {
      this.props.form.validateFieldsAndScroll((err, values) => {
        if (!err) {
          // Quill.sources.USER
          this.quillRef.getEditor().insertEmbed(range.index, 'image', values.imgUrl); // 在当前光标位置插入图片
        }
      });
    } else {
      this.quillRef.getEditor().insertEmbed(range.index, 'image', url); // 在当前光标位置插入图片
    }
    this.quillRef.getEditor().setSelection(range.index + 1); // 移动光标位置至图片后
    this.setState({
      isShowModal: false,
    });
  }

  handleOk = () => {
    const { type } = this.state;
    if (type === 'online') {
      this.insertToEditor();
    } else {
      const { file } = this.state;
      const data = new FormData();
      data.append('file', file);
      this.setState({ submitting: true });
      axios.post(`${Choerodon.API_HOST}/file/v1/files?bucket_name=file&file_name=file`, data)
        .then((res) => {
          if (res.failed) {
            Choerodon.prompt(res.message);
          } else {
            this.insertToEditor(res);
          }
          this.setState({ submitting: false });
        })
        .catch((error) => {
          Choerodon.handleResponseError(error);
          this.setState({ submitting: false });
        });
    }
  };

  changeUploadType = (type) => {
    const { resetFields } = this.props.form;
    resetFields();
    this.setState({
      file: null,
      localSrc: null,
      previewUrl: null,
      type,
    });
  }


  /**
   *
   * @param content HTML格式的内容
   * @param delta delta格式的内容
   * @param source change的触发者 user/silent/api
   * @param editor 文本框对象
   */
  onQuillChange = (content, delta, source, editor) => {
    if (this.props.onChange) this.props.onChange(content);
  }

  renderLocal = () => {
    const props = this.getUploadProps();
    const { localSrc } = this.state;
    return (
      <React.Fragment>
        <Dragger className="c7n-iam-editor-dragger" {...props}>
          {
            localSrc ? (
              <React.Fragment>
                <div style={{ backgroundImage: `url(${localSrc})` }} className="c7n-iam-editor-dragger-preview-pic" />
              </React.Fragment>
            ) : (
              <React.Fragment>
                <Icon type="inbox" />
                <h3 className="c7n-iam-editor-dragger-text">
                  <FormattedMessage id="editor.dragger.text" />
                </h3>
                <h4 className="c7n-iam-editor-dragger-hint">
                  <FormattedMessage
                    id="editor.dragger.hint"
                    values={{ size: `${limitSize / 1024}M`, access: 'PNG、JPG、JPEG、GIF' }}
                  />
                </h4>
              </React.Fragment>
            )
          }
        </Dragger>
      </React.Fragment>
    );
  }

  renderOnline = () => {
    const { previewUrl } = this.state;
    const { getFieldDecorator } = this.props.form;
    const { intl } = this.props;
    return (
      <React.Fragment>
        <div className="c7n-iam-editor-modal-preview-top">
          <Form
            style={{ display: 'inline-block' }}
          >
            <FormItem>
              {
                getFieldDecorator('imgUrl', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: 'editor.pic.url.required' }),
                  }],
                })(
                  <Input
                    ref={(e) => {
                      this.urlFocusInput = e;
                    }}
                    style={{ width: '438px', verticalAlign: 'middle' }}
                    label={<FormattedMessage id="editor.pic.url" />}
                    id="c7n-iam-editor-input"
                    autoComplete="off"
                  />,
                )
              }
            </FormItem>
          </Form>
          <Button
            className="c7n-iam-editor-modal-preview-top-btn"
            funcType="raised"
            onClick={this.previewPic}
          >
            <FormattedMessage id="editor.view" />
          </Button>
        </div>
        <div className="c7n-iam-editor-modal-preview-content">
          <div className="c7n-iam-editor-modal-preview-sentence">
            <FormattedMessage id="editor.preview" />
          </div>
          <div style={{ backgroundImage: `url(${previewUrl})` }} className="c7n-iam-editor-modal-preview-pic" />
        </div>
      </React.Fragment>
    );
  }

  modules = {
    toolbar: {
      container: `#${this.props.toolbarContainer}` || '#toolbar',
      handlers: {
        image: this.handleOpenModal,
        'code-block': this.changeToHtml,
      },
    },
  }

  formats = [
    'bold',
    'italic',
    'underline',
    'header',
    'list',
    'bullet',
    'link',
    'image',
    'color',
    'open',
    'font',
    'size',
    'align',
    'code-block',
  ];

  defaultStyle = {
    width: this.props.width || '100%',
    height: this.props.height || 320,
  };

  render() {
    const { value } = this.props;
    const { isCode, isShowModal, htmlString, submitting, localSrc, type } = this.state;
    const style = { ...this.defaultStyle, ...this.props.style };
    const editHeight = style.height - 42;
    const modalFooter = [
      <Button disabled={submitting} key="cancel" onClick={this.handleCloseModal}>
        <FormattedMessage id="cancel" />
      </Button>,
      <Button key="save" type="primary" disabled={!localSrc && type === 'local'} loading={submitting} onClick={this.handleOk}>
        <FormattedMessage id="save" />
      </Button>,
    ];
    if (isCode) {
      return (
        <ChoerodonEditor
          value={value}
          onChange={this.props.onChange}
        />
      );
    }
    return (
      <div style={style} className="c7n-iam-react-quill-editor">
        <CustomToolbar nomore={this.props.nomore} toolbarContainer={this.props.toolbarContainer || 'toolbar'} />
        <ReactQuill
          id="c7n-iam-editor"
          theme="snow"
          modules={this.modules}
          formats={this.formats}
          style={{ height: editHeight }}
          value={value}
          onChange={this.onQuillChange}
          bounds="#c7n-iam-editor"
          ref={(el) => { this.quillRef = el; }}
        />
        <Modal
          width={560}
          visible={isShowModal}
          maskClosable={false}
          closable={false}
          title={<FormattedMessage id="editor.add.pic" />}
          okText={<FormattedMessage id="add" />}
          onCancel={this.handleCloseModal}
          onOk={this.savePic}
          footer={modalFooter}
        >
          <Tabs onChange={this.changeUploadType} activeKey={type} style={{ marginTop: '10px' }}>
            <TabPane tab={<FormattedMessage id="editor.online.pic" />} key="online">
              {this.renderOnline()}
            </TabPane>
            <TabPane tab={<FormattedMessage id="editor.local.upload" />} key="local">
              {this.renderLocal()}
            </TabPane>
          </Tabs>
        </Modal>
      </div>
    );
  }
}
