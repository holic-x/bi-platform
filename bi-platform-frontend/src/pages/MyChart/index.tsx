import { listChartByPageUsingPost } from '@/services/noob-bi/chartController';
import { useModel } from '@@/exports';
import { Avatar, List, Result, message } from 'antd';
import Search from 'antd/es/input/Search';
import EChartsReact from 'echarts-for-react';
import React, { useEffect, useState } from 'react';

const MyChart: React.FC = () => {

  // 构建初始条件（便于后面恢复初始条件）
  const initSearchParams = {
    // 默认第一页
    current: 1,
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

  // 从全局状态中获取登陆用户信息
  const {initialState} = useModel('@@initialState');
  const {currentUser} = initialState??{};
  // 定义数据加载状态（控制页面是否加载：默认加载）
  // const {loading,setLoading} = useState<boolean>(true);

  // 定义变量存储图表数据
  const [chartList,setChartList] = useState<API.Chart[]>();
  // 定义数据总数（类型为number、默认为0）
  const [total,setTotal] = useState<number>(0);

  // 定义一个获取数据的异步函数
  const loadData = async()=>{
    // 获取数据（此时页面还在加载中，设定加载属性）
    // setLoading(true);

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

          // 列表数据处理（有些图表有标题、有些没有，此处过滤掉不展示标题信息）
          if(res.data.records){
            res.data.records.forEach(data => {
              // 当图表状态为succeed的时候才解析图表代码（渲染数据）
              if(data.status === 'succeed'){
                // 将后端返回的图表字符串修改为对象数组，如果后端返回空字符串则返回{}
                const chartOption = JSON.parse(data.genChart??'{}');
                // 标题设置为undefined
                chartOption.title = undefined;
                // 将修改后的option重新赋值给原genChart字段
                data.genChart = JSON.stringify(chartOption);
              }
            });
          }

        }else{
          // 如果后端返回数据为空，抛出异常（获取图表失败）
          message.error("获取图表失败");
        }
      } catch (e:any) {
        // 出现异常，提示失败西悉尼
        message.error('图表获取失败'+e.message);
      }

      // 数据获取成功，加载完毕，将加载状态设置为false
      // setLoading(false);
  }

  // 页面首次加载，触发加载数据
  useEffect(()=>{
    // 调用方法加载数据（页面首次渲染以及数组中的搜索条件发生变化的时候执行loadData方法触发搜索）
    loadData();
  },[searchParams])

  return (
    // 页面信息定义（my-chart）
    <div className = "my-chart">
      {/* 搜索框引入 */}
      <div>
        
        {/* <Search placeholder='请输入图表名称' enterButton loading={loading} onSearch={(value)=>{ */}
        <Search placeholder='请输入图表名称' enterButton onSearch={(value)=>{
          // 设置搜索条件
          setSearchParams({
            // 原始搜索条件
            ...initSearchParams,
            // 搜索词
            name:value,
          });
        }}></Search>
      </div>

      <div className='margin-16'/>
      {/* 数据引入 */}
      <List
          itemLayout="vertical"
          size="large"

          // 设置组件样式（栅栏格式）
          grid={{
            gutter: 16,
            xs: 1,
            sm: 1,
            md: 1,
            lg: 2,
            xl: 2,
            xxl: 2,
          }}

          // 分页组件定义
          pagination={{
            // 当切换分页，在当前搜索条件的基础上，将页数调整为当前的页数
            onChange: (page,pageSize) => {
              setSearchParams({
                ...searchParams,
                current:page,
                pageSize,
              })
            },
            // 显示当前页数
            current: searchParams.current,
            // 页面参数、总数修改为自己的
            pageSize: searchParams.pageSize,
            total: total,
          }}

          // loading状态调整为自己的状态
          // loading = {loading};

          // 数据源改成图表数据（列表组件会自动渲染）
          dataSource={chartList}
          
          renderItem={(item) => (
            // List.Item 要展示的每一条数据
            <List.Item
              // key 对应图表id
              key={item.id}
              // 要展示的图表信息
              /*
              extra={
                <img
                  width={272}
                  alt="logo"
                  src="https://gw.alipayobjects.com/zos/rmsportal/mqaQswcyDLcXyDKnZfES.png"
                />
              }
              */
            >
              {/* 要展示的图表元素信息 */}
              <List.Item.Meta
                // 头像（todo）
                // avatar={<Avatar src={'https://cos.holic-x.com/profile/avatar/avatar02.png'} />}
                avatar={<Avatar src={currentUser&&currentUser.userAvatar} />}
                // 图表名称(链接跳转查看详情)
                title={<a href={item.href}>{item.name}</a>}
                // 描述
                description={item.chartType?'图表类型'+item.chartType:undefined}
              />
              
              {/* <div style={{marginBottom:16}}></div> */}

              {/* 要展示的内容 */}
              {/* {'分析目标：'+item.goal} */}
              {/* <div style={{marginBottom:16}}></div> */}

              {/* 图表信息展示 */}
              {/* <EChartsReact option={JSON.parse(item.genChart??'{}')} /> */}
              {/* <EChartsReact option={item.genChart&&JSON.parse(item.genChart)} /> */}

              <>
                {
                  item.status === 'wait' && <>
                    <Result
                      status="warning"
                      title="待生成"
                      subTitle={item.execMessage ?? '当前图表生成队列繁忙，请耐心等候'}
                    />
                  </>
                }
                {
                  item.status === 'running' && <>
                    <Result
                      status="info"
                      title="图表生成中"
                      subTitle={item.execMessage}
                    />
                  </>
                }
                {
                  item.status === 'succeed' && <>
                    <div style={{ marginBottom: 16 }} />
                    <p>{'分析目标：' + item.goal}</p>
                    <div style={{ marginBottom: 16 }} />
                    <EChartsReact option={item.genChart && JSON.parse(item.genChart)} />
                  </>
                }
                {
                  item.status === 'failed' && <>
                    <Result
                      status="error"
                      title="图表生成失败"
                      subTitle={item.execMessage}
                    />
                  </>
                }
              </>

            </List.Item>
          )}
        />
    </div>
  );
};
export default MyChart;