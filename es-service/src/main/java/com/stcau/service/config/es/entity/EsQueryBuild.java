package com.stcau.service.config.es.entity;

import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EsQueryBuild {

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




    private String[] selectFields;
    /**
     * 总条数
     */
    private int totalHits;
    /**
     * 查询条数
     */
    private long tookInMillis;

    public EsQueryBuild setCurrent(long current) {
        this.current = current;
        return this;
    }

    public EsQueryBuild setPageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 开始页
     */
    private long current;

    public long getPageSize() {
        return pageSize;
    }

    /**
     * 每页条数
     */
    private long pageSize;

    public String getIndex() {
        return index;
    }

    public EsQueryBuild setIndex(String index) {
        this.index = index;
        return this;
    }

    private String index;



    public Map<String, Set<String>> getTermQueryMap() {
        return termQueryMap;
    }

    public EsQueryBuild setTermQueryMap(Map<String, Set<String>> termQueryMap) {
        this.termQueryMap = termQueryMap;
        return this;
    }

    public List<RangeQueryBuilder> getRangeQueryBuilderList() {
        return rangeQueryBuilderList;
    }

    public EsQueryBuild setRangeQueryBuilderList(List<RangeQueryBuilder> rangeQueryBuilderList) {
        this.rangeQueryBuilderList = rangeQueryBuilderList;
        return this;
    }

    public Map<String, String> getWildcardQueryMap() {
        return wildcardQueryMap;
    }

    public EsQueryBuild setWildcardQueryMap(Map<String, String> wildcardQueryMap) {
        this.wildcardQueryMap = wildcardQueryMap;
        return this;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public EsQueryBuild setTotalHits(int totalHits) {
        totalHits = totalHits;
        return this;
    }

    public String[] getSelectFields() {
        return selectFields;
    }

    public EsQueryBuild setSelectFields(String[] selectFields) {
        this.selectFields = selectFields;
        return this;
    }

    public long getTookInMillis() {
        return tookInMillis;
    }

    public EsQueryBuild setTookInMillis(long tookInMillis) {
        this.tookInMillis = tookInMillis;
        return this;
    }



    public EsQueryBuild setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }


    public EsQuery build(){
        return new EsQuery(index,termQueryMap,rangeQueryBuilderList,wildcardQueryMap,current,pageSize,selectFields);
    }


    public long getCurrent() {
        return current;
    }



}
