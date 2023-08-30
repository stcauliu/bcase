package com.stcau.service.modules.example.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stcau.service.common.core.web.ApiResult;
import com.stcau.service.modules.example.param.BehaviorGroupParam;
import com.stcau.service.modules.example.service.BehaviorService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;



/**
 * feign provider端代码
 * 只是为了展示controller+feign client→feign provider(spring mvc/jersey/restful...)
 */
@Log4j2
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class FeignProviderShowcaseController {

    @Resource
    private BehaviorService behaviorService;

    @RequestMapping(value = "/showcase", method = RequestMethod.GET)
    @ResponseBody
    public String showcase() {
        System.out.println("from provider showcase~~~");
        return "from provider showcase~~~";
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult selectInfo(@RequestBody BehaviorGroupParam param) {
        ApiResult apiResult = new ApiResult();

        if(StringUtils.isEmpty(param.getQueryType())){
            apiResult.setCode(500);
            apiResult.setError("缺少查询类型");
            return apiResult;
        }

        if (StringUtils.isEmpty(param.getPageSize())){
            param.setPageSize("50");
        }
        if (StringUtils.isEmpty(param.getPageCurrent())){
            param.setPageCurrent("0");
        }

        //将查询条件存入redis中
        int code = JSONObject.toJSONString(param).hashCode();
        Page page = null;
        switch (param.getQueryType()){
            case "info":
                param.setQueryType(null);
                page  = behaviorService.selectInfo(param);
                break;
            case "group":
                if (CollUtil.isEmpty(param.getGroupCodeList())){
                    apiResult.setCode(500);
                    apiResult.setError("汇总字段不存在");
                    return apiResult;
                }
                param.setQueryType(null);
                page  =  behaviorService.selectGroupInfo(param);
                break;
            default:
                apiResult.setCode(500);
                apiResult.setError("查询类型不存在");
                return apiResult;

        }
        apiResult.setCode(200);
        apiResult.setData(page);
        apiResult.setMessage("成功");
        return apiResult;
    }

}
