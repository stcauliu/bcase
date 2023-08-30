package com.stcau.service.modules.example.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author stcau
 * @since 2023-06-09 14:22:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "BehaviorParam对象", description = "实体参数")
public class BehaviorParam {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "用户编码")
    private String id;

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "系统编码")
    private String system;

    @ApiModelProperty(value = "模块名称")
    private String module;

    @ApiModelProperty(value = "事件ID")
    private String eventGroupId;

    @ApiModelProperty(value = "事件类型Id")
    private String eventId;

    @ApiModelProperty(value = "事件类型")
    private String event;

    @ApiModelProperty(value = "事件内容（url、button名称）")
    private String data;

    @ApiModelProperty(value = "浏览器ip")
    private String ip;

    @ApiModelProperty(value = "浏览器user-agent")
    private String userAgent;

    @ApiModelProperty(value = "请求时间")
    private String requestTime;




}
