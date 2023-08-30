package com.stcau.service.config.es.entity;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EsQuery {
    /**
     * 精准查询
     */
    private Map<String, Set<String>> termQueryMap;

    /**
     * 范围查询
     */
    private List<RangeQueryBuilder> rangeQueryBuilderList;
    /**
     * 模糊查询
     */
    private Map<String, String> wildcardQueryMap;

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    /**
     * 总条数
     */
    private long totalHits;

    /**
     * 结果集
     */
    private SearchHit[] searchHits;

    /**
     * 查询时间
     */
    private TimeValue tookInMillis;
    /**
     * 开始页
     */
    private long current;

    public long getCurrent() {
        return current;
    }

    public long getPageSize() {
        return pageSize;
    }

    /**
     * 每页条数
     */
    private long pageSize;
    private String index;

    public String[] getSelectFields() {
        return selectFields;
    }

    /**
     * 查询结果集
     */
    private String[] selectFields;

    public Map<String, Set<String>> getTermQueryMap() {
        return termQueryMap;
    }

    public List<RangeQueryBuilder> getRangeQueryBuilderList() {
        return rangeQueryBuilderList;
    }

    public Map<String, String> getWildcardQueryMap() {
        return wildcardQueryMap;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public TimeValue getTookInMillis() {
        return tookInMillis;
    }



    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public void setTookInMillis(TimeValue tookInMillis) {
        this.tookInMillis = tookInMillis;
    }



    public EsQuery(String index,Map<String, Set<String>> termQueryMap, List<RangeQueryBuilder> rangeQueryBuilderList, Map<String, String> wildcardQueryMap, long current, long pageSize,String[] selectFields) {
        this.index = index;
        this.termQueryMap = termQueryMap;
        this.rangeQueryBuilderList = rangeQueryBuilderList;
        this.wildcardQueryMap = wildcardQueryMap;
        this.current = current;
        this.pageSize = pageSize;
        this.selectFields = selectFields;
    }


    public String getIndex() {
        return index;
    }

    public SearchHit[] getSearchHits() {
        return searchHits;
    }

    public void setSearchHits(SearchHit[] searchHits) {
        this.searchHits = searchHits;
    }
}
