package com.noob.springbootinit.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求（删除请求实体定义，一般用作单个根据id删除，统一删除请求变量为id，减少前后端数据交互对接负担）
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}