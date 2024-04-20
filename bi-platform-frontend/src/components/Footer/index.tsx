import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'Noob智能BI平台',
          title: 'Noob智能BI平台',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/holic-x/bi-platform',
          blankTarget: true,
        },
        {
          key: '一人の境',
          title: '一人の境',
          href: 'http://noob.holic-x.com',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
