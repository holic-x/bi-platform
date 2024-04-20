

import React, { useState } from 'react';

import { genChartByAiSyncUsingPost } from '@/services/noob-bi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import {
  Button,
  Card,
  Col,
  Divider,
  Form,
  Input,
  Row,
  Select,
  Space,
  Spin,
  Upload,
  message
} from 'antd';
import TextArea from 'antd/es/input/TextArea';
import EChartsReact from 'echarts-for-react';



const AddChart: React.FC = () => {
  // 定义状态，接收后端返回值，实时展示在页面上
  const [chart,setChart] = useState<API.BiResponse>();
  const [option,setOption] = useState<any>();
  // 提交中的状态，默认未提交
  const [submitting,setSubmitting] = useState<boolean>(false);

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

    // 如果提交了，则将图表数据和图表代码清空（避免和之前提交的图表堆叠）；如果option清空了则组件会触发重新渲染，不会保留之前的历史记录
    setChart(undefined);
    setOption(undefined);
  
    // 对接后端，上传数据
    const params = {
      ...values,
      file: undefined,
    };
    try {
      // 获取到上传的原始数据并传入后端接口
      const res = await genChartByAiSyncUsingPost(params, {}, values.file.file.originFileObj);
      // 一般情况下没有返回值为分析失败，有则认为成功
      if (!res?.data) {
        message.error('分析失败');
      } else {
        message.success('分析成功');
        // 解析成对象，为空则设置为空字符串
        const chartOption = JSON.parse(res.data.genChart??'');
        // 如果为空，则抛出异常，提示图表代码解析错误
        if(!chartOption){
          throw new Error('图表代码解析错误');
        }else{
          // 解析成功，则将响应结果设置到图表中进行渲染
          setChart(res.data);
          setOption(chartOption);
        }
      }
    } catch (e: any) {
      // 异常情况下，提示分析失败和具体的原因说明
      message.error('分析失败，' + e.message);
    }

    // 提交结束，将setSubmitting设置为false
    setSubmitting(false);
  };

  return (
    // 页面展示美化


    // 页面信息定义（add-chart）
    <div className = "add-chart">
      <Row gutter={24}>
        <Col span={12}>
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
        </Col>

        <Col span={12}>
          <Card title="分析结论">
            {chart?.genResult ?? <div>请先在左侧进行提交</div>}
            <Spin spinning={submitting}/>
          </Card>
          <Divider />
          <Card title="可视化图表">
            {
              option ? <EChartsReact option={option} /> : <div>请先在左侧进行提交</div>
            }
            <Spin spinning={submitting}/>
          </Card>
        </Col>
      </Row>
      
    </div>
  );
};
export default AddChart;
