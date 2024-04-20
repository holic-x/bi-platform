package com.noob.springbootinit.constant;

/**
 * 文件常量（存放文件常量信息）
 * 常见有文件访问地址等，如果要实现动态化配置可结合application.yml中根据不同环境进行动态配置，需动态初始化加载
 */
public interface FileConstant {

    /**
     * COS 访问地址
     * todo 需替换配置
     */
    String COS_HOST = "https://xxx";
}
