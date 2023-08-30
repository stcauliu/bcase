package com.stcau.service.modules.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stcau.service.config.es.IEsUtil;
import com.stcau.service.config.es.entity.EsQuery;
import com.stcau.service.config.es.entity.EsQueryBuild;
import com.stcau.service.modules.example.param.BehaviorGroupParam;
import com.stcau.service.modules.example.param.BehaviorParam;
import com.stcau.service.modules.example.service.BehaviorService;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
@Log4j2
@Service
public class BehaviorServiceImpl implements BehaviorService {

    @Resource
    IEsUtil iEsUtil;
    @Value("${es.index}")
    private String indexName;
    @Value("${es.type}")
    private String type;

    @Override
    public Page selectGroupInfo(BehaviorGroupParam behaviorGroupParam) {


        Page<BehaviorGroupParam> returnPage = new Page<BehaviorGroupParam>();
        List groupCodeList = behaviorGroupParam.getGroupCodeList();

        behaviorGroupParam.setGroupCodeList(null);
        //拼装查询参数
        EsQuery esQuery = this.getEsQuery(behaviorGroupParam);
        try {

            List<String> list = iEsUtil.getGroupIndex(esQuery,groupCodeList);
            List<BehaviorGroupParam> resultList = new ArrayList<>();
            if (!list.isEmpty()){
                list.forEach(
                        index -> resultList.add(JSONObject.parseObject(index,BehaviorGroupParam.class))
                );
            }
            returnPage.setRecords(resultList);
            returnPage.setTotal(resultList.size());
            log.error(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnPage;
    }

    @Override
    public Page selectInfo(BehaviorGroupParam behaviorGroupParam) {


        int size = Integer.valueOf(behaviorGroupParam.getPageSize());
        int current = Integer.valueOf(behaviorGroupParam.getPageCurrent());

        Page<BehaviorParam> returnPage = new Page<BehaviorParam>();
        //拼装查询参数
        EsQuery esQuery = this.getEsQuery(behaviorGroupParam);
        try {
            iEsUtil.getIndexInfo(esQuery);
            List<BehaviorParam> list = new ArrayList<>();
            if (esQuery.getSearchHits().length > 0){
                for (SearchHit searchHit : esQuery.getSearchHits()){
                    //log.error("打印数据看看文本：【{}】",searchHit.getSourceAsString());
                    list.add(JSONObject. parseObject(searchHit.getSourceAsString(),BehaviorParam.class));
                }
            }

            if (CollUtil.isNotEmpty(list)){
                if(list.size() < 20){
                    log.error("打印数据看看：【{}】",JSONObject.toJSONString(list));
                }
                returnPage.setRecords(list);
            }
            returnPage.setSize(size);
            returnPage.setCurrent(current);
            returnPage.setTotal(esQuery.getTotalHits());
            log.error("页面查询返回总条数；[{}]",returnPage.getTotal());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnPage;
    }

    @Override
    public void save(BehaviorParam bean) {
        try {
            iEsUtil.addRecord(indexName,type,bean.getId(),bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拼装查询参数
     * @param dto
     * @return
     */
    public EsQuery getEsQuery(BehaviorGroupParam dto) {
        int size = Integer.valueOf(dto.getPageSize());
        int current = Integer.valueOf(dto.getPageCurrent());
        dto.setPageCurrent(null);
        dto.setPageSize(null);
        Map<String, Set<String>> termQueryMap = new HashMap<>();// 精装查询
        List<RangeQueryBuilder> rangeList = new ArrayList<>();//范围查询
        //拼装DTO 参数  ES查询
        Arrays.stream(BehaviorGroupParam.class.getDeclaredFields()).filter(
                field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(dto) != null;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
        ).forEach(field -> {
            try {
                String fieldName =  field.getName();
                Object value = field.get(dto);
                //范围查询
                if ("serialVersionUID".equals(fieldName) || "page".equals(fieldName) ) {

                }else if (fieldName.startsWith("start_")){
                    rangeList.add(new RangeQueryBuilder(fieldName.substring(6))
                            .gte(value.toString()));
                }else if (fieldName.startsWith("end_")){
                    rangeList.add(new RangeQueryBuilder(fieldName.substring(4))
                            .lt(value.toString()));
                }else {
                    //其他精准查询
                    termQueryMap.put(("deal_flag".equals(fieldName)||"income_or_cost".equals(fieldName))
                                    ? fieldName:fieldName+".keyword",
                            new HashSet<String>(Arrays.asList(value.toString().split(","))));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        });
        EsQuery esQuery = new EsQueryBuild()
                .setIndex(indexName)
                .setTermQueryMap(termQueryMap)
                .setRangeQueryBuilderList(rangeList)
                .setCurrent(current)
                .setPageSize(size)
                .build();

        return esQuery;
    }

}
