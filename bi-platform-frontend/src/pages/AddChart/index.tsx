import { Footer } from '@/components';

import { listChartByPageUsingPost } from '@/services/noob-bi/chartController';

import { useModel } from '@umijs/max';
import React, { useEffect,useState } from 'react';


const AddChart: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const { setInitialState } = useModel('@@initialState');

  useEffect(()=>{
    listChartByPageUsingPost({}).then(res=>{
      console.error('res',res)
    })
  });

  
  return (
    
    // 页面信息定义（add-chart）
    <div className = "add-chart">
      hello my chart
    </div>
  
  );
};
export default AddChart;
