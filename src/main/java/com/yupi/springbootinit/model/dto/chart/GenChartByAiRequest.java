package com.yupi.springbootinit.model.dto.chart;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
 *
 *
 *
 */
@Data
public class GenChartByAiRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}