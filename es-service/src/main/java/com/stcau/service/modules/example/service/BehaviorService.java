package com.stcau.service.modules.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stcau.service.modules.example.param.BehaviorGroupParam;
import com.stcau.service.modules.example.param.BehaviorParam;

public interface BehaviorService {
    /**
     * 分组统计
     * @param behaviorGroupParam
     */
    Page selectGroupInfo(BehaviorGroupParam behaviorGroupParam);

    /**
     * 查询明细
     * @param behaviorGroupParam
     */
    Page selectInfo(BehaviorGroupParam behaviorGroupParam);


    /**
     * 新增数据
     * @param bean
     */
    void save(BehaviorParam bean);
}
