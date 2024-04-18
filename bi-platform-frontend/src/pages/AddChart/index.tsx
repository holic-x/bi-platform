
import { genChartByAiUsingPost, listChartByPageUsingPost } from '@/services/noob-bi/chartController';

import { useModel } from '@umijs/max';
import React, { useEffect, useState } from 'react';

import { UploadOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  Select,
  Space,
  Upload,
  message
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

  const onFinish = async (values: any) => {
    console.log('Received values of form: ', values);
  
    // 对接后端，上传数据
    const params = {
      ...values,
      file: undefined,
    };
    try {
      // 获取到上传的原始数据并传入后端接口
      const res = await genChartByAiUsingPost(params, {}, values.file.file.originFileObj);
      // 一般情况下没有返回值为分析失败，有则认为成功
      if (!res?.data) {
        message.error('分析失败');
      } else {
        message.success('分析成功');
      }
    } catch (e: any) {
      message.error('分析失败，' + e.message);
    }
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

        {/* 前端表单的name属性对应后端接口请求参数的字段，name对应goal，label为左侧提示文本，rules=....是必填项提示 */}

        <Form.Item name="goal" label="分析目标" rules={[{required:true,message:"请输入分析目标"}]}>
          {/* placeholder文本框提示语 */}
          <TextArea placeholder="请输入分析需求，例如：分析网站用户的增长情况"/>
        </Form.Item>

        {/* 图表名称 */}
        <Form.Item name="name" label="图表名称">
          {/* placeholder文本框提示语 */}
          <Input placeholder="请输入分析需求，例如：分析网站用户的增长情况"/>
        </Form.Item>


        {/* 图表类型非必填，不做校验 */}
        <Form.Item
          name="selchartTypeect"
          label="图表类型"
        >
          <Select placeholder="请选择图表类型"
            options={[
              {value:'折线图',label:'折线图'},
              {value:'柱状图',label:'柱状图'},
              {value:'堆叠图',label:'堆叠图'},
              {value:'饼图',label:'饼图'},
              {value:'雷达图',label:'雷达图'},
            ]}
          />
        </Form.Item>

        {/* 文件上传 */}
        <Form.Item
          name="file"
          label="原始数据"
        >
          {/* action：文件上传之后 调用后台接口  action="/upload.do" */}
          <Upload name="file">
            <Button icon={<UploadOutlined />}>上传CSV文件</Button>
          </Upload>
        </Form.Item>

        <Form.Item wrapperCol={{ span: 12, offset: 6 }}>
          <Space>
            <Button type="primary" htmlType="submit">
              提交
            </Button>
            <Button htmlType="reset">重置</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  
  );
};
export default AddChart;
