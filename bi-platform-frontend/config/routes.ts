export default [
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' }],
  },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/sub-page' },
      { path: '/admin/sub-page', name: '二级管理页', component: './Admin' },
    ],
  },

  // { path: '/', redirect: '/welcome' },

  { path: '/', redirect: '/add_chart' },
  { name: '智能分析（同步）', icon: 'table', path: '/add_chart', component: './AddChart' },
  { name: '智能分析（异步）', icon: 'table', path: '/add_chart_async', component: './AddChartAsync' },
  { name: '智能分析（MQ）', icon: 'table', path: '/add_chart_async_mq', component: './AddChartAsyncMQ' },
  { name: '我的图表', icon: 'pieChart', path: '/my_chart', component: './MyChart' },


  { path: '*', layout: false, component: './404' },
];
