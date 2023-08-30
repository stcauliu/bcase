package com.stcau.service.modules.example.param;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 *
 * @author stcau
 * @since 2023-06-09 14:22:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "BehaviorGroupParam", description = "实体参数")
public class BehaviorGroupParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "系统编码")
    private String system;

    @ApiModelProperty(value = "模块名称")
    private String module;

    @ApiModelProperty(value = "事件类型")
    private String event;

    @ApiModelProperty(value = "请求开始时间")
    private String start_requestTime;

    @ApiModelProperty(value = "请求结束时间")
    private String end_requestTime;

    @ApiModelProperty(value = "统计个数")
    private String countNum;
    @ApiModelProperty(value = "汇总字段")
    private List<String> groupCodeList;

    @ApiModelProperty(value = "查询类型")
    private String queryType;

    @ApiModelProperty(value = "当前页数")
    private String pageCurrent;

    @ApiModelProperty(value = "每页个数")
    private String pageSize;


}
