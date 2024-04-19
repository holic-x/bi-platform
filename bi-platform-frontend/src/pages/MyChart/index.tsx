import { listChartByPageUsingPost } from '@/services/noob-bi/chartController';
import { message,Avatar, List, Space } from 'antd';
import EChartsReact from 'echarts-for-react';
import React, { useEffect, useState } from 'react';

const MyChart: React.FC = () => {

  // 构建初始条件（便于后面恢复初始条件）
  const initSearchParams = {
    // 初始情况每页数据返回12条
    pageSize:12,
  }

  /**
   * 定义一个状态（searchParams）和其对应的更新函数（setSearchParams），初始化为initSearchParams
   * searchParams是发送给后端的查询条件，参数类型是API。ChartQueryRequest
   * {...}是展开语法，将initSearchParams中的所有属性展开并复制到一个新对象中（不改变原始对象，可以避免在现有对象直接更改值的对象变异操作）
   * React中不推荐直接修改状态或者属性，而是通过创建要一个新对象并将其分配给状态或属性
   */
  const [searchParams,setSearchParams] = useState<API.ChartQueryRequest>({...initSearchParams});

  // 定义变量存储图表数据
  const [chartList,setChartList] = useState<API.Chart[]>();
  // 定义数据总数（类型为number、默认为0）
  const [total,setTotal] = useState<number>(0);

  // 定义一个获取数据的异步函数
  const loadData = async()=>{
    /**
     * 调用后端接口，传入serchParams请求参数并返回响应结果
     * listChartByPageUsingPost是通过openAPI生成的接口
     * 当searchParam状态改变时，可通过setSearchParams更新该状态并重新获取数据
     */
      try {
        const res = await listChartByPageUsingPost(searchParams);
        // 响应成功，将图表数据进行渲染（如果为空则传入空数组，分页数据则拿到数据列表）
        if(res.data){
          setChartList(res.data.records ?? []);
          // 数据总数：数据列表如果为空则返回0
          setTotal(res.data.total ?? 0);
        }else{
          // 如果后端返回数据为空，抛出异常（获取图表失败）
          message.error("获取图表失败");
        }
      } catch (e:any) {
        // 出现异常，提示失败西悉尼
        message.error('图表获取失败'+e.message);
      }
  }

  // 页面首次加载，触发加载数据
  useEffect(()=>{
    // 调用方法加载数据（页面首次渲染以及数组中的搜索条件发生变化的时候执行loadData方法触发搜索）
    loadData();
  },[searchParams])

  return (
    // 页面信息定义（my-chart）
    <div className = "my-chart">

      <List
          itemLayout="vertical"
          size="large"
          pagination={{
            onChange: (page) => {
              console.log(page);
            },
            pageSize: 3,
          }}
          // 数据源改成图表数据（列表组件会自动渲染）
          dataSource={chartList}
          footer={
            <div>
              <b>ant design</b> footer part
            </div>
          }
          renderItem={(item) => (
            // List.Item 要展示的每一条数据
            <List.Item
              // key 对应图表id
              key={item.id}
              // 要展示的图表信息
              extra={
                <img
                  width={272}
                  alt="logo"
                  src="https://gw.alipayobjects.com/zos/rmsportal/mqaQswcyDLcXyDKnZfES.png"
                />
              }
            >
              {/* 要展示的图表元素信息 */}
              <List.Item.Meta
                // 头像（todo）
                avatar={<Avatar src={'https://cos.holic-x.com/profile/avatar/avatar02.png'} />}
                // 图表名称
                title={<a href={item.href}>{item.name}</a>}
                // 描述
                description={item.chartType?'图表类型'+item.chartType:undefined}
              />
              {/* 要展示的内容 */}
              {'分析目标：'+item.goal}

              {/* 图表信息展示 */}
              <EChartsReact option={JSON.parse(item.genChart??'{}')} />

            </List.Item>
          )}
        />
    </div>
  );
};
export default MyChart;