

import React, { useState } from 'react';

import { UploadOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Form,
  Input,
  Select,
  Space,
  Upload,
  message
} from 'antd';
import { useForm } from 'antd/es/form/Form';
import TextArea from 'antd/es/input/TextArea';
import { genChartByAiAsyncMqUsingPost } from '@/services/noob-bi/chartController';



const AddChartAsyncMQ: React.FC = () => {
  // 提交中的状态，默认未提交
  const [submitting,setSubmitting] = useState<boolean>(false);

  const [form] = useForm();

  /**
   * 提交
   * @param values 
   */
  const onFinish = async (values: any) => {
    console.log('Received values of form: ', values);

    // 如果已经提交中的状态（加载），则直接返回避免重复提交
    if(submitting){
      return;
    }

    // 开始提交，将submitting设置为true
    setSubmitting(true);

    // 对接后端，上传数据
    const params = {
      ...values,
      file: undefined,
    };
    try {
      // 获取到上传的原始数据并传入后端接口
      const res = await genChartByAiAsyncMqUsingPost(params, {}, values.file.file.originFileObj);
      // 一般情况下没有返回值为分析失败，有则认为成功
      if (!res?.data) {
        message.error('分析失败');
      } else {
        message.success('分析任务提交成功，稍后请在【我的图表】页面查看');
        // 提交成功，重置所有字段
        form.resetFields();
      }
    } catch (e: any) {
      // 异常情况下，提示分析失败和具体的原因说明
      message.error('分析失败，' + e.message);
    }

    // 提交结束，将setSubmitting设置为false
    setSubmitting(false);
  };

  return (
    // 页面信息定义（add-chart-async）
    <div className = "add-chart-async">
          <Card title="智能分析">
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
            </Card>
    </div>
  );
};
export default AddChartAsyncMQ;
