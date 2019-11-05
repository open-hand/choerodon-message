import React from 'react';
import { Form, TextField, TextArea, Select, Table, CheckBox } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';

const { Column } = Table;

const CreateAndEditWebhooksForm = observer(({ dataSet, triggerEventsSettingDataSet }) => {
  const checkBoxRenderer = ({ value, record }) => {
    const handleNodeChecked = (clicked) => (clicked ? triggerEventsSettingDataSet.select(record) : triggerEventsSettingDataSet.unSelect(record));
    return (
      <CheckBox
        checked={record.isSelected}
        onChange={handleNodeChecked}
      />
    );
  };
  const checkBoxHeaderRenderer = () => {
    const handleHeaderChecked = (value) => (value ? triggerEventsSettingDataSet.selectAll() : triggerEventsSettingDataSet.unSelectAll());
    return (
      <CheckBox
        checked={triggerEventsSettingDataSet.every((item) => item.isSelected)}
        onChange={handleHeaderChecked}
      />
    );
  };
  return (
    <React.Fragment>
      <Form dataSet={dataSet} style={{ width: '5.12rem' }}>
        <TextField name="name" />
        <Select name="type" />
        {dataSet.current && dataSet.current.get('type') === 'DingTalk' && <TextArea name="secret" />}
        <TextArea name="webhookPath" />
      </Form>
      <Table dataSet={triggerEventsSettingDataSet} mode="tree">
        <Column name="name" />
        <Column name="description" />
      </Table>
    </React.Fragment>

  );
});

export default CreateAndEditWebhooksForm;
