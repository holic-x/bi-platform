import { Footer } from '@/components';

import { listChartByPageUsingPost } from '@/services/noob-bi/chartController';

import { useModel } from '@umijs/max';
import React, { useEffect,useState } from 'react';

import { InboxOutlined, UploadOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Select,
  Space,
  Upload,
} from 'antd';
import TextArea from 'antd/es/input/TextArea';



const AddChart: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const { setInitialState } = useModel('@@initialState');

  useEffect(()=>{
    listChartByPageUsingPost({}).then(res=>{
      console.error('res',res)
    })
  });

  const onFinish = (values: any) => {
    console.log('Received values of form: ', values);
  };

  return (
    // 页面信息定义（add-chart）
    <div className = "add-chart">
      <Form
        // 设定表单名称
        name="addChart"
        onFinish={onFinish}
        // 初始化数据为空
        initialValues={{ }}
        style={{ maxWidth: 600 }}
      >

        <Form.Item name="rate" label="Rate">
          <TextArea />
        </Form.Item>

        <Form.Item
          name="select"
          label="Select"
          hasFeedback
          rules={[{ required: true, message: 'Please select your country!' }]}
        >
          <Select placeholder="Please select a country">
            <Option value="china">China</Option>
            <Option value="usa">U.S.A</Option>
          </Select>
        </Form.Item>

        <Form.Item
          name="upload"
          label="Upload"
          valuePropName="fileList"
          extra="longgggggggggggggggggggggggggggggggggg"
        >
          <Upload name="logo" action="/upload.do" listType="picture">
            <Button icon={<UploadOutlined />}>Click to upload</Button>
          </Upload>
        </Form.Item>

        <Form.Item wrapperCol={{ span: 12, offset: 6 }}>
          <Space>
            <Button type="primary" htmlType="submit">
              Submit
            </Button>
            <Button htmlType="reset">reset</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  
  );
};
export default AddChart;
