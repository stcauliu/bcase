package com.stcau.service.config.es;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.stcau.service.config.es.entity.EsQuery;
import com.stcau.service.modules.example.param.BehaviorInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @DES:
 * @auth: 01437949
 * 2020/2/12
 */
@Slf4j
@Service
public class IEsUtilImpl implements IEsUtil {
    @Resource
    RestHighLevelClient restClient;

    /**
     *  验证index 是否存在
     * @param index
     * @return
     * @throws IOException
     */
    public  boolean existsIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        //boolean qqq = restClient.exists(getRequest,RequestOptions.DEFAULT);
        boolean exists = restClient.indices().exists(request, RequestOptions.DEFAULT);
        return exists;
    }

    /**
     *  创建索引
     * @param index
     * @throws IOException
     */
    public void createIndex(String index,String type) throws IOException {

        CreateIndexRequest request = new CreateIndexRequest(index);

        Field[] fields = BehaviorInfo.class.getDeclaredFields();
        XContentBuilder builder1 = getClassMapping(fields);
        //1.7 默认 type
        //request.mapping(type,builder1);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 20)
                .put("index.number_of_replicas", 1)
        );
        CreateIndexResponse createIndexResponse = restClient.indices().create(request,RequestOptions.DEFAULT);
    }


    /**
     * 新增记录
     * @param index
     * @param type
     * @param id
     * @param entity
     * @throws IOException
     */
    public void addRecord(String index, String type,String id, Object entity) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        String a = JSON.toJSONStringWithDateFormat(entity,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //indexRequest.source(JSON.toJSONStringWithDateFormat(entity,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), XContentType.JSON);
        indexRequest.source(JSON.toJSONStringWithDateFormat(entity,"yyyy-MM-dd HH:mm:ss"), XContentType.JSON);

        //indexRequest.source(JSONObject.toJSONString(entity));
        IndexResponse indexResponse = restClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 查询记录
     * @param index
     * @param type
     * @param id
     * @throws IOException
     */
    public GetResponse getRecord(String index, String type, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse getResponse = restClient.get(getRequest, RequestOptions.DEFAULT);
       return getResponse;
    }

    /**
     * 更新记录
     * @param index
     * @param type
     * @param id
     * @param entity
     * @throws IOException
     */
    public void updateRecord(String index, String type, String id,Object entity) throws IOException {
        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(JSON.toJSONString(entity), XContentType.JSON);
        UpdateResponse updateResponse = restClient.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除记录
     * @param index
     * @param type
     * @param id
     * @throws IOException
     */
    public void deleteRecord(String index, String type, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        DeleteResponse response = restClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }


    public void search(String index, String type, String name) throws IOException {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.matchQuery("name", name)); // 这里可以根据字段进行搜索，must表示符合条件的，相反的mustnot表示不符合条件的
        // boolBuilder.must(QueryBuilders.matchQuery("id", tests.getId().toString()));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(100); // 获取记录数，默认10
        sourceBuilder.fetchSource(new String[]{"id", "name"}, new String[]{}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = restClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            log.info("search -> " + hit.getSourceAsString());
        }
    }

    /**
     * 批量操作
     * @param index
     * @param type
     * @param list
     * @throws IOException
     */
    public void bulk(String index,String type,List<?> list) throws IOException {
        // 批量增加
        BulkRequest bulkAddRequest = new BulkRequest();
        Object obj;
        String id ;
        for (int i = 0; i < list.size(); i++) {
            obj = list.get(i);
            id = "";//
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(JSON.toJSONString(index), XContentType.JSON);
            bulkAddRequest.add(indexRequest);
        }
        BulkResponse bulkAddResponse = restClient.bulk(bulkAddRequest, RequestOptions.DEFAULT);
        //search(INDEX_TEST, TYPE_TEST, "this");
        /**
        // 批量更新
        BulkRequest bulkUpdateRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            tests.setName(tests.getName() + " updated");
            UpdateRequest updateRequest = new UpdateRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
            updateRequest.doc(JSON.toJSONString(tests), XContentType.JSON);
            bulkUpdateRequest.add(updateRequest);
        }
        BulkResponse bulkUpdateResponse = client.bulk(bulkUpdateRequest, RequestOptions.DEFAULT);
        System.out.println("bulkUpdate: " + JSON.toJSONString(bulkUpdateResponse));
        search(INDEX_TEST, TYPE_TEST, "updated");

        // 批量删除
        BulkRequest bulkDeleteRequest = new BulkRequest();
        for (int i = 0; i < testsList.size(); i++) {
            tests = testsList.get(i);
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_TEST, TYPE_TEST, tests.getId().toString());
            bulkDeleteRequest.add(deleteRequest);
        }
        BulkResponse bulkDeleteResponse = client.bulk(bulkDeleteRequest, RequestOptions.DEFAULT);
        System.out.println("bulkDelete: " + JSON.toJSONString(bulkDeleteResponse));
        search(INDEX_TEST, TYPE_TEST, "this");
         */
    }





    public static XContentBuilder getClassMapping(Field[] fields) {
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    for (int i = 0; i < fields.length; i++) {
                        String name  = fields[i].getName();
                        String type  = fields[i].getType().getSimpleName();
                        builder.startObject(name);
                        {
                            GetElasticSearchMappingType(type,builder);
                        }
                        builder.endObject();
                    }
                }
                builder.endObject();
            }
            builder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }


    private static String GetElasticSearchMappingType(String varType,XContentBuilder builder) throws IOException {
        switch (varType) {
            case "Date": case "Timestamp":
                builder.field("type","date");
                builder.field("format","yyyy-MM-dd HH:mm:ss");
                //builder.field("format","strict_date_optional_time||epoch_millis");
                break;
            case "Double":
                builder.field("type","double");
                break;
            case "Long": case "long":
                builder.field("type","text");
                break;
            default:
                builder.field("type","text");
                break;
        }
        return null;
    }


    @Override
    public EsQuery getIndexInfo(EsQuery esQuery) throws IOException {
        List<String> returnMsg = new ArrayList<String>();
        BoolQueryBuilder boolBuilder = this.getBoolQueryBuilder(esQuery);
        //展示字段
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder);
        if (null != esQuery.getSelectFields()){
            sourceBuilder.fetchSource(esQuery.getSelectFields(), new String[]{}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部

        }
        //es7 展示所有数据
        sourceBuilder.trackTotalHits(true);
        //条数
        sourceBuilder.from((int)esQuery.getCurrent()*(int)esQuery.getPageSize());
        sourceBuilder.size((int)esQuery.getPageSize()); // 获取记录数，默认10

        //实时查询，无快照
        SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
        searchRequest.source(sourceBuilder);
        Long startTime = System.currentTimeMillis();
        SearchResponse response = restClient.search(searchRequest, RequestOptions.DEFAULT);
        log.error("sourceBuilder:[{}]", sourceBuilder.toString());
        esQuery.setTookInMillis(response.getTook());

        Long endTime = System.currentTimeMillis();
        log.error("restClietn :[{}],took:[{}]",endTime-startTime,response.getTook());
        SearchHits hits = response.getHits();
        esQuery.setTotalHits(hits.getTotalHits().value);
        esQuery.setSearchHits(hits.getHits());
        log.error("getTotalHits :[{}]",hits.getTotalHits());
        return esQuery;
    }


    @Override
    public List<String> getAllIndexInfo(EsQuery esQuery) throws IOException {
        List<String> returnMsg = new ArrayList<String>();
        BoolQueryBuilder boolBuilder = this.getBoolQueryBuilder(esQuery);
        //展示字段
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder);
        if (null != esQuery.getSelectFields()){
            sourceBuilder.fetchSource(esQuery.getSelectFields(), new String[]{}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部

        }
        //es7 展示所有数据
        sourceBuilder.trackTotalHits(true);
        //条数
        sourceBuilder.from((int)esQuery.getCurrent());
        sourceBuilder.size((int)esQuery.getPageSize()); // 获取记录数，默认10

        //
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(5L)); //设定滚动时间间隔
        SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(scroll);
        Long startTime = System.currentTimeMillis();
        SearchResponse response = restClient.search(searchRequest, RequestOptions.DEFAULT);
        log.error("sourceBuilder:[{}]", sourceBuilder.toString());
        //esQuery.setTookInMillis(response.getTook());

        String scrollId = response.getScrollId();
        log.error("scrollId:[{}]",scrollId);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = response.getHits().getHits();

        if (hits.getTotalHits().value>1200000){
            log.error("导出条数:[{}]",hits.getTotalHits());
            throw new RuntimeException("导出条数超限,上限120万条");
        }

        //第一页
        System.out.println("-----第一页-----");
        for (SearchHit searchHit : searchHits) {
            returnMsg.add(searchHit.getSourceAsString());
            //log.error("searchHit:[{}]",searchHit.getSourceAsString());
        }
        //后台scroll 快照查询 todo 导出时使用
        //https://blog.csdn.net/weixin_40341116/article/details/80821655
        //https://www.cnblogs.com/chentop/p/10296517.html


        //遍历搜索命中数据
        while (null != searchHits && searchHits.length> 0){
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            try {
                response = restClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            scrollId = response.getScrollId();
            //log.error("scrollId:[{}]",scrollId);
            searchHits = response.getHits().getHits();
            if (searchHits != null && searchHits.length > 0) {
                //System.out.println("-----下一页-----");
                for (SearchHit searchHit : searchHits) {
                    returnMsg.add(searchHit.getSourceAsString());
                }
            }
        }

        //清除滚屏
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);//也可以选择setScrollIds()将多个scrollId一起使用
        ClearScrollResponse clearScrollResponse = null;
        try {
            clearScrollResponse = restClient.clearScroll(clearScrollRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean succeeded = clearScrollResponse.isSucceeded();


        return returnMsg;
    }

    @Override
    public List<String> getGroupIndex(EsQuery esQuery, List<String> groupCodeList) {
        BoolQueryBuilder boolBuilder = this.getBoolQueryBuilder(esQuery);
        //展示字段
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolBuilder);
        if (null != esQuery.getSelectFields()){
            sourceBuilder.fetchSource(esQuery.getSelectFields(), new String[]{}); // 第一个是获取字段，第二个是过滤的字段，默认获取全部
        }
        //es7 展示所有数据
        sourceBuilder.trackTotalHits(true);
        //条数 只显示统计数量，不显示单条结果集
        sourceBuilder.from(0);
        sourceBuilder.size(0); // 获取记录数，默认10

        //拼接分组数据
        List<TermsAggregationBuilder> aggregationBuilderList  =  new ArrayList<>();
        for (int i=0;i<groupCodeList.size();i++){
            String name = groupCodeList.get(i);
            String key = null;
            if ("income_or_cost".equals(name)){
                key = name;
            }else {
                key = name + ".keyword";
            }
            if (i == 0){
                TermsAggregationBuilder firstField = AggregationBuilders.terms(name).field(key);
                if (groupCodeList.size()==1){
                    firstField.subAggregation(AggregationBuilders.count("countNum").field(groupCodeList.get(0)+".keyword"));
                }
                firstField.size(10000);//设置大点，分组出所有数据
                aggregationBuilderList.add(firstField);
            } else if (i == groupCodeList.size()-1){
                TermsAggregationBuilder lastField = AggregationBuilders.terms(name).field(key);
                lastField.subAggregation(AggregationBuilders.count("countNum").field(groupCodeList.get(0)+".keyword"));
                //lastField.subAggregation(AggregationBuilders.sum("amount_money").field("amount_money"));
                //lastField.subAggregation(AggregationBuilders.sum("net_price").field("net_price"));
                lastField.size(10000);
                aggregationBuilderList.add(lastField);
            }else {
                TermsAggregationBuilder field = AggregationBuilders.terms(name).field(key);
                field.size(10000);
                aggregationBuilderList.add(field);
            }
        }
        for(int i=aggregationBuilderList.size()-1; i > 0;i--){
            aggregationBuilderList.get(i-1).subAggregation(aggregationBuilderList.get(i));
        }

        sourceBuilder.aggregation(aggregationBuilderList.get(0));



        //实时查询，无快照
        SearchRequest searchRequest = new SearchRequest(esQuery.getIndex());
        searchRequest.source(sourceBuilder);

        SearchResponse response = null;
        try {
            response = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Aggregations aggregations = response.getAggregations();
        Map<String,String> map =  new HashMap<>();
        List<String> list  = new ArrayList<>();
        this.parseArregation(aggregations,list,map);
        return list;
    }

    private BoolQueryBuilder getBoolQueryBuilder(EsQuery esQuery) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        //精准查询
        if (null != esQuery.getTermQueryMap()){
            esQuery.getTermQueryMap().entrySet().forEach(
                    entry ->
                            boolBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()))
            );
        }
        //模糊查询
        if (null != esQuery.getWildcardQueryMap()){
            esQuery.getWildcardQueryMap().entrySet().forEach(
                    entry -> boolBuilder.filter(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()))
            );
        }
        //范围查询
        if (null != esQuery.getRangeQueryBuilderList()){
            esQuery.getRangeQueryBuilderList().forEach(
                    list -> boolBuilder.filter(list)
            );
        }
        return boolBuilder;

    }

    private void parseArregation(Aggregations asList,List<String> list,Map<String,String> valueMap) {
        for(Aggregation a:asList){
            Terms terms = (Terms) a;
            for(Terms.Bucket bucket:terms.getBuckets()){
                valueMap.put(a.getName(),bucket.getKeyAsString());
                //log.error("key is [{}],name:[{}]",bucket.getKeyAsString(),a.getName());
                Map<String, Aggregation> map = bucket.getAggregations().asMap();
                if (null != map && !map.containsKey("countNum")){
                    this.parseArregation( bucket.getAggregations(),list,valueMap);
                }else {
                    for(String key : map.keySet()){
                        String value = null;
                        if (map.get(key).getClass() == ParsedValueCount.class){
                            value = ((ParsedValueCount)map.get(key)).getValueAsString();
                        }else if (map.get(key).getClass() == ParsedSum.class){
                            value = ((ParsedSum)map.get(key)).getValueAsString();
                        }
                        valueMap.put(key,value);
                        //log.error("key is [{}],name:[{}],key:[{}]，value:[{}],",bucket.getKeyAsString(),a.getName(), key,value);
                    }
                    list.add(JSONObject.toJSONString(valueMap));
                }

            }
        }

    }



}
