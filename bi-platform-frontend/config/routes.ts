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
  { name: '添加图表', icon: 'table', path: '/add_chart', component: './AddChart' },


  { path: '*', layout: false, component: './404' },
];
